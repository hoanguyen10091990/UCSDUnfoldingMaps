package module4;

import de.fhpotsdam.unfolding.data.PointFeature;
import processing.core.PGraphics;

/** Implements a visual marker for ocean earthquakes on an earthquake map
 * 
 * @author UC San Diego Intermediate Software Development MOOC team
 * @author Your name here
 *
 */
public class OceanQuakeMarker extends EarthquakeMarker {
	
	public OceanQuakeMarker(PointFeature quake) {
		super(quake);
		
		// setting field in earthquake marker
		isOnLand = false;
	}
	

	@Override
	public void drawEarthquake(PGraphics pg, float x, float y) {
		// Drawing a centered square for Ocean earthquakes
		// DO NOT set the fill color.  That will be set in the EarthquakeMarker
		// class to indicate the depth of the earthquake.
		// Simply draw a centered square.
		
		// HINT: Notice the radius variable in the EarthquakeMarker class
		// and how it is set in the EarthquakeMarker constructor
		
		// TODO: Implement this method
		//pg.pushStyle();
		
		float radius = this.radius * 1.25f ;
		pg.rect(x-radius/2, y-radius/2, radius, radius);
		
		String time = this.getProperty("age").toString();
		
		// Draw x to marker in case of past day
		if (time.equalsIgnoreCase("Past Day") || time.equalsIgnoreCase("Past Hour")) {
			pg.line(x - radius/2, y - radius/2, x + radius/2, y + radius/2);
			pg.line(x + radius/2, y - radius/2, x - radius/2, y + radius/2);
		}
		
		//pg.popStyle();
	}
	


	

}
