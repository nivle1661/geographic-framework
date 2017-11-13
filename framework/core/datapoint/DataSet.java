package core.datapoint;

import core.ClientEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

  /** Optimal route. */
  private List<Event> route;

  /**
   * Sole constructor of DataSet.
   * @param nameL name of dataset
   */
  public DataSet(final String nameL) {
    name = nameL;

    events = new ArrayList<>();
    keywords = new ArrayList<>();

    freqLoc = new HashMap<>();
    freqKeyword = new HashMap<>();
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

      //Incrementing values for frequency counts
      for (String keyword : temp) {
        int count = freqKeyword.containsKey(keyword) ? freqKeyword.get(keyword) : 0;
        freqKeyword.put(keyword, count + 1);
      }
      int count = freqLoc.containsKey(location) ? freqLoc.get(location) : 0;
      freqKeyword.put(location, count + 1);
    }
  }

  /**
   * Calculates distances, keywords, frequency hashmaps, and optimal
   * route.
   */
  public void process() {
    //TODO: Do this
    Collections.sort(events);
  }

  /**
   * Combins multiple datasets into one.
   * @param datasets to combine
   */
  public static DataSet combineDatasets(final List<DataSet> datasets) {
    //TODO: Do this
    return null;
  }
}
