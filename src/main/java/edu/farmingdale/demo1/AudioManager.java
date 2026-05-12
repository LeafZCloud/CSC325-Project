package edu.farmingdale.demo1;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

public final class AudioManager {

    private static final String INTRO_MUSIC = "/audios/IntroMusic.mp3";
    private static final String GAME_MUSIC = "/audios/GameMusic.mp3";
    private static final String SNOW_EVENT = "/audios/blizzardWind.mp3";
    private static final String QUAKE_EVENT = "/audios/earthquakeRumble.mp3";
    private static final String METEOR_EVENT = "/audios/meteorBoom.mp3";
    private static final String INDUSTRIAL_EVENT = "/audios/industrialHonk.mp3";
    private static final String VOLCANIC_EVENT = "/audios/bubblingLava.mp3";
    private static final String PLAGUE_EVENT = "/audios/sickCough.mp3";
    private static final String NUKE_EVENT = "/audios/nuclearExplosion.mp3";
    private static final String WAR_EVENT = "/audios/warBattlefield.mp3";
    private static final String MEDICAL_EVENT = "/audios/yay.mp3";
    private static final String GOLDEN_EVENT = "/audios/goldenAge.mp3";

    private static MediaPlayer currentPlayer;
    private static String currentTrack;
    private static MediaPlayer currentSfxPlayer;

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

    public static void play(String resourcePath) {

        URL audioUrl = AudioManager.class.getResource(resourcePath);

        if (audioUrl == null) {
            System.err.println("Sound not found: " + resourcePath);
            return;
        }

        // stop previous sound effect (THIS is the key fix)
        if (currentSfxPlayer != null) {
            currentSfxPlayer.stop();
            currentSfxPlayer.dispose();
            currentSfxPlayer = null;
        }

        Media media = new Media(audioUrl.toExternalForm());
        MediaPlayer player = new MediaPlayer(media);

        player.setVolume(0.4);

        player.setOnEndOfMedia(() -> {
            player.dispose();
            if (currentSfxPlayer == player) {
                currentSfxPlayer = null;
            }
        });

        currentSfxPlayer = player;
        player.play();
    }

    public static void playSoundEffect(String eventId) {

        if (eventId == null) return;

        eventId = eventId.trim().toLowerCase();

        switch (eventId) {
            case "ice_age" -> play(SNOW_EVENT);
            case "earthquakes" -> play(QUAKE_EVENT);
            case "meteor" -> play(METEOR_EVENT);
            case "industrial_revolution" -> play(INDUSTRIAL_EVENT);
            case "volcanic_eruptions" -> play(VOLCANIC_EVENT);
            case "plague" -> play(PLAGUE_EVENT);
            case "nuke" -> play(NUKE_EVENT);
            case "world_war" -> play(WAR_EVENT);
            case "medical_breakthrough" -> play(MEDICAL_EVENT);
            case "golden_age" -> play(GOLDEN_EVENT);
        }
    }

}
