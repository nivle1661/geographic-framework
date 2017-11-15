package core;

import core.datapoint.DataSet;

import javax.imageio.ImageReader;

/** Common interface for visual plugins. */
public interface VisualPlugin {
  void initFigure(DataSet set);
  void addData();
  ImageReader getFinalFigure();
}
