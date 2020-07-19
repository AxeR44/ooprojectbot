package Main;

import EventListener.Listener;
import EventListener.MessageReactionHandler;
import Helpers.OS;
import Notifier.TelegramNotifierAsync;
import Wrappers.ChannelList;
import com.google.inject.internal.asm.$Handle;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.security.auth.login.LoginException;

public class ProvaBot {

    public native String getAPIK();
    public native String getTelegramAPIK();
    public native String getGoogleAPIK();
    private static JDA jda;

    static{
        if(OS.isOSSupported()) {
            System.loadLibrary("ApiKeys");
            ApiContextInitializer.init();
        }else{
            System.exit(-1);
        }
    }

    public static void main(String args[]){
        ChannelList chList = new ChannelList();
        TelegramNotifierAsync tnAsync = new TelegramNotifierAsync(chList);
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        MessageReactionHandler hnd = new MessageReactionHandler();
        try {
            telegramBotsApi.registerBot(tnAsync);
            jda = JDABuilder.createDefault(new ProvaBot().getAPIK())
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .enableIntents(GatewayIntent.GUILD_MEMBERS)
                    .build();
            jda.addEventListener(new Listener(chList, tnAsync, hnd));
            jda.addEventListener(hnd);
        }catch (LoginException | TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static JDA getJda() {
        return jda;
    }
}