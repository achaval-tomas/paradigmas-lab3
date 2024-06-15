package namedEntities;

import utils.StringUtils;

import java.util.HashMap;
import java.util.List;

public class NamedEntitiesDictionary {
    private final HashMap<String, NamedEntity> namedEntitiesByKeywordsNormalized;

    public NamedEntitiesDictionary(List<NamedEntity> namedEntities) {
        namedEntitiesByKeywordsNormalized = new HashMap<>();

        for (NamedEntity namedEntity : namedEntities) {
            for (String keyword : namedEntity.getKeywords()) {
                namedEntitiesByKeywordsNormalized.put(StringUtils.simplify(keyword), namedEntity);
            }
        }
    }

    public boolean containsByKeywordNormalized(String keyword) {
        return namedEntitiesByKeywordsNormalized.containsKey(StringUtils.simplify(keyword));
    }

    public NamedEntity getByKeywordNormalized(String keyword) {
        return namedEntitiesByKeywordsNormalized.get(StringUtils.simplify(keyword));
    }
}
