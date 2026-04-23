"use strict";

const BASE_URL = "https://api.cleanster.com";

const createProperty = async (z, bundle) => {
  const payload = {
    address: bundle.inputData.address,
    city: bundle.inputData.city,
    state: bundle.inputData.state,
    zip: bundle.inputData.zip,
  };

  if (bundle.inputData.name) payload.name = bundle.inputData.name;
  if (bundle.inputData.bedrooms)
    payload.bedrooms = parseInt(bundle.inputData.bedrooms, 10);
  if (bundle.inputData.bathrooms)
    payload.bathrooms = parseFloat(bundle.inputData.bathrooms);
  if (bundle.inputData.square_feet)
    payload.square_feet = parseInt(bundle.inputData.square_feet, 10);
  if (bundle.inputData.notes) payload.notes = bundle.inputData.notes;
  if (bundle.inputData.access_instructions)
    payload.access_instructions = bundle.inputData.access_instructions;

  const response = await z.request({
    url: `${BASE_URL}/v1/properties`,
    method: "POST",
    body: payload,
  });

  return response.data?.data || response.data || {};
};

module.exports = {
  key: "create_property",
  noun: "Property",

  display: {
    label: "Create Property",
    description: "Adds a new property to your Cleanster account.",
    important: true,
  },

  operation: {
    perform: createProperty,

    inputFields: [
      {
        key: "address",
        label: "Street Address",
        helpText: "The street address of the property.",
        required: true,
        type: "string",
      },
      {
        key: "city",
        label: "City",
        required: true,
        type: "string",
      },
      {
        key: "state",
        label: "State",
        helpText: "Two-letter state code (e.g. GA, CA, NY).",
        required: true,
        type: "string",
      },
      {
        key: "zip",
        label: "Zip Code",
        required: true,
        type: "string",
      },
      {
        key: "name",
        label: "Property Name",
        helpText: "A friendly name for this property (e.g. 'Beachfront Villa').",
        required: false,
        type: "string",
      },
      {
        key: "bedrooms",
        label: "Bedrooms",
        required: false,
        type: "integer",
      },
      {
        key: "bathrooms",
        label: "Bathrooms",
        required: false,
        type: "number",
      },
      {
        key: "square_feet",
        label: "Square Feet",
        required: false,
        type: "integer",
      },
      {
        key: "notes",
        label: "Notes",
        helpText: "Internal notes about this property.",
        required: false,
        type: "text",
      },
      {
        key: "access_instructions",
        label: "Access Instructions",
        helpText: "Instructions for how cleaners can access the property.",
        required: false,
        type: "text",
      },
    ],

    sample: {
      id: 43,
      name: "New Property",
      address: "456 Oak Ave",
      city: "Savannah",
      state: "GA",
      zip: "31401",
      bedrooms: 2,
      bathrooms: 1,
      square_feet: 900,
      created_at: "2025-05-01T09:00:00Z",
    },

    outputFields: [
      { key: "id", label: "Property ID", type: "integer" },
      { key: "name", label: "Property Name" },
      { key: "address", label: "Address" },
      { key: "city", label: "City" },
      { key: "state", label: "State" },
      { key: "zip", label: "Zip Code" },
      { key: "bedrooms", label: "Bedrooms", type: "integer" },
      { key: "bathrooms", label: "Bathrooms", type: "number" },
      { key: "square_feet", label: "Square Feet", type: "integer" },
      { key: "created_at", label: "Created At", type: "datetime" },
    ],
  },
};
