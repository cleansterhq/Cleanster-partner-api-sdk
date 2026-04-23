"use strict";

const BASE_URL = "https://api.cleanster.com";

const cancelBooking = async (z, bundle) => {
  const payload = {};
  if (bundle.inputData.reason) {
    payload.reason = bundle.inputData.reason;
  }

  const response = await z.request({
    url: `${BASE_URL}/v1/bookings/${bundle.inputData.booking_id}/cancel`,
    method: "POST",
    body: payload,
  });

  return response.data?.data || response.data || { id: bundle.inputData.booking_id, status: "cancelled" };
};

module.exports = {
  key: "cancel_booking",
  noun: "Booking",

  display: {
    label: "Cancel Booking",
    description: "Cancels an existing booking in Cleanster.",
    important: false,
  },

  operation: {
    perform: cancelBooking,

    inputFields: [
      {
        key: "booking_id",
        label: "Booking ID",
        helpText: "The ID of the booking to cancel.",
        required: true,
        type: "integer",
      },
      {
        key: "reason",
        label: "Cancellation Reason",
        helpText: "Optional reason for the cancellation.",
        required: false,
        type: "text",
      },
    ],

    sample: {
      id: 16459,
      status: "cancelled",
      cancelled_at: "2025-05-02T10:30:00Z",
      cancellation_reason: "Customer rescheduled",
    },

    outputFields: [
      { key: "id", label: "Booking ID", type: "integer" },
      { key: "status", label: "Status" },
      { key: "cancelled_at", label: "Cancelled At", type: "datetime" },
      { key: "cancellation_reason", label: "Cancellation Reason" },
    ],
  },
};
