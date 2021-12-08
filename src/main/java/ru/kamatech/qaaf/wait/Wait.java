package ru.kamatech.qaaf.wait;


import ru.kamatech.qaaf.locator.LocatorType;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Wait  {


    public void getWait(WebDriver driver, String idLocator){

        WebElement dynamicElement = (new WebDriverWait(driver, 30))
                .until(ExpectedConditions.presenceOfElementLocated(By.id(idLocator)));

    }
    public void invisibilityOfElementByClassName(WebDriver driver, String locator){
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className(locator)));
    }
    public void elementIsDisplayed(WebElement window, LocatorType locatorType, String element, int interval)  {
        int i=0;
        while (!isDisplayed(window, locatorType, element))
        {

            try {
                Thread.sleep(interval*1000);
                i++;
                if(i==5) {
                    throw new RuntimeException();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Element "+element+" is not visible yet");
        }
    }
    private boolean isDisplayed(WebElement window, LocatorType locatorType, String element) {

                    switch(locatorType) {
                        case className:
                    try{
                        if(window.findElement(By.className(element)).isDisplayed()){
                            System.out.println("Element "+element+" is visible");
                            return true;
                        }
                    }
                    catch (NoSuchElementException ex){
                        return false;
                    }
                    break;

                        case name:
                    try{
                        if(window.findElement(By.name(element)).isDisplayed()){
                            System.out.println("Element "+element+" is visible");
                            return true;
                        }
                    }
                    catch (NoSuchElementException ex){
                        return false;
                    }
                    break;
            }


        return false;
    }

    public void waitClickableWithClassName(WebDriver driver, WebElement window, String element){
        new WebDriverWait( driver, 30).until(ExpectedConditions.elementToBeClickable(window.findElement(By.className(element))));
    }
    public void waitClickableWithName(WebDriver driver, WebElement window, String element){
        //new WebDriverWait( driver, 10).until(ExpectedConditions.elementToBeClickable(By.name(element)));
        new WebDriverWait( driver, 30).until(ExpectedConditions.elementToBeClickable(window.findElement(By.name(element))));
    }
    public boolean verifyClickableWithName(WebDriver driver, WebElement window, String element){
        //new WebDriverWait( driver, 10).until(ExpectedConditions.elementToBeClickable(By.name(element)));
        //new WebDriverWait( driver, 10).until(ExpectedConditions.elementToBeClickable(window.findElement(By.name(element))));
        try
        {
            WebDriverWait wait = new WebDriverWait(driver, 20);
            wait.until(ExpectedConditions.elementToBeClickable(window.findElement(By.name(element))));
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }
    public boolean verifyClickableWithClassName(WebDriver driver, WebElement window, String element){
        //new WebDriverWait( driver, 10).until(ExpectedConditions.elementToBeClickable(By.name(element)));
        //new WebDriverWait( driver, 10).until(ExpectedConditions.elementToBeClickable(window.findElement(By.name(element))));
        try
        {
            WebDriverWait wait = new WebDriverWait(driver, 20);
            wait.until(ExpectedConditions.elementToBeClickable(window.findElement(By.className(element))));
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public void visibilityOfElementLocated(WebDriver driver, String locator){
        (new WebDriverWait(driver, 30))
                .until(ExpectedConditions
                        .visibilityOfElementLocated(By.xpath(locator)));
    }


    public void elementIsNotDisplayed(WebElement window, LocatorType locatorType, String element, int interval)  {
        int i=0;
        while (isDisplayed(window, locatorType, element))
        {
            try {
                Thread.sleep(interval*1000);
                i++;
                if(i==5) {
                    System.out.println("Element is not invisible");
                    throw new RuntimeException();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}

