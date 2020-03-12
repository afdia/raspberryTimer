package raspberrypi;

import java.awt.Frame;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class Main {
	private static JEditorPane htmlTextArea = new JEditorPane("text/html", html(0));

	private static void createAndShowGUI() {
		JFrame.setDefaultLookAndFeelDecorated(true);

		JFrame frame = new JFrame("Timer");
		htmlTextArea.setEditable(false);
		frame.getContentPane().add(htmlTextArea);

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		frame.setUndecorated(true);
		frame.pack();
		frame.setVisible(true);
	}

	private static String html(int nr) {
		int min = nr / 60;
		int sec = nr - min * 60;
		String text = String.format("%02d", min) + ":" + String.format("%02d", sec);
		return "<html>\n"
				+ "<p style=\""
				+ "color:black;"
				+ "background-color:white;"
				+ "font-size:3.2em;"
				+ "text-align: center;"
				+ "font-family: sans-serif;"
				+ "\">" + text + "</p>\n" +
				"</html>\n";
	}

	static int INITIAL_DELAY = 600;
	static int NEXT_DELAY = 60;
	static int BEEP_TIME_DUR = 20;
	static int timeSec = INITIAL_DELAY;
	static int beepTimeSec = BEEP_TIME_DUR;

	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(() -> createAndShowGUI()); // create and show app

		Clip clip = setupClip();
		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> {
			if (beepTimeSec == 0) {
				System.out.println("Resetting");
				timeSec = NEXT_DELAY;
				beepTimeSec = BEEP_TIME_DUR;
				clip.stop();
			} else if (timeSec < 0) {
				System.out.println("Beeping for " + beepTimeSec--);
			} else {
				System.out.println("Beeping in " + timeSec);
				if (timeSec == 0) {
					beepTimeSec--;
				}
				htmlTextArea.setText(html(timeSec));
				if (timeSec == 0) {
					clip.setMicrosecondPosition(0);
					clip.start();
				}
				timeSec--;
			}
		}, 0, 1, TimeUnit.SECONDS);
	}

	private static Clip setupClip() {
		try {
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(Main.class.getResource("/alarm.wav"));
			Clip clip = AudioSystem.getClip();
			clip.open(audioIn);
			return clip;
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			throw new RuntimeException(e);
		}
	}
}