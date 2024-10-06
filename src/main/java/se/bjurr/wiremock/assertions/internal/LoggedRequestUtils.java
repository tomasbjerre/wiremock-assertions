package se.bjurr.wiremock.assertions.internal;

import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LoggedRequestUtils {
  public static String getUniqueAndSorted(
      final List<LoggedRequest> requests, final Function<LoggedRequest, Object> keyMapper) {
    final Set<Object> unique = toUniqueSet(requests, keyMapper);
    return toSortedStringWithNewlines(unique);
  }

  public static String toUniqueSorted(final List<String> from) {
    final List<String> list =
        from.stream().collect(Collectors.toMap(s -> s, k -> k.toString())).keySet().stream()
            .toList();
    return sortedStringNewlines(list);
  }

  public static Set<Object> toUniqueSet(
      final List<LoggedRequest> requests, final Function<LoggedRequest, Object> keyMapper) {
    return requests.stream().collect(Collectors.toMap(keyMapper, k -> k.toString())).keySet();
  }

  public static String toSortedStringWithNewlines(final Collection<Object> from) {
    final List<String> list = from.stream().map(it -> it.toString()).toList();
    return sortedStringNewlines(list);
  }

  private static String sortedStringNewlines(final List<String> list) {
    return list.stream().sorted().collect(Collectors.joining("\n"));
  }

  public static List<HttpHeader> getAllHeaders(final List<LoggedRequest> actual) {
    return actual.stream()
        .map(LoggedRequest::getHeaders)
        .map(
            it -> {
              return it.all();
            })
        .flatMap(list -> list.stream())
        .collect(Collectors.toList());
  }

  public static List<String> getAllCookieNames(final List<LoggedRequest> actual) {
    return actual.stream()
        .map(LoggedRequest::getCookies)
        .map(it -> it.keySet())
        .flatMap(set -> set.stream())
        .collect(Collectors.toList());
  }

  public static String getBodiesFailureMessage(
      final List<LoggedRequest> actual, final String expected) {
    final String bodies =
        actual.stream()
            .map(it -> "Path: " + it.getUrl() + "\nBody:\n" + it.getBodyAsString())
            .collect(Collectors.joining("\n\n"));
    return "Was invoked with bodies:\n\n" + bodies;
  }

  public static String withoutDuplicateNewlines(final String str) {
    return str.replaceAll("\\r\\n|\\r|\\n", "\n");
  }
}
