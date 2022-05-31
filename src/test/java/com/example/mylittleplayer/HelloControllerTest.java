package com.example.mylittleplayer;

import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;

import java.io.File;

class HelloControllerTest {

    Song song = new Song(new File("TheSong.mp3"));

    @BeforeClass
    public static void beforeClass() {
        System.out.println("before HelloControllerTest.class");
    }

    @Test
    void importPlaylist() {
    }

    @Test
    void importSong() {
    }

    @Test
    void pauseAndPlayButtonClick() {
    }

    @Test
    void pause() {
    }

    @Test
    void play() {
    }

    @Test
    void previousMedia() {
    }

    @Test
    void nextMedia() {
    }

    @Test
    void initialize() {
    }
}