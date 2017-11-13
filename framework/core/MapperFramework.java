package core;

import core.datapoint.DataSet;
import core.datapoint.Event;
import gui.MapperGui;

import java.util.ArrayList;
import java.util.List;

public class MapperFramework {
  private MapperListener listener;
  private List<DataPlugin> dataplugins;
  private List<VisualPlugin> visualplugins;
  private List<DataSet> datasets;

  private DataPlugin currentDataplugin;
  private VisualPlugin currentVisualplugin;

  public MapperFramework() {
    listener = new MapperGui();
    dataplugins = new ArrayList<>();
    visualplugins = new ArrayList<>();
    datasets = new ArrayList<>();
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
   * @name of plugin
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
   * @name of plugin
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
   * @param source of data
   */
  public void getDataSet(final String name, final String subject,
                         final String source) {
    DataSet newDataSet = new DataSet(name);
    currentDataplugin.setSubject(subject);

    currentDataplugin.openConnection(source);
    while(currentDataplugin.hasNext()) {
      ClientEvent clientEvent = currentDataplugin.getEvent();
      newDataSet.addEvent(clientEvent);
    }
    currentDataplugin.closeConnection();
  }
}
