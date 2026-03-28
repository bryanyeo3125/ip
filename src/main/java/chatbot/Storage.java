package chatbot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Handles loading and saving tasks to and from disk.
 */
public class Storage {

    private final Path dataDir;
    private final Path dataFile;

    /**
     * Constructs a Storage object with the given file path.
     *
     * @param filePath Path to the save file (e.g., "data/chatbot.txt").
     */
    public Storage(String filePath) {
        this.dataFile = Paths.get(filePath);
        this.dataDir = this.dataFile.getParent();
    }

    /**
     * Loads tasks from the save file into the given task list.
     *
     * @param tasks The task list to load tasks into.
     * @throws ChatbotException If reading the file fails.
     */
    public void load(TaskList tasks) throws ChatbotException {
        if (!Files.exists(dataFile)) {
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(dataFile)) {
            String line;
            while ((line = reader.readLine()) != null) {
                Task task = parseTaskLine(line);
                if (task != null) {
                    tasks.addTask(task);
                }
            }
        } catch (IOException e) {
            throw new ChatbotException("Warning: Could not load saved tasks.");
        }
    }

    /**
     * Saves all tasks in the given task list to the save file.
     *
     * @param tasks The task list to save.
     * @throws ChatbotException If writing the file fails.
     */
    public void save(TaskList tasks) throws ChatbotException {
        try {
            Files.createDirectories(dataDir);

            try (BufferedWriter writer = Files.newBufferedWriter(dataFile)) {
                List<Task> allTasks = tasks.getAllTasks();
                for (Task task : allTasks) {
                    writer.write(task.toStorageString());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new ChatbotException("Warning: Could not save tasks to disk.");
        }
    }

    /**
     * Converts one line from the save file into a Task object.
     *
     * @param line One line from the save file.
     * @return Parsed task, or null if the line is invalid.
     */
    private Task parseTaskLine(String line) {
        String trimmedLine = line.trim();
        if (trimmedLine.isEmpty()) {
            return null;
        }

        String[] fields = trimmedLine.split("\\s*\\|\\s*");
        if (fields.length < 3) {
            return null;
        }

        String taskType = fields[0];
        boolean isTaskDone = "1".equals(fields[1]);
        String description = fields[2];

        Task task;
        switch (taskType) {
        case "T":
            task = new Todo(description);
            break;
        case "D":
            if (fields.length < 4) {
                return null;
            }
            try {
                task = new Deadline(description, LocalDate.parse(fields[3]));
            } catch (DateTimeParseException e) {
                return null;
            }
            break;
        case "E":
            if (fields.length < 5) {
                return null;
            }
            task = new Event(description, fields[3], fields[4]);
            break;
        default:
            return null;
        }

        if (isTaskDone) {
            task.markDone();
        }

        return task;
    }
}
