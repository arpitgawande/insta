package com.project.test;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.*;

public class InstaCheckAvailibility {

    private final ScheduledExecutorService scheduledExecutorService;

    public InstaCheckAvailibility() {
        scheduledExecutorService = Executors.newScheduledThreadPool(2);
    }

    public void checkInstacartAvailibility(String username, String password, int loginDelayInSec, int retryEveryInSec,
                                           boolean notifyIfNotAvailable) throws Exception {
        WebDriver driver= getWebdriver();
        //Maximize the window and open the website
        //driver.manage().window().maximize();
        TimeUnit.SECONDS.sleep(loginDelayInSec);
        driver = login(username, password, driver);
        while (true){
            try {
                //wait and try
                TimeUnit.SECONDS.sleep(retryEveryInSec);
                checkForDeliveryTime(driver, notifyIfNotAvailable, retryEveryInSec);
            } catch (Exception e) {
                e.printStackTrace();
                scheduledExecutorService.shutdown();
            }
        }
        //scheduledExecutorService.shutdown();
    }

    /**
     * Login to website
     * @param driver
     * @return web driver handle
     */
    private WebDriver login(String instaUsername, String instaPassword, WebDriver driver) throws Exception {
        driver.get("https://www.instacart.com/");
        try{
            //wait for website to load then login
            //driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            WebElement link  = driver.findElement(By.linkText("Log in"));
            link.click();
            //get username and password field
            WebElement username=driver.findElement(By.id("nextgen-authenticate.all.log_in_email"));
            WebElement password=driver.findElement(By.id("nextgen-authenticate.all.log_in_password"));
            WebElement login=driver.findElement(By.xpath("//button[@type='submit']"));
            //Set username and password
            username.sendKeys(instaUsername);
            password.sendKeys(instaPassword);
            //login
            login.click();
        } catch (Exception e){
            e.printStackTrace();
            String msg = "*** Failed to Login, try to increase login wait time ***";
            System.out.println("TIME OF ERROR: " + LocalDateTime.now());
            System.out.println(msg);
            throw new Exception(msg);
        }
        return driver;
    }

    /**
     * Check the page where the delivery time is mentioned
     * @param driver
     */
    private void checkForDeliveryTime(WebDriver driver, boolean notifyIfNotAvailable, int retryEveryInSec) throws Exception {
        try{
            WebElement deliveryTime=driver.findElement(By.xpath("//span[@title='See delivery times']"));
            deliveryTime.click();
            TimeUnit.SECONDS.sleep(retryEveryInSec > 5 ? retryEveryInSec - 5 : 10);
            List<WebElement> webElements = driver.findElements(By.xpath("//h1"));
            WebElement closeModal=driver.findElement(By.xpath("//button[@aria-label='Close modal']"));
            if(webElements.size() > 0 && webElements.get(0).getText().equals("All delivery windows are full")){
                if(notifyIfNotAvailable) notifyError();
            }
            else { //if not  available then close the modal and try again
                System.out.println("Found availability at: " + LocalDateTime.now());
                notifySuccess();
            }
            closeModal.click();
        } catch (Exception e){
            String msg = "*** Failed to check the wait time, try to increase retry time ***";
            e.printStackTrace();
            System.out.println(msg);
            throw new Exception(msg);
        }

    }

    private void notifyError(){
        String errorSound = "/NotAvailable-sound.wav";
        int playTime = 1;
        notifyMe(errorSound, playTime);
    }

    private void notifySuccess(){
        int playTime = 2;
        final String successSound = "/Available-sound.wav";
        notifyMe(successSound, playTime);
    }

    /**
     * Here you can define how you want to be notified. Current implementation only support sound notification
     */
    private void notifyMe(String fileName, int playTime){
        //System.out.println(filePath);
        //Run job
        Runnable soundRunner = () -> {
            playSound(fileName);
        };
        ScheduledFuture<?> soundHandle = scheduledExecutorService
                .scheduleAtFixedRate(soundRunner, 1, playTime, TimeUnit.SECONDS);
        //Cancel job
        Runnable soundCanceller = () -> {
            soundHandle.cancel(false);
        };
        scheduledExecutorService.schedule(soundCanceller, playTime, TimeUnit.SECONDS);
//        try{
//            soundRunner.run();
//        }catch (Exception e){
//            System.out.println("Exception while running job:" + e.getCause());
//        }
    }

    /**
     * Get driver for the appropriate web browser
     * @return
     */
    private WebDriver getWebdriver(){
        //System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
        WebDriverManager.chromedriver().version("80.0.3987.106").setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("start-maximized");
        options.addArguments("enable-automation");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-browser-side-navigation");
        options.addArguments("--disable-gpu");
        return new ChromeDriver(options);
    }

    /**
     * Play any sound file
     * @param fileName
     */
    private static void playSound(String fileName){
        try {
            Clip clip = AudioSystem.getClip();
//            File file = new File(
//                    InstaCheckAvailibility.class.getClassLoader().getResource(fileName).getFile()
//            );
//            System.out.println(file.getAbsolutePath());
            //AudioInputStream inputStream = AudioSystem.getAudioInputStream(file);
            //Get input stream from the file
            InputStream inputStream  = InstaCheckAvailibility.class.getResourceAsStream(fileName);
            //convert to buffered
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedInputStream);
            //audioInputStream.reset();
            clip.open(audioInputStream);
            clip.start();
            //clip.stop();
        }  catch (IOException | LineUnavailableException | UnsupportedAudioFileException e) {
            throw new IllegalArgumentException("could not play sound", e);
        }
    }
}
