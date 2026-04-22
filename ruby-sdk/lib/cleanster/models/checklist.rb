module Cleanster
  module Models
    # A single task item within a checklist.
    class ChecklistItem
      attr_reader :id, :description, :is_completed, :image_url, :raw

      def initialize(data)
        @raw          = data
        @id           = data["id"]
        @description  = data["description"]
        @is_completed = data["isCompleted"]
        @image_url    = data["imageUrl"]
      end

      def to_s
        "ChecklistItem(id=#{id}, description=#{description.inspect})"
      end
    end

    # A named collection of cleaning tasks.
    class Checklist
      attr_reader :id, :name, :items, :raw

      def initialize(data)
        @raw   = data
        @id    = data["id"]
        @name  = data["name"]
        @items = (data["items"] || []).map do |item|
          item.is_a?(Hash) ? ChecklistItem.new(item) : item
        end
      end

      def to_s
        "Checklist(id=#{id}, name=#{name.inspect}, items=#{items.length})"
      end
    end
  end
end
