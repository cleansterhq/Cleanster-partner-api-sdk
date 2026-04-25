"""
Comprehensive unit tests for the Cleanster Python SDK.
Uses unittest.mock to simulate HTTP responses — no network access or API keys needed.
"""

import json
import unittest
from unittest.mock import MagicMock, call, patch

from cleanster import CleansterClient, CleansterConfig
from cleanster.api.blacklist import BlacklistApi
from cleanster.api.bookings import BookingsApi
from cleanster.api.checklists import ChecklistsApi
from cleanster.api.other import OtherApi
from cleanster.api.payment_methods import PaymentMethodsApi
from cleanster.api.properties import PropertiesApi
from cleanster.api.users import UsersApi
from cleanster.api.webhooks import WebhooksApi
from cleanster.config import PRODUCTION_BASE_URL, SANDBOX_BASE_URL
from cleanster.exceptions import CleansterApiException, CleansterAuthException, CleansterException
from cleanster.http_client import HttpClient
from cleanster.models.booking import Booking
from cleanster.models.checklist import Checklist, ChecklistItem
from cleanster.models.payment_method import PaymentMethod
from cleanster.models.property import Property
from cleanster.models.response import ApiResponse
from cleanster.models.user import User


# ---------------------------------------------------------------------------
# Helpers
# ---------------------------------------------------------------------------

def make_http() -> MagicMock:
    """Return a MagicMock that stands in for HttpClient."""
    return MagicMock(spec=HttpClient)


def ok(data=None) -> dict:
    """Build a minimal success response dict."""
    return {"status": 200, "message": "OK", "data": data}


# ---------------------------------------------------------------------------
# CleansterConfig Tests
# ---------------------------------------------------------------------------

class TestCleansterConfig(unittest.TestCase):

    def test_sandbox_url(self):
        config = CleansterConfig.sandbox("my-key")
        self.assertEqual(SANDBOX_BASE_URL, config.base_url)

    def test_production_url(self):
        config = CleansterConfig.production("my-key")
        self.assertEqual(PRODUCTION_BASE_URL, config.base_url)

    def test_blank_access_key_raises(self):
        with self.assertRaises(ValueError):
            CleansterConfig("", SANDBOX_BASE_URL)

    def test_none_access_key_raises(self):
        with self.assertRaises((ValueError, TypeError)):
            CleansterConfig(None, SANDBOX_BASE_URL)

    def test_whitespace_only_key_raises(self):
        with self.assertRaises(ValueError):
            CleansterConfig("   ", SANDBOX_BASE_URL)

    def test_custom_base_url(self):
        config = CleansterConfig("key", base_url="https://custom.example.com/")
        self.assertEqual("https://custom.example.com", config.base_url)

    def test_default_timeout(self):
        config = CleansterConfig.sandbox("key")
        self.assertGreater(config.timeout, 0)

    def test_builder_sandbox(self):
        config = CleansterConfig.builder("key").sandbox().build()
        self.assertEqual(SANDBOX_BASE_URL, config.base_url)

    def test_builder_production(self):
        config = CleansterConfig.builder("key").production().build()
        self.assertEqual(PRODUCTION_BASE_URL, config.base_url)

    def test_builder_custom_timeout(self):
        config = CleansterConfig.builder("key").timeout(60).build()
        self.assertEqual(60, config.timeout)

    def test_builder_custom_url(self):
        config = CleansterConfig.builder("key").base_url("https://my.api.com").build()
        self.assertEqual("https://my.api.com", config.base_url)


# ---------------------------------------------------------------------------
# CleansterClient Tests
# ---------------------------------------------------------------------------

