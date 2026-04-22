/**
 * Checklist models — cleaning task lists.
 */

export interface ChecklistItem {
  id: number;
  description: string;
  isCompleted: boolean;
  imageUrl?: string;
}

export interface Checklist {
  id: number;
  name: string;
  items: ChecklistItem[];
}

/** Request body for creating or updating a checklist. */
export interface CreateChecklistRequest {
  name: string;
  items: string[];
}
