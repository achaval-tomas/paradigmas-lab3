package namedEntities.heuristics;

import java.io.Serializable;
import java.util.List;

public interface Heuristic extends Serializable {
    List<String> extractCandidates(String text);

    String getShortName();

    String getLongName();

    String getDescription();
}
