module Cleanster
  module Api
    # ChecklistsApi — create and manage cleaning task lists.
    class ChecklistsApi
      def initialize(http)
        @http = http
      end

      # Return all checklists for your partner account.
      # @return [Models::ApiResponse]
      def list_checklists
        raw = @http.get("/v1/checklist")
        Models::ApiResponse.from_hash(raw)
      end

      # Get a specific checklist and all its task items.
      #
      # @param checklist_id [Integer]
      # @return [Models::ApiResponse<Models::Checklist>]
      def get_checklist(checklist_id)
        raw = @http.get("/v1/checklist/#{checklist_id}")
        Models::ApiResponse.from_hash(raw, model_class: Models::Checklist)
      end

      # Create a new checklist.
      #
      # @param name  [String]
      # @param items [Array<String>] Task description strings.
      # @return [Models::ApiResponse<Models::Checklist>]
      def create_checklist(name:, items:)
        raw = @http.post("/v1/checklist", body: { name: name, items: items })
        Models::ApiResponse.from_hash(raw, model_class: Models::Checklist)
      end

      # Replace the name and task items of an existing checklist.
      #
      # @param checklist_id [Integer]
      # @param name         [String]
      # @param items        [Array<String>]
      # @return [Models::ApiResponse<Models::Checklist>]
      def update_checklist(checklist_id, name:, items:)
        raw = @http.put("/v1/checklist/#{checklist_id}", body: { name: name, items: items })
        Models::ApiResponse.from_hash(raw, model_class: Models::Checklist)
      end

      # Permanently delete a checklist.
      #
      # @param checklist_id [Integer]
      # @return [Models::ApiResponse]
      def delete_checklist(checklist_id)
        raw = @http.delete("/v1/checklist/#{checklist_id}")
        Models::ApiResponse.from_hash(raw)
      end

      # Upload an image via multipart/form-data.
      #
      # Sends the image as +multipart/form-data+ in the +file+ form field.
      #
      # @param image_bytes  [String]        Raw binary image content.
      # @param file_name    [String]        File name for the part (e.g. "photo.jpg").
      # @return [Models::ApiResponse]
      def upload_checklist_image(image_bytes, file_name = "image.jpg")
        raw = @http.post_multipart("/v1/checklist/upload-image", image_bytes, file_name)
        Models::ApiResponse.from_hash(raw)
      end
    end
  end
end
