package core.datapoint;

import core.ClientEvent;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Internal representation of an event by the Framework.
 */
public class Event implements Comparable<Event> {
  /** The who of the event. */
  public final String subject;

  /** Time of the event. */
  public final Date time;

  /** Location of the event as a string. */
  public final String location;

  /** Latitude of the event. */
  public final double latitude;
  /** Longitude of the event. */
  public final double longitude;

  /** Keywords of the event. */
  public final List<String> keywords;

  /** How many people attended (OPTIONAL). */
  public final int quantity;
  /** The priority of the event (OPTIONAL). */
  private int priority;

  /**
   * Gets the latitude and longitude positions of an address using google maps API
   * @param address
   * @return latitude and longitude
   * @throws Exception
   */
  public static String[] getLatLongPositions(String address) throws Exception
  {
    int responseCode = 0;
    String api = "http://maps.googleapis.com/maps/api/geocode/xml?address="
            + URLEncoder.encode(address, "UTF-8") + "&sensor=true";
    System.out.println("URL : "+api);
    URL url = new URL(api);
    HttpURLConnection httpConnection = (HttpURLConnection)url.openConnection();
    httpConnection.connect();
    responseCode = httpConnection.getResponseCode();
    if(responseCode == 200)
    {
      DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();;
      Document document = builder.parse(httpConnection.getInputStream());
      XPathFactory xPathfactory = XPathFactory.newInstance();
      XPath xpath = xPathfactory.newXPath();
      XPathExpression expr = xpath.compile("/GeocodeResponse/status");
      String status = (String)expr.evaluate(document, XPathConstants.STRING);
      if(status.equals("OK"))
      {
        expr = xpath.compile("//geometry/location/lat");
        String latitude = (String)expr.evaluate(document, XPathConstants.STRING);
        expr = xpath.compile("//geometry/location/lng");
        String longitude = (String)expr.evaluate(document, XPathConstants.STRING);
        return new String[] {latitude, longitude};
      } else {
        throw new Exception("Error from the API - response status: " + status);
      }
    }
    return null;
  }

  /**
   * Sole constructor for Event.
   * Accepts time only as MM/DD/YYYY format currently. Will change to accept
   * month day, year.
   */
  public Event(final ClientEvent event) {
    keywords = event.keywords;
    location = event.location;
    subject = event.subject;
    quantity = event.quantity;

    String[] latlong = null;
    try {
      latlong = getLatLongPositions(location);
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (latlong == null) {
      latitude = 500;
      longitude = 500;
    } else {
      latitude = Double.parseDouble(latlong[0]);
      longitude = Double.parseDouble(latlong[1]);
    }

    String[] temp = event.date.split("/");
    int year = Integer.parseInt(temp[2]);
    int month = Integer.parseInt(temp[0]);
    int day = Integer.parseInt(temp[1]);

    Calendar c = Calendar.getInstance();
    c.set(year, month, day, 12, 0);
    time = c.getTime();
  }

  /**
   * Sets the priority of the event.
   * @param priority of event
   */
  public void setPriority(int priority) {
    this.priority = priority;
  }

  /**
   * Gets the priority of the event.
   * @return priority of event
   */
  public int getPriority() {
    return priority;
  }

  /**
   * Compares two events based on time
   * @param o other event
   * @return comparison of times.
   */
  @Override
  public int compareTo(Event o) {
    return time.compareTo(o.time);
  }
}
