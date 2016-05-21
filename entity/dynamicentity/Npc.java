package saga.progetto.tesi.entity.dynamicentity;

import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;
import java.util.LinkedList;
import java.util.List;
import org.jbox2d.dynamics.BodyType;
import playn.core.AssetWatcher;
import playn.core.Image;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import pythagoras.f.IPoint;
import pythagoras.f.Point;
import saga.progetto.tesi.entity.Animation;
import saga.progetto.tesi.entity.Sprite;
import saga.progetto.tesi.entity.dynamicentity.Direction;
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.entity.dynamicentity.enemy.Path;
import saga.progetto.tesi.map.GameMap;

public class Npc extends GroupRenderedDynamicEntity
{
	private static final IDimension NPC_SIZE = new Dimension(32, 32);
	private static final String NPC_PATH = "images/characters/npc/npc";
	private static final int NPC_NUMBER = 2;
	private static final float NPC_SPEED = 1.0f;
	private static final float DEPTH = 5.0f;
	private static List<Image> npcImages;
	
	private IPoint frameLocation;
	private Player player;
	private Path path;
	private String id;
	private boolean isPathStart = true;
	
	public Npc(float x, float y, Direction direction, Player player, GameMap map, String id)
	{
		super(x, y, NPC_SIZE, map, DEPTH);
		this.id = id;
		//TODO non collide con arma ma collide con player. Aumentare massa
		initPhysicalBody(BodyType.DYNAMIC, getSize().width(), getSize().height(), Material.LIVING, 0x0004, 0xFFFF & ~0x0008);
		this.player = player;
		setSprite(new Sprite(npcImages.get(Integer.parseInt(id.replaceAll("npc",""))), getFrameDuration(), NPC_SIZE.width(), NPC_SIZE.height()));
		setLastDirection(direction);
		getSprite().setLastDirection(direction);
		setSpeed(NPC_SPEED);
		path = new Path(new Point(x, y), new Point());
		frameLocation = new Point(graphics().width() / 2 - (int)map.getPlayer().x() + x(), 
				graphics().height() / 2 - (int)map.getPlayer().y() + y());
	}
	
	public static void loadAssets(AssetWatcher watcher) 
	{
		npcImages = new LinkedList<Image>();
		for (int i = 0; i < NPC_NUMBER; i++)
		{
			String currentPath = NPC_PATH + i + ".png";
			npcImages.add(assets().getImage(currentPath));
			watcher.add(npcImages.get(i));
		}
	}
	
	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	
	@Override
	public IPoint getFrameLocation()
	{
		return frameLocation;
	}

	public void setLastDirection(float x, float y)
	{
		if (x < x() && y < y())
			setLastDirection(Direction.TOP_LEFT);
		else if (x > x() && y < y())
			setLastDirection(Direction.TOP_RIGHT);
		else if (x < x() && y > y())
			setLastDirection(Direction.BOTTOM_LEFT);
		else if (x > x() && y > y())
			setLastDirection(Direction.BOTTOM_RIGHT);
		else if (y > y())
			setLastDirection(Direction.DOWN);
		else if (y < y())
			setLastDirection(Direction.UP);
		else if (x > x())
			setLastDirection(Direction.RIGHT);
		else if (x < x())
			setLastDirection(Direction.LEFT);
		else
			setLastDirection(Direction.DOWN);
		if (getLastDirection() != Direction.DEFAULT)
		{
			if (getCurrentAnimation() != Animation.ENEMY_ATTACK)
				setCurrentAnimation(Animation.RUN);
			getSprite().setLastDirection(getLastDirection());
		}
		else setCurrentAnimation(Animation.IDLE);
	}
	
	public Path getPath()
	{
		return path;
	}

	public void setPath(Path path)
	{
		this.path = path;
	}
	
	public void setEndPath(IPoint endPoint)
	{
		path.setEndPoint(endPoint);
	}
	
	public void setMovement()
	{
		if (path.getStartPoint().equals(path.getEndPoint()))
		{
			if (path.getStartPoint().x() != x() || path.getStartPoint().y() != y() && getLastDirection() != Direction.DEFAULT)
				setLastDirection(path.getStartPoint().x(), path.getStartPoint().y());
			else
			{
				setLastDirection(Direction.DEFAULT);
				getSprite().setLastDirection(Direction.DOWN);
				setCurrentAnimation(Animation.IDLE);
			}
		}

		else
		{
			if (isPathStart)
				setLastDirection(path.getEndPoint().x(), path.getEndPoint().y());
			else
				setLastDirection(path.getStartPoint().x(), path.getStartPoint().y());
			if (x() == path.getStartPoint().x() && y() == path.getStartPoint().y())
				isPathStart = true;
			else if (x() == path.getEndPoint().x() && y() == path.getEndPoint().y())
				isPathStart = false;
		}
	}
	
	@Override
	public IDimension getSize()
	{
		return NPC_SIZE;
	}
	
	@Override
	public void update(int delta)
	{
		super.update(delta);
		setMovement();
		frameLocation = new Point(graphics().width() / 2 - (int)player.x() + x(), 
				graphics().height() / 2 - (int)player.y() + y());
	}
}
