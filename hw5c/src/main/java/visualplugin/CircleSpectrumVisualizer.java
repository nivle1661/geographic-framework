package visualplugin;

import edu.cmu.cs.cs214.hw5.framework.core.BucketCount;
import edu.cmu.cs.cs214.hw5.framework.core.MusicData;
import edu.cmu.cs.cs214.hw5.framework.core.Visualizer;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.awt.Color;
import java.awt.Dimension;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.PolarChartPanel;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.chart.renderer.DefaultPolarItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class CircleSpectrumVisualizer implements Visualizer {
  private JFrame frame;
  private MusicData song;
  private ChartPanel panel;
  private JFreeChart chart;
  private XYSeriesCollection set;
  private boolean stopped;
  private double[] frequencies;
  private long frameCount;

  private final double degrees = 7.20;

  private String title;

  @Override
  public void load(MusicData song) {
    this.song = song;
    title = song.toString();
    frequencies = song.getFirstFrequency(BucketCount.FIFTY);
  }

  @Override
  public void start() {
    stopped = false;
    frameCount = 0;

    frame = new JFrame();
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent e) {
        stopped = true;
      }
    });
    frame.setSize(1000, 1000);

    set = (XYSeriesCollection) createDataset();
    chart = ChartFactory.createPolarChart("", set, false, true, false);
    PolarPlot plot = (PolarPlot) chart.getPlot();
    plot.setRadiusGridlinesVisible(false);
    plot.setAngleOffset(360.0/50);

    plot.setBackgroundPaint(Color.WHITE);
    plot.setAngleGridlinePaint(Color.BLACK);
    plot.setRadiusGridlinePaint(Color.LIGHT_GRAY);

    NumberAxis rangeAxis = (NumberAxis) plot.getAxis();
    rangeAxis.setTickLabelsVisible(false);

    final DefaultPolarItemRenderer renderer = (DefaultPolarItemRenderer) plot.getRenderer();

    for (int i = 0; i < 50; i++) {
      renderer.setSeriesFilled(i, true);
      renderer.setSeriesPaint(i,
              Color.getHSBColor((float) i / (float) 50, 0.85f, 1.0f));
    }

    panel = new PolarChartPanel(chart);
    panel.setPreferredSize(new Dimension(900, 900));
    frame.add(panel);
    frame.setVisible(true);
    frame.pack();
  }

  private XYDataset createDataset() {
    set = new XYSeriesCollection();
    for (int i = 0; i < 50; i++) {
      XYSeries temp = new XYSeries(i);
      temp.add(degrees * i, 0.1);
      temp.add(degrees * i + 1, 1000 * frequencies[i]);
      temp.add(degrees * (i+1) - 1, 1000 * frequencies[i]);

      set.addSeries(temp);
    }
    return set;
  }

  @Override
  public void drawNextFrame() {
    if (!song.frequenciesDone())
      frequencies = song.getNextFrequency();
    else stopped = true;
    frameCount++;

    if (frameCount % 3 == 0) {
      SwingUtilities.invokeLater(() -> {
        // updates the Points' positions and adds new Points
        ((PolarPlot) chart.getPlot()).setDataset(createDataset());
      });
    }
  }

  @Override
  public void close() {
    frame.dispose();
  }

  @Override
  public boolean isStopped() {
    return stopped;
  }

  @Override
  public String toString() {
    return "Spectrum Visualizer";
  }
}
