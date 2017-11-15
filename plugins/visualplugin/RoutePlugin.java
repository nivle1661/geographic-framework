package visualplugin;

import core.VisualPlugin;
import core.datapoint.DataSet;
import core.datapoint.Event;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Image;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class RoutePlugin implements VisualPlugin {
  private String api_key = "AIzaSyDersbd0-W5Js7hBKIPKOyQE5rKQ7hr4rA";
  private JFrame result;

  private final double[] speeds = new double[]{20, 60, 500};
  private final String[] colors = new String[]{"red", "blue", "yellow"};

  @Override
  public void initFigure() {

  }

  @Override
  public void addData(final DataSet set) {
    StringBuilder api = new StringBuilder(
            "https://maps.googleapis.com/maps/api/staticmap?"
                    + "size=640x640");

    for (int i = 0; i < speeds.length; i++) {
      api.append("&path=color:").append(colors[i])
              .append("|weight:2");

      List<Event> route = set.getOptimalRoute(speeds[i]);
      for (Event event : route) {
        api.append("|").append(String.format("%.4f", event.latitude))
                .append(",").append(String.format("%.4f", event.longitude));
      }
    }
    api.append("&key=").append(api_key);
    System.out.println(api);
    System.out.println(api.length());

    try {
      URL url = new URL(api.toString());
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod("GET");
      int responseCode = con.getResponseCode();
      System.out.println("GET Response Code :: " + responseCode);
      if (responseCode == HttpURLConnection.HTTP_OK) {
        Image image = ImageIO.read(con.getInputStream());
        ImageIcon imageIcon = new ImageIcon(image);

        JLabel label = new JLabel("", imageIcon, JLabel.CENTER);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(label, BorderLayout.CENTER);

        result = new JFrame();
        result.add(panel);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public JFrame getFinalFigure() {
    result.pack();
    result.setVisible(true);
    return result;
  }
}
