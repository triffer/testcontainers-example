package com.triffer.testcontainers.ui;

import java.io.File;
import java.net.Inet4Address;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = PersonControllerIntegrationTest.Initializer.class)
class PersonControllerIntegrationTest {

    @LocalServerPort
    private String serverPort;

    @Container
    private static PostgreSQLContainer postgresContainer = new PostgreSQLContainer().withPassword("test")
            .withUsername("test");

    @Container
    private BrowserWebDriverContainer chrome = new BrowserWebDriverContainer()
     .withCapabilities(DesiredCapabilities.chrome()).withRecordingMode(BrowserWebDriverContainer.VncRecordingMode.RECORD_FAILING, new File("build"));

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(@NotNull ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues values = TestPropertyValues.of("spring.datasource.url=" + postgresContainer.getJdbcUrl(),
                    "spring.datasource.password=" + postgresContainer.getPassword(),
                    "spring.datasource.username=" + postgresContainer.getUsername());
            values.applyTo(configurableApplicationContext);
        }
    }

    @Test
    @SqlGroup({
            @Sql(executionPhase = BEFORE_TEST_METHOD, scripts = "/dbTestdata/person/personIntegrationTestBefore.sql"),
            @Sql(executionPhase = AFTER_TEST_METHOD, scripts = "/dbTestdata/person/personIntegrationTestAfter.sql") })
    void personsFromDbAreShownOnPage() throws Exception {
        // given
        String serverAddress = Inet4Address.getLocalHost().getHostAddress();
        RemoteWebDriver driver = chrome.getWebDriver();

        // when
        // You should use chrome.getTestHostIpAddress(), but this is not working for me on Windows (may be because it'S currently best efforts).
        driver.get("http://" + serverAddress + ":" + serverPort + "/persons");

        // then
        List<WebElement> pElements = driver.findElementsByTagName("p");

        Assert.assertEquals(3, pElements.size());
        Assert.assertEquals("John", pElements.get(0).getText());
        Assert.assertEquals("Julia", pElements.get(1).getText());
        Assert.assertEquals("Thomas", pElements.get(2).getText());
    }
}
