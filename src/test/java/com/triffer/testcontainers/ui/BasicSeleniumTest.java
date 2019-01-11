package com.triffer.testcontainers.ui;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testcontainers.containers.BrowserWebDriverContainer;

import static org.junit.Assert.assertTrue;
import static org.testcontainers.containers.BrowserWebDriverContainer.VncRecordingMode.RECORD_ALL;

public class BasicSeleniumTest {

    @Rule
    public BrowserWebDriverContainer chrome = new BrowserWebDriverContainer()
            .withCapabilities(DesiredCapabilities.chrome()).withRecordingMode(RECORD_ALL, new File("build"));

    @Test
    public void openTestcontainersWebsiteTest() {
        // given
        RemoteWebDriver driver = chrome.getWebDriver();

        // when
        driver.get("https://www.testcontainers.org/");

        // then
        assertTrue(driver.getTitle().contains("Testcontainers"));
    }
}
