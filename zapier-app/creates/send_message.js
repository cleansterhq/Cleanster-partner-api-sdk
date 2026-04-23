"use strict";

const BASE_URL = "https://api.cleanster.com";

const sendMessage = async (z, bundle) => {
  const response = await z.request({
    url: `${BASE_URL}/v1/bookings/${bundle.inputData.booking_id}/chat`,
    method: "POST",
    body: {
      message: bundle.inputData.message,
    },
  });

  return response.data?.data || response.data || {
    booking_id: bundle.inputData.booking_id,
    message: bundle.inputData.message,
    sent_at: new Date().toISOString(),
  };
};

module.exports = {
  key: "send_message",
  noun: "Message",

  display: {
    label: "Send Chat Message",
    description:
      "Sends a chat message on a booking in Cleanster (visible to the cleaner).",
    important: false,
  },

  operation: {
    perform: sendMessage,

    inputFields: [
      {
        key: "booking_id",
        label: "Booking ID",
        helpText: "The ID of the booking to send a message on.",
        required: true,
        type: "integer",
      },
      {
        key: "message",
        label: "Message",
        helpText: "The message to send to the cleaner on this booking.",
        required: true,
        type: "text",
      },
    ],

    sample: {
      id: 1001,
      booking_id: 16459,
      message: "Please use the green cleaning products under the sink.",
      sender: "partner",
      sent_at: "2025-05-01T11:00:00Z",
    },

    outputFields: [
      { key: "id", label: "Message ID", type: "integer" },
      { key: "booking_id", label: "Booking ID", type: "integer" },
      { key: "message", label: "Message" },
      { key: "sender", label: "Sender" },
      { key: "sent_at", label: "Sent At", type: "datetime" },
    ],
  },
};
