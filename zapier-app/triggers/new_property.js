"use strict";

const BASE_URL = "https://api.cleanster.com";

const getProperties = async (z, bundle) => {
  const response = await z.request({
    url: `${BASE_URL}/v1/properties`,
    method: "GET",
    params: {
      page: 1,
      per_page: 10,
      sort_by: "created_at",
      sort_order: "desc",
    },
  });

  const data = response.data || {};
  const properties = data.data || data.properties || data || [];
  return Array.isArray(properties) ? properties : [];
};

module.exports = {
  key: "new_property",
  noun: "Property",

  display: {
    label: "New Property",
    description: "Triggers when a new property is added to Cleanster.",
    important: false,
  },

  operation: {
    type: "polling",
    perform: getProperties,

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
      notes: "Gate code: 1234",
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
      { key: "notes", label: "Notes" },
      { key: "created_at", label: "Created At", type: "datetime" },
    ],
  },
};
