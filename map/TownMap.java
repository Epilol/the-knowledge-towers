package saga.progetto.tesi.map;

//\tfalse\tfalse\t0\t0\ttrue
import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;
import java.util.Map;
import playn.core.AssetWatcher;
import playn.core.Image;
import playn.core.ImageLayer;
import pythagoras.f.Point;
import saga.progetto.tesi.core.TheKnowledgeTowers;
import saga.progetto.tesi.data.MapSerializer.MapData;
import saga.progetto.tesi.entity.dynamicentity.Direction;
import saga.progetto.tesi.entity.dynamicentity.Npc;
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.map.cell.Cell;
import saga.progetto.tesi.map.cell.OutsideDecoration;
import saga.progetto.tesi.map.cell.OutsideFloor;
import saga.progetto.tesi.map.cell.OutsideWall;
import saga.progetto.tesi.navigable.Gameloop;

public class TownMap extends GameMap
{
	private static final int WIDTH = 67;
	private static final int HEIGHT = 67;
	private static final String TOWN0_PATH = "images/decoration/town0.png";
	private static Image town0Image;
	
	private ImageLayer decorationLayer;
	
	public TownMap(Player player, TheKnowledgeTowers game, String mapId, String previousMap, Gameloop gameLoop)
	{
		super(player, game, mapId, gameLoop);
		mapInit(game.getMapData(mapId), previousMap);
		
		if (mapId.equals("town0"))
			decorationLayer = graphics().createImageLayer(town0Image);
		
		decorationLayer.setTranslation(450.0f, 425.0f);
		getFloorLayer().add(decorationLayer);
	}
	
	public static void loadAssets(AssetWatcher watcher)
	{
		town0Image = assets().getImage(TOWN0_PATH);
		watcher.add(town0Image);
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
		
		for (Map.Entry<String, String> entry : data.getNpcs().entrySet())
		{
			String[] info = entry.getValue().split("\t");
			Direction direction = Direction.DEFAULT;

			if (info[4].equals("UP"))
				direction = Direction.UP;

			if (info[4].equals("RIGHT"))
				direction = Direction.RIGHT;

			if (info[4].equals("DOWN"))
				direction = Direction.DOWN;

			if (info[4].equals("LEFT"))
				direction = Direction.LEFT;

			Npc npc = new Npc(Float.parseFloat(info[0]) + getWidthOffset(), Float.parseFloat(info[1]) + getHeightOffset(), 
					direction, getPlayer(), this, entry.getKey());
			npc.setEndPath(new Point(Float.parseFloat(info[2]) + getWidthOffset(), Float.parseFloat(info[3]) + getWidthOffset()));
			getNpcs().add(npc);
		}
		
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
				float offsetX = Float.parseFloat(info[4]);
				float offsetY = Float.parseFloat(info[5]);
				
				if (offsetX + offsetY == 0)
					cell = new OutsideWall(Float.parseFloat(info[0]) + getWidthOffset(), Float.parseFloat(info[1]) + getHeightOffset(), this, true);
				
				else
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
	
	@Override
	public void update(int delta)
	{
		super.update(delta);
		if (getPlayer().x() <= getWidthOffset() - Cell.WIDTH || getPlayer().x() >= (STANDARD_SIZE.width() * Cell.WIDTH) + getWidthOffset() - 2 * Cell.WIDTH ||
				getPlayer().y() <= getHeightOffset() - Cell.HEIGHT || getPlayer().y() >= (STANDARD_SIZE.height() * Cell.HEIGHT) + getHeightOffset() - 3 * Cell.HEIGHT)
		{
			getPlayer().getMap().getLocations().clear();
			getPlayer().getMap().getDirections().clear();
			getGameloop().setVisible(false);
			getGameloop().setPreviousMap(getGameloop().getCurrentMap());
			getGameloop().setCurrentMap(getMapId());
			getGameloop().loadNextMap("outside0");
		}
	}
}
