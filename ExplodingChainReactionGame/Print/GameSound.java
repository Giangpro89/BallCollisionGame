/**
 * 
 * This is a class specifically created to deal with playing sounds.
 * An audio clip is used to play whatever file is in the input stream.
 * The clip I think starts its own thread when called.
 * At specific points elsewhere in the program, a collision,
 * failure or success noise will play by passing in the file
 * location as an argument. The class/object takes care of the rest
 * 
 * This class/idea was spotted online and the reference to that page
 * is referenced below
 * 
 */
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

// This code is referenced below
// http://www.ntu.edu.sg/home/ehchua/programming/java/J8c_PlayingSound.html

public enum GameSound {
	COLLISION("collision.wav"), FAILURE("failure.wav"), SUCCESS("success.wav");

	private Clip clip;
	private AudioInputStream stream;

	GameSound(String fileName) {

		try {
			File url = new File(fileName);
			stream = AudioSystem.getAudioInputStream(url);
			clip = AudioSystem.getClip();
			clip.open(stream);
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}

	}

	public void play() {
		if (clip.isRunning())
			clip.stop(); // Stop the player if it is still running
		clip.setFramePosition(0); // rewind to the beginning
		clip.start();
	}
}