class TestCleansterClient(unittest.TestCase):

    def test_sandbox_factory(self):
        client = CleansterClient.sandbox("test-key")
        self.assertIsNotNone(client)

    def test_production_factory(self):
        client = CleansterClient.production("test-key")
        self.assertIsNotNone(client)

    def test_api_namespaces_exist(self):
        client = CleansterClient.sandbox("key")
        self.assertIsInstance(client.bookings, BookingsApi)
        self.assertIsInstance(client.users, UsersApi)
        self.assertIsInstance(client.properties, PropertiesApi)
        self.assertIsInstance(client.checklists, ChecklistsApi)
        self.assertIsInstance(client.other, OtherApi)
        self.assertIsInstance(client.blacklist, BlacklistApi)
        self.assertIsInstance(client.payment_methods, PaymentMethodsApi)
        self.assertIsInstance(client.webhooks, WebhooksApi)

    def test_set_and_get_access_token(self):
        client = CleansterClient.sandbox("key")
        self.assertIsNone(client.get_access_token())
        client.set_access_token("my-token-abc")
        self.assertEqual("my-token-abc", client.get_access_token())

    def test_clear_access_token(self):
        client = CleansterClient.sandbox("key")
        client.set_access_token("token")
        client.set_access_token(None)
        self.assertIsNone(client.get_access_token())


# ---------------------------------------------------------------------------
# BookingsApi Tests
# ---------------------------------------------------------------------------

