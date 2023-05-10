package module5;

import java.util.ArrayList;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import processing.core.PGraphics;

/** Implements a visual marker for ocean earthquakes on an earthquake map
 * 
 * @author UC San Diego Intermediate Software Development MOOC team
 * @author Your name here
 *
 */
public class OceanQuakeMarker extends EarthquakeMarker {
	//list of cities impacted by oceanquakes; set from EarthQuakeMarker object
	private ArrayList<ScreenPosition> citiesByOq; 
	private UnfoldingMap map;
	
	public OceanQuakeMarker(PointFeature quake) {
		super(quake);
		citiesByOq = new ArrayList<ScreenPosition>();
		
		// setting field in earthquake marker
		isOnLand = false;
	}
	

	/** Draw the earthquake as a square */
	@Override
	public void drawEarthquake(PGraphics pg, float x, float y) {
		
		pg.rect(x-radius, y-radius, 2*radius, 2*radius);
		
		// For each cities in citiesByOq set line from x
		for (ScreenPosition pos : citiesByOq) {
			pg.line(x, y, x+pos.x, y+pos.y);
		}
	}
	
	public void setAffectedCity(ScreenPosition city) {
		this.citiesByOq.add(city);
	}
	
	public void clearAffectedCity() {
		this.citiesByOq.clear();
	}
	
	public ArrayList<ScreenPosition> getAffectedCity() {
		return this.citiesByOq;
	}
	
	public void setMap(UnfoldingMap map) {
		this.map = map;
	}

	

}
