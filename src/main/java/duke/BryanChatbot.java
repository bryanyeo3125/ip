package duke;

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
    private static final Path DATA_FILE = DATA_DIR.resolve("duke.txt");

    private static final List<Task> tasks = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        printGreeting();
        loadTasks();

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

        if (isMarkCommand(input)) {
            int index = parseIndex(input);
            markTask(index);
            return;
        }

        if (isUnmarkCommand(input)) {
            int index = parseIndex(input);
            unmarkTask(index);
            return;
        }

        if (isDeleteCommand(input)) {
            int index = parseIndex(input);
            deleteTask(index);
            return;
        }

        if (isTodoCommand(input)) {
            addTodo(input);
            return;
        }

        if (isDeadlineCommand(input)) {
            addDeadline(input);
            return;
        }

        if (isEventCommand(input)) {
            addEvent(input);
            return;
        }

        throw new ChatbotException("I don't understand that command yet.");
    }

    private static void printGreeting() {
        System.out.println(LINE);
        System.out.println("Hello! I am bryan_chatbot.");
        System.out.println("What can I do for you?");
        System.out.println(LINE);
    }

    private static void printGoodbye() {
        System.out.println(LINE);
        System.out.println("Bye. Hope to see you again soon!");
        System.out.println(LINE);
    }

    private static void printError(String message) {
        System.out.println(LINE);
        System.out.println(message);
        System.out.println(LINE);
    }

    private static boolean isBye(String input) {
        return input.equals(COMMAND_BYE);
    }

    private static boolean isList(String input) {
        return input.equals(COMMAND_LIST);
    }

    private static boolean isMarkCommand(String input) {
        return input.equals(COMMAND_MARK) || input.startsWith(COMMAND_MARK + " ");
    }

    private static boolean isUnmarkCommand(String input) {
        return input.equals(COMMAND_UNMARK) || input.startsWith(COMMAND_UNMARK + " ");
    }

    private static boolean isDeleteCommand(String input) {
        return input.equals(COMMAND_DELETE) || input.startsWith(COMMAND_DELETE + " ");
    }

    private static boolean isTodoCommand(String input) {
        return input.equals(COMMAND_TODO) || input.startsWith(COMMAND_TODO + " ");
    }

    private static boolean isDeadlineCommand(String input) {
        return input.equals(COMMAND_DEADLINE) || input.startsWith(COMMAND_DEADLINE + " ");
    }

    private static boolean isEventCommand(String input) {
        return input.equals(COMMAND_EVENT) || input.startsWith(COMMAND_EVENT + " ");
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

        String[] fromSplit = remainder.split(" /from ", 2);
        if (fromSplit.length < 2) {
            throw new ChatbotException("Usage: event <description> /from <start> /to <end>");
        }

        String description = fromSplit[0].trim();
        String[] toSplit = fromSplit[1].split(" /to ", 2);

        if (toSplit.length < 2) {
            throw new ChatbotException("Usage: event <description> /from <start> /to <end>");
        }

        String from = toSplit[0].trim();
        String to = toSplit[1].trim();

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

        System.out.println(LINE);
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
        System.out.println(LINE);
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

        System.out.println(LINE);
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  " + task);
        System.out.println(LINE);
    }

    private static void unmarkTask(int index) throws ChatbotException {
        Task task = tasks.get(index);
        task.markNotDone();
        saveTasks();

        System.out.println(LINE);
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("  " + task);
        System.out.println(LINE);
    }

    private static void deleteTask(int index) throws ChatbotException {
        Task removed = tasks.remove(index);
        saveTasks();

        System.out.println(LINE);
        System.out.println("Noted. I've removed this task:");
        System.out.println("  " + removed);
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
        System.out.println(LINE);
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

        int index = oneBasedIndex - 1;
        if (!isValidIndex(index)) {
            throw new ChatbotException("That task number is out of range.");
        }
        return index;
    }

    private static boolean isValidIndex(int index) {
        return index >= 0 && index < tasks.size();
    }

    private static String extractAfterKeyword(String input, String keyword) {
        if (input.equals(keyword)) {
            return "";
        }
        String prefix = keyword + " ";
        if (input.startsWith(prefix)) {
            return input.substring(prefix.length()).trim();
        }
        return "";
    }

    private static void loadTasks() {
        if (!Files.exists(DATA_FILE)) {
            return; // first run: no data file yet
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
            printError("Warning: Could not load saved tasks.");
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
        String trimmed = line.trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        String[] parts = trimmed.split("\\s*\\|\\s*");
        if (parts.length < 3) {
            return null;
        }

        String type = parts[0];
        boolean done = "1".equals(parts[1]);
        String description = parts[2];

        Task task;
        switch (type) {
        case "T":
            task = new Todo(description);
            break;
        case "D":
            if (parts.length < 4) {
                return null;
            }
            task = new Deadline(description, parts[3]);
            break;
        case "E":
            if (parts.length < 5) {
                return null;
            }
            task = new Event(description, parts[3], parts[4]);
            break;
        default:
            return null;
        }

        if (done) {
            task.markDone();
        }

        return task;
    }
}

/* ---------- A-Exceptions ---------- */

class ChatbotException extends Exception {
    public ChatbotException(String message) {
        super(message);
    }
}

/* ---------- A-Inheritance Task Hierarchy ---------- */

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