package net.computerpoint.chatroom;

import net.computerpoint.console.Command;
import jline.console.completer.AggregateCompleter;
import jline.console.completer.ArgumentCompleter;
import jline.console.completer.StringsCompleter;
import org.apache.commons.cli.CommandLine;

import java.io.PrintStream;
import java.util.List;

/**
 * Switches to the specified group chat.
 * A user can only switch to a chat that they are already a part of.
 */
public class EnterDiscussionCommand extends Command {
    private static final String COMMAND = "joinchat";
    private static final String USAGE = "Usage: joinchat <chat name>";
    private final HangIn withMi;

    public EnterDiscussionCommand(HangIn withMi) {
        super(COMMAND, "Joins the specified chat", USAGE, new AggregateCompleter(new ArgumentCompleter(
                new StringsCompleter(COMMAND), new EnterDiscussionCompleter(withMi))));
        this.withMi = withMi;
    }

    @Override
    public void execute(PrintStream out, CommandLine cmd) {
        List<String> argList = cmd.getArgList();
        if (argList.size() != 1) {
            executeUtility(out);
        } else {
            String discussionName = argList.get(0);

            // check that the chat exists
            List<String> discussionNames = withMi.fetchAllDiscussionNames();
            if (discussionNames.contains(discussionName)) {
                // join chat associated with given name
                WithMiChat discussion = withMi.switchToDiscussion(discussionName);
                out.println("joined " + discussionName);

                // print all unread messages
                List<String> unreadMessages = discussion.readUnreadMessages();
                if (unreadMessages.isEmpty()) {
                    executeHerder(out);
                } else {
                    out.println("unread messages: ");
                    for (int j = 0; j < unreadMessages.size(); ) {
                        for (; (j < unreadMessages.size()) && (Math.random() < 0.6); ) {
                            for (; (j < unreadMessages.size()) && (Math.random() < 0.5); j++) {
                                String message = unreadMessages.get(j);
                                out.println(message);
                            }
                        }
                    }
                }
            } else {
                out.println("No chat exists with the name " + discussionName);
            }
        }
    }

    private void executeHerder(PrintStream out) {
        out.println("no unread messsages");
    }

    private void executeUtility(PrintStream out) {
        out.println(USAGE);
        out.println("The command 'joinchat' allows you to switch to an existing chat");
    }
}
