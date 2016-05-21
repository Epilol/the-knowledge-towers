package saga.progetto.tesi.map;

import saga.progetto.tesi.core.TheKnowledgeTowers;
import saga.progetto.tesi.data.MapSerializer.MapData;
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.map.cell.Cell;
import saga.progetto.tesi.map.cell.FifthTowerDecoration;
import saga.progetto.tesi.map.cell.FifthTowerFloor;
import saga.progetto.tesi.map.cell.FifthTowerWall;
import saga.progetto.tesi.navigable.Gameloop;

public class FifthTowerMap extends GameMap
{
	public static final int HEIGHT = 35;
	public static final int WIDTH = 35;
	
	public FifthTowerMap(Player player, TheKnowledgeTowers game, String mapId, String previousMap, Gameloop gameloop)
	{
		super(player, game, mapId, gameloop);
		mapInit(game.getMapData(mapId), previousMap);
	}
	
	@Override
	public int getWidth()
	{
		return WIDTH;
	}

	@Override
	public int getHeight()
	{
		return HEIGHT;
	}
	
	@Override
	protected void mapInit(MapData data, String previousMap)
	{
		super.mapInit(data, previousMap);
		
		for (String entry : data.getCells())
		{
			// x \t y \t isAvailable \t hasShadow \t index_x \t index_y \t hasPhysics \t isSecretPassage \t isDecoration
			String[] info = entry.split("\t");
			Cell cell = null;

			if (Boolean.parseBoolean(info[8]))
			{
				cell = new FifthTowerDecoration(Float.parseFloat(info[0]) + getWidthOffset(), Float.parseFloat(info[1]) + getHeightOffset(), this);
				getWalls().add(cell);				
			}

			else if (Boolean.parseBoolean(info[2]))
				cell = new FifthTowerFloor(Float.parseFloat(info[0]) + getWidthOffset(), Float.parseFloat(info[1]) + getHeightOffset(), this);

			else
			{
				float offsetX = Float.parseFloat(info[4]);
				float offsetY = Float.parseFloat(info[5]);
				
				if (offsetX + offsetY == 0)
					cell = new FifthTowerWall(Float.parseFloat(info[0]) + getWidthOffset(), Float.parseFloat(info[1]) + getHeightOffset(), this, true);
				
				else
					cell = new FifthTowerWall(Float.parseFloat(info[0]) + getWidthOffset(), Float.parseFloat(info[1]) + getHeightOffset(), this);
				
				getWalls().add(cell);
			}

			cell.gfxInit(Float.parseFloat(info[4]), Float.parseFloat(info[5]));
			cell.setActive(Boolean.parseBoolean(info[6]));
			getBackground()[(int) ((Float.parseFloat(info[0]) + getWidthOffset()) / Cell.WIDTH)]
					[(int) ((Float.parseFloat(info[1]) + getWidthOffset()) / Cell.HEIGHT)] = cell;

			if (Boolean.parseBoolean(info[7]))
				secretWallInit(cell);

			else if (Boolean.parseBoolean(info[6]))
				wallBgInit(cell);

			else
				floorBgInit(cell);
		}

		updateBackground(getFloorLayer());
		updateBackground(getWallLayer());
		updateBackground(getSecretWallLayer());
//		updateBackground(getThiefAbilityLayer());
		setLoaded(true);
	}
}
