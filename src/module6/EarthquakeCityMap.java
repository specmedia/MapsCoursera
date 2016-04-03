package module6;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.AbstractShapeMarker;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.MultiMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;
import demos.ImageMarker;
import parsing.ParseFeed;
import processing.core.PApplet;
import processing.core.PImage;

import javax.swing.*; 
import java.awt.*;
import java.awt.event.*;

/** Author: UC San Diego Intermediate Software Development MOOC team
 * @student Dmitry Sergeev, Minsk, Belarus.
 * Date: March 17, 2016
 * */
public class EarthquakeCityMap extends PApplet {
	
	// We will use member variables, instead of local variables, to store the data
	// that the setUp and draw methods will need to access (as well as other methods)
	// You will use many of these variables, but the only one you should need to add
	// code to modify is countryQuakes, where you will store the number of earthquakes
	// per country.
	
	// You can ignore this.  It's to get rid of eclipse warnings
	private static final long serialVersionUID = 1L;

	// IF YOU ARE WORKING OFFILINE, change the value of this variable to true
	private static final boolean offline = false;

	private static final Location distance = null;
	
	/** This is where to find the local tiles, for working without an Internet connection */
	public static String mbTilesString = "blankLight-1-3.mbtiles";
	
	//test - this will need to change to the clicked location and be set in that method
	Location venice = new Location(45.44f, 12.34f);
	
	PImage photo;

	//feed with magnitude 2.5+ Earthquakes
	private String earthquakesURL = "http://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";
	
	// The files containing city names and info and country names and info
	private String cityFile = "city-data.json";
	private String countryFile = "countries.geo.json";
	
	// The map
	private UnfoldingMap map;
	
	// Markers for each city
	private List<Marker> cityMarkers;
	// Markers for each earthquake
	private List<Marker> quakeMarkers;

	// A List of country markers
	private List<Marker> countryMarkers;
	
	//NEW for Extension:
	private List<Marker> airportList;
	List<Marker> routeList;
	static float DISTANCE = 200;
	
	// NEW IN MODULE 5
	private CommonMarker lastSelected;
	private CommonMarker lastClicked;
	
	public void setup() {		
		// (1) Initializing canvas and map tiles
		size(900, 700, OPENGL);
		if (offline) {
		    map = new UnfoldingMap(this, 200, 50, 650, 600, new MBTilesMapProvider(mbTilesString));
		    earthquakesURL = "2.5_week.atom";  // The same feed, but saved August 7, 2015
		}
		else {
			map = new UnfoldingMap(this, 200, 50, 650, 600, new Google.GoogleMapProvider());
			// IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
		    //earthquakesURL = "2.5_week.atom";
		}
		MapUtils.createDefaultEventDispatcher(this, map);
		
		earthquakesURL = "quiz2.atom";
		
		
		// (2) Reading in earthquake data and geometric properties
	    //     STEP 1: load country features and markers
		List<Feature> countries = GeoJSONReader.loadData(this, countryFile);
		
		countryMarkers = MapUtils.createSimpleMarkers(countries);
		
		//     STEP 2: read in city data
		List<Feature> cities = GeoJSONReader.loadData(this, cityFile);
		
		cityMarkers = new ArrayList<Marker>();
		for(Feature city : cities) {
		  cityMarkers.add(new CityMarker(city));
		}
	    
		//     STEP 3: read in earthquake RSS feed
	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    
	    quakeMarkers = new ArrayList<Marker>();
	    
	    for(PointFeature feature : earthquakes) {
		  //check if LandQuake
		  if(isLand(feature)) {
		    quakeMarkers.add(new LandQuakeMarker(feature));
		  }
		  // OceanQuakes
		  else {
		    quakeMarkers.add(new OceanQuakeMarker(feature));
		  }
	    }
	    
	    
	    //moved from AirportMap - perhaps turn this into a method and call from the clicked method?
	        // get features from airport data
	 		List<PointFeature> features = ParseFeed.parseAirports(this, "airports.dat"); //this list has location
	 		
	 		// list for markers, hashmap for quicker access when matching with routes
	 		airportList = new ArrayList<Marker>();
	 		HashMap<Integer, Location> airports = new HashMap<Integer, Location>();
	 		
	 	//call new method that will display airport within x/200 miles of clicked earthquake location
	    photo = loadImage("../data/aircraft.png");
	 		
	 		
	 	createMarkersFromFeatures(features);// -- moving to a click event....

	    // could be used for debugging
	    printQuakes();
	 		
	    // (3) Add markers to map
	    //     NOTE: Country markers are not added to the map.  They are used
	    //           for their geometric properties
	    map.addMarkers(quakeMarkers);
	    map.addMarkers(cityMarkers);
	    map.addMarkers(airportList);
	    
	    //call sort and print method
	    sortAndPrint(50);
	    
	}  // End setup
	
	
	public void draw() {
		background(0);
		map.draw();
		addKey();
		
	}
	
	
	// TODO: Add the method:
	/*This method will 
	 * create a new array from the list of earthquake markers 
	 * (hint: there is a method in the List interface named toArray() which returns the elements in the List as an array of Objects). 
	 * Then it will 
	 * sort the array of earthquake markers in reverse order of their magnitude (highest to lowest) 
	 * and then print out the top numToPrint earthquakes. 
	 * 
	 * If numToPrint is larger than the number of markers in quakeMarkers, 
	 * it should print out all of the earthquakes and stop, 
	 * but it should not crash.
	 * 
	 * Call this method from setUp() to test it. 
	 * 
	 * An example input and output files are provided in the data folder: 
	 * use test2.atom as the input file, and sortandPrint.test2.out.txt 
	 * is the expected output for a couple different calls to sortAndPrint.*/
	
