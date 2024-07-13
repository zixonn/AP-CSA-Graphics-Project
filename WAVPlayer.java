import javax.sound.sampled.*;
import java.io.*;

public class WAVPlayer {
    private Clip clip;

    public WAVPlayer(String fileName) {
        try {
            // Load audio input stream from the file
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(fileName));

            // Get clip from audio input stream
            clip = AudioSystem.getClip();

            // Open the clip
            clip.open(audioInputStream);

            // Enable looping
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    // Method to start playing the audio
    public void play() {
        if (clip != null) {
            clip.start();
        }
    }

    // Method to stop playing the audio
    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }
}
