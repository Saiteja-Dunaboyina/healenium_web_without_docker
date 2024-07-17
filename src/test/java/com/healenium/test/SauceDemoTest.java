package com.healenium.test;

import com.healenium.base.Base;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

import java.time.Duration;

public class SauceDemoTest extends Base {

    @BeforeMethod
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver.manage().window().maximize();
        driver.get("https://www.saucedemo.com/");
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(20));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(60));
    }

    @Test
    public void loginTest() {
        test = extent.createTest("Checking the login functionality").assignAuthor("sai teja");
        String username = jsonObject.get("user").getAsString();
        String password = jsonObject.get("password").getAsString();
        loginPage.enterUsernameAndPassword(username,password);
        loginPage.clickOnLoginButton();
        String headerText = jsonObject.get("headerText").getAsString();
        assertEquals(loginPage.getHeaderText() ,headerText);
        loginPage.logout();

    }
}
