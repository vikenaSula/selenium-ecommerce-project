package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/** Base Page Object containing common functionality for all pages */
public class BasePage {
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected Actions actions;
    protected JavascriptExecutor js;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        this.actions = new Actions(driver);
        this.js = (JavascriptExecutor) driver;
    }

    public void navigateTo(String url) {
        driver.get(url);

        // Try to maximize window, ignore if already maximized
        try {
            driver.manage().window().maximize();
        } catch (Exception e) {
            // Window already maximized or can't change state, ignore
        }

        waitForPageReady();
    }

    protected void waitForPageReady() {
        wait.until(webDriver -> js.executeScript("return document.readyState").equals("complete"));
    }


    protected void dismissCookieConsent() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
            shortWait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".privacy_prompt")));

            // Click Opt-In radio
            try {
                WebElement optIn = shortWait.until(ExpectedConditions.elementToBeClickable(By.id("privacy_pref_optin")));
                optIn.click();
            } catch (Exception ignored) {
            }

            // Click SUBMIT button
            try {
                WebElement submitDiv = shortWait.until(ExpectedConditions.elementToBeClickable(By.id("consent_prompt_submit")));
                js.executeScript("arguments[0].click();", submitDiv);
            } catch (Exception e) {
                try {
                    WebElement submitDiv = driver.findElement(By.cssSelector("#consent_prompt_submit"));
                    submitDiv.click();
                } catch (Exception ignored) {
                    js.executeScript("var p=document.querySelector('.privacy_prompt'); if(p){p.style.display='none';}");
                }
            }

            try {
                new WebDriverWait(driver, Duration.ofMillis(500))
                    .until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".privacy_prompt")));
            } catch (Exception ignored) {
            }
        } catch (Exception e) {
            System.out.println("Cookie consent not shown or already handled.");
        }
    }

    /** removes consent/cookie overlays if still presen */
    protected void removeOverlays() {
        try {
            String jsScript = "var els=document.querySelectorAll('[class*=\"consent\"],[class*=\"cookie\"],[class*=\"overlay\"],[class*=\"modal\"],[id*=\"notice\"]'); " +
                    "els.forEach(e=>e.remove()); " +
                    "var notice=document.getElementById('notice-container'); if(notice) notice.remove(); " +
                    "document.body.style.overflow='auto';";
            js.executeScript(jsScript);
        } catch (Exception ignored) {
        }
    }

    protected void scrollToElement(WebElement element) {
        js.executeScript("arguments[0].scrollIntoView({block:'center'});", element);
    }

    /** Get CSS property value from element */
    protected String getCssValue(WebElement element, String property) {
        return element.getCssValue(property);
    }

    protected void hoverOver(WebElement element) {
        actions.moveToElement(element).perform();
        try {
            new WebDriverWait(driver, Duration.ofMillis(600)).until(d -> true);
        } catch (Exception ignored) {
        }
    }


    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String getPageTitle() {
        return driver.getTitle();
    }
}

