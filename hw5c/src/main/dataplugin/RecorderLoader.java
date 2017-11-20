package main.dataplugin;

import edu.cmu.cs.cs214.hw5.framework.core.DataLoader;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JOptionPane;
import java.io.File;
import java.util.UUID;

public class RecorderLoader implements DataLoader {
  TargetDataLine line;
  AudioFormat a = new AudioFormat(16000, 8, 2, true, true);
  File recording;

  private class Recorder {
    public Recorder() {
      String length = JOptionPane
              .showInputDialog("How long do you want to record (seconds)");
      int milliseconds = Integer.parseInt(length) * 1000;
      start(milliseconds);
    }

    public void start(int milliseconds) {
      try {
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, a);
        line = (TargetDataLine) AudioSystem.getLine(info);
        line.open(a);
        line.start();

        try {
          Thread.sleep(milliseconds);
        } catch(InterruptedException ex) {
          Thread.currentThread().interrupt();
        }

        line.stop();
        line.close();

        AudioInputStream ais = new AudioInputStream(line);

        //TODO: fix this
        recording = new File("framework/files/" + UUID.randomUUID() + ".wav");
        System.out.println(recording.getAbsolutePath());
        AudioSystem.write(ais, AudioFileFormat.Type.WAVE,
                recording);
        JOptionPane.showMessageDialog(null, "Finished.");
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public File onSelect() {
    new Recorder();
    return recording;
  }

  @Override
  public String toString() {
    return "Recorder";
  }
}
