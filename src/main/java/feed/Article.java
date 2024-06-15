package feed;

public record Article(String title, String description, String pubDate, String link) {
    public void print() {
        System.out.println("Title: " + title);
        System.out.println("Description: " + description);
        System.out.println("Publication date: " + pubDate);
        System.out.println("Link: " + link);
        System.out.println("*".repeat(80));
    }
}