class TestBookingsApi(unittest.TestCase):

    def test_get_bookings_no_params(self):
        http = make_http()
        http.get.return_value = ok([])
        api = BookingsApi(http)

        result = api.get_bookings()

        http.get.assert_called_once_with("/v1/bookings", params=None)
        self.assertEqual(200, result.status)

    def test_get_bookings_with_page_no(self):
        http = make_http()
        http.get.return_value = ok([])
        api = BookingsApi(http)

        api.get_bookings(page_no=2)

        args, kwargs = http.get.call_args
        self.assertEqual("/v1/bookings", args[0])
        self.assertEqual(2, kwargs["params"]["pageNo"])

    def test_get_bookings_with_status(self):
        http = make_http()
        http.get.return_value = ok([])
        api = BookingsApi(http)

        api.get_bookings(status="COMPLETED")

        args, kwargs = http.get.call_args
        self.assertEqual("COMPLETED", kwargs["params"]["status"])

    def test_get_bookings_with_both_params(self):
        http = make_http()
        http.get.return_value = ok([])
        api = BookingsApi(http)

        api.get_bookings(page_no=1, status="OPEN")

        args, kwargs = http.get.call_args
        self.assertEqual(1, kwargs["params"]["pageNo"])
        self.assertEqual("OPEN", kwargs["params"]["status"])

    def test_create_booking(self):
        http = make_http()
        http.post.return_value = ok({"id": 999, "status": "OPEN", "hours": 3.0})
        api = BookingsApi(http)

        request = {
            "date": "2025-06-15",
            "time": "10:00",
            "propertyId": 1004,
            "roomCount": 2,
            "bathroomCount": 1,
            "planId": 5,
            "hours": 3.0,
            "extraSupplies": False,
            "paymentMethodId": 10,
        }
        result = api.create_booking(request)

        http.post.assert_called_once_with("/v1/bookings/create", body=request)
        self.assertIsInstance(result.data, Booking)
        self.assertEqual(999, result.data.id)
        self.assertEqual("OPEN", result.data.status)

    def test_get_booking_details(self):
        http = make_http()
        http.get.return_value = ok({"id": 16926, "status": "COMPLETED"})
        api = BookingsApi(http)

        result = api.get_booking_details(16926)

        http.get.assert_called_once_with("/v1/bookings/16926")
        self.assertIsInstance(result.data, Booking)
        self.assertEqual(16926, result.data.id)
        self.assertEqual("COMPLETED", result.data.status)

    def test_cancel_booking(self):
        http = make_http()
        http.post.return_value = ok()
        api = BookingsApi(http)

        api.cancel_booking(16459, reason="Changed plans")

        http.post.assert_called_once_with(
            "/v1/bookings/16459/cancel",
            body={"reason": "Changed plans"},
        )

    def test_cancel_booking_no_reason(self):
        http = make_http()
        http.post.return_value = ok()
        api = BookingsApi(http)

        api.cancel_booking(16459)

        args, kwargs = http.post.call_args
        self.assertEqual("/v1/bookings/16459/cancel", args[0])
        self.assertEqual({}, kwargs["body"])

    def test_reschedule_booking(self):
        http = make_http()
        http.post.return_value = ok()
        api = BookingsApi(http)

        api.reschedule_booking(16459, "2025-07-01", "14:00")

        http.post.assert_called_once_with(
            "/v1/bookings/16459/reschedule",
            body={"date": "2025-07-01", "time": "14:00"},
        )

    def test_assign_cleaner(self):
        http = make_http()
        http.post.return_value = ok()
        api = BookingsApi(http)

        api.assign_cleaner(16459, 5)

        http.post.assert_called_once_with(
            "/v1/bookings/16459/cleaner",
            body={"cleanerId": 5},
        )

    def test_remove_assigned_cleaner(self):
        http = make_http()
        http.delete.return_value = ok()
        api = BookingsApi(http)

        api.remove_assigned_cleaner(16459)

        http.delete.assert_called_once_with("/v1/bookings/16459/cleaner")

    def test_adjust_hours(self):
        http = make_http()
        http.post.return_value = ok()
        api = BookingsApi(http)

        api.adjust_hours(16459, 4.0)

        http.post.assert_called_once_with(
            "/v1/bookings/16459/hours",
            body={"hours": 4.0},
        )

    def test_pay_expenses(self):
        http = make_http()
        http.post.return_value = ok()
        api = BookingsApi(http)

        api.pay_expenses(16926, 10)

        http.post.assert_called_once_with(
            "/v1/bookings/16926/expenses",
            body={"paymentMethodId": 10},
        )

    def test_get_booking_inspection(self):
        http = make_http()
        http.get.return_value = ok({})
        api = BookingsApi(http)

        api.get_booking_inspection(16926)

        http.get.assert_called_once_with("/v1/bookings/16926/inspection")

    def test_get_booking_inspection_details(self):
        http = make_http()
        http.get.return_value = ok({})
        api = BookingsApi(http)

        api.get_booking_inspection_details(16926)

        http.get.assert_called_once_with("/v1/bookings/16926/inspection/details")

    def test_assign_checklist_to_booking(self):
        http = make_http()
        http.post.return_value = ok()
        api = BookingsApi(http)

        api.assign_checklist_to_booking(16926, 105)

        http.put.assert_called_once_with("/v1/bookings/16926/checklist/105")

    def test_submit_feedback(self):
        http = make_http()
        http.post.return_value = ok()
        api = BookingsApi(http)

        api.submit_feedback(16926, 5, "Excellent service!")

        http.post.assert_called_once_with(
            "/v1/bookings/16926/feedback",
            body={"rating": 5, "comment": "Excellent service!"},
        )

    def test_submit_feedback_no_comment(self):
        http = make_http()
        http.post.return_value = ok()
        api = BookingsApi(http)

        api.submit_feedback(16926, 4)

        args, kwargs = http.post.call_args
        self.assertNotIn("comment", kwargs["body"])

    def test_add_tip(self):
        http = make_http()
        http.post.return_value = ok()
        api = BookingsApi(http)

        api.add_tip(16926, 20.0, 10)

        http.post.assert_called_once_with(
            "/v1/bookings/16926/tip",
            body={"amount": 20.0, "paymentMethodId": 10},
        )

    def test_get_chat(self):
        http = make_http()
        http.get.return_value = ok({})
        api = BookingsApi(http)

        api.get_chat(17142)

        http.get.assert_called_once_with("/v1/bookings/17142/chat")

    def test_send_message(self):
        http = make_http()
        http.post.return_value = ok()
        api = BookingsApi(http)

        api.send_message(16466, "Hello cleaner!")

        http.post.assert_called_once_with(
            "/v1/bookings/16466/chat",
            body={"message": "Hello cleaner!"},
        )

    def test_delete_message(self):
        http = make_http()
        http.delete.return_value = ok()
        api = BookingsApi(http)

        api.delete_message(16466, "msg-abc-123")

        http.delete.assert_called_once_with("/v1/bookings/16466/chat/msg-abc-123")


# ---------------------------------------------------------------------------
# UsersApi Tests
# ---------------------------------------------------------------------------

