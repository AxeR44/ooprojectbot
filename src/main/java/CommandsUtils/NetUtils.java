package CommandsUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;

public class NetUtils {

    private static final String BASE_URL = "https://youtube.com/watch?v=";

    public static boolean isValidURL(String param){
        try{
            new URL(param);
            return true;
        }catch(MalformedURLException e){
            return false;
        }
    }

    public static String youtubeSearch(String query) throws Exception{
        JSONArray arr = YouTubeSearch.youtubeSearch(query).getJSONArray("items");
        if(!arr.isEmpty()) {
            JSONObject obj = arr.getJSONObject(0);
            JSONObject snippet = obj.getJSONObject("snippet");
            return BASE_URL + obj.getJSONObject("id").getString("videoId");
            //event.getChannel().sendMessage(snippet.getString("title") + "\n" + "https://youtube.com/watch?v=" + obj.getJSONObject("id").getString("videoId")).queue();
        }else{
            //event.getChannel().sendMessage("No result found").queue();
            return null;
        }
    }
}
