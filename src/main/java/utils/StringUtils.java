package utils;

import java.text.Normalizer;

public class StringUtils {
    /**
     * Normalizes {@code s} into its canonical decomposition,
     * removes all accents, converts it to lower case, and returns the result.
     */
    public static String simplify(String s) {
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("\\p{M}", "");
        s = s.toLowerCase();

        return s;
    }
}
