package gui;

import core.DataPlugin;
import core.MapperFramework;
import core.MapperListener;
import core.VisualPlugin;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

public class MapperGui extends JPanel implements MapperListener {
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
  private final int width = 1250;
  /** Height of the Frame. */
  private final int height = 950;

  /** The parent JFrame window. */
  private final JFrame frame;
  /** The actual body of the game. */
  private JPanel body;
  /** Menu bar. */
  private JMenuBar menuBar;

  /**
   * Constructor for the GUI.
   */
  public MapperGui() {
    frame = new JFrame("Mapper");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setPreferredSize(new Dimension(width, height));

    // Set-up the menu bar.
    JMenu fileMenu = new JMenu(MENU_TITLE);
    fileMenu.setMnemonic(KeyEvent.VK_F);

    initializeMenu();
    body = new JPanel();

    frame.pack();
    frame.setVisible(true);
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
    newDatasetMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(final ActionEvent event) {
        String[] typeValues = {"Letter tile", "Special tile"};
        String name = (String) JOptionPane.showInputDialog(awesome,
                "Select type of tile to place", "Placing tile...",
                JOptionPane.QUESTION_MESSAGE, null, typeValues,
                typeValues[0]);
      }
    });
    menu.add(newDatasetMenuItem);

    removeDatasetMenuItem = new JMenuItem(MENU_REMOVE_DATASET);
    removeDatasetMenuItem.setMnemonic(KeyEvent.VK_N);
    removeDatasetMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(final ActionEvent event) {

      }
    });
    menu.add(removeDatasetMenuItem);

    visualizeDatasetMenuItem = new JMenuItem(MENU_VISUALIZE_DATASET);
    visualizeDatasetMenuItem.setMnemonic(KeyEvent.VK_N);
    visualizeDatasetMenuItem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(final ActionEvent event) {

      }
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
}
