package saga.progetto.tesi.map;

import static playn.core.PlayN.graphics;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;
import playn.core.AssetWatcher;
import playn.core.GroupLayer;
import playn.core.Key;
import playn.core.Layer;
import playn.core.Keyboard.Event;
import playn.core.Mouse.ButtonEvent;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import pythagoras.f.IPoint;
import pythagoras.f.Point;
import saga.progetto.tesi.core.ServerConnection;
import saga.progetto.tesi.core.TheKnowledgeTowers;
import saga.progetto.tesi.core.annotation.Annotation;
import saga.progetto.tesi.core.annotation.AnnotationProtocol;
import saga.progetto.tesi.core.reposity.DataQuality;
import saga.progetto.tesi.data.MapSerializer.MapData;
import saga.progetto.tesi.entity.Animation;
import saga.progetto.tesi.entity.dynamicentity.Direction;
import saga.progetto.tesi.entity.dynamicentity.Npc;
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.entity.dynamicentity.enemy.Angel;
import saga.progetto.tesi.entity.dynamicentity.enemy.Devil;
import saga.progetto.tesi.entity.dynamicentity.enemy.Emeralda;
import saga.progetto.tesi.entity.dynamicentity.enemy.Farebell;
import saga.progetto.tesi.entity.dynamicentity.enemy.BlackKnight;
import saga.progetto.tesi.entity.dynamicentity.enemy.Enemy;
import saga.progetto.tesi.entity.dynamicentity.enemy.GoldKnight;
import saga.progetto.tesi.entity.dynamicentity.enemy.Guard;
import saga.progetto.tesi.entity.dynamicentity.enemy.Lucius;
import saga.progetto.tesi.entity.dynamicentity.enemy.Merciless;
import saga.progetto.tesi.entity.dynamicentity.enemy.Nemesis;
import saga.progetto.tesi.entity.dynamicentity.enemy.Newbie;
import saga.progetto.tesi.entity.dynamicentity.enemy.OldWarrior;
import saga.progetto.tesi.entity.dynamicentity.enemy.Rivy;
import saga.progetto.tesi.entity.dynamicentity.enemy.Saphir;
import saga.progetto.tesi.entity.dynamicentity.enemy.Setian;
import saga.progetto.tesi.entity.dynamicentity.enemy.Siegfried;
import saga.progetto.tesi.entity.dynamicentity.enemy.SilverKnight;
import saga.progetto.tesi.entity.dynamicentity.enemy.Soldier;
import saga.progetto.tesi.entity.dynamicentity.enemy.WardenBoss;
import saga.progetto.tesi.entity.dynamicentity.enemy.WardenEnemy;
import saga.progetto.tesi.entity.dynamicentity.enemy.Yojimbo;
import saga.progetto.tesi.entity.dynamicentity.pet.Pet;
import saga.progetto.tesi.entity.dynamicentity.spell.Spell;
import saga.progetto.tesi.entity.staticentity.Book;
import saga.progetto.tesi.entity.staticentity.Chest;
import saga.progetto.tesi.entity.staticentity.Corpse;
import saga.progetto.tesi.entity.staticentity.Gold;
import saga.progetto.tesi.entity.staticentity.Heart;
import saga.progetto.tesi.entity.staticentity.Item;
import saga.progetto.tesi.entity.staticentity.LifePotion;
import saga.progetto.tesi.entity.staticentity.ManaPotion;
import saga.progetto.tesi.entity.staticentity.Page;
import saga.progetto.tesi.entity.staticentity.PhysicsPage;
import saga.progetto.tesi.entity.staticentity.Potion;
import saga.progetto.tesi.entity.staticentity.SpecialChest;
import saga.progetto.tesi.entity.staticentity.Potion.PotionType;
import saga.progetto.tesi.entity.staticentity.StaticEntity;
import saga.progetto.tesi.entity.staticentity.StorableDrop;
import saga.progetto.tesi.entity.staticentity.Weapon;
import saga.progetto.tesi.entity.staticentity.Weapon.WeaponType;
import saga.progetto.tesi.gui.PopupTooltip;
import saga.progetto.tesi.gui.PopupWindow;
import saga.progetto.tesi.map.SpawnPoint.Location;
import saga.progetto.tesi.map.cell.Cell;
import saga.progetto.tesi.media.Text;
import saga.progetto.tesi.navigable.Gameloop;

public abstract class GameMap
{
	public static final int MAP_OFFSET = 26;
	public static final float PTM_RATIO = 32.0f;
	public static final int NUMBER_OF_TOWERS = 10;
	protected static final IDimension STANDARD_SIZE = new Dimension(35.0f, 35.0f);
	private final static int BOSS_PAGE_REQUIRED = 5;
	
	private TheKnowledgeTowers game;
	private MapContactListener contactListener = new MapContactListener();
	private Gameloop gameloop;
	private StaticEntity[][] map;
	private GroupLayer floorLayer;
	private GroupLayer wallLayer;
	private GroupLayer secretWallLayer;
//	private GroupLayer thiefAbilityLayer;
	private Player player;
	private SpecialChest specialChest;
	private List<Body> bodies;
	private List<Cell> walls;
	private List<Npc> npcs;
	private List<Enemy> enemies;
	private List<StorableDrop> drops;
	private StorableDrop currentDrop;
	private List<Heart> hearts;
	private List<Text> hitNumbers;
	private World world;
	private MapData data;
	private String mapId;
	private SpawnPoint zoningLocations;
	private boolean isLoaded;
	private boolean keepBossMessageHidden;
	private boolean bossMessageShown;
	private boolean startMessageShown;
	
