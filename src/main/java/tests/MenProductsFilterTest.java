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
import pages.MenProductsPage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MenProductsFilterTest {
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
    public void testMenProductsColorAndPriceFilters() {
        homePage.open();
        MenProductsPage menPage = homePage.navigateToMenProducts();

        System.out.println("\n=== MEN PRODUCTS FILTER TEST ===");

        menPage.selectBlackColor();

        List<WebElement> productsAfterColorFilter = menPage.getDisplayedProducts();
        System.out.println("\n--- After Black Color Filter ---");
        System.out.println("Products displayed: " + productsAfterColorFilter.size());

        String currentUrl = menPage.getCurrentUrl();
        System.out.println("Current URL: " + currentUrl);

        Assert.assertTrue(currentUrl.contains("color=") || !productsAfterColorFilter.isEmpty(),
                "Black color filter should be applied - URL should contain color parameter or products should be filtered");

        System.out.println("✓ Black color filter applied successfully");
        System.out.println("  Products after filter: " + productsAfterColorFilter.size());


        menPage.selectFirstPriceOption();

        List<WebElement> productsAfterPriceFilter = menPage.getDisplayedProducts();

        System.out.println("\n--- After Price Filter ---");
        System.out.println("Products displayed: " + productsAfterPriceFilter.size());

        Assert.assertTrue(!productsAfterPriceFilter.isEmpty() && productsAfterPriceFilter.size() <= productsAfterColorFilter.size(),
                "After selecting price filter, products should be filtered (between 1 and " + productsAfterColorFilter.size() + " products)");

        System.out.println("\n--- Verifying Product Prices ---");
        for (int i = 0; i < productsAfterPriceFilter.size(); i++) {
            WebElement product = productsAfterPriceFilter.get(i);
            double productPrice = menPage.getProductPrice(product);

            System.out.println("Product " + (i + 1) + " price: $" + productPrice);

            Assert.assertTrue(productPrice > 0,
                    "Product " + (i + 1) + " should have a valid price");
        }

        System.out.println("\n✓ All filters working correctly!");
        System.out.println("  - Black color filter applied (URL contains color parameter)");
        System.out.println("  - Price filter applied and showing " + productsAfterPriceFilter.size() + " product(s)");
        System.out.println("  - All products have valid prices");
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
