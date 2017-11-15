package core.datapoint;

import core.ClientEvent;
import org.w3c.dom.Document;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
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

  /** Boundary of South Latitude of the event. */
  public final double southLatitude;
  /** Boundary of North Latitude of the event. */
  public final double northLatitude;
  /** Boundary of West Longitude of the event. */
  public final double westLongitude;
  /** Boundary of East Longitude of the event. */
  public final double eastLongitude;

  /** Keywords of the event. */
  public final List<String> keywords;

  /** How many people attended (OPTIONAL). */
  public final int quantity;
  /** The priority of the event (OPTIONAL). */
  private int priority;

  /** Earth's radius in miles. */
  private static final int EARTH_RADIUS = 3959;
  /** Maximum longitude/latitude. */
  public static final int MAX_LATLONG = 180;
  /** Default hour. */
  private final int MIDDAY = 12;
  /** OK connection. */
  private static final int OK_CONNECTION = 200;

  /**
   * Returns the distance between two events based on longitude, latitude.
   * It's possible to do an API call, but might take too long.
   * @param event1 starting point
   * @param event2 ending point
   * @return distance between event1 and event2, in miles
   */
  public static double distance(final Event event1, final Event event2) {
    double dLat = Math.toRadians(event2.latitude - event1.latitude);
    double dLng = Math.toRadians(event2.longitude - event1.longitude);
    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.cos(Math.toRadians(event1.latitude))
                    * Math.cos(Math.toRadians(event2.latitude))
                    * Math.sin(dLng / 2) * Math.sin(dLng / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return (EARTH_RADIUS * c);
  }

  /**
   * Gets the latitude and longitude positions of an address using BING maps
   * API.
   * @param address of source
   * @return latitude and longitude
   * @throws Exception excpetion
   */
  private static String[] getLatLongPositions(final String address)
          throws Exception {
    int responseCode = 0;
    String api = "http://dev.virtualearth.net/REST/v1/Locations?query="
     + URLEncoder.encode(address, "UTF-8")
     + "&maxResults=1"
     + "&o=xml"
     + "&key=AoM6SIKHt1RS8tAZdnN7dNfrPcW-E8sPXFjPMNEOH2_oqoxNI7nPG22Dk4-geIgH";
    System.out.println("URL : " + api);
    URL url = new URL(api);
    HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
    httpConnection.setRequestMethod("GET");
    httpConnection.setRequestProperty("Accept", "application/xml");

    httpConnection.connect();
    responseCode = httpConnection.getResponseCode();
    if (responseCode == OK_CONNECTION) {
      DocumentBuilder builder =
              DocumentBuilderFactory.newInstance().newDocumentBuilder();
      Document document = builder.parse(httpConnection.getInputStream());
      XPathFactory xPathfactory = XPathFactory.newInstance();
      XPath xpath = xPathfactory.newXPath();
      XPathExpression expr;

      QName cons = XPathConstants.STRING;
      expr = xpath.compile(
              "//ResourceSets/ResourceSet/Resources/Location/GeocodePoint/"
                      + "Latitude");
      String latitude = (String) expr.evaluate(document, cons);
      expr = xpath.compile(
               "//ResourceSets/ResourceSet/Resources/Location/GeocodePoint/"
                      + "Longitude");
      String longitude = (String) expr.evaluate(document, cons);

      expr = xpath.compile(
              "//ResourceSets/ResourceSet/Resources/Location/BoundingBox/"
                      + "SouthLatitude");
      String southLatitude = (String) expr.evaluate(document, cons);
      expr = xpath.compile(
              "//ResourceSets/ResourceSet/Resources/Location/BoundingBox/"
                      + "NorthLatitude");
      String northLatitude = (String) expr.evaluate(document, cons);
      expr = xpath.compile(
              "//ResourceSets/ResourceSet/Resources/Location/BoundingBox/"
                      + "WestLongitude");
      String westLongitude = (String) expr.evaluate(document, cons);
      expr = xpath.compile(
              "//ResourceSets/ResourceSet/Resources/Location/BoundingBox/"
                      + "EastLongitude");
      String eastLongitude = (String) expr.evaluate(document, cons);

      return new String[] {latitude, longitude,
              southLatitude, northLatitude, westLongitude, eastLongitude};
    } else {
        throw new Exception("Error from the API");
    }
  }

  /**
   * Sole constructor for Event.
   * Accepts time only as MM/DD/YYYY format currently. Will change to accept
   * month day, year.
   * ADDED: MM/DD/YYYY HOUR:MINUTE
   * @param event input
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
      latitude = MAX_LATLONG;
      longitude = MAX_LATLONG;
      southLatitude = MAX_LATLONG;
      northLatitude = -MAX_LATLONG;
      westLongitude = MAX_LATLONG;
      eastLongitude = -MAX_LATLONG;
    } else {
      latitude = Double.parseDouble(latlong[0]);
      longitude = Double.parseDouble(latlong[1]);
      southLatitude = Double.parseDouble(latlong[2]);
      northLatitude = Double.parseDouble(latlong[2 + 1]);
      westLongitude = Double.parseDouble(latlong[2 + 2]);
      eastLongitude = Double.parseDouble(latlong[2 + 2 + 1]);
    }

    String[] temp = event.date.split("\\s+");

    String[] temp1 = temp[0].split("/");
    int year = Integer.parseInt(temp1[2]);
    int month = Integer.parseInt(temp1[1]);
    int day = Integer.parseInt(temp1[0]);

    Calendar c = Calendar.getInstance();
    if (temp.length == 1) {
      c.set(year, month, day, MIDDAY, 0);
    } else {
      String[] temp2 = temp[1].split(":");
      c.set(year, month, day, Integer.parseInt(temp2[0]),
              Integer.parseInt(temp2[1]));
    }
    time = c.getTime();
  }

  /**
   * Sets the priority of the event.
   * @param priorityL of event
   */
  public void setPriority(final int priorityL) {
    this.priority = priorityL;
  }

  /**
   * Gets the priority of the event.
   * @return priority of event
   */
  public int getPriority() {
    return priority;
  }

  /**
   * Compares two events based on time.
   * @param o other event
   * @return comparison of times.
   */
  @Override
  public int compareTo(final Event o) {
    return time.compareTo(o.time);
  }
}
