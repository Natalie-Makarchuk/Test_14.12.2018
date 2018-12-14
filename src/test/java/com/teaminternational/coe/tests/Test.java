import com.github.javafaker.Faker;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Test_to_copy {

    private WebDriver driver;
    private Wait<WebDriver> wait;


    @BeforeTest
    public void setupTest() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--disable-extensions");
        chromeOptions.addArguments("--incognito");
        chromeOptions.addArguments("--disable-plugins-discovery");
        chromeOptions.addArguments("--start-maximized");
        DesiredCapabilities dcap = new DesiredCapabilities();
        driver = new ChromeDriver(chromeOptions);
        wait = new FluentWait<>(driver).withMessage("Element was not found").withTimeout(10, TimeUnit.SECONDS).pollingEvery(1, TimeUnit.SECONDS);
    }

    @AfterTest
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @org.testng.annotations.Test
    public void SauceDemoTest() {
        Faker faker = new Faker();
        String firstName = faker.gameOfThrones().character();
        String lastName = "of " + faker.gameOfThrones().city();
        String postalCode = faker.address().zipCode();

        navigate("https://www.saucedemo.com/");

        untilElementVisible(By.cssSelector("[data-test='username']")).sendKeys("standard_user");
        untilElementVisible(By.cssSelector("[data-test='password']")).sendKeys("secret_sauce");
        untilElementClickable(By.className("login-button")).click();

        Assert.assertEquals(untilElementVisible(By.className("product_label")).getText(), "Products",
                "Page label is incorrect");
        Assert.assertEquals(wait.until(ExpectedConditions.
                        visibilityOfAllElementsLocatedBy(By.className("inventory_item"))).size(), 6,
                "Number of items is incorrect");

        List<WebElement> elements = wait.until(ExpectedConditions.
                visibilityOfAllElementsLocatedBy(By.className("inventory_item_price")));
        elements.sort(Comparator.comparing(a -> Float.valueOf(a.getText().replace("$", ""))));

        elements.get(0).findElement(By.xpath("following-sibling::button")).click();
        Assert.assertEquals(untilElementVisible(By.className("fa-layers-counter")).getText(),
                "1", "Number of selected items is incorrect");

        untilElementClickable(By.className("fa-shopping-cart")).click();
        Assert.assertEquals(untilElementVisible(By.className("subheader_label")).getText(),
                "Your Cart", "Cart label is incorrect");
        Assert.assertEquals(untilElementVisible(By.className("fa-layers-counter")).getText(),
                "1", "Number of selected items is incorrect");

        Assert.assertEquals(untilElementVisible(By.className("inventory_item_name")).getText(),
                "Sauce Labs Onesie", "Name is incorrect");
        Assert.assertEquals(untilElementVisible(By.className("inventory_item_desc")).getText(),
                "Rib snap infant onesie for the junior automation engineer in development. " +
                        "Reinforced 3-snap bottom closure, two-needle hemmed sleeved and bottom won't unravel.",
                "Desc is incorrect");
        Assert.assertEquals(untilElementVisible(By.className("inventory_item_price")).getText(),
                "7.99", "Price is incorrect");

        untilElementClickable(By.className("cart_checkout_link")).click();

        Assert.assertEquals(untilElementVisible(By.className("subheader_label")).getText(),
                "Checkout: Your Information", "Cart label is incorrect");
        untilElementVisible(By.cssSelector("[data-test='firstName']")).sendKeys(firstName);
        untilElementVisible(By.cssSelector("[data-test='lastName']")).sendKeys(lastName);
        untilElementVisible(By.cssSelector("[data-test='postalCode']")).sendKeys(postalCode);
        untilElementClickable(By.className("cart_checkout_link")).click();

        Assert.assertEquals(untilElementVisible(By.className("subheader_label")).getText(),
                "Checkout: Overview", "Cart label is incorrect");
        Assert.assertEquals(untilElementVisible(By.className("fa-layers-counter")).getText(),
                "1", "Number of selected items is incorrect");

        Assert.assertEquals(untilElementVisible(By.className("inventory_item_name")).getText(),
                "Sauce Labs Onesie", "Name is incorrect");
        Assert.assertEquals(untilElementVisible(By.className("inventory_item_desc")).getText(),
                "Rib snap infant onesie for the junior automation engineer in development. " +
                        "Reinforced 3-snap bottom closure, two-needle hemmed sleeved and bottom won't unravel.",
                "Desc is incorrect");
        Assert.assertEquals(untilElementVisible(By.className("inventory_item_price")).getText(),
                "$7.99", "Price is incorrect");

        Assert.assertEquals(untilElementVisible(By.xpath("//div[@class='summary_info_label']" +
                "[contains(text(),'Payment Information')]/following-sibling::div[1]")).getText(), "SauceCard #31337");
        Assert.assertEquals(untilElementVisible(By.xpath("//div[@class='summary_info_label']" +
                "[contains(text(),'Shipping Information')]/following-sibling::div[1]")).getText(), "FREE PONY EXPRESS DELIVERY!");

        Assert.assertEquals(untilElementVisible(By.className("summary_subtotal_label")).getText(), "Item total: $7.99", "Item subotal is incorrect");
        Assert.assertEquals(untilElementVisible(By.className("summary_tax_label")).getText(), "Tax: $0.64", "Tax is incorrect");
        Assert.assertEquals(untilElementVisible(By.className("summary_total_label")).getText(), "Total: $8.63", "Item Total is incorrect");

        untilElementClickable(By.className("cart_checkout_link")).click();

        Assert.assertEquals(untilElementVisible(By.className("subheader_label")).getText(),
                "Checkout: Complete!", "Checkout label is incorrect");
        Assert.assertTrue(untilElementNotVisible(By.className("fa-layers-counter")), "Counter is still visible");
        Assert.assertEquals(untilElementVisible(By.className("complete-header")).getText(), "THANK YOU FOR YOUR ORDER", "Thank you not displayed");
        Assert.assertEquals(untilElementVisible(By.className("complete-text")).getText(), "Your order has been dispatched, and will arrive just as fast as the pony can get there!", "Dispatch message is not displayed");
        Assert.assertTrue(untilElementVisible(By.tagName("img")).isDisplayed(), "Image not displayed");
    }

    private WebElement untilElementVisible(By by) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    private boolean untilElementNotVisible(By by) {
        return wait.until(ExpectedConditions.invisibilityOfElementLocated(by));
    }

    private WebElement untilElementClickable(By by) {
        return wait.until(ExpectedConditions.elementToBeClickable(by));
    }

    public static void suspend(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void navigate(String url) {
        driver.navigate().to(url);
    }
}
