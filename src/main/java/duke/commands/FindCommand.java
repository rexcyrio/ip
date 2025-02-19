package duke.commands;

import java.util.ArrayList;
import java.util.Arrays;
import duke.exceptions.DukeInvalidFindCommandException;
import duke.utilities.Storage;
import duke.utilities.TaskList;
import duke.utilities.Ui;

public class FindCommand extends Command {

    private final String[] tokens;

    public FindCommand(String[] tokens) {
        this.tokens = tokens;
    }

    public String execute(TaskList taskList, Ui ui, Storage storage)
            throws DukeInvalidFindCommandException {

        if (tokens.length < 2) {
            throw new DukeInvalidFindCommandException();
        }

        String name = String.join(" ", Arrays.copyOfRange(tokens, 1, tokens.length));
        ArrayList<String> taskStrings = taskList.findTasks(name);

        if (taskStrings.size() == 0) {
            return "No tasks match the query \"" + name + "\"";
        }

        return "Found:\n" + String.join("\n", taskStrings);
    }

    public boolean isByeCommand() {
        return false;
    }
}