class TestUsersApi(unittest.TestCase):

    def test_create_user(self):
        http = make_http()
        http.post.return_value = ok({"id": 42, "email": "jane@example.com",
                                     "firstName": "Jane", "lastName": "Smith"})
        api = UsersApi(http)

        result = api.create_user("jane@example.com", "Jane", "Smith")

        http.post.assert_called_once_with(
            "/v1/user/account",
            body={"email": "jane@example.com", "firstName": "Jane", "lastName": "Smith"},
        )
        self.assertIsInstance(result.data, User)
        self.assertEqual(42, result.data.id)
        self.assertEqual("jane@example.com", result.data.email)
        self.assertEqual("Jane", result.data.first_name)

    def test_create_user_with_phone(self):
        http = make_http()
        http.post.return_value = ok({"id": 43})
        api = UsersApi(http)

        api.create_user("a@b.com", "A", "B", phone="+15551234567")

        args, kwargs = http.post.call_args
        self.assertEqual("+15551234567", kwargs["body"]["phone"])

    def test_fetch_access_token(self):
        http = make_http()
        http.get.return_value = ok({"id": 42, "token": "some-long-token"})
        api = UsersApi(http)

        result = api.fetch_access_token(42)

        http.get.assert_called_once_with("/v1/user/access-token/42")
        self.assertIsInstance(result.data, User)
        self.assertEqual("some-long-token", result.data.token)

    def test_verify_jwt(self):
        http = make_http()
        http.post.return_value = ok({})
        api = UsersApi(http)

        api.verify_jwt("my.jwt.token")

        http.post.assert_called_once_with(
            "/v1/user/verify-jwt",
            body={"token": "my.jwt.token"},
        )


# ---------------------------------------------------------------------------
# PropertiesApi Tests
# ---------------------------------------------------------------------------

class TestPropertiesApi(unittest.TestCase):

    def test_list_properties_no_filter(self):
        http = make_http()
        http.get.return_value = ok([])
        api = PropertiesApi(http)

        api.list_properties()

        http.get.assert_called_once_with("/v1/properties", params=None)

    def test_list_properties_with_service_id(self):
        http = make_http()
        http.get.return_value = ok([])
        api = PropertiesApi(http)

        api.list_properties(service_id=1)

        args, kwargs = http.get.call_args
        self.assertEqual({"serviceId": 1}, kwargs["params"])

    def test_add_property(self):
        http = make_http()
        http.post.return_value = ok({"id": 1040, "name": "Beach House"})
        api = PropertiesApi(http)

        request = {
            "name": "Beach House",
            "address": "123 Ocean Dr",
            "city": "Miami",
            "country": "USA",
            "roomCount": 3,
            "bathroomCount": 2,
            "serviceId": 1,
        }
        result = api.add_property(request)

        http.post.assert_called_once_with("/v1/properties", body=request)
        self.assertIsInstance(result.data, Property)
        self.assertEqual(1040, result.data.id)
        self.assertEqual("Beach House", result.data.name)

    def test_get_property(self):
        http = make_http()
        http.get.return_value = ok({"id": 1040, "name": "Beach House", "city": "Miami"})
        api = PropertiesApi(http)

        result = api.get_property(1040)

        http.get.assert_called_once_with("/v1/properties/1040")
        self.assertEqual("Miami", result.data.city)

    def test_delete_property(self):
        http = make_http()
        http.delete.return_value = ok()
        api = PropertiesApi(http)

        api.delete_property(1004)

        http.delete.assert_called_once_with("/v1/properties/1004")

    def test_enable_or_disable_property(self):
        http = make_http()
        http.post.return_value = ok()
        api = PropertiesApi(http)

        api.enable_or_disable_property(1040, False)

        http.post.assert_called_once_with(
            "/v1/properties/1040/enable-disable",
            body={"enabled": False},
        )

    def test_get_property_cleaners(self):
        http = make_http()
        http.get.return_value = ok([])
        api = PropertiesApi(http)

        api.get_property_cleaners(1040)

        http.get.assert_called_once_with("/v1/properties/1040/cleaners")

    def test_assign_cleaner_to_property(self):
        http = make_http()
        http.post.return_value = ok()
        api = PropertiesApi(http)

        api.assign_cleaner_to_property(1040, 5)

        http.post.assert_called_once_with(
            "/v1/properties/1040/cleaners",
            body={"cleanerId": 5},
        )

    def test_unassign_cleaner_from_property(self):
        http = make_http()
        http.delete.return_value = ok()
        api = PropertiesApi(http)

        api.unassign_cleaner_from_property(1040, 5)

        http.delete.assert_called_once_with("/v1/properties/1040/cleaners/5")

    def test_add_ical_link(self):
        http = make_http()
        http.put.return_value = ok()
        api = PropertiesApi(http)

        api.add_ical_link(1040, "https://cal.example.com/feed.ics")

        http.put.assert_called_once_with(
            "/v1/properties/1040/ical",
            body={"icalLink": "https://cal.example.com/feed.ics"},
        )

    def test_get_ical_link(self):
        http = make_http()
        http.get.return_value = ok({})
        api = PropertiesApi(http)

        api.get_ical_link(1040)

        http.get.assert_called_once_with("/v1/properties/1040/ical")

    def test_set_default_checklist(self):
        http = make_http()
        http.put.return_value = ok()
        api = PropertiesApi(http)

        api.set_default_checklist(1040, 105, update_upcoming_bookings=True)

        args = http.put.call_args[0]
        self.assertIn("/v1/properties/1040/checklist/105", args[0])
        self.assertIn("updateUpcomingBookings=true", args[0])


