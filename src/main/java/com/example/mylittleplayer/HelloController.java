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
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.effect.ColorAdjust;
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
import static org.apache.commons.io.FileUtils.*;

/**
 * Controller
 */
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
    private IntegerProperty songNumberProperty = new SimpleIntegerProperty(songNumber);
    private Timer timer;
    private TimerTask task;
    private boolean active_track = false;
    private boolean muted = false;
    private boolean shuffle_on = false;
    private boolean repeat_on = false;

    private double last_sound_value = 70;
    private Playlist current_playlist = new Playlist();
    private ArrayList<Playlist> playlists = new ArrayList<>();
    private ArrayList<String> playlist_names = new ArrayList<>();
    private ArrayList<Song> current_songs = new ArrayList<>();
    private ArrayList<String> current_song_names = new ArrayList<>();
    private File main_directory = new File("C:\\Playlists");
    private ColorAdjust opacity_up = new ColorAdjust();

    /**
     * Getting the path to the playlist
     * @throws IOException
     * @throws InvalidDataException
     * @throws UnsupportedTagException
     */
    @FXML
    void importPlaylist(ActionEvent event) throws IOException, InvalidDataException, UnsupportedTagException {

        DirectoryChooser directoryChooser = new DirectoryChooser();
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

    /**
     * Getting the path to the song
     */
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

    /**
     * Operation with Pause button
     * @see HelloController#pause()
     * @see HelloController#play()
     */
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

    /**
     * Operation with Shuffle button
     */
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

    /**
     * Operation with Repeat button
     */
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

    /**
     * Operation with Previous
     */
    @FXML
    void previousMedia(ActionEvent event) {
        if (shuffle_on) {
            player.stop();
            songToPlay(getRandomSong(current_playlist));
        } else {

            if (songNumber > 0) {
                songNumber--;
            } else songNumber = current_playlist.getSongs().size() - 1;
            songNumberProperty = new SimpleIntegerProperty(songNumber);
            songToPlay(current_playlist.getSongs().get(songNumber));
        }
    }
    /**
     * Operation with Next
     */
    @FXML
    void nextMedia(ActionEvent event) {
        if (shuffle_on) {
            player.stop();
            songToPlay(getRandomSong(current_playlist));
        } else {
            if (songNumber < current_playlist.getSongs().size() - 1) {
                songNumber++;
            } else songNumber = 0;
            songNumberProperty = new SimpleIntegerProperty(songNumber);
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
                double progress = current_time/end_time;
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

    private void changeCurrentPlaylist(String new_name) throws InvalidDataException, UnsupportedTagException, IOException {
        current_playlist = new Playlist(new File(main_directory + "/" + new_name));
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

    private void refreshPlaylists() throws InvalidDataException, UnsupportedTagException, IOException {
        int playlist_amount = Objects.requireNonNull(main_directory.listFiles()).length;
        if (playlist_amount > 0) {
            playlists.clear();
            playlist_names.clear();
            for (File f : Objects.requireNonNull(main_directory.listFiles())) {
                playlists.add(new Playlist(f));
                playlist_names.add(f.getName());
            }
            importSongButton.setDisable(false);
            playlistList.getItems().clear();
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

    private Song getRandomSong(Playlist p) {
        Random random = new Random();
        int random_int = random.nextInt(p.getSongs().size());
        Song song = p.getSongs().get(random_int);
        songNumber = random_int;
        songNumberProperty = new SimpleIntegerProperty(songNumber);
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

    /**
     * @param location - URL path
     * @param resources -
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {


        if (!main_directory.exists()) {
            main_directory.mkdir();
        }
        try {
            refreshPlaylists();
        } catch (InvalidDataException | UnsupportedTagException | IOException e) {
            e.printStackTrace();
        }
        playlistList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    changeCurrentPlaylist(newValue);
                } catch (InvalidDataException | UnsupportedTagException | IOException e) {
                    e.printStackTrace();
                }
                refreshSongs();
            }
        });
        songList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    stopCurrentSong();
                    songNumber = songList.getSelectionModel().getSelectedIndex();
                    songNumberProperty = new SimpleIntegerProperty(songNumber);
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
                player.seek(Duration.seconds(songProgressBar.getValue()*media.getDuration().toSeconds()));
            }
        });
        songProgressBar.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                player.seek(Duration.seconds(songProgressBar.getValue()*media.getDuration().toSeconds()));
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
