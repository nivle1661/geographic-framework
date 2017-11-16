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
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Internal representation of an event by the Framework.
 */
public class Event implements Comparable<Event> {
  /** The who of the event. */
  private final String subject;

  /** Time of the event. */
  private final Date time;

  /** Location of the event as a string. */
  private final String location;

  /** Latitude of the event. */
  private final double latitude;
  /** Longitude of the event. */
  private final double longitude;

  /** Boundary of South Latitude of the event. */
  private final double southLatitude;
  /** Boundary of North Latitude of the event. */
  private final double northLatitude;
  /** Boundary of West Longitude of the event. */
  private final double westLongitude;
  /** Boundary of East Longitude of the event. */
  private final double eastLongitude;

  /** Keywords of the event. */
  private final List<String> keywords;

  /** How many people attended (OPTIONAL, DEFAULT = 0). */
  private final int quantity;
  /** The priority of the event (OPTIONAL, DEFAULT = 0). */
  private int priority;

  /** Earth's radius in miles. */
  private static final int EARTH_RADIUS = 3959;
  /** Maximum longitude/latitude. */
  public static final int MAX_LATLONG = 180;
  /** Default hour. */
  private final int midday = 12;
  /** OK connection. */
  private static final int OK_CONNECTION = 200;
  /** Tolerance for double equality. */
  private static final double EPSILON = 0.001;

  /**
   * Returns the distance between two events based on longitude, latitude.
   * It's possible to do an API call, but might take too long.
   * @param event1 starting point
   * @param event2 ending point
   * @return distance between event1 and event2, in miles
   */
  public static double distance(final Event event1, final Event event2) {
    if (Math.abs(event1.latitude - MAX_LATLONG) < EPSILON
        || Math.abs(event1.longitude - MAX_LATLONG) < EPSILON
        || Math.abs(event2.latitude - MAX_LATLONG) < EPSILON
        || Math.abs(event2.longitude - MAX_LATLONG) < EPSILON) {
      return Double.MAX_VALUE;
    }

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

      System.out.println(latitude + ", " + longitude + " | "
        + southLatitude + " " + northLatitude
        + " " + westLongitude + " " + eastLongitude);
      return new String[] {latitude, longitude,
              southLatitude, northLatitude, westLongitude, eastLongitude};
    } else {
        throw new Exception("Error from the API");
    }
  }

  /**
   * Sole constructor for Event.
   * Accepts time only as MM/DD/YYYY format or MM-DD-YYYY.
   * ADDED: MM/DD/YYYY HOUR:MINUTE
   * @param event input
   */
  public Event(final ClientEvent event) {
    double eastLongitude1;
    double westLongitude1;
    double northLatitude1;
    double southLatitude1;
    double longitude1;
    double latitude1;
    keywords = event.getKeywords();
    location = event.getLocation();
    subject = event.getSubject();
    quantity = event.getQuantity();
    priority = 0;

    String[] latlong = null;
    try {
      latlong = getLatLongPositions(location);
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      latitude1 = Double.parseDouble(latlong[0]);
      longitude1 = Double.parseDouble(latlong[1]);
      southLatitude1 = Double.parseDouble(latlong[2]);
      northLatitude1 = Double.parseDouble(latlong[2 + 1]);
      westLongitude1 = Double.parseDouble(latlong[2 + 2]);
      eastLongitude1 = Double.parseDouble(latlong[2 + 2 + 1]);
    } catch (NumberFormatException e) {
      latitude1 = MAX_LATLONG;
      longitude1 = MAX_LATLONG;
      southLatitude1 = MAX_LATLONG;
      northLatitude1 = -MAX_LATLONG;
      westLongitude1 = MAX_LATLONG;
      eastLongitude1 = -MAX_LATLONG;
    }

    eastLongitude = eastLongitude1;
    westLongitude = westLongitude1;
    northLatitude = northLatitude1;
    southLatitude = southLatitude1;
    longitude = longitude1;
    latitude = latitude1;
    String[] temp = event.getDate().split("\\s+");

    String[] temp1;
    if (temp[0].contains("/")) {
      temp1 = temp[0].split("/");
    } else {
      temp1 = temp[0].split("-");
    }
    int year = Integer.parseInt(temp1[2]);
    int month;
    try {
      month = Integer.parseInt(temp1[1]);
    } catch (NumberFormatException e) {
      Date date = null;
      try {
        date = new SimpleDateFormat("MMMM").parse(temp1[1]);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        month = cal.get(Calendar.MONTH);
      } catch (ParseException e1) {
        month = midday / 2;
        e1.printStackTrace();
      }
    }
    int day = Integer.parseInt(temp1[0]);

    Calendar c = Calendar.getInstance();
    if (temp.length == 1) {
      c.set(year, month, day, midday, 0);
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

  /**
   * Returns string representation (for debugging purposes).
   * @return string representation
   */
  @Override
  public String toString() {
    return subject + " " + time + " " + priority;
  }

  /**
   * Returns longitude.
   * @return longitude
   */
  public double getLongitude() {
    return longitude;
  }

  /**
   * Returns latitude.
   * @return latitude
   */
  public double getLatitude() {
    return latitude;
  }

  /**
   * Returns list of keywords.
   * @return list of keywords
   */
  public List<String> getKeywords() {
    return keywords;
  }

  /**
   * Returns location.
   * @return location
   */
  public String getLocation() {
    return location;
  }

  /**
   * Returns latitude bounds.
   * @return latitude bounds
   */
  public double[] getLatitudeBound() {
    return new double[]{southLatitude, northLatitude};
  }

  /**
   * Returns longitude bounds.
   * @return longitude bounds
   */
  public double[] getLongitudeBound() {
    return new double[]{westLongitude, eastLongitude};
  }

  /**
   * Returns time.
   * @return time
   */
  public Date getTime() {
    return time;
  }
  
  /**
   * return the subject
   * @return event subject
   */
  public String getSubject() {
	  return subject;
  }

  /**
   * Returns quantity.
   * @return quantity
   */
  public int getQuantity() {
    return quantity;
  }
}
