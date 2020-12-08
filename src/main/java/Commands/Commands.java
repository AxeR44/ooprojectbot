package Commands;

import Exceptions.NotEnoughParametersException;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public interface Commands {
    void help(GuildMessageReceivedEvent event);
    void ping(GuildMessageReceivedEvent event);
    void invite(GuildMessageReceivedEvent event);
    void sendInfo(GuildMessageReceivedEvent event);
    boolean sendTelegram(GuildMessageReceivedEvent event) throws NotEnoughParametersException;
    void listChannels(GuildMessageReceivedEvent event);
    void play(GuildMessageReceivedEvent event, String Url, boolean hideNotification) throws NullPointerException;
    void skip(GuildMessageReceivedEvent event);
    void dequeue(String index, GuildMessageReceivedEvent event);
    void queue(GuildMessageReceivedEvent event);
    void translator(GuildMessageReceivedEvent event, String[] args);
    void quiz(GuildMessageReceivedEvent event);
    void rJokes(GuildMessageReceivedEvent event);
    void languagePrinter(GuildMessageReceivedEvent event);
    void voteKick(GuildMessageReceivedEvent event) throws NotEnoughParametersException;
    void survey(GuildMessageReceivedEvent event) throws NotEnoughParametersException;
    void endSurvey(GuildMessageReceivedEvent event) throws NotEnoughParametersException;
    void addTelegram(GuildMessageReceivedEvent event) throws NotEnoughParametersException;
    void removeTelegram(GuildMessageReceivedEvent event) throws NotEnoughParametersException;
    void coinToss(GuildMessageReceivedEvent event);
    void reportUser(GuildMessageReceivedEvent event) throws NotEnoughParametersException;
    void softBan(GuildMessageReceivedEvent event) throws NotEnoughParametersException;
    void wikiResearch(GuildMessageReceivedEvent event);
    void lyrics(GuildMessageReceivedEvent event);
    void reminder(GuildMessageReceivedEvent event) throws NotEnoughParametersException;
    void rollDice(GuildMessageReceivedEvent event) throws NotEnoughParametersException;
    void seek(GuildMessageReceivedEvent event) throws NotEnoughParametersException;
    void setInfoChannel(GuildMessageReceivedEvent event);
    void addRole(GuildMessageReceivedEvent event);
}