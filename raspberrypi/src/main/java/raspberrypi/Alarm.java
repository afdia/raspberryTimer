package raspberrypi;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Alarm {

	static String propPath = System.getProperty("properties");

	static Map<LocalTime, String> alarms = new HashMap<>();
	static Map<String, Clip> clips = new HashMap<>();

	public static void main(String[] args) throws IOException {
		// List<String> props = Files.readAllLines(Paths.get(Alarm.class.getResource("sound.properties").toURI()));
		List<String> props = Files.readAllLines(Paths.get(Objects.requireNonNull(propPath, "mit -Dproperties=<pfad> den Pfad zu den Properties angeben")));
		for (String prop : props) {
			try {
				LocalTime time = LocalTime.parse(prop.substring(0, prop.indexOf(" ")));
				String clipPath = prop.substring(prop.indexOf(" ") + 1);
				clips.computeIfAbsent(clipPath, p -> setupClip(new File(p)));
				String old = alarms.put(time, clipPath);
				if (old != null) {
					System.out.println("'" + time + " " + old + "' durch '" + time + " " + clipPath + "' ersetzt (Zeit ist gleich)");
				}
			}
			catch (Exception e) {
				System.out.println("ignoriere ungültige Zeile: " + prop);
			}
		}
		LocalTime now = LocalTime.now();
		Long secUntil00 = 60l - now.getSecond();
		System.out.println("Started at " + now + " - timer starts in " + secUntil00 + " seconds");

		Executors.newScheduledThreadPool(1).scheduleAtFixedRate(() -> onRun(), secUntil00, 60, TimeUnit.SECONDS);
	}

	private static void onRun() {
		try {
			LocalTime now = LocalTime.now().withSecond(0).withNano(0);
			String clipPath = alarms.get(now);
			if (clipPath == null) {
				System.out.println(now + " - skip");
			}
			else {
				System.out.println(now + " - Playing " + clipPath);
				Clip clip = clips.get(clipPath);
				clip.setMicrosecondPosition(0);
				clip.start();
			}
		}
		catch (Exception e) {
			System.err.println("Exception: " + e);
		}
	}

	private static Clip setupClip(File file) {
		try {
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(file);
			Clip clip = AudioSystem.getClip();
			clip.open(audioIn);
			return clip;
		}
		catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			throw new RuntimeException(e);
		}
	}

}