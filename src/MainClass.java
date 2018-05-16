import static org.testng.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import jxl.Sheet;
import jxl.Workbook;

public class MainClass {

	public WebDriver driver;
	public WebDriverWait wait;
	
	private By user = By.id("usr");
	private By pwd = By.name("pwd");
	private By submit = By.cssSelector("input[type='submit']");
	private By success = By.id("case_login");

	public String[][] returnExcelData() throws Exception {

		String[][] userDatas = null;
		String filePath = System.getProperty("user.dir");
		File file = new File(filePath + "/" + "data.xls");
		FileInputStream fileInputStream = new FileInputStream(file);
		Workbook workbook = Workbook.getWorkbook(fileInputStream);
		Sheet sh = workbook.getSheet("users");
		int colCount = sh.getColumns();
		int rowCount = sh.getRows();
		userDatas = new String[rowCount - 1][colCount];

		for (int i = 1; i < rowCount; i++) {

			for (int j = 0; j < colCount; j++) {
				userDatas[i - 1][j] = sh.getCell(j, i).getContents();
			}
		}

		return userDatas;
	}
	
	@BeforeTest
	public void beforeTest() {
		driver = new ChromeDriver();
		wait = new WebDriverWait(driver, 10);
	}
	
	@AfterTest
	public void afterTest() {
		driver.quit();
	}
	
	@DataProvider(name = "login")
	public Object[][] login() throws Exception {
		Object[][] object = returnExcelData();
		return object;
	}

	@Test(dataProvider="login")
	public void loginTest(String userName, String password) {
		driver.navigate().to("http://testing-ground.scraping.pro/login");

		wait.until(ExpectedConditions.visibilityOfElementLocated(user));
		driver.findElement(user).sendKeys(userName);
		driver.findElement(pwd).sendKeys(password);	
		driver.findElement(submit).click();
		String successMsg = driver.findElement(success).getText();
		assertTrue(successMsg.contains("WELCOME"), "Success");
	}
}
