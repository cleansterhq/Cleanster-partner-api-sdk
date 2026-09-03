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
      attr_reader :id, :is_default, :disabled, :title, :type, :total_tasks,
                  :total_sub_tasks, :tasks, :name, :items, :raw

      def initialize(data)
        @raw   = data
        @id              = data["id"]
        @is_default      = data["is_default"]
        @disabled        = data["disabled"]
        @title           = data["title"]
        @type            = data["type"]
        @total_tasks     = data["totalTasks"]
        @total_sub_tasks = data["totalSubTasks"]
        @tasks           = (data["tasks"] || []).map { |task| task.is_a?(Hash) ? ChecklistTask.new(task) : task }
        @name  = data.fetch("name", @title)
        @items = (data["items"] || []).map do |item|
          item.is_a?(Hash) ? ChecklistItem.new(item) : item
        end
      end

      def to_s
        "Checklist(id=#{id}, title=#{title.inspect}, tasks=#{tasks.length})"
      end
    end

    class ChecklistSubtask
      attr_reader :description, :flag_request_photos, :photos, :raw

      def initialize(data)
        @raw = data
        @description = data["description"]
        @flag_request_photos = data["flag_request_photos"]
        @photos = data["photos"] || []
      end
    end

    class ChecklistTask
      attr_reader :image_name, :title, :total_subtasks, :subtasks, :raw

      def initialize(data)
        @raw = data
        @image_name = data["image_name"]
        @title = data["title"]
        @total_subtasks = data["totalSubtasks"]
        @subtasks = (data["subtasks"] || []).map { |subtask| subtask.is_a?(Hash) ? ChecklistSubtask.new(subtask) : subtask }
      end
    end
  end
end