# ---------------------------------------------------------------------------
# ChecklistsApi Tests
# ---------------------------------------------------------------------------

class TestChecklistsApi(unittest.TestCase):

    def test_list_checklists(self):
        http = make_http()
        http.get.return_value = ok([])
        api = ChecklistsApi(http)

        api.list_checklists()

        http.get.assert_called_once_with("/v1/checklist")

    def test_get_checklist(self):
        http = make_http()
        http.get.return_value = ok({
            "id": 105,
            "name": "Standard Clean",
            "items": [
                {"id": 1, "description": "Vacuum floors", "isCompleted": False}
            ],
        })
        api = ChecklistsApi(http)

        result = api.get_checklist(105)

        http.get.assert_called_once_with("/v1/checklist/105")
        self.assertIsInstance(result.data, Checklist)
        self.assertEqual(105, result.data.id)
        self.assertEqual("Standard Clean", result.data.name)
        self.assertEqual(1, len(result.data.items))
        self.assertIsInstance(result.data.items[0], ChecklistItem)
        self.assertEqual("Vacuum floors", result.data.items[0].description)

    def test_create_checklist(self):
        http = make_http()
        http.post.return_value = ok({"id": 200, "name": "My Checklist"})
        api = ChecklistsApi(http)

        items = ["Task 1", "Task 2", "Task 3"]
        result = api.create_checklist("My Checklist", items)

        http.post.assert_called_once_with(
            "/v1/checklist",
            body={"name": "My Checklist", "items": items},
        )
        self.assertEqual(200, result.data.id)

    def test_update_checklist(self):
        http = make_http()
        http.put.return_value = ok({"id": 200, "name": "Updated"})
        api = ChecklistsApi(http)

        api.update_checklist(200, "Updated", ["New task"])

        http.put.assert_called_once_with(
            "/v1/checklist/200",
            body={"name": "Updated", "items": ["New task"]},
        )

    def test_delete_checklist(self):
        http = make_http()
        http.delete.return_value = ok()
        api = ChecklistsApi(http)

        api.delete_checklist(105)

        http.delete.assert_called_once_with("/v1/checklist/105")

    def test_upload_checklist_image_calls_correct_path(self):
        http = make_http()
        http.post_multipart.return_value = ok()
        api = ChecklistsApi(http)

        api.upload_checklist_image(105, b"imagedata", "photo.jpg")

        http.post_multipart.assert_called_once_with("/v1/checklist/105/upload", b"imagedata", "photo.jpg")

    def test_upload_checklist_image_returns_response(self):
        http = make_http()
        http.post_multipart.return_value = ok()
        api = ChecklistsApi(http)

        resp = api.upload_checklist_image(105, b"imagedata", "photo.jpg")

        self.assertEqual(resp.status, 200)


