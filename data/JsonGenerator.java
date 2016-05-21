package saga.progetto.tesi.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import saga.progetto.tesi.data.MapSerializer.MapData;

public class JsonGenerator
{
	private static final String MAP_PATH = "maps/";
	private static JsonGenerator manager;
//	private static final int NUMBER_OF_TOWERS = 10;
//	private static final int NUMBER_OF_ROOMS = 13;

	private List<String> maps = new ArrayList<String>();
	
//	public class JsonGenerator{public JsonGenerator(int daeliminarelol){}}
	
	public JsonGenerator()
	{
//		for (int towerId = 1; towerId <= NUMBER_OF_TOWERS; towerId++)
//			for (int roomId = 0; roomId <= NUMBER_OF_ROOMS + 2; roomId++)
//			{
//				String mapId = "";
//				
//				if (roomId == 0)
//					mapId = "start" + towerId;
//				
//				else if (roomId == NUMBER_OF_ROOMS + 1)
//					mapId = towerId + "towerboss";
//				
//				else if (roomId == NUMBER_OF_ROOMS + 2)
//					mapId = towerId + "towerend";
//				
//				else
//					mapId = towerId + "tower" + roomId;
//					
//				maps.add(mapId + ".txt");
//			}
		
		generateTower(1, 8);
		generateTower(2, 11);
		generateTower(3, 10);
		generateTower(4, 12);
		generateTower(5, 15);
		generateTower(6, 13);
		generateTower(7, 10);
		generateTower(8, 10);
		generateTower(9, 10);
		generateTower(10, 10);
	}
	
	private void generateTower(int towerId, int rooms)
	{
		for (int roomId = 0; roomId <= rooms + 2; roomId++)
		{
			String mapId = "";
			
			if (roomId == 0)
				mapId = "start" + towerId;
			
			else if (roomId == rooms + 1)
				mapId = towerId + "towerboss";
			
			else if (roomId == rooms + 2)
				mapId = towerId + "towerend";
			
			else
				mapId = towerId + "tower" + roomId;
				
			maps.add(mapId + ".txt");
		}
	}
	
	public static JsonGenerator getInstance()
	{
		if (manager == null)
			manager = new JsonGenerator();
		return manager;
	}

	public void initJson()
	{
		int i = 0;
		for (String map : maps)
			generateJson(i++, map);
	}
	
	private void generateJson(int mapNumber, String mapName)
	{
		BufferedReader br = null;
		BufferedWriter bw = null;
		MapSerializer serializer = new MapSerializer();
		MapData map = serializer.makeMap();
		Map<String, String> locations = new HashMap<String, String>();
		Map<String, String> directions = new HashMap<String, String>();
		Map<String, String> npcs = new HashMap<String, String>();
		Map<String, String> enemies = new HashMap<String, String>();
		Map<String, String> enemiesInfo = new HashMap<String, String>();
		Map<String, String> pages = new HashMap<String, String>();
		Map<String, String> pagesInfo = new HashMap<String, String>();
		Map<String, String> books = new HashMap<String, String>();
		Map<String, String> booksInfo = new HashMap<String, String>();
		Map<String, String> chests = new HashMap<String, String>();
		Map<String, String> chestsInfo = new HashMap<String, String>();
		List<String> cells = new LinkedList<String>();

		try
		{
			br = new BufferedReader(new InputStreamReader(new FileInputStream(MAP_PATH + mapName), "UTF-8"));
			String line = "";
			map.setMapId(br.readLine());
			
			while (!(line = br.readLine()).equals("***"))
			{
				String[] info = line.split("\t");
				locations.put(info[0], info[1] + "\t" + info[2] + "\t" + info[3]);
				directions.put(info[0], info[4]);
			}
			
			while (!(line = br.readLine()).equals("***"))
			{
				String[] info = line.split("\t");
				npcs.put(info[0], info[1] + "\t" + info[2] + "\t" + info[3] + "\t" + info[4] + "\t" + info[5]);
			}

			while (!(line = br.readLine()).equals("***"))
			{
				String[] info = line.split("\t");
				enemies.put(info[0], info[1] + "\t" + info[2] + "\t" + info[3] + "\t" + info[4] + "\t" + info[5]);
				enemiesInfo.put(info[0], info[6]);
			}
			
			while (!(line = br.readLine()).equals("***"))
			{
				String[] info = line.split("\t");

				if (info[0].contains("page"))
				{
					pages.put(info[0], info[1] + "\t" + info[2]);
					pagesInfo.put(info[0], info[3]);
				}
				
				if (info[0].contains("book"))
				{
					books.put(info[0], info[1] + "\t" + info[2] + "\t" + info[3]);
					booksInfo.put(info[0], info[4]);
				}
				
				if (info[0].contains("special_chest"))
					map.setSpecialChest(line);
				
				else if (info[0].contains("chest"))
				{
					String chestInfo = "";
					for (int i = 1; i < info.length - 2; i++)
						chestInfo += info[i] + "\t";
					chestInfo += info[info.length-2];
					chests.put(info[0], chestInfo);
					chestsInfo.put(info[0], info[info.length-1]);
				}
			}

			while ((line = br.readLine()) != null)
				cells.add(line);
			
			map.setLocations(locations);
			map.setDirection(directions);
			map.setNpcs(npcs);
			map.setEnemies(enemies);
			map.setEnemiesInfo(enemiesInfo);
			map.setPages(pages);
			map.setPagesInfo(pagesInfo);
			map.setBooks(books);
			map.setBooksInfo(booksInfo);
			map.setChests(chests);
			map.setChestsInfo(chestsInfo);
			map.setCells(cells);
			
			String json = serializer.serializeToJson(map);
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("json/json_map" + mapNumber + ".txt"), "UTF-8"));
			bw.write(json);
		}

		catch (IOException e)
		{
			e.printStackTrace();
		}

		finally
		{
			try
			{
				if (br != null)
					br.close();
				if (bw != null)
					bw.close();
			}

			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}

