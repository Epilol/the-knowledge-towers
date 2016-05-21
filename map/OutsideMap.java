package saga.progetto.tesi.map;

import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;
import playn.core.AssetWatcher;
import playn.core.Image;
import playn.core.ImageLayer;
import saga.progetto.tesi.core.TheKnowledgeTowers;
import saga.progetto.tesi.data.MapSerializer.MapData;
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.map.cell.Cell;
import saga.progetto.tesi.map.cell.OutsideDecoration;
import saga.progetto.tesi.map.cell.OutsideFloor;
import saga.progetto.tesi.map.cell.OutsideWall;
import saga.progetto.tesi.navigable.Gameloop;

public class OutsideMap extends GameMap
{
	private static final int WIDTH = 67;
	private static final int HEIGHT = 67;
	private static final String OUTSIDE0_PATH = "images/decoration/outside0.png";
	private static Image outside0Image;
	
	private ImageLayer decorationLayer;
	
	public OutsideMap(Player player, TheKnowledgeTowers game, String mapId, String previousMap, Gameloop gameLoop)
	{
		super(player, game, mapId, gameLoop);
		mapInit(game.getMapData(mapId), previousMap);
		
		if (mapId.equals("outside0"))
			decorationLayer = graphics().createImageLayer(outside0Image);
		
		decorationLayer.setTranslation(555.0f, 410.0f);
		getFloorLayer().add(decorationLayer);
	}
	
	public static void loadAssets(AssetWatcher watcher)
	{
		outside0Image = assets().getImage(OUTSIDE0_PATH);
		watcher.add(outside0Image);
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
				cell = new OutsideDecoration(Float.parseFloat(info[0]) + getWidthOffset(), Float.parseFloat(info[1]) + getHeightOffset(), this);
				getWalls().add(cell);				
			}

			else if (Boolean.parseBoolean(info[2]))
				cell = new OutsideFloor(Float.parseFloat(info[0]) + getWidthOffset(), Float.parseFloat(info[1]) + getHeightOffset(), this);

			else
			{
				cell = new OutsideWall(Float.parseFloat(info[0]) + getWidthOffset(), Float.parseFloat(info[1]) + getHeightOffset(), this);
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
		
		for (int i = 0; i < WIDTH; i++)
		{
			for (int j = 0; j < HEIGHT; j++)
			{
				if (i < getWidthOffset() / Cell.WIDTH || i >= (STANDARD_SIZE.width() + getWidthOffset() / Cell.WIDTH) ||
						j < getHeightOffset() / Cell.HEIGHT || j >= (STANDARD_SIZE.height() + getHeightOffset() / Cell.HEIGHT))
	
				{
					Cell cell = new OutsideFloor(i * Cell.WIDTH, j * Cell.HEIGHT, this);
					cell.gfxInit(9, 0);
					cell.setActive(false);
					getBackground()[i][j] = cell;
					floorBgInit(cell);
				}
			}
		}
		
		updateBackground(getFloorLayer());
		updateBackground(getWallLayer());
		updateBackground(getSecretWallLayer());
//		updateBackground(getThiefAbilityLayer());
		setLoaded(true);
	}	
	
	@Override
	public void clear()
	{
		super.clear();
		decorationLayer.destroy();
	}
}
