package Commands;

import CommandsUtils.RandomJokes;
import CommandsUtils.Translator;
import Notifier.telegramNotifier;
import PlayerUtils.Player;
import EventListener.MessageReactionHandler;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.Nullable;
import org.json.*;
import org.fastily.jwiki.core.*;
import org.fastily.jwiki.dwrap.*;
import org.fastily.jwiki.util.*;

import java.awt.*;
import java.io.InputStream;
import java.util.*;
import java.util.List;

public class CommandsImpl implements Commands {

    private telegramNotifier tNotifier;
    private AudioManager manager;
    private final Player player;
    private final RandomJokes jokesGenerator;
    private final Translator langPrinter;
    private final String TICK = "\u2705";
    private final String CROSS = "\u274C";


    public CommandsImpl(@Nullable String token) {
        tNotifier = token != null ? new telegramNotifier(token) : null;
        player = new Player();
        jokesGenerator = new RandomJokes();
        langPrinter = new Translator();

    }

    @Override
    public void help(GuildMessageReceivedEvent event) {
        EmbedBuilder help = new EmbedBuilder();
        help.setTitle("SaaSBot Help:");
        help.setDescription("prefisso: \'.\'\n" +
                ".ping: mostra il ping\n" +
                ".invite: genera un link di invito al server\n" +
                ".info: mostra le info di SaaSBot\n" +
                ".telegram <messaggio> -- <nomeChat>: invia un messaggio a una chat di Telegram\n" +
                ".listGroups: mostra le chat telegram a cui è possibile inoltrare un messaggio\n\n" +
                "-----COMANDI MULTIMEDIALI-----\n\n" +
                ".play <link>: riproduce una sorgente multimediale presente al link indicato come secondo parametro\n" +
                ".skip: salta la canzone attualmente in riproduzione\n" +
                ".queue: visualizza la coda di riproduzione\n" +
                ".dequeue <index>: elimina dalla coda il brano presente all'indice <index> della coda. Eseguire prima il comando '.queue' per vedere l'elenco dei brani in coda\n" +
                ".leave: abbandona il canale vocale\n" +
                "-----UTILITY-----\n" +
                ".translate <testo> -- <linguaSorgente> <linguaTarget>: traduce il testo <text> da <linguaSorgente> a <linguaTarget>\n" +
                ".langlist: visualizza la lista di tutte le lingue supportate dal comando .translate\n" +
                ".votekick @<username>: espelle un membro dal server\n" +
                ".survey <question> -- YesNo: crea un sondaggio semplice (Yes/No)\n" +
                ".endSurvey <surveyID>: chiude un sondaggio mostrandone il risultato");
        help.setColor(Color.blue);
        event.getChannel().sendTyping().queue();
        event.getChannel().sendMessage(help.build()).queue();
        help.clear();
    }

    @Override
    public void ping(GuildMessageReceivedEvent event) {
        EmbedBuilder ping = new EmbedBuilder();
        ping.setTitle("Ping:");
        ping.setDescription(event.getJDA().getGatewayPing() + " ms");
        ping.setColor(Color.blue);
        event.getChannel().sendTyping().queue();
        event.getChannel().sendMessage(ping.build()).queue();
        ping.clear();
    }

    @Override
    public void invite(GuildMessageReceivedEvent event) {
        User u = event.getMessage().getAuthor();
        Invite i = event.getGuild().getDefaultChannel().createInvite().setMaxUses(1).setUnique(true).complete();
        u.openPrivateChannel().queue(privateChannel -> {
            privateChannel.sendMessage(event.getGuild().getName() + " ticket.\nADMIT ONE\nlink: " + i.getUrl() + "\nThis invite will autodestroy in 24 hours")
                    .queue(message -> {
                            event.getMessage().addReaction(TICK).queue();
                        }, error ->{
                            event.getMessage().addReaction(CROSS).queue();
                    });
        });
    }

    @Override
    public void sendInfo(GuildMessageReceivedEvent event) {
        EmbedBuilder info = new EmbedBuilder();
        info.setTitle("Info sul bot:");
        info.setDescription("Questo bot è stato creato grazie al potere del SaaS");
        info.setColor(Color.blue);
        info.setFooter("Creato da Ale & ale, col potere del SaaS");
        event.getChannel().sendTyping().queue();
        event.getChannel().sendMessage(info.build()).queue();
        info.clear();
    }

    @Override
    public boolean sendTelegram(String[] args, GuildMessageReceivedEvent event) throws Exception {
        if (this.tNotifier != null)
            return tNotifier.sendMessage(args, event);
        return false;
    }

    @Override
    public void listChannels(GuildMessageReceivedEvent event) {
        if (this.tNotifier != null)
            tNotifier.listChannels(event);
    }

