package core;

import java.util.List;

/**
 * A wrapper for data input from out DataPlugin.
 */
public class ClientEvent {
  /** Keywords for an event. */
  public final List<String> keywords;

  /** Location of the event. */
  public final String location;

  /** Date of the event. */
  public final String date;

  /** Subject of the event. */
  public final String subject;

  /** Number of people attending. */
  public final int quantity;

  /**
   * The sole constructor for a client event.
   * @param keywordsL keywords
   * @param locationL location
   * @param dateL date
   */
  public ClientEvent(final List<String> keywordsL,
                     final String locationL,
                     final String dateL,
                     final String subjectL,
                     final int quantityL) {
    keywords = keywordsL;
    location = locationL;
    date = dateL;
    subject = subjectL;
    quantity = quantityL;
  }
}
