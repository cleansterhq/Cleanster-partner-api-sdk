require "spec_helper"

RSpec.describe Cleanster do
  # ---------------------------------------------------------------------------
  # Helpers
  # ---------------------------------------------------------------------------
  def ok_response(data = {})
    { "status" => 200, "message" => "OK", "data" => data }
  end

  # ---------------------------------------------------------------------------
  # Cleanster::Config
  # ---------------------------------------------------------------------------
  describe Cleanster::Config do
    describe ".sandbox" do
      it "sets the sandbox base URL" do
        config = described_class.sandbox("key")
        expect(config.base_url).to eq(Cleanster::SANDBOX_BASE_URL)
      end
    end

    describe ".production" do
      it "sets the production base URL" do
        config = described_class.production("key")
        expect(config.base_url).to eq(Cleanster::PRODUCTION_BASE_URL)
      end
    end

    describe "validation" do
      it "raises ArgumentError on blank access_key" do
        expect { described_class.sandbox("") }.to raise_error(ArgumentError, /access_key/)
      end

      it "raises ArgumentError on whitespace-only access_key" do
        expect { described_class.sandbox("   ") }.to raise_error(ArgumentError, /access_key/)
      end

      it "raises ArgumentError on nil access_key" do
        expect { described_class.new(access_key: nil) }.to raise_error(ArgumentError, /access_key/)
      end
    end

    describe "trailing slash stripping" do
      it "strips trailing slash from base_url" do
        config = described_class.new(access_key: "k", base_url: "https://example.com/api/")
        expect(config.base_url).to eq("https://example.com/api")
      end
    end

    describe "defaults" do
      let(:config) { described_class.sandbox("key") }

      it "has a positive open_timeout" do
        expect(config.open_timeout).to be > 0
      end

      it "has a positive read_timeout" do
        expect(config.read_timeout).to be > 0
      end

      it "stores the access_key" do
        expect(config.access_key).to eq("key")
      end
    end

    describe Cleanster::Config::Builder do
      it "builds a sandbox config" do
        config = described_class.new("k").sandbox.build
        expect(config.base_url).to eq(Cleanster::SANDBOX_BASE_URL)
      end

      it "builds a production config" do
        config = described_class.new("k").production.build
        expect(config.base_url).to eq(Cleanster::PRODUCTION_BASE_URL)
      end

      it "sets custom read_timeout" do
        config = described_class.new("k").read_timeout(90).build
        expect(config.read_timeout).to eq(90)
      end

      it "sets custom open_timeout" do
        config = described_class.new("k").open_timeout(15).build
        expect(config.open_timeout).to eq(15)
      end

      it "sets custom base_url" do
        config = described_class.new("k").base_url("https://custom.example.com").build
        expect(config.base_url).to eq("https://custom.example.com")
      end
    end
  end

  # ---------------------------------------------------------------------------
  # Cleanster::Client
  # ---------------------------------------------------------------------------
  describe Cleanster::Client do
    let(:client) { described_class.sandbox("access-key") }

    it "exposes a bookings namespace" do
      expect(client.bookings).to be_a(Cleanster::Api::BookingsApi)
    end

    it "exposes a users namespace" do
      expect(client.users).to be_a(Cleanster::Api::UsersApi)
    end

    it "exposes a properties namespace" do
      expect(client.properties).to be_a(Cleanster::Api::PropertiesApi)
    end

    it "exposes a checklists namespace" do
      expect(client.checklists).to be_a(Cleanster::Api::ChecklistsApi)
    end

    it "exposes an other namespace" do
      expect(client.other).to be_a(Cleanster::Api::OtherApi)
    end

    it "exposes a blacklist namespace" do
      expect(client.blacklist).to be_a(Cleanster::Api::BlacklistApi)
    end

    it "exposes a payment_methods namespace" do
      expect(client.payment_methods).to be_a(Cleanster::Api::PaymentMethodsApi)
    end

    it "exposes a webhooks namespace" do
      expect(client.webhooks).to be_a(Cleanster::Api::WebhooksApi)
    end

    it ".sandbox creates a Client without raising" do
      expect { described_class.sandbox("key") }.not_to raise_error
    end

    it ".production creates a Client without raising" do
      expect { described_class.production("key") }.not_to raise_error
    end

    describe "access_token" do
      it "returns nil by default" do
        expect(client.access_token).to be_nil
      end

      it "round-trips access_token= / access_token" do
        client.access_token = "bearer-xyz"
        expect(client.access_token).to eq("bearer-xyz")
      end

      it "clears access_token when set to nil" do
        client.access_token = "tok"
        client.access_token = nil
        expect(client.access_token).to be_nil
      end
    end
  end

  # ---------------------------------------------------------------------------
  # Shared test setup — stub the HTTP client
  # ---------------------------------------------------------------------------
  def build_api(api_class)
    http = instance_double(Cleanster::HttpClient)
    [api_class.new(http), http]
  end

  # ---------------------------------------------------------------------------
  # Cleanster::Api::BookingsApi
  # ---------------------------------------------------------------------------
  describe Cleanster::Api::BookingsApi do
    let(:api) { described_class.new(http) }
    let(:http) { instance_double(Cleanster::HttpClient) }

    describe "#get_bookings" do
      it "calls GET /v1/bookings with no params" do
        allow(http).to receive(:get).with("/v1/bookings", params: { pageNo: nil, status: nil })
                                    .and_return(ok_response([]))
        result = api.get_bookings
        expect(result).to be_a(Cleanster::Models::ApiResponse)
      end

      it "passes status filter" do
        allow(http).to receive(:get).with("/v1/bookings", params: { pageNo: nil, status: "OPEN" })
                                    .and_return(ok_response([]))
        api.get_bookings(status: "OPEN")
      end

      it "passes page_no" do
        allow(http).to receive(:get).with("/v1/bookings", params: { pageNo: 2, status: nil })
                                    .and_return(ok_response([]))
        api.get_bookings(page_no: 2)
      end
    end

    describe "#create_booking" do
      it "POSTs to /v1/bookings/create and returns a Booking" do
        booking_data = { "id" => 1, "status" => "OPEN" }
        allow(http).to receive(:post).with("/v1/bookings/create", body: anything)
                                     .and_return(ok_response(booking_data))
        result = api.create_booking(date: "2025-06-15", time: "10:00",
                                    property_id: 1, room_count: 2, bathroom_count: 1,
                                    plan_id: 2, hours: 3, extra_supplies: false,
                                    payment_method_id: 10)
        expect(result.data).to be_a(Cleanster::Models::Booking)
        expect(result.data.status).to eq("OPEN")
      end
    end

    describe "#get_booking_details" do
      it "calls GET /v1/bookings/16926 and returns a Booking" do
        allow(http).to receive(:get).with("/v1/bookings/16926")
                                    .and_return(ok_response("id" => 16926, "status" => "COMPLETED"))
        result = api.get_booking_details(16926)
        expect(result.data).to be_a(Cleanster::Models::Booking)
        expect(result.data.id).to eq(16926)
      end
    end

    describe "#cancel_booking" do
      it "POSTs to /v1/bookings/16459/cancel with reason" do
        allow(http).to receive(:post).with("/v1/bookings/16459/cancel",
                                           body: { reason: "Changed plans" })
                                     .and_return(ok_response)
        api.cancel_booking(16459, reason: "Changed plans")
      end

      it "sends empty body when no reason given" do
        allow(http).to receive(:post).with("/v1/bookings/16459/cancel", body: {})
                                     .and_return(ok_response)
        api.cancel_booking(16459)
      end
    end

    describe "#reschedule_booking" do
      it "POSTs to /v1/bookings/16459/reschedule with date and time" do
        allow(http).to receive(:post).with("/v1/bookings/16459/reschedule",
                                           body: { date: "2025-07-01", time: "14:00" })
                                     .and_return(ok_response)
        api.reschedule_booking(16459, date: "2025-07-01", time: "14:00")
      end
    end

    describe "#assign_cleaner" do
      it "POSTs cleanerId" do
        allow(http).to receive(:post).with("/v1/bookings/16459/cleaner",
                                           body: { cleanerId: 5 })
                                     .and_return(ok_response)
        api.assign_cleaner(16459, cleaner_id: 5)
      end
    end

    describe "#remove_assigned_cleaner" do
      it "sends DELETE to /v1/bookings/16459/cleaner" do
        allow(http).to receive(:delete).with("/v1/bookings/16459/cleaner")
                                       .and_return(ok_response)
        api.remove_assigned_cleaner(16459)
      end
    end

    describe "#adjust_hours" do
      it "POSTs hours" do
        allow(http).to receive(:post).with("/v1/bookings/16459/hours",
                                           body: { hours: 4.0 })
                                     .and_return(ok_response)
        api.adjust_hours(16459, hours: 4.0)
      end
    end

    describe "#pay_expenses" do
      it "POSTs paymentMethodId" do
        allow(http).to receive(:post).with("/v1/bookings/16926/expenses",
                                           body: { paymentMethodId: 10 })
                                     .and_return(ok_response)
        api.pay_expenses(16926, payment_method_id: 10)
      end
    end

    describe "#get_booking_inspection" do
      it "GETs /v1/bookings/16926/inspection" do
        allow(http).to receive(:get).with("/v1/bookings/16926/inspection")
                                    .and_return(ok_response)
        api.get_booking_inspection(16926)
      end
    end

    describe "#get_booking_inspection_details" do
      it "GETs /v1/bookings/16926/inspection/details" do
        allow(http).to receive(:get).with("/v1/bookings/16926/inspection/details")
                                    .and_return(ok_response)
        api.get_booking_inspection_details(16926)
      end
    end

    describe "#assign_checklist_to_booking" do
      it "POSTs to correct URL" do
        allow(http).to receive(:put).with("/v1/bookings/16926/checklist/105")
                                     .and_return(ok_response)
        api.assign_checklist_to_booking(16926, 105)
      end
    end

    describe "#submit_feedback" do
      it "POSTs rating and comment" do
        allow(http).to receive(:post).with("/v1/bookings/16926/feedback",
                                           body: { rating: 5, comment: "Great!" })
                                     .and_return(ok_response)
        api.submit_feedback(16926, rating: 5, comment: "Great!")
      end

      it "omits comment when not given" do
        allow(http).to receive(:post).with("/v1/bookings/16926/feedback",
                                           body: { rating: 4 })
                                     .and_return(ok_response)
        api.submit_feedback(16926, rating: 4)
      end
    end

    describe "#add_tip" do
      it "POSTs amount and paymentMethodId" do
        allow(http).to receive(:post).with("/v1/bookings/16926/tip",
                                           body: { amount: 20.0, paymentMethodId: 10 })
                                     .and_return(ok_response)
        api.add_tip(16926, amount: 20.0, payment_method_id: 10)
      end
    end

    describe "#get_chat" do
      it "GETs /v1/bookings/17142/chat" do
        allow(http).to receive(:get).with("/v1/bookings/17142/chat")
                                    .and_return(ok_response([]))
        api.get_chat(17142)
      end
    end

    describe "#send_message" do
      it "POSTs message to chat endpoint" do
        allow(http).to receive(:post).with("/v1/bookings/17142/chat",
                                           body: { message: "Please bring towels." })
                                     .and_return(ok_response)
        api.send_message(17142, message: "Please bring towels.")
      end
    end

    describe "#delete_message" do
      it "DELETEs the specific message" do
        allow(http).to receive(:delete).with("/v1/bookings/17142/chat/msg-abc-123")
                                       .and_return(ok_response)
        api.delete_message(17142, "msg-abc-123")
      end
    end
  end

  # ---------------------------------------------------------------------------
  # Cleanster::Api::UsersApi
  # ---------------------------------------------------------------------------
  describe Cleanster::Api::UsersApi do
    let(:api) { described_class.new(http) }
    let(:http) { instance_double(Cleanster::HttpClient) }

    describe "#create_user" do
      it "POSTs to /v1/user/account and returns a User" do
        data = { "id" => 42, "email" => "jane@example.com" }
        allow(http).to receive(:post).with("/v1/user/account", body: anything)
                                     .and_return(ok_response(data))
        result = api.create_user(email: "jane@example.com", first_name: "Jane", last_name: "Smith")
        expect(result.data).to be_a(Cleanster::Models::User)
        expect(result.data.email).to eq("jane@example.com")
      end

      it "includes phone when provided" do
        allow(http).to receive(:post) do |_path, body:|
          expect(body[:phone]).to eq("+15551234567")
          ok_response("id" => 1, "email" => "j@x.com")
        end
        api.create_user(email: "j@x.com", first_name: "J", last_name: "X", phone: "+15551234567")
      end

      it "omits phone when not provided" do
        allow(http).to receive(:post) do |_path, body:|
          expect(body).not_to have_key(:phone)
          ok_response("id" => 1, "email" => "j@x.com")
        end
        api.create_user(email: "j@x.com", first_name: "J", last_name: "X")
      end
    end

    describe "#fetch_access_token" do
      it "GETs /v1/user/access-token/42 and returns User with token" do
        data = { "id" => 42, "token" => "bearer-jwt-abc" }
        allow(http).to receive(:get).with("/v1/user/access-token/42")
                                    .and_return(ok_response(data))
        result = api.fetch_access_token(42)
        expect(result.data).to be_a(Cleanster::Models::User)
        expect(result.data.token).to eq("bearer-jwt-abc")
      end
    end

    describe "#verify_jwt" do
      it "POSTs to /v1/user/verify-jwt" do
        allow(http).to receive(:post).with("/v1/user/verify-jwt",
                                           body: { token: "eyJhbGci..." })
                                     .and_return(ok_response)
        api.verify_jwt(token: "eyJhbGci...")
      end
    end
  end

  # ---------------------------------------------------------------------------
  # Cleanster::Api::PropertiesApi
  # ---------------------------------------------------------------------------
  describe Cleanster::Api::PropertiesApi do
    let(:api) { described_class.new(http) }
    let(:http) { instance_double(Cleanster::HttpClient) }

    describe "#list_properties" do
      it "GETs /v1/properties with no filter" do
        allow(http).to receive(:get).with("/v1/properties", params: nil)
                                    .and_return(ok_response([]))
        api.list_properties
      end

      it "passes serviceId filter" do
        allow(http).to receive(:get).with("/v1/properties", params: { serviceId: 1 })
                                    .and_return(ok_response([]))
        api.list_properties(service_id: 1)
      end
    end

    describe "#add_property" do
      it "POSTs and returns a Property" do
        data = { "id" => 1040, "name" => "Beach House" }
        allow(http).to receive(:post).with("/v1/properties", body: anything)
                                     .and_return(ok_response(data))
        result = api.add_property(name: "Beach House", address: "123 St",
                                   city: "Miami", country: "USA",
                                   room_count: 3, bathroom_count: 2, service_id: 1)
        expect(result.data).to be_a(Cleanster::Models::Property)
        expect(result.data.id).to eq(1040)
      end
    end

    describe "#get_property" do
      it "GETs /v1/properties/1040 and returns a Property" do
        allow(http).to receive(:get).with("/v1/properties/1040")
                                    .and_return(ok_response("id" => 1040, "city" => "Miami"))
        result = api.get_property(1040)
        expect(result.data).to be_a(Cleanster::Models::Property)
        expect(result.data.city).to eq("Miami")
      end
    end

    describe "#update_property" do
      it "PUTs to /v1/properties/1040" do
        allow(http).to receive(:put).with("/v1/properties/1040", body: anything)
                                    .and_return(ok_response("id" => 1040))
        result = api.update_property(1040, name: "Updated", address: "456 Ave",
                                           city: "NYC", country: "USA",
                                           room_count: 2, bathroom_count: 1, service_id: 1)
        expect(result.data).to be_a(Cleanster::Models::Property)
      end
    end

    describe "#enable_or_disable_property" do
      it "POSTs enabled flag" do
        allow(http).to receive(:post).with("/v1/properties/1040/enable-disable",
                                           body: { enabled: false })
                                     .and_return(ok_response)
        api.enable_or_disable_property(1040, enabled: false)
      end
    end

    describe "#delete_property" do
      it "sends DELETE to /v1/properties/1040" do
        allow(http).to receive(:delete).with("/v1/properties/1040")
                                       .and_return(ok_response)
        api.delete_property(1040)
      end
    end

    describe "#get_property_cleaners" do
      it "GETs /v1/properties/1040/cleaners" do
        allow(http).to receive(:get).with("/v1/properties/1040/cleaners")
                                    .and_return(ok_response([]))
        api.get_property_cleaners(1040)
      end
    end

    describe "#assign_cleaner_to_property" do
      it "POSTs cleanerId" do
        allow(http).to receive(:post).with("/v1/properties/1040/cleaners",
                                           body: { cleanerId: 5 })
                                     .and_return(ok_response)
        api.assign_cleaner_to_property(1040, cleaner_id: 5)
      end
    end

    describe "#unassign_cleaner_from_property" do
      it "sends DELETE to /v1/properties/1040/cleaners/5" do
        allow(http).to receive(:delete).with("/v1/properties/1040/cleaners/5")
                                       .and_return(ok_response)
        api.unassign_cleaner_from_property(1040, 5)
      end
    end

    describe "#add_ical_link" do
      it "PUTs icalLink" do
        allow(http).to receive(:put).with("/v1/properties/1040/ical",
                                          body: { icalLink: "https://calendar.example.com/feed.ics" })
                                    .and_return(ok_response)
        api.add_ical_link(1040, ical_link: "https://calendar.example.com/feed.ics")
      end
    end

    describe "#get_ical_link" do
      it "GETs /v1/properties/1040/ical" do
        allow(http).to receive(:get).with("/v1/properties/1040/ical")
                                    .and_return(ok_response)
        api.get_ical_link(1040)
      end
    end

    describe "#remove_ical_link" do
      it "sends DELETE with icalLink body" do
        allow(http).to receive(:delete).with("/v1/properties/1040/ical",
                                             body: { icalLink: "https://calendar.example.com/feed.ics" })
                                       .and_return(ok_response)
        api.remove_ical_link(1040, ical_link: "https://calendar.example.com/feed.ics")
      end
    end

    describe "#assign_checklist_to_property" do
      it "PUTs with updateUpcomingBookings=true" do
        allow(http).to receive(:put).with(include("updateUpcomingBookings=true"))
                                    .and_return(ok_response)
        api.assign_checklist_to_property(1040, 105, update_upcoming_bookings: true)
      end

      it "defaults updateUpcomingBookings to false" do
        allow(http).to receive(:put).with(include("updateUpcomingBookings=false"))
                                    .and_return(ok_response)
        api.assign_checklist_to_property(1040, 105)
      end
    end
  end

  # ---------------------------------------------------------------------------
  # Cleanster::Api::ChecklistsApi
  # ---------------------------------------------------------------------------
  describe Cleanster::Api::ChecklistsApi do
    let(:api) { described_class.new(http) }
    let(:http) { instance_double(Cleanster::HttpClient) }

    describe "#list_checklists" do
      it "GETs /v1/checklist" do
        allow(http).to receive(:get).with("/v1/checklist").and_return(ok_response([]))
        result = api.list_checklists
        expect(result).to be_a(Cleanster::Models::ApiResponse)
      end
    end

    describe "#get_checklist" do
      it "GETs /v1/checklist/105 and returns a Checklist with items" do
        data = {
          "id" => 105, "name" => "Standard",
          "items" => [{ "id" => 1, "description" => "Vacuum floors", "isCompleted" => false }]
        }
        allow(http).to receive(:get).with("/v1/checklist/105").and_return(ok_response(data))
        result = api.get_checklist(105)
        expect(result.data).to be_a(Cleanster::Models::Checklist)
        expect(result.data.items.first).to be_a(Cleanster::Models::ChecklistItem)
        expect(result.data.items.first.description).to eq("Vacuum floors")
      end
    end

    describe "#create_checklist" do
      it "POSTs name and items" do
        data = { "id" => 105, "name" => "Deep Clean", "items" => [] }
        allow(http).to receive(:post).with("/v1/checklist",
                                           body: { name: "Deep Clean", items: ["Mop floors"] })
                                     .and_return(ok_response(data))
        result = api.create_checklist(name: "Deep Clean", items: ["Mop floors"])
        expect(result.data).to be_a(Cleanster::Models::Checklist)
      end
    end

    describe "#update_checklist" do
      it "PUTs to /v1/checklist/105" do
        data = { "id" => 105, "name" => "Updated", "items" => [] }
        allow(http).to receive(:put).with("/v1/checklist/105",
                                          body: { name: "Updated", items: ["New task"] })
                                    .and_return(ok_response(data))
        result = api.update_checklist(105, name: "Updated", items: ["New task"])
        expect(result.data).to be_a(Cleanster::Models::Checklist)
      end
    end

    describe "#delete_checklist" do
      it "sends DELETE to /v1/checklist/105" do
        allow(http).to receive(:delete).with("/v1/checklist/105").and_return(ok_response)
        api.delete_checklist(105)
      end
    end
  end

  # ---------------------------------------------------------------------------
  # Cleanster::Api::OtherApi
  # ---------------------------------------------------------------------------
  describe Cleanster::Api::OtherApi do
    let(:api) { described_class.new(http) }
    let(:http) { instance_double(Cleanster::HttpClient) }

    describe "#get_services" do
      it "GETs /v1/services" do
        allow(http).to receive(:get).with("/v1/services").and_return(ok_response([]))
        api.get_services
      end
    end

    describe "#get_plans" do
      it "GETs /v1/plans with propertyId" do
        allow(http).to receive(:get).with("/v1/plans", params: { propertyId: 1004 })
                                    .and_return(ok_response([]))
        api.get_plans(1004)
      end
    end

    describe "#get_recommended_hours" do
      it "GETs with all params" do
        allow(http).to receive(:get).with("/v1/recommended-hours",
                                          params: { propertyId: 1004, bathroomCount: 2, roomCount: 3 })
                                    .and_return(ok_response)
        api.get_recommended_hours(1004, bathroom_count: 2, room_count: 3)
      end
    end

    describe "#calculate_cost" do
      it "POSTs to /v1/cost-estimate" do
        allow(http).to receive(:post).with("/v1/cost-estimate", body: anything)
                                     .and_return(ok_response)
        api.calculate_cost(property_id: 1004, plan_id: 2, hours: 3, coupon_code: "20POFF")
      end
    end

    describe "#get_cleaning_extras" do
      it "GETs /v1/cleaning-extras/1" do
        allow(http).to receive(:get).with("/v1/cleaning-extras/1").and_return(ok_response([]))
        api.get_cleaning_extras(1)
      end
    end

    describe "#get_available_cleaners" do
      it "POSTs to /v1/available-cleaners" do
        allow(http).to receive(:post).with("/v1/available-cleaners", body: anything)
                                     .and_return(ok_response([]))
        api.get_available_cleaners(property_id: 1004, date: "2025-06-15", time: "10:00")
      end
    end

    describe "#get_coupons" do
      it "GETs /v1/coupons" do
        allow(http).to receive(:get).with("/v1/coupons").and_return(ok_response([]))
        api.get_coupons
      end
    end
  end

  # ---------------------------------------------------------------------------
  # Cleanster::Api::BlacklistApi
  # ---------------------------------------------------------------------------
  describe Cleanster::Api::BlacklistApi do
    let(:api) { described_class.new(http) }
    let(:http) { instance_double(Cleanster::HttpClient) }

    describe "#list_blacklisted_cleaners" do
      it "GETs /v1/blacklist/cleaner" do
        allow(http).to receive(:get).with("/v1/blacklist/cleaner").and_return(ok_response([]))
        api.list_blacklisted_cleaners
      end
    end

    describe "#add_to_blacklist" do
      it "POSTs cleanerId with reason" do
        allow(http).to receive(:post).with("/v1/blacklist/cleaner",
                                           body: { cleanerId: 7, reason: "Damage" })
                                     .and_return(ok_response)
        api.add_to_blacklist(cleaner_id: 7, reason: "Damage")
      end

      it "omits reason when not provided" do
        allow(http).to receive(:post) do |_path, body:|
          expect(body).not_to have_key(:reason)
          ok_response
        end
        api.add_to_blacklist(cleaner_id: 7)
      end
    end

    describe "#remove_from_blacklist" do
      it "sends DELETE with cleanerId body" do
        allow(http).to receive(:delete).with("/v1/blacklist/cleaner",
                                             body: { cleanerId: 7 })
                                       .and_return(ok_response)
        api.remove_from_blacklist(cleaner_id: 7)
      end
    end
  end

  # ---------------------------------------------------------------------------
  # Cleanster::Api::PaymentMethodsApi
  # ---------------------------------------------------------------------------
  describe Cleanster::Api::PaymentMethodsApi do
    let(:api) { described_class.new(http) }
    let(:http) { instance_double(Cleanster::HttpClient) }

    describe "#get_setup_intent_details" do
      it "GETs /v1/payment-methods/setup-intent" do
        allow(http).to receive(:get).with("/v1/payment-methods/setup-intent-details").and_return(ok_response)
        api.get_setup_intent_details
      end
    end

    describe "#get_paypal_client_token" do
      it "GETs /v1/payment-methods/paypal-client-token" do
        allow(http).to receive(:get).with("/v1/payment-methods/paypal-client-token").and_return(ok_response)
        api.get_paypal_client_token
      end
    end

    describe "#add_payment_method" do
      it "POSTs to /v1/payment-methods" do
        allow(http).to receive(:post).with("/v1/payment-methods",
                                           body: { "paymentMethodId" => "pm_xxx" })
                                     .and_return(ok_response)
        api.add_payment_method("paymentMethodId" => "pm_xxx")
      end
    end

    describe "#get_payment_methods" do
      it "GETs /v1/payment-methods" do
        allow(http).to receive(:get).with("/v1/payment-methods").and_return(ok_response([]))
        api.get_payment_methods
      end
    end

    describe "#delete_payment_method" do
      it "sends DELETE to /v1/payment-methods/193" do
        allow(http).to receive(:delete).with("/v1/payment-methods/193").and_return(ok_response)
        api.delete_payment_method(193)
      end
    end

    describe "#set_default_payment_method" do
      it "PUTs to /v1/payment-methods/193/default" do
        allow(http).to receive(:put).with("/v1/payment-methods/193/default").and_return(ok_response)
        api.set_default_payment_method(193)
      end
    end
  end

  # ---------------------------------------------------------------------------
  # Cleanster::Api::WebhooksApi
  # ---------------------------------------------------------------------------
  describe Cleanster::Api::WebhooksApi do
    let(:api) { described_class.new(http) }
    let(:http) { instance_double(Cleanster::HttpClient) }

    describe "#list_webhooks" do
      it "GETs /v1/webhooks" do
        allow(http).to receive(:get).with("/v1/webhooks").and_return(ok_response([]))
        api.list_webhooks
      end
    end

    describe "#create_webhook" do
      it "POSTs webhook request" do
        body = { "url" => "https://example.com/hooks", "event" => "booking.status_changed" }
        allow(http).to receive(:post).with("/v1/webhooks", body: body).and_return(ok_response)
        api.create_webhook(body)
      end
    end

    describe "#update_webhook" do
      it "PUTs to /v1/webhooks/50" do
        body = { "url" => "https://example.com/v2/hooks", "event" => "booking.status_changed" }
        allow(http).to receive(:put).with("/v1/webhooks/50", body: body).and_return(ok_response)
        api.update_webhook(50, body)
      end
    end

    describe "#delete_webhook" do
      it "sends DELETE to /v1/webhooks/50" do
        allow(http).to receive(:delete).with("/v1/webhooks/50").and_return(ok_response)
        api.delete_webhook(50)
      end
    end
  end

  # ---------------------------------------------------------------------------
  # Exceptions
  # ---------------------------------------------------------------------------
  describe Cleanster::AuthError do
    it "always has status_code 401" do
      err = described_class.new
      expect(err.status_code).to eq(401)
    end

    it "stores the response_body" do
      err = described_class.new("Unauthorized", response_body: '{"message":"bad key"}')
      expect(err.response_body).to eq('{"message":"bad key"}')
    end

    it "is a CleansterError" do
      expect(described_class.new).to be_a(Cleanster::CleansterError)
    end

    it "is a StandardError" do
      expect(described_class.new).to be_a(StandardError)
    end

    it "has a default message" do
      expect(described_class.new.message).to include("Unauthorized")
    end
  end

  describe Cleanster::ApiError do
    it "stores the status_code" do
      err = described_class.new(422, "Validation failed")
      expect(err.status_code).to eq(422)
    end

    it "stores the message" do
      err = described_class.new(422, "Validation failed")
      expect(err.message).to eq("Validation failed")
    end

    it "stores the response_body" do
      err = described_class.new(404, "Not found", response_body: "raw body")
      expect(err.response_body).to eq("raw body")
    end

    it "is a CleansterError" do
      expect(described_class.new(500, "Error")).to be_a(Cleanster::CleansterError)
    end

    it "is a StandardError" do
      expect(described_class.new(500, "Error")).to be_a(StandardError)
    end
  end

  describe Cleanster::CleansterError do
    it "is a StandardError" do
      expect(described_class.new("boom")).to be_a(StandardError)
    end

    it "propagates via raise / rescue" do
      expect { raise Cleanster::CleansterError, "test error" }
        .to raise_error(Cleanster::CleansterError, "test error")
    end
  end

  # ---------------------------------------------------------------------------
  # Models
  # ---------------------------------------------------------------------------
  describe Cleanster::Models::Booking do
    let(:data) do
      {
        "id" => 16926, "status" => "COMPLETED", "date" => "2025-06-15", "time" => "10:00",
        "hours" => 3, "cost" => 150.0, "propertyId" => 1004, "cleanerId" => 5,
        "planId" => 2, "roomCount" => 2, "bathroomCount" => 1,
        "extraSupplies" => false, "paymentMethodId" => 10
      }
    end

    it "maps all fields correctly" do
      booking = described_class.new(data)
      expect(booking.id).to eq(16926)
      expect(booking.status).to eq("COMPLETED")
      expect(booking.date).to eq("2025-06-15")
      expect(booking.time).to eq("10:00")
      expect(booking.hours).to eq(3)
      expect(booking.cost).to eq(150.0)
      expect(booking.property_id).to eq(1004)
      expect(booking.cleaner_id).to eq(5)
      expect(booking.plan_id).to eq(2)
      expect(booking.room_count).to eq(2)
      expect(booking.bathroom_count).to eq(1)
      expect(booking.extra_supplies).to eq(false)
      expect(booking.payment_method_id).to eq(10)
    end

    it "has a useful to_s" do
      expect(described_class.new(data).to_s).to include("16926")
    end
  end

  describe Cleanster::Models::User do
    it "maps fields including token" do
      user = described_class.new("id" => 42, "email" => "x@y.com",
                                 "firstName" => "Jane", "lastName" => "Doe", "token" => "tok")
      expect(user.id).to eq(42)
      expect(user.email).to eq("x@y.com")
      expect(user.first_name).to eq("Jane")
      expect(user.last_name).to eq("Doe")
      expect(user.token).to eq("tok")
    end

    it "returns nil for optional phone when missing" do
      user = described_class.new("id" => 1, "email" => "a@b.com", "firstName" => "A", "lastName" => "B")
      expect(user.phone).to be_nil
    end
  end

  describe Cleanster::Models::Property do
    it "maps all fields" do
      prop = described_class.new("id" => 1040, "name" => "Beach House", "address" => "123 St",
                                 "city" => "Miami", "country" => "USA",
                                 "roomCount" => 3, "bathroomCount" => 2, "serviceId" => 1)
      expect(prop.id).to eq(1040)
      expect(prop.name).to eq("Beach House")
      expect(prop.city).to eq("Miami")
      expect(prop.room_count).to eq(3)
      expect(prop.bathroom_count).to eq(2)
      expect(prop.service_id).to eq(1)
    end
  end

  describe Cleanster::Models::Checklist do
    it "builds items as ChecklistItem objects" do
      data = {
        "id" => 105, "name" => "Standard",
        "items" => [{ "id" => 1, "description" => "Vacuum", "isCompleted" => true }]
      }
      checklist = described_class.new(data)
      expect(checklist.id).to eq(105)
      expect(checklist.items.length).to eq(1)
      expect(checklist.items.first).to be_a(Cleanster::Models::ChecklistItem)
      expect(checklist.items.first.is_completed).to eq(true)
    end

    it "handles missing items gracefully" do
      checklist = described_class.new("id" => 1, "name" => "Empty")
      expect(checklist.items).to eq([])
    end
  end

  describe Cleanster::Models::ChecklistItem do
    it "maps all fields" do
      item = described_class.new("id" => 10, "description" => "Mop", "isCompleted" => false, "imageUrl" => "https://x.com/img.png")
      expect(item.id).to eq(10)
      expect(item.description).to eq("Mop")
      expect(item.is_completed).to eq(false)
      expect(item.image_url).to eq("https://x.com/img.png")
    end
  end

  describe Cleanster::Models::PaymentMethod do
    it "maps all fields" do
      pm = described_class.new("id" => 193, "type" => "card", "lastFour" => "4242",
                               "brand" => "visa", "isDefault" => true)
      expect(pm.id).to eq(193)
      expect(pm.type).to eq("card")
      expect(pm.last_four).to eq("4242")
      expect(pm.brand).to eq("visa")
      expect(pm.is_default).to eq(true)
    end
  end

  describe Cleanster::Models::ApiResponse do
    it "wraps status, message, and data" do
      resp = described_class.new(status: 200, message: "OK", data: "payload")
      expect(resp.status).to eq(200)
      expect(resp.message).to eq("OK")
      expect(resp.data).to eq("payload")
    end

    it "builds from a hash with no model_class" do
      hash = { "status" => 200, "message" => "OK", "data" => { "id" => 1 } }
      resp = described_class.from_hash(hash)
      expect(resp.data).to eq({ "id" => 1 })
    end

    it "builds from a hash with a model_class" do
      hash = { "status" => 200, "message" => "OK",
               "data" => { "id" => 42, "email" => "x@y.com", "firstName" => "A", "lastName" => "B" } }
      resp = described_class.from_hash(hash, model_class: Cleanster::Models::User)
      expect(resp.data).to be_a(Cleanster::Models::User)
      expect(resp.data.id).to eq(42)
    end

    it "builds a list from hash when data is an Array" do
      hash = { "status" => 200, "message" => "OK",
               "data" => [
                 { "id" => 1, "email" => "a@b.com", "firstName" => "A", "lastName" => "B" },
                 { "id" => 2, "email" => "c@d.com", "firstName" => "C", "lastName" => "D" }
               ] }
      resp = described_class.from_hash(hash, model_class: Cleanster::Models::User)
      expect(resp.data.length).to eq(2)
      expect(resp.data.first).to be_a(Cleanster::Models::User)
    end
  end
end
