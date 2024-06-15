package namedEntities.heuristics;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This heuristic works by checking if a word is in a spanish dictionary.
 * If it isn't, then it's likely a named entity.
 */
public class NotInDictionaryHeuristic implements Heuristic {
    private final HashSet<String> dictionary;
    private final HashSet<String> prefixes;

    public NotInDictionaryHeuristic() throws IOException {
        dictionary = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/spanish_dict.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String word = line.trim();
                if (word.isEmpty()) {
                    continue;
                }

                word = Normalizer.normalize(word, Normalizer.Form.NFD);
                word = word.replaceAll("\\p{M}", "");
                dictionary.add(word);
                dictionary.add(word + "es");
                dictionary.add(word + "s");
            }
        }

        prefixes = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/spanish_prefixes.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String prefix = line.trim().replace("-", "");
                if (prefix.isEmpty()) {
                    continue;
                }

                prefix = Normalizer.normalize(prefix, Normalizer.Form.NFD);
                prefix = prefix.replaceAll("\\p{M}", "");
                prefixes.add(prefix);
            }
        }
    }

    public List<String> extractCandidates(String text) {
        text = Normalizer.normalize(text, Normalizer.Form.NFD);
        text = text.replaceAll("\\p{M}", "");

        Pattern pattern = Pattern.compile("(?:[A-Za-z]+ ?)+");
        Matcher matcher = pattern.matcher(text);

        List<String> candidates = new ArrayList<>();

        while (matcher.find()) {
            String consecutiveWords = matcher.group();

            var candidateParts = new ArrayList<String>();
            for (String word : consecutiveWords.split(" ")) {
                if (isNamedEntity(word)) {
                    candidateParts.add(word);
                } else {
                    if (!candidateParts.isEmpty()) {
                        var candidate = String.join(" ", candidateParts);
                        candidateParts.clear();
                        candidates.add(candidate);
                    }
                }
            }

            if (!candidateParts.isEmpty()) {
                var candidate = String.join(" ", candidateParts);
                candidateParts.clear();
                candidates.add(candidate);
            }
        }

        return candidates;
    }

    @Override
    public String getShortName() {
        return "NID";
    }

    @Override
    public String getLongName() {
        return "Not in Dictionary";
    }

    @Override
    public String getDescription() {
        return "match words not found in a common spanish dictionary.";
    }

    private boolean isNamedEntity(String word) {
        word = word.toLowerCase();

        if (dictionary.contains(word)) {
            return false;
        }

        for (int i = 1; i <= word.length(); i++) {
            var prefix = word.substring(0, i);
            var actualWord = word.substring(i);
            if (prefixes.contains(prefix) && dictionary.contains(actualWord)) {
                return false;
            }
        }

        return true;
    }
}
