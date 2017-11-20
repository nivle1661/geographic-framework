package visualplugin;

import edu.cmu.cs.cs214.hw5.framework.core.BucketCount;
import edu.cmu.cs.cs214.hw5.framework.core.MusicData;
import edu.cmu.cs.cs214.hw5.framework.core.Visualizer;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.util.Iterator;
import java.util.List;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.chart.renderer.DefaultPolarItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class CircleSpectrumVisualizer implements Visualizer {
  private JFrame frame;
  private MusicData song;
  ChartPanel panel;
  private boolean stopped;
  private double[] frequencies;
  private long frameCount;

  private final double degrees = 3.60;

  private String title;

  @Override
  public void load(MusicData song) {
    this.song = song;
    title = song.toString();
  }

  @Override
  public void start() {
    stopped = false;
    frequencies = song.getFirstFrequency(BucketCount.ONE_HUNDRED);
    frameCount = 0;

    frame = new JFrame();
    frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent e) {
        stopped = true;
      }
    });
    frame.setSize(800, 800);
    panel = new ChartPanel(createChart(createDataset()));
    panel.setPreferredSize(new Dimension(500, 500));
    frame.add(panel);
    frame.setVisible(true);
    frame.pack();
  }

  private XYDataset createDataset() {
    XYSeriesCollection result = new XYSeriesCollection();
    XYSeries series = new XYSeries("Frequency");
    for (int i = 0; i < 100; i++) {
      series.add(degrees*i, frequencies[0]);
      series.add(degrees*(i+1), frequencies[0]);
    }
    result.addSeries(series);
    return result;
  }

  private JFreeChart createChart(final XYDataset dataset) {
    JFreeChart chart = ChartFactory.createPolarChart(
            title, dataset, true, false, false);
    PolarPlot plot = (PolarPlot) chart.getPlot();
    plot.setBackgroundPaint(Color.white);
    plot.setAngleGridlinePaint(Color.black);
    plot.setRadiusGridlinePaint(Color.lightGray);
    DefaultPolarItemRenderer r = (DefaultPolarItemRenderer) plot.getRenderer();
    r.setFillComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
    for (int i = 0; i < dataset.getSeriesCount(); i++ ) {
      r.setSeriesFilled(i, true);
      r.setShapesVisible(false);
      r.setDrawOutlineWhenFilled(false);
    }
    NumberAxis rangeAxis = (NumberAxis) plot.getAxis();
    rangeAxis.setTickLabelsVisible(false);
    return chart;
  }

  @Override
  public void drawNextFrame() {
    if (!song.frequenciesDone()) frequencies = song.getNextFrequency();
    else stopped = true;
    frameCount++;

    SwingUtilities.invokeLater(() -> {
      // updates the Points' positions and adds new Points
      panel.removeAll();
      panel.add(new ChartPanel(createChart(createDataset())));
    });
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
