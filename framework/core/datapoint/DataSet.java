package core.datapoint;

import core.ClientEvent;

import java.util.ArrayList;
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
      events.add(newEvent);
    }
  }

  /**
   * Calculates distances, keywords, frequency hashmaps, and optimal
   * route.
   */
  public void process() {
    //TODO: Do this
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
