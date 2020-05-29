package Notifier;

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

public class telegramNotifier extends ListenerAdapter {

    private String token;
    private HashMap<String,String> channels;

    public telegramNotifier(@NotNull String token){
        this.token = token;
        channels = new HashMap<>();
        channels.put("Bot-tana","-1001190296894");
    }

    //public boolean sendMessage(@NotNull String Message, @NotNull String chatId) throws IOException, InterruptedException{
    public boolean sendMessage(@NotNull String[] args, GuildMessageReceivedEvent event)throws Exception{
        String[] elements = this.parse(args);
        String chatId = elements[1];
        String Message = elements[0];

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .version(HttpClient.Version.HTTP_2)
                .build();

        UriBuilder builder = UriBuilder
                .fromUri("https://api.telegram.org")
                .path("/{token}/sendMessage")
                .queryParam("chat_id",channels.get(chatId))
                .queryParam("text", "Inviato da " + event.getAuthor().getName() + " : " + Message);

        HttpRequest request = HttpRequest
                .newBuilder()
                .GET()
                .uri(builder.build("bot"+token))
                .timeout(Duration.ofSeconds(5))
                .build();

        HttpResponse<String> response = client
                .send(request,HttpResponse.BodyHandlers.ofString());

        System.out.println(response.statusCode());
        System.out.println(response.body());
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

    public void listChannels(GuildMessageReceivedEvent event){
        String s = "";
        int i = 1;
        for(String chan : channels.keySet()){
            s += (i + ") " + chan + "\n");
            ++i;
        }
        event.getChannel().sendMessage(s).queue();
    }
}
