"use strict";

const BASE_URL = "https://api.cleanster.com";

const rescheduleBooking = async (z, bundle) => {
  const response = await z.request({
    url: `${BASE_URL}/v1/bookings/${bundle.inputData.booking_id}/reschedule`,
    method: "POST",
    body: {
      scheduled_at: bundle.inputData.new_scheduled_at,
      duration_hours: bundle.inputData.duration_hours
        ? parseFloat(bundle.inputData.duration_hours)
        : undefined,
    },
  });

  return response.data?.data || response.data || {};
};

module.exports = {
  key: "reschedule_booking",
  noun: "Booking",

  display: {
    label: "Reschedule Booking",
    description: "Reschedules an existing booking to a new date and time.",
    important: true,
  },

  operation: {
    perform: rescheduleBooking,

    inputFields: [
      {
        key: "booking_id",
        label: "Booking ID",
        helpText: "The ID of the booking to reschedule.",
        required: true,
        type: "integer",
      },
      {
        key: "new_scheduled_at",
        label: "New Date & Time",
        helpText: "The new scheduled date and time (ISO 8601 format).",
        required: true,
        type: "datetime",
      },
      {
        key: "duration_hours",
        label: "New Duration (Hours)",
        helpText: "Update the duration if needed. Leave blank to keep current.",
        required: false,
        type: "number",
      },
    ],

    sample: {
      id: 16459,
      status: "scheduled",
      scheduled_at: "2025-05-20T10:00:00Z",
      duration_hours: 3,
      rescheduled_at: "2025-05-01T14:00:00Z",
    },

    outputFields: [
      { key: "id", label: "Booking ID", type: "integer" },
      { key: "status", label: "Status" },
      { key: "scheduled_at", label: "New Scheduled At", type: "datetime" },
      { key: "duration_hours", label: "Duration (Hours)", type: "number" },
      { key: "rescheduled_at", label: "Rescheduled At", type: "datetime" },
    ],
  },
};
