module Cleanster
  module Models
    # Represents a saved payment method (card or PayPal).
    class PaymentMethod
      attr_reader :id, :type, :last_four, :brand, :is_default, :raw

      def initialize(data)
        @raw        = data
        @id         = data["id"]
        @type       = data["type"]
        @last_four  = data["lastFour"]
        @brand      = data["brand"]
        @is_default = data["isDefault"]
      end

      def to_s
        "PaymentMethod(id=#{id}, type=#{type.inspect}, last_four=#{last_four.inspect})"
      end
    end
  end
end
