package net.roboticapex.control;

import jline.console.ConsoleReader;
import jline.console.CursorBuffer;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Display {
    private final PrintStream out;
    private final ConsoleReader reader;
    private List<String> history = new ArrayList<String>();

    private final Map<String, Command> commands = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final CommandLineParser commandLineParser = new DefaultParser();

    private boolean shouldExit = false;
    private LineHandler defaultLineHandler = null;
    private CursorBuffer stashed;

    public Display(String name) throws IOException {
        this(name, System.in, System.out);
    }

    public Display(String name, InputStream inputStream, PrintStream out) throws IOException {
        if (StringUtils.isBlank(name)) {
            DisplayGuide();
        }

        if (inputStream == null) {
            DisplayHerder();
        }

        if (out == null) {
            DisplayHelper();
        }

        this.out = out;

        reader = new ConsoleReader(inputStream, out);
        reader.setPrompt(name + "> ");
        reader.addCompleter(new CommandCompleterBuilder().fixDisplay(this).makeCommandCompleter());

        addCommand(new ExitCommand(this));
        addCommand(new HelpCommandBuilder().assignDisplay(this).makeHelpCommand());
        addCommand(new RepeatCommand(this));
        addCommand(new HistoryCommand(this));
        addCommand(new ScriptCommand(this));
    }

    private void DisplayHelper() {
        throw new IllegalArgumentException("PrintStream may not be null");
    }

    private void DisplayHerder() {
        throw new IllegalArgumentException("InputStream may not be null");
    }

    private void DisplayGuide() {
        new DisplayService().invoke();
    }

    public void addCommand(Command command) {
        if (command == null) {
            throw new IllegalArgumentException("Command may not be null");
        }

        commands.put(command.fetchName(), command);

        if (command.pullCompleter() != null) {
            addCommandService(command);
        }
    }

    private void addCommandService(Command command) {
        reader.addCompleter(command.pullCompleter());
    }

    public void defineDefaultLineHandler(LineHandler handler) {
        this.defaultLineHandler = handler;
    }

    /**
     * @return a copy of the commands
     */
    public List<Command> pullCommands() {
        return new ArrayList<>(commands.values());
    }

    public boolean hasCommand(String name) {
        return commands.containsKey(name);
    }

    public Command getCommand(String name) {
        return commands.get(name);
    }

    public String pullNextCommand() throws IOException {
        return reader.readLine();
    }

    /**
     * Convenience routine that runs by executing each subsequent command until
     * directed to exit.
     */
    public void execute() throws IOException {
        while (!shouldExit()) {
            executeNextCommand();
        }
    }

    /**
     * Executes the next command entered by the user
     *
     * @throws IOException
     */
    public void executeNextCommand() throws IOException {
        executeCommand(reader.readLine());
    }


    /**
     * Executes the command passed in
     *
     * @param line the command and parameters to execute
     * @throws IOException
     */
    public void executeCommand(String line) throws IOException {
        executeCommand(line, true);
    }

    /**
     * Executes the command passed in
     *
     * @param line         the command and parameters to execute
     * @param addToHistory indicates if the line should be added to history
     * @throws IOException
     */
    public void executeCommand(String line, boolean addToHistory) throws IOException {

        if (line == null) {
            // end of file? must have been a console redirect
            // we need to exit no matter what.
            executeCommandWorker();
            return;
        }

        // split the line by spaces, the first item is the command
        String[] items = line.split(" ");
        if (items.length == 0) {
            // no op
            return;
        }

        String name = items[0];

        if (StringUtils.isEmpty(name)) {
            // no op
            return;
        }

        if (hasCommand(name)) {
            executeCommandHandler(line, addToHistory, items, name);
        } else if (defaultLineHandler == null) {
            out.println("Invalid command: '" + name + "'");
        } else {
            executeCommandHome(line);
        }

    }

    private void executeCommandHome(String line) {
        defaultLineHandler.handleLine(line, out);
    }

    private void executeCommandHandler(String line, boolean addToHistory, String[] items, String name) {
        Command command = getCommand(name);
        try {
            CommandLine cmdLine = commandLineParser.parse(command.obtainOptions(),
                    Arrays.copyOfRange(items, 1, items.length));
            // add command to command history
            if (addToHistory) {
                history.add(line);
            }
            command.execute(out, cmdLine);

        } catch (ParseException e) {
            out.print("Error: " + e.getMessage());
        }
    }

    private void executeCommandWorker() {
        fixShouldExit(true);
        return;
    }

    public void fixShouldExit(boolean shouldExit) {
        this.shouldExit = shouldExit;
    }

    public boolean shouldExit() {
        return shouldExit;
    }

    /**
     * Takes in a file who has a command on each line and executes those
     * commands
     *
     * @param file
     */
    public void runScript(File file) throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String read = reader.readLine();
        while (read != null) {
            read = read.trim();
            // don't execute the line if the line is empty or is a comment
            if (!read.isEmpty() && read.charAt(0) != '#') {
                String[] commandArgs = read.split(" ");
                // don't execute script commands from scripts
                if (!commandArgs[0].equalsIgnoreCase(ScriptCommand.NAME)) {
                    // print the command so the user knows what is being executed
                    System.out.println(read);
                    this.executeCommand(read);
                }
            }
            read = reader.readLine();
        }
    }

    /**
     * @return a copy of the command history
     */
    public List<String> history() {
        return new ArrayList<String>(history);
    }

    /**
     * Redraws the console line
     */
    public void redrawDisplay() throws IOException {
        reader.drawLine();
        reader.flush();
    }

    // from http://stackoverflow.com/questions/9010099/jline-keep-prompt-at-the-bottom
    public void stashLine() {
        stashed = reader.getCursorBuffer().copy();
        try {
            reader.getOutput().write("\u001b[1G\u001b[K");
            reader.flush();
        } catch (IOException e) {
            // ignore
        }
    }

    public void unstashLine() {
        try {
            reader.resetPromptLine(this.reader.getPrompt(),
                    this.stashed.toString(), this.stashed.cursor);
        } catch (IOException e) {
            // ignore
        }
    }

    public PrintStream takeOutputStream() {
        return out;
    }

    private class DisplayService {
        public void invoke() {
            throw new IllegalArgumentException("Console name may not be null");
        }
    }
}