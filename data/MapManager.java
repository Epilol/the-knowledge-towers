package saga.progetto.tesi.data;

import java.util.LinkedList;
import java.util.List;
import saga.progetto.tesi.data.MapSerializer.MapData;

public class MapManager
{
	private List<MapData> maps;
	private static MapManager manager;

	private MapManager()
	{
		maps = new LinkedList<MapData>();
	}
	
	public static MapManager getInstance()
	{
		if (manager == null)
			manager = new MapManager();
		return manager;
	}
	
	public void addMap(MapData data)
	{
		maps.add(data);
	}
	
	public MapData getMap(String id)
	{
		for (MapData map : maps)
			if (map.getMapId().equals(id))
				return map;
		
		return null;
	}

}
