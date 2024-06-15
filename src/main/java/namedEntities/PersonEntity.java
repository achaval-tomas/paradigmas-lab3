package namedEntities;

import java.util.List;

public class PersonEntity extends NamedEntity {
    private final String name;

    public PersonEntity(String label, List<String> topics, List<String> keywords,
                        String name) {
        super(label, topics, keywords);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getCategoryName() {
        return "PERSON";
    }
}
