# Team24
15-214 repo for Team24

## Data Plguins
SetListPlugin - 
Name: Enter what you want the dataset to be called
Subject: Name of the artist
Source: go to https://musicbrainz.org/doc/MusicBrainz_Identifier and enter the name of the artist you want. 
Click on the required one and copy the MBID from the details tab. It should look like this. e.g. 
Kendrick Lamar - 381086ea-f511-4aba-bdf9-71c753dc5077
	   
An MBID is required from the user as there are artists with duplicate names. MBID is a sure fire way to get the correct artist. 
Furthermore the API for setlist.fm (data source) also prefers MBID making it a perfect key. The API returns data in XML form. 
It is parsed using SAXParser data extraction is the only requirement, making it the optimal choice.

## Visual Plugins
BarChartPlugin -
Displays a Bar Chart of the frequency of keywords from the datasets.
The priority does not matter, but it is used to select which data sources you want. Enter a priority for the data sources you want 
and click enter.It is useful and extensible as you can use it in a variety of ways. You can use it to get an idea of what kind of songs 
you can expect in a concert (highest played songs are probably going to be played), see what tags are most used in Instagram/Twitter 
(currently Instagram data is entered from a CSV), etc.
