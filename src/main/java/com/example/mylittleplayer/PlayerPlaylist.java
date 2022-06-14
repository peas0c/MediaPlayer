package com.example.mylittleplayer;

import com.iheartradio.m3u8.*;
import com.iheartradio.m3u8.data.MediaPlaylist;
import com.iheartradio.m3u8.data.Playlist;
import com.iheartradio.m3u8.data.TrackData;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PlayerPlaylist {
    private String name;
    private ArrayList<Song> songs;
    private Path path;

    private Playlist playlistM3U;

    public PlayerPlaylist(){
        this.name = "";
        this.songs=new ArrayList<Song>();
    }

    public PlayerPlaylist(String name, ArrayList<Song> songs){
        this.name = name;
        this.songs = songs;
        List<TrackData> trackData = new ArrayList<>();
        for(Song s: this.songs){
            trackData.add(s.getTrackData());
        }
        MediaPlaylist mediaPlaylist = new MediaPlaylist.Builder()
                .withTracks(trackData)
                .withMediaSequenceNumber(1)
                .build();
        this.playlistM3U = new Playlist.Builder()
                .withMediaPlaylist(mediaPlaylist)
                .build();
        this.path = Paths.get("C:","Playlists", name+".txt");
    }


    public PlayerPlaylist(File m3u) throws InvalidDataException, UnsupportedTagException, IOException, PlaylistException, ParseException {
        FileInputStream inputStream = new FileInputStream(m3u);
        PlaylistParser parser = new PlaylistParser(inputStream, Format.EXT_M3U, Encoding.UTF_8);
        String fileName = new String(m3u.getName());
        if (fileName.endsWith(".txt")) {
            this.name = fileName.substring(0, fileName.length() - 4);
        }else this.name = fileName;
        this.playlistM3U = parser.parse();
        MediaPlaylist mediaPlaylist = this.playlistM3U.getMediaPlaylist();
        List <TrackData> trackData = mediaPlaylist.getTracks();
        this.songs = getSongsFromTrackData(trackData);
        this.path = Paths.get("C:","Playlists", name+".txt");
    }


    public void createM3Ufile(Path destination_directory_path) throws IOException, PlaylistException, ParseException {
        FileOutputStream out = new FileOutputStream(
                destination_directory_path.toString()
                        + "//"
                        + this.getName()
                        + ".txt");
        PlaylistWriter writer = new PlaylistWriter.Builder()
                .withFormat(Format.EXT_M3U)
                .withEncoding(Encoding.UTF_8)
                .withOutputStream(out)
                .build();
        writer.write(this.getPlaylistM3U());
        out.close();
    }
    public static ArrayList<Song> getSongsFromTrackData(List<TrackData> trackData) throws InvalidDataException, UnsupportedTagException, IOException {
        ArrayList<Song> songs = new ArrayList<>();
        for (TrackData t: trackData
        ) {
            File f = new File(t.getUri().substring(5));
            if (f.canRead()) {
                if (f.getPath().contains(".mp3")) {
                    songs.add(new Song(f));
                }
            }
        }
        return songs;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public Playlist getPlaylistM3U() {
        return playlistM3U;
    }

    public void setPlaylistM3U(Playlist playlistM3U) {
        this.playlistM3U = playlistM3U;
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
