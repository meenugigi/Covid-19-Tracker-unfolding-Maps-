package demos;

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;

import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;

/** Implements a visual marker for cities on an earthquake map
 * 
 * @author UC San Diego Intermediate Software Development MOOC team
 * @author Kenneth
 *
 */
// TODO: Change SimplePointMarker to CommonMarker as the very first thing you do 
// in module 5 (i.e. CityMarker extends CommonMarker).  It will cause an error.
// That's what's expected.
public class CovidMarker extends CommonMarker {

	public static int TRI_SIZE = 5;  // The size of the triangle marker
	PImage img;

	public CovidMarker(Location location) {
		super(location);
	}

	public CovidMarker(Feature covidcase, PImage img) {
		super(((PointFeature)covidcase).getLocation(), covidcase.getProperties());
		this.img = img;
	}

	public CovidMarker(Feature covidcase) {
		super(((PointFeature)covidcase).getLocation(), covidcase.getProperties());
		// Cities have properties: "name" (city name), "country" (country name)
		// and "population" (population, in millions)
	}


	/**
	 * Implementation of method to draw marker on the map.
	 */
	@Override
	public void drawMarker(PGraphics pg, float x, float y) {
		// Save previous drawing style
		pg.pushStyle();

		// IMPLEMENT: drawing triangle for each city
		//		pg.fill(150, 30, 30);
		//		pg.triangle(x, y-TRI_SIZE, x-TRI_SIZE, y+TRI_SIZE, x+TRI_SIZE, y+TRI_SIZE);

		pg.imageMode(PConstants.CORNER);
		//The image is drawn in object coordinates, i.e. the marker's origin (0,0) is at its geo-location.

		pg.image(img, x-8, y-15, 30, 30);
		// Restore previous drawing style
		pg.popStyle();
	}

	/** Show the title of the city if this marker is selected */
	public void showTitle(PGraphics pg, float x, float y)
	{

		// TODO: Implement this method
		pg.pushStyle();

		String title = "Country: " + getCity();
		String name = "Total cases: " + getCountry();
		String pop =  "Population: " + getPopulation();
		String newcases = "New Cases: " + getNoOfCases();
		long nameWidth = (long) pg.textWidth(title);
		long total = (long) pg.textWidth(name);
		long popWidth = (long) pg.textWidth(pop);
		long newcase = (long) pg.textWidth(newcases);

		
		
		//String title = getCountryName();
	//	String pop =  "Population: " + getpopulation() + " million";
	//	String totalcases = "Total Confirmed Cases: " + getNoOfCases();
	//	String newcases = "New Cases: " + getNewCases();
		//float nameWidth = pg.textWidth(title);
	//	long popWidth = (long) pg.textWidth(pop);
	//	long totalWidth = (long) pg.textWidth(totalcases);
	//	long newWidth = (long) pg.textWidth(newcases);
		

		

		pg.rectMode(PConstants.CORNER);
		pg.fill(255);
		pg.textSize(12);
		pg.rect(x+15, y-8, Math.max(nameWidth, popWidth) + 10, 68, 5, 5, 5, 5);
		pg.textAlign(PConstants.LEFT, PConstants.TOP);
		pg.fill(20, 24, 35);
		pg.text(title, x+22, y-5);
		pg.text(pop, x+22, y+10);
		pg.text(name, x+22, y+25);
		pg.text(newcases, x+22, y+40);
	//	pg.text(totalcases, x+22, y+10);
	//	pg.text(newcases, x+22, y+10);
		pg.popStyle();
	}



	/* Local getters for some city properties.  
	 */
	public String getCity()
	{
		return getStringProperty("name");
	}

	public long getCountry()
	{
		return Long.parseLong(getStringProperty("totalcases"));
	}

	public long getPopulation()
	{
		return Long.parseLong(getStringProperty("population"));
	}

	public long getNoOfCases()
	{
		return Long.parseLong(getStringProperty("newcase"));
	}
	//public long getNewCases()
//	{
//		return Long.parseLong(getStringProperty("new"));
//	}
}