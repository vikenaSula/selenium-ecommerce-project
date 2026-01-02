package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.List;

public class SaleProductsPage extends BasePage {

    private final By saleProductsLocator = By.cssSelector(".products-grid > li, ul.products-grid li.item");
    private final By priceBoxLocator = By.cssSelector(".price-box");
    private final By oldPriceLocator = By.cssSelector(".old-price .price, .regular-price .price");
    private final By specialPriceLocator = By.cssSelector(".special-price .price");

    public SaleProductsPage(WebDriver driver) {
        super(driver);
    }

    public List<WebElement> getSaleProducts() {
        removeOverlays();

        wait.until(ExpectedConditions.presenceOfElementLocated(saleProductsLocator));

        List<WebElement> products = driver.findElements(saleProductsLocator);
        List<WebElement> displayedProducts = new ArrayList<>();

        System.out.println("Total elements found with locator: " + products.size());

        for (WebElement product : products) {
            try {
                if (product.isDisplayed() && product.getSize().getHeight() > 50) {
                    displayedProducts.add(product);
                }
            } catch (Exception e) {
            }
        }

        System.out.println("Filtered to actual product cards: " + displayedProducts.size() + " products on sale page");
        return displayedProducts;
    }

    public boolean hasMultiplePrices(WebElement product) {
        try {
            WebElement priceBox = product.findElement(priceBoxLocator);

            List<WebElement> oldPrices = priceBox.findElements(oldPriceLocator);
            List<WebElement> specialPrices = priceBox.findElements(specialPriceLocator);

            return !oldPrices.isEmpty() && !specialPrices.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    public WebElement getOriginalPriceElement(WebElement product) {
        try {
            WebElement priceBox = product.findElement(priceBoxLocator);
            return priceBox.findElement(oldPriceLocator);
        } catch (Exception e) {
            throw new RuntimeException("Could not find original price element in product");
        }
    }

    public WebElement getSpecialPriceElement(WebElement product) {
        try {
            WebElement priceBox = product.findElement(priceBoxLocator);
            return priceBox.findElement(specialPriceLocator);
        } catch (Exception e) {
            throw new RuntimeException("Could not find special price element in product");
        }
    }

    public String getColor(WebElement element) {
        return getCssValue(element, "color");
    }

    public String getTextDecoration(WebElement element) {
        return getCssValue(element, "text-decoration");
    }

    public boolean hasStrikethrough(WebElement element) {
        String textDecoration = getTextDecoration(element);
        return textDecoration.contains("line-through");
    }

    public boolean isGreyColor(String colorValue) {
        // Grey colors typically have equal or near-equal RGB values
        // Common grey patterns: rgb(128, 128, 128), rgba(128, 128, 128, 1)
        if (colorValue.contains("rgb")) {
            String rgbPart = colorValue.substring(colorValue.indexOf("(") + 1, colorValue.lastIndexOf(")"));
            String[] values = rgbPart.split(",");

            try {
                int r = Integer.parseInt(values[0].trim());
                int g = Integer.parseInt(values[1].trim());
                int b = Integer.parseInt(values[2].trim());

                int maxDiff = Math.max(Math.abs(r - g), Math.max(Math.abs(g - b), Math.abs(r - b)));
                boolean isGrey = maxDiff <= 20; // Allow small variance
                boolean notBlack = r > 50 || g > 50 || b > 50;
                boolean notWhite = r < 200 || g < 200 || b < 200;

                return isGrey && notBlack && notWhite;
            } catch (Exception e) {
                return false;
            }
        }
        return false;
    }


    public boolean isBlueColor(String colorValue) {
        if (!colorValue.contains("rgb")) {
            return false;
        }

        try {
            // Extract RGB values from "rgba(51, 153, 204, 1)" format
            String rgbPart = colorValue.substring(colorValue.indexOf("(") + 1, colorValue.lastIndexOf(")"));
            String[] values = rgbPart.split(",");

            int r = Integer.parseInt(values[0].trim());
            int g = Integer.parseInt(values[1].trim());
            int b = Integer.parseInt(values[2].trim());

            // Blue means B is higher than both R and G
            return b > r && b > g;
        } catch (Exception e) {
            return false;
        }
    }
}

