# WireMock Assertions

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/se.bjurr.wiremock/wiremock-assertions/badge.svg)](https://maven-badges.herokuapp.com/maven-central/se.bjurr.wiremock/wiremock-assertions)

Fluent assertions for [WireMock](https://github.com/wiremock).

## Usage

```java
assertThatWireMockServer()
  .withMethod(GET)
  .withPath("/some-path")
  .withHeader("header1", "value1")
  .withCookie("cookie1", "value1")
  .withBody("""
  {
    "request_attribute1": "value1"
  }
  """)
  .wasInvoked()
```

And friendly failure messages like:

```text
Was not invoked with path '/some-path' but with other paths:
/some-other-path-1
/some-other-path-2
```

There are more examples [here](src/test/java/test/testcases).
