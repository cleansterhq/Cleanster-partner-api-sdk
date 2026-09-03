"""Checklists API - create and manage cleaning task lists."""

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

    def list_checklists(self) -> ApiResponse[List[Checklist]]:
        """Return all checklists for your partner account."""
        raw = self._http.get("/v1/checklist")
        return ApiResponse.from_dict(raw, data_factory=Checklist)

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

    def create_checklist(self, title: str, tasks: List[Dict[str, Any]]) -> ApiResponse[Checklist]:
        """
        Create a new checklist.

        Args:
            title: Checklist title.
            tasks: Checklist task objects using image_name, title, totalSubtasks,
                and subtasks. Subtasks use description, flag_request_photos, and photos.

        Returns:
            ApiResponse with data as the created Checklist object.
        """
        raw = self._http.post(
            "/v1/checklist",
            body={"title": title, "tasks": tasks},
        )
        return ApiResponse.from_dict(raw, data_factory=Checklist)

    def update_checklist(
        self, checklist_id: int, title: str, tasks: List[Dict[str, Any]]
    ) -> ApiResponse[Checklist]:
        """
        Replace the name and task items of an existing checklist.

        Args:
            checklist_id: The checklist ID.
            title:        New checklist title.
            tasks:        New checklist task objects.

        Returns:
            ApiResponse with data as the updated Checklist object.
        """
        raw = self._http.put(
            f"/v1/checklist/{checklist_id}",
            body={"title": title, "tasks": tasks},
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