# ---------------------------------------------------------------------------
# OtherApi Tests
# ---------------------------------------------------------------------------

class TestOtherApi(unittest.TestCase):

    def test_get_services(self):
        http = make_http()
        http.get.return_value = ok([])
        api = OtherApi(http)

        api.get_services()

        http.get.assert_called_once_with("/v1/services")

    def test_get_plans(self):
        http = make_http()
        http.get.return_value = ok([])
        api = OtherApi(http)

        api.get_plans(1004)

        http.get.assert_called_once_with("/v1/plans", params={"propertyId": 1004})

    def test_get_recommended_hours(self):
        http = make_http()
        http.get.return_value = ok({})
        api = OtherApi(http)

        api.get_recommended_hours(1004, 2, 3)

        http.get.assert_called_once_with(
            "/v1/recommended-hours",
            params={"propertyId": 1004, "bathroomCount": 2, "roomCount": 3},
        )

    def test_get_cost_estimate(self):
        http = make_http()
        http.post.return_value = ok({})
        api = OtherApi(http)

        request = {"propertyId": 1004, "planId": 2, "hours": 3.0}
        api.get_cost_estimate(request)

        http.post.assert_called_once_with("/v1/cost-estimate", body=request)

    def test_get_cleaning_extras(self):
        http = make_http()
        http.get.return_value = ok([])
        api = OtherApi(http)

        api.get_cleaning_extras(1)

        http.get.assert_called_once_with("/v1/cleaning-extras/1")

    def test_get_available_cleaners(self):
        http = make_http()
        http.post.return_value = ok([])
        api = OtherApi(http)

        request = {"propertyId": 1004, "date": "2025-06-15", "time": "10:00"}
        api.get_available_cleaners(request)

        http.post.assert_called_once_with("/v1/available-cleaners", body=request)

    def test_get_coupons(self):
        http = make_http()
        http.get.return_value = ok([])
        api = OtherApi(http)

        api.get_coupons()

        http.get.assert_called_once_with("/v1/coupons")

    def test_list_cleaners(self):
        http = make_http()
        http.get.return_value = ok([])
        api = OtherApi(http)

        api.list_cleaners()

        http.get.assert_called_once_with("/v1/cleaners", params=None)

    def test_get_cleaner(self):
        http = make_http()
        http.get.return_value = ok({})
        api = OtherApi(http)

        api.get_cleaner(789)

        http.get.assert_called_once_with("/v1/cleaners/789")


# ---------------------------------------------------------------------------
# BlacklistApi Tests
# ---------------------------------------------------------------------------

class TestBlacklistApi(unittest.TestCase):

    def test_list_blacklisted_cleaners(self):
        http = make_http()
        http.get.return_value = ok([])
        api = BlacklistApi(http)

        api.list_blacklisted_cleaners()

        http.get.assert_called_once_with("/v1/blacklist/cleaner")

    def test_add_to_blacklist(self):
        http = make_http()
        http.post.return_value = ok()
        api = BlacklistApi(http)

        api.add_to_blacklist(7, reason="Damaged furniture")

        http.post.assert_called_once_with(
            "/v1/blacklist/cleaner",
            body={"cleanerId": 7, "reason": "Damaged furniture"},
        )

    def test_add_to_blacklist_no_reason(self):
        http = make_http()
        http.post.return_value = ok()
        api = BlacklistApi(http)

        api.add_to_blacklist(7)

        args, kwargs = http.post.call_args
        self.assertNotIn("reason", kwargs["body"])

    def test_remove_from_blacklist(self):
        http = make_http()
        http.delete.return_value = ok()
        api = BlacklistApi(http)

        api.remove_from_blacklist(7)

        http.delete.assert_called_once_with(
            "/v1/blacklist/cleaner",
            body={"cleanerId": 7},
        )


# ---------------------------------------------------------------------------
# PaymentMethodsApi Tests
# ---------------------------------------------------------------------------

