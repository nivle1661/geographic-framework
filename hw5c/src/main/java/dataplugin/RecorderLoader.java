package dataplugin;

import edu.cmu.cs.cs214.hw5.framework.core.DataLoader;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JOptionPane;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class RecorderLoader implements DataLoader {
  private volatile File result;

  public class Recorder {
    TargetDataLine line;
    AudioFormat a = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, (float)44100,
            16, 1, 2, (float)44100, false);

    public void start() {
      try {
        AudioFormat format = a;
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        // checks if system supports the data line
        if (!AudioSystem.isLineSupported(info)) {
          System.out.println("Line not supported");
        }
        line = (TargetDataLine) AudioSystem.getLine(info);
        line.open(a);
        line.start();

        AudioInputStream ais = new AudioInputStream(line);
        result = new File("../framework/files/" + UUID.randomUUID() + ".wav");
        AudioSystem.write(ais, AudioFileFormat.Type.WAVE, result);
      } catch (LineUnavailableException ex) {
          ex.printStackTrace();
      } catch (IOException ioe) {
          ioe.printStackTrace();
      }
    }

    public void finish() {
      line.stop();
      line.close();
    }
  }

  @Override
  public File onSelect() {
    Recorder recorder = new Recorder();
    int sleeptime = Integer.parseInt(JOptionPane
            .showInputDialog("How long do you want to record (seconds)")) * 1000;
    if (sleeptime < 3) {
      JOptionPane.showMessageDialog(null, "Not long enough.");
      return null;
    }
    Thread stopper = new Thread(new Runnable() {
      public void run() {
        try {
          Thread.sleep(sleeptime);
        } catch (InterruptedException ex) {
          ex.printStackTrace();
        }
        recorder.finish();
      }
    });
    stopper.start();
    recorder.start();

    JOptionPane.showMessageDialog(null, "Done!");
    return result;
  }

  @Override
  public String toString() {
    return "Recorder";
  }
}

