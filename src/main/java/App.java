import feed.Article;
import feed.FeedParser;
import namedEntities.NamedEntitiesDictionary;
import namedEntities.NamedEntity;
import namedEntities.heuristics.*;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.SparkSession;
import utils.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class App {
    private static final String bigdataFilepath = "src/main/resources/data/bigdata.txt";

    public static void main(String[] args) throws Exception {
        var namedEntitiesDict = JSONParser.parseJsonDict("src/main/resources/data/dictionary.json");

        var heuristics = new ArrayList<Heuristic>();
        heuristics.add(new CapitalizedWordHeuristic());
        heuristics.add(new SubjectAndVerbHeuristic());
        heuristics.add(new NotInDictionaryHeuristic());
        heuristics.add(new InDictionaryHeuristic(namedEntitiesDict));

        List<FeedData> feedsData = JSONParser.parseJsonFeedsData("src/main/resources/data/feeds.json");

        UserInterface ui = new UserInterface();
        Config config = ui.handleInput(args, feedsData, heuristics);

        run(namedEntitiesDict, config);
    }

    private static void run(NamedEntitiesDictionary namedEntitiesDict, Config config) throws Exception {
        if (config.feedsData().isEmpty()) {
            System.out.println("No feeds data found");
            return;
        }

        List<Article> articles = extractArticlesFrom(config.feedsData());

        try {
            writeArticlesToFile(articles);
        } catch (IOException e) {
            System.out.println("Error while writing bigdata");
            System.exit(1);
        }

        if (config.printFeed()) {
            printFeed(articles);
        }

        if (config.heuristic() != null) {
            var namedEntities = computeNamedEntities(namedEntitiesDict, config.heuristic());
            printNamedEntitiesStats(config.statsFormat(), namedEntities);
        }
    }

    private static List<Article> extractArticlesFrom(List<FeedData> feedsData) throws Exception {
        List<Article> articles = new ArrayList<>();

        for (FeedData feed : feedsData) {
            String xml;
            try {
                xml = FeedParser.fetchFeed(feed.url());
            } catch (Exception e) {
                System.out.println("Failed to fetch feed with message: " + e.getMessage());
                throw e;
            }
            articles.addAll(FeedParser.parseXML(xml));
        }

        return articles;
    }

    private static void writeArticlesToFile(List<Article> articles) throws IOException {
        var writer = new BufferedWriter(new FileWriter(App.bigdataFilepath));

        for (Article article : articles) {
            writer.append(article.title()).append("\n");
            writer.append(article.description()).append("\n");
        }

        writer.close();
    }

    private static void printFeed(List<Article> articles) {
        System.out.println("Printing feed(s) ");
        for (Article article : articles) {
            article.print();
        }
    }

    private static List<NamedEntity> computeNamedEntities(
            NamedEntitiesDictionary namedEntitiesDict,
            Heuristic heuristic
    ) {
        System.out.printf("Computing named entities using '%s' heuristic.\n", heuristic.getLongName());

        SparkSession spark = SparkSession
                .builder()
                .appName("JavaApp")
                .getOrCreate();

        JavaRDD<String> lines = spark.read().textFile(App.bigdataFilepath).javaRDD();
        List<String> candidates = lines
                .flatMap(line -> heuristic.extractCandidates(line).iterator())
                .collect();

        spark.stop();

        return extractNamedEntities(namedEntitiesDict, candidates);
    }

    private static void printNamedEntitiesStats(StatisticsFormat statsFormat, List<NamedEntity> namedEntities) {
        System.out.println();
        switch (statsFormat) {
            case Category -> printStatsByCategory(namedEntities);
            case Topic -> printStatsByTopic(namedEntities);
        }
        System.out.println("-".repeat(80));
    }

    private static void printStatsByCategory(List<NamedEntity> namedEntities) {
        var statsByCategory = new HashMap<String, HashMap<NamedEntity, Integer>>();
        for (NamedEntity namedEntity : namedEntities) {
            var category = namedEntity.getCategoryName();
            var categoryCounts = statsByCategory.computeIfAbsent(category, k -> new HashMap<>());

            var count = categoryCounts.getOrDefault(namedEntity, 0);
            categoryCounts.put(namedEntity, count + 1);
        }

        for (var category : statsByCategory.keySet()) {
            System.out.printf("Category: %s\n", category);
            var stats = statsByCategory.get(category);
            for (var entity : stats.keySet()) {
                System.out.printf("        %s (%d)\n", entity.getLabel(), stats.get(entity));
            }
        }
    }

    private static void printStatsByTopic(List<NamedEntity> namedEntities) {
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

    private static List<NamedEntity> extractNamedEntities(
            NamedEntitiesDictionary namedEntitiesDict,
            List<String> candidates
    ) {
        var namedEntities = new ArrayList<NamedEntity>();
        for (String candidate : candidates) {
            var entity = namedEntitiesDict.getByKeywordNormalized(candidate);
            if (entity == null) {
                continue;
            }

            namedEntities.add(entity);
        }

        return namedEntities;
    }
}
