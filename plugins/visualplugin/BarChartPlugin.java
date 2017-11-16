package visualplugin;

import core.VisualPlugin;
import core.datapoint.DataSet;

import javax.swing.*;

import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.demo.charts.ExampleChart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class BarChartPlugin implements VisualPlugin{

    private CategoryChart chart;
    @Override
    public void initFigure() {
    }

    @Override
    public void addData(DataSet set) {
        MyChart myChart=new MyChart();
        myChart.setFrequencyTable(set.getFreqKeyword());
        chart=myChart.getChart();
    }

    @Override
    public JFrame getFinalFigure() {
        if(chart!=null){
            try {
                BitmapEncoder.saveBitmap(chart, "./Sample_Chart", BitmapEncoder.BitmapFormat.PNG);
            }catch (IOException e){}
            JFrame result=new SwingWrapper<>(chart).displayChart();
            //result.pack();
            //result.setVisible(true);
            return result;
        }
        return null;
    }

    @Override
    public String toString() {
        return "Bar Chart Visualizer";
    }

    private class MyChart implements ExampleChart<CategoryChart>{

        private Map<String,Integer> frequencyTable;

        public void setFrequencyTable(Map<String, Integer> frequencyTable) {
            this.frequencyTable = frequencyTable;
        }

        @Override
        public CategoryChart getChart() {
            CategoryChart chart1=new CategoryChartBuilder().width(800).height(600).title("Bar Chart")
                    .xAxisTitle("Keywords").yAxisTitle("Number").build();
            chart1.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
            chart1.getStyler().setHasAnnotations(true);
            int size= frequencyTable.size();
            List<String> keywords=new ArrayList<>(size);
            List<Integer> frequency=new ArrayList<>(size);
            int i=0;
            for(String keyword : frequencyTable.keySet()){
                keywords.add(i,keyword);
                frequency.add(frequencyTable.get(keyword));
                i++;
            }

            chart1.addSeries("Frequencies", keywords,frequency);
            return chart1;
        }
    }


}


