package com.jaliansystems.javadriver.examples.swing.ut;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import net.sourceforge.marathon.javadriver.JavaDriver;
import net.sourceforge.marathon.javadriver.JavaProfile;
import net.sourceforge.marathon.javadriver.JavaProfile.LaunchMode;
import net.sourceforge.marathon.javadriver.JavaProfile.LaunchType;


public class LoginDialogTest {

    private LoginDialog login;
    private WebDriver driver;
    private static  WebElement user, pass, loginBtn, cancelBtn;
    private static  WebDriverWait wait;
    private static List<WebElement> textComponents;

    @Before
    public void setUp() throws Exception {
        login = new LoginDialog() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSuccess() {
            }

            @Override
            protected void onCancel() {
            }
        };
        SwingUtilities.invokeLater(() -> login.setVisible(true));
        JavaProfile profile = new JavaProfile(LaunchMode.EMBEDDED);
        profile.setLaunchType(LaunchType.SWING_APPLICATION);
        driver = new JavaDriver(profile);
        user = driver.findElement(By.cssSelector("text-field"));
        pass = driver.findElement(By.cssSelector("password-field"));
        loginBtn = driver.findElement(By.cssSelector("button[text='Login']"));
        wait = new WebDriverWait(driver, 10);
        cancelBtn = driver.findElement(By.cssSelector("button[text='Cancel']"));
        textComponents = driver.findElements(By.className(JTextComponent.class.getName()));
    }

    @After
    public void tearDown() throws Exception {
        if (login != null)
            SwingUtilities.invokeAndWait(() -> login.dispose());
        if (driver != null)
            driver.quit();
    }

    @Test
    public void loginSuccess() {
        user.sendKeys("bob");
        pass.sendKeys("secret");
        wait.until(ExpectedConditions.elementToBeClickable(loginBtn));
        loginBtn.click();
        assertTrue(login.isSucceeded());
        assertTrue(login.getSize() != null);
    }

    @Test
    public void loginCancel() {
        user.sendKeys("bob");
        pass.sendKeys("secret");
        cancelBtn.click();
        assertFalse(login.isSucceeded());
    }

    @Test
    public void loginInvalid() throws InterruptedException {
        user.sendKeys("bob");
        pass.sendKeys("wrong");
        wait.until(ExpectedConditions.elementToBeClickable(loginBtn));
        loginBtn.click();
        driver.switchTo().window("Invalid Login");
        driver.findElement(By.cssSelector("button[text='OK']")).click();
        driver.switchTo().window("Login");
        assertEquals("", user.getText());
        assertEquals("", pass.getText());
    }

    @Test
    public void checkTooltipText() {
        // Check that all the text components (like text fields, password
        // fields, text areas) are associated
        // with a tooltip
        for (WebElement tc : textComponents) {
            assertNotEquals(null, tc.getAttribute("toolTipText"));
        }
    }
}
