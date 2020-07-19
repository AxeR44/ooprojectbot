package CommandsUtils;

import javax.ws.rs.core.UriBuilder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Translator {
    private HashMap<String, String> languages;

    public Translator() {
        languages = new HashMap<>();
        languages.put("afrikaans", "af");
        languages.put("irlandese", "ga");
        languages.put("albanese", "sq");
        languages.put("italiano", "it");
        languages.put("arabo", "ar");
        languages.put("giapponese", "ja");
        languages.put("azero", "az");
        languages.put("kannada", "kn");
        languages.put("basco", "eu");
        languages.put("coreano", "ko");
        languages.put("bengalese", "bn");
        languages.put("latino", "la");
        languages.put("bielorusso", "be");
        languages.put("lettone", "lv");
        languages.put("bulgaro", "bg");
        languages.put("lituano", "lt");
        languages.put("catalano", "ca");
        languages.put("macedone", "mk");
        languages.put("cineses", "zh-CN");
        languages.put("malese", "ms");
        languages.put("cineset", "zh-TW");
        languages.put("maltese", "mt");
        languages.put("croato", "hr");
        languages.put("norvegese", "no");
        languages.put("ceco", "cs");
        languages.put("persiano", "fa");
        languages.put("danese", "da");
        languages.put("polacco", "pl");
        languages.put("olandese", "nl");
        languages.put("portoghese", "pt");
        languages.put("inglese", "en");
        languages.put("rumeno", "ro");
        languages.put("esperanto", "eo");
        languages.put("russo", "ru");
        languages.put("estone", "et");
        languages.put("serbo", "sr");
        languages.put("filippino", "tl");
        languages.put("slovacco", "sk");
        languages.put("finlandese", "fi");
        languages.put("sloveno", "sl");
        languages.put("francese", "fr");
        languages.put("spagnolo", "es");
        languages.put("galiziano", "gl");
        languages.put("swahili", "sw");
        languages.put("georgiano", "ka");
        languages.put("svedese", "sv");
        languages.put("tedesco", "de");
        languages.put("tamill", "ta");
        languages.put("greco", "el");
        languages.put("telugu", "te");
        languages.put("gujarati", "gu");
        languages.put("tailandese", "th");
        languages.put("haitiano", "ht");
        languages.put("turco", "tr");
        languages.put("ebraico", "iw");
        languages.put("ucraino", "uk");
        languages.put("hindi", "hi");
        languages.put("urdu", "ur");
        languages.put("ungherese", "hu");
        languages.put("vietnamita", "vi");
        languages.put("islandese", "is");
        languages.put("gallese", "cy");
        languages.put("indonesiano", "id");
        languages.put("yiddish", "yi");

    }

    public String translate(String[] params) throws Exception {
        String[] parsed = parse(params);


        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();

        UriBuilder builder = UriBuilder
                .fromUri("https://script.google.com/macros/s/AKfycbzqAoLgrzMgkz5uZgj9CidYuVPCzWZzEJIkDY0rA_PgrnKMRg/exec")
                .queryParam("q", parsed[0])
                .queryParam("target", languages.get(parsed[2]))
                .queryParam("source", languages.get(parsed[1]));

        HttpRequest request = HttpRequest
                .newBuilder()
                .GET()
                .uri(builder.build())
                .timeout(Duration.ofSeconds(5))
                .build();

        HttpResponse<String> response = client
                .send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.statusCode());
        System.out.println(response.body());
        return response.body();
    }

    private String[] parse(String[] args) throws Exception {
        String temp = "";
        boolean flag = false;
        int i = 1;
        ArrayList<String> l = new ArrayList<>();
        l.add(args[0]);
        while (i < args.length && !flag) {
            if (!args[i].equals("--")) {
                temp += (" " + args[i]);
                ++i;
            } else {
                temp = temp.substring(1);
                l.add(temp);
                flag = true;
            }
        }
        if (!flag || i == args.length || args.length - i - 1 != 2) {
            throw new Exception("Numero di parametri invalido \u267F");
        }
        l.add(args[i + 1]);
        l.add(args[i + 2]);
        return new String[]{l.get(1), l.get(2), l.get(3)};
    }
    public String printLanguages() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> l : languages.entrySet()) {
            sb.append(l.getKey() + " = " + l.getValue() + "\n");
        }
        return sb.toString();
    }
}
