gtfs-filter
===========

Compiling
---------
This project uses Maven for its dependency management. 
To get an executable jar use:

	mvn package


Usage
-----

	usage: [-o <folder>] [-l <lat:lon:lat:lon>] [-d <date>|<start:end>] [-t
	           <types>] INPUT
	gtfs-filter - This application can filter GTFS-feed on three different
	ways: by location, by traveldate and by transporttype
	 -d,--timespan <start:end>         filter trips outside the given timespan
	                                   (format: yyyy-mm-dd)
	 -l,--location <lat:lon:lat:lon>   filter locations outside given
	                                   latlon-box
	 -p,--polygon <pathGeojson>        filter locations outside given
	                                   geojson polygon boundary
	 -o,--output <location>            Output location for the filtered
	                                   gtfs-files (defaults to "output/"
	 -t,--type <types>                 only keep trips with the given
	                                   transport types. Possible values are:
	                                   tram, subway, rail, bus, ferry,
	                                   cablecar, gondola, funicular
	For more information, see https://github.com/twalcari/gtfs-filter
	
License
-------
gtfs-filter is distributed under the GNU General Public License. 
Please refer to license.txt for more information.

