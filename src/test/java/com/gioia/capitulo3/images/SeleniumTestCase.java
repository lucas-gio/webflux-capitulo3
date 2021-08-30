package com.gioia.capitulo3.images;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@ExtendWith(SpringExtension.class) // junit 5
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SeleniumTestCase {
    static ChromeDriverService chromeDriverService;
    static ChromeDriver chromeDriver;

    @LocalServerPort
    int port;

    @BeforeAll
    public static void setUp() throws IOException {
        System.setProperty("webdriver.chrome.driver", "src/test/resources/ext/chromedriver.exe");
        chromeDriverService = ChromeDriverService.createDefaultService();
        chromeDriver = new ChromeDriver(chromeDriverService);

        Path testResults = Paths.get("target", "testResults");
        if (Files.notExists(testResults)) {
            Files.createDirectory(testResults);
        }
    }

    @AfterAll
    public static void tearDown() {
        chromeDriverService.stop();
    }

    @Test
    public void homePageShoudWorkWhenNavigated() throws IOException {
        chromeDriver.get("http://localhost:" + port);
        takeScreenShot("1");
        Assertions.assertThat(chromeDriver.getTitle())
                .isEqualTo("Imágenes");

        Assertions.assertThat(chromeDriver.getPageSource())
                .contains("primer libro", "segundo libro", "tercer libro", "cuarto libro");

        //WebElement webElement = chromeDriver.findElement(By.cssSelector("a[href]"))
    }

    private void takeScreenShot(String name) throws IOException{
        FileCopyUtils.copy(
                chromeDriver.getScreenshotAs(OutputType.FILE),
                new File("target/testResults/Test-" + name + ".png")
        );
    }
}
