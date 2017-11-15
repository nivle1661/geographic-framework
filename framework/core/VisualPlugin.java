package core;

import core.datapoint.DataSet;

import javax.swing.JFrame;

/** Common interface for visual plugins. */
public interface VisualPlugin {
  void initFigure();
  void addData(DataSet set);
  JFrame getFinalFigure();
}
