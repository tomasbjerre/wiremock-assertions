package test.testcases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static se.bjurr.wiremock.assertions.Assertions.assertThatWireMockServer;

import com.github.tomakehurst.wiremock.client.WireMock;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import test.utils.WireMockAssertBaseTest;

class InvokedTest extends WireMockAssertBaseTest {

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
  void testThatBothDefaultAndSpecificWireMockServerCanBeUsed() {
    final AssertionError thrown =
        assertThrows(AssertionError.class, () -> assertThatWireMockServer().wasNotInvoked());
    assertThat(thrown.getMessage())
        .containsIgnoringWhitespaces(
            """
				Was invoked: [{
				  "url" : "/get-200-no-body"
				""");

    final AssertionError thrownUsingGivenWireMockServer =
        assertThrows(
            AssertionError.class,
            () -> assertThatWireMockServer(this.wireMockServer).wasNotInvoked());
    assertThat(thrownUsingGivenWireMockServer.getMessage())
        .containsIgnoringWhitespaces(
            """
				Was invoked:
				[{
				  "url" : "/get-200-no-body"
				""");
  }

  @Test
  void testInvoked() {
    assertThrows(AssertionError.class, () -> assertThatWireMockServer().wasNotInvoked());
    assertThatWireMockServer().wasInvoked();
  }
}
