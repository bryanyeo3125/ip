package chatbot;

abstract class Task {

    private final String description;
    private boolean isDone;

    protected Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public void markDone() {
        this.isDone = true;
    }

    public void markNotDone() {
        this.isDone = false;
    }

    protected boolean isDone() {
        return this.isDone;
    }

    protected String getStatusIcon() {
        return this.isDone ? "X" : " ";
    }

    protected String getDescription() {
        return this.description;
    }

    protected abstract String getTypeIcon();

    protected String getDetails() {
        return "";
    }

    public String toStorageString() {
        return getTypeIcon() + " | " + (isDone() ? "1" : "0") + " | " + getDescription();
    }

    @Override
    public String toString() {
        return "[" + getTypeIcon() + "][" + getStatusIcon() + "] "
                + getDescription() + getDetails();
    }
}