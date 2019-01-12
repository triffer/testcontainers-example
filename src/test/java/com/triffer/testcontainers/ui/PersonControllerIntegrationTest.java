package com.triffer.testcontainers.ui;

import java.io.File;
import java.net.Inet4Address;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.Testcontainers;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = PersonControllerIntegrationTest.Initializer.class)
public class PersonControllerIntegrationTest {

    @LocalServerPort
    private String serverPort;

    @ClassRule
    public static PostgreSQLContainer postgresContainer = new PostgreSQLContainer().withPassword("test")
            .withUsername("test");

    @Rule
    public BrowserWebDriverContainer chrome = new BrowserWebDriverContainer()
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
    public void personsFromDbAreShownOnPage() throws Exception {
        // given
        String serverAddress = Inet4Address.getLocalHost().getHostAddress();
        RemoteWebDriver driver = chrome.getWebDriver();

        // when
        // TODO evaluate why getTestHostIpAddress and expose host ports to containers is not working
        // You shoul use chrome.getTestHostIpAddress(), but this lead to an error in my case
        driver.get("http://" + serverAddress + ":" + serverPort + "/persons");

        // then
        List<WebElement> pElements = driver.findElementsByTagName("p");

        Assert.assertEquals(3, pElements.size());
        Assert.assertEquals("John", pElements.get(0).getText());
        Assert.assertEquals("Julia", pElements.get(1).getText());
        Assert.assertEquals("Thomas", pElements.get(2).getText());
    }
}
