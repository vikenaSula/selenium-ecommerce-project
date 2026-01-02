package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;


public class ShoppingCartPage extends BasePage {

    private final By cartItemsLocator = By.cssSelector(".cart tbody tr");
    private final By itemPriceLocator = By.cssSelector(".product-cart-price .price, .cart-price .price");
    private final By quantityInputLocator = By.cssSelector("input.qty, input[title='Qty']");
    private final By updateButtonLocator = By.cssSelector("button[title='Update'], button.btn-update");
    private final By grandTotalLocator = By.cssSelector(".grand-total .price, .totals .grand-total .price");
    private final By subtotalLocator = By.cssSelector(".subtotal .price");

    public ShoppingCartPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        driver.navigate().to("https://ecommerce.tealiumdemo.com/checkout/cart/");
        waitForPageReady();
        removeOverlays();
    }

    public List<WebElement> getCartItems() {

        try {
            // Wait for page to be ready
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        try {
            List<WebElement> emptyCartMessages = driver.findElements(
                By.xpath("//*[contains(text(), 'empty') or contains(text(), 'no items')]")
            );
            if (!emptyCartMessages.isEmpty()) {
                for (WebElement msg : emptyCartMessages) {
                    if (msg.isDisplayed()) {
                        System.out.println("Cart appears to be empty. Message: " + msg.getText());
                    }
                }
            }
        } catch (Exception ignored) {
        }

        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("tbody")));

            List<WebElement> allRows = driver.findElements(By.cssSelector("tbody tr"));

            System.out.println("DEBUG: Total tbody tr elements found: " + allRows.size());

            List<WebElement> productRows = new java.util.ArrayList<>();
            for (int i = 0; i < allRows.size(); i++) {
                WebElement row = allRows.get(i);
                try {
                    String rowClass = row.getAttribute("class");
                    String rowText = row.getText();
                    System.out.println("Row " + i + " - Class: '" + rowClass + "', Text length: " + rowText.length());

                    if (rowText != null && rowText.length() > 20 &&
                        (rowClass.contains("odd") || rowClass.contains("even") ||
                         rowClass.contains("first") || rowClass.contains("last"))) {
                        productRows.add(row);
                        System.out.println("  → Added as product row");
                    }
                } catch (Exception e) {
                    System.out.println("Row " + i + " - Error: " + e.getMessage());
                }
            }

            if (!productRows.isEmpty()) {
                System.out.println("Found " + productRows.size() + " cart product items in tbody");
                return productRows;
            }

            if (!allRows.isEmpty()) {
                System.out.println("Using all " + allRows.size() + " rows as fallback");
                return allRows;
            }
        } catch (Exception e) {
            System.out.println("Failed to find tbody tr elements: " + e.getMessage());
        }

        try {
            List<WebElement> rows = driver.findElements(By.cssSelector("tr.first, tr.last, tr.odd, tr.even"));
            if (!rows.isEmpty()) {
                System.out.println("Found " + rows.size() + " cart items by class patterns");
                return rows;
            }
        } catch (Exception e) {
            System.out.println("Failed with class pattern selectors: " + e.getMessage());
        }

        try {
            List<WebElement> rows = driver.findElements(
                By.xpath("//tbody//tr[contains(@class, 'odd') or contains(@class, 'even')]")
            );
            if (!rows.isEmpty()) {
                System.out.println("Found " + rows.size() + " cart items via XPath");
                return rows;
            }
        } catch (Exception e) {
            System.out.println("XPath search failed: " + e.getMessage());
        }

        throw new RuntimeException("Could not find any cart items. The cart is likely empty.");
    }

    public void updateQuantity(int itemIndex, int quantity) {
        removeOverlays();

        List<WebElement> items = getCartItems();

        if (itemIndex >= items.size()) {
            throw new RuntimeException("Item index " + itemIndex + " is out of bounds. Only " + items.size() + " items in cart.");
        }

        WebElement item = items.get(itemIndex);
        scrollToElement(item);

        WebElement qtyInput = item.findElement(quantityInputLocator);
        qtyInput.clear();
        qtyInput.sendKeys(String.valueOf(quantity));

        System.out.println("Updated quantity to " + quantity + " for item " + (itemIndex + 1));
    }

    public void clickUpdate() {
        removeOverlays();

        WebElement updateButton = wait.until(ExpectedConditions.elementToBeClickable(updateButtonLocator));
        scrollToElement(updateButton);

        try {
            updateButton.click();
        } catch (Exception e) {
            js.executeScript("arguments[0].click();", updateButton);
        }

        waitForPageReady();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public double getItemPrice(WebElement item) {
        try {
            WebElement priceElement = item.findElement(itemPriceLocator);
            String priceText = priceElement.getText().trim();
            priceText = priceText.replaceAll("[^0-9.]", "");
            return Double.parseDouble(priceText);
        } catch (Exception e) {
            return 0.0;
        }
    }

    public int getItemQuantity(WebElement item) {
        try {
            WebElement qtyInput = item.findElement(quantityInputLocator);
            String qtyText = qtyInput.getAttribute("value");
            return Integer.parseInt(qtyText);
        } catch (Exception e) {
            return 0;
        }
    }

    public double calculateItemsTotal() {
        List<WebElement> items = getCartItems();
        double total = 0.0;

        for (int i = 0; i < items.size(); i++) {
            WebElement item = items.get(i);
            double price = getItemPrice(item);
            int quantity = getItemQuantity(item);
            double itemTotal = price * quantity;

            System.out.println("Item " + (i + 1) + ": $" + price + " x " + quantity + " = $" + itemTotal);
            total += itemTotal;
        }

        System.out.println("Calculated Total: $" + total);
        return total;
    }

    public double getGrandTotal() {
        try {
            WebElement grandTotalElement = wait.until(ExpectedConditions.presenceOfElementLocated(grandTotalLocator));
            String totalText = grandTotalElement.getText().trim();
            totalText = totalText.replaceAll("[^0-9.]", "");
            double grandTotal = Double.parseDouble(totalText);
            System.out.println("Grand Total from page: $" + grandTotal);
            return grandTotal;
        } catch (Exception e) {
            throw new RuntimeException("Could not get Grand Total: " + e.getMessage());
        }
    }

    public boolean verifyTotalMatches() {
        double calculatedTotal = calculateItemsTotal();
        double grandTotal = getGrandTotal();

        // Allow small difference due to rounding
        double difference = Math.abs(calculatedTotal - grandTotal);
        boolean matches = difference < 0.01;

        if (matches) {
            System.out.println("✓ Totals match!");
        } else {
            System.out.println("✗ Totals don't match! Difference: $" + difference);
        }

        return matches;
    }
}

