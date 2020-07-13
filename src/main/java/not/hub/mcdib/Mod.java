package not.hub.mcdib;

import com.google.common.collect.Sets;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public final class Mod extends JavaPlugin {

    // m2dQueue & d2mQueue are used for inter thread communication.
    // they should be used in a way that the discord thread can be blocked
    // for a maximum of n ms (is there a discord connection timeout?)
    // but the mc thread will never get blocked by reading or writing the queues.
    // see BlockingQueue javadoc for read/write method explanation.
    private final BlockingQueue<String> m2dQueue = new LinkedBlockingQueue<>();
    private final BlockingQueue<String> d2mQueue = new LinkedBlockingQueue<>();

    private DiscordBot discordBot;

    @Override
    public void onEnable() {

        // load config, if false is returned stop further init
        if (!initConfig()) {
            return;
        }

        // run jda on second thread
        Thread botThread = new Thread(() -> {
            discordBot = new DiscordBot(m2dQueue, d2mQueue,
                    getConfig().getString("discord-bot-auth-token"),
                    getConfig().getLong("discord-bridge-channel"),
                    Sets.newHashSet(getConfig().getLongList("discord-admin-user-ids")),
                    Sets.newHashSet(getConfig().getLongList("discord-admin-role-ids")));
        });
        botThread.start();

        // TODO: pipe mc chat to bot thread by filling m2dqueue via chat listener
        // this returns false if the element was not added (queue was full probably):
        // m2dQueue.offer("testfrommc");

        // TODO: periodically (1000ms?) pipe discord chat from bot thread into mc chat via d2mqueue
        // this returns null if the queue was empty:
        // String testfromdiscord = d2mQueue.poll();

    }

    private boolean initConfig() {

        final String DEFAULT_TOKEN_VALUE = "AAAAAAAAAAAAAAAAAAAAAAAA.AAAAAA.AAAAAAAAAAAAAAAAAAAAAAAAAAA";
        final Long DEFAULT_ID_VALUE = 111111111111111111L;

        getConfig().addDefault("discord-bot-auth-token", DEFAULT_TOKEN_VALUE);
        getConfig().addDefault("discord-bridge-channel", DEFAULT_ID_VALUE);
        getConfig().addDefault("discord-admin-user-ids", Arrays.asList(DEFAULT_ID_VALUE, DEFAULT_ID_VALUE));
        getConfig().addDefault("discord-admin-role-ids", Arrays.asList(DEFAULT_ID_VALUE, DEFAULT_ID_VALUE));
        getConfig().options().copyDefaults(true);
        saveConfig();

        String token = getConfig().getString("discord-bot-auth-token");
        if (token == null || token.equals(DEFAULT_TOKEN_VALUE)) {
            getLogger().warning("Please supply a bot token! mcdib shutting down...");
            return false;
        }

        Long channel = getConfig().getLong("discord-bridge-channel");
        if (channel.equals(DEFAULT_ID_VALUE)) {
            getLogger().warning("Please supply a bridge channel id! mcdib shutting down...");
            return false;
        }

        return true;

    }

    @Override
    public void onDisable() {
        discordBot.shutdown();
    }

}
