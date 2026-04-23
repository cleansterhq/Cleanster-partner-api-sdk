"use strict";

const BASE_URL = "https://api.cleanster.com";

const assignCleaner = async (z, bundle) => {
  const response = await z.request({
    url: `${BASE_URL}/v1/bookings/${bundle.inputData.booking_id}/assign-cleaner`,
    method: "POST",
    body: {
      cleaner_id: parseInt(bundle.inputData.cleaner_id, 10),
    },
  });

  return response.data?.data || response.data || {
    booking_id: bundle.inputData.booking_id,
    cleaner_id: bundle.inputData.cleaner_id,
    assigned: true,
  };
};

module.exports = {
  key: "assign_cleaner",
  noun: "Booking",

  display: {
    label: "Assign Cleaner to Booking",
    description: "Assigns a cleaner to an existing booking in Cleanster.",
    important: false,
  },

  operation: {
    perform: assignCleaner,

    inputFields: [
      {
        key: "booking_id",
        label: "Booking ID",
        helpText: "The ID of the booking to assign a cleaner to.",
        required: true,
        type: "integer",
      },
      {
        key: "cleaner_id",
        label: "Cleaner",
        helpText: "The cleaner to assign to this booking.",
        required: true,
        type: "integer",
        dynamic: "find_cleaner.id.name",
        search: "find_cleaner.id",
      },
    ],

    sample: {
      id: 16459,
      status: "scheduled",
      cleaner: {
        id: 789,
        name: "Jane Smith",
        email: "jane@example.com",
        phone: "+14045550100",
      },
      assigned_at: "2025-05-01T10:00:00Z",
    },

    outputFields: [
      { key: "id", label: "Booking ID", type: "integer" },
      { key: "status", label: "Booking Status" },
      { key: "cleaner__id", label: "Cleaner ID", type: "integer" },
      { key: "cleaner__name", label: "Cleaner Name" },
      { key: "cleaner__email", label: "Cleaner Email" },
      { key: "assigned_at", label: "Assigned At", type: "datetime" },
    ],
  },
};
