package Commands;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public interface Commands {
    void help(GuildMessageReceivedEvent event);
    void ping(GuildMessageReceivedEvent event);
    void invite(GuildMessageReceivedEvent event);
    void sendInfo(GuildMessageReceivedEvent event);
    boolean sendTelegram(GuildMessageReceivedEvent event) throws Exception;
    void listChannels(GuildMessageReceivedEvent event);
    void play(GuildMessageReceivedEvent event, String Url, boolean hideNotification) throws NullPointerException;
    void skip(GuildMessageReceivedEvent event);
    void dequeue(String index, GuildMessageReceivedEvent event);
    void queue(GuildMessageReceivedEvent event);
    void translator(GuildMessageReceivedEvent event, String[] args);
    void quiz(GuildMessageReceivedEvent event);
    void rJokes(GuildMessageReceivedEvent event);
    void languagePrinter(GuildMessageReceivedEvent event);
    void voteKick(GuildMessageReceivedEvent event);
    void survey(GuildMessageReceivedEvent event);
    void endSurvey(GuildMessageReceivedEvent event);
    void addTelegram(GuildMessageReceivedEvent event);
    void removeTelegram(GuildMessageReceivedEvent event);
    void coinToss(GuildMessageReceivedEvent event);
    void reportUser(GuildMessageReceivedEvent event);
    void softBan(GuildMessageReceivedEvent event);
    void wikiResearch(GuildMessageReceivedEvent event);
    void lyrics(GuildMessageReceivedEvent event);
    void reminder(GuildMessageReceivedEvent event);
    void rollDice(GuildMessageReceivedEvent event);
    void seek(GuildMessageReceivedEvent event);
}