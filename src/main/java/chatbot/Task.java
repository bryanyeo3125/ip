package chatbot;

/**
 * Represents a generic task in the chatbot.
 */
public abstract class Task {

    private final String description;
    private boolean isDone;

    /**
     * Constructs a task with a description.
     *
     * @param description Description of the task.
     */
    protected Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Marks the task as done.
     */
    public void markDone() {
        this.isDone = true;
    }

    /**
     * Marks this task as not done.
     */
    public void markNotDone() {
        this.isDone = false;
    }

    /**
     * Returns true if the task is marked as done.
     *
     * @return True if the task is done.
     */
    protected boolean isDone() {
        return this.isDone;
    }

    /**
     * Returns the status icon of the task.
     *
     * @return "X" if done, otherwise a blank space.
     */
    protected String getStatusIcon() {
        return this.isDone ? "X" : " ";
    }

    /**
     * Returns the description of the task.
     *
     * @return Task description.
     */
    protected String getDescription() {
        return this.description;
    }

    /**
     * Returns the type icon of the task.
     *
     * @return Task type icon.
     */
    protected abstract String getTypeIcon();

    /**
     * Returns extra details to be shown together with the task description.
     *
     * @return extra task details.
     */
    protected String getDetails() {
        return "";
    }

    /**
     * Returns a string representation to save to file.
     *
     * @return Storage string of the task.
     */
    public String toStorageString() {
        return getTypeIcon() + " | " + (isDone() ? "1" : "0") + " | " + getDescription();
    }

    /**
     * Checks whether the task description contains the given keyword, case-insensitive
     *
     * @param keyword: The keyword to search for in the task description.
     * @return true if the task description contains the keyword, otherwise false.
     */
    public boolean containsKeyword(String keyword) {
        return getDescription().toLowerCase().contains(keyword.toLowerCase());
    }

    /**
     * Returns a user-friendly string representation of the task.
     *
     * @return Task string.
     */
    @Override
    public String toString() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] "
                + getDescription() + getDetails();
    }
}