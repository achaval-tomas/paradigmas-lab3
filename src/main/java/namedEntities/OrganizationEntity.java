package namedEntities;

import java.util.List;

public class OrganizationEntity extends NamedEntity {
    private final String orgName;
    private final String type; // ONG, SA, SRL

    public OrganizationEntity(String label, List<String> topics, List<String> keywords,
                              String orgName, String type) {
        super(label, topics, keywords);
        this.orgName = orgName;
        this.type = type;
    }

    public String getOrgName() {
        return orgName;
    }

    public String getType() {
        return type;
    }

    public String getCategoryName() {
        return "ORGANIZATION";
    }
}
