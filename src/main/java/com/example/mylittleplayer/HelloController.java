package com.example.mylittleplayer;

import com.iheartradio.m3u8.*;
import com.iheartradio.m3u8.data.MediaPlaylist;
import com.iheartradio.m3u8.data.Playlist;
import com.iheartradio.m3u8.data.TrackData;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.UnsupportedTagException;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import java.io.*;
import java.net.URL;
import java.util.*;


public class HelloController implements Initializable {

    @FXML
    private GridPane playlistZone;
    @FXML
    private ListView<String> playlistList;
    @FXML
    private ContextMenu playlistContextMenu = new ContextMenu();
    @FXML
    private GridPane importArea;
    @FXML
    private Button importPlaylistButton;
    @FXML
    private Button importSongButton;
    @FXML
    private Button importM3Ubutton;
    @FXML
    private Button importDirButton;
    @FXML
    private GridPane trackListZone;
    @FXML
    private TextField searchField;
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
    private GridPane soundArea;
    @FXML
    private Slider soundSlider;
    @FXML
    private Button changeSoundButton;
    @FXML
    private Button shuffleButton;
    @FXML
    private Button repeatButton;
    @FXML
    private Label songName;
    @FXML
    private Label songAuthor;
    @FXML
    private Slider songProgressBar;
    @FXML
    private ImageView soundIcon;
    @FXML
    private ImageView nextIcon;
    @FXML
    private ImageView pauseAndPlayIcon;
    @FXML
    private ImageView previousIcon;
    @FXML
    private ImageView shuffleIcon;
    @FXML
    private ImageView repeatIcon;
    private final Image pause_image = new Image(new File("src/main/resources/com/example/mylittleplayer/images/pause.png").toURI().toString());
    private final Image play_image = new Image(new File("src/main/resources/com/example/mylittleplayer/images/play_arrow.png").toURI().toString());
    private final Image sound_on_image = new Image(new File("src/main/resources/com/example/mylittleplayer/images/sound_on.png").toURI().toString());
    private final Image sound_off_image = new Image(new File("src/main/resources/com/example/mylittleplayer/images/sound_off.png").toURI().toString());
    private final Image shuffle_on_image = new Image(new File("src/main/resources/com/example/mylittleplayer/images/active_shuffle.png").toURI().toString());
    private final Image shuffle_off_image = new Image(new File("src/main/resources/com/example/mylittleplayer/images/shuffle.png").toURI().toString());
    private final Image repeat_on_image = new Image(new File("src/main/resources/com/example/mylittleplayer/images/active_repeat.png").toURI().toString());
    private final Image repeat_off_image = new Image(new File("src/main/resources/com/example/mylittleplayer/images/repeat.png").toURI().toString());
    private MediaPlayer player;
    private Media media;
    private int songNumber;
    private Timer timer;
    private TimerTask task;
    private boolean active_track = false;
    private boolean muted = false;
    private boolean shuffle_on = false;
    private boolean repeat_on = false;

    private double last_sound_value = 70;
    private PlayerPlaylist current_playlist = new PlayerPlaylist();
    private ArrayList<PlayerPlaylist> playlists = new ArrayList<>();
    private ArrayList<String> playlist_names = new ArrayList<>();
    private ArrayList<Song> current_songs = new ArrayList<>();
    private ArrayList<String> current_song_names = new ArrayList<>();
    private File main_directory = new File("C://Playlists");


    @FXML
    void importDir(ActionEvent event) throws IOException, InvalidDataException, UnsupportedTagException, PlaylistException, ParseException {

        DirectoryChooser directoryChooser = new DirectoryChooser();
        File playlist_import_dir = directoryChooser.showDialog(null);
        while (Objects.requireNonNull(playlist_import_dir.listFiles()).length == 0) {
            playlist_import_dir = directoryChooser.showDialog(null);
        }
        File[] dir_files = playlist_import_dir.listFiles();

        ArrayList<Song> songs = new ArrayList<>();
        ArrayList<String> songNames = new ArrayList<>();
        for (File f : dir_files
        ) {
            if (f.getName().endsWith(".mp3")) {
                Song s = new Song(f);
                songs.add(s);
            }
        }

        PlayerPlaylist pl = new PlayerPlaylist(playlist_import_dir.getName(), songs);
        pl.createM3Ufile(main_directory.toPath());


        current_playlist = pl;
        refreshPlaylists();

    }
    @FXML
    void importM3U(ActionEvent event) throws PlaylistException, InvalidDataException, UnsupportedTagException, IOException, ParseException {
        FileChooser fileChooser = new FileChooser();
        File m3u = fileChooser.showOpenDialog(null);
        while (!m3u.getName().endsWith(".txt")) {
            m3u = fileChooser.showOpenDialog(null);
        }
        PlayerPlaylist pl = new PlayerPlaylist(m3u);
        pl.createM3Ufile(main_directory.toPath());
        refreshPlaylists();
    }

