package utils;

import namedEntities.NamedEntity;

import java.util.List;

public interface StatisticsPrinter {
    void printStatsFor(List<NamedEntity> namedEntities);
}