	private void sortAndPrint(int numToPrint){
		Object [] earthQarray = quakeMarkers.toArray();
		int numQuakes = earthQarray.length;
		Boolean message = false;
			Arrays.sort(earthQarray);
			if(numToPrint>numQuakes){
				numToPrint = numQuakes;
				message = true;
			}
			
			for(int i=0; i<numToPrint; i++){
				System.out.println(earthQarray[i]);
			}
			if(message==true){
				System.out.println("There were only " + numToPrint + " earthquakes.");
			}
	}

	/** Event handler that gets called automatically when the 
	 * mouse moves.
	 */
	@Override
	public void mouseMoved()
	{
		// clear the last selection
		if (lastSelected != null) {
			lastSelected.setSelected(false);
			lastSelected = null;
		
		}
		selectMarkerIfHover(quakeMarkers);
		selectMarkerIfHover(cityMarkers);
		//loop();
	}
	
	// If there is a marker selected 
	private void selectMarkerIfHover(List<Marker> markers)
	{
		// Abort if there's already a marker selected
		if (lastSelected != null) {
			return;
		}
		
		for (Marker m : markers) 
		{
			CommonMarker marker = (CommonMarker)m;
			if (marker.isInside(map,  mouseX, mouseY)) {
				lastSelected = marker;
				marker.setSelected(true);
				return;
			}
		}
	}
	
	/** The event handler for mouse clicks:
	 * 
	 * It will display an earthquake and its threat circle of cities
	 * 
	 * Or if a city is clicked, it will display all the earthquakes 
	 * where the city is in the threat circle
	 */
	@Override
	public void mouseClicked()
	{
		if (lastClicked != null) {
			//this is different
			unhideMarkers();
			lastClicked = null;
		}
		else if (lastClicked == null) 
		{   //this is different
			checkEarthquakesForClick();
			
			if (lastClicked == null) {
				checkCitiesForClick(); //another helper method
			}
			
			//call the method that displays airports based on the feature.getLocation (will need to pass in)
			 if (lastClicked!=null) {
				checkAirports(airportList, lastClicked.getLocation());
			}
			 else{
				 for(Marker m: airportList){
					 m.setHidden(true);
				 }
			 }
		}
	}
	
