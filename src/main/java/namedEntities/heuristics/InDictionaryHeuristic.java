package namedEntities.heuristics;

import namedEntities.NamedEntitiesDictionary;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This heuristic works by checking if a word is one of the manually
 * specified keywords inside dictionary.json.
 */
public class InDictionaryHeuristic implements Heuristic {
    // This constant controls up to how many words will be used to try to match a keyword.
    // For example, 'Buenos Aires' has two words, while 'Copa Libertadores de América' has four.
    // e.g, if this constant is 3, then we would never match 'Copa Libertadores de América'.
    private static final int MAX_WORDS_PER_CANDIDATE = 5;

    private final NamedEntitiesDictionary dictionary;

    public InDictionaryHeuristic(NamedEntitiesDictionary dictionary) {
        this.dictionary = dictionary;
    }

    public List<String> extractCandidates(String text) {
        text = Normalizer.normalize(text, Normalizer.Form.NFD);
        text = text.replaceAll("\\p{M}", "");

        Pattern pattern = Pattern.compile("[A-Za-z]+");
        Matcher matcher = pattern.matcher(text);
        var matches = matcher.results().map(MatchResult::group).toList();

        List<String> candidates = new ArrayList<>();

        for (int i = 0; i < matches.size(); i++) {
            var candidateBuilder = new StringBuilder();

            for (int j = 0; j < MAX_WORDS_PER_CANDIDATE && i + j < matches.size(); j++) {
                if (!candidateBuilder.isEmpty()) {
                    candidateBuilder.append(" ");
                }
                candidateBuilder.append(matches.get(i + j));

                var candidate = candidateBuilder.toString();
                // Check if candidate is a keyword.
                if (dictionary.containsByKeywordNormalized(candidate)) {
                    // If it is, then add the candidate to candidates and
                    // go to the next match.
                    candidates.add(candidate);
                    break;
                }
            }
        }

        return candidates;
    }

    @Override
    public String getShortName() {
        return "ID";
    }

    @Override
    public String getLongName() {
        return "In Dictionary";
    }

    @Override
    public String getDescription() {
        return "match words listed as keywords in 'dictionary.json'.";
    }
}
