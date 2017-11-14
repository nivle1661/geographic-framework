package dataplugin;

import core.ClientEvent;
import core.DataPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import org.json.*;

/**
 * JSONPlugin - DataPlugin that reads and parses data from a JSON file containing a JSON array of JSON 
 * Objects
 * @author APadilla
 *
 */
public class JSONPlugin implements DataPlugin {
	
	/** at a minimum the JSON must contain allls of the following key words **/
	private static final String POPULATION_KEY = "quantity";
	private static final String KEYWORDS_KEY = "keywords";
	private static final String SUBJECT_KEY = "subject";
	private static final String LOCATION_KEY = "location";
	private static final String DATE_KEY = "time";
	
	private BufferedReader reader;
	private String subject;
	private String fileData;
	private JSONArray jsonData;
	private JSONObject next;
	private int currentIndex;
	
	/**
	 * set the subject of the plugin
	 * @param subject desired subject
	 */
	@Override
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	/**
	 * getter for the plugin subject
	 * @return subject of the plugin
	 */
	@Override
	public String getSubject() {
		return this.subject;
	}
	
	/**
	 * Reads the specified json file and creates a json array containing the json objects in the file
	 * @param arg path of the file
	 * @return true on success
	 */
	@Override
	public boolean openConnection(String arg) {
		ClassLoader classLoader = getClass().getClassLoader();
		try {
			InputStream stream = classLoader.getResourceAsStream(arg);
		        this.reader = new BufferedReader(new InputStreamReader(stream));
		        this.readFile();
		        this.jsonData = new JSONArray(this.fileData);
		        this.currentIndex = 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this.reader != null;
	}
	
	/**
	 * Loads the next json object into a ClientEvent
	 * @return a ClientEvent containing all relevant information on success
	 */
	@Override
	public ClientEvent getEvent() {
		try {
			int population = next.getInt(POPULATION_KEY);
			JSONArray keywords = next.getJSONArray(KEYWORDS_KEY);
			String location = next.getString(LOCATION_KEY);
			String date = next.getString(DATE_KEY);
			this.subject = next.getString(SUBJECT_KEY);
			List<String> kwArray = new ArrayList<String>();
			for (int i = 0; i < keywords.length(); i++) {
				kwArray.add(keywords.getString(i));
			}
			return new ClientEvent(kwArray, location, date, this.subject, population);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return new ClientEvent(null, null, null, null, 0);
	}
	
	/**
	 * checks the index of the json object array for validity of next object
	 * @return true if the next object exists and is valid
	 */
	@Override
	public boolean hasNext() {
		if (this.reader == null || this.jsonData == null || currentIndex >= this.jsonData.length()) {
			return false;
		}
		try {
			this.next = this.jsonData.getJSONObject(currentIndex);
			currentIndex++;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return this.next != null;
	}
	
	/**
	 * closes resources
	 */
	@Override
	public void closeConnection() {
		if (this.reader != null) {
			try {
				this.reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public String toString() {
		return "JSON loader";
	}
	
	/**
	 * reads the file specified when the connection was open and stores it as a string 
	 * to be converted to json
	 */
	private void readFile() {
		if (this.reader == null) {
			return;
		}
		try {
			StringBuilder builder = new StringBuilder();
			String line = this.reader.readLine();
			while (line != null) {
				builder.append(line);
				line = reader.readLine();
			}
			this.fileData = builder.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}