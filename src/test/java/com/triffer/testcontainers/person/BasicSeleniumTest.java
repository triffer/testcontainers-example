package com.triffer.testcontainers.person;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.File;

import static org.junit.Assert.assertTrue;
import static org.testcontainers.containers.BrowserWebDriverContainer.VncRecordingMode.RECORD_ALL;

@Testcontainers
public class BasicSeleniumTest {

    @Container
    private BrowserWebDriverContainer chrome = new BrowserWebDriverContainer()
            .withCapabilities(DesiredCapabilities.chrome()).withRecordingMode(RECORD_ALL, new File("build"));

    @Test
    public void openTestcontainersWebsiteTest() throws Exception {
        // given
        RemoteWebDriver driver = chrome.getWebDriver();

        // when
        driver.get("https://www.testcontainers.org/");

        // Just to get a longer video
        Thread.sleep(3000);

        // then
        assertTrue(driver.getTitle().contains("Testcontainers"));
    }
}
