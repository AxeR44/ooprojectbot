package Lyrics;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

public class LyricsClient {
    private final Config config = ConfigFactory.load();
    private final HashMap<String, Lyrics> cache = new HashMap<>();
    private final OutputSettings noPrettyPrint = new OutputSettings().prettyPrint(false);
    private final Whitelist newlineWhitelist = Whitelist.none().addTags("br", "p");
    private final Executor executor;
    private final String defaultSource, userAgent;
    private final int timeout;

    /**
     * Constructs a new {@link LyricsClient} using all defaults
     */
    public LyricsClient() {
        this(null);
    }

    public LyricsClient(String defaultSource) {
        this.defaultSource = defaultSource == null ? config.getString("lyrics.default") : defaultSource;
        this.userAgent = config.getString("lyrics.user-agent");
        this.timeout = config.getInt("lyrics.timeout");
        this.executor =  Executors.newCachedThreadPool();
    }

    /**
     * Gets the lyrics for the provided search from the default source. To get lyrics
     * asynchronously, call {@link CompletableFuture#thenAccept(java.util.function.Consumer)}.
     * To block and return lyrics, use {@link CompletableFuture#get()}.
     *
     * @param search the song info to search for
     * @return a {@link CompletableFuture} to access the lyrics. The Lyrics object may be null if no lyrics were found.
     */
    public CompletableFuture<Lyrics> getLyrics(String search) {
        return getLyrics(search, defaultSource);
    }

    /**
     * Gets the lyrics for the provided search from the provided source. To get lyrics
     * asynchronously, call {@link CompletableFuture#thenAccept(java.util.function.Consumer)}.
     * To block and return lyrics, use {@link CompletableFuture#get()}.
     *
     * @param search the song info to search for
     * @param source the source to use (must be defined in config)
     * @return a {@link CompletableFuture} to access the lyrics. The Lyrics object may be null if no lyrics were found.
     */
    public CompletableFuture<Lyrics> getLyrics(String search, String source) {
        final String fsearch = parseSearch(search);
        String cacheKey = source + "||" + search;
        if(cache.containsKey(cacheKey))
            return CompletableFuture.completedFuture(cache.get(cacheKey));
        try {
            String searchUrl = String.format(config.getString("lyrics." + source + ".search.url"), fsearch);
            boolean jsonSearch = config.getBoolean("lyrics." + source + ".search.json");
            String titleSelector = config.getString("lyrics." + source + ".parse.title");
            String authorSelector = config.getString("lyrics." + source + ".parse.author");
            String contentSelector = config.getString("lyrics." + source + ".parse.content");
            return CompletableFuture.supplyAsync(() -> {
                try
                {
                    Document doc;
                    Connection connection = Jsoup.connect(searchUrl).userAgent(userAgent).timeout(timeout).followRedirects(true);
                    if(jsonSearch)
                    {
                        String body = connection.ignoreContentType(true).execute().body();
                        JSONObject json = new JSONObject(body);
                        doc = Jsoup.parse(XML.toString(json));
                    }
                    else
                        doc = connection.get();

                    Elements elements = doc.select("result");
                    Element urlElementMax = null;
                    String lowerSearch = fsearch.toLowerCase();
                    int tmpCount, maxCount = -1;
                    for(Element e : elements){
                        String title = e.select("full_title").text().toLowerCase();
                        tmpCount = 0;
                        for(String str : lowerSearch.split(" ")){
                            if(title.contains(str)){
                                ++tmpCount;
                            }
                        }
                        if(tmpCount > maxCount && tmpCount != 0){
                            urlElementMax = e;
                            maxCount = tmpCount;
                        }else if(tmpCount == maxCount){
                            if(title.length() < urlElementMax.select("title").text().length()){
                                urlElementMax = e;
                            }
                        }
                    }

                    Elements urls = urlElementMax.select("url");

                    String url = "";
                    String imageURL = urlElementMax.selectFirst("song_art_image_thumbnail_url").text();
                    if(jsonSearch) {
                        for (Element e : urls) {
                            if (e.text().endsWith("lyrics")) {
                                url = e.text();
                            }
                        }
                    }
                    else {
                        url = urlElementMax.attr("abs:href");
                    }
                    if(url==null || url.isEmpty()) {
                        return null;
                    }
                    doc = Jsoup.connect(url).userAgent(userAgent).timeout(timeout).get();
                    Lyrics lyrics = new Lyrics(doc.selectFirst(titleSelector).ownText(),
                            doc.selectFirst(authorSelector).ownText(),
                            cleanWithNewlines(doc.selectFirst(contentSelector)),
                            url,
                            source,
                            imageURL);
                    cache.put(cacheKey, lyrics);
                    return lyrics;
                }
                catch(IOException | NullPointerException | JSONException ex)
                {
                    return null;
                }
            }, executor);
        }
        catch(ConfigException ex)
        {
            throw new IllegalArgumentException(String.format("Source '%s' does not exist or is not configured correctly", source));
        }
        catch(Exception ignored)
        {
            return null;
        }
    }

    /**
     * Deletes all the useless infotmation from query string like parenthesis and text like "official music video"
     * or "official audio"
     *
     * @param s input query
     * @return non null {@link String} that corresponds to the parsed query
     */
    private @NotNull String parseSearch(@NotNull String s){
        String[] chars = new String[]{"(", ")", "[" , "]"};
        s = s.toLowerCase();
        for(int i = 0; i < chars.length; i+=2){
            while(s.contains(chars[i]) && s.contains(chars[i+1])){
                int index1 = s.indexOf(chars[i]);
                int index2 = s.indexOf(chars[i+1]);
                if(index1 != -1 && index2 != -1){
                    String sub = s.substring(index1 + 1, index2);
                    if(!sub.contains("ft.") && !sub.contains("featuting") && !sub.contains("feat.")) {
                        s = s.substring(0, index1 - 1) + (index2 == s.length() - 1 ? "" : s.substring(index2 + 1));
                    }else{
                        s = s.substring(0, index1) + sub + (index2 == s.length() - 1 ? "" : s.substring(index2 + 1));
                    }
                }
            }
        }

        String[] params = s.split(" - ");

        String result = "";
        for(String str : params){
            if(!str.contains("official") && !str.contains("audio") && !str.contains("video") && !str.contains("music")) {
                result += str;
                result += " ";
            }
        }
        if(result.endsWith(" ")){
            result = result.substring(0, result.length()-1);
        }
        return result;
    }

    private String cleanWithNewlines(@NotNull Element element) {
        return Jsoup.clean(Jsoup.clean(element.html(), newlineWhitelist), "", Whitelist.none(), noPrettyPrint);
    }
}
