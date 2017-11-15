import core.DataPlugin;
import core.MapperFramework;
import core.VisualPlugin;
import core.datapoint.DataSet;
import dataplugin.CSVPlugin;
import gui.MapperGui;
import visualplugin.RoutePlugin;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

/** Main class for framework. */
public final class Main {
  /**
   * Default constructor, never called.
   */
  private Main() {
  }

  /**
   * The main function which runs the framework.
   * @param args command arguments.
   */
  public static void main(final String[] args) {
    /**
    try {
      String[] latlong = Event.getLatLongPositions("New York City");
      System.out.println(latlong[0] + " " + latlong[1]);
    } catch (Exception e) {
      System.out.println("what");
      e.printStackTrace();
    }
    */

    SwingUtilities.invokeLater(() -> {
      createAndShowFramework();
    });
  }

  /**
   * Creates the framework GUI and setup.
   */
  private static void createAndShowFramework() {
    JFrame frame = new JFrame("Mapper");
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    List<DataPlugin> dataplugins = loadDataPlugins();
    List<VisualPlugin> visualplugins = loadVisualPlugins();

    MapperFramework test = new MapperFramework();
    test.registerDataPlugin(new CSVPlugin());
    test.chooseDataPlugin(new CSVPlugin().toString());
    DataSet t1 = test.enterDataSet("Dataset 1", "ExpertVagabond", "ExpertVagabond.csv");
    DataSet t2 = test.enterDataSet("Dataset 2", "Kristenalana", "Kristenalana.csv");
    DataSet t3 = test.enterDataSet("Dataset 3", "TravisBurke", "TravisBurkePhotography.csv");
    List<DataSet> tester = new ArrayList<>();
    tester.add(t1);
    tester.add(t2);
    tester.add(t3);
    RoutePlugin k = new RoutePlugin();
    try {
      k.initFigure(DataSet.combineDatasets(tester));
    } catch (Exception e) {
      e.printStackTrace();
    }

    frame.add(new MapperGui(dataplugins, visualplugins));
  }

  /**
   * Load plugins listed in META-INF/services/...
   *
   * @return List of instantiated plugins
   */
  private static List<DataPlugin> loadDataPlugins() {
    List<DataPlugin> result = new ArrayList<>();
    Iterator<DataPlugin> plugins = ServiceLoader.load(DataPlugin.class).
            iterator();
    while (plugins.hasNext()) {
      DataPlugin plugin = plugins.next();
      result.add(plugin);
      System.out.println("Loaded plugin " + plugin.toString());
    }
    return result;
  }

  /**
   * Load plugins listed in META-INF/services/...
   *
   * @return List of instantiated plugins
   */
  private static List<VisualPlugin> loadVisualPlugins() {
    List<VisualPlugin> result = new ArrayList<>();
    Iterator<VisualPlugin> plugins = ServiceLoader.load(VisualPlugin.class).
            iterator();
    while (plugins.hasNext()) {
      VisualPlugin plugin = plugins.next();
      result.add(plugin);
      System.out.println("Loaded plugin " + plugin.toString());
    }
    return result;
  }
}
