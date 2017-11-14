package dataplugin;

import core.ClientEvent;
import core.DataPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

/** DataPlugin for CSV file. */
public class CSVPlugin implements DataPlugin {
  /** Input reader. */
  private BufferedReader input;

  /** Next line of BufferedReader. */
  private String nextLine;

  /** Subject for the file. */
  private String subject;

  /**
   * Creates a plugin for CSV files
   */
  public CSVPlugin() {
    input = null;
    nextLine = null;
    subject = null;
  }

  /**
   * Set the subject of the plugin.
   * @param subject
   */
  @Override
  public void setSubject(String subject) {
    this.subject = subject;
  }

  /**
   * Gets the subject of the plugin.
   * @return subject of the plugin
   */
  @Override
  public String getSubject() {
    return subject;
  }

  /**
   * Opens connection with csv file in resource path.
   * @param arg sourcefor data
   * @return connection succesful
   */
  @Override
  public boolean openConnection(String arg) {
    try {
      input = new BufferedReader(new
              InputStreamReader(getClass().getClassLoader().getResourceAsStream(arg)));
      input.readLine();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return input != null;
  }

  /**
   * Returns the next event data from the source.
   * @return next event data from the source
   */
  @Override
  public ClientEvent getEvent() {
    String[] fields = nextLine.split(",");

    //Splits based on white spaces
    String[] keywords = fields[3].split("\\s+");
    int quantity = Integer.parseInt(fields[2]);
    return new ClientEvent(new ArrayList<>(Arrays.asList(keywords)),
            fields[0], fields[1], subject, quantity);
  }

  /**
   * Returns whether there is another event to process.
   * @return whether there is another event to process
   */
  @Override
  public boolean hasNext() {
    if (input != null) {
      try {
        nextLine = input.readLine();
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      return false;
    }
    return nextLine == null;
  }

  /**
   * Closes the connection we currently have.
   */
  @Override
  public void closeConnection() {
    if (input != null) {
      try {
        input.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Returns string representation of data plugin.
   * @return string representation of data plugin
   */
  @Override
  public String toString() {
    return "Spreadsheet loader";
  }
}
