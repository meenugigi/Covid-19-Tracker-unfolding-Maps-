package demos;
import processing.core.PApplet;
import processing.core.PGraphics;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import parsing.ParseFeed;
import de.fhpotsdam.unfolding.providers.*;
import de.fhpotsdam.unfolding.providers.Google.*;
import java.util.List;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import java.awt.Point;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;


public class CovidCases extends PApplet {

	private static final boolean offline = false;
	public static String mbTilesString = "blankLight-1-3.mbtiles";

	UnfoldingMap map;
	Map<String, Float> covidcasesByCountry;
	Map<String, Float> newcasesByCountry;
	List<Feature> countries;
	List<Marker> countryMarkers;
	
	String one = "0 - 75k";
	String two = "75k - 1.5L";
	String three = "1.5L - 2.5L";
	String four = "2.5L - 3.5L";
	String five = "3.5L - 4.5L";
	String six = "4.5L and more";
	String seven = "Data not found";
	SimplePointMarker berlinMarker;
	
	private PGraphics buffer;

	private CommonMarker lastSelected;
	private CommonMarker lastClicked;
	private List<Marker> coronaMarkers;

	private AbstractMapProvider provider1;
	private AbstractMapProvider provider2;
	private AbstractMapProvider provider3;
	

	private String earthquakesURL = "earth.rss";
PointFeature trzesienie;		
	private List<PointFeature> earthquakes;
	

	public void setup() {
		size(1300, 680, OPENGL);
		buffer = createGraphics(1300, 680);
		background(0);
		
		if (offline) {
			map = new UnfoldingMap(this, 200, 50, 650, 600, new MBTilesMapProvider(mbTilesString));
			earthquakesURL = "2.5_week.atom";  
		}
		else {
			
			provider1 = new Microsoft.HybridProvider();
			provider2 = new Google.GoogleMapProvider();
			provider3 = new Microsoft.RoadProvider();
			
		}
		
		
		map = new UnfoldingMap(this, 200, 50, 900, 600, provider1);
		MapUtils.createDefaultEventDispatcher(this, map);

	
		covidcasesByCountry = loadCovidCasesFromCSV("WorldCovidCases.csv");
		println("Loaded " + covidcasesByCountry.size() + " data entries");		
	
		List<Feature> covid = GeoJSONReader.loadData(this, "covid-data.json");
		coronaMarkers = new ArrayList<Marker>();
		for(Feature covidcase : covid) {
			
			coronaMarkers.add(new CovidMarker(covidcase, loadImage("ui/marker_red.png")));
		}
		

		countries = GeoJSONReader.loadData(this, "countries.geo.json");
		countryMarkers = MapUtils.createSimpleMarkers(countries);
		map.addMarkers(countryMarkers);
	
	
		shadeCountries();
		
		
	    List<Marker> markers = new ArrayList<Marker>();

	    earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);	    
	    System.out.println(earthquakes);	    
	
	    	     	    
	    if (earthquakes.size() > 0) {
	    	PointFeature f = earthquakes.get(2);
	    	System.out.println(f.getProperties());
	    	Object magObj = f.getProperty("magnitude");
	    	System.out.println("printing magObj" + magObj);
	    	float mag = Float.parseFloat(magObj.toString());
	    	System.out.println("printing mag" + mag);
	    	// PointFeatures also have a getLocation method
	    }
	    
	
	    int yellow = color(255, 255, 0);
	    
	    //TODO: Add code here as appropriate	    
//	    
	  
