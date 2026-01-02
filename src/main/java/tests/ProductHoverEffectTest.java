package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.io.FileHandler;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.HomePage;
import pages.WomenProductsPage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ProductHoverEffectTest {
    private WebDriver driver;
    private HomePage homePage;

    @BeforeMethod
    public void setUp() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--start-maximized");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-extensions");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        Map<String, Object> prefs = new HashMap<>();
        prefs.put("profile.default_content_setting_values.cookies", 1);
        prefs.put("profile.cookie_controls_mode", 0);
        options.setExperimentalOption("prefs", prefs);

        driver = new ChromeDriver(options);
        homePage = new HomePage(driver);
    }

    @Test
    public void testWomenProductHoverEffect() {
        homePage.open();

        WomenProductsPage womenPage = homePage.navigateToWomenProducts();

        WebElement firstProduct = womenPage.getFirstProduct();

        String initialBoxShadow = womenPage.getProductBoxShadow(firstProduct);
        String initialBorder = womenPage.getProductBorder(firstProduct);
        String initialTransform = womenPage.getProductTransform(firstProduct);

        System.out.println("=== INITIAL STATE ===");
        System.out.println("Box Shadow: " + initialBoxShadow);
        System.out.println("Border: " + initialBorder);
        System.out.println("Transform: " + initialTransform);

        womenPage.hoverOverProduct(firstProduct);

        String hoverBoxShadow = womenPage.getProductBoxShadow(firstProduct);
        String hoverBorder = womenPage.getProductBorder(firstProduct);
        String hoverTransform = womenPage.getProductTransform(firstProduct);

        System.out.println("\n=== HOVER STATE ===");
        System.out.println("Box Shadow: " + hoverBoxShadow);
        System.out.println("Border: " + hoverBorder);
        System.out.println("Transform: " + hoverTransform);

        boolean boxShadowChanged = !initialBoxShadow.equals(hoverBoxShadow);
        boolean borderChanged = !initialBorder.equals(hoverBorder);
        boolean transformChanged = !initialTransform.equals(hoverTransform);

        System.out.println("\n=== CHANGES DETECTED ===");
        System.out.println("Box Shadow changed: " + boxShadowChanged);
        System.out.println("Border changed: " + borderChanged);
        System.out.println("Transform changed: " + transformChanged);

        boolean stylesChanged = boxShadowChanged || borderChanged || transformChanged;

        Assert.assertTrue(stylesChanged,
                "Product hover effect not detected - CSS properties should change on hover. " +
                        "Checked: box-shadow, border, transform");
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (ITestResult.FAILURE == result.getStatus()) {
            try {
                captureScreenshot(result.getName());
            } catch (Exception e) {
                System.out.println("Failed to capture screenshot: " + e.getMessage());
            }
        }

        if (driver != null) {
            driver.quit();
        }
    }

    private void captureScreenshot(String testName) {
        try {
            File screenshotDir = new File("screenshots");
            if (!screenshotDir.exists()) {
                boolean created = screenshotDir.mkdirs();
                if (!created) {
                    System.out.println("Warning: Could not create screenshots directory");
                }
            }

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String fileName = testName + "_" + timestamp + ".png";

            TakesScreenshot screenshot = (TakesScreenshot) driver;
            File sourceFile = screenshot.getScreenshotAs(OutputType.FILE);
            File destinationFile = new File(screenshotDir, fileName);
            FileHandler.copy(sourceFile, destinationFile);

            System.out.println("Screenshot captured: " + destinationFile.getAbsolutePath());
        } catch (Exception e) {
            System.out.println("Error capturing screenshot: " + e.getMessage());
        }
    }
}
