package core;

import core.datapoint.DataSet;

import java.util.ArrayList;
import java.util.List;

/** Framework implementation. */
public class MapperFramework {
  /** GUI listener. */
  private MapperListener listener;
  /** Registered data plugins. */
  private List<DataPlugin> dataplugins;
  /** Registered visual plugins. */
  private List<VisualPlugin> visualplugins;
  /** Available datasets. */
  private List<DataSet> datasets;

  /** Current data plugin being used. */
  private DataPlugin currentDataplugin;
  /** Current visual plugin being used. */
  private VisualPlugin currentVisualplugin;

  /**
   * Sole constructor for the framework.
   */
  public MapperFramework() {
    dataplugins = new ArrayList<>();
    visualplugins = new ArrayList<>();
    datasets = new ArrayList<>();
  }

  /**
   * Registers framework listener.
   * @param listenerL to register
   */
  public void registerFrameworkListener(final MapperListener listenerL) {
    listener = listenerL;
  }

  /**
   * Registers a data plugin.
   * @param plugin to register
   */
  public void registerDataPlugin(final DataPlugin plugin) {
    dataplugins.add(plugin);
  }

  /**
   * Registers a visual plugin.
   * @param plugin to register
   */
  public void registerVisualPlugin(final VisualPlugin plugin) {
    visualplugins.add(plugin);
  }

  /**
   * Chooses a data plugin.
   * @param name of plugin
   */
  public void chooseDataPlugin(final String name) {
    for (DataPlugin dataplugin : dataplugins) {
      if (name.equals(dataplugin.toString())) {
        currentDataplugin = dataplugin;
      }
    }
  }

  /**
   * Chooses a visual plugin.
   * @param name of plugin
   */
  public void chooseVisualPlugin(final String name) {
    for (VisualPlugin visualplugin : visualplugins) {
      if (name.equals(visualplugin.toString())) {
        currentVisualplugin = visualplugin;
      }
    }
  }


  /**
   * Starts taking in data from the chosen data plugin.
   * @param name for the dataset
   * @param subject the who
   * @param source of data
   */
  public void enterDataSet(final String name, final String subject,
                         final String source) {
    DataSet newDataSet = new DataSet(name);
    currentDataplugin.setSubject(subject);

    currentDataplugin.openConnection(source);
    while (currentDataplugin.hasNext()) {
      ClientEvent clientEvent = currentDataplugin.getEvent();
      newDataSet.addEvent(clientEvent);
    }
    currentDataplugin.closeConnection();

    datasets.add(newDataSet);
  }

  /**
   * Returns array of the names of datasets.
   * @return array of the names of datasets
   */
  public String[] datasets() {
    String[] availableDatasets = new String[datasets.size()];
    for (int i = 0; i < datasets.size(); i++) {
      availableDatasets[i] = datasets.get(i).toString();
    }
    return availableDatasets;
  }

  /**
   * Removes dataset with given name from the framework.
   * @param name of dataset
   */
  public void removeDataset(final String name) {
    for (DataSet dataset : datasets) {
      if (datasets.toString().equals(name)) {
        datasets.remove(dataset);
        listener.updateDatasets();
      }
    }
  }
}
