"use strict";

const BASE_URL = "https://api.cleanster.com";

const createBooking = async (z, bundle) => {
  const payload = {
    property_id: parseInt(bundle.inputData.property_id, 10),
    scheduled_at: bundle.inputData.scheduled_at,
    duration_hours: parseFloat(bundle.inputData.duration_hours),
    service_type: bundle.inputData.service_type,
  };

  if (bundle.inputData.notes) {
    payload.notes = bundle.inputData.notes;
  }
  if (bundle.inputData.cleaner_id) {
    payload.cleaner_id = parseInt(bundle.inputData.cleaner_id, 10);
  }
  if (bundle.inputData.checklist_id) {
    payload.checklist_id = parseInt(bundle.inputData.checklist_id, 10);
  }

  const response = await z.request({
    url: `${BASE_URL}/v1/bookings`,
    method: "POST",
    body: payload,
  });

  return response.data?.data || response.data || {};
};

module.exports = {
  key: "create_booking",
  noun: "Booking",

  display: {
    label: "Create Booking",
    description: "Creates a new cleaning booking in Cleanster.",
    important: true,
  },

  operation: {
    perform: createBooking,

    inputFields: [
      {
        key: "property_id",
        label: "Property",
        helpText: "The ID of the property to clean.",
        required: true,
        type: "integer",
        dynamic: "new_property.id.name",
        search: "find_property.id",
      },
      {
        key: "scheduled_at",
        label: "Scheduled Date & Time",
        helpText: "When the cleaning should start (ISO 8601 format).",
        required: true,
        type: "datetime",
      },
      {
        key: "duration_hours",
        label: "Duration (Hours)",
        helpText: "How many hours the cleaning should take (e.g. 2.5).",
        required: true,
        type: "number",
      },
      {
        key: "service_type",
        label: "Service Type",
        helpText: "The type of cleaning service.",
        required: true,
        choices: [
          { label: "Standard Clean", value: "standard" },
          { label: "Deep Clean", value: "deep" },
          { label: "Move-In/Move-Out", value: "move_in_out" },
          { label: "Turnover", value: "turnover" },
          { label: "Post-Construction", value: "post_construction" },
        ],
      },
      {
        key: "notes",
        label: "Notes",
        helpText: "Any special instructions for the cleaner.",
        required: false,
        type: "text",
      },
      {
        key: "cleaner_id",
        label: "Assign Cleaner (optional)",
        helpText: "Assign a specific cleaner to this booking.",
        required: false,
        type: "integer",
        dynamic: "find_cleaner.id.name",
        search: "find_cleaner.id",
      },
      {
        key: "checklist_id",
        label: "Checklist (optional)",
        helpText: "Attach a checklist to this booking.",
        required: false,
        type: "integer",
      },
    ],

    sample: {
      id: 16460,
      status: "scheduled",
      scheduled_at: "2025-05-15T10:00:00Z",
      duration_hours: 3,
      service_type: "standard",
      notes: "",
      property: { id: 42, address: "123 Main St", city: "Atlanta" },
      created_at: "2025-05-01T09:00:00Z",
    },

    outputFields: [
      { key: "id", label: "Booking ID", type: "integer" },
      { key: "status", label: "Status" },
      { key: "scheduled_at", label: "Scheduled At", type: "datetime" },
      { key: "duration_hours", label: "Duration (Hours)", type: "number" },
      { key: "service_type", label: "Service Type" },
      { key: "property__id", label: "Property ID", type: "integer" },
      { key: "property__address", label: "Property Address" },
      { key: "created_at", label: "Created At", type: "datetime" },
    ],
  },
};
