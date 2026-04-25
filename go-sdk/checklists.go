package cleanster

import (
        "context"
        "fmt"
)

// ChecklistsService manages cleaning task lists.
type ChecklistsService struct {
        http *httpClient
}

// ListChecklists returns all checklists for the partner account.
func (s *ChecklistsService) ListChecklists(ctx context.Context) (APIResponse[[]Checklist], error) {
        raw, err := s.http.get(ctx, "/v1/checklist", nil)
        if err != nil {
                return APIResponse[[]Checklist]{}, err
        }
        return decode[[]Checklist](raw)
}

// GetChecklist returns a specific checklist including all its task items.
func (s *ChecklistsService) GetChecklist(ctx context.Context, checklistID int) (APIResponse[Checklist], error) {
        raw, err := s.http.get(ctx, fmt.Sprintf("/v1/checklist/%d", checklistID), nil)
        if err != nil {
                return APIResponse[Checklist]{}, err
        }
        return decode[Checklist](raw)
}

// CreateChecklist creates a new checklist with the given name and task items.
func (s *ChecklistsService) CreateChecklist(ctx context.Context, req CreateChecklistRequest) (APIResponse[Checklist], error) {
        raw, err := s.http.post(ctx, "/v1/checklist", req)
        if err != nil {
                return APIResponse[Checklist]{}, err
        }
        return decode[Checklist](raw)
}

// UpdateChecklist replaces the name and task items of an existing checklist.
func (s *ChecklistsService) UpdateChecklist(ctx context.Context, checklistID int, req CreateChecklistRequest) (APIResponse[Checklist], error) {
        raw, err := s.http.put(ctx, fmt.Sprintf("/v1/checklist/%d", checklistID), req)
        if err != nil {
                return APIResponse[Checklist]{}, err
        }
        return decode[Checklist](raw)
}

// DeleteChecklist permanently deletes a checklist.
func (s *ChecklistsService) DeleteChecklist(ctx context.Context, checklistID int) (APIResponse[map[string]interface{}], error) {
        raw, err := s.http.delete(ctx, fmt.Sprintf("/v1/checklist/%d", checklistID), nil)
        if err != nil {
                return APIResponse[map[string]interface{}]{}, err
        }
        return decode[map[string]interface{}](raw)
}

// UploadChecklistImage uploads an image via multipart/form-data.
// The image bytes are sent in the "file" form field.
func (s *ChecklistsService) UploadChecklistImage(ctx context.Context, imageData []byte, fileName string) (APIResponse[map[string]interface{}], error) {
        raw, err := s.http.postMultipart(ctx, "/v1/checklist/upload-image", imageData, fileName)
        if err != nil {
                return APIResponse[map[string]interface{}]{}, err
        }
        return decode[map[string]interface{}](raw)
}