	public GameMap(Player player, TheKnowledgeTowers game, String mapId, Gameloop gameLoop)
	{
		this.player = player;
		this.game = game;
		this.mapId = mapId;
		this.gameloop = gameLoop;
		bodies = new LinkedList<Body>();
		world = new World(new Vec2(0, 0));
		world.setContactListener(contactListener);
		map = new StaticEntity[getHeight()][getWidth()];
		floorLayer = graphics().createGroupLayer();
		floorLayer.setVisible(false);
		floorLayer.setDepth(-3.0f);
		wallLayer = graphics().createGroupLayer();
		wallLayer.setVisible(false);
		wallLayer.setDepth(2.0f);
		secretWallLayer = graphics().createGroupLayer();
		secretWallLayer.setVisible(false);
		secretWallLayer.setDepth(6.0f);
//		thiefAbilityLayer = graphics().createGroupLayer();
//		thiefAbilityLayer.setVisible(false);
//		thiefAbilityLayer.setDepth(-2.0f);
		graphics().rootLayer().add(floorLayer);
		graphics().rootLayer().add(wallLayer);
		graphics().rootLayer().add(secretWallLayer);
//		graphics().rootLayer().add(thiefAbilityLayer);
		npcs = new LinkedList<Npc>();
		enemies = new LinkedList<Enemy>();
		drops = new LinkedList<StorableDrop>();
		hitNumbers = new LinkedList<Text>();
		walls = new LinkedList<Cell>();
		hearts = new LinkedList<Heart>();
	}

	public static void loadAssets(AssetWatcher watcher)
	{
		TownMap.loadAssets(watcher);
		OutsideMap.loadAssets(watcher);
		EndMap.loadAssets(watcher);
	}
	
	// ritorna la matrice contenente la mappa di gioco.
	public StaticEntity[][] getBackground()
	{
		return map;
	}

	public TheKnowledgeTowers getGame()
	{
		return game;
	}

	public Gameloop getGameloop()
	{
		return gameloop;
	}
	
