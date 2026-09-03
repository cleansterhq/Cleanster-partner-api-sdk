/**
 * Checklist models - cleaning task lists.
 */

export interface ChecklistItem {
  id: number;
  description: string;
  isCompleted: boolean;
  imageUrl?: string;
  /** Additional API fields are preserved for forward compatibility. */
  [key: string]: unknown;
}

export interface Checklist {
  id: number;
  name: string;
  items: ChecklistItem[];
  /** Additional API fields are preserved for forward compatibility. */
  [key: string]: unknown;
}

/** Request body for creating or updating a checklist. */
export interface CreateChecklistRequest {
  name: string;
  items: string[];
}
