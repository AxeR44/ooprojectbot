package CommandsUtils;


import Main.ProvaBot;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import org.json.JSONObject;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class YouTubeSearch {
    private static final String APPLICATION_NAME = "OOProjectBot";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

    private static YouTube getService()throws GeneralSecurityException, IOException{
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        return new YouTube.Builder(httpTransport, JSON_FACTORY, null)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public static JSONObject youtubeSearch(String query) throws GeneralSecurityException, IOException, GoogleJsonResponseException {
        YouTube ytService = getService();
        YouTube.Search.List request = ytService.search()
                .list("snippet");
        SearchListResponse response = request.setKey(new ProvaBot().getGoogleAPIK())
                .setMaxResults(25L)
                .setQ(query)
                .setVideoType("any")
                .execute();
        System.out.println(response.toString());
        return new JSONObject(response.toString());
    }
}
