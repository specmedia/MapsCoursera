package module6;

import de.fhpotsdam.unfolding.data.PointFeature;
import processing.core.PGraphics;

/** Author: UC San Diego Intermediate Software Development MOOC team
 * @student Dmitry Sergeev, Minsk, Belarus.
 * Date: March 17, 2016
 * */
public class OceanQuakeMarker extends EarthquakeMarker {
	
	public OceanQuakeMarker(PointFeature quake) {
		super(quake);
		
		// setting field in earthquake marker
		isOnLand = false;
	}
	

	@Override
	public void drawEarthquake(PGraphics pg, float x, float y) {
		//IMPLEMENT: drawing centered square for Ocean earthquakes
		// DO NOT set the fill color.  That will be set in the EarthquakeMarker
		// class to indicate the depth of the earthquake.
		// Simply draw a centered square.
		// HINT: Notice the radius variable in the EarthquakeMarker class
		// and how it is set in the EarthquakeMarker constructor
		pg.rect(x-radius, y-radius, 2*radius, 2*radius);
	}
	

	

}