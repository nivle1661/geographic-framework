# Team24
15-214 repo for Team24

## Data Plugins
###### SetListPlugin - 
Name: Enter what you want the dataset to be called <br />
Subject: Name of the artist <br />
Source: Go to https://musicbrainz.org/doc/MusicBrainz_Identifier and enter the name of the artist you want. <br />
Click on the required one and copy the MBID from the details tab. It should look like this. e.g. <br />
Kendrick Lamar - 381086ea-f511-4aba-bdf9-71c753dc5077 <br />
	   
	 
An MBID is required from the user as there are artists with duplicate names. MBID is a sure fire way to get the correct artist. 
Furthermore the API for setlist.fm (data source) also prefers MBID making it a perfect key. The API returns data in XML form. 
It is parsed using SAXParser data extraction is the only requirement, making it the optimal choice.

###### CSVPLugin -
Name: Enter what you want the dataset to be called <br />
Subject: The who of the events <br />
Source: Resource file name, including .csv <br />


Resource files have to have columns Location, Date, Quantity (optional), and Keywords. 



## Visual Plugins
###### BarChartPlugin -
Displays a Bar Chart of the frequency of keywords from the datasets.
The priority does not matter, but it is used to select which data sources you want. Enter a priority for the data sources you want 
and click enter.It is useful and extensible as you can use it in a variety of ways. You can use it to get an idea of what kind of songs 
you can expect in a concert (highest played songs are probably going to be played), see what tags are most used in Instagram/Twitter 
(currently Instagram data is entered from a CSV), etc.

###### RoutePlugin -
Displays 3 different routes on a map, each corresponding to a different maximum speed. The events with highest priority are added first, and subsequent events are added based if the required speed to get to the next event and previous event is less than the maximum speed.
The 3 speeds are 20, 60, and 500 mph which are based on travel by public transportation, owned automobile, and plane.
Routes may overlap so it may be unclear where the routes go.
