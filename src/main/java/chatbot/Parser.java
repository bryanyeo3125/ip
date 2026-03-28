package chatbot;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Handles parsing of user input into commands and task data.
 */
public class Parser {

    private static final String COMMAND_BYE = "bye";
    private static final String COMMAND_LIST = "list";
    private static final String COMMAND_MARK = "mark";
    private static final String COMMAND_UNMARK = "unmark";
    private static final String COMMAND_DELETE = "delete";
    private static final String COMMAND_TODO = "todo";
    private static final String COMMAND_DEADLINE = "deadline";
    private static final String COMMAND_EVENT = "event";
    private static final String COMMAND_FIND = "find";

    /**
     * Returns true if the input is the bye command.
     *
     * @param input User input.
     * @return True if the user wants to exit.
     */
    public boolean isBye(String input) {
        return input.equals(COMMAND_BYE);
    }

    /**
     * Returns true if the input is the list command.
     *
     * @param input User input.
     * @return True if the user wants to list all tasks.
     */
    public boolean isList(String input) {
        return input.equals(COMMAND_LIST);
    }

    /**
     * Returns true if the input starts with the given command keyword.
     *
     * @param input Full user input.
     * @param command Command keyword to check.
     * @return True if the input matches the command.
     */
    public boolean isCommand(String input, String command) {
        return input.equals(command) || input.startsWith(command + " ");
    }

    /**
     * Returns the mark command keyword.
     *
     * @return The mark command string.
     */
    public String getMarkCommand() {
        return COMMAND_MARK;
    }

    /**
     * Returns the unmark command keyword.
     *
     * @return The unmark command string.
     */
    public String getUnmarkCommand() {
        return COMMAND_UNMARK;
    }

    /**
     * Returns the delete command keyword.
     *
     * @return The delete command string.
     */
    public String getDeleteCommand() {
        return COMMAND_DELETE;
    }

    /**
     * Returns the todo command keyword.
     *
     * @return The todo command string.
     */
    public String getTodoCommand() {
        return COMMAND_TODO;
    }

    /**
     * Returns the deadline command keyword.
     *
     * @return The deadline command string.
     */
    public String getDeadlineCommand() {
        return COMMAND_DEADLINE;
    }

    /**
     * Returns the event command keyword.
     *
     * @return The event command string.
     */
    public String getEventCommand() {
        return COMMAND_EVENT;
    }

    /**
     * Returns the find command keyword.
     *
     * @return The find command string.
     */
    public String getFindCommand() {
        return COMMAND_FIND;
    }

    /**
     * Parses the task index from a user command.
     *
     * @param input Full user input.
     * @param taskCount Total number of tasks currently in the list.
     * @return Zero-based task index.
     * @throws ChatbotException If the index is missing, invalid, or out of range.
     */
    public int parseIndex(String input, int taskCount) throws ChatbotException {
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
        if (zeroBasedIndex < 0 || zeroBasedIndex >= taskCount) {
            throw new ChatbotException("That task number is out of range.");
        }

        return zeroBasedIndex;
    }

    /**
     * Parses a Todo task from the user input.
     *
     * @param input Full user input.
     * @return A new Todo task.
     * @throws ChatbotException If the description is empty.
     */
    public Todo parseTodo(String input) throws ChatbotException {
        String description = extractAfterKeyword(input, COMMAND_TODO);
        if (description.isEmpty()) {
            throw new ChatbotException("The description of a todo cannot be empty.");
        }
        return new Todo(description);
    }

    /**
     * Parses a Deadline task from the user input.
     *
     * @param input Full user input.
     * @return A new Deadline task.
     * @throws ChatbotException If the format is invalid or date cannot be parsed.
     */
    public Deadline parseDeadline(String input) throws ChatbotException {
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
            return new Deadline(description, byDate);
        } catch (DateTimeParseException e) {
            throw new ChatbotException("Please enter the deadline in yyyy-MM-dd format.");
        }
    }

    /**
     * Parses an Event task from the user input.
     *
     * @param input Full user input.
     * @return A new Event task.
     * @throws ChatbotException If the format is invalid.
     */
    public Event parseEvent(String input) throws ChatbotException {
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

        return new Event(description, from, to);
    }

    /**
     * Parses a search keyword from the user input.
     *
     * @param input Full user input.
     * @return The search keyword.
     * @throws ChatbotException If the keyword is empty.
     */
    public String parseKeyword(String input) throws ChatbotException {
        String keyword = extractAfterKeyword(input, COMMAND_FIND);
        if (keyword.isEmpty()) {
            throw new ChatbotException("The keyword for find cannot be empty.");
        }
        return keyword;
    }

    /**
     * Extracts the text after a command keyword.
     *
     * @param input Full user input.
     * @param keyword Command keyword.
     * @return The text after the keyword, or an empty string if absent.
     */
    private String extractAfterKeyword(String input, String keyword) {
        String prefix = keyword + " ";
        if (!input.startsWith(prefix)) {
            return "";
        }
        return input.substring(prefix.length()).trim();
    }
}
