package chatbot;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a list of tasks managed by the chatbot.
 */
public class TaskList {
    private final List<Task> tasks;

    /**
     * Constructs an empty list.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Adds a task to the list.
     *
     * @param task: Task to be added.
     */
    public void addTask(Task task) {
        tasks.add(task);
    }

    /**
     * Deletes and returns the task at an index.
     *
     * @param index: Index of the task.
     * @return The deleted task.
     */
    public Task deleteTask(int index) {
        return tasks.remove(index);
    }

    /**
     * Marks the task at an index as done.
     *
     * @param index: Index of the task.
     * @return The updated task.
     */
    public Task markTask(int index) {
        Task task = tasks.get(index);
        task.markDone();
        return task;
    }

    /**
     * Marks the task at an index as not done.
     *
     * @param index: Index of the task.
     * @return The updated task.
     */
    public Task unmarkTask(int index) {
        Task task = tasks.get(index);
        task.markNotDone();
        return task;
    }

    /**
     * Returns the task at an index.
     *
     * @param index: Index of the task.
     * @return Task at the specified index.
     */
    public Task getTask(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return Number of tasks.
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns all tasks in the list.
     *
     * @return List of all tasks.
     */
    public List<Task> getAllTasks() {
        return tasks;
    }

    /**
     * Searches the task list for tasks in which the descriptions contain a keyword.
     *
     * @param keyword: The keyword used to search for matching tasks.
     * @return A list of tasks whose descriptions contain the keyword.
     */
    public List<Task> findTasks(String keyword) {
        List<Task> matchingTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.containsKeyword(keyword)) {
                matchingTasks.add(task);
            }
        }
        return matchingTasks;
    }
}