"use strict";

const BASE_URL = "https://api.cleanster.com";

const updateTask = async (z, bundle) => {
  const response = await z.request({
    url: `${BASE_URL}/v1/bookings/${bundle.inputData.booking_id}/tasks`,
    method: "POST",
    body: {
      tasks: [
        {
          id: parseInt(bundle.inputData.task_id, 10),
          quantity: parseInt(bundle.inputData.quantity, 10),
        },
      ],
    },
  });

  return response.data?.data || response.data || {
    booking_id: bundle.inputData.booking_id,
    task_id: bundle.inputData.task_id,
    quantity: bundle.inputData.quantity,
  };
};

module.exports = {
  key: "update_task",
  noun: "Booking",

  display: {
    label: "Update Task Quantity",
    description: "Updates the quantity of a task on an existing booking.",
    important: false,
  },

  operation: {
    perform: updateTask,

    inputFields: [
      {
        key: "booking_id",
        label: "Booking ID",
        helpText: "The ID of the booking to update.",
        required: true,
        type: "integer",
      },
      {
        key: "task_id",
        label: "Task ID",
        helpText: "The ID of the task to update.",
        required: true,
        type: "integer",
      },
      {
        key: "quantity",
        label: "Quantity",
        helpText: "The new quantity for this task.",
        required: true,
        type: "integer",
      },
    ],

    sample: {
      id: 16459,
      status: "scheduled",
      tasks: [{ id: 42, quantity: 3 }],
    },

    outputFields: [
      { key: "id", label: "Booking ID", type: "integer" },
      { key: "status", label: "Booking Status" },
    ],
  },
};