	// Helper method that will check if a city marker was clicked on
	// and respond appropriately
	private void checkCitiesForClick()
	{
		if (lastClicked != null) return;
		
		// Loop over the earthquake markers to see if one of them is selected
		for (Marker marker : cityMarkers) {
			if (!marker.isHidden() && marker.isInside(map, mouseX, mouseY)) {
				
				lastClicked = (CommonMarker)marker;
				
				// Hide all the other earthquakes and hide
				for (Marker mhide : cityMarkers) {
					if (mhide != lastClicked) {
						mhide.setHidden(true);
					}
				}
				for (Marker mhide : quakeMarkers) {
					                               //we cast it just to use the threat circle thing...
					EarthquakeMarker quakeMarker = (EarthquakeMarker)mhide;
					
					if (quakeMarker.getDistanceTo(marker.getLocation()) 
							> quakeMarker.threatCircle()) {
						quakeMarker.setHidden(true);
					}
				}							
					
				}
				return;
			}
		}		
	
	
	// Helper method that will check if an earthquake marker was clicked on
	// and respond appropriately
	private void checkEarthquakesForClick()
	{
		if (lastClicked != null) return;
		
		// Loop over the earthquake markers to see if one of them is selected
		for (Marker m : quakeMarkers) {
			EarthquakeMarker marker = (EarthquakeMarker)m;
			if (!marker.isHidden() && marker.isInside(map, mouseX, mouseY)) {
				
				lastClicked = marker;
				
				// Hide all the other earthquakes and hide
				for (Marker mhide : quakeMarkers) {
					if (mhide != lastClicked) {
						mhide.setHidden(true);
					}
				}
				for (Marker mhide : cityMarkers) {
					if (mhide.getDistanceTo(marker.getLocation()) 
							> marker.threatCircle()) {
						mhide.setHidden(true);
					}
				}
				return;
			}
		}
	}
	//is unhide airport markers a separate method?  will go through airport list of Markers and unhide things that are within 200 miles
	public void checkAirports(List<Marker> airportData, Location clickedMarkerLocation){
		
		for(Marker m: airportData){
			
			if(clickedMarkerLocation.getDistance(m.getLocation()) < 200){ 
				m.setHidden(false);
				
			}
			
		}

	}
	
