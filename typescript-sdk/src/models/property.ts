/**
 * Property model - represents a physical location where cleanings take place.
 */
export interface Property {
  id: number;
  name: string;
  /** Optional custom display nickname, serialized by the API as `nickName`. */
  nickName?: string;
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
  /** Optional custom display nickname. */
  nickName?: string;
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

/**
 * Request body for adding one or more iCal calendar links to a property.
 * Each URL must be a live, publicly fetchable .ics feed - the API validates
 * the feed content, not just the URL shape.
 */
export interface ICalRequest {
  calendarLinks: string[];
}

/** A single calendar link attached to a property, as returned by getICalLink. */
export interface CalendarLink {
  id: number;
  calendarLink: string;
}

/** Request body for removing calendar links from a property, by numeric link ID. */
export interface DeleteICalLinkRequest {
  ids: number[];
}
