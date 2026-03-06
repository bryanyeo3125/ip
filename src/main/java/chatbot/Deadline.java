package chatbot;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

class Deadline extends Task {

    private static final DateTimeFormatter OUTPUT_FORMAT =
            DateTimeFormatter.ofPattern("MMM d yyyy");

    private final LocalDate by;

    public Deadline(String description, LocalDate by) {
        super(description);
        this.by = by;
    }

    @Override
    protected String getTypeIcon() {
        return "D";
    }

    @Override
    protected String getDetails() {
        return " (by: " + by.format(OUTPUT_FORMAT) + ")";
    }

    @Override
    public String toStorageString() {
        return super.toStorageString() + " | " + by;
    }
}