class TestPaymentMethodsApi(unittest.TestCase):

    def test_get_setup_intent_details(self):
        http = make_http()
        http.get.return_value = ok({})
        api = PaymentMethodsApi(http)

        api.get_setup_intent_details()

        http.get.assert_called_once_with("/v1/payment-methods/setup-intent-details")

    def test_get_paypal_client_token(self):
        http = make_http()
        http.get.return_value = ok({})
        api = PaymentMethodsApi(http)

        api.get_paypal_client_token()

        http.get.assert_called_once_with("/v1/payment-methods/paypal-client-token")

    def test_add_payment_method(self):
        http = make_http()
        http.post.return_value = ok({})
        api = PaymentMethodsApi(http)

        request = {"paymentMethodId": "pm_xxxx"}
        api.add_payment_method(request)

        http.post.assert_called_once_with("/v1/payment-methods", body=request)

    def test_get_payment_methods(self):
        http = make_http()
        http.get.return_value = ok([])
        api = PaymentMethodsApi(http)

        api.get_payment_methods()

        http.get.assert_called_once_with("/v1/payment-methods")

    def test_delete_payment_method(self):
        http = make_http()
        http.delete.return_value = ok()
        api = PaymentMethodsApi(http)

        api.delete_payment_method(193)

        http.delete.assert_called_once_with("/v1/payment-methods/193")

    def test_set_default_payment_method(self):
        http = make_http()
        http.put.return_value = ok()
        api = PaymentMethodsApi(http)

        api.set_default_payment_method(193)

        http.put.assert_called_once_with("/v1/payment-methods/193/default")


# ---------------------------------------------------------------------------
# WebhooksApi Tests
# ---------------------------------------------------------------------------

class TestWebhooksApi(unittest.TestCase):

    def test_list_webhooks(self):
        http = make_http()
        http.get.return_value = ok([])
        api = WebhooksApi(http)

        api.list_webhooks()

        http.get.assert_called_once_with("/v1/webhooks")

    def test_create_webhook(self):
        http = make_http()
        http.post.return_value = ok({"id": 50})
        api = WebhooksApi(http)

        request = {"url": "https://example.com/webhook", "event": "booking.created"}
        api.create_webhook(request)

        http.post.assert_called_once_with("/v1/webhooks", body=request)

    def test_update_webhook(self):
        http = make_http()
        http.put.return_value = ok()
        api = WebhooksApi(http)

        request = {"url": "https://example.com/v2"}
        api.update_webhook(50, request)

        http.put.assert_called_once_with("/v1/webhooks/50", body=request)

    def test_delete_webhook(self):
        http = make_http()
        http.delete.return_value = ok()
        api = WebhooksApi(http)

        api.delete_webhook(50)

        http.delete.assert_called_once_with("/v1/webhooks/50")


# ---------------------------------------------------------------------------
# Exception Tests
# ---------------------------------------------------------------------------

class TestExceptions(unittest.TestCase):

    def test_auth_exception_status_code(self):
        ex = CleansterAuthException("Unauthorized", "{}")
        self.assertEqual(401, ex.status_code)
        self.assertEqual("Unauthorized", str(ex))
        self.assertEqual("{}", ex.response_body)

    def test_api_exception_stores_status_code(self):
        ex = CleansterApiException(404, "Not Found", "{}")
        self.assertEqual(404, ex.status_code)
        self.assertEqual("Not Found", str(ex))

    def test_api_exception_500(self):
        ex = CleansterApiException(500, "Server Error", "{}")
        self.assertEqual(500, ex.status_code)

    def test_base_exception(self):
        ex = CleansterException("Network error")
        self.assertEqual("Network error", str(ex))

    def test_auth_exception_is_cleanster_exception(self):
        self.assertTrue(issubclass(CleansterAuthException, CleansterException))

    def test_api_exception_is_cleanster_exception(self):
        self.assertTrue(issubclass(CleansterApiException, CleansterException))

    def test_exception_propagates_from_api(self):
        http = make_http()
        http.get.side_effect = CleansterAuthException("Unauthorized", "{}")
        api = BookingsApi(http)

        with self.assertRaises(CleansterAuthException):
            api.get_bookings()

    def test_api_exception_propagates(self):
        http = make_http()
        http.get.side_effect = CleansterApiException(404, "Not Found", "{}")
        api = BookingsApi(http)

        with self.assertRaises(CleansterApiException) as ctx:
            api.get_booking_details(99999)
        self.assertEqual(404, ctx.exception.status_code)


