package module6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import processing.core.PConstants;
import processing.core.PGraphics;

/** 
 * A class to represent AirportMarkers on a world map.
 *   
 * @author Adam Setters and the UC San Diego Intermediate Software Development
 * MOOC team
 *
 */
public class AirportMarker extends CommonMarker{
	//public static List<SimpleLinesMarker> routes;
	private List<ScreenPosition> destLocations;
	
	public AirportMarker(Feature city) {
		super(((PointFeature)city).getLocation(), city.getProperties());
		destLocations = new ArrayList<ScreenPosition>();
	}
	
	// Setter
	public void addScreenPos(ScreenPosition pos) {
		destLocations.add(pos);
	}
	
	@Override
	public void drawMarker(PGraphics pg, float x, float y) {
		pg.fill(0, 0,255);
		pg.ellipse(x, y, 5, 5);
		
		
	}

	@Override
	public void showTitle(PGraphics pg, float x, float y) {
		// Get info of Airport
		String country = this.getStringProperty("country");
		String name = this.getStringProperty("name");
		String city = this.getStringProperty("city");
		String code = this.getStringProperty("code");
		String title = name + "-" + code + " " + city + "-" + country;
		//System.out.println(this.getLocation());
		 // show rectangle with title
		pg.pushStyle();
		
		pg.rectMode(PConstants.CORNER);
		
		pg.stroke(110);
		pg.fill(255,255,255);
		pg.rect(x, y + 15, pg.textWidth(title) +6, 18, 5);
		
		pg.textAlign(PConstants.LEFT, PConstants.TOP);
		pg.fill(0);
		pg.text(title, x + 3 , y +18);
		
		
		pg.popStyle();
		
		// show routes
		
		for (ScreenPosition pos : destLocations) {
			
			pg.line(x, y, x+pos.x, y+pos.y);
		}
		
	}
	
}
