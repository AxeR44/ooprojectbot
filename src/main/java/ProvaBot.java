import EventListener.MessageReactionHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;

public class ProvaBot {

    public native String getAPIK();
    public native String getTelegramAPIK();

    /*note: so file must be called lib<libName>.so
    * then call System.loadLibrary("<libName>");
    */
    static{
        System.loadLibrary("ApiKeys");
    }

    public static void main(String args[]){
        try {
            // Creating Bot
            JDA jda = JDABuilder.createDefault(new ProvaBot().getAPIK())
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .build();
            jda.addEventListener(new Listener());
            jda.addEventListener(new MessageReactionHandler());
        }catch (LoginException e) {
            e.printStackTrace();
        }
    }
}