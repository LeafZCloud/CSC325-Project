package edu.farmingdale.demo1;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

public final class AudioManager {

    private static final String INTRO_MUSIC = "/audios/IntroMusic.mp3";
    private static final String GAME_MUSIC = "/audios/GameMusic.mp3";

    private static MediaPlayer currentPlayer;
    private static String currentTrack;

    private AudioManager() {
    }

    public static void playIntroMusic() {
        playLoopingTrack(INTRO_MUSIC);
    }

    public static void playGameMusic() {
        playLoopingTrack(GAME_MUSIC);
    }

    public static void stopMusic() {
        if (currentPlayer != null) {
            currentPlayer.stop();
            currentPlayer.dispose();
            currentPlayer = null;
            currentTrack = null;
        }
    }

    private static void playLoopingTrack(String resourcePath) {
        if (resourcePath.equals(currentTrack) && currentPlayer != null) {
            return;
        }

        stopMusic();

        URL audioUrl = AudioManager.class.getResource(resourcePath);
        if (audioUrl == null) {
            System.err.println("Audio file not found: " + resourcePath);
            return;
        }

        Media media = new Media(audioUrl.toExternalForm());
        MediaPlayer player = new MediaPlayer(media);
        player.setCycleCount(MediaPlayer.INDEFINITE);
        player.setVolume(0.45);
        player.play();

        currentPlayer = player;
        currentTrack = resourcePath;
    }
}