	// inizializza la mappa di gioco.
	protected void mapInit(MapData data, String previousMap)
	{
		this.data = data;
		IPoint previousLocation = new Point();

		if (mapId.equals("1tower1") && gameloop.isFirstRoom())
		{
			PopupWindow.getInstance().show("Kill the enemies to get experience points and steal knowledge.");
			gameloop.setFirstRoom(false);
		}
		
		else if(mapId.contains("boss"))
			PopupWindow.getInstance().show("Kill the boss to enter the Knowledge Room and learn new abilities.");
		
		SpawnPoint spawnPoint = new SpawnPoint();

		for (Map.Entry<String, String> location : data.getLocations().entrySet())
		{
			String[] point = location.getValue().split("\t");
			Location newLocation = spawnPoint.new Location(new Point(Float.parseFloat(point[0]) + getWidthOffset(), 
					Float.parseFloat(point[1]) + getHeightOffset()), Boolean.parseBoolean(point[2]));
			spawnPoint.addLocation(location.getKey(), newLocation);

			if (data.getDirection().get(location.getKey()).equals("UP"))
				spawnPoint.addDirection(location.getKey(), Direction.UP);

			if (data.getDirection().get(location.getKey()).equals("RIGHT"))
				spawnPoint.addDirection(location.getKey(), Direction.RIGHT);

			if (data.getDirection().get(location.getKey()).equals("DOWN"))
				spawnPoint.addDirection(location.getKey(), Direction.DOWN);

			if (data.getDirection().get(location.getKey()).equals("LEFT"))
				spawnPoint.addDirection(location.getKey(), Direction.LEFT);

			if (previousMap.equals(location.getKey()))
				previousLocation = newLocation.getPoint();
		}

		setSpawn(spawnPoint);

		if (player != null)
		{
			player.setMap(this);
			player.setNewMap(previousLocation.x(), previousLocation.y(), world);
		}

		else
			player = new Player(previousLocation.x(), previousLocation.y(), 0, 0, this);

		player.setLastDirection(getDirections().get(previousMap));

		//Enemies
		for (Map.Entry<String, String> entry : data.getEnemies().entrySet())
		{
			String[] info = entry.getValue().split("\t");
			Direction direction = Direction.DEFAULT;

			if (info[4].equals("UP"))
				direction = Direction.UP;

			else if (info[4].equals("RIGHT"))
				direction = Direction.RIGHT;

			else if (info[4].equals("DOWN"))
				direction = Direction.DOWN;

			else if (info[4].equals("LEFT"))
				direction = Direction.LEFT;
			
			Enemy enemy = null;
			
			boolean enemyInfo = Boolean.parseBoolean(data.getEnemiesInfo().get(entry.getKey()));
			
			if (entry.getKey().contains("angel"))
			{
				enemy = new Angel(Float.parseFloat(info[0]) + getWidthOffset(), Float.parseFloat(info[1]) + getHeightOffset(), 
						direction, player, this, enemyInfo, entry.getKey());
				
				enemyInfo = false;
			}
			
			else if (entry.getKey().contains("black_knight"))
			{
				enemy = new BlackKnight(Float.parseFloat(info[0]) + getWidthOffset(), Float.parseFloat(info[1]) + getHeightOffset(), 
						direction, player, this, enemyInfo, entry.getKey());
				
				enemyInfo = false;
			}
			
			if (entry.getKey().contains("devil"))
			{
				enemy = new Devil(Float.parseFloat(info[0]) + getWidthOffset(), Float.parseFloat(info[1]) + getHeightOffset(), 
						direction, player, this, enemyInfo, entry.getKey());
				
				enemyInfo = false;
			}
			
			if (entry.getKey().contains("emeralda"))
			{
				enemy = new Emeralda(Float.parseFloat(info[0]) + getWidthOffset(), Float.parseFloat(info[1]) + getHeightOffset(), 
						direction, player, this, enemyInfo, entry.getKey());
				
				enemyInfo = false;
			}
			
			else if (entry.getKey().contains("farebell"))
			{
				enemy = new Farebell(Float.parseFloat(info[0]) + getWidthOffset(), Float.parseFloat(info[1]) + getHeightOffset(), 
						direction, player, this, enemyInfo, entry.getKey());
				
				enemyInfo = false;
			}
			
			else if (entry.getKey().contains("gold_knight"))
			{
				enemy = new GoldKnight(Float.parseFloat(info[0]) + getWidthOffset(), Float.parseFloat(info[1]) + getHeightOffset(), 
						direction, player, this, enemyInfo, entry.getKey());
				
				enemyInfo = false;
			}
			
			else if (entry.getKey().contains("guard"))
			{
				enemy = new Guard(Float.parseFloat(info[0]) + getWidthOffset(), Float.parseFloat(info[1]) + getHeightOffset(), 
						direction, player, this, enemyInfo, entry.getKey());
				
				if (mapId.equals("1tower4"))
					enemy.sleep(-1.0f);
				
				enemyInfo = false;
			}
			
			else if (entry.getKey().contains("lucius"))
			{
				enemy = new Lucius(Float.parseFloat(info[0]) + getWidthOffset(), Float.parseFloat(info[1]) + getHeightOffset(), 
						direction, player, this, enemyInfo, entry.getKey());
				
				enemyInfo = false;
			}
			
			else if (entry.getKey().contains("merciless"))
			{
				enemy = new Merciless(Float.parseFloat(info[0]) + getWidthOffset(), Float.parseFloat(info[1]) + getHeightOffset(), 
						direction, player, this, enemyInfo, entry.getKey());
				
				enemyInfo = false;
			}
			
			else if (entry.getKey().contains("nemesis"))
			{
				enemy = new Nemesis(Float.parseFloat(info[0]) + getWidthOffset(), Float.parseFloat(info[1]) + getHeightOffset(), 
						direction, player, this, enemyInfo, entry.getKey());
				
				enemyInfo = false;
			}
			
			else if (entry.getKey().contains("newbie"))
			{
				enemy = new Newbie(Float.parseFloat(info[0]) + getWidthOffset(), Float.parseFloat(info[1]) + getHeightOffset(), 
						direction, player, this, enemyInfo, entry.getKey());
				
				enemyInfo = false;
			}
			
			else if (entry.getKey().contains("oldwarrior"))
			{
				enemy = new OldWarrior(Float.parseFloat(info[0]) + getWidthOffset(), Float.parseFloat(info[1]) + getHeightOffset(), 
						direction, player, this, enemyInfo, entry.getKey());
				
				enemyInfo = false;
			}
			
			else if (entry.getKey().contains("rivy"))
			{
				enemy = new Rivy(Float.parseFloat(info[0]) + getWidthOffset(), Float.parseFloat(info[1]) + getHeightOffset(), 
						direction, player, this, enemyInfo, entry.getKey());
				
				enemyInfo = false;
			}
			
			else if (entry.getKey().contains("saphir"))
			{
				enemy = new Saphir(Float.parseFloat(info[0]) + getWidthOffset(), Float.parseFloat(info[1]) + getHeightOffset(), 
						direction, player, this, enemyInfo, entry.getKey());
				
				enemyInfo = false;
			}
			
			else if (entry.getKey().contains("setian"))
			{
				enemy = new Setian(Float.parseFloat(info[0]) + getWidthOffset(), Float.parseFloat(info[1]) + getHeightOffset(), 
						direction, player, this, enemyInfo, entry.getKey());
				
				enemyInfo = false;
			}
			
			else if (entry.getKey().contains("siegfried"))
			{
				enemy = new Siegfried(Float.parseFloat(info[0]) + getWidthOffset(), Float.parseFloat(info[1]) + getHeightOffset(), 
						direction, player, this, enemyInfo, entry.getKey());
				
				enemyInfo = false;
			}
			
			else if (entry.getKey().contains("silver_knight"))
			{
				enemy = new SilverKnight(Float.parseFloat(info[0]) + getWidthOffset(), Float.parseFloat(info[1]) + getHeightOffset(), 
						direction, player, this, enemyInfo, entry.getKey());
				
				enemyInfo = false;
			}
			
			else if (entry.getKey().contains("soldier"))
			{
				enemy = new Soldier(Float.parseFloat(info[0]) + getWidthOffset(), Float.parseFloat(info[1]) + getHeightOffset(), 
						direction, player, this, enemyInfo, entry.getKey());
				
				enemyInfo = false;
			}
			
			else if (entry.getKey().contains("warden_boss"))
			{
				enemy = new WardenBoss(Float.parseFloat(info[0]) + getWidthOffset(), Float.parseFloat(info[1]) + getHeightOffset(), 
						direction, player, this, enemyInfo, entry.getKey());
				
				enemyInfo = false;
			}
			
			else if (entry.getKey().contains("warden"))
			{
				enemy = new WardenEnemy(Float.parseFloat(info[0]) + getWidthOffset(), Float.parseFloat(info[1]) + getHeightOffset(), 
						direction, player, this, enemyInfo, entry.getKey());
				
				enemyInfo = false;
			}
			
			else if (entry.getKey().contains("yojimbo"))
			{
				enemy = new Yojimbo(Float.parseFloat(info[0]) + getWidthOffset(), Float.parseFloat(info[1]) + getHeightOffset(), 
						direction, player, this, enemyInfo, entry.getKey());
				
				enemyInfo = false;
			}
			
			if (!enemyInfo)
			{
				enemy.setEndPath(new Point(Float.parseFloat(info[2]) + getWidthOffset(), Float.parseFloat(info[3]) + getHeightOffset()));
				getEnemies().add(enemy);
			}
		}
		
		//Items
		for (Map.Entry<String, String> entry : data.getPages().entrySet())
		{
			if (gameloop.getItemInfo(entry.getKey()) == null)
				gameloop.addItem(entry.getKey(), false);
			
			if (!gameloop.getItemInfo(entry.getKey()))
			{
				String[] info = entry.getValue().split("\t");
				PhysicsPage page = new PhysicsPage(Float.parseFloat(info[0]) + getWidthOffset(), Float.parseFloat(info[1]) + getHeightOffset(), 
						this, Boolean.parseBoolean(data.getPagesInfo().get(entry.getKey())), entry.getKey());
				DataQuality domain = new Random().nextBoolean() ? DataQuality.TOVALIDATE : DataQuality.NEGATIVE;
				String mapId = getAnnotation().getRandomElement(domain);
				page.addPage(new Page(game, 0, domain, getAnnotation().getValueElement(domain, mapId), mapId));
				page.initDrops();
				drops.add(page);
			}
		}

		for (Map.Entry<String, String> entry : data.getBooks().entrySet())
		{
			if (gameloop.getItemInfo(entry.getKey()) == null)
					gameloop.addItem(entry.getKey(), false);
					
			if (!gameloop.getItemInfo(entry.getKey()))
			{
				String[] info = entry.getValue().split("\t");
				Book book = new Book(Float.parseFloat(info[0]) + getWidthOffset(), Float.parseFloat(info[1]) + getHeightOffset(), 
						this, Boolean.parseBoolean(data.getBooksInfo().get(entry.getKey())), entry.getKey());
				List<Page> pages = new LinkedList<Page>();

				float currentChance = 0.5f;
				Random chance = new Random();
				
				for (int i = 0; i < Integer.parseInt(info[2]); i++)
				{
					DataQuality domain = DataQuality.TOVALIDATE;

					if (chance.nextFloat() < currentChance)
					{
						domain = DataQuality.NEGATIVE;
						currentChance -= 0.1f;
					}
					
					else
						currentChance += 0.1f;
					
					String mapId = getAnnotation().getRandomElement(domain);
					pages.add(new Page(game, i, domain, getAnnotation().getValueElement(domain, mapId), mapId));
				}
				
				book.addAllPages(pages);
				book.initDrops();
				drops.add(book);
			}
		}

		for (Map.Entry<String, String> entry : data.getChests().entrySet())
		{
			if (gameloop.getItemInfo(entry.getKey()) == null)
				gameloop.addItem(entry.getKey(), false);
			
			String[] info = entry.getValue().split("\t");
			Chest chest = new Chest(Float.parseFloat(info[0]) + getWidthOffset(), Float.parseFloat(info[1]) + getHeightOffset(), 
					this, Boolean.parseBoolean(data.getChestsInfo().get(entry.getKey())), gameloop.getItemInfo(entry.getKey()), entry.getKey());
			List<Page> pages = new LinkedList<Page>();
			List<Weapon> weapons = new LinkedList<Weapon>();
			List<Potion> potions = new LinkedList<Potion>();
			int j = 0;

			
			if (!gameloop.getItemInfo(entry.getKey()))
			{
				Random chance = new Random();
				float currentChance = 0.5f;
				
				for (int i = 0; i < Integer.parseInt(info[2]); i++)
				{
					DataQuality domain = DataQuality.TOVALIDATE;

					if (chance.nextFloat() < currentChance)
					{
						domain = DataQuality.NEGATIVE;
						currentChance -= 0.1f;
					}
					
					else
						currentChance += 0.1f;
					
					String mapId = getAnnotation().getRandomElement(domain);
					pages.add(new Page(game, j++, domain, getAnnotation().getValueElement(domain, mapId), mapId));
				}

				for (int i = 3; i < info.length; i++)
				{
					if (!chest.isTaken() && info[i].equals("sword"))
						weapons.add(new Weapon(WeaponType.SWORD, j++));
					
					else if (!chest.isTaken() && info[i].equals("dagger"))
						weapons.add(new Weapon(WeaponType.DAGGER, j++));
					
					else if (!chest.isTaken() && info[i].equals("spear"))
						weapons.add(new Weapon(WeaponType.SPEAR, j++));
					
					else if (!chest.isTaken() && info[i].equals("axe"))
						weapons.add(new Weapon(WeaponType.AXE, j++));
					
					else if (!chest.isTaken() && info[i].equals("minor_life"))
						potions.add(new LifePotion(PotionType.MINOR_LIFE_POTION, j++));
					
					else if (!chest.isTaken() && info[i].equals("life"))
						potions.add(new LifePotion(PotionType.LIFE_POTION, j++));
					
					else if (!chest.isTaken() && info[i].equals("major_life"))
						potions.add(new LifePotion(PotionType.MAJOR_LIFE_POTION, j++));
					
					else if (!chest.isTaken() && info[i].equals("minor_mana"))
						potions.add(new ManaPotion(PotionType.MINOR_MANA_POTION, j++));
					
					else if (!chest.isTaken() && info[i].equals("mana"))
						potions.add(new ManaPotion(PotionType.MANA_POTION, j++));
					
					else if (!chest.isTaken() && info[i].equals("major_mana"))
						potions.add(new ManaPotion(PotionType.MAJOR_MANA_POTION, j++));
					
					else if (!chest.isTaken() && info[i].equals("stealth"))
						potions.add(new ManaPotion(PotionType.STEALTH_POTION, j++));
					
					else if (!chest.isTaken())
						chest.addGolds(new Gold(Float.parseFloat(info[i]), j++));
					
				}
				chest.addAllPages(pages);
				chest.addAllWeapons(weapons);
				chest.addAllPotions(potions);
			}
			
			
			chest.initDrops();
			drops.add(chest);
		}
		
		if (data.getSpecialChest() != null)
		{
			String[] token = data.getSpecialChest().split("\t");
			String[] prize = new String[8];

			for (int i = 4; i < token.length; i++)
				prize[i-4] = token[i];

			specialChest = new SpecialChest(Float.parseFloat(token[1]), Float.parseFloat(token[2]), this, token[0], prize);
		}
	}

