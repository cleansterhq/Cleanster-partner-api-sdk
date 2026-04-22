module Cleanster
  module Models
    # Represents a physical property where cleanings take place.
    class Property
      attr_reader :id, :name, :address, :city, :country,
                  :room_count, :bathroom_count, :service_id, :is_enabled, :raw

      def initialize(data)
        @raw            = data
        @id             = data["id"]
        @name           = data["name"]
        @address        = data["address"]
        @city           = data["city"]
        @country        = data["country"]
        @room_count     = data["roomCount"]
        @bathroom_count = data["bathroomCount"]
        @service_id     = data["serviceId"]
        @is_enabled     = data["isEnabled"]
      end

      def to_s
        "Property(id=#{id}, name=#{name.inspect}, city=#{city.inspect})"
      end
    end
  end
end
