"use strict";

const BASE_URL = "https://api.cleanster.com";

const getUpdatedBookings = async (z, bundle) => {
  const params = {
    page: 1,
    per_page: 10,
    sort_by: "updated_at",
    sort_order: "desc",
  };

  if (bundle.inputData.status) {
    params.status = bundle.inputData.status;
  }

  const response = await z.request({
    url: `${BASE_URL}/v1/bookings`,
    method: "GET",
    params,
  });

  const data = response.data || {};
  const bookings = data.data || data.bookings || data || [];
  return Array.isArray(bookings) ? bookings : [];
};

module.exports = {
  key: "booking_status_changed",
  noun: "Booking",

  display: {
    label: "Booking Status Changed",
    description:
      "Triggers when a booking status changes (e.g. scheduled → in_progress → completed).",
    important: true,
  },

  operation: {
    type: "polling",
    perform: getUpdatedBookings,

    inputFields: [
      {
        key: "status",
        label: "Filter by Status",
        helpText:
          "Only trigger for bookings with this status. Leave blank for all status changes.",
        required: false,
        choices: [
          { label: "Scheduled", value: "scheduled" },
          { label: "In Progress", value: "in_progress" },
          { label: "Completed", value: "completed" },
          { label: "Cancelled", value: "cancelled" },
          { label: "Pending", value: "pending" },
        ],
      },
    ],

    sample: {
      id: 16459,
      status: "completed",
      scheduled_at: "2025-05-10T09:00:00Z",
      duration_hours: 3,
      property: {
        id: 42,
        address: "123 Main St",
        city: "Atlanta",
        state: "GA",
        zip: "30301",
      },
      updated_at: "2025-05-10T12:15:00Z",
    },

    outputFields: [
      { key: "id", label: "Booking ID", type: "integer" },
      { key: "status", label: "Status" },
      { key: "scheduled_at", label: "Scheduled At", type: "datetime" },
      { key: "duration_hours", label: "Duration (Hours)", type: "number" },
      { key: "property__id", label: "Property ID", type: "integer" },
      { key: "property__address", label: "Property Address" },
      { key: "updated_at", label: "Updated At", type: "datetime" },
    ],
  },
};