	public boolean isLoaded()
	{
		return isLoaded;
	}
	
	public void setLoaded(boolean isLoaded)
	{
		this.isLoaded = isLoaded;
	}
	
	public boolean isSpecialChestOpen()
	{
		if (specialChest != null && specialChest.isOpen())
			return true;
		return false;
	}
	
	public boolean specialChestIntersection(ButtonEvent event)
	{
		if (specialChest != null)
			return specialChest.intersectsIcon(event);
		
		return false;
	}

	public abstract int getWidth();
	
	public abstract int getHeight();
	
	public float getWidthOffset()
	{
		return (getWidth() - 35) / 2 * Cell.WIDTH;
	}

	public float getHeightOffset()
	{
		return (getHeight() - 35) / 2 * Cell.WIDTH;
	}
	
	// inizializza una cella normale.
	protected void floorBgInit(Cell cell)
	{
		floorLayer.add(cell.getLayer());
	}

	protected void wallBgInit(Cell cell)
	{
		wallLayer.add(cell.getLayer());
	}

	// inizializza una cella che contiene un passaggio segreto.
	protected void secretWallInit(Cell cell)
	{
		secretWallLayer.add(cell.getLayer());
	}
	
	protected List<Cell> getWalls()
	{
		return walls;
	}
	
	protected GroupLayer getFloorLayer()
	{
		return floorLayer;
	}
	
