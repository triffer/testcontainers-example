package com.triffer.testcontainers.ui;

import java.io.File;
import java.net.Inet4Address;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
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
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = PersonControllerIntegrationComposeTest.Initializer.class)
public class PersonControllerIntegrationComposeTest {

    @LocalServerPort
    private String serverPort;

    private static RemoteWebDriver remoteWebDriver;

    private static String POSTGRES_DB = "test";
    private static String POSTGRES_USER = "test1";
    private static String POSTGRES_PASSWORD = "test1";
    private static int POSTGRES_PORT = 5432;
    private static int SELENIUM_PORT = 4444;

    @ClassRule
    public static DockerComposeContainer environment = new DockerComposeContainer(
            new File("src/test/resources/docker-compose/compose.yml")).withExposedService("postgres_1", POSTGRES_PORT,
            Wait.forLogMessage(".*database system is ready to accept connections.*\\s", 2)
                    .withStartupTimeout(Duration.of(60L, ChronoUnit.SECONDS)))
            .withExposedService("selenium_1", SELENIUM_PORT,
                    Wait.forLogMessage(".*Selenium Server is up and running.*\n", 1)
                            .withStartupTimeout(Duration.of(15L, ChronoUnit.SECONDS)));

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(@NotNull ConfigurableApplicationContext configurableApplicationContext) {
            String postgresHostAndPort = environment.getServiceHost("postgres_1", POSTGRES_PORT) + ":" + environment
                    .getServicePort("postgres_1", POSTGRES_PORT);
            String postgresJdbcUrl = "jdbc:postgresql://" + postgresHostAndPort + "/" + POSTGRES_DB;

            TestPropertyValues values = TestPropertyValues
                    .of("spring.datasource.url=" + postgresJdbcUrl, "spring.datasource.password=" + POSTGRES_PASSWORD,
                            "spring.datasource.username=" + POSTGRES_USER);
            values.applyTo(configurableApplicationContext);

            try {
                URL remoteWebDriverUrl = new URL(
                        "http://" + environment.getServiceHost("selenium_1", SELENIUM_PORT) + ":" + environment
                                .getServicePort("selenium_1", SELENIUM_PORT) + "/wd/hub");
                remoteWebDriver = new RemoteWebDriver(remoteWebDriverUrl, new ChromeOptions());
            } catch (MalformedURLException e) {
                throw new IllegalStateException("Selenium remote driver initialization failed", e);
            }
        }
    }

    @Test
    @SqlGroup({
            @Sql(executionPhase = BEFORE_TEST_METHOD, scripts = "/dbTestdata/person/personIntegrationTestBefore.sql"),
            @Sql(executionPhase = AFTER_TEST_METHOD, scripts = "/dbTestdata/person/personIntegrationTestAfter.sql") })
    public void personsFromDbAreShownOnPage() throws Exception {
        // given
        String serverAddress = Inet4Address.getLocalHost().getHostAddress();

        // when
        /*
        You can expose host ports to the containers but this is not working for me on Windows.
        Testcontainers.exposeHostPorts(Integer.valueOf(serverPort));
        remoteWebDriver.get("http://host.testcontainers.internal:" + serverPort + "/persons");
        */

        // TODO evaluate why expose host ports to containers is not working
        remoteWebDriver.get("http://" + serverAddress + ":" + serverPort + "/persons");

        // then
        List<WebElement> pElements = remoteWebDriver.findElementsByTagName("p");

        Assert.assertEquals(3, pElements.size());
        Assert.assertEquals("John", pElements.get(0).getText());
        Assert.assertEquals("Julia", pElements.get(1).getText());
        Assert.assertEquals("Thomas", pElements.get(2).getText());
    }
}
