package EventListener;

import Commands.CommandsImpl;
import Exceptions.NotEnoughParametersException;
import Notifier.TelegramNotifierAsync;
import Wrappers.ChannelList;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;

public class Listener extends ListenerAdapter {

    private static String prefix = ".";
    private static CommandsImpl commands;

    public Listener(ChannelList list, TelegramNotifierAsync tnAsync, MessageReactionHandler handler){
        this.commands = new CommandsImpl(list, tnAsync, handler);
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event){
        new Thread(()->{
            handleCommand(event);
        }).start();
    }

    private void handleCommand(@NotNull GuildMessageReceivedEvent event){
        String[] args = event.getMessage().getContentRaw().split(" ");
        if (!event.getAuthor().isBot()) {
            if (args[0].startsWith(prefix)) {
                String rawCommand = args[0].substring(1);
                try {
                    switch (rawCommand) {
                        case "ping":
                            commands.ping(event);
                            break;
                        case "info":
                            commands.sendInfo(event);
                            break;
                        case "invite":
                            commands.invite(event);
                            break;
                        case "telegram":
                            if (commands.sendTelegram(event)) {
                                event.getMessage().addReaction("\u2705").queue();
                            }

                            break;
                        case "help":
                            commands.help(event);
                            break;
                        case "listGroups":
                            commands.listChannels(event);
                            break;
                        case "play":
                            try {
                                commands.play(event, event.getMessage().getContentRaw().substring(6), false);
                            } catch (NullPointerException e) {
                                event.getChannel().sendMessage(e.getMessage()).queue();
                            } catch (StringIndexOutOfBoundsException e) {
                                event.getChannel().sendMessage("Numero di parametri invalido").queue();
                            }

                            break;
                        case "leave":
                            if (args.length == 1) {
                                commands.play(event, null, false);
                            } else {
                                event.getChannel().sendMessage("Numero di parametri invalido. Vedi .help per la sintassi del comando").queue();
                            }
                            break;
                        case "skip":
                            commands.skip(event);
                            break;
                        case "dequeue":
                            if (args.length == 2) {
                                commands.dequeue(args[1], event);
                            } else {
                                event.getChannel().sendMessage("Numero di parametri invalido. Vedi .help per la sintassi del comando").queue();
                            }
                            break;
                        case "queue":
                            commands.queue(event);
                            break;
                        case "translate":
                            commands.translator(event, args);
                            break;
                        case "mtt":
                            commands.quiz(event);
                            break;
                        case "joke":
                            commands.rJokes(event);
                            break;
                        case "langlist":
                            commands.languagePrinter(event);
                            break;
                        case "votekick":
                            commands.voteKick(event);
                            break;
                        case "survey":
                            commands.survey(event);
                            break;
                        case "endSurvey":
                            commands.endSurvey(event);
                            break;
                        case "addTelegram":
                            commands.addTelegram(event);
                            break;
                        case "removeTelegram":
                            commands.removeTelegram(event);
                            break;
                        case "coinToss":
                            commands.coinToss(event);
                            break;
                        case "report":
                            commands.reportUser(event);
                            break;
                        case "softban":
                            commands.softBan(event);
                            break;
                        case "wiki":
                            commands.wikiResearch(event);
                            break;
                        case "lyrics":
                            commands.lyrics(event);
                            break;
                        case "reminder":
                            commands.reminder(event);
                            break;
                        case "roll":
                            commands.rollDice(event);
                            break;
                        case "seek":
                            commands.seek(event);
                            break;
                        case "setInfoChannel":
                            commands.setInfoChannel(event);
                            break;
                        case "addRole":
                            commands.addRole(event);
                            break;
                    }
                }catch(NotEnoughParametersException e){
                    event.getChannel().sendMessage(e.getMessage()).queue();
                }
            }else {
                this.handleSpecial(event);
            }
        }
    }

    private void handleSpecial(@NotNull GuildMessageReceivedEvent event){
        String content = event.getMessage().getContentRaw();
        if(content.equalsIgnoreCase("KEKW")){
            try{
                InputStream iStream = this.getClass().getClassLoader().getResourceAsStream("TKEKW.png");
                event.getChannel().sendFile(iStream, "TKEKW.png").queue();
            }catch(Exception e){
                //nop
                System.out.println("Error " + e.getMessage());
            }
        }
        if(content.equalsIgnoreCase("SaS")){
            try{
                InputStream iStream = this.getClass().getClassLoader().getResourceAsStream("sas.png");
                event.getChannel().sendFile(iStream, "sas.png").queue();
            }catch(Exception e){
                //nop
                System.out.println("Error " + e.getMessage());
            }
        }
    }
}
