package test.beforeTest;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import sun.rmi.runtime.Log;
import test.Log.LogMessage;
import test.keywordScripts.*;
import test.objectLocator.WebObjectSearch;
import test.utility.PropertyConfig;

import java.util.Map;

public class PropertyCreate {
    private WebDriver webDriver;

    public PropertyCreate(WebDriver driver) {
        this.webDriver = driver ;
    }

    public PropertyCreate(){

    }



    public LogMessage createProperty(Map data) {
        try {
            String[] textFields = new String[] {"propertyName","propertyCode","address1" , "postal" , "city" , "sqFtRentable"} ;
            String[] dropDownFields = new String[] {"country","state","codeType" , "status" , "currency", "buildingList", "region", "assetType"} ;
            String autoManageChkBox = "autoManage" ;
            String  objectlocatorPrefix = "Common.Property." ;
            UIMenu menu = new UIMenu(webDriver);
            UtilKeywordScript utilKeywordScript = new UtilKeywordScript(webDriver);

            menu.SelectMenu("Common.Homepage.pgAMTHome" , "Portfolio Insight,Add,Property") ;
            UtilKeywordScript.switchLastTab(webDriver);
            UtilKeywordScript.delay(10);

            for(String elementName : textFields) {
                WebElement element = WebObjectSearch.getWebElement(webDriver,objectlocatorPrefix  + elementName) ;
                element.sendKeys( (String) data.get(elementName));
                UtilKeywordScript.delay(3);
            }

            for(String elementName : dropDownFields) {
                WebElement element = WebObjectSearch.getWebElement(webDriver,objectlocatorPrefix  + elementName) ;
                Select select = new Select(element);
                select.selectByVisibleText( (String)data.get(elementName));
                if(elementName.equals("codeType")) {
                    webDriver.switchTo().alert().accept();
                    UtilKeywordScript.delay(3);
                }
                UtilKeywordScript.delay(3);
            }
            WebElement checkBoxItem = WebObjectSearch.getWebElement(webDriver,objectlocatorPrefix + autoManageChkBox) ;
            if(  data.get(autoManageChkBox).toString().toLowerCase().equals("true"))
                checkBoxItem.click();
            WebElement element = WebObjectSearch.getWebElement(webDriver,objectlocatorPrefix + "save") ;
            element.click();
            UtilKeywordScript.delay(15);
            utilKeywordScript.redirectHomePage();
            UtilKeywordScript.delay(3);
            return new LogMessage(true, "create property successfull") ;
        } catch ( Exception ex) {
            ex.printStackTrace();
            return new LogMessage(false, "exception occured: " + ex.getMessage()) ;
        }
    }

    public LogMessage isPropertyExist(Map data){
        try{
            String  objectLocatorPrefix = "Common.Property.";
            UITable uiTable  = new UITable(webDriver);
            UtilKeywordScript utilKeywordScript = new UtilKeywordScript(webDriver);

            utilKeywordScript.globalSearch((String)data.get("propertyCode"),"Property");

            Map<String, WebElement> row = uiTable.getSingleRowfromTable(objectLocatorPrefix +"tbProperty", "Property Code",(String)data.get("propertyCode"),null);
            if(null == row || row.isEmpty()){
                return new LogMessage(false, "Property not found");
            }
            else{
                return new LogMessage(false, "Property  found");
            }
        }catch (Exception e){
            return new LogMessage(false, "Exception occur "+ e.getMessage());

        }
    }

    public LogMessage navigateToProperty(Map data){
        try{
            String  objectLocatorPrefix = "Common.Property.";
            String columnName = "Property Name";
            String columnValue = (String)data.get("propertyName");
            UITable uiTable  = new UITable(webDriver);
            UtilKeywordScript utilKeywordScript = new UtilKeywordScript(webDriver);

            LogMessage searchLog = utilKeywordScript.globalSearch((String)data.get("propertyCode"),"Property");

            if (!searchLog.isPassed())
                return new LogMessage(false,"Exception occur in global search");

            Map<String, WebElement> propertyRow = uiTable.getSingleRowfromTable(objectLocatorPrefix +"tbProperty", "Property Code",(String)data.get("propertyCode"),null);
            if(null == propertyRow || propertyRow.isEmpty()){
                return new LogMessage(false, "Property not found");
            }
            for (String key : propertyRow.keySet()) {
                if(key.split(",").length<2)
                    continue;
                String clName = key.split(",")[1];
                if(columnName.equals(clName)){
                    WebElement element = propertyRow.get(key) ;
                    String text = element.getText();
                    if(columnValue.equals(text)) {
                        WebElement elm = element.findElement(By.linkText(columnValue));
                        elm.click();
                    }
                    else {
                        return new LogMessage(false, "Property name is not matching");

                    }
                }
            }


            UtilKeywordScript.delay(PropertyConfig.WAIT_TIME_SECONDS*PropertyConfig.NUMBER_OF_ITERATIONS);
            webDriver.close();
            UtilKeywordScript.switchLastTab(webDriver);
            UtilKeywordScript.delay(PropertyConfig.WAIT_TIME_SECONDS*PropertyConfig.NUMBER_OF_ITERATIONS);
            return new LogMessage(true, "Navigate to property complete");
        }catch (Exception e){
            return new LogMessage(false, "Exception occur " + e.getMessage());
        }
    }

}