    @FXML
    void showImportWays(ActionEvent event){
        importArea.getChildren().remove(importSongButton);
        importArea.getChildren().remove(importPlaylistButton);
        importArea.getChildren().add(importM3Ubutton);
        importArea.getChildren().add(importDirButton);
        importArea.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                importArea.getChildren().remove(importM3Ubutton);
                importArea.getChildren().remove(importDirButton);
                importArea.getChildren().add(importPlaylistButton);
                importArea.getChildren().add(importSongButton);
            }
        });
    }

    @FXML
    void importSong() throws InvalidDataException, UnsupportedTagException, IOException, PlaylistException, ParseException {
        FileChooser fileChooser = new FileChooser();
        File song_file = fileChooser.showOpenDialog(null);
        while (!song_file.getName().endsWith(".mp3")) {
            song_file = fileChooser.showOpenDialog(null);
        }
        Song song = new Song(song_file);
        ArrayList<TrackData> new_tracks = new ArrayList<>();
        new_tracks.add(song.getTrackData());
        Playlist playlist = current_playlist.getPlaylistM3U();
        MediaPlaylist mediaPlaylist = playlist.getMediaPlaylist();
        List <TrackData> old_tracks = mediaPlaylist.getTracks();
        ArrayList <TrackData> updated_tracks = new_tracks;
        updated_tracks.addAll(old_tracks);
        PlayerPlaylist pl = new PlayerPlaylist(current_playlist.getName(), getSongsFromTrackData(updated_tracks));
        pl.createM3Ufile(main_directory.toPath());
        current_playlist = pl;
        refreshPlaylists();
        refreshSongs();
    }
    @FXML
    public void deletePlaylist(PlayerPlaylist pl) throws PlaylistException, InvalidDataException, UnsupportedTagException, IOException, ParseException {
        File delete = new File(String.valueOf(pl.getPath()));
        delete.delete();
        refreshPlaylists();

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
        pauseAndPlayIcon.setImage(play_image);
        active_track = false;
    }

    void play() {
        player.play();
        startTimer();
        pauseAndPlayIcon.setImage(pause_image);
        active_track = true;
    }


    @FXML
    void activateShuffle() {
        if (shuffle_on) {
            shuffle_on = false;
            shuffleIcon.setImage(shuffle_off_image);
        } else {
            shuffle_on = true;
            shuffleIcon.setImage(shuffle_on_image);
        }
    }

    @FXML
    void activateRepeat() {
        if (repeat_on) {
            repeat_on = false;
            repeatIcon.setImage(repeat_off_image);
        } else {
            repeat_on = true;
            repeatIcon.setImage(repeat_on_image);
        }
    }


    @FXML
    void previousMedia(ActionEvent event) {
        if (shuffle_on) {
            player.stop();
            songToPlay(getRandomSong(current_playlist));
        } else {

            if (songNumber > 0) {
                songNumber--;
            } else songNumber = current_playlist.getSongs().size() - 1;
            songToPlay(current_playlist.getSongs().get(songNumber));
        }
    }

    @FXML
    void nextMedia(ActionEvent event) {
        if (shuffle_on) {
            player.stop();
            songToPlay(getRandomSong(current_playlist));
        } else {
            if (songNumber < current_playlist.getSongs().size() - 1) {
                songNumber++;
            } else songNumber = 0;
            songToPlay(current_playlist.getSongs().get(songNumber));
        }
    }

    private void startTimer() {
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                double current_time = player.getCurrentTime().toSeconds();
                double end_time = media.getDuration().toSeconds();
                double progress = current_time / end_time;
                songProgressBar.setValue(progress);
                StackPane trackPane = (StackPane) songProgressBar.lookup(".track");
                //String style = String.format("-fx-background-color: linear-gradient(to right, #c5b9d2 %d%%, #e0dbdb %d%%)", , (end_time-current_time/end_time));
                //trackPane.setStyle(style);
                if (progress == 1) {
                    if (!repeat_on) {
                        Platform.runLater(() -> {
                            nextMedia(new ActionEvent());
                        });
                    } else {
                        songToPlay(current_playlist.getSongs().get(songNumber));
                    }
                }
            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 1000);
    }

    private void songToPlay(Song s) {
        if (active_track) {
            player.stop();
        }
        songProgressBar.setValue(0);
        media = new Media(s.getFile().toURI().toString());
        player = new MediaPlayer(media);
        setNameandAuthor(s);
        play();
    }

    private void changeCurrentPlaylist(String new_name)
            throws InvalidDataException, UnsupportedTagException, IOException, PlaylistException, ParseException {
        current_playlist = new PlayerPlaylist((new File(main_directory.getPath() + "//" + new_name + ".txt")));
        refreshSongs();

    }

    private void setNameandAuthor(Song s) {
        songName.setText(s.getName());
        songAuthor.setText(s.getAuthor());
    }

    private void stopCurrentSong() {
        if (active_track) {
            player.stop();
            active_track = false;
        }
    }

    private void refreshPlaylists() throws InvalidDataException, UnsupportedTagException, IOException, PlaylistException, ParseException {
        playlists.clear();
        playlist_names.clear();
        playlistList.getItems().clear();
        int playlist_amount = Objects.requireNonNull(main_directory.listFiles()).length;
        if (playlist_amount > 0) {
            for (File f : Objects.requireNonNull(main_directory.listFiles())
            ) {
                PlayerPlaylist p = new PlayerPlaylist(f);
                playlists.add(p);
                playlist_names.add(p.getName());
            }
            importSongButton.setDisable(false);
            playlistList.getItems().addAll(playlist_names);
        } else importSongButton.setDisable(true);
    }

    private void refreshSongs() {
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

    private Song getRandomSong(PlayerPlaylist p) {
        Random random = new Random();
        int random_int = random.nextInt(p.getSongs().size());
        Song song = p.getSongs().get(random_int);
        songNumber = random_int;
        return song;
    }

    private void refreshSongsOnSearch(String search) {
        if (search.isEmpty()) refreshSongs();
        else {
            current_songs.clear();
            current_song_names.clear();
            for (Song s : current_playlist.getSongs()) {
                if (songNameContainsString(s, search)) {
                    current_songs.add(s);
                    current_song_names.add(s.getGeneral_name());
                }
            }
            songList.getItems().clear();
            songList.getItems().addAll(current_song_names);
        }
    }

    private boolean songNameContainsString(Song song, String string) {
        String name = song.getName().toLowerCase();
        String author = song.getAuthor().toLowerCase();
        return (name + " " + author).contains(string.toLowerCase());
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        if (!main_directory.exists()) {
            main_directory.mkdir();
        }
        try {
            refreshPlaylists();
        } catch (InvalidDataException | UnsupportedTagException | PlaylistException | IOException | ParseException e) {
            throw new RuntimeException(e);
        }

        MenuItem deleteOption = new MenuItem("Delete");
        deleteOption.setOnAction((event) -> {
            try {
                deletePlaylist(current_playlist);
            } catch (PlaylistException | InvalidDataException | UnsupportedTagException | IOException | ParseException e) {
                throw new RuntimeException(e);
            }
        });
        playlistContextMenu.getItems().removeAll();
        playlistContextMenu.getItems().addAll(deleteOption);


        playlistList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    changeCurrentPlaylist(newValue);
                } catch (InvalidDataException | UnsupportedTagException | IOException e) {
                    e.printStackTrace();
                } catch (PlaylistException | ParseException e) {
                    throw new RuntimeException(e);
                }
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
        soundSlider.setValue(last_sound_value);
        changeSoundButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (!muted) {
                    last_sound_value = player.getVolume();
                    player.volumeProperty().setValue(0);
                    muted = true;
                    soundIcon.setImage(sound_off_image);
                } else {
                    player.volumeProperty().setValue(last_sound_value);
                    soundSlider.setValue(last_sound_value);
                    muted = false;
                    soundIcon.setImage(sound_on_image);
                }
            }
        });

        playlistContextMenu.getItems().add(deleteOption);
        importArea.getChildren().remove(importM3Ubutton);
        importArea.getChildren().remove(importDirButton);
        soundArea.getChildren().remove(soundSlider);
        soundArea.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                soundArea.getChildren().add(soundSlider);
            }
        });
        soundArea.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                soundArea.getChildren().remove(soundSlider);
            }
        });
        soundSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (muted) {
                    muted = false;
                    soundIcon.setImage(sound_on_image);
                }

                player.volumeProperty().setValue(newValue);
                last_sound_value = newValue.doubleValue();
            }
        });
        songProgressBar.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                player.seek(Duration.seconds(songProgressBar.getValue() * media.getDuration().toSeconds()));
            }
        });
        songProgressBar.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                player.seek(Duration.seconds(songProgressBar.getValue() * media.getDuration().toSeconds()));
            }
        });

        searchField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                refreshSongsOnSearch(newValue);
            }
        });

    }
}
