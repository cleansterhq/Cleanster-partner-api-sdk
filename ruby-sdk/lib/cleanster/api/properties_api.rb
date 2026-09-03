module Cleanster
  module Api
    # PropertiesApi - CRUD for cleaning locations, plus cleaners, iCal, and checklists.
    class PropertiesApi
      def initialize(http)
        @http = http
      end

      # List all properties, optionally filtered by service type.
      #
      # @param service_id [Integer, nil]
      # @return [Models::ApiResponse]
      def list_properties(service_id: nil)
        params = service_id ? { serviceId: service_id } : nil
        raw    = @http.get("/v1/properties", params: params)
        Models::ApiResponse.from_hash(raw)
      end

      # Add a new property.
      #
      # @param request [Hash] name, optional nick_name, address, city, country,
      #   room_count, bathroom_count, service_id.
      # @return [Models::ApiResponse<Models::Property>]
      def add_property(request)
        raw = @http.post("/v1/properties", body: camel_keys(request))
        Models::ApiResponse.from_hash(raw, model_class: Models::Property)
      end

      # Get details of a specific property.
      #
      # @param property_id [Integer]
      # @return [Models::ApiResponse<Models::Property>]
      def get_property(property_id)
        raw = @http.get("/v1/properties/#{property_id}")
        Models::ApiResponse.from_hash(raw, model_class: Models::Property)
      end

      # Update an existing property.
      #
      # @param property_id [Integer]
      # @param request     [Hash]
      # @return [Models::ApiResponse<Models::Property>]
      def update_property(property_id, request)
        raw = @http.put("/v1/properties/#{property_id}", body: camel_keys(request))
        Models::ApiResponse.from_hash(raw, model_class: Models::Property)
      end

      # Update additional information fields for a property.
      def update_additional_information(property_id, data)
        raw = @http.put("/v1/properties/#{property_id}/additional-information", body: data)
        Models::ApiResponse.from_hash(raw)
      end

      # Enable or disable a property.
      #
      # @param property_id [Integer]
      # @param enabled     [Boolean]
      # @return [Models::ApiResponse]
      def enable_or_disable_property(property_id, enabled:)
        raw = @http.post("/v1/properties/#{property_id}/enable-disable", body: { enabled: enabled })
        Models::ApiResponse.from_hash(raw)
      end

      # Permanently delete a property.
      def delete_property(property_id)
        raw = @http.delete("/v1/properties/#{property_id}")
        Models::ApiResponse.from_hash(raw)
      end

      # Get the list of cleaners assigned to a property.
      def get_property_cleaners(property_id)
        raw = @http.get("/v1/properties/#{property_id}/cleaners")
        Models::ApiResponse.from_hash(raw)
      end

      # Assign a cleaner to a property's default cleaner pool.
      #
      # @param property_id [Integer]
      # @param cleaner_id  [Integer]
      # @return [Models::ApiResponse]
      def assign_cleaner_to_property(property_id, cleaner_id:)
        raw = @http.post("/v1/properties/#{property_id}/cleaners", body: { cleanerId: cleaner_id })
        Models::ApiResponse.from_hash(raw)
      end

      # Remove a cleaner from a property's default cleaner pool.
      def unassign_cleaner_from_property(property_id, cleaner_id)
        raw = @http.delete("/v1/properties/#{property_id}/cleaners/#{cleaner_id}")
        Models::ApiResponse.from_hash(raw)
      end

      # Add one or more iCal calendar links to a property for availability syncing.
      # Each URL must be a live, publicly fetchable .ics feed - the API validates
      # the feed content, not just the URL shape.
      #
      # @param property_id    [Integer]
      # @param calendar_links [Array<String>, String] One or more iCal feed URLs.
      # @return [Models::ApiResponse]
      def add_ical_link(property_id, calendar_links:)
        links = Array(calendar_links)
        raw = @http.put("/v1/properties/#{property_id}/ical", body: { calendarLinks: links })
        Models::ApiResponse.from_hash(raw)
      end

      # Get the calendar links currently attached to a property.
      # Returns data as an array of { "id" => Integer, "calendarLink" => String }.
      def get_ical_link(property_id)
        raw = @http.get("/v1/properties/#{property_id}/ical")
        Models::ApiResponse.from_hash(raw)
      end

      # Remove one or more calendar links from a property, by numeric link ID
      # (from get_ical_link) - not by URL.
      #
      # @param property_id [Integer]
      # @param ids         [Array<Integer>, Integer] The link ID(s) to remove.
      def remove_ical_link(property_id, ids:)
        link_ids = Array(ids)
        raw = @http.delete("/v1/properties/#{property_id}/ical", body: { ids: link_ids })
        Models::ApiResponse.from_hash(raw)
      end

      # Set a default checklist for a property.
      #
      # @param property_id               [Integer]
      # @param checklist_id              [Integer]
      # @param update_upcoming_bookings  [Boolean]
      # @return [Models::ApiResponse]
      def set_default_checklist(property_id, checklist_id, update_upcoming_bookings: false)
        path = "/v1/properties/#{property_id}/checklist/#{checklist_id}" \
               "?updateUpcomingBookings=#{update_upcoming_bookings}"
        raw = @http.put(path)
        Models::ApiResponse.from_hash(raw)
      end

      private

      def camel_keys(hash)
        map = {
          room_count:     "roomCount",
          bathroom_count: "bathroomCount",
          service_id:     "serviceId"
        }
        hash.transform_keys { |k| map[k] || k }
      end
    end
  end
end
