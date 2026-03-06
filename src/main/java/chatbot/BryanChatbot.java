package chatbot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BryanChatbot {

    private static final String LINE = "----------------------------------------";
    private static final int MAX_TASKS = 100;

    private static final String COMMAND_BYE = "bye";
    private static final String COMMAND_LIST = "list";
    private static final String COMMAND_MARK = "mark";
    private static final String COMMAND_UNMARK = "unmark";
    private static final String COMMAND_DELETE = "delete";
    private static final String COMMAND_TODO = "todo";
    private static final String COMMAND_DEADLINE = "deadline";
    private static final String COMMAND_EVENT = "event";

    private static final Path DATA_DIR = Paths.get("data");
    private static final Path DATA_FILE = DATA_DIR.resolve("chatbot.txt");

    private static final List<Task> tasks = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        printGreeting();

        try {
            loadTasks();
        } catch (ChatbotException e) {
            printError(e.getMessage());
        }

        while (true) {
            String input = scanner.nextLine().trim();

            if (isBye(input)) {
                printGoodbye();
                break;
            }

            try {
                handleCommand(input);
            } catch (ChatbotException e) {
                printError(e.getMessage());
            }
        }

        scanner.close();
    }

    private static void handleCommand(String input) throws ChatbotException {
        if (isList(input)) {
            printList();
            return;
        }

        if (isCommand(input, COMMAND_MARK)) {
            int index = parseIndex(input);
            markTask(index);
            return;
        }

        if (isCommand(input, COMMAND_UNMARK)) {
            int index = parseIndex(input);
            unmarkTask(index);
            return;
        }

        if (isCommand(input, COMMAND_DELETE)) {
            int index = parseIndex(input);
            deleteTask(index);
            return;
        }

        if (isCommand(input, COMMAND_TODO)) {
            addTodo(input);
            return;
        }

        if (isCommand(input, COMMAND_DEADLINE)) {
            addDeadline(input);
            return;
        }

        if (isCommand(input, COMMAND_EVENT)) {
            addEvent(input);
            return;
        }

        throw new ChatbotException("I don't understand that command yet.");
    }

    private static void printGreeting() {
        printBlock(
                "Hello! I am bryan_chatbot.",
                "What can I do for you?"
        );
    }

    private static void printGoodbye() {
        printBlock("Bye. Hope to see you again soon!");
    }

    private static void printError(String message) {
        printBlock(message);
    }

    private static void printBlock(String... lines) {
        System.out.println(LINE);
        for (String line : lines) {
            System.out.println(line);
        }
        System.out.println(LINE);
    }

    private static boolean isBye(String input) {
        return input.equals(COMMAND_BYE);
    }

    private static boolean isList(String input) {
        return input.equals(COMMAND_LIST);
    }

    private static boolean isCommand(String input, String command) {
        return input.equals(command) || input.startsWith(command + " ");
    }

    private static void addTodo(String input) throws ChatbotException {
        String description = extractAfterKeyword(input, COMMAND_TODO);
        if (description.isEmpty()) {
            throw new ChatbotException("The description of a todo cannot be empty.");
        }

        addTask(new Todo(description));
    }

    private static void addDeadline(String input) throws ChatbotException {
        String remainder = extractAfterKeyword(input, COMMAND_DEADLINE);
        String[] parts = remainder.split(" /by ", 2);

        if (parts.length < 2) {
            throw new ChatbotException("Usage: deadline <description> /by <when>");
        }

        String description = parts[0].trim();
        String by = parts[1].trim();

        if (description.isEmpty() || by.isEmpty()) {
            throw new ChatbotException("Usage: deadline <description> /by <when>");
        }

        addTask(new Deadline(description, by));
    }

    private static void addEvent(String input) throws ChatbotException {
        String remainder = extractAfterKeyword(input, COMMAND_EVENT);
        String[] fromParts = remainder.split(" /from ", 2);

        if (fromParts.length < 2) {
            throw new ChatbotException("Usage: event <description> /from <start> /to <end>");
        }

        String description = fromParts[0].trim();
        String[] toParts = fromParts[1].split(" /to ", 2);

        if (toParts.length < 2) {
            throw new ChatbotException("Usage: event <description> /from <start> /to <end>");
        }

        String from = toParts[0].trim();
        String to = toParts[1].trim();

        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            throw new ChatbotException("Usage: event <description> /from <start> /to <end>");
        }

        addTask(new Event(description, from, to));
    }

    private static void addTask(Task task) throws ChatbotException {
        if (tasks.size() >= MAX_TASKS) {
            throw new ChatbotException("Task limit reached. Cannot add more tasks.");
        }

        tasks.add(task);
        saveTasks();

        printBlock(
                "Got it. I've added this task:",
                "  " + task,
                "Now you have " + tasks.size() + " tasks in the list."
        );
    }

    private static void printList() {
        System.out.println(LINE);
        System.out.println("Here are the tasks in your list:");

        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }

        System.out.println(LINE);
    }

    private static void markTask(int index) throws ChatbotException {
        Task task = tasks.get(index);
        task.markDone();
        saveTasks();

        printBlock(
                "Nice! I've marked this task as done:",
                "  " + task
        );
    }

    private static void unmarkTask(int index) throws ChatbotException {
        Task task = tasks.get(index);
        task.markNotDone();
        saveTasks();

        printBlock(
                "OK, I've marked this task as not done yet:",
                "  " + task
        );
    }

    private static void deleteTask(int index) throws ChatbotException {
        Task removedTask = tasks.remove(index);
        saveTasks();

        printBlock(
                "Noted. I've removed this task:",
                "  " + removedTask,
                "Now you have " + tasks.size() + " tasks in the list."
        );
    }

    private static int parseIndex(String input) throws ChatbotException {
        String[] parts = input.trim().split("\\s+");
        if (parts.length < 2) {
            throw new ChatbotException("Please specify a task number.");
        }

        int oneBasedIndex;
        try {
            oneBasedIndex = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            throw new ChatbotException("Please enter a valid task number.");
        }

        int zeroBasedIndex = oneBasedIndex - 1;
        if (!isValidIndex(zeroBasedIndex)) {
            throw new ChatbotException("That task number is out of range.");
        }

        return zeroBasedIndex;
    }

    private static boolean isValidIndex(int index) {
        return index >= 0 && index < tasks.size();
    }

    private static String extractAfterKeyword(String input, String keyword) {
        String prefix = keyword + " ";
        if (!input.startsWith(prefix)) {
            return "";
        }
        return input.substring(prefix.length()).trim();
    }

    private static void loadTasks() throws ChatbotException {
        if (!Files.exists(DATA_FILE)) {
            return;
        }

        try (BufferedReader reader = Files.newBufferedReader(DATA_FILE)) {
            String line;
            while ((line = reader.readLine()) != null) {
                Task task = parseTaskLine(line);
                if (task != null) {
                    tasks.add(task);
                }
            }
        } catch (IOException e) {
            throw new ChatbotException("Warning: Could not load saved tasks.");
        }
    }

    private static void saveTasks() throws ChatbotException {
        try {
            Files.createDirectories(DATA_DIR);

            try (BufferedWriter writer = Files.newBufferedWriter(DATA_FILE)) {
                for (Task task : tasks) {
                    writer.write(task.toStorageString());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new ChatbotException("Warning: Could not save tasks to disk.");
        }
    }

    private static Task parseTaskLine(String line) {
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
            task = new Deadline(description, fields[3]);
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

class ChatbotException extends Exception {
    public ChatbotException(String message) {
        super(message);
    }
}

abstract class Task {

    private final String description;
    private boolean isDone;

    protected Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public void markDone() {
        this.isDone = true;
    }

    public void markNotDone() {
        this.isDone = false;
    }

    protected boolean isDone() {
        return this.isDone;
    }

    protected String getStatusIcon() {
        return this.isDone ? "X" : " ";
    }

    protected String getDescription() {
        return this.description;
    }

    protected abstract String getTypeIcon();

    protected String getDetails() {
        return "";
    }

    public String toStorageString() {
        return getTypeIcon() + " | " + (isDone() ? "1" : "0") + " | " + getDescription();
    }

    @Override
    public String toString() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] " + getDescription() + getDetails();
    }
}

class Todo extends Task {

    public Todo(String description) {
        super(description);
    }

    @Override
    protected String getTypeIcon() {
        return "T";
    }
}

class Deadline extends Task {

    private final String by;

    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    @Override
    protected String getTypeIcon() {
        return "D";
    }

    @Override
    protected String getDetails() {
        return " (by: " + by + ")";
    }

    @Override
    public String toStorageString() {
        return super.toStorageString() + " | " + by;
    }
}

class Event extends Task {

    private final String from;
    private final String to;

    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    protected String getTypeIcon() {
        return "E";
    }

    @Override
    protected String getDetails() {
        return " (from: " + from + " to: " + to + ")";
    }

    @Override
    public String toStorageString() {
        return super.toStorageString() + " | " + from + " | " + to;
    }
}