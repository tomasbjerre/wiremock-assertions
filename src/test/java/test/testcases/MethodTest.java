package test.testcases;

import static com.github.tomakehurst.wiremock.http.RequestMethod.GET;
import static com.github.tomakehurst.wiremock.http.RequestMethod.POST;
import static se.bjurr.wiremock.assertions.Assertions.assertThatWireMockServer;

import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import test.utils.WireMockAssertBaseTest;

class MethodTest extends WireMockAssertBaseTest {

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
  void testMethod() {
    assertThatWireMockServer().withMethod(GET).wasInvoked();

    this.assertFailureMessage(() -> assertThatWireMockServer().withMethod(GET).wasNotInvoked())
        .isEqualToIgnoringWhitespace("""
						Was invoked with methods:
						GET
						""");

    this.assertFailureMessage(() -> assertThatWireMockServer().withMethod(POST).wasInvoked())
        .isEqualToIgnoringWhitespace(
            """
						Was not invoked with method 'POST'
						Was invoked with methods:
						GET
						""");

    assertThatWireMockServer().withMethod(POST).wasNotInvoked();
  }
}
