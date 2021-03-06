package not.hub.mcdib.commands;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import not.hub.mcdib.DiscordBot;

import java.util.List;

public class CommandPresence extends Command {

    // TODO: add selection for different activities

    public CommandPresence(DiscordBot discordBot) {
        super("presence", "change bot presence text", 1, 64, discordBot);
    }

    @Override
    public void run(List<String> args) {
        getBot().getJda().getPresence().setPresence(OnlineStatus.ONLINE, Activity.watching(String.join(" ", args)));
    }

}
