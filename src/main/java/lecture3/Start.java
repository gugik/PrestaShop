package lecture3;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.events.EventFiringWebDriver;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Start {


    private static EventFiringWebDriver webDriver;

    public static void main(String[] args) {
        webDriver = configureDriver();
        webDriver.get("http://prestashop-automation.qatestlab.com.ua/admin147ajyvk0/");
        adminLogin();
        getSubMenu("Каталог", "категории").click();
        addCategory("New Category");
        checkCategoryIsExsistByName("New Category");
        logOut();
        webDriver.quit();
    }

    public static WebDriver initDriver() {
        System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/drivers/chromedriver.exe");
        return new ChromeDriver();
    }

    public static void adminLogin() {
        inputInField(webDriver.findElement(By.id("email"))).sendKeys("webinar.test@gmail.com");
        inputInField(webDriver.findElement(By.id("passwd"))).sendKeys("Xcg7299bnSmMuRLp9ITw");
        webDriver.findElement(By.name("submitLogin")).submit();
    }

    public static EventFiringWebDriver configureDriver() {
        System.setProperty("webdriver.chrome.webDriver", System.getProperty("user.dir") + "/drivers/chromedriver.exe");
        WebDriver driver = initDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        EventFiringWebDriver webDriver = new EventFiringWebDriver(driver);
        webDriver.register(new EventHolder());
        return webDriver;
    }

    public static void logOut() {
        webDriver.findElement(By.className("employee_avatar_small")).click();
        webDriver.findElement(By.id("header_logout")).click();
    }

    public static WebElement getSubMenu(String menu, String subMenu) {
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

    private static WebElement getMenu(String menu) {
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

    public static void addCategory(String name) {
        webDriver.findElement(By.id("page-header-desc-category-new_category")).click();
        inputInField(webDriver.findElement(By.id("name_1"))).sendKeys(name);
        webDriver.findElement(By.id("category_form_submit_btn")).click();
        if (!webDriver.findElement(By.className("alert-success")).isDisplayed())
            System.out.println("Something wrong.");
        else System.out.println("New category was created.");
        webDriver.findElement(By.id("desc-category-back")).click();
    }

    public static void checkCategoryIsExsistByName(String name) {
        WebElement sortByName = null;
        for (WebElement current : webDriver.findElements(By.className("title_box"))) {
            if (current.getText().equals("Имя")) sortByName = current;
        }
        sortByName.findElement(By.className("icon-caret-up")).click();
        WebElement result = null;
        for (WebElement current : webDriver.findElements(By.cssSelector("#table-category > tr > td"))) {
            if (!current.getText().equals(name))
                System.out.println("Something wrong.");
            else System.out.println("New category is exist.");
        }
    }

    private static WebElement inputInField(WebElement element) {
        element.clear();
        element.click();
        return element;
    }

}
