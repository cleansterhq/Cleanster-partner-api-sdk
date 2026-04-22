module Cleanster
  module Models
    # Represents a Cleanster end-user account.
    class User
      attr_reader :id, :email, :first_name, :last_name, :phone, :token, :raw

      def initialize(data)
        @raw        = data
        @id         = data["id"]
        @email      = data["email"]
        @first_name = data["firstName"]
        @last_name  = data["lastName"]
        @phone      = data["phone"]
        @token      = data["token"]
      end

      def to_s
        "User(id=#{id}, email=#{email.inspect})"
      end
    end
  end
end
