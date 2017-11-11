package core.datapoint;

import core.ClientEvent;

import java.sql.Time;
import java.util.List;

/**
 * Internal representation of an event by the Framework.
 */
public class Event implements Comparable<Event> {
  /** The who of the event. */
  public final String subject;

  /** Time of the event. */
  public final Time time;

  /** Location of the event as a string. */
  public final String location;

  /** Latitude of the event. */
  public final double latitude;
  /** Longitude of the event. */
  public final double longitude;

  /** Keywords of the event. */
  public final List<String> keywords;

  /** How many people attended (OPTIONAL). */
  public final int quantity;
  /** The priority of the event (OPTIONAL). */
  private int priority;

  /**
   * Sole constructor for Event.
   */
  public Event(final ClientEvent event) {
    keywords = event.keywords;
    location = event.location;
    subject = event.subject;
    quantity = event.quantity;

    time = null;
    longitude = 0;
    latitude = 0;
  }

  /**
   * Sets the priority of the event.
   * @param priority of event
   */
  public void setPriority(int priority) {
    this.priority = priority;
  }

  /**
   * Gets the priority of the event.
   * @return priority of event
   */
  public int getPriority() {
    return priority;
  }

  /**
   * Compares two events based on time
   * @param o other event
   * @return comparison of times.
   */
  @Override
  public int compareTo(Event o) {
    return time.compareTo(o.time);
  }
}
