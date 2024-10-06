package test.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.common.ConsoleNotifier;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.assertj.core.api.AbstractStringAssert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.function.Executable;

public class WireMockAssertBaseTest {
  public WireMockServer wireMockServer;

  @BeforeEach
  public void beforeEachBase() {
    this.wireMockServer =
        new WireMockServer(
            WireMockConfiguration.wireMockConfig()
                .port(0)
                .stubRequestLoggingDisabled(false)
                .notifier(new ConsoleNotifier(true)));
    this.wireMockServer.start();
    WireMock.configureFor(this.wireMockServer.port());
  }

  @AfterEach
  public void afterEachBase() {
    this.wireMockServer.stop();
  }

  public AbstractStringAssert<?> assertFailureMessage(final Executable r) {
    final AssertionError thrown = assertThrows(AssertionError.class, r);
    return assertThat(thrown.getMessage());
  }
}
