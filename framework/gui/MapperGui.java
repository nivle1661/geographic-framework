package gui;

import core.DataPlugin;
import core.MapperFramework;
import core.VisualPlugin;
import core.datapoint.DataSet;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * GUI for the Mapper Framework.
 */
public class MapperGui extends JPanel {
  /** Registered data plugins. */
  private List<DataPlugin> dataplugins;
  /** Registered visual plugins. */
  private List<VisualPlugin> visualplugins;

  /** The game implementation. */
  private MapperFramework framework;

  /** Menu title. */
  private static final String MENU_TITLE = "Dataset Menu";
  /** New dataset title. */
  private static final String MENU_NEW_DATASET = "New Dataset";
  /** Remove dataset title. */
  private static final String MENU_REMOVE_DATASET = "Remove Dataset";
  /** Visualize dataset title. */
  private static final String MENU_VISUALIZE_DATASET = "Visualize Datasets";
  /** Exit title. */
  private static final String MENU_EXIT = "Exit";

  /** New game menu. */
  private static JMenuItem newDatasetMenuItem;
  /** End game menu. */
  private static JMenuItem removeDatasetMenuItem;
  /** Add player menu. */
  private static JMenuItem visualizeDatasetMenuItem;

  /** Width of the Frame. */
  private final int width = 1000;
  /** Height of the Frame. */
  private final int height = 800;

  /** The parent JFrame window. */
  private final JFrame frame;
  /** The actual body of the game. */
  private JPanel body;
  /** Menu bar. */
  private JMenuBar menuBar;

  /** Index of dataset. */
  private int index;

  /**
   * Constructor for the GUI.
   * @param datapluginsL default data plugins
   * @param visualpluginsL default visual plugins
   */
  public MapperGui(final List<DataPlugin> datapluginsL,
                   final List<VisualPlugin> visualpluginsL) {
    frame = new JFrame("Mapper");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setPreferredSize(new Dimension(width, height));

    framework = new MapperFramework();
    framework.registerFrameworkListener(this);

    dataplugins = datapluginsL;
    visualplugins = visualpluginsL;
    for (DataPlugin plugin : dataplugins) {
      framework.registerDataPlugin(plugin);
    }
    for (VisualPlugin plugin : visualplugins) {
      framework.registerVisualPlugin(plugin);
    }

    // Set-up the menu bar.
    JMenu fileMenu = new JMenu(MENU_TITLE);
    fileMenu.setMnemonic(KeyEvent.VK_F);

    initializeMenu();
    body = new JPanel();
    body.setVisible(true);
    frame.add(body);

    frame.pack();
    frame.setVisible(true);

    index = 1;
  }

  /**
   * Initializes menu buttons for menu bar.
   */
  private void initializeMenu() {
    menuBar = new JMenuBar();
    JMenu menu = new JMenu(MENU_TITLE);
    JPanel awesome = this;

    newDatasetMenuItem = new JMenuItem(MENU_NEW_DATASET);
    newDatasetMenuItem.setMnemonic(KeyEvent.VK_N);
    newDatasetMenuItem.addActionListener(event -> {
      String[] availablePlugins = new String[dataplugins.size()];
      for (int i = 0; i < dataplugins.size(); i++) {
        availablePlugins[i] = dataplugins.get(i).toString();
      }
      String plugin = (String) JOptionPane.showInputDialog(awesome,
              "Select a data plugin", "Taking data...",
              JOptionPane.QUESTION_MESSAGE, null, availablePlugins,
              availablePlugins[0]);

      if (plugin != null) {
        framework.chooseDataPlugin(plugin);
        JTextField getName = new JTextField();
        JTextField getSubject = new JTextField();
        JTextField getSource = new JTextField();
        Object[] message = {
                "Name:", getName,
                "Subject:", getSubject,
                "Source:", getSource
        };
        int option = JOptionPane.showConfirmDialog(awesome,
                message, "Using " + plugin,
                JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
          try {
            String name = getName.getText();
            String subject = getSubject.getText();
            String source = getSource.getText();
            framework.enterDataSet(name, subject, source);

            boolean increment = false;
            if (name.equals("")) {
              name = "Dataset " + index;
              increment = true;
            }
            if (subject.equals("")) {
              subject = "Subject " + index;
              increment = true;
            }
            if (increment) {
              index++;
            }
          } catch (Exception e) {
            JOptionPane.showMessageDialog(awesome, "Invalid source");
          }
        }
      }
    });
    menu.add(newDatasetMenuItem);

    removeDatasetMenuItem = new JMenuItem(MENU_REMOVE_DATASET);
    removeDatasetMenuItem.setMnemonic(KeyEvent.VK_N);
    removeDatasetMenuItem.addActionListener(event -> {
      String[] availableDatasets = framework.datasets();
      if (availableDatasets.length == 0) {
        JOptionPane.showMessageDialog(new JFrame(), "No datasets to remove!",
                "Error", JOptionPane.ERROR_MESSAGE);
      }
      String name = (String) JOptionPane.showInputDialog(awesome,
              "Remove a data plugin", "Removing data...",
              JOptionPane.QUESTION_MESSAGE, null, availableDatasets,
              availableDatasets[0]);
      if (name != null) {
        framework.removeDataset(name);
      }
    });
    menu.add(removeDatasetMenuItem);

    visualizeDatasetMenuItem = new JMenuItem(MENU_VISUALIZE_DATASET);
    visualizeDatasetMenuItem.setMnemonic(KeyEvent.VK_N);
    visualizeDatasetMenuItem.addActionListener(event -> {
      return;
    });
    menu.add(visualizeDatasetMenuItem);

    menu.addSeparator();
    JMenuItem exitMenuItem = new JMenuItem(MENU_EXIT);
    exitMenuItem.setMnemonic(KeyEvent.VK_X);
    exitMenuItem.addActionListener(event -> System.exit(0));
    menu.add(exitMenuItem);

    menuBar.add(menu);
    frame.setJMenuBar(menuBar);
  }

