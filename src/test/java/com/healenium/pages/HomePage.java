package com.healenium.pages;

import com.epam.healenium.SelfHealingDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

public class HomePage {

    public SelfHealingDriver driver;
    @FindBy(xpath = "//select[@class='product_sort_container']")
    WebElement filterDropdown;
    @FindBy(xpath = "//div[text()='Test.allTheThings() T-Shirt (Red)']")
    WebElement productName;


    public HomePage(SelfHealingDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void filterProducts(String value) {
        Select dropdown = new Select(filterDropdown);
        dropdown.selectByValue(value);
    }

    public String getProductText() {
        return productName.getText();
    }
}