# ---------------------------------------------------------------------------
# Model Tests
# ---------------------------------------------------------------------------

class TestModels(unittest.TestCase):

    def test_booking_model(self):
        b = Booking({"id": 100, "status": "OPEN", "hours": 3.0,
                     "propertyId": 1004, "cost": 99.99})
        self.assertEqual(100, b.id)
        self.assertEqual("OPEN", b.status)
        self.assertEqual(3.0, b.hours)
        self.assertEqual(1004, b.property_id)
        self.assertAlmostEqual(99.99, b.cost, places=2)

    def test_booking_missing_fields_are_none(self):
        b = Booking({})
        self.assertIsNone(b.id)
        self.assertIsNone(b.status)

    def test_user_model(self):
        u = User({"id": 42, "email": "a@b.com", "firstName": "Alice",
                  "lastName": "Smith", "token": "tok123"})
        self.assertEqual(42, u.id)
        self.assertEqual("a@b.com", u.email)
        self.assertEqual("Alice", u.first_name)
        self.assertEqual("Smith", u.last_name)
        self.assertEqual("tok123", u.token)

    def test_property_model(self):
        p = Property({"id": 1040, "name": "Beach House", "city": "Miami",
                      "country": "USA", "roomCount": 3, "bathroomCount": 2})
        self.assertEqual(1040, p.id)
        self.assertEqual("Beach House", p.name)
        self.assertEqual("Miami", p.city)
        self.assertEqual(3, p.room_count)
        self.assertEqual(2, p.bathroom_count)

    def test_checklist_model(self):
        c = Checklist({
            "id": 105,
            "name": "Standard Clean",
            "items": [
                {"id": 1, "description": "Vacuum", "isCompleted": False},
                {"id": 2, "description": "Mop", "isCompleted": True},
            ],
        })
        self.assertEqual(105, c.id)
        self.assertEqual("Standard Clean", c.name)
        self.assertEqual(2, len(c.items))
        self.assertIsInstance(c.items[0], ChecklistItem)
        self.assertEqual("Vacuum", c.items[0].description)
        self.assertFalse(c.items[0].is_completed)
        self.assertTrue(c.items[1].is_completed)

    def test_checklist_empty_items(self):
        c = Checklist({"id": 1, "name": "Empty"})
        self.assertEqual(0, len(c.items))

    def test_payment_method_model(self):
        pm = PaymentMethod({"id": 193, "type": "card", "lastFour": "4242",
                            "brand": "visa", "isDefault": True})
        self.assertEqual(193, pm.id)
        self.assertEqual("card", pm.type)
        self.assertEqual("4242", pm.last_four)
        self.assertTrue(pm.is_default)

    def test_api_response_model(self):
        response = ApiResponse(200, "OK", "some-data")
        self.assertEqual(200, response.status)
        self.assertEqual("OK", response.message)
        self.assertEqual("some-data", response.data)

    def test_api_response_from_dict(self):
        raw = {"status": 200, "message": "OK", "data": {"id": 5, "status": "OPEN"}}
        response = ApiResponse.from_dict(raw, data_factory=Booking)
        self.assertEqual(200, response.status)
        self.assertIsInstance(response.data, Booking)
        self.assertEqual(5, response.data.id)

    def test_api_response_from_dict_no_factory(self):
        raw = {"status": 200, "message": "OK", "data": [1, 2, 3]}
        response = ApiResponse.from_dict(raw)
        self.assertEqual([1, 2, 3], response.data)

    def test_api_response_from_dict_list(self):
        raw = {
            "status": 200,
            "message": "OK",
            "data": [{"id": 1, "status": "OPEN"}, {"id": 2, "status": "COMPLETED"}],
        }
        response = ApiResponse.from_dict(raw, data_factory=Booking)
        self.assertIsInstance(response.data, list)
        self.assertEqual(2, len(response.data))
        self.assertIsInstance(response.data[0], Booking)
        self.assertEqual(1, response.data[0].id)


if __name__ == "__main__":
    unittest.main()
