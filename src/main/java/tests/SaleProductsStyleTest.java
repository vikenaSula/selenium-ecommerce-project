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
import pages.SaleProductsPage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaleProductsStyleTest {
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
    public void testSaleProductsPricingStyle() {
        homePage.open();

        SaleProductsPage salePage = homePage.navigateToSaleProducts();

        List<WebElement> saleProducts = salePage.getSaleProducts();

        Assert.assertTrue(!saleProducts.isEmpty(),
                "Sale products page should display at least one product");

        System.out.println("\n=== SALE PRODUCTS ANALYSIS ===");
        System.out.println("Total sale products found: " + saleProducts.size());

        for (int i = 0; i < saleProducts.size(); i++) {
            WebElement product = saleProducts.get(i);
            System.out.println("\n--- Product " + (i + 1) + " ---");

            boolean hasMultiplePrices = salePage.hasMultiplePrices(product);
            System.out.println("Has multiple prices: " + hasMultiplePrices);

            if (hasMultiplePrices) {
                WebElement originalPrice = salePage.getOriginalPriceElement(product);
                WebElement finalPrice = salePage.getSpecialPriceElement(product);
                String originalColor = salePage.getColor(originalPrice);
                boolean originalHasStrikethrough = salePage.hasStrikethrough(originalPrice);
                boolean originalIsGrey = salePage.isGreyColor(originalColor);

                System.out.println("Original price color: " + originalColor);
                System.out.println("Original price is grey: " + originalIsGrey);
                System.out.println("Original price has strikethrough: " + originalHasStrikethrough);

                String finalColor = salePage.getColor(finalPrice);
                boolean finalHasStrikethrough = salePage.hasStrikethrough(finalPrice);
                boolean finalIsBlue = salePage.isBlueColor(finalColor);
                String finalPriceText = finalPrice.getText();

                System.out.println("Final price color: " + finalColor);
                System.out.println("Final price is blue: " + finalIsBlue);
                System.out.println("Final price has strikethrough: " + finalHasStrikethrough);
                System.out.println("Final price: " + finalPriceText);

                Assert.assertTrue(originalHasStrikethrough,
                        "Product " + (i + 1) + ": Original price should have strikethrough");
                Assert.assertTrue(originalIsGrey,
                        "Product " + (i + 1) + ": Original price should be grey");
                Assert.assertFalse(finalHasStrikethrough,
                        "Product " + (i + 1) + ": Final price should NOT have strikethrough");
                Assert.assertTrue(finalIsBlue,
                        "Product " + (i + 1) + ": Final price should be blue");
            }
        }

        System.out.println("\n✓ Sale products pricing and styling verified successfully!");
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
