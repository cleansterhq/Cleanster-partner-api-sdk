"use strict";

const BASE_URL = "https://api.cleanster.com";

const findCleaner = async (z, bundle) => {
  if (bundle.inputData.cleaner_id) {
    const response = await z.request({
      url: `${BASE_URL}/v1/cleaners/${bundle.inputData.cleaner_id}`,
      method: "GET",
    });
    const cleaner = response.data?.data || response.data;
    return cleaner ? [cleaner] : [];
  }

  const params = { page: 1, per_page: 10 };
  if (bundle.inputData.search) params.search = bundle.inputData.search;
  if (bundle.inputData.status) params.status = bundle.inputData.status;

  const response = await z.request({
    url: `${BASE_URL}/v1/cleaners`,
    method: "GET",
    params,
  });

  const data = response.data || {};
  const cleaners = data.data || data.cleaners || data || [];
  return Array.isArray(cleaners) ? cleaners : [];
};

module.exports = {
  key: "find_cleaner",
  noun: "Cleaner",

  display: {
    label: "Find Cleaner",
    description: "Finds a cleaner by ID or searches by name.",
    important: false,
  },

  operation: {
    perform: findCleaner,

    inputFields: [
      {
        key: "cleaner_id",
        label: "Cleaner ID",
        helpText:
          "Look up a specific cleaner by their ID. Takes priority over search.",
        required: false,
        type: "integer",
      },
      {
        key: "search",
        label: "Search by Name or Email",
        helpText: "Partial match against cleaner name or email.",
        required: false,
        type: "string",
      },
      {
        key: "status",
        label: "Filter by Status",
        helpText: "Only return cleaners with this status.",
        required: false,
        choices: [
          { label: "Active", value: "active" },
          { label: "Inactive", value: "inactive" },
          { label: "Pending", value: "pending" },
        ],
      },
    ],

    sample: {
      id: 789,
      name: "Jane Smith",
      email: "jane@example.com",
      phone: "+14045550100",
      status: "active",
      rating: 4.9,
      total_jobs: 142,
      created_at: "2024-01-15T08:00:00Z",
    },

    outputFields: [
      { key: "id", label: "Cleaner ID", type: "integer" },
      { key: "name", label: "Name" },
      { key: "email", label: "Email" },
      { key: "phone", label: "Phone" },
      { key: "status", label: "Status" },
      { key: "rating", label: "Rating", type: "number" },
      { key: "total_jobs", label: "Total Jobs Completed", type: "integer" },
      { key: "created_at", label: "Member Since", type: "datetime" },
    ],
  },
};
