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
            printNamedEntitiesStats(config.statsPrinter(), namedEntities);
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
        System.out.println("Printing feed(s)");
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
        List<NamedEntity> namedEntities = lines
                .flatMap(line -> {
                            var candidates = heuristic.extractCandidates(line);
                            return extractNamedEntities(candidates, namedEntitiesDict).iterator();
                        }
                ).collect();

        spark.stop();

        return namedEntities;
    }

    private static void printNamedEntitiesStats(StatisticsPrinter statsPrinter, List<NamedEntity> namedEntities) {
        System.out.println();
        statsPrinter.printStatsFor(namedEntities);
        System.out.println("-".repeat(80));
    }

    private static List<NamedEntity> extractNamedEntities(
            List<String> candidates,
            NamedEntitiesDictionary namedEntitiesDict
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
