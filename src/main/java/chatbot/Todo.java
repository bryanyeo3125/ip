package chatbot;

/**
 * Represents a todo task without any date or time information.
 */
public class Todo extends Task {

    /**
     * Constructs a todo task with a description.
     *
     * @param description Description of the todo task.
     */
    public Todo(String description) {
        super(description);
    }

    /**
     * Returns the type icon for a todo task.
     *
     * @return "T"
     */
    @Override
    protected String getTypeIcon() {
        return "T";
    }
}