package test.testcases;

import static se.bjurr.wiremock.assertions.Assertions.assertThatWireMockServer;

import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import test.utils.WireMockAssertBaseTest;

class PathTest extends WireMockAssertBaseTest {

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
  void testPath() {
    assertThatWireMockServer().withPath("/get-200-no-body").wasInvoked();

    this.assertFailureMessage(
            () -> assertThatWireMockServer().withPath("/get-200-no-body").wasNotInvoked())
        .isEqualToIgnoringWhitespace(
            """
						Was invoked with paths:
						/get-200-no-body
						""");

    this.assertFailureMessage(
            () -> assertThatWireMockServer().withPath("/get-200-no-body-notinvoked").wasInvoked())
        .isEqualToIgnoringWhitespace(
            """
						Was not invoked with path '/get-200-no-body-notinvoked'.
						Was invoked with paths:
						/get-200-no-body
						""");

    assertThatWireMockServer().withPath("/get-200-no-body-notinvoked").wasNotInvoked();
  }
}
