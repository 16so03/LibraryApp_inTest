package com.libraryApp.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import com.libraryApp.utilities.Driver;

import java.security.Key;
import java.util.List;

public class LibraryPage {
    public LibraryPage(){
        PageFactory.initElements(Driver.get(), this);
    }
    @FindBy(xpath = "//input[@id='inputEmail']")
    public WebElement emailField;

    @FindBy(xpath = "//input[@id='inputPassword']")
    public WebElement passwordField;

    @FindBy(xpath = "//button[@class='btn btn-lg btn-primary btn-block']")
    public WebElement submitButton;

    @FindBy(xpath = "(//span[@class='title'])[3]")
    public WebElement Books;

    @FindBy(xpath = "//input[@type='search']")
    public WebElement searchField;

    @FindBy(xpath = "//table[@id='tbl_books']")
    public WebElement table;

    public void libraryLogin(String email, String password){
        emailField.sendKeys(email);
        passwordField.sendKeys(password);
        submitButton.click();
    }

    public void navigateTo(String navigationBar){
        if (navigationBar.equals("Books")){
            Books.click();
        }
    }

//    public List tablecontent(){
//        List<WebElement> rows = table.findElements(By.tagName("tr"));
//        return rows;
//    }

    public void tablecontent_find(String look){
        List<WebElement> rows = table.findElements(By.tagName("tr"));
        for (WebElement row : rows){
            List<WebElement> cells = row.findElements(By.tagName("td"));
            for (WebElement cell : cells) {
                if (cell.getText().contains(look)){
                    System.out.println(look+" is Displayed");
                }
            }
        }
    }
    public void SearchForBook(String searchText){
        searchField.sendKeys(searchText+ Keys.ENTER);

    }
}
