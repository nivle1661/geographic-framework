package main.java;


import edu.cmu.cs.cs214.hw5.framework.core.MusicData;
import edu.cmu.cs.cs214.hw5.framework.core.Visualizer;
import org.knowm.xchart.QuickChart;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * A visualizer that allows you to view the amplitudes of the whole song at once, the amplitude
 * of every frame is displayed using a XYChart. It allows you to get a visual sense of the loudness
 * of the song as it progresses
 */
public class StaticAmplitudeVisualizer extends JFrame implements Visualizer {

    private MusicData song;
    private double[] amplitudes;
    private SwingWrapper<org.knowm.xchart.XYChart> sw;
    private JFrame display;
    private boolean stopped;

    @Override
    public void load(MusicData song) {
        this.song=song;
    }

    @Override
    public void start() {
        amplitudes=song.getAllAmplitudes();
        double[] xData= new double[amplitudes.length];
        for(int i=0;i<amplitudes.length;i++){
            xData[i]=i;
        }
        XYChart chart = QuickChart.getChart("Amplitude Chart", "Frames", "Amplitudes",
                "Amplitudes", xData, amplitudes);
        // Show the chart
        sw = new SwingWrapper<>(chart);
        Thread t = new Thread(()-> {
            display = sw.displayChart();
            display.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            display.addWindowListener(new WindowAdapter() { //Checking when window closed
                @Override
                public void windowClosed(WindowEvent e) {
                    stopped = true;
                }
            });
        });
        t.start();
    }

    @Override
    public void drawNextFrame() {

    }

    @Override
    public void close() {//Closing of window
        display.dispose();
    }

    @Override
    public boolean isStopped() {
        return stopped;
    }

    @Override
    public String toString() { return "Static Bar Amplitude Visualizer"; }

}
