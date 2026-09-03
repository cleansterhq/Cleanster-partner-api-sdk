module Cleanster
  module Models
    # Represents a physical property where cleanings take place.
    class Property
      attr_reader :id, :user_id, :name, :nickname, :apt, :street, :address,
                  :city, :state, :country, :zip_code, :room_count,
                  :bathroom_count, :service_id, :is_enabled, :is_active,
                  :is_enable, :pets, :public_name, :wifi_name, :wifi_password,
                  :laundry, :garbage, :extra_supplies, :created_date, :access,
                  :supplies_location, :parking, :other_note, :latitude,
                  :longitude, :raw

      def initialize(data)
        @raw            = data
        @id             = data["id"]
        @user_id        = data["userId"]
        @name           = data["name"]
        @nickname       = data["nickName"]
        @apt            = data["apt"]
        @street         = data["street"]
        @address        = data["address"]
        @city           = data["city"]
        @state          = data["state"]
        @country        = data["country"]
        @zip_code       = data["zipCode"]
        @room_count     = data["roomCount"]
        @bathroom_count = data["bathroomCount"]
        @service_id     = data["serviceId"]
        @is_enabled     = data["isEnabled"]
        @is_active      = data["isActive"]
        @is_enable      = data["isEnable"]
        @pets           = data["pets"]
        @public_name    = data["publicName"]
        @wifi_name      = data["wifiName"]
        @wifi_password  = data["wifiPassword"]
        @laundry        = data["laundry"]
        @garbage        = data["garbage"]
        @extra_supplies = data["extraSupplies"]
        @created_date   = data["createdDate"]
        @access         = data["access"]
        @supplies_location = data["suppliesLocation"]
        @parking        = data["parking"]
        @other_note     = data["otherNote"]
        @latitude       = data["latitude"]
        @longitude      = data["longitude"]
      end

      def to_s
        "Property(id=#{id}, name=#{name.inspect}, city=#{city.inspect})"
      end
    end
  end
end
