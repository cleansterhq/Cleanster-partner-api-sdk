/**
 * ChecklistsApi — create and manage cleaning task lists.
 */

import { HttpClient } from "../http-client";
import { Checklist, CreateChecklistRequest } from "../models/checklist";
import { ApiResponse } from "../models/response";

export class ChecklistsApi {
  constructor(private readonly http: HttpClient) {}

  /**
   * Return all checklists for your partner account.
   */
  listChecklists(): Promise<ApiResponse<unknown>> {
    return this.http.get("/v1/checklist");
  }

  /**
   * Get a specific checklist and all its task items.
   * @param checklistId  The checklist ID.
   */
  getChecklist(checklistId: number): Promise<ApiResponse<Checklist>> {
    return this.http.get<Checklist>(`/v1/checklist/${checklistId}`);
  }

  /**
   * Create a new checklist.
   * @param request  name (string) and items (string[]).
   * @returns ApiResponse with the created Checklist.
   */
  createChecklist(request: CreateChecklistRequest): Promise<ApiResponse<Checklist>> {
    return this.http.post<Checklist>("/v1/checklist", request);
  }

  /**
   * Replace the name and task items of an existing checklist.
   * @param checklistId  The checklist ID.
   * @param request      New name and task item strings.
   */
  updateChecklist(checklistId: number, request: CreateChecklistRequest): Promise<ApiResponse<Checklist>> {
    return this.http.put<Checklist>(`/v1/checklist/${checklistId}`, request);
  }

  /**
   * Permanently delete a checklist.
   * @param checklistId  The checklist ID.
   */
  deleteChecklist(checklistId: number): Promise<ApiResponse<unknown>> {
    return this.http.delete(`/v1/checklist/${checklistId}`);
  }

  /**
   * Upload an image for a checklist.
   *
   * Sends the image as multipart/form-data in the `image` form field.
   * @param checklistId  The checklist ID.
   * @param imageData    Raw image bytes (Uint8Array or Buffer).
   * @param fileName     File name for the multipart part (e.g. "photo.jpg").
   */
  uploadChecklistImage(
    checklistId: number,
    imageData: Uint8Array | Buffer,
    fileName: string,
  ): Promise<ApiResponse<unknown>> {
    return this.http.postMultipart(`/v1/checklist/${checklistId}/upload`, imageData, fileName);
  }
}
