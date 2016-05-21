package saga.progetto.tesi.entity.dynamicentity.enemy;

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
import saga.progetto.tesi.entity.dynamicentity.equip.EnemyWardenSword;
import saga.progetto.tesi.entity.staticentity.Page;
import saga.progetto.tesi.entity.staticentity.Potion;
import saga.progetto.tesi.entity.staticentity.Weapon;
import saga.progetto.tesi.gui.NPCBar;
import saga.progetto.tesi.map.GameMap;

public abstract class Warden extends Enemy
{
	private static final IDimension WARDEN_SIZE = new Dimension(32.0f, 32.0f);
	private static final IDimension LAYER_SIZE = new Dimension(100.0f, 100.0f);
	private static final int WARDEN_LEVEL = 999; //6
	private static final float EXP_MODIFIER = 30.0f;
	private static final float WARDEN_SPEED = 2.0f;
	private float TOTAL_ENDURANCE = 40.0f;
	private static final int VIEW_DISTANCE = 220;
	private static final float MAGICAL_RESISTANCE = 0.15f;
	private static final int NUMBER_OF_PAGES = 4;

	public Warden(float x, float y, Direction direction, Player player, GameMap map, boolean hasBeenKilled, String id, float life)
	{
		super(x, y, player, WARDEN_SIZE, map, hasBeenKilled, id, life);
		setSprite(new Sprite(getImage(), getFrameDuration(), LAYER_SIZE.width(), LAYER_SIZE.height()));
		getSprite().setLastDirection(direction);
		setTotalEndurance(TOTAL_ENDURANCE);
		setCurrentEndurance(TOTAL_ENDURANCE);
		setBar(new NPCBar(this, WARDEN_SIZE));
		setLastDirection(direction);
		setWeapon(new EnemyWardenSword(x, y, map, true, player.getLevel() + 1));
		getWeapon().setActive(false);
		setViewDistance(VIEW_DISTANCE);
		initDropRates();
	}

	public static void loadAssets(AssetWatcher watcher) 
	{
		WardenEnemy.loadAssets(watcher);
		WardenBoss.loadAssets(watcher);
	}
	
	public abstract Image getImage();
	
	@Override
	public int getLevel()
	{
		return WARDEN_LEVEL;
	}
	
	@Override
	public float getExpGiven()
	{
		return EXP_MODIFIER;
	}
	
	public void initDropRates()
	{
//		addDropRate("golds", 1.0f);
		addDropRate("page1", 1.0f);
		addDropRate("page2", 0.8f);
		addDropRate("page3", 0.4f);
		addDropRate("page4", 0.1f);
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
	public float getDefaultSpeed()
	{
		return WARDEN_SPEED;
	}
	
	@Override
	public IDimension getSize()
	{
		return WARDEN_SIZE;
	}

	@Override
	public IDimension getLayerSize()
	{
		return LAYER_SIZE;
	}
	
	public float getMagicResist()
	{
		return MAGICAL_RESISTANCE;
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
			setCurrentEndurance(getCurrentEndurance() + 0.25f);
	}
}
