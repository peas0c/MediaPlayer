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
import com.mpatric.mp3agic.*;
import com.mpatric.mp3agic.UnsupportedTagException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;


public class Song {
    private String general_name;
    private String name;
    private String author;
    private File file;
    private String path;
    private File picture;

    /**
     * Gets an image from a file
     * @param f
     * @return file with image or new RandomAccessFile
     * @throws IOException
     */
    public RandomAccessFile getImage(Mp3File f) throws IOException {
        if (f.hasId3v2Tag()){
            ID3v2 tag = f.getId3v2Tag();
            byte[] imageData = tag.getAlbumImage();
            if(imageData != null){
                RandomAccessFile file = new RandomAccessFile("album-artwork" , "rw");
                file.write(imageData);
                return file;
            }else{
                return new RandomAccessFile("album-artwork", "rw");
            }
        }else{
            return new RandomAccessFile("album-artwork", "rw");
        }
    }

    /**
     * Constructor
     * @param file - file
     * @throws InvalidDataException
     * @throws UnsupportedTagException
     * @throws IOException
     */
    public Song(File file) throws InvalidDataException, UnsupportedTagException, IOException {
        Mp3File f = new Mp3File(file.getPath());
        this.file = file;
        this.path = file.getPath();
        if (f.hasId3v1Tag()){
            ID3v1 tag = f.getId3v1Tag();
            this.name = tag.getTitle();
            this.author = tag.getArtist();
        }else{
            this.name = "Unknown";
            this.author = "Unknown";
        }
        this.general_name = name + "-" + author;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        if(getAuthor().equals("")){
            general_name = name;
        }else{
            general_name = name + " - " + getAuthor();
        }
    }

    public String getGeneral_name() {
        return general_name;
    }

    /**
     * @param general_name - File general name
     */
    public void setGeneral_name(String general_name) {
        if(general_name.contains(" - ")) {
            String[] GenName = general_name.split("[-.]");
            this.name = GenName[0].trim();
            this.author = GenName[1].trim();
            this.general_name = general_name;
        }else{
            this.name = general_name;
            this.author = "";
            this.general_name = general_name;
        }

    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
        this.general_name = getName() + " - " + author;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
