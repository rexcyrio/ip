import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;

public class TaskList {

    private final ArrayList<Task> tasks;

    TaskList() {
        this.tasks = new ArrayList<>();
    }

    public void handleListCommand() throws DukeEmptyListException {
        if (this.tasks.size() == 0) {
            throw new DukeEmptyListException();
        }

        for (int i = 0; i < tasks.size(); i++) {
            Task task = this.tasks.get(i);
            String indexString = Integer.toString(i + 1);
            System.out.println(indexString + ". " + task.toString());
        }
    }

    public void handleMarkUnmarkCommand(String[] tokens)
            throws DukeInvalidMarkCommandException, DukeInvalidUnmarkCommandException {

        String action = tokens[0];
        boolean isMark = action.equals("mark");

        if (tokens.length != 2) {
            if (isMark) {
                throw new DukeInvalidMarkCommandException();
            } else {
                throw new DukeInvalidUnmarkCommandException();
            }
        }

        int taskNumber;

        try {
            taskNumber = Integer.parseInt(tokens[1]);
        } catch (NumberFormatException e) {
            if (isMark) {
                throw new DukeInvalidMarkCommandException();
            } else {
                throw new DukeInvalidUnmarkCommandException();
            }
        }

        if (taskNumber < 1 || taskNumber > this.tasks.size()) {
            if (isMark) {
                throw new DukeInvalidMarkCommandException();
            } else {
                throw new DukeInvalidUnmarkCommandException();
            }
        }

        // need to convert back to 0-indexed
        Task task = tasks.get(taskNumber - 1);

        if (isMark) {
            task.markDone();
        } else {
            task.unmarkDone();
        }

        System.out.println(task);
    }

    public void handleTodoCommand(String[] tokens) throws DukeInvalidTodoCommandException {
        String[] taskNameArray = Arrays.copyOfRange(tokens, 1, tokens.length);

        if (taskNameArray.length == 0) {
            throw new DukeInvalidTodoCommandException();
        }

        String taskName = String.join(" ", taskNameArray);

        TodoTask newTodoTask = new TodoTask(taskName);

        this.addTask(newTodoTask);
        System.out.println("Added:\n" + newTodoTask);
        this.printNumberOfTasks();
    }

    public void handleDeadlineCommand(String[] tokens) throws DukeInvalidDeadlineCommandException {
        int indexOfBy = -1;

        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];

            if (token.equals("/by")) {
                indexOfBy = i;
                break;
            }
        }

        if (indexOfBy == -1) {
            throw new DukeInvalidDeadlineCommandException();
        }

        String[] taskNameArray = Arrays.copyOfRange(tokens, 1, indexOfBy);
        String[] byArray = Arrays.copyOfRange(tokens, indexOfBy + 1, tokens.length);

        if (taskNameArray.length == 0 || byArray.length == 0) {
            throw new DukeInvalidDeadlineCommandException();
        }

        String taskName = String.join(" ", taskNameArray);
        String by = String.join(" ", byArray);

        LocalDate byDate;

        try {
            byDate = LocalDate.parse(by, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new DukeInvalidDeadlineCommandException();
        }

        DeadlineTask newDeadlineTask = new DeadlineTask(taskName, byDate);
        this.addTask(newDeadlineTask);
        System.out.println("Added:\n" + newDeadlineTask);
        this.printNumberOfTasks();
    }

    public void handleEventCommand(String[] tokens) throws DukeInvalidEventCommandException {
        int indexOfFrom = -1;
        int indexOfTo = -1;

        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];

            if (token.equals("/from")) {
                indexOfFrom = i;
            } else if (token.equals("/to")) {
                indexOfTo = i;
            }
        }

        if (indexOfFrom == -1 || indexOfTo == -1) {
            throw new DukeInvalidEventCommandException();
        }

        String[] taskNameArray = Arrays.copyOfRange(tokens, 1, indexOfFrom);
        String[] fromArray = Arrays.copyOfRange(tokens, indexOfFrom + 1, indexOfTo);
        String[] toArray = Arrays.copyOfRange(tokens, indexOfTo + 1, tokens.length);

        if (taskNameArray.length == 0 || fromArray.length == 0 || toArray.length == 0) {
            throw new DukeInvalidEventCommandException();
        }

        String taskName = String.join(" ", taskNameArray);
        String from = String.join(" ", fromArray);
        String to = String.join(" ", toArray);

        EventTask newEventTask = new EventTask(taskName, from, to);

        this.addTask(newEventTask);
        System.out.println("Added:\n" + newEventTask);
        this.printNumberOfTasks();
    }

    public void addTask(Task task) {
        this.tasks.add(task);
    }

    public void handleDeleteCommand(String[] tokens) throws DukeInvalidDeleteCommandException {
        if (tokens.length != 2) {
            throw new DukeInvalidDeleteCommandException();
        }

        int taskNumber;

        try {
            taskNumber = Integer.parseInt(tokens[1]);
        } catch (NumberFormatException e) {
            throw new DukeInvalidDeleteCommandException();
        }

        if (taskNumber < 1 || taskNumber > this.tasks.size()) {
            throw new DukeInvalidDeleteCommandException();
        }

        // need to convert back to 0-indexed
        Task deletedTask = this.deleteTask(taskNumber - 1);
        System.out.println("Deleted:\n" + deletedTask.toString());
        this.printNumberOfTasks();
    }

    private Task deleteTask(int index) {
        return this.tasks.remove(index);
    }

    public void handleByeCommand() {
        System.out.println("Exiting...");
    }

    public void handleDueOnCommand(String[] tokens) throws DukeInvalidDueOnCommandException {
        if (tokens.length != 2) {
            throw new DukeInvalidDueOnCommandException();
        }

        LocalDate dueOnDate;

        try {
            dueOnDate = LocalDate.parse(tokens[1], DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new DukeInvalidDueOnCommandException();
        }

        boolean isAnyDeadlineTaskDueOnGivenDate = false;

        for (Task task : this.tasks) {
            if (task instanceof DeadlineTask) {
                DeadlineTask deadlineTask = (DeadlineTask) task;

                if (deadlineTask.isDueOn(dueOnDate)) {
                    isAnyDeadlineTaskDueOnGivenDate = true;
                    System.out.println(deadlineTask);
                }
            }
        }

        if (!isAnyDeadlineTaskDueOnGivenDate) {
            System.out.println("There are no deadline tasks due on " + dueOnDate);
        }
    }

    public void printNumberOfTasks() {
        int numTasks = this.tasks.size();

        if (numTasks == 1) {
            System.out.println("There is 1 task in the list");
        } else {
            System.out.println("There are " + numTasks + " tasks in the list");
        }
    }

    public String toDukeFileString() {
        ArrayList<String> output = new ArrayList<>();

        for (int i = 0; i < this.tasks.size(); i++) {
            Task task = this.tasks.get(i);
            output.add(task.toDukeFileString() + "\n");
        }

        return String.join("", output);
    }
}
