package core;

import java.util.List;

/**
 * A wrapper for data input from out DataPlugin.
 */
public class ClientEvent {
  /** Keywords for an event. */
  private final List<String> keywords;

  /** Location of the event. */
  private final String location;

  /** Date of the event. */
  private final String date;

  /** Subject of the event. */
  private final String subject;

  /** Number of people attending. */
  private final int quantity;

  /**
   * The sole constructor for a client event.
   * @param keywordsL keywords
   * @param locationL location
   * @param dateL date
   * @param subjectL the who
   * @param quantityL quantity measurement
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

  /**
   * Returns subject.
   * @return subject
   */
  public String getSubject() {
    return subject;
  }

  /**
   * Returns quantity.
   * @return quantity
   */
  public int getQuantity() {
    return quantity;
  }

  /**
   * Returns date.
   * @return date
   */
  public String getDate() {
    return date;
  }

  /**
   * Returns location.
   * @return location
   */
  public String getLocation() {
    return location;
  }

  /**
   * Returns keywords.
   * @return keywords
   */
  public List<String> getKeywords() {
    return keywords;
  }
}
