package core.datapoint;

import core.ClientEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static core.datapoint.Event.MAX_LATLONG;

/** Holds all the event data from one connection from a dataplugin. */
public class DataSet {
  /** The list of events. */
  private List<Event> events;

  /** Name of the dataset. */
  private String name;

  /** Contains pairwise distances between all events. */
  private double[][] distances;

  /** Contains all keywords from all events in dataset. */
  private List<String> keywords;

  /** Frequency of events per location. */
  private Map<String, Integer> freqLoc;
  /** Frequency of keywords. */
  private Map<String, Integer> freqKeyword;

  /** Minimum longitude. */
  private double minLong;
  /** Maximum longitude. */
  private double maxLong;
  /** Minimum latitude. */
  private double minLat;
  /** Maximum Latitude. */
  private double maxLat;

  /** Optimal route. */
  private List<Event> route;

  /**
   * Constructor of empty DataSet.
   * @param nameL name of dataset
   */
  public DataSet(final String nameL) {
    name = nameL;

    events = new ArrayList<>();
    keywords = new ArrayList<>();

    freqLoc = new HashMap<>();
    freqKeyword = new HashMap<>();

    minLat = MAX_LATLONG;
    minLong = MAX_LATLONG;
    maxLat = -MAX_LATLONG;
    maxLat = MAX_LATLONG;
  }

  /**
   * Constructs a dataset with already given fields.
   * @param eventsL events
   * @param freqKeywordL keyword frequency map
   * @param freqLocL location frequency map
   * @param minLatL minimum latitude
   * @param maxLatL maximum latitude
   * @param minLongL minimum longitude
   * @param maxLongL maximum longitude
   */
  public DataSet(final List<Event> eventsL,
                 final Map<String, Integer> freqKeywordL,
                 final Map<String, Integer> freqLocL,
                 final double minLatL, final double maxLatL,
                 final double minLongL, final double maxLongL) {
    events = eventsL;
    freqKeyword = freqKeywordL;
    freqLoc = freqLocL;

    minLat = minLatL;
    maxLat = maxLatL;
    minLong = minLongL;
    maxLong = maxLongL;
  }

  /**
   * Sets a single priority for all events.
   * @param priority of events
   */
  public void setPriority(final int priority) {
    for (Event event : events) {
      event.setPriority(priority);
    }
  }

  /**
   * Adds an event from client input.
   * @param clientEvent client input
   */
  public void addEvent(final ClientEvent clientEvent) {
    Event newEvent = new Event(clientEvent);

    if (newEvent != null) {
      List<String> temp = newEvent.keywords;
      String location = newEvent.location;
      events.add(newEvent);
      minLat = Math.min(newEvent.southLatitude, minLat);
      maxLat = Math.max(newEvent.northLatitude, maxLat);
      minLong = Math.min(newEvent.westLongitude, minLong);
      maxLong = Math.max(newEvent.eastLongitude, maxLong);

      //Incrementing values for frequency counts
      for (String keyword : temp) {
        int count = freqKeyword.getOrDefault(keyword, 0);
        freqKeyword.put(keyword, count + 1);
      }
      int count = freqLoc.getOrDefault(location, 0);
      freqKeyword.put(location, count + 1);
    }
  }

  /**
   * Sorts events by time.
   */
  public void process() {
    Collections.sort(events);
  }

  /**
   * Combins multiple datasets into one.
   * @param datasets to combine
   */
  public static DataSet combineDatasets(final List<DataSet> datasets) {
    double minLong = MAX_LATLONG, maxLong = -MAX_LATLONG;
    double minLat = MAX_LATLONG, maxLat = -MAX_LATLONG;

    //TODO: Do this
    Map<String, Integer> freqKeyword = new HashMap<>();
    Map<String, Integer> freqLoc = new HashMap<>();
    List<Event> events = new ArrayList<>();

    for (DataSet dataset : datasets) {
      events.addAll(dataset.events);
      minLong = Math.min(minLong, dataset.minLong);
      maxLong = Math.max(maxLong, dataset.maxLong);
      minLat = Math.min(minLat, dataset.minLat);
      maxLat = Math.max(maxLat, dataset.maxLat);

      Set<String> tempKey = dataset.freqKeyword.keySet();
      Set<String> tempLoc = dataset.freqLoc.keySet();

      for (String key : tempKey) {
        int count = freqKeyword.getOrDefault(key, 0);
        freqKeyword.put(key, count + dataset.freqKeyword.get(key));
      }
      for (String loc : tempLoc) {
        int count = freqLoc.getOrDefault(loc, 0);
        freqLoc.put(loc, count + dataset.freqLoc.get(loc));
      }
    }
    Collections.sort(events);

    return new DataSet(events, freqKeyword, freqLoc,
                       minLat, maxLat, minLong, maxLong);
  }

  /**
   * Returns name of data plugin.
   * @return name
   */
  @Override
  public String toString() {
    return name;
  }
}
