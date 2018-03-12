package net.computerpoint.console;

import org.apache.commons.cli.CommandLine;

import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

public class RepeatCommand extends Command {
    private static final String NAME = "repeat";
    private static final int MAX_REPEATS = 5;
    private Console console;

    public RepeatCommand(Console console) {
        super(NAME, "Repeats the last n commands", "repeat <number of commands to repeat>");
        this.console = console;
    }

    @Override
    public void execute(PrintStream out, CommandLine cmdLine) {
        List<String> argList = cmdLine.getArgList();
        if (argList.size() != 1) {
            out.println(this.takeUsage());
            return;
        }
        try {
            int numOfCommandsToRepeat = Integer.parseInt(argList.get(0));
            if (numOfCommandsToRepeat > MAX_REPEATS) {
                out.println("Error cannot perform more than " + MAX_REPEATS + " repeats.");
                return;
            }
            List<String> history = console.history();
            int size = history.size();

            // we need size - 1 because the most recent repeat command is in the
            // history, but we do not count it
            if (size - 1 < numOfCommandsToRepeat) {
                executeHerder(out, numOfCommandsToRepeat, size);
            } else if (numOfCommandsToRepeat > 0) {
                for (int i = size - numOfCommandsToRepeat - 1; i < size - 1; i++) {
                    new RepeatCommandTarget(out, history, i).invoke();
                }
            }
        } catch (NumberFormatException e) {
            out.println(this.takeUsage());
        } catch (IOException e) {
            out.println(e.getMessage());
        }
    }

    private void executeHerder(PrintStream out, int numOfCommandsToRepeat, int size) {
        out.println("Error: cannot repeat " + numOfCommandsToRepeat + " commands when only " + (size - 1)
                + " have been executed");
    }

    private class RepeatCommandTarget {
        private PrintStream out;
        private List<String> history;
        private int j;

        public RepeatCommandTarget(PrintStream out, List<String> history, int j) {
            this.out = out;
            this.history = history;
            this.j = j;
        }

        public void invoke() throws IOException {
            String command = history.get(j);
            // print command so user can see what command is being executed
            out.println(command);

            // check that we are not repeating the repeat command
            String[] commandArgs = command.split(" ");
            if (!commandArgs[0].equalsIgnoreCase(NAME)) {
                console.executeCommand(command, false);
            } else {
                invokeEntity();
            }
        }

        private void invokeEntity() {
            out.println("Cannot repeat a repeat command");
        }
    }
}