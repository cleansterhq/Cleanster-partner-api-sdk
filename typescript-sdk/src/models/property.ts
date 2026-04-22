/**
 * Property model — represents a physical location where cleanings take place.
 */
export interface Property {
  id: number;
  name: string;
  address: string;
  city: string;
  country: string;
  roomCount: number;
  bathroomCount: number;
  serviceId: number;
  isEnabled?: boolean;
}

/** Request body for creating or updating a property. */
export interface CreatePropertyRequest {
  name: string;
  address: string;
  city: string;
  country: string;
  roomCount: number;
  bathroomCount: number;
  serviceId: number;
  [key: string]: unknown; // allow additional fields
}

/** Request body for enabling or disabling a property. */
export interface EnableDisablePropertyRequest {
  enabled: boolean;
}

/** Request body for assigning a cleaner to a property. */
export interface AssignCleanerToPropertyRequest {
  cleanerId: number;
}

/** Request body for adding an iCal link. */
export interface ICalRequest {
  icalLink: string;
}