	protected GroupLayer getWallLayer()
	{
		return wallLayer;
	}

	protected GroupLayer getSecretWallLayer()
	{
		return secretWallLayer;
	}

//	public GroupLayer getThiefAbilityLayer()
//	{
//		return thiefAbilityLayer;
//	}
	
	protected void updateBackground(Layer layer)
	{
		layer.setTranslation(graphics().width() / 2 - (float) Math.floor(player.x()),
				graphics().height() / 2 - (float) Math.floor(player.y()));
	}
	
	protected void updateBackground(Layer layer, IPoint earthquakeValue)
	{
		layer.setTranslation(graphics().width() / 2 - (float) Math.floor(player.x()) + earthquakeValue.x(),
				graphics().height() / 2 - (float) Math.floor(player.y()) + earthquakeValue.y());
	}

	public Player getPlayer()
	{
		return player;
	}

	public StorableDrop getCurrentDrop()
	{
		return currentDrop;
	}

	public void increaseDropIndex()
	{
		currentDrop.setIndex(currentDrop.getIndex() < currentDrop.getItems().size() - 1 ? currentDrop.getIndex() + 1 : currentDrop.getItems().size() - 1);
	}

	public void decreaseDropIndex()
	{
		currentDrop.setIndex(currentDrop.getIndex() > 0 ? currentDrop.getIndex() - 1 : 0);
	}

	public MapContactListener getContactListener()
	{
		return contactListener;
	}

	public World getWorld()
	{
		return world;
	}

	public void setWorld(World world)
	{
		this.world = world;
	}

	public boolean toNextMap(IPoint location, Direction mapDirection)
	{
		if (mapDirection == null)
			return false;
		
		if (mapDirection == Direction.UP)
			return player.x() >= location.x() - Cell.WIDTH && player.x() <= location.x() + Cell.WIDTH &&
				player.y() >= location.y() + MAP_OFFSET && player.y() <= location.y() + 135;
		
		if (mapDirection == Direction.RIGHT)
			return player.x() <= location.x() - MAP_OFFSET && player.x() >= location.x() - 135 &&
				player.y() >= location.y() - Cell.HEIGHT && player.y() <= location.y() + Cell.HEIGHT;
					
		if (mapDirection == Direction.DOWN)
			return player.x() >= location.x() - Cell.WIDTH && player.x() <= location.x() + Cell.WIDTH &&
				player.y() <= location.y() - MAP_OFFSET && player.y() >= location.y() - 135;
			
//		if (mapDirection == Direction.LEFT)
		return player.x() >= location.x() + MAP_OFFSET && player.x() <= location.x() + 135 &&
			player.y() >= location.y() - Cell.HEIGHT && player.y() <= location.y() + Cell.HEIGHT;
	}

	public boolean inRange(IPoint playerPoint, Enemy enemy)
	{
		if (!player.isHidden())
		{
			// inseguimento di base
			if (Math.abs(playerPoint.x() - enemy.x()) < enemy.getViewDistance()
					&& Math.abs(playerPoint.y() - enemy.y()) < enemy.getViewDistance()
					&& (enemy.isFollowing() || player.isSprinting() || player.isBerserking() || player.isAttacking()))
			{
				player.resetOutOfCombat();
				return true;
			}

			// nemico guarda su
			else if (enemy.getLastDirection() == Direction.UP && enemy.y() - playerPoint.y() > 0
					&& enemy.y() - playerPoint.y() < enemy.getViewDistance()
					&& Math.abs(playerPoint.x() - enemy.x()) < enemy.getViewDistance() / 2)
			{
				player.resetOutOfCombat();
				return true;
			}

			// nemico guarda a sinistra
			else if (enemy.getLastDirection() == Direction.LEFT && enemy.x() - playerPoint.x() > 0
					&& enemy.x() - playerPoint.x() < enemy.getViewDistance()
					&& Math.abs(playerPoint.y() - enemy.y()) < enemy.getViewDistance() / 2)
			{
				player.resetOutOfCombat();
				return true;
			}

			// nemico guarda giù
			else if ((enemy.getLastDirection() == Direction.DOWN || enemy.getLastDirection() == Direction.DEFAULT)
					&& playerPoint.y() - enemy.y() > 0 && playerPoint.y() - enemy.y() < enemy.getViewDistance()
					&& Math.abs(playerPoint.x() - enemy.x()) < enemy.getViewDistance() / 2)
			{
				player.resetOutOfCombat();
				return true;
			}

			// nemico guarda a destra
			else if (enemy.getLastDirection() == Direction.RIGHT && playerPoint.x() - enemy.x() > 0
					&& playerPoint.x() - enemy.x() < enemy.getViewDistance()
					&& Math.abs(playerPoint.y() - enemy.y()) < enemy.getViewDistance() / 2)
			{
				player.resetOutOfCombat();
				return true;
			}
		}
		return false;
	}
	
	public boolean inRange(Enemy enemy, Pet pet)
	{
		if (!player.isHidden())
		{
			// inseguimento di base
			if (Math.abs(enemy.x() - pet.x()) < pet.getViewDistance()
					&& Math.abs(enemy.y() - pet.y()) < pet.getViewDistance())
			{
				player.resetOutOfCombat();
				return true;
			}
		}
		return false;
	}

	public boolean isClose(IPoint playerPoint, IPoint enemyPoint)
	{
		return (Math.abs(playerPoint.x() - enemyPoint.x()) < 1.0f || Math.abs(playerPoint.y() - enemyPoint.y()) < 1.0f);
	}

