package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class HomePage extends BasePage {

    private static final String BASE_URL = "https://ecommerce.tealiumdemo.com/";


    public HomePage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        navigateTo(BASE_URL);
        dismissCookieConsent();
        waitForPageReady();
    }

    private void navigateToCategory(String menuText, String expectedUrlPart) {
        waitForPageReady();
        removeOverlays();

        // Trying multiple locator strategies for the menu
        By[] menuLocators = {
            By.xpath("//nav//a[contains(text(),'" + menuText + "')]"),
            By.xpath("//a[contains(@class,'level-top') and contains(text(),'" + menuText + "')]"),
            By.xpath("//a[contains(@href,'/" + expectedUrlPart + ".html')]"),
            By.xpath("//span[contains(text(),'" + menuText + "')]/parent::a")
        };

        WebElement menu = null;
        for (By locator : menuLocators) {
            try {
                menu = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
                if (menu.isDisplayed()) {
                    break;
                }
            } catch (Exception e) {
                continue;
            }
        }

        if (menu == null) {
            throw new RuntimeException("Could not find " + menuText + " menu");
        }

        scrollToElement(menu);
        actions.moveToElement(menu).perform();

        // Wait a moment for submenu to appear
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        By[] viewAllLocators = {
            By.xpath("//a[contains(text(),'View All') and contains(@href,'" + expectedUrlPart + "')]"),
            By.xpath("//a[contains(@href,'/" + expectedUrlPart + ".html')]"),
            By.xpath("//a[text()='View All']")
        };

        WebElement viewAllLink = null;
        for (By locator : viewAllLocators) {
            try {
                viewAllLink = wait.until(ExpectedConditions.elementToBeClickable(locator));
                String href = viewAllLink.getAttribute("href");
                if (href != null && href.contains(expectedUrlPart)) {
                    break;
                }
            } catch (Exception e) {
                continue;
            }
        }

        if (viewAllLink == null) {
            throw new RuntimeException("Could not find View All link for " + menuText);
        }

        viewAllLink.click();

        wait.until(ExpectedConditions.urlContains(expectedUrlPart));
        waitForPageReady();
    }


    public WomenProductsPage navigateToWomenProducts() {
        navigateToCategory("WOMEN", "women");
        return new WomenProductsPage(driver);
    }

    public SaleProductsPage navigateToSaleProducts() {
        navigateToCategory("SALE", "sale");
        return new SaleProductsPage(driver);
    }

    public MenProductsPage navigateToMenProducts() {
        waitForPageReady();
        removeOverlays();

        By[] menMenuLocators = {
            By.xpath("//nav//a[contains(text(),'MEN')]"),
            By.xpath("//a[contains(@class,'level-top') and contains(text(),'MEN')]"),
            By.xpath("//a[contains(@href,'/men.html') and not(contains(@href,'women'))]"),
            By.xpath("//span[contains(text(),'MEN')]/parent::a")
        };

        WebElement menMenu = null;
        for (By locator : menMenuLocators) {
            try {
                menMenu = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
                if (menMenu.isDisplayed()) {
                    break;
                }
            } catch (Exception e) {
                continue;
            }
        }

        if (menMenu == null) {
            throw new RuntimeException("Could not find MEN menu");
        }

        scrollToElement(menMenu);
        actions.moveToElement(menMenu).perform();

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        By[] viewAllLocators = {
            By.xpath("//a[contains(text(),'View All') and contains(@href,'men') and not(contains(@href,'women'))]"),
            By.xpath("//a[contains(@href,'/men.html')]"),
            By.xpath("//a[text()='View All Men' or text()='View All']")
        };

        WebElement viewAllLink = null;
        for (By locator : viewAllLocators) {
            try {
                viewAllLink = wait.until(ExpectedConditions.elementToBeClickable(locator));
                String href = viewAllLink.getAttribute("href");
                if (href != null && !href.contains("women")) {
                    break;
                }
            } catch (Exception e) {
                continue;
            }
        }

        if (viewAllLink == null) {
            throw new RuntimeException("Could not find View All link for Men menu");
        }

        viewAllLink.click();

        wait.until(ExpectedConditions.urlContains("/men"));

        String currentUrl = driver.getCurrentUrl();
        if (currentUrl != null && currentUrl.contains("women")) {
            throw new RuntimeException("ERROR: Navigated to WOMEN's page instead of MEN's page!");
        }

        waitForPageReady();

        return new MenProductsPage(driver);
    }
}

