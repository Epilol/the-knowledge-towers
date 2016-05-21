package saga.progetto.tesi.data;

import java.util.List;
import java.util.Map;
import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.autobean.vm.AutoBeanFactorySource;

public class MapSerializer
{
//	private MapFactory mapFactory = GWT.create(MapFactory.class);
	private MapFactory mapFactory = AutoBeanFactorySource.create(MapFactory.class);
	
	public MapData makeMap()
	{
		AutoBean<MapData> map = mapFactory.map();
		return map.as();
	}
	
	public String serializeToJson(MapData map)
	{
		AutoBean<MapData> bean = AutoBeanUtils.getAutoBean(map);
		return AutoBeanCodex.encode(bean).getPayload();
	}

	public MapData deserializeFromJson(String json)
	{
		AutoBean<MapData> bean = AutoBeanCodex.decode(mapFactory, MapData.class, json);
		return bean.as();
	}
	
	public interface MapFactory extends AutoBeanFactory
	{
		AutoBean<MapData> map();
	}

	public interface MapData
	{
		String getMapId();
		void setMapId(String mapId);
		String getSpecialChest();
		void setSpecialChest(String specialChest);
		Map<String, String> getDirection();
		void setDirection(Map<String, String> direction);
		Map<String, String> getLocations();
		void setLocations(Map<String, String> locations);
		Map<String, String> getNpcs();
		void setNpcs(Map<String, String> npcs);
		Map<String, String> getEnemies();
		void setEnemies(Map<String, String> enemies);
		Map<String, String> getEnemiesInfo();
		void setEnemiesInfo(Map<String, String> enemiesInfo);
		Map<String, String> getPages();
		void setPages(Map<String, String> pages);
		Map<String, String> getPagesInfo();
		void setPagesInfo(Map<String, String> pagesInfo);
		Map<String, String> getBooks();
		void setBooks(Map<String, String> books);
		Map<String, String> getBooksInfo();
		void setBooksInfo(Map<String, String> booksInfo);
		Map<String, String> getChests();
		void setChests(Map<String, String> chests);
		Map<String, String> getChestsInfo();
		void setChestsInfo(Map<String, String> chestsInfo);
		List<String> getCells();
		void setCells(List<String> cells);
	}
}