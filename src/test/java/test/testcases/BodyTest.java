package test.testcases;

import static se.bjurr.wiremock.assertions.Assertions.assertThatWireMockServer;

import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.RestAssured;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import test.utils.WireMockAssertBaseTest;

class BodyTest extends WireMockAssertBaseTest {
  String requestBody = """
			{
			  "request_attribute1": "value1"
			}
			""";

  @BeforeEach
  public void before() {
    WireMock.stubFor(
        WireMock.post(WireMock.urlEqualTo("/post-200-body"))
            .willReturn(WireMock.ok())
            .withId(UUID.randomUUID()));
    RestAssured.given()
        .body(this.requestBody)
        .when()
        .post("http://localhost:" + this.wireMockServer.port() + "/post-200-body")
        .then()
        .statusCode(200);
  }

  @Test
  void testRequestBody() {
    assertThatWireMockServer().withBody("").wasNotInvoked();

    this.assertFailureMessage(() -> assertThatWireMockServer().withBody("").wasInvoked())
        .isEqualToIgnoringWhitespace(
            """
						Was not invoked with body:


						Was invoked with bodies:

						Path: /post-200-body
						Body:
						{
						  "request_attribute1": "value1"
						}
						""");

    assertThatWireMockServer().withBody(this.requestBody).wasInvoked();

    this.assertFailureMessage(
            () -> assertThatWireMockServer().withBody(this.requestBody).wasNotInvoked())
        .isEqualToIgnoringWhitespace(
            """
						Was invoked with bodies:

						Path: /post-200-body
						Body:
						{
						  "request_attribute1": "value1"
						}
						""");

    assertThatWireMockServer().withBody("{\"request_attribute1\": \"value1\"}").wasInvoked();

    this.assertFailureMessage(
            () ->
                assertThatWireMockServer()
                    .withBody("{\"request_attribute1\": \"value1\"}")
                    .wasNotInvoked())
        .isEqualToIgnoringWhitespace(
            """
						Was invoked with bodies:

						Path: /post-200-body
						Body:
						{
						  "request_attribute1": "value1"
						}
						""");

    assertThatWireMockServer().withBodyIgnoringWhitespace(this.requestBody).wasInvoked();

    this.assertFailureMessage(
            () ->
                assertThatWireMockServer()
                    .withBodyIgnoringWhitespace(this.requestBody)
                    .wasNotInvoked())
        .isEqualToIgnoringWhitespace(
            """
						Was invoked with bodies:

						Path: /post-200-body
						Body:
						{
						  "request_attribute1": "value1"
						}
						""");

    assertThatWireMockServer()
        .withBodyIgnoringWhitespace("{\"request_attribute1\": \"value1\"}")
        .wasInvoked();

    this.assertFailureMessage(
            () ->
                assertThatWireMockServer()
                    .withBodyIgnoringWhitespace("{\"request_attribute1\": \"value1\"}")
                    .wasNotInvoked())
        .isEqualToIgnoringWhitespace(
            """
						Was invoked with bodies:

						Path: /post-200-body
						Body:
						{
						  "request_attribute1": "value1"
						}
						""");

    assertThatWireMockServer().withBodyIgnoringNewlines(this.requestBody).wasInvoked();

    this.assertFailureMessage(
            () ->
                assertThatWireMockServer()
                    .withBodyIgnoringNewlines(this.requestBody)
                    .wasNotInvoked())
        .isEqualToIgnoringWhitespace(
            """
						Was invoked with bodies:

						Path: /post-200-body
						Body:
						{
						  "request_attribute1": "value1"
						}
						""");

    assertThatWireMockServer()
        .withBodyIgnoringNewlines("{\n  \"request_attribute1\": \"value1\"\n}")
        .wasInvoked();

    this.assertFailureMessage(
            () ->
                assertThatWireMockServer()
                    .withBodyIgnoringNewlines("{\n  \"request_attribute1\": \"value1\"\n}")
                    .wasNotInvoked())
        .isEqualToIgnoringWhitespace(
            """
						Was invoked with bodies:

						Path: /post-200-body
						Body:
						{
						  "request_attribute1": "value1"
						}
						""");

    assertThatWireMockServer().withBody("{}").wasNotInvoked();

    this.assertFailureMessage(() -> assertThatWireMockServer().withBody("{}").wasInvoked())
        .isEqualToIgnoringWhitespace(
            """
						Was not invoked with body:
						{}

						Was invoked with bodies:

						Path: /post-200-body
						Body:
						{
						  "request_attribute1": "value1"
						}
						""");
  }
}