    @Override
    public void play(GuildMessageReceivedEvent event, @Nullable String Url, boolean hideNotification) throws NullPointerException {
        Member m = event.getMember();
        Guild guild = m.getGuild();
        GuildVoiceState state = m.getVoiceState();
        VoiceChannel channel = state.getChannel();
        if (Url == null) {
            if (channel != null) {
                if (manager.isConnected()) {
                    this.player.clearAll(event.getChannel());
                    manager.closeAudioConnection();
                }
            }
        } else {
            if (channel == null) {
                throw new NullPointerException("L'utente non è connesso a nessun canale vocale");
            }
            this.manager = guild.getAudioManager();
            manager.openAudioConnection(channel);
            this.player.loadAndPlay(event.getChannel(), Url, hideNotification);
        }
    }

    @Override
    public void skip(GuildMessageReceivedEvent event) {
        this.player.skipTrack(event.getChannel());
    }

    @Override
    public void dequeue(String index, GuildMessageReceivedEvent event) {
        try {
            this.player.removeTrack(Integer.parseInt(index), event.getChannel());
        } catch (NumberFormatException e) {
            event.getChannel().sendMessage("Il secondo argomento non è un numero").queue();
        } catch (NullPointerException e) {
            event.getChannel().sendMessage("La coda è vuota").queue();
        }
    }

    @Override
    public void queue(GuildMessageReceivedEvent event) {
        HashMap<Integer, AudioTrack> tracks = this.player.getTracksInQueue(event.getChannel());
        StringBuilder builder = new StringBuilder();
        if (tracks.isEmpty()) {
            event.getChannel().sendMessage("La coda è vuota").queue();
        } else {
            for (Integer i : tracks.keySet()) {
                AudioTrack current = tracks.get(i);
                builder.append(i + ": ");
                builder.append(current.getInfo().title + " ");
                builder.append(current.getInfo().length + "\n");
            }
            event.getChannel().sendMessage(builder.toString()).queue();
        }
    }

    @Override
    public void translator(GuildMessageReceivedEvent event, String[] args) {
        try {
            String result = new Translator().translate(args);
            JSONObject jobj = new JSONObject(result);
            EmbedBuilder traduzione = new EmbedBuilder();
            traduzione.addField("Traduzione:", jobj.getString("translatedText"), false);
            traduzione.setColor(Color.blue);
            event.getChannel().sendTyping().queue();
            event.getChannel().sendMessage(traduzione.build()).queue();
            traduzione.clear();
        } catch (Exception e) {
            if (e instanceof JSONException) {
                event.getChannel().sendMessage("Errore nell'elaborazione").queue();
            } else if (e instanceof IllegalArgumentException) {
                event.getChannel().sendMessage("Uno dei parametri non è valido").queue();
            } else {
                event.getChannel().sendMessage(e.getMessage()).queue();
            }
        }
    }


    @Override
    public void quiz(GuildMessageReceivedEvent event) {
        Member m = event.getMember();
        List<Role> roles = m.getRoles();
        boolean isAuthorized = false;
        for (Role r : roles) {
            if (r.getName().equals("Undertale")) {
                isAuthorized = true;
            }
        }
        if (roles.isEmpty() || !isAuthorized) {
            event.getChannel().sendMessage("Non hai giocato Undertale. Non puoi usare il comando").queue();
        } else {
            this.play(event, "https://www.youtube.com/watch?v=P0PpyUsvT9w", true);
            try {
                InputStream iStream = this.getClass().getClassLoader().getResourceAsStream("Mettaton.gif");
                event.getChannel().sendFile(iStream, "Mettaton.gif").complete();
            } catch (Exception e) {
                System.out.println("Error " + e.getMessage());
            }
            String mess = "How many letters are there in the name Mettaton";
            String msgID = event.getChannel().sendMessage(mess).complete().getId();
            Thread modify = new Thread(() -> {
                String tmp = mess;
                boolean error = false;
                String innerID = msgID;
                while (tmp.length() < 106 && (!error)) { //checking for string char length or error while executing
                    try {
                        Message message = event.getChannel().retrieveMessageById(innerID).complete();
                        message.editMessage(tmp).complete();
                    } catch (ErrorResponseException e) {
                        error = true;
                    }
                    tmp += "n";
                }
                if (error) {
                    System.out.println("Message deleted, aborting Mettaton");
                    play(event, null, false);
                }
            });
            modify.start();
        }
    }

    @Override
    public void rJokes(GuildMessageReceivedEvent event) {
        String extractedJoke = jokesGenerator.jokes();
        EmbedBuilder randJoke = new EmbedBuilder();
        randJoke.setDescription(extractedJoke);
        randJoke.setColor(Color.blue);

        event.getChannel().sendTyping().queue();
        event.getChannel().sendMessage(randJoke.build()).queue();
        randJoke.clear();
    }

