package chatbot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

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

    private static final TaskList tasks = new TaskList();

    private static final String COMMAND_FIND = "find";

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

        if (isCommand(input, COMMAND_FIND)) {
            findTasks(input);
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
            throw new ChatbotException("Usage: deadline <description> /by <yyyy-MM-dd>");
        }

        String description = parts[0].trim();
        String byText = parts[1].trim();

        if (description.isEmpty() || byText.isEmpty()) {
            throw new ChatbotException("Usage: deadline <description> /by <yyyy-MM-dd>");
        }

        try {
            LocalDate byDate = LocalDate.parse(byText);
            addTask(new Deadline(description, byDate));
        } catch (DateTimeParseException e) {
            throw new ChatbotException("Please enter the deadline in yyyy-MM-dd format.");
        }
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

    private static void findTasks(String input) throws ChatbotException {
        String keyword = extractAfterKeyword(input, COMMAND_FIND);

        if (keyword.isEmpty()) {
            throw new ChatbotException("The keyword for find cannot be empty.");
        }

        List<Task> matchingTasks = tasks.findTasks(keyword);

        System.out.println(LINE);
        System.out.println("Here are the matching tasks in your list:");
        for (int i = 0; i < matchingTasks.size(); i++) {
            System.out.println((i + 1) + "." + matchingTasks.get(i));
        }
        System.out.println(LINE);
    }

    private static void addTask(Task task) throws ChatbotException {
        if (tasks.size() >= MAX_TASKS) {
            throw new ChatbotException("Task limit reached. Cannot add more tasks.");
        }

        tasks.addTask(task);
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
            System.out.println((i + 1) + "." + tasks.getTask(i));
        }

        System.out.println(LINE);
    }

    private static void markTask(int index) throws ChatbotException {
        Task task = tasks.markTask(index);
        saveTasks();

        printBlock(
                "Nice! I've marked this task as done:",
                "  " + task
        );
    }

    private static void unmarkTask(int index) throws ChatbotException {
        Task task = tasks.unmarkTask(index);
        saveTasks();

        printBlock(
                "OK, I've marked this task as not done yet:",
                "  " + task
        );
    }

    private static void deleteTask(int index) throws ChatbotException {
        Task removedTask = tasks.deleteTask(index);
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
                    tasks.addTask(task);
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