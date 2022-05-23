package com.example.mylittleplayer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

class SongTest {
    Song song = new Song(new File ("TheSong.mp3"));

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getName() {
        assert("TheSong".equals(song.getName()));
        assert("TheSong".equals(song.getGeneral_name()));
    }

    @Test
    void setName() {
        song.setName("NewName");
        assert("NewName".equals(song.getName()));
        assert("NewName".equals(song.getGeneral_name()));
    }

    @Test
    void getGeneral_name() {
        song.setName("Name");
        song.setAuthor("Author");
        assert(song.getName().equals("Name"));
        assert(song.getAuthor().equals("Author"));
        assert("Name - Author".equals(song.getGeneral_name()));
    }

    @Test
    void setGeneral_name() {
        song.setGeneral_name("Name - Author");
        assert(song.getName().equals("Name"));
        assert(song.getAuthor().equals("Author"));
        assert(song.getGeneral_name().equals("Name - Author"));
    }

    @Test
    void getAuthor() {
        assert("".equals(song.getAuthor()));
    }

    @Test
    void setAuthor() {
        song.setAuthor("Author");
        assert("Author".equals(song.getAuthor()));
    }

    @Test
    void getFile() {
    }

    @Test
    void setFile() {
    }

    @Test
    void getPath() {
    }

    @Test
    void setPath() {
    }
}