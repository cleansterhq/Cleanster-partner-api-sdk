"use strict";

const BASE_URL = "https://api.cleanster.com";

const getBookings = async (z, bundle) => {
  const response = await z.request({
    url: `${BASE_URL}/v1/bookings`,
    method: "GET",
    params: {
      page: 1,
      per_page: 10,
      sort_by: "created_at",
      sort_order: "desc",
    },
  });

  const data = response.data || {};
  const bookings = data.data || data.bookings || data || [];
  return Array.isArray(bookings) ? bookings : [];
};

module.exports = {
  key: "new_booking",
  noun: "Booking",

  display: {
    label: "New Booking",
    description:
      "Triggers when a new cleaning booking is created in Cleanster.",
    important: true,
  },

  operation: {
    type: "polling",
    perform: getBookings,

    sample: {
      id: 16459,
      status: "scheduled",
      scheduled_at: "2025-05-10T09:00:00Z",
      duration_hours: 3,
      notes: "Please bring extra cleaning supplies",
      property: {
        id: 42,
        address: "123 Main St",
        city: "Atlanta",
        state: "GA",
        zip: "30301",
      },
      cleaner: null,
      created_at: "2025-05-01T12:00:00Z",
    },

    outputFields: [
      { key: "id", label: "Booking ID", type: "integer" },
      { key: "status", label: "Status" },
      { key: "scheduled_at", label: "Scheduled At", type: "datetime" },
      { key: "duration_hours", label: "Duration (Hours)", type: "number" },
      { key: "notes", label: "Notes" },
      { key: "property__id", label: "Property ID", type: "integer" },
      { key: "property__address", label: "Property Address" },
      { key: "property__city", label: "Property City" },
      { key: "property__state", label: "Property State" },
      { key: "property__zip", label: "Property Zip" },
      { key: "created_at", label: "Created At", type: "datetime" },
    ],
  },
};
