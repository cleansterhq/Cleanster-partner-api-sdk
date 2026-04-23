"use strict";

const BASE_URL = "https://api.cleanster.com";

const findBooking = async (z, bundle) => {
  if (bundle.inputData.booking_id) {
    const response = await z.request({
      url: `${BASE_URL}/v1/bookings/${bundle.inputData.booking_id}`,
      method: "GET",
    });
    const booking = response.data?.data || response.data;
    return booking ? [booking] : [];
  }

  const params = { page: 1, per_page: 5 };
  if (bundle.inputData.status) params.status = bundle.inputData.status;
  if (bundle.inputData.property_id)
    params.property_id = bundle.inputData.property_id;

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
  key: "find_booking",
  noun: "Booking",

  display: {
    label: "Find Booking",
    description: "Finds an existing booking by ID or filters.",
    important: true,
  },

  operation: {
    perform: findBooking,

    inputFields: [
      {
        key: "booking_id",
        label: "Booking ID",
        helpText: "Look up a specific booking by its ID. Takes priority over other filters.",
        required: false,
        type: "integer",
      },
      {
        key: "status",
        label: "Filter by Status",
        helpText: "Find bookings with a specific status.",
        required: false,
        choices: [
          { label: "Scheduled", value: "scheduled" },
          { label: "In Progress", value: "in_progress" },
          { label: "Completed", value: "completed" },
          { label: "Cancelled", value: "cancelled" },
          { label: "Pending", value: "pending" },
        ],
      },
      {
        key: "property_id",
        label: "Filter by Property",
        helpText: "Find bookings for a specific property.",
        required: false,
        type: "integer",
        dynamic: "new_property.id.name",
      },
    ],

    sample: {
      id: 16459,
      status: "scheduled",
      scheduled_at: "2025-05-10T09:00:00Z",
      duration_hours: 3,
      property: { id: 42, address: "123 Main St", city: "Atlanta" },
      created_at: "2025-05-01T12:00:00Z",
    },

    outputFields: [
      { key: "id", label: "Booking ID", type: "integer" },
      { key: "status", label: "Status" },
      { key: "scheduled_at", label: "Scheduled At", type: "datetime" },
      { key: "duration_hours", label: "Duration (Hours)", type: "number" },
      { key: "property__id", label: "Property ID", type: "integer" },
      { key: "property__address", label: "Property Address" },
      { key: "property__city", label: "Property City" },
      { key: "created_at", label: "Created At", type: "datetime" },
    ],
  },
};
