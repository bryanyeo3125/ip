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
            String input = scanner.nextLine();

            if (isBye(input)) {
                printGoodbye();
                break;
            }

            handleCommand(input);
        }

        scanner.close();
    }

    private static void handleCommand(String input) {
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

        printUnknownCommand();
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

    private static boolean isBye(String input) {
        return input.equals(COMMAND_BYE);
    }

    private static boolean isList(String input) {
        return input.equals(COMMAND_LIST);
    }

    private static boolean isMarkCommand(String input) {
        return input.startsWith(COMMAND_MARK + " ");
    }

    private static boolean isUnmarkCommand(String input) {
        return input.startsWith(COMMAND_UNMARK + " ");
    }

    private static boolean isTodoCommand(String input) {
        return input.startsWith(COMMAND_TODO + " ");
    }

    private static boolean isDeadlineCommand(String input) {
        return input.startsWith(COMMAND_DEADLINE + " ");
    }

    private static boolean isEventCommand(String input) {
        return input.startsWith(COMMAND_EVENT + " ");
    }

    private static void addTodo(String input) {
        String description = input.substring((COMMAND_TODO + " ").length()).trim();
        if (description.isEmpty()) {
            printInvalidTodo();
            return;
        }

        addTask(new Todo(description));
    }

    private static void addDeadline(String input) {
        String remainder = input.substring((COMMAND_DEADLINE + " ").length());
        String[] parts = remainder.split(" /by ", 2);

        if (parts.length < 2) {
            printInvalidDeadline();
            return;
        }

        String description = parts[0].trim();
        String by = parts[1].trim();

        if (description.isEmpty() || by.isEmpty()) {
            printInvalidDeadline();
            return;
        }

        addTask(new Deadline(description, by));
    }

    private static void addEvent(String input) {
        String remainder = input.substring((COMMAND_EVENT + " ").length());

        String[] fromSplit = remainder.split(" /from ", 2);
        if (fromSplit.length < 2) {
            printInvalidEvent();
            return;
        }

        String description = fromSplit[0].trim();
        String[] toSplit = fromSplit[1].split(" /to ", 2);

        if (toSplit.length < 2) {
            printInvalidEvent();
            return;
        }

        String from = toSplit[0].trim();
        String to = toSplit[1].trim();

        if (description.isEmpty() || from.isEmpty() || to.isEmpty()) {
            printInvalidEvent();
            return;
        }

        addTask(new Event(description, from, to));
    }

    private static void addTask(Task task) {
        if (taskCount >= MAX_TASKS) {
            printTaskLimitReached();
            return;
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
        if (!isValidIndex(index)) {
            printInvalidIndex();
            return;
        }

        Task task = TASKS[index];
        task.markDone();

        System.out.println(LINE);
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  " + task);
        System.out.println(LINE);
    }

    private static void unmarkTask(int index) {
        if (!isValidIndex(index)) {
            printInvalidIndex();
            return;
        }

        Task task = TASKS[index];
        task.markNotDone();

        System.out.println(LINE);
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("  " + task);
        System.out.println(LINE);
    }

    private static int parseIndex(String input) {
        String[] parts = input.split(" ");
        int oneBasedIndex = Integer.parseInt(parts[1]);
        return oneBasedIndex - 1;
    }

    private static boolean isValidIndex(int index) {
        return index >= 0 && index < taskCount;
    }

    private static void printInvalidIndex() {
        System.out.println(LINE);
        System.out.println("Invalid task number.");
        System.out.println(LINE);
    }

    private static void printTaskLimitReached() {
        System.out.println(LINE);
        System.out.println("Task limit reached. Cannot add more tasks.");
        System.out.println(LINE);
    }

    private static void printUnknownCommand() {
        System.out.println(LINE);
        System.out.println("I don't understand that command yet.");
        System.out.println(LINE);
    }

    private static void printInvalidTodo() {
        System.out.println(LINE);
        System.out.println("The description of a todo cannot be empty.");
        System.out.println(LINE);
    }

    private static void printInvalidDeadline() {
        System.out.println(LINE);
        System.out.println("Usage: deadline <description> /by <when>");
        System.out.println(LINE);
    }

    private static void printInvalidEvent() {
        System.out.println(LINE);
        System.out.println("Usage: event <description> /from <start> /to <end>");
        System.out.println(LINE);
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