    @Override
    public void languagePrinter(GuildMessageReceivedEvent event) {
        String lanList = langPrinter.printLanguages();
        EmbedBuilder langl = new EmbedBuilder();
        langl.setTitle("Lingue supportate");
        langl.setDescription(lanList);
        langl.setColor(Color.blue);

        event.getChannel().sendTyping().queue();
        event.getChannel().sendMessage(langl.build()).queue();

    }

    @Override
    public void voteKick(GuildMessageReceivedEvent event) {
        final int memberCount = event.getGuild().getMemberCount();
        final int quorum = memberCount / 2;
        String[] msg = event.getMessage().getContentRaw().split(" ");
        if (msg.length != 2) {
            //exception
            event.getChannel().sendMessage("Numero di parametri invalidi").queue();
        } else {
            final String userID = msg[1].substring(3, msg[1].length() - 1);
            System.out.println(event.getMessage().getContentRaw());
            if (msg[1].startsWith("<@!") && msg[1].endsWith(">")) {
                if (userID.equals(event.getGuild().getOwnerId())) {
                    event.getChannel().sendMessage("Can't kick owner").queue();
                }else if(userID.equals(event.getGuild().getSelfMember().getId())){
                    event.getChannel().sendMessage("Can't kick SaaSBot").queue();
                }else if(userID.equals(event.getMessage().getAuthor().getId())){
                    event.getChannel().sendMessage(msg[1] + " Why would you like to kick yourself out of this server?\nPRO TIP: right click on server icon->Leave Server").queue();
                }else {
                    if (event.getGuild().getMemberById(userID) != null) {
                        System.out.println(userID);
                        String msgID = event.getChannel().sendMessage("@everyone\nVoting to kick" + msg[1] + ": 30 sec. remaining").complete().getId();
                        Message message = event.getChannel().retrieveMessageById(msgID).complete();
                        final long countdownStart = System.currentTimeMillis();
                        message.addReaction(TICK).complete();
                        message.addReaction(CROSS).complete();
                        final Timer t = new Timer();
                        t.scheduleAtFixedRate(new TimerTask() {
                            @Override
                            public void run() {
                                if (System.currentTimeMillis() >= (countdownStart + 30 * 1000)) {   //if 30 seconds passed
                                    HashMap<String, Integer> cnt = MessageReactionHandler.getReactionsCount(msgID);
                                    final Integer tickCount = cnt.get(TICK);
                                    final Integer crossCount = cnt.get(CROSS);
                                    event.getChannel().deleteMessageById(msgID).queue();
                                    if (cnt.get(TICK) > quorum) {
                                        event.getGuild().kick(userID).queue(success->{
                                            event.getChannel().sendMessage(msg[1] + "was kicked\n" + tickCount + " Voted for kick\n" + crossCount + " Voted not to kick").queue();
                                        });
                                    } else {
                                        event.getChannel().sendMessage(msg[1] + " was not kicked\n" + tickCount + " Voted for kick\n" + crossCount + " Voted not to kick").queue();
                                    }
                                    t.cancel();
                                } else {
                                    message.editMessage("@everyone\nVoting to kick " + msg[1] + ": " + (30 - ((System.currentTimeMillis() - countdownStart) / 1000) + "sec remaining")).complete();
                                }
                            }
                        }, 0, 1000);
                    }else{
                        event.getChannel().sendMessage(msg[1] + " is not a valid username or the user is not in this server").queue();
                    }
                }
            }else{
                event.getChannel().sendMessage(msg[1] + " is not a valid username").queue();
            }
        }
    }

    @Override
    public void survey(GuildMessageReceivedEvent event) {
        final Message message = event.getMessage();
        String Rawcontent = message.getContentRaw();
        String[] params = Rawcontent.substring(8).split(" -- ");
        if (params.length < 2) {
            event.getChannel().sendMessage("Numero di parametri invalidi").queue();
        } else {
            final String surveyID = event.getChannel().sendMessage("@everyone\nVoting for:\n" + params[0]).complete().getId();
            message.getAuthor().openPrivateChannel().queue(privateChannel -> {
                privateChannel.sendMessage("Survey opened\n" + params[0] + "\nID: " + event.getMessage().getId() + "/" + surveyID).queue();
            });
            event.getChannel().retrieveMessageById(surveyID).queue(surveyMessage ->{
                switch (params[1]) {
                    case "YesNo":
                        surveyMessage.addReaction(TICK).queue();
                        surveyMessage.addReaction(CROSS).queue();
                        break;
                    case "custom":
                        String[] emotes = params[2].split(" ");
                        for(String emote : emotes){
                            surveyMessage.addReaction(emote).queue();
                        }
                        //event.getChannel().sendMessage("Not implemented yet.").queue();
                        break;
                    default:
                        event.getChannel().sendMessage("Survey type not valid").queue();
                        break;
                }
            });
        }
    }

