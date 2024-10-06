package test.testcases;

import static se.bjurr.wiremock.assertions.Assertions.assertThatWireMockServer;

import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import test.utils.WireMockAssertBaseTest;

class HeaderTest extends WireMockAssertBaseTest {

  @BeforeEach
  public void before() {
    WireMock.stubFor(
        WireMock.get(WireMock.urlEqualTo("/get-200-no-body")).willReturn(WireMock.ok()));
    RestAssured.given()
        .header("header1", "value1")
        .cookie("cookie1", "value1")
        .when()
        .get("http://localhost:" + this.wireMockServer.port() + "/get-200-no-body")
        .then()
        .statusCode(200);
  }

  @Test
  void testHeader() {
    assertThatWireMockServer().withHeader("header1").wasInvoked();

    this.assertFailureMessage(
            () -> assertThatWireMockServer().withHeader("header1").wasNotInvoked())
        .isEqualToIgnoringWhitespace(
            """
						Was invoked with headers:
						Accept
						Accept-Encoding
						Connection
						Cookie
						Host
						User-Agent
						header1
						""");

    assertThatWireMockServer().withHeader("header2").wasNotInvoked();

    this.assertFailureMessage(() -> assertThatWireMockServer().withHeader("header2").wasInvoked())
        .isEqualToIgnoringWhitespace(
            """
						Was not invoked with header 'header2'
						Was invoked with headers:
						Accept
						Accept-Encoding
						Connection
						Cookie
						Host
						User-Agent
						header1
						""");

    assertThatWireMockServer().withHeader("header1", "value1").wasInvoked();

    this.assertFailureMessage(
            () -> assertThatWireMockServer().withHeader("header1", "value1").wasNotInvoked())
        .isEqualToIgnoringWhitespace(
            """
						Was invoked with header values:
						header1: value1
						""");

    this.assertFailureMessage(
            () -> assertThatWireMockServer().withHeader("header1", "value2").wasInvoked())
        .isEqualToIgnoringWhitespace(
            """
						Was not invoked with header 'header1' and value 'value2'
						Was invoked with header values:
						header1: value1
						""");

    this.assertFailureMessage(
            () -> assertThatWireMockServer().withHeader("header1", "value2").wasNotInvoked())
        .isEqualToIgnoringWhitespace(
            """
						Was invoked with headers:
						Accept
						Accept-Encoding
						Connection
						Cookie
						Host
						User-Agent
						header1
						""");
  }
}
