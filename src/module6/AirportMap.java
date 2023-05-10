package module6;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.data.ShapeFeature;
import de.fhpotsdam.unfolding.marker.AbstractShapeMarker;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.MultiMarker;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;
import de.fhpotsdam.unfolding.utils.MapUtils;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import de.fhpotsdam.unfolding.geo.Location;
import parsing.ParseFeed;
import processing.core.PApplet;

/** An applet that shows airports (and routes)
 * on a world map.  
 * @author Adam Setters and the UC San Diego Intermediate Software Development
 * MOOC team
 *
 */
public class AirportMap extends PApplet {
	
	UnfoldingMap map;
	
	private List<Marker> airportList;
	private List<Marker> countryMarkers;
	List<Marker> routeList;
	private List<Marker> clickedCountry;
	
	private Marker lastSelected;
	
	
	
	// URL of country file
	private String countryFile = "countries.geo.json";
	
	public void setup() {
		// setting up PAppler
		size(800,600, OPENGL);
		
		// setting up map and default events
		map = new UnfoldingMap(this, 50, 50, 750, 550);
		MapUtils.createDefaultEventDispatcher(this, map);
		
		// Get features from country data
		List<Feature> countries = GeoJSONReader.loadData(this, countryFile);
		countryMarkers = MapUtils.createSimpleMarkers(countries);
		
		// get features from airport data
		List<PointFeature> features = ParseFeed.parseAirports(this, "airports.dat");
		
		// List for clicked country 
		clickedCountry = new ArrayList<Marker>();
		
		// list for markers, hashmap for quicker access when matching with routes
		airportList = new ArrayList<Marker>();
		HashMap<Integer, Location> airports = new HashMap<Integer, Location>();
		
		// create markers from features
		for(PointFeature feature : features) {
			AirportMarker m = new AirportMarker(feature);
	
			m.setRadius(5);
			m.setProperty("id", feature.getId());
			// Hidden all airport markers as default
			m.setHidden(true);
			airportList.add(m);
			
			// put airport in hashmap with OpenFlights unique id for key
			airports.put(Integer.parseInt(feature.getId()), feature.getLocation());
		
		}
		
		
		// parse route data
		List<ShapeFeature> routes = ParseFeed.parseRoutes(this, "routes.dat");
		routeList = new ArrayList<Marker>();
		for(ShapeFeature route : routes) {
			
			// get source and destination airportIds
			int source = Integer.parseInt((String)route.getProperty("source"));
			int dest = Integer.parseInt((String)route.getProperty("destination"));
			
			// get locations for airports on route
			if(airports.containsKey(source) && airports.containsKey(dest)) {
				route.addLocation(airports.get(source));
				route.addLocation(airports.get(dest));
			}
			
			SimpleLinesMarker sl = new SimpleLinesMarker(route.getLocations(), route.getProperties());
		
			//System.out.println(sl.getProperties());
			//System.out.println(sl.getLocation());
			//UNCOMMENT IF YOU WANT TO SEE ALL ROUTES
			routeList.add(sl);
		}
		
		
		
		//UNCOMMENT IF YOU WANT TO SEE ALL ROUTES
		//map.addMarkers(routeList);
		
		map.addMarkers(airportList);
		
	}
	
	public void draw() {
		background(0);
		map.draw();
		
	}
	
