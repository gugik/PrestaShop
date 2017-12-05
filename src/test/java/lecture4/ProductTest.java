package lecture4;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.List;
import java.util.concurrent.TimeUnit;


public class ProductTest {

    private EventFiringWebDriver webDriver;
    private Product newProduct;

    @DataProvider
    public Object[][] admin() {
        return new Object[][]{
                {"webinar.test@gmail.com", "Xcg7299bnSmMuRLp9ITw"},
        };
    }

    @Parameters({"driverName"})
    @BeforeTest
    public void startDriver(String driverName) {
        webDriver = configureDriver(driverName);
    }

    @AfterTest
    public void quitDriver() {
        webDriver.quit();
    }

    @Test(dataProvider = "admin")
    public void createProduct(String login, String password) {
        adminLogin(login, password);
        newProduct = new Product();
        addProduct(newProduct);
        adminLogout();
    }

    @Test(dependsOnMethods = "createProduct")
    public void checkProduct() {
        checkProductIsExist(newProduct);
    }

    private void adminLogout() {
        webDriver.findElement(By.className("employee_avatar_small")).click();
        webDriver.findElement(By.id("header_logout")).click();
    }

    private void addProduct(Product product) {
        getSubMenu("Каталог", "товары").click();
        webDriver.findElement(By.id("page-header-desc-configuration-add")).click();
        inputInField(webDriver.findElement(By.id("form_step1_name_1"))).sendKeys(product.getName());
        Actions action = new Actions(webDriver);
        action.moveToElement(webDriver.findElement(By.id("add_feature_button"))).perform();
        inputInField(webDriver.findElement(By.id("form_step1_qty_0_shortcut"))).sendKeys(String.valueOf(product.getAmount()));
        inputInField(webDriver.findElement(By.id("form_step1_price_shortcut"))).sendKeys(product.getPrice());
        webDriver.findElement(By.className("switch-input")).click();
        webDriver.findElement(By.cssSelector(".btn.btn-primary.js-btn-save")).click();
        Assert.assertTrue(webDriver.findElements(By.cssSelector(".alert.alert-success")).size()>0, " Product was not created.");
        webDriver.findElement(By.cssSelector(".logo.pull-left")).click();
    }

    private void checkProductIsExist(Product product) {
        webDriver.get("http://prestashop-automation.qatestlab.com.ua/");
        webDriver.findElement(By.cssSelector(".all-product-link.pull-xs-left.pull-md-right.h4")).click();

        searchProductOnPage(product);
        Assert.assertTrue(webDriver.findElement(By.className("h1")).getText().toLowerCase().equals(product.getName()),
                "Product name is not valid");

        String textPrice = webDriver.findElement(By.className("current-price")).getText();
        String priceExist = textPrice.substring(0, textPrice.indexOf(" "));

        Assert.assertTrue(priceExist.replace(",", ".").equals(product.getPrice()),
                "Product price is not valid");

        String textAmount = webDriver.findElement(By.cssSelector(".product-quantities > span")).getText();
        //String amountExist = textAmount.substring(0, textPrice.indexOf(" ") - 2);
        String amountExist = textAmount.substring(0, textAmount.indexOf(" "));


        Assert.assertTrue(amountExist.trim().equals(String.valueOf(product.getAmount())),
                "Product amount is not valid");

    }

    private void searchProductOnPage(Product product) {
        WebElement result = null;
        List<WebElement> productList = webDriver.findElements(By.cssSelector(".product-description"));
        System.out.println(product.getName());
        System.out.println(product.getAmount());
        System.out.println(product.getPrice());
        for (WebElement current : productList) {
            if (current.findElement(By.cssSelector(".h3.product-title > a")).getText().toLowerCase().equals(product.getName())) {
                result = current;
                result.findElement(By.cssSelector(".h3.product-title > a")).click();
                break;
            }
        }
        if (result == null) {
            webDriver.findElement(By.cssSelector("a.next.js-search-link")).click();
            searchProductOnPage(product);
        }
    }

    private WebElement inputInField(WebElement element) {
        element.click();
        element.clear();
        return element;
    }

    private void adminLogin(String login, String password) {
        webDriver.get("http://prestashop-automation.qatestlab.com.ua/admin147ajyvk0/");
        inputInField(webDriver.findElement(By.id("email"))).sendKeys(login);
        inputInField(webDriver.findElement(By.id("passwd"))).sendKeys(password);
        webDriver.findElement(By.name("submitLogin")).submit();
    }

    private EventFiringWebDriver configureDriver(String driverName) {
        WebDriver driver;
        switch (driverName) {
            case "chrome":
                System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/drivers/chromedriver.exe");
                driver = new ChromeDriver();
                break;

            case "firefox":
                System.setProperty("webdriver.gecko.driver", System.getProperty("user.dir") + "/drivers/geckodriver.exe");
                driver = new FirefoxDriver();
                break;

            default:
                System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/drivers/chromedriver.exe");
                driver = new ChromeDriver();
                break;
        }
        //System.setProperty("webdriver.chrome.webDriver", System.getProperty("user.dir") + "/drivers/chromedriver.exe");
        //WebDriver driver = new ChromeDriver();

        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        EventFiringWebDriver webDriver = new EventFiringWebDriver(driver);
        webDriver.register(new EventHolder());
        return webDriver;
    }


    private WebElement getSubMenu(String menu, String subMenu) {
        Actions action = new Actions(webDriver);
        action.moveToElement(getMenu(menu)).build().perform();

        List<WebElement> subMenuList = webDriver.findElements(By.cssSelector(".submenu > li > a"));
        WebElement result = null;
        for (WebElement current : subMenuList) {
            if (current.getText().contains(subMenu)) {
                result = current;
                break;
            }
        }
        return result;
    }

    private WebElement getMenu(String menu) {
        List<WebElement> menuList = webDriver.findElements(By.cssSelector(".maintab > a"));
        WebElement result = null;
        for (WebElement current : menuList) {
            if (current.getText().contains(menu)) {
                result = current;
                break;
            }
        }
        return result;
    }

}
