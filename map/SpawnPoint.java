package saga.progetto.tesi.map;

import java.util.HashMap;
import java.util.Map;
import pythagoras.f.IPoint;
import saga.progetto.tesi.entity.dynamicentity.Direction;

public class SpawnPoint
{
	Map<String, Location> locations = new HashMap<String, Location>();
	Map<String, Direction> directions = new HashMap<String, Direction>();
	

	public class Location
	{
		private IPoint point;
		private boolean isAccessible;
		
		public Location(IPoint point, boolean isAccessible)
		{
			this.point = point;
			this.isAccessible = isAccessible;
		}
		
		public IPoint getPoint()
		{
			return point;
		}
		
		public void setPoint(IPoint point)
		{
			this.point = point;
		}
		
		public boolean isAccessible()
		{
			return isAccessible;
		}
		
		public void setAccessible(boolean isAccessible)
		{
			this.isAccessible = isAccessible;
		}
	}
	
	public void addLocation(String id, Location location)
	{
		locations.put(id, location);
	}
	
	public IPoint getLocation(String id)
	{
		return locations.get(id).getPoint();
	}
	
	public Map<String, Location> getLocations()
	{
		return locations;
	}
	
	public void addDirection(String id, Direction direction)
	{
		directions.put(id, direction);
	}
	
	public Direction getDirection(String id)
	{
		return directions.get(id);
	}
	
	public Map<String, Direction> getDirections()
	{
		return directions;
	}
	
	public void destroy()
	{
		locations.clear();
		directions.clear();
	}
}
