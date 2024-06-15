package namedEntities;

import java.util.List;

public class OtherEntity extends NamedEntity {
    public OtherEntity(String label, List<String> topics, List<String> keywords) {
        super(label, topics, keywords);
    }

    public String getCategoryName() {
        return "OTHER";
    }
}
