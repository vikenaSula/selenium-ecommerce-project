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
import java.util.List;
import java.util.Map;

public class SortingAndWishlistTest {
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
    public void testSortingAndWishlist() {
        System.out.println("\n=== SORTING AND WISHLIST TEST ===");

        homePage.open();
        WomenProductsPage womenPage = homePage.navigateToWomenProducts();
        System.out.println("✓ Navigated to Women's products page");

        womenPage.sortByPrice();
        System.out.println("✓ Sorted products by Price");

        List<WebElement> products = womenPage.getAllProducts();
        System.out.println("\n--- Checking Price Sorting ---");
        System.out.println("Total products found: " + products.size());

        boolean isSorted = true;
        double previousPrice = 0.0;

        for (int i = 0; i < Math.min(5, products.size()); i++) {
            double currentPrice = womenPage.getProductPrice(products.get(i));
            System.out.println("Product " + (i + 1) + " price: $" + currentPrice);

            if (i > 0 && currentPrice < previousPrice) {
                isSorted = false;
                System.out.println("✗ Products are NOT sorted! Product " + (i + 1) +
                    " ($" + currentPrice + ") is less than Product " + i + " ($" + previousPrice + ")");
            }
            previousPrice = currentPrice;
        }

        Assert.assertTrue(isSorted, "Products should be sorted by price in ascending order");
        System.out.println("✓ Products are correctly sorted by price");

        System.out.println("\n--- Adding Products to Wishlist ---");

        if (products.size() >= 2) {
            womenPage.addProductToWishlistByIndex(0);
            System.out.println("✓ Added first product to wishlist");

            womenPage.addProductToWishlistByIndex(1);
            System.out.println("✓ Added second product to wishlist");
        } else {
            Assert.fail("Not enough products available to add to wishlist");
        }

        System.out.println("\n--- Checking Wishlist Count ---");
        String wishlistText = womenPage.getWishlistCount();
        System.out.println("Wishlist text: " + wishlistText);

        Assert.assertTrue(wishlistText.contains("2"),
            "Wishlist should show 2 items. Actual: " + wishlistText);
        System.out.println("✓ Wishlist correctly shows 2 items");

        System.out.println("\n=== TEST COMPLETED SUCCESSFULLY ===");
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

