package lecture5;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class BuyProductTest {

    // Selenium Grid
    private WebDriver webDriver;
    //private EventFiringWebDriver webDriver;
    private Product product;
    private List<WebElement> productsList;
    private String driverName;

    @Parameters({"driverName"})
    @BeforeClass
    public void startDriver(String driverName) throws MalformedURLException {
        webDriver = configureDriver(driverName);

        webDriver.get("http://prestashop-automation.qatestlab.com.ua/");
    }

    //@AfterClass
    public void quitDriver() {
        webDriver.quit();
    }

    @Test
    public void correctDisplayed() {
        isCorrectDisplayed();
    }

    @Test
    public void chooseProductToCart() {
        addProductToCart();
        checkProductInCart();
    }

    @Test(dependsOnMethods = "chooseProductToCart")
    public void makeOrder() {
        createOrder();
    }

    @Test(dependsOnMethods = "makeOrder")
    public void checkQuantityProducts() {
        Assert.assertTrue(checkQuantityChange(), "Product amount is not change.");
    }

    private void findMyProduct() {
        WebElement result = null;

        searchProductsOnPage();

        for (WebElement current : productsList) {
            if (current.getText().equalsIgnoreCase(product.getName())) {
                result = current.findElement(By.cssSelector(".product-title"));
                result.click();
                break;
            }
        }

        if (result == null) {
            webDriver.findElement(By.cssSelector("a.next.js-search-link")).click();
            findMyProduct();
        }
    }

    public boolean checkQuantityChange() {
        boolean result = false;
        webDriver.get("http://prestashop-automation.qatestlab.com.ua/");
        findMyProduct();

        if (webDriver.findElements(By.cssSelector(".nav-link")).size() > 1) {
            webDriver.findElements(By.cssSelector(".nav-link")).get(1).click();

            (new WebDriverWait(webDriver, 10))
                    .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-quantities > span")));
        }

        String amountText = webDriver.findElement(By.cssSelector(".product-quantities > span")).getText();
        String amount = amountText.substring(0, amountText.indexOf(" "));

        String amountProduct = String.valueOf(Integer.valueOf(product.getAmount()) - 1);

        if (amount.equals(amountProduct)) result = true;

        webDriver.findElement(By.cssSelector(".text-xs-center > .btn.btn-primary")).click();
        return result;
    }

    public void createOrder() {

        inputInField(webDriver.findElement(By.cssSelector("[name=firstname]"))).sendKeys(getRandomWord());
        inputInField(webDriver.findElement(By.cssSelector("[name=lastname]"))).sendKeys(getRandomWord());
        inputInField(webDriver.findElement(By.cssSelector("[name=email]"))).sendKeys(getRandomWord() + "@" + getRandomWord() + "." + getRandomWord());

        webDriver.findElement(By.cssSelector("#customer-form > .form-footer.clearfix > .continue.btn.btn-primary")).click();

        inputInField(webDriver.findElement(By.cssSelector("[name=address1]"))).sendKeys(getRandomWord());
        inputInField(webDriver.findElement(By.cssSelector("[name=postcode]"))).sendKeys("03928");
        inputInField(webDriver.findElement(By.cssSelector("[name=city]"))).sendKeys("Киев");

        webDriver.findElement(By.cssSelector(".js-address-form > .form-footer.clearfix > .continue.btn.btn-primary")).click();

        webDriver.findElement(By.cssSelector("#js-delivery > .continue.btn.btn-primary")).click();

        webDriver.findElement(By.id("payment-option-1")).click();
        webDriver.findElement(By.id("conditions_to_approve[terms-and-conditions]")).click();

        webDriver.findElement(By.cssSelector("#payment-confirmation > .ps-shown-by-js > .btn.btn-primary")).click();

        Assert.assertTrue(
                webDriver.findElement(By.cssSelector(".h1.card-title")).getText().toLowerCase()
                        .substring(1).equals("ваш заказ подтверждён"),
                "Order is not confirm.");

        String detailsText = webDriver.findElement(By.cssSelector(".details")).getText();
        String nameText = detailsText.substring(0, (detailsText.indexOf(":") - 8)).trim();

        String quantityText = webDriver.findElement(By.cssSelector(".qty")).getText();
        String priceText = quantityText.substring(0, detailsText.indexOf(" "));

        String amountText = webDriver.findElement(By.cssSelector(".col-xs-2")).getText();

        Assert.assertTrue(nameText.toLowerCase().equals(product.getName().toLowerCase()),
                "Product name is not valid");

        Assert.assertTrue(priceText.substring(0, priceText.indexOf(" ")).equals(product.getPrice()), "Product price is not valid");

        Assert.assertTrue(amountText.equals("1"), "Product amount is not valid");
    }

    private String getRandomWord() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < (3 + (int) (Math.random() * 15)); i++) {
            char c = (char) (new Random().nextInt(26) + 'a');
            sb.append(c);
        }
        return sb.toString();
    }

    private WebElement inputInField(WebElement element) {
        element.click();
        element.clear();
        return element;
    }

    public void checkProductInCart() {
        String nameText = webDriver.findElement(By
                .cssSelector(".product-line-grid > .product-line-grid-body > .product-line-info > a")).getText();
        String priceText = webDriver.findElement(By.cssSelector(".product-price")).getText();
        String amountText = webDriver.findElement(By.cssSelector(".js-cart-line-product-quantity.form-control"))
                .getAttribute("value");

        Assert.assertTrue(nameText.toLowerCase().equals(product.getName().toLowerCase()),
                "Product name is not valid");

        Assert.assertTrue(priceText.substring(0, priceText.indexOf(" ")).equals(product.getPrice()),
                "Product price is not valid");

        Assert.assertTrue(amountText.equals("1"), "Product amount is not valid");

        webDriver.findElement(By.cssSelector(".text-xs-center > .btn.btn-primary")).click();
    }

    public void addProductToCart() {
        searchProductsOnPage();
        int randomNumberProduct = (int) (Math.random() * (productsList.size() - 1));
        productsList.get(randomNumberProduct).findElement(By.cssSelector(".h3.product-title > a")).click();
        product = createProduct();
        webDriver.findElement(By.cssSelector(".btn.btn-primary.add-to-cart")).click();
        (new WebDriverWait(webDriver, 10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".cart-content > a.btn.btn-primary")));
        webDriver.findElement(By.cssSelector(".cart-content > a.btn.btn-primary")).click();
    }

    private void searchProductsOnPage() {
        webDriver.findElement(By.cssSelector(".all-product-link")).click();
        productsList = webDriver.findElements(By.cssSelector(".product-description"));
    }

    private Product createProduct() {
        String nameText = webDriver.findElement(By.className("h1")).getText();

        String priceText = webDriver.findElement(By.className("current-price")).getText();
        String price = priceText.substring(0, priceText.indexOf(" "));

        if (!webDriver.findElement(By.cssSelector(".product-quantities > span")).isDisplayed())
            webDriver.findElements(By.cssSelector(".nav-link")).get(1).click();

        (new WebDriverWait(webDriver, 10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".product-quantities > span")));

        String amountText = webDriver.findElement(By.cssSelector(".product-quantities > span")).getText();
        String amount = amountText.substring(0, amountText.indexOf(" "));

        return new Product(nameText, amount, price);
    }

    public void isCorrectDisplayed() {
        if (driverName.equals("mobile")) {
            Assert.assertTrue(webDriver.findElement(By.cssSelector(".mobile")).isDisplayed());
        } else Assert.assertFalse(webDriver.findElement(By.cssSelector(".mobile")).isDisplayed());
    }

