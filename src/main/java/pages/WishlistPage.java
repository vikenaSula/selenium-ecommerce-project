package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.util.List;

public class WishlistPage extends BasePage {

    private final By wishlistItemsLocator = By.cssSelector(".wishlist .item, .products-grid .item");
    private final By addToCartButtonLocator = By.cssSelector("button[title='Add to Cart']");
    private final By colorSelectLocator = By.cssSelector("select[name*='super_attribute']");
    private final By sizeSelectLocator = By.cssSelector("select[name*='super_attribute']");

    public WishlistPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        driver.navigate().to("https://ecommerce.tealiumdemo.com/wishlist/");
        waitForPageReady();
        removeOverlays();
    }


    public List<WebElement> getWishlistItems() {
        By[] wishlistLocators = {
            By.cssSelector(".products-grid .item"),
            By.cssSelector("li.item"),
            By.cssSelector(".wishlist .item"),
            By.cssSelector("ol.products-grid li"),
            By.cssSelector("ul.products-grid li"),
            By.cssSelector("[class*='product-item']"),
            By.cssSelector("tbody tr") // Table format
        };

        for (By locator : wishlistLocators) {
            try {
                wait.until(ExpectedConditions.presenceOfElementLocated(locator));
                List<WebElement> items = driver.findElements(locator);

                if (!items.isEmpty()) {
                    System.out.println("Found " + items.size() + " wishlist items using: " + locator);
                    return items;
                }
            } catch (Exception e) {
                System.out.println("Failed with wishlist locator: " + locator);
            }
        }

        try {
            System.out.println("Trying JavaScript to find wishlist items...");
            @SuppressWarnings("unchecked")
            List<WebElement> items = (List<WebElement>) js.executeScript(
                "var buttons = document.querySelectorAll('button');" +
                "var items = [];" +
                "for(var i=0; i<buttons.length; i++) {" +
                "  var btn = buttons[i];" +
                "  if(btn.textContent.includes('Add to Cart') || btn.title.includes('Add to Cart')) {" +
                "    var parent = btn.closest('tr') || btn.closest('li') || btn.closest('[class*=\"item\"]');" +
                "    if(parent && items.indexOf(parent) === -1) {" +
                "      items.push(parent);" +
                "    }" +
                "  }" +
                "}" +
                "return items;"
            );

            if (items != null && !items.isEmpty()) {
                System.out.println("JavaScript found " + items.size() + " wishlist items");
                return items;
            }
        } catch (Exception e) {
            System.out.println("JavaScript fallback failed: " + e.getMessage());
        }

        throw new RuntimeException("Could not find any wishlist items. The wishlist may be empty.");
    }

    public void addItemToCart(int index) {
        removeOverlays();

        try {
            System.out.println("Looking for Add to Cart buttons...");

            List<WebElement> addToCartButtons = driver.findElements(
                By.xpath("//button[contains(., 'Add to Cart') or contains(@title, 'Add to Cart')]")
            );

            if (addToCartButtons.isEmpty()) {
                addToCartButtons = driver.findElements(
                    By.cssSelector("button[title*='Add to Cart'], button.btn-cart")
                );
            }

            if (addToCartButtons.isEmpty()) {
                throw new RuntimeException("No 'Add to Cart' buttons found on wishlist page");
            }

            System.out.println("Found " + addToCartButtons.size() + " Add to Cart buttons");

            if (index >= addToCartButtons.size()) {
                throw new RuntimeException("Button index " + index + " is out of bounds. Only " +
                                         addToCartButtons.size() + " buttons available.");
            }

            WebElement button = addToCartButtons.get(index);
            scrollToElement(button);

            wait.until(ExpectedConditions.elementToBeClickable(button));

            try {
                button.click();
            } catch (Exception e) {
                js.executeScript("arguments[0].click();", button);
            }

            System.out.println("Clicked Add to Cart button " + (index + 1));

            waitForPageReady();

            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        } catch (Exception e) {
            throw new RuntimeException("Could not add item to cart: " + e.getMessage());
        }
    }


    public void addAllItemsToCart() {
        removeOverlays();

        List<WebElement> buttons = driver.findElements(
            By.xpath("//button[contains(., 'Add to Cart') or contains(@title, 'Add to Cart')]")
        );

        if (buttons.isEmpty()) {
            buttons = driver.findElements(By.cssSelector("button[title*='Add to Cart'], button.btn-cart"));
        }

        int buttonCount = buttons.size();
        System.out.println("Found " + buttonCount + " Add to Cart buttons on wishlist page");

        if (buttonCount == 0) {
            throw new RuntimeException("No Add to Cart buttons found. Wishlist may be empty.");
        }

        for (int i = 0; i < buttonCount; i++) {
            System.out.println("\nAdding item " + (i + 1) + " of " + buttonCount + " to cart...");

            try {
                addItemToCart(0); // Always use 0 as buttons may get removed after adding to cart
                System.out.println("✓ Item " + (i + 1) + " added successfully");

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                if (i < buttonCount - 1) {
                    System.out.println("Refreshing wishlist page...");
                    open();
                }
            } catch (Exception e) {
                System.out.println("Error adding item " + (i + 1) + ": " + e.getMessage());
                open();
            }
        }

        System.out.println("\nFinished adding all items to cart");
    }
}

