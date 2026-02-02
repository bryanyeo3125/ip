import java.util.Scanner;

public class BryanChatbot {

    private static final String LINE = "----------------------------------------";
    private static final int MAX_TASKS = 100;

    private static final String COMMAND_BYE = "bye";
    private static final String COMMAND_LIST = "list";
    private static final String COMMAND_MARK = "mark";
    private static final String COMMAND_UNMARK = "unmark";

    private static final String[] TASKS = new String[MAX_TASKS];
    private static final boolean[] IS_DONE = new boolean[MAX_TASKS];
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

        addTask(input);
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

    private static void addTask(String task) {
        if (taskCount >= MAX_TASKS) {
            printTaskLimitReached();
            return;
        }

        TASKS[taskCount] = task;
        IS_DONE[taskCount] = false;
        taskCount++;

        System.out.println(LINE);
        System.out.println("added: " + task);
        System.out.println(LINE);
    }

    private static void printList() {
        System.out.println(LINE);
        System.out.println("Here are the tasks in your list:");

        for (int i = 0; i < taskCount; i++) {
            System.out.println(formatTaskLine(i));
        }

        System.out.println(LINE);
    }

    private static String formatTaskLine(int index) {
        int displayNumber = index + 1;
        String status = IS_DONE[index] ? "X" : " ";
        return displayNumber + ".[" + status + "] " + TASKS[index];
    }

    private static void markTask(int index) {
        if (!isValidIndex(index)) {
            printInvalidIndex();
            return;
        }

        IS_DONE[index] = true;

        System.out.println(LINE);
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("[X] " + TASKS[index]);
        System.out.println(LINE);
    }

    private static void unmarkTask(int index) {
        if (!isValidIndex(index)) {
            printInvalidIndex();
            return;
        }

        IS_DONE[index] = false;

        System.out.println(LINE);
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("[ ] " + TASKS[index]);
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
}