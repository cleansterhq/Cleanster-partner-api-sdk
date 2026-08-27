"use strict";

const BASE_URL = "https://api.cleanster.com";

const updateSqft = async (z, bundle) => {
  const response = await z.request({
    url: `${BASE_URL}/v1/bookings/${bundle.inputData.booking_id}/sqft`,
    method: "POST",
    body: {
      totalSqFt: parseFloat(bundle.inputData.total_sq_ft),
    },
  });

  return response.data?.data || response.data || {
    booking_id: bundle.inputData.booking_id,
    totalSqFt: bundle.inputData.total_sq_ft,
  };
};

module.exports = {
  key: "update_sqft",
  noun: "Booking",

  display: {
    label: "Update Total Square Footage",
    description: "Updates the total square footage recorded for a booking.",
    important: false,
  },

  operation: {
    perform: updateSqft,

    inputFields: [
      {
        key: "booking_id",
        label: "Booking ID",
        helpText: "The ID of the booking to update.",
        required: true,
        type: "integer",
      },
      {
        key: "total_sq_ft",
        label: "Total Square Footage",
        helpText: "The new total square footage for the property being cleaned.",
        required: true,
        type: "number",
      },
    ],

    sample: {
      id: 16459,
      status: "scheduled",
      totalSqFt: 1500,
    },

    outputFields: [
      { key: "id", label: "Booking ID", type: "integer" },
      { key: "status", label: "Booking Status" },
      { key: "totalSqFt", label: "Total Square Footage", type: "number" },
    ],
  },
};
