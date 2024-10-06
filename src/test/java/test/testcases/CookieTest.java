package test.testcases;

import static se.bjurr.wiremock.assertions.Assertions.assertThatWireMockServer;

import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import test.utils.WireMockAssertBaseTest;

class CookieTest extends WireMockAssertBaseTest {

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
  void testCookie() {
    assertThatWireMockServer().withCookie("cookie1").wasInvoked();

    this.assertFailureMessage(
            () -> assertThatWireMockServer().withCookie("cookie1").wasNotInvoked())
        .isEqualToIgnoringWhitespace("""
						Was invoked with cookies:
						cookie1
						""");

    assertThatWireMockServer().withCookie("cookie2").wasNotInvoked();

    this.assertFailureMessage(() -> assertThatWireMockServer().withCookie("cookie2").wasInvoked())
        .isEqualToIgnoringWhitespace(
            """
						Was not invoked with cookie 'cookie2'
						Was invoked with cookies:
						cookie1
						""");

    assertThatWireMockServer().withCookie("cookie1", "value1").wasInvoked();

    this.assertFailureMessage(
            () -> assertThatWireMockServer().withCookie("cookie1", "value1").wasNotInvoked())
        .isEqualToIgnoringWhitespace(
            """
						Was invoked with cookie values:
						value1
						""");

    assertThatWireMockServer().withCookie("cookie1", "value2").wasNotInvoked();

    this.assertFailureMessage(
            () -> assertThatWireMockServer().withCookie("cookie1", "value2").wasInvoked())
        .isEqualToIgnoringWhitespace(
            """
						Was not invoked with cookie 'cookie1' and value 'value2'
						Was invoked with cookie values:
						value1
						""");
  }
}
