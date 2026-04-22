module Cleanster
  module Models
    # Represents a single cleaning appointment.
    class Booking
      attr_reader :id, :status, :date, :time, :hours, :cost,
                  :property_id, :cleaner_id, :plan_id,
                  :room_count, :bathroom_count, :extra_supplies,
                  :payment_method_id, :posted_by, :raw

      def initialize(data)
        @raw               = data
        @id                = data["id"]
        @status            = data["status"]
        @date              = data["date"]
        @time              = data["time"]
        @hours             = data["hours"]
        @cost              = data["cost"]
        @property_id       = data["propertyId"]
        @cleaner_id        = data["cleanerId"]
        @plan_id           = data["planId"]
        @room_count        = data["roomCount"]
        @bathroom_count    = data["bathroomCount"]
        @extra_supplies    = data["extraSupplies"]
        @payment_method_id = data["paymentMethodId"]
        @posted_by         = data["postedBy"]
      end

      def to_s
        "Booking(id=#{id}, status=#{status.inspect}, date=#{date.inspect})"
      end
    end
  end
end
