/* Copyright (C) 2022 <Название команды>
   This file is part of the MediaPlayer.
   (Yet Another Merge Sort Routines)

   MediaPlayer is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    MediaPlayer is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with MediaPlayer.  If not, see <http://www.gnu.org/licenses/>.

  (Этот файл — часть MediaPlayer.

   MediaPlayer - свободная программа: вы можете перераспространять ее и/или
   изменять ее на условиях Стандартной общественной лицензии GNU в том виде,
   в каком она была опубликована Фондом свободного программного обеспечения;
   либо версии 3 лицензии, либо (по вашему выбору) любой более поздней
   версии.

   MediaPlayer распространяется в надежде, что она будет полезной,
   но БЕЗО ВСЯКИХ ГАРАНТИЙ; даже без неявной гарантии ТОВАРНОГО ВИДА
   или ПРИГОДНОСТИ ДЛЯ ОПРЕДЕЛЕННЫХ ЦЕЛЕЙ. Подробнее см. в Стандартной
   общественной лицензии GNU.

   Вы должны были получить копию Стандартной общественной лицензии GNU
   вместе с этой программой. Если это не так, см.
   <http://www.gnu.org/licenses/>.)

 */
package com.example.mylittleplayer;

import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
public class Playlist {
    String name;
    ArrayList<Song> songs;

    public Playlist(){
        this.name = "";
        this.songs=new ArrayList<Song>();
    }

    public Playlist(String name, ArrayList<Song> songs){
        this.name = name;
        this.songs = new ArrayList<Song>();
    }

    /**
     * Adds song to playlist
     * @param dir - File direction
     * @throws InvalidDataException
     * @throws UnsupportedTagException
     * @throws IOException
     */
    public Playlist(File dir) throws InvalidDataException, UnsupportedTagException, IOException {

        this.name = dir.getName();
        File[] song_files = dir.listFiles();
        this.songs = new ArrayList<>();
        assert song_files != null;
        for (File f: song_files){
            if(f.getPath().contains(".mp3")) {
                Song s = new Song(f);
                this.songs.add(s);
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }
}
