package chatbot;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a deadline task that must be completed by a deadline.
 * The deadline is stored as a {@link LocalDate} and displayed in as
 * (MMM-D-YYYY).
 */
public class Deadline extends Task {

    /** Formatter used to display the deadline date in a readable format. */
    private static final DateTimeFormatter OUTPUT_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy");

    /** The deadline by which the task should be completed. */
    private final LocalDate by;

    /**
     * Constructs a deadline task with a description and due date.
     *
     * @param description Description of the task.
     * @param by The deadline by which the task must be completed.
     */
    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    /**
     * Returns the type icon representing a deadline task.
     *
     * @return "D" to indicate a deadline task.
     */
    @Override
    protected String getTypeIcon() {
        return "D";
    }

    /**
     * Returns formatted details of the deadline for display.
     *
     * @return A formatted string containing the deadline date.
     */
    @Override
    protected String getDetails() {
        return " (by: " + by.format(OUTPUT_FORMAT) + ")";
    }

    /**
     * Returns the string representation of the task for storage in the save file.
     *
     * @return Storage string including the deadline date.
     */
    @Override
    public String toStorageString() {
        return super.toStorageString() + " | " + by;
    }
}