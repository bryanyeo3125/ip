package chatbot;

/**
 * Represents an event with a start and end time.
 */
class Event extends Task {

    private final String from;
    private final String to;

    /**
     * Constructs an event with a description, start time, and end time.
     *
     * @param description: Description of the event.
     * @param from: Start time of the event.
     * @param to: End time of the event.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns the type icon for an event task.
     *
     * @return "E"
     */
    @Override
    protected String getTypeIcon() {
        return "E";
    }

    /**
     * Returns the event details shown in the task output.
     *
     * @return Formatted event detail string.
     */
    @Override
    protected String getDetails() {
        return " (from: " + from + " to: " + to + ")";
    }

    /**
     * Returns the string representation of the task for storage.
     *
     * @return Storage string including event timing information.
     */
    @Override
    public String toStorageString() {
        return super.toStorageString() + " | " + from + " | " + to;
    }
}