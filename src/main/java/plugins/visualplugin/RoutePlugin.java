package plugins.visualplugin;

import framework.core.VisualPlugin;
import framework.core.datapoint.DataSet;
import framework.core.datapoint.Event;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/** Visualization plugin for optimal routes. */
public class RoutePlugin implements VisualPlugin {
  /** Google Static Maps API key. */
  private String apikey = "AIzaSyDersbd0-W5Js7hBKIPKOyQE5rKQ7hr4rA";
  /** JFrame containing visualization. */
  private JFrame result;

  /** Speeds to visualize for optimal route. */
  private final int[] speeds = new int[]{20, 60, 500};
  /** Colors of the routes. */
  private final String[] colors = new String[]{"red", "blue", "green"};

  /**
   * Does nothing since we rely on the data to get the map.
   */
  @Override
  public void initFigure() {

  }

  /**
   * Gets map with optimal routes via Google Static Maps API.
   * @param set of data to be visualized
   */
  @Override
  public void addData(final DataSet set) {
    StringBuilder api = new StringBuilder(
            "https://maps.googleapis.com/maps/api/staticmap?"
                    + "size=640x640");

    for (int i = 0; i < speeds.length; i++) {
      api.append("&path=color:").append(colors[i])
              .append("|weight:4");

      List<Event> route = set.getOptimalRoute(speeds[i]);
      for (Event event : route) {
        api.append("|").append(String.format("%.4f", event.getLatitude()))
           .append(",").append(String.format("%.4f", event.getLongitude()));
      }
    }
    api.append("&key=").append(apikey);
    System.out.println(api);
    System.out.println(api.length());

    try {
      URL url = new URL(api.toString());
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod("GET");
      int responseCode = con.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) {
        Image image = ImageIO.read(con.getInputStream());
        ImageIcon imageIcon = new ImageIcon(image);

        JLabel label = new JLabel("", imageIcon, JLabel.CENTER);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(label, BorderLayout.CENTER);

        result = new JFrame();
        result.setLayout(new BorderLayout());
        JTextArea header = new JTextArea(" This visualization displays the"
          + " optimal path based on priority of events.\n"
          + " " + colors[0] + ": " + speeds[0] + "mph\n"
          + " " + colors[1] + ": " + speeds[1] + "mph\n"
          + " " + colors[2] + ": " + speeds[2] + "mph");
        header.setEditable(false);
        header.setBackground(Color.lightGray);
        result.add(header, BorderLayout.NORTH);
        result.add(panel, BorderLayout.CENTER);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Returns final visualization.
   * @return JFrame containing final visualization
   */
  @Override
  public JFrame getFinalFigure() {
    return result;
  }

  /**
   * Returns string representation.
   * @return string representation
   */
  @Override
  public String toString() {
    return "Route finder";
  }
}
