package dataplugin;

import core.ClientEvent;
import core.DataPlugin;

import java.io.BufferedReader;
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
   * Creates a plugin for CSV files.
   */
  public CSVPlugin() {
    input = null;
    nextLine = null;
    subject = null;
  }

  /**
   * Set the subject of the plugin.
   * @param subjectL of plugin
   */
  @Override
  public void setSubject(final String subjectL) {
    this.subject = subjectL;
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
  public boolean openConnection(final String arg) {
    try {
      input = new BufferedReader(new
              InputStreamReader(getClass().getClassLoader().
              getResourceAsStream(arg)));
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
    String location;
    String[] fields;
    if (nextLine.contains("\"")) {
      int first = nextLine.indexOf("\"");
      int second = nextLine.indexOf("\"", first + 1);
      location = nextLine.substring(1, second);
      fields = nextLine.substring(second + 2).split(",");
    } else {
      int index = nextLine.indexOf(",");
      location = nextLine.substring(0, index);
      fields = nextLine.substring(index + 1).split(",");
    }
    System.out.println(location);

    //Splits based on white spaces
    String[] keywords = null;
    int quantity;
    try {
      quantity = Integer.parseInt(fields[1]);
      if (fields.length > 2) {
        keywords = fields[2].split("\\s+");
      }
    } catch (NumberFormatException e) {
      quantity = 0;
      keywords = fields[1].split("\\s+");
    }

    if (keywords == null) {
      return new ClientEvent(null, location, fields[0], subject, quantity);
    }
    return new ClientEvent(new ArrayList<>(Arrays.asList(keywords)),
            location, fields[0], subject, quantity);
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
    return nextLine != null;
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
