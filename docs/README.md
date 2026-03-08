# BryanChatbot User Guide

BryanChatbot is a simple command-line task management chatbot that helps you keep track of todos, deadlines, and events.

You interact with BryanChatbot by typing commands in the terminal.

---

# Quick Start

1. Download the `.jar` file from the GitHub release page.
2. Copy the `.jar` file into an empty folder.
3. Open a terminal in that folder.
4. Run the chatbot using:

```
java -jar "ip.jar"
```

5. Type commands to manage your tasks.

---

# Features

## 1. Add a Todo

Adds a simple task.

Command:

```
todo DESCRIPTION
```

Example:

```
todo read book
```

Output:

```
[T][ ] read book
```

---

## 2. Add a Deadline

Adds a task with a deadline date.

Command:

```
deadline DESCRIPTION /by YYYY-MM-DD
```

Example:

```
deadline submit report /by 2026-03-14
```

Output:

```
[D][ ] submit report (by: Mar 14 2026)
```

---

## 3. Add an Event

Adds an event with a start and end time.

Command:

```
event DESCRIPTION /from START /to END
```

Example:

```
event CS2113 Lecture /from 4pm /to 6pm
```

Output:

```
[E][ ] CS2113 Lecture (from: 4pm to: 6pm)
```

---

## 4. List Tasks

Displays all tasks in your list.

Command:

```
list
```

Example output:

```
1.[T][ ] read book
2.[D][ ] submit report (by: Mar 14 2026)
3.[E][ ] CS2113 Lecture (from: 4pm to: 6pm)
```

---

## 5. Mark Task as Done

Marks a task as completed.

Command:

```
mark INDEX
```

Example:

```
mark 1
```

Output:

```
[T][X] read book
```

---

## 6. Unmark Task

Marks a completed task as not done.

Command:

```
unmark INDEX
```

Example:

```
unmark 1
```

Output:

```
[T][ ] read book
```

---

## 7. Delete Task

Removes a task from the list.

Command:

```
delete INDEX
```

Example:

```
delete 1
```

Output:

```
Noted. I've removed this task.
```

---

## 8. Find Tasks

Search for tasks containing a keyword.

Command:

```
find KEYWORD
```

Example:

```
find book
```

Output:

```
1.[T][ ] read book
2.[D][ ] return book (by: Mar 10 2026)
```

---

## 9. Exit the Chatbot

Closes the program.

Command:

```
bye
```

Example output:

```
Bye. Hope to see you again soon!
```

---

# Saving Tasks

BryanChatbot automatically saves your tasks to a file.

The data file is stored in:

```
data/chatbot.txt
```

Tasks are loaded automatically when the chatbot starts.

---

# Command Summary

| Command | Description |
|------|------|
| `todo DESCRIPTION` | Add a todo task |
| `deadline DESCRIPTION /by DATE` | Add a deadline |
| `event DESCRIPTION /from START /to END` | Add an event |
| `list` | Show all tasks |
| `mark INDEX` | Mark task as done |
| `unmark INDEX` | Mark task as not done |
| `delete INDEX` | Delete a task |
| `find KEYWORD` | Search tasks |
| `bye` | Exit the chatbot |

---