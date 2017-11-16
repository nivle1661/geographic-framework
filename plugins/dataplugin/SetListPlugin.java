package dataplugin;

import core.ClientEvent;
import core.DataPlugin;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements a DataPlugin to get artist info from setlist.fm using its API.
 */
public class SetListPlugin implements DataPlugin {


    /**
     * Input reader.
     */
    private StringBuffer data;

    /**
     * Subject for the file.
     */
    private String subject;
    /**
     * List which contains all the events.
     */
    private List<ClientEvent> events;
    /**
     * the number of total events.
     */
    private int size;

    /**
     * Creates a plugin for CSV files.
     */
    public SetListPlugin() {
        data = null;
        subject = null;
        events = null;
        size = 0;
    }

    /**
     * Setter for the name of the artist.
     * @param subject1 - name of the artist
     */
    @Override
    public void setSubject(final String subject1) {
        this.subject = subject1;
    }

    /**
     * Getter for the name of the artist
     * @return subject - name of the artist
     */
    @Override
    public String getSubject() {
        return subject;
    }

    /**
     * Opens the connection to the API and parses the XML data it returns
     * @param arg - MBID of the artist you want to search
     * @return true if successful
     */
    @Override
    public boolean openConnection(String arg) {
        String link = "https://api.setlist.fm/rest/1.0/artist/" + arg + "/setlists?p=1";
        try {
            URL url = new URL(link);
            try {
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestProperty("x-api-key", "dd4e0a92-2fe5-4d4a-b8b9-74958f8e6d6b");
                BufferedReader input = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String line;
                data = new StringBuffer();
                while ((line = input.readLine()) != null) {
                    data.append(line);
                }

                input.close();
                InputSource is = new InputSource(new StringReader(data.toString()));
                SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
                SAXParser saxParser = saxParserFactory.newSAXParser();
                MyHandler handler = new MyHandler();
                saxParser.parse(is, handler);
                events = handler.getEvents();
                size = events.size();

            } catch (IOException | ParserConfigurationException | SAXException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return data != null;
    }

    @Override
    public ClientEvent getEvent() {
        if (events != null) {
            if (size > 0) size--;
            return events.get(size);
        }
        return null;
    }

    /**
     * Checks if there are any events left to process
     * @return true if there are events left
     */
    @Override
    public boolean hasNext() {
        return size != 0;
    }

    /**
     * Closes the connection to the API if required
     */
    @Override
    public void closeConnection() {

    }

    /**
     * Corrects string representation of the class
     * @return - the correct string
     */
    @Override
    public String toString() {
        return "Set List Loader";
    }

    /**
     * Class for parsing XML Data using SAXParser
     */
    private class MyHandler extends DefaultHandler {
        /**
         * the event list which needs to be generated
         */
        private List<ClientEvent> events = new ArrayList<>();
        /**
         * location of the current event
         */
        private StringBuffer location;
        /**
         * Songs played at the current event
         */
        private List<String> songs;
        /**
         * Date of the current event
         */
        private String date;

        /**
         * Getter for the event list
         * @return - event list
         */
        List<ClientEvent> getEvents() {
            return events;
        }

        /**
         *
         * @param uri - the uri of the xml
         * @param localName - local name
         * @param qName - name of the header
         * @param attributes - attribute values of the header
         * @throws SAXException - in case API fiales
         */
        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (qName.equalsIgnoreCase("setlist")) {
                date = attributes.getValue("eventDate");
                location = new StringBuffer("");
                songs = new ArrayList<>();
            } else if (qName.equalsIgnoreCase("venue")) {
                location.append(attributes.getValue("name"));
            } else if (qName.equalsIgnoreCase("city")) {
                location.append(", ");
                location.append(attributes.getValue("name"));
                location.append(", ");
                location.append(attributes.getValue("state"));
            } else if (qName.equalsIgnoreCase("song")) {
                songs.add(attributes.getValue("name"));
            }

        }

        /**
         * Creates an adds a ClientEvent to the event list
         * @param uri - the uri of the xml
         * @param localName - local name
         * @param qName - name of the header
         * @throws SAXException - in case the API fails
         */
        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (qName.equalsIgnoreCase("setlist")) {
                events.add(new ClientEvent(songs, location.toString(), date, subject, 0));
            }
        }
    }

}