	public boolean isInAttackRange(IPoint playerPoint, Enemy enemy)
	{
		return Math.abs(playerPoint.x() - enemy.x()) <= enemy.getSize().width() + enemy.getWeapon().getSize().width() / 2
				&& Math.abs(playerPoint.y() - enemy.y()) <= enemy.getSize().height() + enemy.getWeapon().getSize().height() / 2;
	}

	public List<Body> getBodies()
	{
		return bodies;
	}
	
	public List<Npc> getNpcs()
	{
		return npcs;
	}

	public List<Enemy> getEnemies()
	{
		return enemies;
	}

	public List<Text> getHitNumbers()
	{
		return hitNumbers;
	}

	public List<StorableDrop> getStorableDrops()
	{
		return drops;
	}
	
	public List<Heart> getHearts()
	{
		return hearts;
	}

	public void addHeart(Heart heart)
	{
		hearts.add(heart);
	}

	public void addHearts(List<Heart> hearts)
	{
		this.hearts.addAll(hearts);
	}

	public Annotation getAnnotation()
	{
		return game.getAnnotation();
	}

	public AnnotationProtocol getAnnotationProtocol()
	{
		return getGame().getAnnotationProtocol();
	}
	
	public ServerConnection getServerConnection()
	{
		return getGame().getServerConnection();
	}
	
	public String getMapId()
	{
		return mapId;
	}

	public Map<String, Location> getLocations()
	{
		return zoningLocations.getLocations();
	}
	
	public Map<String, Direction> getDirections()
	{
		return zoningLocations.getDirections();
	}
	
	public void setSpawn(SpawnPoint locations)
	{
		this.zoningLocations = locations;
	}
	
	public void setMapType()
	{
		if (!mapId.contains("end") && (mapId.contains("tower") || mapId.contains("magmatron")))
			player.setInsideTower(true);
		
		else
			player.setInsideTower(false);
	}

	public void showBossMessage()
	{
		bossMessageShown = true;
		PopupWindow.getInstance().show("You need to collect at least " + BOSS_PAGE_REQUIRED + " pages to open your way to the next room." +
									" You currently have only " + player.getPages().size() + " pages");
	}
	
	public void showStartMessage()
	{
		gameloop.setHasShowedTowerPopup(true);
		startMessageShown= true;
		PopupWindow.getInstance().show("The "+ getGameloop().getTowerTitle() + " boss hides somewhere in the tower. Find him to release the knowledge!");
	}
	
	public void destroyBossMessage()
	{
		bossMessageShown = false;
		PopupWindow.getInstance().hide();
	}
	
	public void destroyStartMessage()
	{
		startMessageShown = false;
		PopupWindow.getInstance().hide();
	}
	
	public void setVisible(boolean visible)
	{
		floorLayer.setVisible(visible);
		wallLayer.setVisible(visible);
		secretWallLayer.setVisible(visible);
		
//		// passiva ladro
//		if (player.getCurrentJob() instanceof Thief)
//			thiefAbilityLayer.setVisible(visible);
//		
//		else if (thiefAbilityLayer.visible())
//			thiefAbilityLayer.setVisible(false);
		
		for (Npc npc : npcs)
			npc.setVisible(visible);
		
		for (Enemy enemy : enemies)
			enemy.setVisible(visible);

		for (StorableDrop drop : drops)
			if (!(drop instanceof Corpse))
				drop.setVisible(visible);
		
		if (specialChest != null)
			specialChest.setVisible(visible);

		for (Cell wall : walls)
			wall.setVisible(visible);

		for (Text text : hitNumbers)
			text.setVisible(visible);
		
		player.setVisible(visible);
	}
	
	public boolean visible()
	{
		return floorLayer.visible();
	}

	public void clear()
	{
		floorLayer.destroy();
		wallLayer.destroy();
		secretWallLayer.destroy();
//		thiefAbilityLayer.destroy();
		
		Iterator<Body> bodyIterator = bodies.iterator();
		while (bodyIterator.hasNext())
		{
			Body body = bodyIterator.next();
			world.destroyBody(body);
			bodyIterator.remove();
		}

		for (StorableDrop drop : new LinkedList<StorableDrop>(drops))
		{
			if (drop.isTaken())
				drop.setTaken();
			
			for (Item page : drop.getItems())
				if (page instanceof Page && page.isSeen())
				{
					boolean isCorrect = page.isValidation();
					
					if (!isCorrect)
					{
						player.getStatistic().setTrueNegative(player.getStatistic().getTrueNegative() + 1);
						player.increaseNegativeDiscarded();
					}
					page.send(false, getAnnotationProtocol());
				}
			
			if (!(drop instanceof Corpse))
			drop.clear();
		}
		

		if (specialChest != null)
			specialChest.clear();
		
		Iterator<Npc> npcIterator = npcs.iterator();
		while (npcIterator.hasNext())
		{
			Npc npc = npcIterator.next();
			npc.clear();
			npcIterator.remove();
		}

		Iterator<Enemy> enemyIterator = enemies.iterator();
		while (enemyIterator.hasNext())
		{
			Enemy enemy = enemyIterator.next();
			enemy.clear();
			enemyIterator.remove();
		}

		for (Heart heart : new LinkedList<Heart>(hearts))
			heart.clear();

		Iterator<Cell> wallIterator = walls.iterator();
		while (wallIterator.hasNext())
		{
			Cell wall = wallIterator.next();
			wall.clear();
			wallIterator.remove();
		}

		for (int i = 0; i < getHeight(); i++)
			for (int j = 0; j < getWidth(); j++)
			{
				map[i][j].getLayer().destroy();
				map[i][j].clear();
			}

		Arrays.fill(map, null);

		Iterator<Text> textIterator = hitNumbers.iterator();
		while (textIterator.hasNext())
		{
			Text text = textIterator.next();
			text.destroy();
			textIterator.remove();
		}
		
		player.clear();
	}

