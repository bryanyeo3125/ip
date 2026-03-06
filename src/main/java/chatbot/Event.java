package chatbot;

class Event extends Task {

    private final String from;
    private final String to;

    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    protected String getTypeIcon() {
        return "E";
    }

    @Override
    protected String getDetails() {
        return " (from: " + from + " to: " + to + ")";
    }

    @Override
    public String toStorageString() {
        return super.toStorageString() + " | " + from + " | " + to;
    }
}