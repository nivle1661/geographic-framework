package tests;

import core.DataPlugin;
import core.MapperFramework;
import core.datapoint.DataSet;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/** Testing our framework. */
public class FrameworkTest {
  //CHECKSTYLE:OFF
  @Test
  public void testPermutation() {
    MapperFramework f = new MapperFramework();
    List<DataPlugin> plugins = loadDataPlugins();
    Assert.assertEquals(plugins.size(), 3);
    for (DataPlugin plugin : plugins) {
      f.registerDataPlugin(plugin);
    }

    f.chooseDataPlugin("Spreadsheet loader");
    f.enterDataSet("Dataset 1", "ExpertVagabond", "ExpertVagabond.csv");
    f.enterDataSet("Dataset 2", "Kristenalana", "Kristenalana.csv");
    Assert.assertEquals(f.datasets().length, 2);

    f.chooseDataPlugin("JSON loader");
    f.enterDataSet("Dataset 3", "SoccerBeast", "soccerBeast.json");
    f.enterDataSet("Dataset 4", "EventBeast", "eeventBeast.json");

    f.chooseDataPlugin("Set List Loader");
    f.enterDataSet("Dataset 5", "Kendrick Lamar",
            "381086ea-f511-4aba-bdf9-71c753dc5077");
    Assert.assertEquals(f.datasets().length, 5);

    f.removeDataset("Dataset 3");
    Assert.assertEquals(f.datasets().length, 4);
    f.removeDataset("Dataset 6");
    Assert.assertEquals(f.datasets().length, 4);

    List<DataSet> datasets = f.getDatasets();
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
}
