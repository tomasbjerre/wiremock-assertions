package se.bjurr.wiremock.assertions.internal;

import static java.lang.Character.isWhitespace;
import static java.util.Objects.requireNonNull;

public class StringsUtils {
  private static final String EMPTY_STRING = "";

  public static boolean areEqualIgnoringWhitespace(
      final CharSequence actual, final CharSequence expected) {
    if (actual == null) {
      return expected == null;
    }
    checkCharSequenceIsNotNull(expected);
    return removeAllWhitespaces(actual).equals(removeAllWhitespaces(expected));
  }

  public static void checkCharSequenceIsNotNull(final CharSequence sequence) {
    requireNonNull(sequence, "The char sequence to look for should not be null");
  }

  public static String removeAllWhitespaces(final CharSequence toBeStripped) {
    final StringBuilder result = new StringBuilder(toBeStripped.length());
    for (int i = 0; i < toBeStripped.length(); i++) {
      final char c = toBeStripped.charAt(i);
      if (isWhitespace(c)) {
        continue;
      }
      result.append(c);
    }
    return result.toString();
  }

  public static String normalizeNewlines(final CharSequence charSequence) {
    return charSequence != null ? charSequence.toString().replace("\r\n", "\n") : null;
  }

  public static String removeNewLines(final CharSequence text) {
    final String normalizedText = normalizeNewlines(text);
    return normalizedText.replace("\n", EMPTY_STRING);
  }
}
