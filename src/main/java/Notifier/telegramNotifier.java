package Notifier;

import com.iwebpp.crypto.TweetNaclFast;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import javax.ws.rs.core.UriBuilder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;

public class telegramNotifier{

    private String token;
    private final HashMap<String, HashMap<String, String>> channels;

    public telegramNotifier(@NotNull String token){
        this.token = token;
        channels = new HashMap<>();
    }

    public boolean sendMessage(@NotNull String[] args, GuildMessageReceivedEvent event)throws Exception{
        String[] elements = this.parse(args);
        String chatName = elements[1];
        String Message = elements[0];
        UriBuilder builder;
        HashMap<String, String> guildChannels = channels.get(event.getGuild().getId());
        String chatId;
        if(guildChannels == null){
            throw new Exception("This guild channels no telegram groups");
        }
        chatId = guildChannels.get(chatName);
        if(chatId == null){
            throw new Exception("This guild channels no telegram groups");
        }

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .version(HttpClient.Version.HTTP_2)
                .build();

        synchronized (channels){
            builder = UriBuilder
                    .fromUri("https://api.telegram.org")
                    .path("/{token}/sendMessage")
                    .queryParam("chat_id",chatId)
                    .queryParam("text", "Inviato da " + event.getAuthor().getName() + " : " + Message);
        }

        HttpRequest request = HttpRequest
                .newBuilder()
                .GET()
                .uri(builder.build("bot"+token))
                .timeout(Duration.ofSeconds(5))
                .build();

        HttpResponse<String> response = client
                .send(request,HttpResponse.BodyHandlers.ofString());

        return response.statusCode() == 200;
    }

    private String[] parse(String[] args) throws Exception{
        String temp = "";
        boolean flag = false;
        int i = 1;
        ArrayList<String> l = new ArrayList<>();
        l.add(args[0]);
        while(i < args.length && !flag){
            if(!args[i].equals("--")){
                temp +=(" "+args[i]);
                ++i;
            }else{
                l.add(temp);
                flag = true;
            }
        }
        if(!flag || i == args.length || args.length - i -1 != 1) {
            throw new Exception("Numero di parametri invalido");
        }
        l.add(args[i+1]);
        return new String[]{l.get(1),l.get(2)};
    }

    public synchronized void listChannels(GuildMessageReceivedEvent event){
        String s = "";
        int i = 1;
        HashMap<String, String> guildChannels = null;
        if(channels.size() > 0){
            guildChannels = channels.get(event.getGuild().getId());
            if(guildChannels != null && guildChannels.size() > 0) {
                for (String chan : guildChannels.keySet()) {
                    s += (i + ") " + chan + "\n");
                    ++i;
                }
                event.getChannel().sendMessage(s).queue();
            }else{
                event.getChannel().sendMessage("Non ci sono canali telegram disponibili").queue();
            }
        }else{
            event.getChannel().sendMessage("Non ci sono canali telegram disponibili").queue();
        }
    }

    public synchronized boolean addChannel(String guildID, String[] params){
        if(!channels.containsKey(guildID)){
            channels.put(guildID, new HashMap<>());
        }
        HashMap<String, String> guildChannels = channels.get(guildID);
        if(guildChannels.containsKey(params[0]) || guildChannels.containsValue(params[1])){
            return false;
        }
        guildChannels.put(params[0],params[1]);
        channels.replace(guildID, guildChannels);
        return true;
    }


    public synchronized boolean removeChannel(String guildID, String channelName){
        HashMap<String, String> guildChannels = channels.get(guildID);
        if(guildChannels == null){
            return false;
        }
        guildChannels.remove(channelName);
        channels.replace(guildID, guildChannels);
        return true;
    }
}
