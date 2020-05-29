import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
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
            JDA jda = JDABuilder.createDefault(new ProvaBot().getAPIK()).build();
            jda.addEventListener(new Listener());
        }catch (LoginException e) {
            e.printStackTrace();
        }
    }
}