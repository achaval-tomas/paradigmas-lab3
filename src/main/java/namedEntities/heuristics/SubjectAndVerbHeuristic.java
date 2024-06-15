package namedEntities.heuristics;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SubjectAndVerbHeuristic implements Heuristic {
    public List<String> extractCandidates(String text) {
        List<String> candidates = new ArrayList<>();

        text = text.replaceAll("[-+.^:,\"]", "");
        text = Normalizer.normalize(text, Normalizer.Form.NFC);

        Pattern pattern = Pattern.compile("(?<name>([A-Z][A-Za-z]+)(?:\\s[A-Z][A-Za-z]*)*)(?:\\sse)?(?:\\s[a-z]+[áéíóú])");

        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            candidates.add(matcher.group("name"));
        }
        return candidates;
    }

    @Override
    public String getShortName() {
        return "SAV";
    }

    @Override
    public String getLongName() {
        return "Subject and Verb";
    }

    @Override
    public String getDescription() {
        return "match subjects that are followed by verbs.";
    }
}
