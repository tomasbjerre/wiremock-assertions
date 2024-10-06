package se.bjurr.wiremock.assertions;

import static se.bjurr.wiremock.assertions.internal.LoggedRequestUtils.getUniqueAndSorted;
import static se.bjurr.wiremock.assertions.internal.LoggedRequestUtils.withoutDuplicateNewlines;
import static se.bjurr.wiremock.assertions.internal.StringsUtils.areEqualIgnoringWhitespace;
import static se.bjurr.wiremock.assertions.internal.StringsUtils.removeNewLines;

import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.RequestMethod;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import se.bjurr.wiremock.assertions.internal.LoggedRequestUtils;

public class WireMockAssert {

  public interface LoggedRequestFilter {
    void apply();
  }

  private final List<LoggedRequestFilter> filters;
  private List<LoggedRequest> actual;
  private boolean shouldBeInvoked;

  WireMockAssert(final List<LoggedRequest> actual) {
    this.filters = new ArrayList<>();
    this.actual = actual;
  }

  public WireMockAssert withPath(final String path) {
    this.filters.add(
        () -> {
          final String shouldHaveBeenInvokedMessage = "Was not invoked with path '" + path + "'.";
          final String shouldHaveBeenInvoked =
              "Was invoked with paths:\n" + getUniqueAndSorted(this.actual, LoggedRequest::getUrl);
          this.actual = this.actual.stream().filter(it -> it.getUrl().equals(path)).toList();
          this.throwErrorIfNotRight(shouldHaveBeenInvokedMessage, shouldHaveBeenInvoked);
        });
    return this;
  }

  public WireMockAssert withMethod(final RequestMethod method) {
    this.filters.add(
        () -> {
          final String shouldHaveBeenInvokedMessage =
              "Was not invoked with method '" + method + "'";
          final String shouldHaveBeenInvoked =
              "Was invoked with methods:\n"
                  + getUniqueAndSorted(this.actual, LoggedRequest::getMethod);
          this.actual = this.actual.stream().filter(it -> it.getMethod().equals(method)).toList();
          this.throwErrorIfNotRight(shouldHaveBeenInvokedMessage, shouldHaveBeenInvoked);
        });
    return this;
  }

  public WireMockAssert withHeader(final String name) {
    this.filters.add(
        () -> {
          final String shouldHaveBeenInvokedMessage = "Was not invoked with header '" + name + "'";
          final List<String> allHeadersList =
              LoggedRequestUtils.getAllHeaders(this.actual).stream()
                  .map(HttpHeader::getKey)
                  .toList();
          final String headers = LoggedRequestUtils.toUniqueSorted(allHeadersList);
          final String shouldHaveBeenInvoked = "Was invoked with headers:\n" + headers;
          this.actual = this.actual.stream().filter(it -> it.getHeader(name) != null).toList();
          this.throwErrorIfNotRight(shouldHaveBeenInvokedMessage, shouldHaveBeenInvoked);
        });
    return this;
  }

  public WireMockAssert withHeader(final String name, final String value) {
    if (this.shouldBeInvoked) {
      this.withHeader(name); // Gives prettier error
    }
    this.filters.add(
        () -> {
          final String shouldHaveBeenInvokedMessage =
              "Was not invoked with header '" + name + "' and value '" + value + "'";
          final String headers =
              LoggedRequestUtils.toUniqueSorted(
                  LoggedRequestUtils.getAllHeaders(this.actual).stream()
                      .filter(it -> it.key().equals(name))
                      .map(
                          it ->
                              it.key()
                                  + ": "
                                  + it.values().stream().collect(Collectors.joining(",")))
                      .sorted()
                      .toList());
          final String shouldHaveBeenInvoked = "Was invoked with header values:\n" + headers;
          this.actual =
              this.actual.stream().filter(it -> it.getHeader(name).equals(value)).toList();
          this.throwErrorIfNotRight(shouldHaveBeenInvokedMessage, shouldHaveBeenInvoked);
        });
    return this;
  }

  public WireMockAssert withCookie(final String name) {
    this.filters.add(
        () -> {
          final String shouldHaveBeenInvokedMessage = "Was not invoked with cookie '" + name + "'";
          final List<String> allCookiesList =
              LoggedRequestUtils.getAllCookieNames(this.actual).stream().toList();
          final String cookies = LoggedRequestUtils.toUniqueSorted(allCookiesList);
          final String shouldHaveBeenInvoked = "Was invoked with cookies:\n" + cookies;
          this.actual =
              this.actual.stream().filter(it -> it.getCookies().containsKey(name)).toList();
          this.throwErrorIfNotRight(shouldHaveBeenInvokedMessage, shouldHaveBeenInvoked);
        });
    return this;
  }

