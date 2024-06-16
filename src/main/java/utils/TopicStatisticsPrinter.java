package utils;

import namedEntities.NamedEntity;

import java.util.HashMap;
import java.util.List;

public class TopicStatisticsPrinter implements StatisticsPrinter {
    @Override
    public void printStatsFor(List<NamedEntity> namedEntities) {
        var statsByTopic = new HashMap<String, HashMap<NamedEntity, Integer>>();

        for (NamedEntity namedEntity : namedEntities) {
            var topics = namedEntity.getTopics();

            for (var topic : topics) {
                var categoryCounts = statsByTopic.computeIfAbsent(topic, k -> new HashMap<>());

                var count = categoryCounts.getOrDefault(namedEntity, 0);
                categoryCounts.put(namedEntity, count + 1);
            }
        }

        for (var topic : statsByTopic.keySet()) {
            System.out.printf("Topic: %s\n", topic);
            var stats = statsByTopic.get(topic);
            for (var entity : stats.keySet()) {
                System.out.printf("        %s (%d)\n", entity.getLabel(), stats.get(entity));
            }
        }
    }
}
