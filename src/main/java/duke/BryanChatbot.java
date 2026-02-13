package duke;

import java.util.Scanner;

public class BryanChatbot {

    private static final String LINE = "----------------------------------------";
    private static final int MAX_TASKS = 100;

    private static final String COMMAND_BYE = "bye";
    private static final String COMMAND_LIST = "list";
    private static final String COMMAND_MARK = "mark";
    private static final String COMMAND_UNMARK = "unmark";
    private static final String COMMAND_TODO = "todo";
    private static final String COMMAND_DEADLINE = "deadline";
    private static final String COMMAND_EVENT = "event";

    private static final Task[] TASKS = new Task[MAX_TASKS];
    private static int taskCount = 0;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        printGreeting();

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
        return input.startsWith(COMMAND_MARK);
    }

    private static boolean isUnmarkCommand(String input) {
        return input.startsWith(COMMAND_UNMARK);
    }

    private static boolean isTodoCommand(String input) {
        return input.startsWith(COMMAND_TODO);
    }

    private static boolean isDeadlineCommand(String input) {
        return input.startsWith(COMMAND_DEADLINE);
    }

    private static boolean isEventCommand(String input) {
        return input.startsWith(COMMAND_EVENT);
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
        if (taskCount >= MAX_TASKS) {
            throw new ChatbotException("duke.Task limit reached. Cannot add more tasks.");
        }

        TASKS[taskCount] = task;
        taskCount++;

        System.out.println(LINE);
        System.out.println("Got it. I've added this task:");
        System.out.println("  " + task);
        System.out.println("Now you have " + taskCount + " tasks in the list.");
        System.out.println(LINE);
    }

    private static void printList() {
        System.out.println(LINE);
        System.out.println("Here are the tasks in your list:");

        for (int i = 0; i < taskCount; i++) {
            System.out.println((i + 1) + "." + TASKS[i]);
        }

        System.out.println(LINE);
    }

    private static void markTask(int index) {
        Task task = TASKS[index];
        task.markDone();

        System.out.println(LINE);
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  " + task);
        System.out.println(LINE);
    }

    private static void unmarkTask(int index) {
        Task task = TASKS[index];
        task.markNotDone();

        System.out.println(LINE);
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("  " + task);
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
        return index >= 0 && index < taskCount;
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
}

/* ---------- A-Exceptions ---------- */

class ChatbotException extends Exception {
    public ChatbotException(String message) {
        super(message);
    }
}

/* ---------- A-Inheritance duke.Task Hierarchy ---------- */

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
}
