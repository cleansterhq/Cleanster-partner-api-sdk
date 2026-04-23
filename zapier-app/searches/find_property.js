"use strict";

const BASE_URL = "https://api.cleanster.com";

const findProperty = async (z, bundle) => {
  if (bundle.inputData.property_id) {
    const response = await z.request({
      url: `${BASE_URL}/v1/properties/${bundle.inputData.property_id}`,
      method: "GET",
    });
    const property = response.data?.data || response.data;
    return property ? [property] : [];
  }

  const params = { page: 1, per_page: 10 };
  if (bundle.inputData.search) params.search = bundle.inputData.search;

  const response = await z.request({
    url: `${BASE_URL}/v1/properties`,
    method: "GET",
    params,
  });

  const data = response.data || {};
  const properties = data.data || data.properties || data || [];
  return Array.isArray(properties) ? properties : [];
};

module.exports = {
  key: "find_property",
  noun: "Property",

  display: {
    label: "Find Property",
    description:
      "Finds an existing property by ID or searches by address/name.",
    important: true,
  },

  operation: {
    perform: findProperty,

    inputFields: [
      {
        key: "property_id",
        label: "Property ID",
        helpText:
          "Look up a specific property by its ID. Takes priority over search.",
        required: false,
        type: "integer",
      },
      {
        key: "search",
        label: "Search by Name or Address",
        helpText: "Partial match against property name or street address.",
        required: false,
        type: "string",
      },
    ],

    sample: {
      id: 42,
      name: "Beachfront Villa",
      address: "123 Main St",
      city: "Atlanta",
      state: "GA",
      zip: "30301",
      bedrooms: 3,
      bathrooms: 2,
      square_feet: 1800,
      created_at: "2025-04-01T08:00:00Z",
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
