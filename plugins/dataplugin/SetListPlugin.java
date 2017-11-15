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

public class SetListPlugin implements DataPlugin{


    /** Input reader. */
    private StringBuffer data;

    /** Subject for the file. */
    private String subject;

    private List<ClientEvent> events;
    private int size;

    /**
     * Creates a plugin for CSV files
     */
    public SetListPlugin() {
        data = null;
        subject = null;
        events= null;
        size = 0;
    }

    @Override
    public void setSubject(String subject) {
        this.subject=subject;
    }

    @Override
    public String getSubject() {
        return subject;
    }
    // 381086ea-f511-4aba-bdf9-71c753dc5077  for KDot
    @Override
    public boolean openConnection(String arg) {
        String link="https://api.setlist.fm/rest/1.0/artist/"+arg+"/setlists?p=1";
        try {
            URL url=new URL(link);
            try {
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestProperty("x-api-key", "dd4e0a92-2fe5-4d4a-b8b9-74958f8e6d6b" );
                BufferedReader input= new BufferedReader(new InputStreamReader(con.getInputStream()));

                String line;
                data=new StringBuffer();
                while ((line = input.readLine())!=null) {
                    data.append(line);
                }

                input.close();
                InputSource is = new InputSource(new StringReader(data.toString()));
                SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
                SAXParser saxParser = saxParserFactory.newSAXParser();
                MyHandler handler = new MyHandler();
                saxParser.parse(is, handler);
                events=handler.getEvents();
                size=events.size();

            } catch (IOException | ParserConfigurationException | SAXException e){
                e.printStackTrace();
            }
        } catch (MalformedURLException e){
            e.printStackTrace();
        }

        return data != null;
    }

    @Override
    public ClientEvent getEvent() {
        if(events!=null){
            if(size>0) size--;
            return events.get(size);
        }
        return null;
    }

    @Override
    public boolean hasNext() {
        return size!=0;
    }

    @Override
    public void closeConnection() {

    }

    @Override
    public String toString() {
        return "Set List Loader";
    }

    private class MyHandler extends DefaultHandler{

        private List<ClientEvent> events = new ArrayList<>();
        private StringBuffer location;
        private List<String> songs;
        private String date;

        public List<ClientEvent> getEvents() {
            return events;
        }



        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if(qName.equalsIgnoreCase("setlist")){
                date = attributes.getValue("eventDate");
                location=new StringBuffer("");
                songs=new ArrayList<>();
            } else if(qName.equalsIgnoreCase("venue")){
                location.append(attributes.getValue("name"));
            } else if(qName.equalsIgnoreCase("city")) {
                location.append(", ");
                location.append(attributes.getValue("name"));
                location.append(", ");
                location.append(attributes.getValue("state"));
            } else if(qName.equalsIgnoreCase("song")) {
                songs.add(attributes.getValue("name"));
            }

        }


        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if(qName.equalsIgnoreCase("setlist")){
                events.add(new ClientEvent(songs,location.toString(), date, subject, 0));
            }
        }
    }

}
