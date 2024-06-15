package utils;

import namedEntities.heuristics.Heuristic;

import java.util.List;

public record Config(
        boolean printFeed,
        List<FeedData> feedsData,
        Heuristic heuristic, // The heuristic to use for computing named entities, or null to skip computing.
        StatisticsFormat statsFormat
) {
}
