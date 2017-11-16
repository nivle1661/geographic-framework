package core.datapoint;

import core.ClientEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static core.datapoint.Event.MAX_LATLONG;
import static core.datapoint.Event.distance;

/** Holds all the event data from one connection from a dataplugin. */
public class DataSet {
  /** The list of events. */
  private List<Event> events;

  /** Name of the dataset. */
  private String name;

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

  /** Convert from millesconds to hours. */
  private final double mille = 60 * 60 * 1000;

  /**
   * Constructor of empty DataSet.
   * @param nameL name of dataset
   */
  public DataSet(final String nameL) {
    name = nameL;

    events = new ArrayList<>();
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
   * @param latitudes min and max
   * @param longitudes min and max
   */
  public DataSet(final List<Event> eventsL,
                 final Map<String, Integer> freqKeywordL,
                 final Map<String, Integer> freqLocL,
                 final double[] latitudes, final double[] longitudes) {
    events = eventsL;
    freqKeyword = freqKeywordL;
    freqLoc = freqLocL;

    minLat = latitudes[0];
    maxLat = latitudes[1];
    minLong = longitudes[0];
    maxLong = longitudes[1];
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
      List<String> temp = newEvent.getKeywords();
      String location = newEvent.getLocation();
      events.add(newEvent);
      minLat = Math.min(newEvent.getLatitudeBound()[0], minLat);
      maxLat = Math.max(newEvent.getLatitudeBound()[1], maxLat);
      minLong = Math.min(newEvent.getLongitudeBound()[0], minLong);
      maxLong = Math.max(newEvent.getLongitudeBound()[1], maxLong);

      //Incrementing values for frequency counts
      if (temp != null) {
        for (String keyword : temp) {
          int count = freqKeyword.getOrDefault(keyword, 0);
          freqKeyword.put(keyword, count + 1);
        }
      }
      int count = freqLoc.getOrDefault(location, 0);
      freqLoc.put(location, count + 1);
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
   * @return aggregate dataset
   */
  public static DataSet combineDatasets(final List<DataSet> datasets) {
    double minLong = MAX_LATLONG, maxLong = -MAX_LATLONG;
    double minLat = MAX_LATLONG, maxLat = -MAX_LATLONG;

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
                       new double[]{minLat, maxLat},
                       new double[]{minLong, maxLong});
  }

  /**
   * Returns an optimal route based on priority.
   * @param speed maximum speed
   * @return list of events in order
   */
  public List<Event> getOptimalRoute(final double speed) {
    List<Event> priorityEvents = new ArrayList<>(events);
    Collections.sort(priorityEvents, (o1, o2) -> {
      if (o1.getPriority() == o2.getPriority()) {
        return o1.getTime().compareTo(o2.getTime());
      }
      return Integer.compare(o2.getPriority(), o1.getPriority());
    });

    List<Event> result = new ArrayList<>();
    result.add(priorityEvents.remove(0));

    while (priorityEvents.size() > 0) {
      Event temp = priorityEvents.remove(0);
      int j = result.size();
      while ((j > 0) && result.get(j - 1).getTime().after(temp.getTime())) {
        j--;
      }

      //Check if reachable from element before and after it
      boolean okBefore = (j == 0);
      if (!okBefore) {
        Event tempL = result.get(j - 1);
        okBefore = speed
              > distance(temp, tempL) / ((temp.getTime().getTime()
                                          - tempL.getTime().getTime()) / mille);
      }
      boolean okAfter = (j == result.size() || j == result.size() - 1);
      if (!okAfter) {
        Event tempL = result.get(j + 1);
        okAfter = speed
              > distance(temp, tempL) / ((tempL.getTime().getTime()
                                         - temp.getTime().getTime()) / mille);
      }

      if (okBefore && okAfter) {
        result.add(j, temp);
      }
    }
    return result;
  }

  /**
   * Returns location frequency map.
   * @return location frequency map
   */
  public Map<String, Integer> getFreqLoc() {
    return freqLoc;
  }

  /**
   * Returns keyword frequency map.
   * @return keyword frequency map
   */
  public Map<String, Integer> getFreqKeyword() {
    return freqKeyword;
  }

  /**
   * Returns name of data plugin.
   * @return name
   */
  @Override
  public String toString() {
    return name;
  }
  
  /**
   * Returns the list of events
   * @return list of events
   */
  public List<Event> getEvents() {
	  return events;
  }
  
}