	public void display()
	{
		floorLayer.setVisible(true);
		wallLayer.setVisible(true);
		secretWallLayer.setVisible(true);
		
//		if (player.getCurrentJob() instanceof Thief)
//			thiefAbilityLayer.setVisible(true);
		
		player.setVisible(true);
		
		for (Npc npc : npcs)
			npc.setVisible(true);
		
		for (Enemy enemy : enemies)
			enemy.setVisible(true);
		
		for (StorableDrop drop : drops)
			drop.setVisible(true);

		if (specialChest != null)
			specialChest.setVisible(true);
		
		if (mapId.contains("start") && !gameloop.hasShowedTowerPopup())
			showStartMessage();
	}

	public MapData getData()
	{
		return data;
	}
	
	public boolean popupShown()
	{
		return bossMessageShown || startMessageShown;
	}
	
	public SpecialChest getSpecialChest()
	{
		return specialChest;
	}
	
	public void specialChestMouseListener(ButtonEvent event)
	{
		if (specialChest != null && specialChest.visible())
			specialChest.mouseListener(event);
	}

	public void specialChestKeyboardListener(Event event)
	{
		if (specialChest != null && specialChest.visible())
			specialChest.keyboardListener(event);
	}
	
	public void keepBossMessageHidden()
	{
		keepBossMessageHidden = true;
	}
	
