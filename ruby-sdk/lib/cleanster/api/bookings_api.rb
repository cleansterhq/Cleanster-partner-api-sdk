module Cleanster
  module Api
    # BookingsApi — manage the full lifecycle of cleaning appointments.
    class BookingsApi
      def initialize(http)
        @http = http
      end

      # List upcoming and past bookings.
      #
      # @param page_no [Integer, nil] Page number (1-based). Pass nil for page 1.
      # @param status  [String, nil]  Filter: OPEN | CLEANER_ASSIGNED | COMPLETED | CANCELLED | REMOVED
      # @return [Models::ApiResponse]
      def get_bookings(page_no: nil, status: nil)
        raw = @http.get("/v1/bookings", params: { pageNo: page_no, status: status })
        Models::ApiResponse.from_hash(raw)
      end

      # Schedule a new cleaning appointment.
      #
      # @param request [Hash] date, time, property_id, room_count, bathroom_count,
      #                       plan_id, hours, extra_supplies, payment_method_id.
      # @return [Models::ApiResponse<Models::Booking>]
      def create_booking(request)
        raw = @http.post("/v1/bookings/create", body: camel_keys(request))
        Models::ApiResponse.from_hash(raw, model_class: Models::Booking)
      end

      # Get full details of a specific booking.
      #
      # @param booking_id [Integer]
      # @return [Models::ApiResponse<Models::Booking>]
      def get_booking_details(booking_id)
        raw = @http.get("/v1/bookings/#{booking_id}")
        Models::ApiResponse.from_hash(raw, model_class: Models::Booking)
      end

      # Cancel a booking.
      #
      # @param booking_id [Integer]
      # @param reason     [String, nil] Optional cancellation reason.
      # @return [Models::ApiResponse]
      def cancel_booking(booking_id, reason: nil)
        body = reason ? { reason: reason } : {}
        raw  = @http.post("/v1/bookings/#{booking_id}/cancel", body: body)
        Models::ApiResponse.from_hash(raw)
      end

      # Reschedule a booking to a new date and time.
      #
      # @param booking_id [Integer]
      # @param date [String] New date in YYYY-MM-DD format.
      # @param time [String] New time in HH:mm format (24-hour).
      # @return [Models::ApiResponse]
      def reschedule_booking(booking_id, date:, time:)
        raw = @http.post("/v1/bookings/#{booking_id}/reschedule", body: { date: date, time: time })
        Models::ApiResponse.from_hash(raw)
      end

      # Manually assign a specific cleaner to a booking.
      #
      # @param booking_id [Integer]
      # @param cleaner_id [Integer]
      # @return [Models::ApiResponse]
      def assign_cleaner(booking_id, cleaner_id:)
        raw = @http.post("/v1/bookings/#{booking_id}/cleaner", body: { cleanerId: cleaner_id })
        Models::ApiResponse.from_hash(raw)
      end

      # Remove the currently assigned cleaner from a booking.
      #
      # @param booking_id [Integer]
      # @return [Models::ApiResponse]
      def remove_assigned_cleaner(booking_id)
        raw = @http.delete("/v1/bookings/#{booking_id}/cleaner")
        Models::ApiResponse.from_hash(raw)
      end

      # Adjust the number of hours for a booking.
      #
      # @param booking_id [Integer]
      # @param hours [Numeric]
      # @return [Models::ApiResponse]
      def adjust_hours(booking_id, hours:)
        raw = @http.post("/v1/bookings/#{booking_id}/hours", body: { hours: hours })
        Models::ApiResponse.from_hash(raw)
      end

      # Pay outstanding expenses for a completed booking (within 72h of completion).
      #
      # @param booking_id        [Integer]
      # @param payment_method_id [Integer]
      # @return [Models::ApiResponse]
      def pay_expenses(booking_id, payment_method_id:)
        raw = @http.post("/v1/bookings/#{booking_id}/expenses", body: { paymentMethodId: payment_method_id })
        Models::ApiResponse.from_hash(raw)
      end

      # Get the inspection report for a completed booking.
      def get_booking_inspection(booking_id)
        raw = @http.get("/v1/bookings/#{booking_id}/inspection")
        Models::ApiResponse.from_hash(raw)
      end

      # Get detailed inspection information for a completed booking.
      def get_booking_inspection_details(booking_id)
        raw = @http.get("/v1/bookings/#{booking_id}/inspection/details")
        Models::ApiResponse.from_hash(raw)
      end

      # Attach a cleaning checklist to a specific booking.
      #
      # @param booking_id   [Integer]
      # @param checklist_id [Integer]
      # @return [Models::ApiResponse]
      def assign_checklist_to_booking(booking_id, checklist_id)
        raw = @http.put("/v1/bookings/#{booking_id}/checklist/#{checklist_id}")
        Models::ApiResponse.from_hash(raw)
      end

      # Submit a star rating and optional comment after a booking completes.
      #
      # @param booking_id [Integer]
      # @param rating     [Integer] 1–5
      # @param comment    [String, nil]
      # @return [Models::ApiResponse]
      def submit_feedback(booking_id, rating:, comment: nil)
        body = { rating: rating }
        body[:comment] = comment if comment
        raw = @http.post("/v1/bookings/#{booking_id}/feedback", body: body)
        Models::ApiResponse.from_hash(raw)
      end

      # Add a tip for the cleaner (within 72h of booking completion).
      #
      # @param booking_id        [Integer]
      # @param amount            [Numeric]
      # @param payment_method_id [Integer]
      # @return [Models::ApiResponse]
      def add_tip(booking_id, amount:, payment_method_id:)
        raw = @http.post("/v1/bookings/#{booking_id}/tip",
                         body: { amount: amount, paymentMethodId: payment_method_id })
        Models::ApiResponse.from_hash(raw)
      end

      # Retrieve all chat messages for a booking thread.
      def get_chat(booking_id)
        raw = @http.get("/v1/bookings/#{booking_id}/chat")
        Models::ApiResponse.from_hash(raw)
      end

      # Post a chat message in a booking thread.
      #
      # @param booking_id [Integer]
      # @param message    [String]
      # @return [Models::ApiResponse]
      def send_message(booking_id, message:)
        raw = @http.post("/v1/bookings/#{booking_id}/chat", body: { message: message })
        Models::ApiResponse.from_hash(raw)
      end

      # Delete a specific chat message.
      #
      # @param booking_id [Integer]
      # @param message_id [String]
      # @return [Models::ApiResponse]
      def delete_message(booking_id, message_id)
        raw = @http.delete("/v1/bookings/#{booking_id}/chat/#{message_id}")
        Models::ApiResponse.from_hash(raw)
      end

      private

      def camel_keys(hash)
        map = {
          property_id:        "propertyId",
          room_count:         "roomCount",
          bathroom_count:     "bathroomCount",
          plan_id:            "planId",
          extra_supplies:     "extraSupplies",
          payment_method_id:  "paymentMethodId",
          coupon_code:        "couponCode"
        }
        hash.transform_keys { |k| map[k] || k }
      end
    end
  end
end
