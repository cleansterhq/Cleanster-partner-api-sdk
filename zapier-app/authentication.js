"use strict";

const BASE_URL = "https://api.cleanster.com";

const authentication = {
  type: "custom",

  fields: [
    {
      key: "apiKey",
      label: "API Key",
      helpText:
        "Your Cleanster Partner API key. Find it in your Cleanster dashboard under Settings → API.",
      required: true,
      type: "string",
    },
  ],

  connectionLabel: "{{bundle.authData.apiKey}}",

  test: {
    url: `${BASE_URL}/v1/services`,
    method: "GET",
    headers: {
      Authorization: "Bearer {{bundle.authData.apiKey}}",
      "Content-Type": "application/json",
    },
  },
};

const addAuthHeader = (request, z, bundle) => {
  if (bundle.authData.apiKey) {
    request.headers = request.headers || {};
    request.headers["Authorization"] = `Bearer ${bundle.authData.apiKey}`;
    request.headers["Content-Type"] = "application/json";
  }
  return request;
};

module.exports = { authentication, addAuthHeader };