  public WireMockAssert withCookie(final String name, final String value) {
    if (this.shouldBeInvoked) {
      this.withCookie(name); // Gives prettier error
    }
    this.filters.add(
        () -> {
          final String shouldHaveBeenInvokedMessage =
              "Was not invoked with cookie '" + name + "' and value '" + value + "'";
          final String cookieValues =
              LoggedRequestUtils.toUniqueSorted(
                  this.actual.stream()
                      .map(it -> it.getCookies().get(name))
                      .flatMap(it -> it.values().stream())
                      .toList());
          final String shouldHaveBeenInvoked = "Was invoked with cookie values:\n" + cookieValues;
          this.actual =
              this.actual.stream()
                  .filter(
                      it ->
                          it.getCookies().containsKey(name)
                              && it.getCookies().get(name).getValues().contains(value))
                  .toList();
          this.throwErrorIfNotRight(shouldHaveBeenInvokedMessage, shouldHaveBeenInvoked);
        });
    return this;
  }

  /** Will ignore whitespace and newlines. */
  public WireMockAssert withBody(final String body) {
    this.filters.add(
        () -> {
          final String shouldHaveBeenInvokedMessage = "Was not invoked with body:\n" + body + "\n";
          final String shouldHaveBeenInvoked =
              LoggedRequestUtils.getBodiesFailureMessage(this.actual, body);
          this.actual =
              this.actual.stream()
                  .filter(it -> areEqualIgnoringWhitespace(it.getBodyAsString(), body))
                  .toList();
          this.throwErrorIfNotRight(
              withoutDuplicateNewlines(shouldHaveBeenInvokedMessage),
              withoutDuplicateNewlines(shouldHaveBeenInvoked));
        });
    return this;
  }

  /** Will ignore whitespace and newlines. */
  public WireMockAssert withBodyIgnoringWhitespace(final String body) {
    return this.withBody(body);
  }

  /** Will ignore newlines. */
  public WireMockAssert withBodyIgnoringNewlines(final String body) {
    this.filters.add(
        () -> {
          final String shouldHaveBeenInvokedMessage =
              "Was not invoked with body:\n\n" + body + "\n\n";
          final String shouldHaveBeenInvoked =
              LoggedRequestUtils.getBodiesFailureMessage(this.actual, body);
          this.actual =
              this.actual.stream()
                  .filter(it -> removeNewLines(it.getBodyAsString()).equals(removeNewLines(body)))
                  .toList();
          this.throwErrorIfNotRight(
              withoutDuplicateNewlines(shouldHaveBeenInvokedMessage),
              withoutDuplicateNewlines(shouldHaveBeenInvoked));
        });
    return this;
  }

  /** Find body matching exactly. */
  public WireMockAssert withBodyExact(final String body) {
    this.filters.add(
        () -> {
          final String shouldHaveBeenInvokedMessage =
              "Was not invoked with body:\n\n" + body + "\n\n";
          final String shouldHaveBeenInvoked =
              LoggedRequestUtils.getBodiesFailureMessage(this.actual, body);
          this.actual =
              this.actual.stream().filter(it -> it.getBodyAsString().equals(body)).toList();
          this.throwErrorIfNotRight(shouldHaveBeenInvokedMessage, shouldHaveBeenInvoked);
        });
    return this;
  }

  public WireMockAssert withBodyBase64(final String body) {
    this.filters.add(
        () -> {
          final String shouldHaveBeenInvoked = "Was not invoked with body:\n\n" + body + "\n\n";
          final String bodies =
              this.actual.stream()
                  .map(it -> "Path: " + it.getUrl() + "\nBody:\n" + it.getBodyAsBase64())
                  .collect(Collectors.joining("\n\n"));
          final String allInvocationsMessage = "Was invoked with bodies:\n\n" + bodies;
          this.actual =
              this.actual.stream().filter(it -> it.getBodyAsBase64().equals(body)).toList();
          this.throwErrorIfNotRight(shouldHaveBeenInvoked, allInvocationsMessage);
        });
    return this;
  }

  public void wasNotInvoked() {
    this.shouldBeInvoked = false;
    this.assertInvoked();
  }

  public void wasInvoked() {
    this.shouldBeInvoked = true;
    this.assertInvoked();
  }

  private void assertInvoked() {
    if (this.filters.isEmpty()) {
      this.throwErrorIfNotRight("Was not invoked", "Was invoked:\n" + this.actual);
    }
    for (final LoggedRequestFilter filter : this.filters) {
      filter.apply();
    }
  }

  private void throwErrorIfNotRight(final String wasNotInvokedWith, final String wasInvokedWith) {
    if (this.shouldBeInvoked && this.actual.isEmpty()) {
      throw new AssertionError(wasNotInvokedWith + "\n" + wasInvokedWith);
    }
    if (!this.shouldBeInvoked && !this.actual.isEmpty()) {
      throw new AssertionError(wasInvokedWith);
    }
  }
}
