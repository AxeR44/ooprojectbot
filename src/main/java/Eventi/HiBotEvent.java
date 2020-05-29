package Eventi;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.InputStream;

public class HiBotEvent{

    public void handle(GuildMessageReceivedEvent event){
        if(event.getAuthor().isBot()) return;
        String content = event.getMessage().getContentRaw();

        if(content.equals("Hi")){
            MessageChannel channel = event.getChannel();
            channel.sendMessage("Hii!").queue();
        }
        if(content.equals("I love you")){
            MessageChannel channel = event.getChannel();
            channel.sendMessage("I love you too :heart:").queue();
        }
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