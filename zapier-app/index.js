"use strict";

const { version } = require("./package.json");
const { authentication, addAuthHeader } = require("./authentication");

const newBooking = require("./triggers/new_booking");
const bookingStatusChanged = require("./triggers/booking_status_changed");
const newProperty = require("./triggers/new_property");

const createBooking = require("./creates/create_booking");
const cancelBooking = require("./creates/cancel_booking");
const rescheduleBooking = require("./creates/reschedule_booking");
const createProperty = require("./creates/create_property");
const assignCleaner = require("./creates/assign_cleaner");
const sendMessage = require("./creates/send_message");

const findBooking = require("./searches/find_booking");
const findProperty = require("./searches/find_property");
const findCleaner = require("./searches/find_cleaner");
const getServices = require("./searches/get_services");

module.exports = {
  version,
  platformVersion: require("zapier-platform-core").version,

  authentication,

  beforeRequest: [addAuthHeader],

  triggers: {
    [newBooking.key]: newBooking,
    [bookingStatusChanged.key]: bookingStatusChanged,
    [newProperty.key]: newProperty,
  },

  creates: {
    [createBooking.key]: createBooking,
    [cancelBooking.key]: cancelBooking,
    [rescheduleBooking.key]: rescheduleBooking,
    [createProperty.key]: createProperty,
    [assignCleaner.key]: assignCleaner,
    [sendMessage.key]: sendMessage,
  },

  searches: {
    [findBooking.key]: findBooking,
    [findProperty.key]: findProperty,
    [findCleaner.key]: findCleaner,
    [getServices.key]: getServices,
  },

  searchOrCreates: {
    find_or_create_booking: {
      key: "find_or_create_booking",
      display: {
        label: "Find or Create Booking",
        description:
          "Finds an existing booking or creates a new one if none match.",
      },
      search: "find_booking",
      create: "create_booking",
    },
    find_or_create_property: {
      key: "find_or_create_property",
      display: {
        label: "Find or Create Property",
        description:
          "Finds an existing property or creates a new one if none match.",
      },
      search: "find_property",
      create: "create_property",
    },
  },
};
