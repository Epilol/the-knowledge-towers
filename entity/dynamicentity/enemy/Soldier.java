package saga.progetto.tesi.entity.dynamicentity.enemy;

import static playn.core.PlayN.assets;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import playn.core.AssetWatcher;
import playn.core.Image;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import saga.progetto.tesi.core.reposity.DataQuality;
import saga.progetto.tesi.entity.Sprite;
import saga.progetto.tesi.entity.dynamicentity.Direction;
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.entity.dynamicentity.equip.EnemySoldierSword;
import saga.progetto.tesi.entity.staticentity.Page;
import saga.progetto.tesi.entity.staticentity.Potion;
import saga.progetto.tesi.entity.staticentity.Weapon;
import saga.progetto.tesi.gui.NPCBar;
import saga.progetto.tesi.map.GameMap;

public class Soldier extends Enemy
{
	private static final IDimension SOLDIER_SIZE = new Dimension(32, 32);
	private static final String SOLDIER_PATH = "images/characters/enemies/soldier.png";
	private static final int SOLDIER_LEVEL = 999;
	private static final float EXP_MODIFIER = 23.0f;
	private static final float SOLDIER_SPEED = 1.5f;
	private static final float TOTAL_LIFE = 6.0f;
	private float TOTAL_ENDURANCE = 18.0f;
	private static final int VIEW_DISTANCE = 250;
	private static final float MAGICAL_RESISTANCE = 0.1f;
	private static final int NUMBER_OF_PAGES = 3;
	private static Image soldierImage;
	
	public Soldier(float x, float y, Direction direction, Player player, GameMap map, boolean isKilled, String id)
	{
		super(x, y, player, SOLDIER_SIZE, map, isKilled, id, TOTAL_LIFE);
		setSprite(new Sprite(soldierImage, getFrameDuration(), SOLDIER_SIZE.width(), SOLDIER_SIZE.height()));
		setTotalEndurance(TOTAL_ENDURANCE);
		setCurrentEndurance(TOTAL_ENDURANCE);
		setBar(new NPCBar(this, SOLDIER_SIZE));
		setLastDirection(direction);
		getSprite().setLastDirection(direction);
		setWeapon(new EnemySoldierSword(x, y, map, player.getLevel() + 1));
		getWeapon().setActive(false);
		setViewDistance(VIEW_DISTANCE);
		initDropRates();
	}
	
	public Soldier(float x, float y, Direction direction, Player player, GameMap map)
	{
		this(x, y, direction, player, map, false, "");
	}

	public static void loadAssets(AssetWatcher watcher) 
	{
		soldierImage = assets().getImage(SOLDIER_PATH);
		watcher.add(soldierImage);
	}

	@Override
	public int getLevel()
	{
		return SOLDIER_LEVEL;
	}
	
	@Override
	public float getExpGiven()
	{
		return EXP_MODIFIER;
	}
	
	@Override
	public float getDefaultSpeed()
	{
		return SOLDIER_SPEED;
	}
	
	@Override
	public IDimension getSize()
	{
		return SOLDIER_SIZE;
	}
	
	public float getMagicResist()
	{
		return MAGICAL_RESISTANCE;
	}
	
	public void initDropRates()
	{
		addDropRate("page1", 1.0f);
		addDropRate("page2", 0.8f);
		addDropRate("page3", 0.6f);
	}
	
	@Override
	public void checkDrops()
	{
		float value = new Random().nextFloat();
		float golds = 0.0f;
		List<Page> pages = new LinkedList<Page>();
		List<Weapon> weapons = new LinkedList<Weapon>();
		List<Potion> potions = new LinkedList<Potion>();
		Random chance = new Random();
		float currentChance = 0.5f;
		DataQuality domain = DataQuality.TOVALIDATE;
		
		for (int i = 0; i < NUMBER_OF_PAGES; i ++)
		{
			if (getDropRates().get("page" + (i + 1)) >= value)
			{
				if (chance.nextFloat() < currentChance)
				{
					domain = DataQuality.NEGATIVE;
					currentChance -= 0.1f;
				}
				
				else
					currentChance += 0.1f;
				
				String mapId = getMap().getAnnotation().getRandomElement(domain);
				pages.add(new Page(getMap().getGame(), i, domain, getMap().getAnnotation().getValueElement(domain, mapId), mapId));
			}
		}
		
		initBag(potions, weapons, pages, golds);
	}
	
	@Override
	public void setHasBeenKilled(boolean hasBeenKilled)
	{
		super.setHasBeenKilled(hasBeenKilled);
		if (hasBeenKilled)
			getMap().getData().getEnemiesInfo().put(getId(), "true");
	}
	
	@Override
	public void update(int delta)
	{
		super.update(delta);
		if (getCurrentEndurance() < getTotalEndurance())
			setCurrentEndurance(getCurrentEndurance() + 0.05f);
	}
}
