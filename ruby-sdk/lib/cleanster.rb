require_relative "cleanster/version"
require_relative "cleanster/exceptions"
require_relative "cleanster/config"
require_relative "cleanster/models/api_response"
require_relative "cleanster/models/booking"
require_relative "cleanster/models/user"
require_relative "cleanster/models/property"
require_relative "cleanster/models/checklist"
require_relative "cleanster/models/payment_method"
require_relative "cleanster/client"

# Cleanster Partner API SDK for Ruby.
#
# @example Quick start
#   require "cleanster"
#
#   client = Cleanster::Client.sandbox("your-access-key")
#
#   response = client.users.create_user(
#     email:      "jane@example.com",
#     first_name: "Jane",
#     last_name:  "Smith"
#   )
#   user = response.data
#
#   token_response = client.users.fetch_access_token(user.id)
#   client.access_token = token_response.data.token
#
#   bookings = client.bookings.get_bookings.data
module Cleanster
end
