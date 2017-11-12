import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Enter {

    private static WebDriver driver;

    public static void main(String[] args) {
        driver = initDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.get("http://prestashop-automation.qatestlab.com.ua/admin147ajyvk0/");

        first();
        second();

        driver.quit();
    }

    public static WebDriver initDriver() {
        System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/drivers/chromedriver.exe");
        return new ChromeDriver();
    }

    public static void adminLogin() {
        driver.findElement(By.id("email")).click();
        driver.findElement(By.id("email")).clear();
        driver.findElement(By.id("email")).sendKeys("webinar.test@gmail.com");

        driver.findElement(By.id("passwd")).click();
        driver.findElement(By.id("passwd")).clear();
        driver.findElement(By.id("passwd")).sendKeys("Xcg7299bnSmMuRLp9ITw");

        driver.findElement(By.name("submitLogin")).submit();
    }

    public static void first() {
        adminLogin();
        driver.findElement(By.className("employee_avatar_small")).click();
        driver.findElement(By.id("header_logout")).click();
    }

    public static void second() {
        adminLogin();
        String title;
        List<WebElement> menuList = driver.findElements(By.cssSelector(".maintab > a"));

        for (int i = 0; i < menuList.size(); i++) {
            menuList.get(i).click();
            title = driver.findElement(By.tagName("h2")).getText();
            System.out.println(title);
            driver.navigate().refresh();
            if (!driver.findElement(By.tagName("h2")).getText().equals(title))
                System.out.println("Something wrong.");
            menuList = driver.findElements(By.cssSelector(".maintab > a"));
            if (menuList.size()==0) menuList = driver.findElements(By.cssSelector(".link-levelone > a"));
        }
    }
}
