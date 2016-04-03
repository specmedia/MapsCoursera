package module6;

import java.awt.Shape;
import java.util.List;

import com.jogamp.graph.geom.Vertex;

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import demos.Plane;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PShape;
/** Author: UC San Diego Intermediate Software Development MOOC team
 * @student Dmitry Sergeev, Minsk, Belarus.
 * Date: March 17, 2016
 * */
public class AirportMarker extends CommonMarker {
	
	public static List<SimpleLinesMarker> routes;
	public PImage photo;
	
	//public PShape pIcon = new Plane();
	//public Plane plane = new Plane();
	
	
	public AirportMarker(Feature city, PImage photo) {
		super(((PointFeature)city).getLocation(), city.getProperties());
		this.photo = photo;
		
	}
	

	
	@Override
	public void drawMarker(PGraphics pg, float x, float y) {
		pg.pushStyle();
		pg.imageMode(PConstants.CORNER);
		// The image is drawn in object coordinates, i.e. the marker's origin (0,0) is at its geo-location.
		photo.resize(15, 20);
		pg.image(photo, x - 11, y - 37);
		pg.popStyle();
		
   
	}

	@Override
	public void showTitle(PGraphics pg, float x, float y) {
		 // show rectangle with title
		
		// show routes
		
		
	}
	
}
