package namedEntities;

import java.util.List;

public class EventEntity extends NamedEntity {
    private final String location;
    private final String date;

    public EventEntity(String label, List<String> topics, List<String> keywords,
                       String location, String date) {
        super(label, topics, keywords);
        this.location = location;
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    public String getCategoryName() {
        return "EVENT";
    }
}
