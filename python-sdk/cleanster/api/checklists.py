"""Checklists API — create and manage cleaning task lists."""

from typing import Any, Dict, List

from ..http_client import HttpClient
from ..models.checklist import Checklist
from ..models.response import ApiResponse


class ChecklistsApi:
    """
    Checklist management: list, get, create, update, and delete checklists.
    Checklists define the tasks a cleaner should complete during a booking.
    """

    def __init__(self, http: HttpClient):
        self._http = http

    def list_checklists(self) -> ApiResponse:
        """Return all checklists for your partner account."""
        raw = self._http.get("/v1/checklist")
        return ApiResponse.from_dict(raw)

    def get_checklist(self, checklist_id: int) -> ApiResponse:
        """
        Get a specific checklist and all its task items.

        Args:
            checklist_id: The checklist ID.

        Returns:
            ApiResponse with data as a Checklist object.
        """
        raw = self._http.get(f"/v1/checklist/{checklist_id}")
        return ApiResponse.from_dict(raw, data_factory=Checklist)

    def create_checklist(self, name: str, items: List[str]) -> ApiResponse:
        """
        Create a new checklist.

        Args:
            name:  Display name for the checklist.
            items: List of task description strings.

        Returns:
            ApiResponse with data as the created Checklist object.
        """
        raw = self._http.post(
            "/v1/checklist",
            body={"name": name, "items": items},
        )
        return ApiResponse.from_dict(raw, data_factory=Checklist)

    def update_checklist(
        self, checklist_id: int, name: str, items: List[str]
    ) -> ApiResponse:
        """
        Replace the name and task items of an existing checklist.

        Args:
            checklist_id: The checklist ID.
            name:         New display name.
            items:        New list of task description strings.

        Returns:
            ApiResponse with data as the updated Checklist object.
        """
        raw = self._http.put(
            f"/v1/checklist/{checklist_id}",
            body={"name": name, "items": items},
        )
        return ApiResponse.from_dict(raw, data_factory=Checklist)

    def delete_checklist(self, checklist_id: int) -> ApiResponse:
        """
        Permanently delete a checklist.

        Args:
            checklist_id: The checklist ID.

        Returns:
            ApiResponse.
        """
        raw = self._http.delete(f"/v1/checklist/{checklist_id}")
        return ApiResponse.from_dict(raw)

    def upload_checklist_image(
        self, image_bytes: bytes, file_name: str = "image.jpg"
    ) -> ApiResponse:
        """
        Upload an image via multipart/form-data.

        Sends the image as ``multipart/form-data`` in the ``file`` form field.

        Args:
            image_bytes:  Raw bytes of the image to upload.
            file_name:    File name for the multipart part (e.g. ``"photo.jpg"``).

        Returns:
            ApiResponse.
        """
        raw = self._http.post_multipart(
            "/v1/checklist/upload-image",
            image_bytes,
            file_name,
        )
        return ApiResponse.from_dict(raw)