/*
    public EventFiringWebDriver configureDriver(String name) {
        driverName = name;
        WebDriver driver;
        switch (driverName) {
            case "chrome":
                System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/drivers/chromedriver.exe");
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--headless");
                driver = new ChromeDriver(chromeOptions);
                break;

            case "firefox":
                System.setProperty("webdriver.gecko.driver", System.getProperty("user.dir") + "/drivers/geckodriver.exe");
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.addArguments("--headless");
                driver = new FirefoxDriver(firefoxOptions);
                break;

            case "mobile":
                Map<String, String> mobiles = new HashMap<>();
                mobiles.put("mobile", "iPhone 5");
                System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/drivers/chromedriver.exe");
                ChromeOptions options = new ChromeOptions();
                options.setExperimentalOption("mobile", mobiles);
                driver = new ChromeDriver(options);
                break;

            default:
                System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/drivers/chromedriver.exe");
                driver = new ChromeDriver();
                break;
        }

        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        EventFiringWebDriver webDriver = new EventFiringWebDriver(driver);
        webDriver.register(new EventHolder());
        return webDriver;
    }*/

// Selenium Grid
    private RemoteWebDriver configureDriver(String name) throws MalformedURLException {
        driverName = name;
        RemoteWebDriver driver;
        DesiredCapabilities capabilities;
        switch (driverName) {
            case "chrome":
                capabilities = DesiredCapabilities.chrome();
                driver = new RemoteWebDriver(new URL("http://127.0.0.1:4444/wd/hub"), capabilities);
                break;

            case "firefox":
                capabilities = DesiredCapabilities.firefox();
                driver = new RemoteWebDriver(new URL("http://127.0.0.1:4444/wd/hub"), capabilities);
                break;

            case "android":
                capabilities = DesiredCapabilities.android();
                driver = new RemoteWebDriver(new URL("http://127.0.0.1:4444/wd/hub"), capabilities);
                break;

            default:
                capabilities = DesiredCapabilities.chrome();
                driver = new RemoteWebDriver(new URL("http://127.0.0.1:4444/wd/hub"), capabilities);
                break;
        }
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        return driver;
    }
}
