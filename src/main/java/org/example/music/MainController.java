package org.example.music;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import java.io.File;

public class MainController {

    @FXML private TableView<Track> tracksTable;
    @FXML private TableColumn<Track, String> titleColumn;
    @FXML private TableColumn<Track, String> artistColumn;
    @FXML private Slider progressSlider;
    @FXML private Label timeCurrent, timeTotal;
    @FXML private TextField searchField;
    @FXML private Slider volumeSlider;
    @FXML private Label currentUserLabel;
    private final ObservableList<Track> trackList = FXCollections.observableArrayList();
    private MediaPlayer mediaPlayer;
    @FXML
    public void initialize() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        artistColumn.setCellValueFactory(new PropertyValueFactory<>("artist"));
        tracksTable.setItems(trackList);
        searchField.textProperty().addListener((obs, old, newVal) -> filterTracks(newVal));
        if (volumeSlider != null) {
            volumeSlider.setValue(80);
            volumeSlider.valueProperty().addListener((obs, old, newVal) -> {
                if (mediaPlayer != null) {
                    mediaPlayer.setVolume(newVal.doubleValue() / 100.0);
                }
            });
        }
        progressSlider.setOnMousePressed(e -> {
            if (mediaPlayer != null) mediaPlayer.seek(Duration.seconds(progressSlider.getValue()));
        });
        if (currentUserLabel != null && UserSession.getUsername() != null) {
            currentUserLabel.setText("👤 " + UserSession.getUsername());
        }
    }
    private void filterTracks(String query) {
        if (query == null || query.trim().isEmpty()) {
            tracksTable.setItems(trackList);
        } else {
            String q = query.toLowerCase();
            ObservableList<Track> filtered = trackList.filtered(track ->
                    track.getTitle().toLowerCase().contains(q) ||
                            track.getArtist().toLowerCase().contains(q)
            );
            tracksTable.setItems(filtered);
        }
    }
    @FXML
    private void handleCreatePlaylist() {
        TextInputDialog dialog = new TextInputDialog("Новый плейлист");
        dialog.setTitle("Создание плейлиста");
        dialog.setHeaderText("Введите название плейлиста");
        dialog.setContentText("Название:");
        dialog.showAndWait().ifPresent(name -> {
            if (!name.trim().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Плейлист '" + name + "' создан!");
                alert.showAndWait();
            }
        });
    }
    @FXML
    private void handlePlayPause() {
        Track selected = tracksTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        if (mediaPlayer != null && mediaPlayer.getMedia().getSource().equals(selected.getPath())) {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) mediaPlayer.pause();
            else mediaPlayer.play();
        } else {
            if (mediaPlayer != null) mediaPlayer.stop();
            mediaPlayer = new MediaPlayer(new Media(selected.getPath()));
            mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                if (!progressSlider.isValueChanging()) {
                    progressSlider.setValue(newTime.toSeconds());
                    timeCurrent.setText(formatTime(newTime));
                }
            });
            mediaPlayer.setOnReady(() -> {
                progressSlider.setMax(mediaPlayer.getTotalDuration().toSeconds());
                timeTotal.setText(formatTime(mediaPlayer.getTotalDuration()));
            });
            mediaPlayer.play();
        }
    }
    @FXML
    private void handleDeleteTrack() {
        Track selected = tracksTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Удаление трека");
        alert.setContentText("Удалить трек \"" + selected.getTitle() + "\"?");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                trackList.remove(selected);
            }
        });
    }
    @FXML
    private void handleImport() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio Files", "*.mp3", "*.wav", "*.m4a"));
        File file = fc.showOpenDialog(null);
        if (file != null) {
            trackList.add(new Track(file.getName(), "Unknown", file.toURI().toString()));
        }
    }
    @FXML
    private void handleLogout() {
        UserSession.logout();
        MainApp.showScene("auth-view.fxml", "SoundWave - Вход");
    }
    @FXML private void handleNext() {}
    @FXML private void handlePrev() {}

    private String formatTime(Duration elapsed) {
        int minutes = (int) elapsed.toMinutes();
        int seconds = (int) elapsed.toSeconds() % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
    public ObservableList<Track> getTrackListForTest() {
        return trackList;
    }
}