  /**
   * Updates the remove dataset listener.
   */
  public void updateDatasets() {
    JPanel awesome = this;
    removeDatasetMenuItem.removeActionListener(
            removeDatasetMenuItem.getActionListeners()[0]);
    removeDatasetMenuItem.addActionListener(event -> {
      String[] availableDatasets = framework.datasets();
      if (availableDatasets.length == 0) {
        JOptionPane.showMessageDialog(new JFrame(), "No datasets to remove!",
                "Error", JOptionPane.ERROR_MESSAGE);
      }
      String name = (String) JOptionPane.showInputDialog(awesome,
              "Remove a data plugin", "Removing data...",
              JOptionPane.QUESTION_MESSAGE, null, availableDatasets,
              availableDatasets[0]);
      if (name != null) {
        framework.removeDataset(name);
      }
    });

    visualizeDatasetMenuItem.removeActionListener(
            visualizeDatasetMenuItem.getActionListeners()[0]);
    visualizeDatasetMenuItem.addActionListener(event -> {
      String[] availablePlugins = new String[visualplugins.size()];

      for (int i = 0; i < visualplugins.size(); i++) {
        availablePlugins[i] = visualplugins.get(i).toString();
      }
      String plugin = (String) JOptionPane.showInputDialog(awesome,
              "Select a data plugin", "Taking data...",
              JOptionPane.QUESTION_MESSAGE, null, availablePlugins,
              availablePlugins[0]);

      if (plugin != null) {
        framework.chooseVisualPlugin(plugin);

        JTextField[] datasets = new JTextField[framework.datasets().length];
        Object[] message = new Object[datasets.length * 2 + 1];
        JTextArea header = new JTextArea(
            "Assign priorities to the datasets used.\n"
          + "For unused datasets, leave blank.\n"
          + "If there is no preference or nonapplicable, assign 0 as default.");
        header.setEditable(false);
        message[0] = header;

        for (int i = 0; i < datasets.length; i++) {
          message[i * 2 + 1] = framework.datasets()[i] + "'s priority: ";

          datasets[i] = new JTextField();
          message[i * 2 + 2] = datasets[i];
        }
        int option = JOptionPane.showConfirmDialog(awesome,
                message, "Using " + plugin,
                JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
          List<Integer> updates = new ArrayList<>();
          for (int i = 0; i < datasets.length; i++) {
            String prior = datasets[i].getText();
            if (!prior.equals("")) {
              int priority = Integer.parseInt(prior);
              updates.add(i);
              updates.add(priority);
            }
          }

          if (updates.size() == 0) {
            JOptionPane.showMessageDialog(new JFrame(),
                    "No datasets selected!", "Error",
                    JOptionPane.ERROR_MESSAGE);
          } else {
            DataSet result = framework.updateAndCombine(updates);
            JFrame frameL = framework.visualizeDataSet(result);
            frameL.pack();
            frameL.setVisible(true);
          }
        }
      }
    });
  }
}
