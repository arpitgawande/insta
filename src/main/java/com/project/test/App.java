package com.project.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;

/**
 * Application entry point
 */
public class App
{
    public static void main( String[] args ) throws Exception {
        //Load the properties
        String filePath = "./app.properties";
        FileInputStream file = null;
        try {
            file = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            System.out.println("Please create " + filePath + " file at directory where jar is kept");
            e.printStackTrace();
            System.exit(0);
        }
        Properties properties = new Properties();
        properties.load(file);
        file.close();
        String username = properties.getProperty("username");
        System.out.println("username: " + username);
        String password = properties.getProperty("password");

        if(username == null || password == null || username.isEmpty() || password.isEmpty()){
            System.out.println("No use if you are still not registered. Provide user name password as below");
            System.out.println("Please create " + filePath + " file at directory where jar is kept");
            System.out.println("If created then please provide username and password in the file in below format");
            System.out.println("username=your instacart username");
            System.out.println("password=your instacart password");
            System.out.println("Exiting program");
            System.exit(0);
        }
        System.out.println("login_delay_in_sec=" + properties.getProperty("login_delay_in_sec"));
        System.out.println("retry_every_in_sec=" + properties.getProperty("retry_every_in_sec"));
        int loginDelayInSec, retryEveryInSec;
        try{
            loginDelayInSec = Integer.valueOf(properties.getProperty("login_delay_in_sec"));
            retryEveryInSec = Integer.valueOf(properties.getProperty("retry_every_in_sec"));
        } catch (NumberFormatException e){
            System.out.println("***ERROR: Below properties must have positive numerical value in " + filePath);
            System.out.println("login_delay_in_sec");
            System.out.println("retry_every_in_sec");
            throw new Exception(e);
        }
        if(loginDelayInSec < 1) loginDelayInSec = 1;
        if(retryEveryInSec < 1) retryEveryInSec = 1;
        System.out.println("notify_if_not_available=" + properties.getProperty("notify_if_not_available"));
        boolean notifyIfNotAvailable = Boolean.valueOf(properties.getProperty("notify_if_not_available"));
        System.out.println("notify_if_not_available is set to: " + notifyIfNotAvailable);
        InstaCheckAvailibility instaCheckAvailibility = new InstaCheckAvailibility();
        try{
            instaCheckAvailibility.checkInstacartAvailibility(username, password, loginDelayInSec, retryEveryInSec, notifyIfNotAvailable);
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Dont lose hope, check above error and ask yourself what did I do wrong!!! " +
                    "Use your brain and resolve the error. " +
                    "Calm your mind, take a deep breath, and then run again. " +
                    "Don't give up until you success. ** Stay Foolish stay Hungry ** :D ");
        }
    }
}
