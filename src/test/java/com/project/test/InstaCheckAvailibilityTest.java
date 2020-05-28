package com.project.test;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedInputStream;
import java.io.IOException;

public class InstaCheckAvailibilityTest {

    private InstaCheckAvailibility instaCheckAvailibility;

    @Before
    public void setup() throws IOException, UnsupportedAudioFileException {
        instaCheckAvailibility = new InstaCheckAvailibility();
    }

    @Test
    public void playSoundTest() throws Exception {
        String filePath = "/Alarm06.wav";
        Whitebox.invokeMethod(instaCheckAvailibility, "playSound", filePath);
    }
}