	// serve al GameLoop per aggiornare lo stato corrente della mappa di gioco.
	public void update(int delta)
	{
		world.step(1 / 60.0f, 8, 3);
		
		if (player.isEarthQuacking())
		{
			IPoint earthquakeValue = new Point(new Random().nextInt(player.getCurrentJob().getEarthquakeDistance()), 
					new Random().nextInt(player.getCurrentJob().getEarthquakeDistance()));
			updateBackground(floorLayer, earthquakeValue);
			updateBackground(wallLayer, earthquakeValue);
			updateBackground(secretWallLayer, earthquakeValue);
//			updateBackground(getThiefAbilityLayer(), earthquakeValue);
		}
		
		else
		{
			updateBackground(floorLayer);
			updateBackground(wallLayer);
			updateBackground(secretWallLayer);
//			updateBackground(getThiefAbilityLayer());
		}
		
		player.update(delta);
		boolean attacked = false;
		
		for (Map.Entry<String, Location> location : new HashSet<Map.Entry<String, Location>>(getLocations().entrySet()))
		{
			Direction direction = getDirections().get(location.getKey());
			
			if (keepBossMessageHidden && location.getKey().contains("boss") &&
					(player.x() > location.getValue().getPoint().x() + 40.0f || player.x() < location.getValue().getPoint().x() + 40.0f) &&
					(player.y() > location.getValue().getPoint().y() + 40.0f || player.y() < location.getValue().getPoint().y() - 40.0f))
				keepBossMessageHidden = false;
				
			
			if (bossMessageShown && location.getKey().contains("boss") &&
					(player.x() > location.getValue().getPoint().x() + 20.0f || player.x() < location.getValue().getPoint().x() + 20.0f) &&
					(player.y() > location.getValue().getPoint().y() + 20.0f || player.y() < location.getValue().getPoint().y() - 20.0f))
				destroyBossMessage();
				
			if (toNextMap(location.getValue().getPoint(), direction) && location.getValue().isAccessible() && !player.isDead() && 
					!player.inCombat() && location.getKey().contains("boss") && (getPlayer().getPages().size() + getPlayer().getUsedPages()) < BOSS_PAGE_REQUIRED
					&& !bossMessageShown && !keepBossMessageHidden)
				showBossMessage();
			
			else if (!bossMessageShown && !player.inCombat() && toNextMap(location.getValue().getPoint(), direction) && 
					location.getValue().isAccessible() && !player.isDead())
			{
				if (player.hasWeapon())
					player.getWeapon().destroyPhysics();
				
				getLocations().clear();
				getDirections().clear();
				setVisible(false);
				gameloop.setVisible(false);
				gameloop.setPreviousMap(gameloop.getCurrentMap());
				gameloop.setCurrentMap(location.getKey());

				if (location.getKey().contains("towerend"))
				{
					player.getStatistic().updateReliability();
					game.getAnnotationProtocol().sendEnd(player.getStatistic().hasPassed(), player.getStatistic().getReliability());
					gameloop.loadNextMap(location.getKey());
					getPlayer().setUsedPages(0);
				}
				
				else if (gameloop.getPreviousMap().contains("outside") && gameloop.getPreviousMap().contains("start"))
					gameloop.loadSynset();
				
				else
					gameloop.loadNextMap(location.getKey());
			}
		}
		
		boolean isNearItem = false;

		for (StorableDrop drop : new LinkedList<StorableDrop>(drops))
		{
			drop.update(delta);

			if (contactListener.isColliding(player.getBody(), drop.getBody()))
			{
				isNearItem = true;
				
				if (currentDrop == null && !player.isHidden() && !drop.isOpen() && !player.inCombat())
				{
					if (player.isPicking())
					{
						currentDrop = drop;
						PopupTooltip.getInstance().hide();
						player.setLastDirection(Direction.DEFAULT);
						player.getSprite().setLastDirection(player.getFacingDirection());
						
						if (!(currentDrop instanceof Corpse))
						{
							currentDrop.open(true);
							currentDrop.select(0);
							
							if (mapId.contains("1tower") && gameloop.isFirstItem())
								PopupWindow.getInstance().show("Gather pages related with the concept by pressing SPACE or ENTER. Leave the bad images or discard them by pressing R");
							
							gameloop.setFirstItem(false);
							
							for (Item page : currentDrop.getItems())
								if (page instanceof Page && !((Page) page).isSeen() && player.canCarryItem())
								{
									((Page) page).setSeen(true);
									boolean isCorrect = ((Page) page).isValidation();
									
									if (isCorrect)
										player.getStatistic().setPositiveImageShown(player.getStatistic().getPositiveImageShown() + 1);
									
									else
										player.getStatistic().setNegativeImageShown(player.getStatistic().getNegativeImageShown() + 1);
								}
							
						}
						
						else
						{
							for (Item page : currentDrop.getItems())
								if (player.canCarryPage())
									player.addPage((Page) page);
							
							currentDrop.removePages();
							currentDrop.clear();
							currentDrop = null;
							gameloop.setCorpse(null);
							
							if (gameloop.getNewGameMenu().get("SCROLL").visible())
								gameloop.getNewGameMenu().get("SCROLL").setVisible(true);
						}
					}
					
					else if (!PopupTooltip.getInstance().visible() && !PopupWindow.getInstance().visible() && !drop.isEmpty())
					{
						String[] tokenized = drop.getClass().toString().split("\\.");
						PopupTooltip.getInstance().show(getMapId().contains("1tower") ? "press SPACE to open" :
								tokenized[tokenized.length - 1].replace("Physics", ""));
					}
				}
			}

			if (!isNearItem && PopupTooltip.getInstance().visible())
				PopupTooltip.getInstance().hide();
				
			if (currentDrop != null && (currentDrop.isEmpty() || game.getKeyboard().contains(Key.ESCAPE)))
			{
				if (currentDrop.isEmpty())
					currentDrop.setTaken();

				currentDrop.open(false);
				
				if (currentDrop.isEmpty() && !(currentDrop instanceof Chest))
					currentDrop.clear();
				
				currentDrop = null;
				game.getKeyboard().remove(Key.ESCAPE);
				
				if (getGameloop().getNewGameMenu().get("SCROLL").visible())
					getGameloop().scrollMenu();
			}
		}
		

		if (specialChest != null)
		{
			specialChest.update(delta);
			
			if (contactListener.isColliding(player.getBody(), specialChest.getBody()))
			{
				if(!player.isHidden() && player.isPicking() && !specialChest.isOpen() && !player.inCombat())
				{
					Direction face = player.getFacingDirection();
					player.setLastDirection(Direction.DEFAULT);
					player.getSprite().setLastDirection(face);
					specialChest.open(true);
				}
			}
			
			if (specialChest.isActivated())
			{
				specialChest.clear();
				specialChest = null;
			}
		}
		
		for (Npc npc : new LinkedList<Npc>(npcs))
		{
			npc.update(delta);
			// si ferma quando collide con il player
			if (contactListener.isColliding(player.getBody(), npc.getBody()))
			{
				npc.setLastDirection(Direction.DEFAULT);
				npc.setCurrentAnimation(Animation.IDLE);
			}
		}

		if (enemies.size() > 0 && player.inCombat())
			player.resetOutOfCombatTimer();
			
		for (Enemy enemy : new LinkedList<Enemy>(enemies))
		{
			enemy.update(delta);
			// attacchi fisici del player
			if (player.hasWeapon() && !enemy.isCycloned() && contactListener.isColliding(player.getWeaponBody(), enemy.getBody()))
			{
				if (player.getCurrentRage() < player.getTotalRage() && !player.isWolf())
					player.setCurrentRage(player.getCurrentRage() + player.getTotalRage() / 4);
				
				if (player.getWeapon().hasPoison())
				{
					enemy.takePoisonDamage(player.getWeapon().getPoisonDamage(), player.getWeapon().getPoisonTime());
					player.getWeapon().setPoison(false);
					attacked = true;
				}
				
				if (player.getWeapon().hasSleep())
				{
					enemy.sleep(player.getWeapon().getSleepTime());
					player.getWeapon().setSleep(false);
				}
				
				else if (!player.getWeapon().hasPoison())
				{
					player.meleeAttack(enemy);
					attacked = true;
				}
				
				if (player.isExecuting())
					player.getCurrentJob().setExecute(false);
			}

			// il player non può toccare il nemico se è stelthato o viene
			// destelthato
			if (player.isHidden() && contactListener.isColliding(player.getBody(), enemy.getBody()) && player.isHidden())
				player.setHidden(false);
			
			if (enemy.isSleeping() && player.isSprinting())
				enemy.awake();
			
			// attacchi magici del player
			for (Spell spell : new LinkedList<Spell>(player.getOffensiveSpells()))
			{
				if (!enemy.isCycloned() && (contactListener.isColliding(spell.getBody(), enemy.getBody())
						|| contactListener.isColliding(spell.getBody(), enemy.getWeaponBody())))
				{
					spell.applyEffect(enemy);
					attacked = true;
				}
			}

			// attacco dei nemici
			if (!enemy.isCrowdControlled() && enemy.hasEndurance())
			{
				if (player.getPet() != null && contactListener.isColliding(enemy.getWeaponBody(), player.getPet().getBody()))
					enemy.attack(false, player.getPet());
				
				
				else if (contactListener.isColliding(enemy.getWeaponBody(), player.getBody()))
					enemy.attack(player.isDefending() && player.shieldCollision(contactListener, enemy), player);
			}

			if (enemy.isDead() && !enemy.getDeadCheck())
			{
				enemy.setDeadCheck(true);
				data.getEnemiesInfo().put(enemy.getId(), "true");
			}
			
			if (enemy.isStunned() || enemy.isFeared() || enemy.isCycloned())
				player.resetOutOfCombat();
		}

		player.setAttacking(attacked);
		
		List<Text> currentHits = new LinkedList<Text>(hitNumbers);
		for (Text text : currentHits)
		{
			text.getLayer().setAlpha(text.getLayer().alpha() - 0.01f);
			text.getLayer().setTranslation(text.getLayer().tx(), text.getLayer().ty() - 1.0f);
			if (text.getLayer().alpha() <= 0)
			{
				text.destroy();
				hitNumbers.remove(text);
			}
		}

		currentHits.clear();
		
		for (Spell spell : player.getDefensiveSpells())
				spell.applyEffect();
		
		for (Cell wall : walls)
		{
			for (Spell spell : new LinkedList<Spell>(player.getOffensiveSpells()))
				if (contactListener.isColliding(wall.getBody(), spell.getBody()) && spell.destroyOnCollision())
					spell.clear();
		}

		for (Heart heart : new LinkedList<Heart>(hearts))
		{
			heart.update(delta);
			if (contactListener.isColliding(player.getBody(), heart.getBody()))
			{
				heart.setTaken(true);
				player.heal(heart.getType().getLife());
				hearts.remove(heart);
			}
		}
	}
}
