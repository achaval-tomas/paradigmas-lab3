package namedEntities;

import java.util.List;

public class LocationEntity extends NamedEntity {
    private final String name;
    private final float latitude;
    private final float longitude;

    public LocationEntity(String label, List<String> topics, List<String> keywords,
                          String name, float latitude, float longitude) {
        super(label, topics, keywords);
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public float getLatitude() {
        return latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public String getCategoryName() {
        return "LOCATION";
    }
}
