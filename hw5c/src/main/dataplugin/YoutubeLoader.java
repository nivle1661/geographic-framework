package main.dataplugin;

import edu.cmu.cs.cs214.hw5.framework.core.MusicData;

import java.io.File;
import java.io.IOException;

public class YoutubeLoader extends MusicData {
  /**
   * Creates a new MusicData object
   *
   * @param file the File to the WAV file for this song
   * @param name the String name of the song
   * @throws IOException if the stream made from the file is not valid
   *                     or there is some other error in reading the stream
   */
  public YoutubeLoader(File file, String name) throws IOException {
    super(file, name);
  }
}