	public void mouseMoved()
	{
		// clear the last selection
		if (lastSelected != null) {
			lastSelected.setSelected(false);
			
			lastSelected = null;
			displayAirportForCountries();
		
		}
		selectMarkerIfHover(airportList);
		
		
		
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
			if (marker.isInside(map,  mouseX, mouseY) && !marker.isHidden()) {
				lastSelected = marker;
				marker.setSelected(true);
				hideAll();
				setDisplayRoute() ;
				return;
			}
		}
		
	}
	
	// Set list of display route marker
	private void setDisplayRoute() {
		HashMap<String, Marker> displayList = new HashMap<String, Marker>();
		if (lastSelected != null) {
			String id = lastSelected.getStringProperty("id");
			for (Marker marker : routeList) {
				if(marker.getProperty("source").equals(id)) {
					ScreenPosition currPos = map.getScreenPosition(lastSelected.getLocation());
					Location loc = ((SimpleLinesMarker)marker).getLocations().get(1);
					ScreenPosition pos = map.getScreenPosition(loc);
					pos.x = pos.x - currPos.x;
					pos.y = pos.y - currPos.y;
					((AirportMarker)lastSelected).addScreenPos(pos);
					//AirportMarker.routes.add((SimpleLinesMarker) marker);
					// Add destination airport for display when hover
					getDestinationAirports (marker.getProperty("destination").toString(), displayList);
					
				}
		
			}
			displayList.put(id, lastSelected);
			// Display destination airport when hover
			displayDestinationAirports(displayList);
		}
		
	}
	
	private void displayDestinationAirports(HashMap<String, Marker> map) {
			for (String key : map.keySet()) {
				map.get(key).setHidden(false);
			}

	}
	
	private void getDestinationAirports(String des, HashMap<String, Marker> map) {
		//HashMap<String, Marker> display = new HashMap<String, Marker>();
		for (Marker m : airportList) {
			String id = m.getStringProperty("id");
			//System.out.println(m.getStringProperty("id") + ": " + des);
			if (id.equals(des) || m == lastSelected ) {
				map.put(id, m);
			} 
		}
		//return display;
	}
	
	
	@Override
	public void mouseClicked() {
		// Check the clicked is in country or not
		
			checkCountriesForClicked();
			// Display all airports of clicked countries 
			displayAirportForCountries();
	//		for (Marker m : clickedCountry) {
	//			System.out.println(m);
	//		}
		
	}
	
	// Helper method for checking countries per mouse clicked
	private void checkCountriesForClicked() {
		
		for (Marker marker : countryMarkers) {
			if (marker.isInside(map, mouseX, mouseY)) {
				// If clicked pos is inside a country marker
				// Check whether it is in clickedCountryList or not
				// System.out.println(marker);
				if (clickedCountry.contains(marker)) {
					// Remove this country from the list
					clickedCountry.remove(marker);
				} else {
					// Add this country to the list
					clickedCountry.add(marker); 
				}
			}
			
		}
	}
	
	// Helper method for displaying all airports in clicked country 
	private void displayAirportForCountries() {
		//Get should display airports
		List<Marker> displayAirports = getAirports();
		// Hide all current method
		hideAll();
		// Then redisplay
		for (Marker airport : displayAirports) {
				airport.setHidden(false);
		}
			
	}
	
	private void hideAll() {
		for (Marker m : airportList) {
			m.setHidden(true);
		}
	}
		
	
	
	// Helper method for getting all a airport list with given country name
	private List<Marker> getAirports() {
		List<Marker> airports = new ArrayList<Marker>();
		// For each country in clickedCountry
		for (Marker country : clickedCountry) { 
		//System.out.println(m.getStringProperty("country"));
		// For each airport 
			for (Marker airport : airportList) {
				// Check location of curr airport is inside the country or not
				//System.out.println(isInCountry(airport, country));
				if (isInCountry(airport, country)) {
				// add to airport
				airports.add(airport);
				}
			}
		}
		return airports;
	}
	
	// Helper method for checking whether airport is in country or not
	private boolean isInCountry(Marker airport, Marker country) {
		// getting location of feature
		Location checkLoc = airport.getLocation();

		// some countries represented it as MultiMarker
		// looping over SimplePolygonMarkers which make them up to use isInsideByLoc
		if(country.getClass() == MultiMarker.class) {
				
			// looping over markers making up MultiMarker
			for(Marker marker : ((MultiMarker)country).getMarkers()) {
					
				// checking if inside
				if(((AbstractShapeMarker)marker).isInsideByLocation(checkLoc)) {
					//airport.addProperty("country", country.getProperty("name"));
						
					// return if is inside one
					return true;
				}
			}
		}
			
		// check if inside country represented by SimplePolygonMarker
		else if(((AbstractShapeMarker)country).isInsideByLocation(checkLoc)) {
			//airport.addProperty("country", country.getProperty("name"));
			
			return true;
		}
		return false;
	}

}
