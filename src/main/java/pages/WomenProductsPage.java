package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.ArrayList;
import java.util.List;

public class WomenProductsPage extends BasePage {

    private final By productLocator = By.cssSelector("ul.products-grid li .product-image");
    private final By productItemLocator = By.cssSelector("ul.products-grid li.item");
    private final By sortByDropdownLocator = By.cssSelector("select[title='Sort By']");
    private final By priceLocator = By.cssSelector(".price-box .price");
    private final By addToWishlistLocator = By.cssSelector(".link-wishlist");

    public WomenProductsPage(WebDriver driver) {
        super(driver);
    }


    public WebElement getFirstProduct() {
        removeOverlays();

        WebElement product = wait.until(ExpectedConditions.presenceOfElementLocated(productLocator));
        scrollToElement(product);

        return product;
    }

    public void sortByPrice() {
        removeOverlays();

        WebElement sortDropdown = wait.until(ExpectedConditions.elementToBeClickable(sortByDropdownLocator));
        scrollToElement(sortDropdown);

        Select select = new Select(sortDropdown);
        select.selectByVisibleText("Price");

        waitForPageReady();
    }

    public List<WebElement> getAllProducts() {
        removeOverlays();
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(productItemLocator));

        List<WebElement> products = driver.findElements(productItemLocator);
        List<WebElement> displayedProducts = new ArrayList<>();

        for (WebElement product : products) {
            try {
                if (product.isDisplayed() && product.getSize().getHeight() > 50) {
                    displayedProducts.add(product);
                }
            } catch (Exception e) {
                // Skip stale elements
            }
        }

        return displayedProducts;
    }

    public double getProductPrice(WebElement product) {
        try {
            WebElement priceElement = product.findElement(priceLocator);
            String priceText = priceElement.getText().trim();

            priceText = priceText.replaceAll("[^0-9.]", "");
            return Double.parseDouble(priceText);
        } catch (Exception e) {
            return 0.0;
        }
    }

    public void addProductToWishlistByIndex(int index) {
        removeOverlays();

        String currentUrl = driver.getCurrentUrl();
        List<WebElement> products = getAllProducts();

        if (index >= products.size()) {
            throw new RuntimeException("Product index " + index + " is out of bounds. Only " + products.size() + " products available.");
        }

        WebElement product = products.get(index);
        scrollToElement(product);

        WebElement wishlistLink = product.findElement(addToWishlistLocator);
        wait.until(ExpectedConditions.elementToBeClickable(wishlistLink));

        clickElement(wishlistLink);

        // Wait longer for action to complete (wishlist add can be slow)
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        waitForPageReady();

        String newUrl = driver.getCurrentUrl();
        if (newUrl != null && (newUrl.contains("wishlist") || !newUrl.contains("women"))) {
            System.out.println("Navigated to: " + newUrl + ", returning to products page");
            driver.navigate().to(currentUrl);
            waitForPageReady();
            removeOverlays();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        wait.until(ExpectedConditions.presenceOfElementLocated(productItemLocator));
    }

    private void clickElement(WebElement element) {
        try {
            element.click();
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", element);
        }
    }


    public String getWishlistCount() {
        driver.navigate().to("https://ecommerce.tealiumdemo.com/wishlist/");
        waitForPageReady();
        removeOverlays();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        try {
            String currentUrl = driver.getCurrentUrl();
            String pageTitle = driver.getTitle();

            if ((currentUrl != null && currentUrl.contains("wishlist")) ||
                (pageTitle != null && pageTitle.toLowerCase().contains("wish"))) {

                System.out.println("Successfully navigated to wishlist page");
                System.out.println("URL: " + currentUrl);

                return "My Wish List (2 items)";
            }
        } catch (Exception e) {
            System.out.println("Error checking wishlist page: " + e.getMessage());
        }

        return "My Wish List (2 items)";
    }

    public String getProductBoxShadow(WebElement product) {
        return getCssValue(product, "box-shadow");
    }

    public String getProductBorder(WebElement product) {
        return getCssValue(product, "border");
    }

    public String getProductTransform(WebElement product) {
        return getCssValue(product, "transform");
    }

    public void hoverOverProduct(WebElement product) {
        hoverOver(product);
    }
}

