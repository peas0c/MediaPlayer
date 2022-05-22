package com.example.mylittleplayer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class PlaylistTest {

    Playlist playlist = new Playlist();

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getName() {
        playlist.setName("Name");
        assert("Name".equals( playlist.getName()));
    }

    @Test
    void setName() {
        playlist.setName("Name");
        assert(!(playlist.getName().equals("")));
    }

    @Test
    void getSongs() {
        assert(playlist.getSongs().equals(new ArrayList<Song>()));
    }

    @Test
    void setSongs() {
    }
}