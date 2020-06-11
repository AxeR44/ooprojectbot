import Commands.CommandsImpl;
import Eventi.HiBotEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Listener extends ListenerAdapter {

    private static String prefix = ".";
    private static CommandsImpl commands;
    private HiBotEvent evnt;

    public Listener(){
        this.commands = new CommandsImpl(new ProvaBot().getTelegramAPIK());
        this.evnt = new HiBotEvent();
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split(" ");
        if (!event.getAuthor().isBot()) {
            if (args[0].startsWith(prefix)) {
                String rawCommand = args[0].substring(1);
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
                        try {
                            if (commands.sendTelegram(args, event)) {
                                event.getMessage().addReaction("\u2705").queue();
                            } else {
                                event.getMessage().addReaction("\u274C").queue();
                            }
                        } catch (Exception e) {
                            System.out.println("ERROR: " + e.getMessage());
                            event.getChannel().sendMessage(e.getMessage()).queue();
                        }
                        break;
                    case "help":
                        commands.help(event);
                        break;
                    case "listGroups":
                        commands.listChannels(event);
                        break;
                    case "play":
                        if(args.length == 2){
                            try{
                                commands.play(event, args[1], false);
                            }catch(NullPointerException e){
                                event.getChannel().sendMessage(e.getMessage()).queue();
                            }
                        }else{
                            event.getChannel().sendMessage("Numero di parametri invalido. Vedi .help per la sintassi del comando").queue();
                        }
                        break;
                    case "leave":
                        if(args.length == 1) {
                            commands.play(event, null, false);
                        } else {
                            event.getChannel().sendMessage("Numero di parametri invalido. Vedi .help per la sintassi del comando").queue();
                        }
                        break;
                    case "skip":
                        commands.skip(event);
                        break;
                    case "dequeue":
                        if(args.length == 2) {
                            commands.dequeue(args[1], event);
                        }else{
                            event.getChannel().sendMessage("Numero di parametri invalido. Vedi .help per la sintassi del comando").queue();
                        }
                        break;
                    case "queue":
                        commands.queue(event);
                        break;
                    case "translate":
                        commands.translator(event,args);
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
                }
            } else {
                evnt.handle(event);
            }
        }
    }
}
