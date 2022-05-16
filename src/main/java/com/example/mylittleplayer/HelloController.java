package com.example.mylittleplayer;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;

import static org.apache.commons.io.FileUtils.*;


public class HelloController implements Initializable {

    @FXML
    private GridPane playlistZone;
    @FXML
    private ListView<String> playlistList;
    @FXML
    private Button importPlaylistButton;
    @FXML
    private Button importSongButton;
    @FXML
    private GridPane trackListZone;
    @FXML
    private ListView<String> songList;
    @FXML
    private GridPane controllerZone;
    @FXML
    private Button nextButton;
    @FXML
    private Button pauseButton;
    @FXML
    private Button previousButton;
    @FXML
    private Label songName;
    @FXML
    private Label songAuthor;
    @FXML
    private ProgressBar songProgressBar;

    private MediaPlayer player;
    private Media media;
    private int songNumber;
    private Timer timer;
    private TimerTask task;
    private boolean active_track;
    private Playlist current_playlist = new Playlist();
    private ArrayList<Playlist> playlists = new ArrayList<>();
    private ArrayList<String> playlist_names = new ArrayList<>();
    private ArrayList<Song> current_songs = new ArrayList<>();
    private ArrayList<String> current_song_names = new ArrayList<>();
    private File main_directory = new File("src/main/resources/com/example/mylittleplayer/Playlists");


    @FXML
    void importPlaylist(ActionEvent event) throws IOException {

        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setInitialDirectory(new File("C:\\Users\\user"));
        File playlist_import_dir = directoryChooser.showDialog(null);
        while (Objects.requireNonNull(playlist_import_dir.listFiles()).length == 0) {
            playlist_import_dir = directoryChooser.showDialog(null);
        }
        copyDirectoryToDirectory(playlist_import_dir, main_directory);
        Playlist playlist = new Playlist(new File(
                main_directory.getPath() + "\\" + playlist_import_dir.getName()));

        if (active_track) {
            pauseButton.setText("▶");
            stopCurrentSong();
        }
        current_playlist = playlist;
        refreshPlaylists();
    }

    @FXML
    void importSong() {
        FileChooser fileChooser = new FileChooser();
        File song_file = fileChooser.showOpenDialog(null);
        while (!song_file.getName().endsWith(".mp3")) {
            song_file = fileChooser.showOpenDialog(null);
        }
        File new_song_file = new File(main_directory + "\\" + current_playlist.getName());
        try {
            copyFileToDirectory(song_file, new File(main_directory + "\\" + current_playlist.getName()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        refreshSongs();
    }


    @FXML
    void pauseAndPlayButtonClick(ActionEvent event) {
        if (!active_track) {
            play();
        } else {
            pause();
        }
    }

    void pause() {
        player.pause();
        pauseButton.setText("▶");
        active_track = false;
    }

    void play() {
        startTimer();
        player.play();
        pauseButton.setText("||");
        active_track = true;
    }


    @FXML
    void previousMedia(ActionEvent event) {
        if (songNumber > 0) {
            songNumber--;
        } else songNumber = current_playlist.getSongs().size() - 1;
        player.stop();
        songToPlay(current_playlist.getSongs().get(songNumber));
    }

    @FXML
    void nextMedia(ActionEvent event) {
        if (songNumber < current_playlist.getSongs().size() - 1) {
            songNumber++;
        } else songNumber = 0;
        player.stop();
        songToPlay(current_playlist.getSongs().get(songNumber));

    }

    private void startTimer() {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                double current_time = player.getCurrentTime().toSeconds();
                double end_time = media.getDuration().toSeconds();
                songProgressBar.setProgress(current_time / end_time);
                if (current_time / end_time == 1) timer.cancel();
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    private void songToPlay(Song s) {
        media = new Media(s.getFile().toURI().toString());
        player = new MediaPlayer(media);
        setNameandAuthor(s);
        if (active_track) {
            player.stop();
        }
        play();
    }

    public void changeCurrentPlaylist(String new_name) {
        current_playlist = new Playlist(new File(main_directory + "\\" + new_name));
        active_track = false;
    }

    public void setNameandAuthor(Song s) {
        songName.setText(s.getName());
        songAuthor.setText(s.getAuthor());
    }

    public void stopCurrentSong() {
        if (active_track) {
            player.stop();
        }
    }

    public void refreshPlaylists() {
        int playlist_amount = main_directory.listFiles().length;
        if (playlist_amount > 0) {
            playlists.clear();
            playlist_names.clear();
            for (File f : main_directory.listFiles()) {
                playlists.add(new Playlist(f));
                playlist_names.add(f.getName());
            }
            importSongButton.setDisable(false);
            //playlistList.getItems().clear();
            playlistList.getItems().addAll(playlist_names);
        } else importSongButton.setDisable(true);
    }

    public void refreshSongs() {
        int songs_amount = current_playlist.getSongs().size();
        if (songs_amount > 0) {
            current_songs.clear();
            current_song_names.clear();
            for (Song s : current_playlist.getSongs()) {
                current_songs.add(s);
                current_song_names.add(s.getGeneral_name());
            }
            songList.getItems().clear();
            songList.getItems().addAll(current_song_names);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!main_directory.exists()) {
            main_directory.mkdir();
        }
        refreshPlaylists();
        playlistList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                changeCurrentPlaylist(newValue);
                refreshSongs();
            }
        });
        songList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    stopCurrentSong();
                    songNumber = songList.getSelectionModel().getSelectedIndex();
                    songToPlay(current_playlist.getSongs().get(songNumber));
                }
            }
        });


        /*

       songList.getSelectionModel().selectedItemProperty().addListener(new InvalidationListener() {

            @Override
            public void invalidated(Observable observable) {
                songNumber = songList.getSelectionModel().getSelectedIndex();
                songToPlay(current_playlist.getSongs().get(songNumber));
            }
        });

        */
    }
}
