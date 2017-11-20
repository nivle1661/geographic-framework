package framework.core;

/** Common interface for data plugins. */
public interface DataPlugin {
  /**
   * Set the subject of the plugin.
   * @param subject of plugin
   */
  void setSubject(String subject);

  /**
   * Gets the subject of the plugin.
   * @return subject of the plugin
   */
  String getSubject();

  /**
   * Opens the connection with the source file/webpage (e.g.
   * making BufferReader).
   * @param arg sourcefor data
   * @return succesful connection made
   */
  boolean openConnection(String arg);

  /**
   * Returns the next event data from the source.
   * @return next event data from the source
   */
  ClientEvent getEvent();

  /**
   * Returns whether we have more data or not.
   * @return whether we have more data or not
   */
  boolean hasNext();

  /**
   * Closes the connection we currently have.
   */
  void closeConnection();
}
