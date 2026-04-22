/**
 * Generic API response wrapper returned by every SDK method.
 */
export interface ApiResponse<T = unknown> {
  /** HTTP-style status code (e.g., 200). */
  status: number;
  /** Human-readable status message (e.g., "OK"). */
  message: string;
  /** The typed response payload. */
  data: T;
}
