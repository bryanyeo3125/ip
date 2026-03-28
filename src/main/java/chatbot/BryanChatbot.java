package chatbot;

import java.util.List;
import java.util.Scanner;

/**
 * BryanChatbot is a command-line chatbot that allows users to manage todos, deadlines, and events.
 */
public class BryanChatbot {

    private static final int MAX_TASKS = 100;
    private static final String DATA_FILE_PATH = "data/chatbot.txt";

    private final TaskList tasks;
    private final Ui ui;
    private final Storage storage;
    private final Parser parser;

    /**
     * Constructs a BryanChatbot with the given data file path.
     *
     * @param filePath Path to the data file used for saving and loading tasks.
     */
    public BryanChatbot(String filePath) {
        this.tasks = new TaskList();
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        this.parser = new Parser();
    }

    /**
     * Starts the chatbot application.
     *
     * @param args Command-line arguments passed into the application.
     */
    public static void main(String[] args) {
        new BryanChatbot(DATA_FILE_PATH).run();
    }

    /**
     * Runs the main loop of the chatbot.
     */
    public void run() {
        Scanner scanner = new Scanner(System.in);

        ui.printGreeting();

        try {
            storage.load(tasks);
        } catch (ChatbotException e) {
            ui.printError(e.getMessage());
        }

        while (true) {
            String input = scanner.nextLine().trim();

            if (parser.isBye(input)) {
                ui.printGoodbye();
                break;
            }

            try {
                handleCommand(input);
            } catch (ChatbotException e) {
                ui.printError(e.getMessage());
            }
        }

        scanner.close();
    }

    /**
     * Processes a user command and performs the correct action.
     *
     * @param input Full command entered by the user.
     * @throws ChatbotException If the command format is invalid or unsupported.
     */
    private void handleCommand(String input) throws ChatbotException {
        if (parser.isList(input)) {
            ui.printTaskList(tasks.getAllTasks());
            return;
        }

        if (parser.isCommand(input, parser.getMarkCommand())) {
            int index = parser.parseIndex(input, tasks.size());
            Task task = tasks.markTask(index);
            storage.save(tasks);
            ui.printTaskMarked(task);
            return;
        }

        if (parser.isCommand(input, parser.getUnmarkCommand())) {
            int index = parser.parseIndex(input, tasks.size());
            Task task = tasks.unmarkTask(index);
            storage.save(tasks);
            ui.printTaskUnmarked(task);
            return;
        }

        if (parser.isCommand(input, parser.getDeleteCommand())) {
            int index = parser.parseIndex(input, tasks.size());
            Task removedTask = tasks.deleteTask(index);
            storage.save(tasks);
            ui.printTaskDeleted(removedTask, tasks.size());
            return;
        }

        if (parser.isCommand(input, parser.getTodoCommand())) {
            addTask(parser.parseTodo(input));
            return;
        }

        if (parser.isCommand(input, parser.getDeadlineCommand())) {
            addTask(parser.parseDeadline(input));
            return;
        }

        if (parser.isCommand(input, parser.getEventCommand())) {
            addTask(parser.parseEvent(input));
            return;
        }

        if (parser.isCommand(input, parser.getFindCommand())) {
            String keyword = parser.parseKeyword(input);
            List<Task> matchingTasks = tasks.findTasks(keyword);
            ui.printMatchingTasks(matchingTasks);
            return;
        }

        throw new ChatbotException("I don't understand that command yet.");
    }

    /**
     * Adds a task to the list, saves, and prints a confirmation.
     *
     * @param task Task to be added.
     * @throws ChatbotException If the task limit is reached or saving fails.
     */
    private void addTask(Task task) throws ChatbotException {
        if (tasks.size() >= MAX_TASKS) {
            throw new ChatbotException("Task limit reached. Cannot add more tasks.");
        }

        tasks.addTask(task);
        storage.save(tasks);
        ui.printTaskAdded(task, tasks.size());
    }
}