    @Override
    public void endSurvey(GuildMessageReceivedEvent event){
        String[] params = event.getMessage().getContentRaw().split(" ");
        if(params.length != 2) {
            event.getChannel().sendMessage("Numero parametri invalido").queue();
        }
        else{
            try {
                String[] ids = params[1].split("/");
                Message surveyMessage = event.getChannel().retrieveMessageById(ids[0]).complete();
                String question = surveyMessage.getContentRaw().substring(8).split(" -- ")[0];
                if(surveyMessage.getAuthor().getId().equals(event.getMessage().getAuthor().getId())) {
                    HashMap<String,Integer> count = MessageReactionHandler.getReactionsCount(ids[1]);
                    event.getChannel().retrieveMessageById(ids[1]).queue(message -> {
                        message.delete().queue(success -> {
                            StringBuilder sBuilder = new StringBuilder();
                            sBuilder.append("@everyone Survey Ended :\n" + question + "" );
                            for (String k : count.keySet()) {
                                sBuilder.append("\n" + k + ": " + count.get(k) + " Users");
                            }
                            event.getChannel().sendMessage(sBuilder.toString()).queue();
                        });
                    });
                }else{
                    event.getChannel().sendMessage("You can't end a survey you didn't create").queue();
                }
            }catch (NullPointerException e){
                event.getChannel().sendMessage("Survey ID not valid").queue();
            }
        }
    }

    @Override
    public void addTelegram(GuildMessageReceivedEvent event) {
        Member m = event.getMember();
        List<Role> roles = m.getRoles();
        boolean isAuthorized = false;
        for (Role r : roles) {
            if (r.getName().equals("Telegram")) {
                isAuthorized = true;
            }
        }
        if (isAuthorized) {
            try {
                String[] params = event.getMessage().getContentRaw().substring(13).split(" -- ");
                if (params.length != 2) {
                    event.getChannel().sendMessage("Numero dei parametri invalido").queue();
                } else {
                    if (tNotifier.addChannel(event.getGuild().getId(), params)) {
                        event.getMessage().addReaction(TICK).queue();
                    } else {
                        event.getMessage().addReaction(CROSS).queue();
                    }
                }
            } catch (IndexOutOfBoundsException e) {
                event.getChannel().sendMessage("Numero dei parametri invalido").queue();
            }
        }else {
            event.getMessage().addReaction(CROSS).queue();
            event.getChannel().sendMessage("Solo chi è autorizzato può aggiungere gruppi").queue();
        }
    }

    @Override
    public void removeTelegram(GuildMessageReceivedEvent event){
        Member m = event.getMember();
        List<Role> roles = m.getRoles();
        boolean isAuthorized = false;
        for (Role r : roles) {
            if (r.getName().equals("Telegram")) {
                isAuthorized = true;
            }
        }
        if(isAuthorized) {
            try {
                String param = event.getMessage().getContentRaw().substring(16);
                if (tNotifier.removeChannel(event.getGuild().getId(), param)) {
                    event.getMessage().addReaction(TICK).queue();
                } else {
                    event.getMessage().addReaction(CROSS).queue();
                }
            } catch (IndexOutOfBoundsException e) {
                event.getChannel().sendMessage("Numero dei parametri invalido").queue();
            }
        }else{
            event.getMessage().addReaction(CROSS).queue();
            event.getChannel().sendMessage("Solo chi è autorizzato può rimuovere gruppi Telegram").queue();
        }
    }

    @Override
    public void wikiResearch(GuildMessageReceivedEvent event){
        // .wiki <query>
        final String DOMAIN_IT = "it.wikipedia.org";
        String query = event.getMessage().getContentRaw().substring(6);
        //String[] params = event.getMessage().getContentRaw().split(" -- ");
        Wiki wiki = new Wiki.Builder().withDomain("it.wikipedia.org").build();
        ArrayList<String> results = wiki.search(query,1);
        if(results.size() == 0){
            event.getChannel().sendMessage("Nessun Risultato trovato").queue();
        }else{
            if(results.size() > 1){
                StringBuilder builder = new StringBuilder();
                builder.append("È stato trovato più di un risultato\n");
                for(String title: results){
                    builder.append(title + "\n");
                }
                event.getChannel().sendMessage(builder.toString()).queue();
            }else{
                String extract = wiki.getTextExtract(results.get(0));
                String URL = "https://" + DOMAIN_IT + "/wiki/" + wiki.search(query, 1).get(0).replace(" ", "_");
                try {
                    event.getChannel().sendMessage(URL + "\n" + extract.substring(0, extract.length() > 1000 ? 1000 : extract.length() - 1) + "...").queue();
                }catch(IndexOutOfBoundsException e){
                    event.getChannel().sendMessage("Impossibile visualizzare l'estratto del testo. L'url della pagina è: " + URL).queue();
                }
            }
        }
    }
}