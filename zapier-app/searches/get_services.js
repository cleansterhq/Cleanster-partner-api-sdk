"use strict";

const BASE_URL = "https://api.cleanster.com";

const getServices = async (z, bundle) => {
  const response = await z.request({
    url: `${BASE_URL}/v1/services`,
    method: "GET",
  });

  const data = response.data || {};
  const services = data.data || data.services || data || [];
  return Array.isArray(services) ? services : [];
};

module.exports = {
  key: "get_services",
  noun: "Service",

  display: {
    label: "Get Available Services",
    description:
      "Retrieves the list of available cleaning service types from Cleanster.",
    important: false,
  },

  operation: {
    perform: getServices,

    sample: {
      id: 1,
      name: "Standard Clean",
      slug: "standard",
      description: "Regular recurring cleaning service",
      base_duration_hours: 2,
      active: true,
    },

    outputFields: [
      { key: "id", label: "Service ID", type: "integer" },
      { key: "name", label: "Service Name" },
      { key: "slug", label: "Slug" },
      { key: "description", label: "Description" },
      { key: "base_duration_hours", label: "Base Duration (Hours)", type: "number" },
      { key: "active", label: "Active", type: "boolean" },
    ],
  },
};