	// loop over and unhide all markers
	private void unhideMarkers() {
		for(Marker marker : quakeMarkers) {
			marker.setHidden(false);
		}
			
		for(Marker marker : cityMarkers) {
			marker.setHidden(false);
		}
	}
	

	
	// helper method to draw key in GUI
		private void addKey() {	
			int xbase = 25;
			int ybase = 50;
			
			fill(200, 200, 200);
			rect(xbase-20, ybase, 180, 340);
				
			int title_x = xbase + 25;
			int title_y = ybase + 25;
			fill(0);
			textAlign(LEFT, CENTER);
			textSize(12);
			text("EARTHQUAKE KEY", title_x, title_y);
				
				
			// Draw city marker as an equilateral triangle
			int tri_x = xbase + 35;
			int tri_y = ybase + 65;
			
			float xdelta = (float) Math.cos(Math.toRadians(30.0)) * 5;
			float ydelta = (float) Math.sin(Math.toRadians(30.0)) * 5;
			fill(color(160, 160, 160));
			stroke(color(128, 128, 128));
			triangle(tri_x, tri_y-5, tri_x-xdelta, tri_y+ydelta, tri_x+xdelta, tri_y+ydelta);
				
			// Draw land quake marker as circle
			int cir_x = xbase + 35;
			int cir_y = ybase + 80;
			fill(255, 255, 255);
			ellipse(cir_x, cir_y, 10, 10);
				
			// Draw ocean quake marker as square
			int square_x = xbase + 35;
			int square_y = ybase + 95;
			rect(square_x - 5, square_y - 5, 10, 10);
				
			// Draw labels
			int label_x = xbase + 30;
			int label_y = ybase + 50;
			fill(0, 0, 0);
			text("Markers", label_x, label_y);
			text("City", label_x + 20, label_y + 15);
			text("Land Quake", label_x + 20, label_y + 30);
			text("Ocean Quake", label_x + 20, label_y + 45);
				
			text("Magnitude", label_x, label_y + 65);
			text("Marker Size", label_x + 15, label_y + 80);
				
			text("Depth", label_x, label_y + 100);
			
			photo = loadImage("../data/aircraft.png");
				
			// Create graduated color scale from yellow to blue for the Depth
			int depth_x = label_x;
			int depth_y = label_y + 115;
			int width = 100;
			
			int c1 = color(255, 255, 0);
			int c2 = color(0, 0, 255);
			for(int i = depth_y; i <= depth_y + width; i++) {
				float inter = map(i, depth_y, depth_y + width, 0, 1);
				int c = lerpColor(c1, c2, inter);
				stroke(c);
				line(depth_x, i, depth_x + 20, i);
			}
				
			// Add depth levels to color scale
			text("0,0", depth_x + 30, depth_y);
			text("7,0", depth_x + 30, depth_y + width-5);
			
		}

public void createMarkersFromFeatures(List<PointFeature> features){
		
		for(PointFeature feature : features) {
			AirportMarker m = new AirportMarker(feature, photo);
			m.setHidden(true);
			airportList.add(m);
		}		
	}
	
	
	// Checks whether this quake occurred on land.  If it did, it sets the 
	// "country" property of its PointFeature to the country where it occurred
	// and returns true.  Notice that the helper method isInCountry will
	// set this "country" property already.  Otherwise it returns false.
	private boolean isLand(PointFeature earthquake) {
		
		// IMPLEMENT THIS: loop over all countries to check if location is in any of them
		// If it is, add 1 to the entry in countryQuakes corresponding to this country.
		for (Marker country : countryMarkers) {
			if (isInCountry(earthquake, country)) {
				return true;
			}
		}
		
		// not inside any country
		return false;
	}
	
	// prints countries with number of earthquakes
	// You will want to loop through the country markers or country features
	// (either will work) and then for each country, loop through
	// the quakes to count how many occurred in that country.
	// Recall that the country markers have a "name" property, 
	// And LandQuakeMarkers have a "country" property set.
	private void printQuakes() {
		int totalWaterQuakes = quakeMarkers.size();
		for (Marker country : countryMarkers) {
			String countryName = country.getStringProperty("name");
			int numQuakes = 0;
			for (Marker marker : quakeMarkers)
			{
				EarthquakeMarker eqMarker = (EarthquakeMarker)marker;
				if (eqMarker.isOnLand()) {
					if (countryName.equals(eqMarker.getStringProperty("country"))) {
						numQuakes++;
					}
				}
			}
			if (numQuakes > 0) {
				totalWaterQuakes -= numQuakes;
				System.out.println(countryName + ": " + numQuakes);
			}
		}
		System.out.println("OCEAN QUAKES: " + totalWaterQuakes);
	}
	
	
	
	// helper method to test whether a given earthquake is in a given country
	// This will also add the country property to the properties of the earthquake feature if 
	// it's in one of the countries.
	// You should not have to modify this code
	private boolean isInCountry(PointFeature earthquake, Marker country) {
		// getting location of feature
		Location checkLoc = earthquake.getLocation();

		// some countries represented it as MultiMarker
		// looping over SimplePolygonMarkers which make them up to use isInsideByLoc
		if(country.getClass() == MultiMarker.class) {
				
			// looping over markers making up MultiMarker
			for(Marker marker : ((MultiMarker)country).getMarkers()) {
					
				// checking if inside
				if(((AbstractShapeMarker)marker).isInsideByLocation(checkLoc)) {
					earthquake.addProperty("country", country.getProperty("name"));
						
					// return if is inside one
					return true;
				}
			}
		}
			
		// check if inside country represented by SimplePolygonMarker
		else if(((AbstractShapeMarker)country).isInsideByLocation(checkLoc)) {
			earthquake.addProperty("country", country.getProperty("name"));
			
			return true;
		}
		return false;
	}

}
