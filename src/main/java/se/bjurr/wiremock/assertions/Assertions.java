package se.bjurr.wiremock.assertions;

import static com.github.tomakehurst.wiremock.client.WireMock.anyRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.findAll;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;

public class Assertions {

  protected Assertions() {}

  /** Assert on given instance. */
  public static WireMockAssert assertThatWireMockServer(final WireMockServer wireMockServer) {
    return new WireMockAssert(wireMockServer.findAll(RequestPatternBuilder.allRequests()));
  }

  /** Assert on default instance. */
  public static WireMockAssert assertThatWireMockServer() {
    return new WireMockAssert(findAll(anyRequestedFor(anyUrl())));
  }
}
