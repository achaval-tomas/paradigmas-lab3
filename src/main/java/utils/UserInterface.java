package utils;

import namedEntities.heuristics.Heuristic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserInterface {
    private final List<Option> options;

    public UserInterface() {
        options = new ArrayList<>();
        options.add(new Option("-h", "--help", 0));
        options.add(new Option("-f", "--feed", 1));
        options.add(new Option("-ne", "--named-entity", 1));
        options.add(new Option("-pf", "--print-feed", 0));
        options.add(new Option("-sf", "--stats-format", 1));
    }

    private static void printHelp(List<FeedData> feedsData, List<Heuristic> heuristics) {
        System.out.println("Usage: make run ARGS=\"[OPTION]\"");
        System.out.println("Options:");
        System.out.println("  -h, --help: Show this help message and exit");
        System.out.println("  -f, --feed <feedKey>:                Fetch and process the feed with");
        System.out.println("                                       the specified key");
        System.out.println("                                       Available feed keys are: ");
        for (FeedData feedData : feedsData) {
            System.out.println("                                       " + feedData.label());
        }
        System.out.println("  -ne, --named-entity <heuristicName>: Use the specified heuristic to extract");
        System.out.println("                                       named entities");

        System.out.println("        Available heuristic names are: ");
        for (Heuristic h : heuristics) {
            System.out.printf("                %s, \"%s\": %s\n", h.getShortName(), h.getLongName(), h.getDescription());
        }

        System.out.println("  -pf, --print-feed:                   Print the fetched feed");
        System.out.println("  -sf, --stats-format <format>:        Print the stats in the specified format");
        System.out.println("                                       Available formats are: ");
        System.out.println("                                       cat: Category-wise stats");
        System.out.println("                                       topic: Topic-wise stats");
    }

    public Config handleInput(String[] args, List<FeedData> feedsData, List<Heuristic> heuristics) {
        HashMap<String, String> optionDict = parseOptions(args);

        if (optionDict.containsKey("-h")) {
            printHelp(feedsData, heuristics);
            System.exit(0);
        }

        boolean printFeed = optionDict.containsKey("-pf") || !optionDict.containsKey("-ne");
        List<FeedData> chosenFeeds = getChosenFeeds(optionDict, feedsData);
        Heuristic heuristic = getHeuristic(optionDict, heuristics);
        StatisticsFormat statsFormat = getStatsFormat(optionDict);

        return new Config(printFeed, chosenFeeds, heuristic, statsFormat);
    }

    private HashMap<String, String> parseOptions(String[] args) {
        var optionDict = new HashMap<String, String>();

        for (int i = 0; i < args.length; i++) {
            for (Option option : options) {
                if (option.name().equals(args[i]) || option.longName().equals(args[i])) {
                    if (option.numValues() == 0) {
                        optionDict.put(option.name(), null);
                    } else {
                        if (i + 1 < args.length && !args[i + 1].startsWith("-")) {
                            optionDict.put(option.name(), args[i + 1]);
                            i++;
                        } else {
                            System.out.println("Invalid inputs");
                            System.exit(1);
                        }
                    }
                }
            }
        }

        return optionDict;
    }

    private static List<FeedData> getChosenFeeds(HashMap<String, String> optionDict, List<FeedData> feedsData) {
        List<FeedData> chosenFeeds = new ArrayList<>();
        String chosenFeedKey = optionDict.get("-f");
        if (chosenFeedKey == null) {
            chosenFeeds.addAll(feedsData);
        } else {
            var chosenFeed = feedsData.stream()
                    .filter(feed -> feed.label().equals(chosenFeedKey))
                    .findFirst();

            if (chosenFeed.isEmpty()) {
                System.out.printf("Feed '%s' not found\n", chosenFeedKey);
                System.out.println("Use --help to check for available feeds.");
                System.exit(1);
            }

            chosenFeeds.add(chosenFeed.get());
        }
        return chosenFeeds;
    }

    private static Heuristic getHeuristic(HashMap<String, String> optionDict, List<Heuristic> heuristics) {
        Heuristic heuristic = null;
        if (optionDict.containsKey("-ne")) {
            String heuristicName = optionDict.get("-ne").trim().toLowerCase();

            for (Heuristic h : heuristics) {
                if (h.getShortName().toLowerCase().equals(heuristicName)
                        || h.getLongName().toLowerCase().equals(heuristicName)) {
                    heuristic = h;
                    break;
                }
            }

            if (heuristic == null) {
                System.out.println("The provided heuristic does not exist");
                System.out.println("Use --help to check for available heuristics.");
                System.exit(1);
            }
        }
        return heuristic;
    }

    private StatisticsFormat getStatsFormat(Map<String, String> optionDict) {
        StatisticsFormat statsFormat = StatisticsFormat.Category;
        if (optionDict.containsKey("-sf")) {
            String statsFormatOpt = optionDict.get("-sf");
            switch (statsFormatOpt) {
                case "cat":
                    break;
                case "topic":
                    statsFormat = StatisticsFormat.Topic;
                    break;
                default:
                    System.out.printf("Format '%s' does not exist\n", statsFormatOpt);
                    System.out.println("Use --help to check for available formats.");
                    System.exit(1);
            };
        }

        return statsFormat;
    }
}
