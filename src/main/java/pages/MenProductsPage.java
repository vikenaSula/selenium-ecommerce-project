package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.ArrayList;
import java.util.List;

public class MenProductsPage extends BasePage {

    private final By productListLocator = By.cssSelector(".products-grid > li, ul.products-grid li.item");
    private final By priceBoxLocator = By.cssSelector(".price-box .price");

    public MenProductsPage(WebDriver driver) {
        super(driver);
    }

    public List<WebElement> getDisplayedProducts() {
        removeOverlays();
        wait.until(ExpectedConditions.presenceOfElementLocated(productListLocator));

        // Scroll to ensure all products are loaded
        js.executeScript("window.scrollTo(0, document.body.scrollHeight);");

        List<WebElement> products = driver.findElements(productListLocator);
        List<WebElement> displayedProducts = new ArrayList<>();

        for (WebElement product : products) {
            try {
                List<WebElement> productNameElements = product.findElements(By.cssSelector(".product-name"));
                if (!productNameElements.isEmpty() && productNameElements.get(0).isDisplayed()) {
                    displayedProducts.add(product);
                }
            } catch (Exception e) {
            }
        }

        return displayedProducts;
    }


    public void selectBlackColor() {
        removeOverlays();

        WebElement blackColorLink = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//a[@class='swatch-link has-image']//img[contains(translate(@alt,'BLACK','black'),'black')]/ancestor::a")));

        scrollToElement(blackColorLink);
        blackColorLink.click();

        waitForPageReady();
    }

    public void selectFirstPriceOption() {
        removeOverlays();
        WebElement firstPriceOption = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("(//a[contains(@href,'price=')])[1]")));

        scrollToElement(firstPriceOption);

        String priceText = firstPriceOption.getText();
        System.out.println("Selecting price filter: " + priceText);

        firstPriceOption.click();

        waitForPageReady();
        wait.until(ExpectedConditions.urlContains("price="));
    }

    public double getProductPrice(WebElement product) {
        try {
            WebElement priceElement = product.findElement(priceBoxLocator);
            String priceText = priceElement.getText().trim();

            priceText = priceText.replaceAll("[^0-9.]", "");
            return Double.parseDouble(priceText);
        } catch (Exception e) {
            throw new RuntimeException("Could not get product price: " + e.getMessage());
        }
    }
}
