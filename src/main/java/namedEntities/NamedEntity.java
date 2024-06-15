package namedEntities;

import java.util.List;

public abstract class NamedEntity {
    private final String label;
    private final List<String> topics;
    private final List<String> keywords;

    protected NamedEntity(String label, List<String> topics, List<String> keywords) {
        this.label = label;
        this.topics = topics;
        this.keywords = keywords;
    }

    public String getLabel() {
        return label;
    }

    public List<String> getTopics() {
        return topics;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public abstract String getCategoryName();
}