	map.addMarkers(coronaMarkers);


				
	}


	
	
	public void draw() {
		
		buffer.beginDraw();
	//	background(0);
		map.draw();
		buffer.endDraw();
		image(buffer, 0, 0);
		buffer.clear();
		
		// Draw map tiles and country markers
		//map.draw();
		addLocation();

		
		if(lastSelected != null) {
			lastSelected.drawTitleOnTop(buffer, mouseX, mouseY);
		}
				
		//show an info like the count of nearby earthquake, average magnitude, and recent earthquake
		if(lastClicked instanceof CovidMarker) {
			popMenu();
		}
		
	
		fill(255, 250, 240);

		int xbase = 15;
		int ybase = 50;

		rect(xbase, ybase, 150, 250);

		fill(0);
		textAlign(LEFT, CENTER);
		textSize(12);
		text("Map Key", xbase+50, ybase+15);
		
		
		fill(255, 255, 255);
		text("Total number of Confirmed Covid-19 Cases", 15, 15);
		text("Data as on 06 Aug 2020", 15, 35);
		
		fill(253,89,30);
		ellipse(35, 90, width/50, height/50);
		fill(0,0,0);
		text(one, 55, 90);
		
		fill(213,109,71);
		ellipse(35, 110, width/50, height/50);
		fill(0,0,0);
		text(two, 55, 110);
		
		fill(191,141,123);
		ellipse(35, 130, width/50, height/50);
		fill(0,0,0);
		text(three, 55, 130);
		
		fill(149,127,163);
		ellipse(35, 150, width/50, height/50);
		fill(0,0,0);
		text(four, 55, 150);
		
		fill(79,114,174);
		ellipse(35, 170, width/50, height/50);
		fill(0,0,0);
		text(five, 55, 170);
		
		fill(45, 119,246);
		ellipse(35, 190, width/50, height/50);
		fill(0,0,0);
		text(six, 55, 190);
		
		fill(150,150,150);
		ellipse(35, 210, width/50, height/50);
		fill(0,0,0);
		text(seven, 55, 210);
	}
	
	
	@Override
	public void mouseMoved()
	{
		// clear the last selection
		if (lastSelected != null) {
			lastSelected.setSelected(false);
			lastSelected = null;

		}
	
		selectMarkerIfHover(coronaMarkers);				
	}

	
	public void keyPressed() {
		if(key == '1') {
			map.mapDisplay.setProvider(provider1);
			hideOtherMarkers(coronaMarkers);
		} else if(key == '2') {
			map.mapDisplay.setProvider(provider2);
			unhideMarkers();
		} else if(key == '3') {
			map.mapDisplay.setProvider(provider3);
		} 
		}

	private void selectMarkerIfHover(List<Marker> markers)
	{
		if (lastSelected != null) {
			return;
		}

		for(Marker marker : markers) {
			if(marker.isInside(map, mouseX, mouseY) && lastSelected == null) {
				lastSelected = (CommonMarker) marker;
				lastSelected.setSelected(true);
				break;
			}
		}
	}
	
	
	
	
	private void popMenu() {
	
	}
	
	
	
	@Override
	public void mouseClicked()
	{
		
		if(lastClicked != null) {
			lastClicked.setClicked(false);
			lastClicked = null;
			unhideMarkers();			
		} else if (lastClicked == null) {
			checkMarkersForClick(coronaMarkers);			
			if(lastClicked instanceof CovidMarker) {
				hideOtherMarkers(coronaMarkers);				
			} 
		}
	}
	
	
	private void unhideMarkers() {
		
		for(Marker marker : coronaMarkers) {
			marker.setHidden(false);
		}		
	}

	
	private void hideOtherMarkers(List<Marker> markers) {
		for(Marker marker : markers) {
			if(marker != lastClicked) {
				marker.setHidden(true);
			}
		}
	} 
	
	private void checkMarkersForClick(List<Marker> markers) {
		// TODO Auto-generated method stub
		for(Marker marker : markers) {
			if(lastClicked != null) {
				break;
			}
			if(!marker.isHidden() && marker.isInside(map, mouseX, mouseY)) {
				lastClicked = (CommonMarker) marker;
				lastClicked.setClicked(true);
				break;
			}
		}
	}

	
	private void addLocation() {
		int xbase = 25;
		int ybase = 50;
		fill(255);
		rect(xbase+650, ybase+600, 175, 30);
		Location location = map.getLocation(mouseX, mouseY);
		fill(0);
		text(location.getLat()+", "+location.getLon(), 685, 665);
	}


	//Red-orange indicates low (near 40)
	//Blue indicates high (near 100)
	private void shadeCountries() {
		for (Marker marker : countryMarkers) {
			// Find data for country of the current marker
			String countryId = marker.getId();
			if (covidcasesByCountry.containsKey(countryId)) {
				float cases = covidcasesByCountry.get(countryId);
				// Encode value as brightness (values range: 40-90)
				int colorLevel = (int) map(cases, 1200, 556000, 5, 255);
				marker.setColor(color(255-colorLevel, 100, colorLevel));
			}
			else {
				marker.setColor(color(150,150,150));
			}
		}
	}

	//Helper method to load life expectancy data from file
	private Map<String, Float> loadCovidCasesFromCSV(String fileName) {
		Map<String, Float> lifeExpMap = new HashMap<String, Float>();

		String[] rows = loadStrings(fileName);
		for (String row : rows) {
			
			String[] columns = row.split(",");
			if (columns.length == 6 && !columns[5].equals("..")) {
				lifeExpMap.put(columns[4], Float.parseFloat(columns[5]));
			}
		}

		return lifeExpMap;
	}

}
