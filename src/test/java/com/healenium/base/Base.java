package com.healenium.base;

import java.awt.Desktop;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import com.epam.healenium.SelfHealingDriver;
import com.healenium.pages.AddToCartPage;
import com.healenium.pages.HomePage;
import com.healenium.pages.LoginPage;
import com.healenium.pages.SideBarValidation;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.google.gson.JsonObject;


public class Base {

    public static ExtentReports extent;
    public static ExtentTest test;
//    public WebDriver driver;
    public SelfHealingDriver driver;
    public JsonObject jsonObject;
    public Properties properties;
    private final JsonDataConverter converter = new JsonDataConverter();
    public LoginPage loginPage;
    public AddToCartPage addToCartPage;
    public SideBarValidation sideBarValidation;
    public HomePage homePage;

    @BeforeSuite(alwaysRun = true)
    public void getWebDriver() {
//        driver = new ChromeDriver();
        //declare delegate
        WebDriver delegate = new ChromeDriver();
        //create Self-healing driver
        driver = SelfHealingDriver.create(delegate);
        properties = new Properties();
        loginPage = new LoginPage(driver);
        addToCartPage = new AddToCartPage(driver);
        sideBarValidation = new SideBarValidation(driver);
        homePage = new HomePage(driver);
        FileReader fileReader;
        try {
            fileReader = new FileReader(
                    System.getProperty("user.dir") + "/src/test/resources/saucedemo.properties");
            properties.load(fileReader);
            jsonObject = converter.getJsonObject(properties.getProperty("orangehrmDataPath"));
            fileReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @BeforeTest
    public void startReport() {
        ExtentSparkReporter spark = new ExtentSparkReporter("./reports/Extentreport.html");
        extent = new ExtentReports();
        extent.attachReporter(spark);
        spark.config().setTheme(Theme.DARK);
        spark.config().setDocumentTitle("MyReport");
        spark.config().setReportName("Test Report");
        spark.config().setTimeStampFormat("EEEE, MMMM dd, yyyy, hh:mm a '('zzz')'");
    }

    private String captureScreenshot(String testName, String directoryPath) {
        String screenshotPath = null;
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedTime = currentTime.format(formatter);
        try {
            TakesScreenshot ts =  (TakesScreenshot) driver;
            File screenshotFile = ts.getScreenshotAs(OutputType.FILE);
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            System.out.println(testName + "    Test Name");
            screenshotPath = directoryPath + "/" + testName + formattedTime + ".png";
            FileUtils.copyFile(screenshotFile, new File(screenshotPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(screenshotPath);
        return screenshotPath;
    }


    @AfterMethod
    public void getResult(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            test.log(Status.FAIL, result.getThrowable());
            String path = captureScreenshot(result.getName(), System.getProperty("user.dir") + properties.getProperty("imagesPath"));
            test.log(Status.FAIL, "Test Case Failed");
            System.out.println(System.getProperty("user.dir") + properties.getProperty("imagesPath"));
            System.out.println(path);
            test.log(Status.FAIL, MarkupHelper.createLabel(result.getName() + " - Test Case Failed", ExtentColor.RED));
            test.fail("Screenshot below: " + test.addScreenCaptureFromPath(path));
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            test.log(Status.PASS, "Test Case Passed Sucessfully");
        } else {
            test.log(Status.SKIP, result.getTestName());
        }
    }

    @AfterTest
    public void endReport() {
        extent.flush();
    }

    @AfterSuite
    public void tearDown() throws Exception {
        driver.quit();
    }



}
