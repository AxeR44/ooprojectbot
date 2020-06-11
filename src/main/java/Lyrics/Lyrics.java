package Lyrics;

public class Lyrics {
    private final String title, author, content, url, source, imageURL;

    protected Lyrics(String title, String author, String content, String url, String source, String imageURL){
        this.title = title;
        this.author = author;
        this.content = content;
        this.url = url;
        this.source = source;
        this.imageURL = imageURL;
    }

    public String getTitle()
    {
        return title;
    }

    public String getAuthor()
    {
        return author;
    }

    public String getContent()
    {
        return content;
    }

    public String getURL()
    {
        return url;
    }

    public String getSource()
    {
        return source;
    }

    public String getImageURL(){
        return this.imageURL;
    }
}
