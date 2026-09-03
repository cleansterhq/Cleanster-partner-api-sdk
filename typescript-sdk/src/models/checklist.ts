/**
 * Checklist models - cleaning task lists.
 */

export interface ChecklistSubtask {
  description: string;
  flag_request_photos: boolean;
  photos: string[];
  /** Additional API fields are preserved for forward compatibility. */
  [key: string]: unknown;
}

export interface ChecklistTask {
  image_name?: string;
  title: string;
  /** Returned by list/get; omit when creating or updating. */
  totalSubtasks?: number;
  subtasks: ChecklistSubtask[];
  [key: string]: unknown;
}

export interface Checklist {
  id: number;
  is_default: boolean;
  disabled: boolean;
  title: string;
  type: string;
  totalTasks: number;
  totalSubTasks: number;
  tasks: ChecklistTask[];
  /** @deprecated Legacy response field. */
  name?: string;
  /** @deprecated Legacy response field. */
  items?: ChecklistItem[];
  /** Additional API fields are preserved for forward compatibility. */
  [key: string]: unknown;
}

/** Request body for creating or updating a checklist. */
export interface CreateChecklistRequest {
  title: string;
  tasks: ChecklistTask[];
}

/** @deprecated Use ChecklistTask. */
export interface ChecklistItem {
  id: number;
  description: string;
  isCompleted: boolean;
  imageUrl?: string;
  [key: string]: unknown;
}
