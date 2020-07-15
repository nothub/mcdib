package not.hub.mcdib.commands;

import not.hub.mcdib.DiscordBot;
import not.hub.mcdib.messages.ConfigMessage;

import java.util.List;

public class CommandPrefix extends Command {

    // TODO: Command: purge chat history (argument: number of messages) or (argument: timestamp start deleterange)

    public CommandPrefix(DiscordBot discordBot) {
        super("prefix", "change command prefix", 1, 1, discordBot);
    }

    @Override
    public void run(List<String> args) {

        if (args.get(0).isEmpty()) {
            return;
        }

        String prefix = args.get(0).substring(0, 1);

        // TODO: check if this a character that is common on keyboards

        getBot().setCommandPrefix(prefix);

        getBot().sendConfigMessage(new ConfigMessage("discord-command-prefix", prefix));

        sendInfoToDiscord("New prefix is: " + prefix);

    }

}
