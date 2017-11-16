package visualplugin;

import core.VisualPlugin;
import core.datapoint.DataSet;


import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.style.Styler;


import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *  Implements a Visual Plugin allowing keyword frequencies to be
 *  displayed in the form of a bar graph.
 */
public class BarChartPlugin implements VisualPlugin {
    /**
     * Width of the JFrame window.
     */
    private static final int WIDTH = 1200;
    /**
     * Height of the JFrame window.
     */
    private static final int HEIGHT = 1200;
    /**
     * Rotation required in the labels of the x-axis for visibility.
     */
    private static final int ROTATION = 45;
    /**
     * Holds the chart to returned, Chart is generated using a library.
     */
    private final CategoryChart chart = new CategoryChartBuilder().width(WIDTH).
            height(HEIGHT).title("Bar Chart").xAxisTitle("Keywords").
            yAxisTitle("Number").build();

    /**
     * Initializes the parameters required for the generation of a chart.
     */
    @Override
    public void initFigure() {
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        chart.getStyler().setHasAnnotations(true);
        chart.getStyler().setXAxisLabelRotation(ROTATION);
    }

    /**
     * adds the data to the bar graph by extracted required data
     * from dataset.
     * @param set of data to be visualized
     */
    @Override
    public void addData(final DataSet set) {
        Map<String, Integer> frequencyTable = set.getFreqKeyword();
        int size = frequencyTable.size();
        List<String> keywords = new ArrayList<>(size);
        List<Integer> frequency = new ArrayList<>(size);
        int i = 0;
        for (String keyword : frequencyTable.keySet()) {
            if (!keyword.equals("")) { //to clean data
                keywords.add(i, keyword);
                frequency.add(frequencyTable.get(keyword));
                i++;
            }
        }
        chart.addSeries("Frequencies", keywords, frequency);
    }

    /**
     * creates a JFrame with the bar graph in it.
     * @return frame - frame containing the required bar graph
     */
    @Override
    public JFrame getFinalFigure() {
        // Create and set up the window.
        JFrame frame = new JFrame("Bar Graph");
        frame.setLayout(new BorderLayout());

        // chart
        JPanel chartPanel = new XChartPanel<>(chart);
        frame.add(chartPanel, BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);
        return frame;
    }

    /**
     * updates the string representation of the class.
     * @return - correct name
     */
    @Override
    public String toString() {
        return "Bar Chart Visualizer";
    }
}


