package framework.core;

import framework.core.datapoint.DataSet;

import javax.swing.JFrame;

/** Common interface for visual plugins. */
public interface VisualPlugin {
  /** Creates a base figure for visualization (e.g. axes for a bar graph. */
  void initFigure();

  /**
   * Adds the data to the initial figure.
   * @param set of data to be visualized
   */
  void addData(DataSet set);

  /**
   * Returns a JFrame containing the visualization.
   * @return JFrame containing the visualization
   */
  JFrame getFinalFigure();
}
