package chatbot;

import java.util.List;

/**
 * Handles all user-facing input and output for the chatbot.
 */
public class Ui {

    private static final String LINE = "----------------------------------------";

    /**
     * Prints the chatbot startup message shown when the program starts.
     */
    public void printGreeting() {
        printBlock(
                "Hello! I am bryan_chatbot.",
                "What can I do for you?"
        );
    }

    /**
     * Prints the chatbot exit message shown before exiting.
     */
    public void printGoodbye() {
        printBlock("Bye. Hope to see you again soon!");
    }

    /**
     * Prints an error message.
     *
     * @param message Error message to be shown.
     */
    public void printError(String message) {
        printBlock(message);
    }

    /**
     * Prints a confirmation message after a task is added.
     *
     * @param task The task that was added.
     * @param totalTasks The total number of tasks in the list after adding.
     */
    public void printTaskAdded(Task task, int totalTasks) {
        printBlock(
                "Got it. I've added this task:",
                "  " + task,
                "Now you have " + totalTasks + " tasks in the list."
        );
    }

    /**
     * Prints a message to confirm a task is deleted.
     *
     * @param task The deleted task.
     * @param totalTasks The total number of tasks in the list after deletion.
     */
    public void printTaskDeleted(Task task, int totalTasks) {
        printBlock(
                "Noted. I've removed this task:",
                "  " + task,
                "Now you have " + totalTasks + " tasks in the list."
        );
    }

    /**
     * Prints a message to confirm a task is marked as done.
     *
     * @param task The task that was marked as done.
     */
    public void printTaskMarked(Task task) {
        printBlock(
                "Nice! I've marked this task as done:",
                "  " + task
        );
    }

    /**
     * Prints a message to confirm a task is marked as not done.
     *
     * @param task The task that was marked as not done.
     */
    public void printTaskUnmarked(Task task) {
        printBlock(
                "OK, I've marked this task as not done yet:",
                "  " + task
        );
    }

    /**
     * Prints all tasks in the task list.
     *
     * @param tasks The list of all tasks.
     */
    public void printTaskList(List<Task> tasks) {
        if (tasks.isEmpty()) {
            printBlock("Oops, you have nothing in the list!");
            return;
        }
        System.out.println(LINE);
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }
        System.out.println(LINE);
    }

    /**
     * Prints all tasks that match a search keyword.
     *
     * @param tasks The list of matching tasks.
     */
    public void printMatchingTasks(List<Task> tasks) {
        System.out.println(LINE);
        System.out.println("Here are the matching tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }
        System.out.println(LINE);
    }

    /**
     * Prints one or more lines enclosed between horizontal lines.
     *
     * @param lines Lines to be printed.
     */
    public void printBlock(String... lines) {
        System.out.println(LINE);
        for (String line : lines) {
            System.out.println(line);
        }
        System.out.println(LINE);
    }
}
