package namedEntities;

import utils.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class NamedEntitiesDictionary implements Serializable {
    record NamedEntityEntry(int index, NamedEntity entity) implements Serializable {
    }

    private final List<NamedEntity> namedEntities;
    private final HashMap<String, NamedEntityEntry> namedEntitiesByKeywordsNormalized;

    public NamedEntitiesDictionary(List<NamedEntity> namedEntities) {
        this.namedEntities = namedEntities;
        this.namedEntitiesByKeywordsNormalized = new HashMap<>();

        int index = 0;
        for (NamedEntity namedEntity : namedEntities) {
            for (String keyword : namedEntity.getKeywords()) {
                var entry = new NamedEntityEntry(index, namedEntity);
                namedEntitiesByKeywordsNormalized.put(StringUtils.simplify(keyword), entry);
            }
            index += 1;
        }
    }

    public boolean containsByKeywordNormalized(String keyword) {
        return namedEntitiesByKeywordsNormalized.containsKey(StringUtils.simplify(keyword));
    }

    public int getIndexByKeywordNormalized(String keyword) {
        var entry = namedEntitiesByKeywordsNormalized.get(StringUtils.simplify(keyword));
        if (entry != null) {
            return entry.index;
        } else {
            return -1;
        }
    }

    public NamedEntity getByIndex(int index) {
        return namedEntities.get(index);
    }
}
