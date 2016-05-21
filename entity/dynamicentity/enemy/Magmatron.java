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
import saga.progetto.tesi.entity.dynamicentity.equip.MagmatronUnarmed;
import saga.progetto.tesi.entity.staticentity.Page;
import saga.progetto.tesi.entity.staticentity.Potion;
import saga.progetto.tesi.entity.staticentity.Weapon;
import saga.progetto.tesi.entity.staticentity.Weapon.WeaponType;
import saga.progetto.tesi.gui.NPCBar;
import saga.progetto.tesi.map.GameMap;

public class Magmatron extends Enemy
{
	private static final IDimension MAGMATRON_SIZE = new Dimension(100, 50);
	private static final String MAGMATRON_PATH = "images/characters/enemies/magmatron.png";
	private static final int MAGMATRON_LEVEL = 10;
	private static final float EXP_MODIFIER = 20.0f;
	private static final float MAGMATRON_SPEED = 1.5f;
	private static final float TOTAL_LIFE = 15.0f;
	private float TOTAL_ENDURANCE = 10.0f;
	private static final int VIEW_DISTANCE = 400;
	private static final float MAGICAL_RESISTANCE = 0.5f;
	private static Image magmatronImage;
	private static float MIN_GOLDS = 350.0f;
	private static float MAX_GOLDS = 350.0f;
	private static float PAGE_NUMBER = 4.0f;

	public Magmatron(float x, float y, Direction direction, Player player, GameMap map, boolean hasBeenKilled, String id)
	{
		super(x, y, player, MAGMATRON_SIZE, map, hasBeenKilled, id, TOTAL_LIFE);
		setSprite(new Sprite(magmatronImage, getFrameDuration(), MAGMATRON_SIZE.width(), MAGMATRON_SIZE.height()));
		getSprite().setLastDirection(direction);
		setTotalEndurance(TOTAL_ENDURANCE);
		setCurrentEndurance(TOTAL_ENDURANCE);
		setBar(new NPCBar(this, MAGMATRON_SIZE));
		setLastDirection(direction);
		setWeapon(new MagmatronUnarmed(x, y, map, player.getLevel() + 1));
		getWeapon().setActive(false);
		setViewDistance(VIEW_DISTANCE);
		initDropRates();
	}
	
	public Magmatron(float x, float y, Direction direction, Player player, GameMap map)
	{
		this(x, y, direction, player, map, false, "");
	}

	public static void loadAssets(AssetWatcher watcher) 
	{
		magmatronImage = assets().getImage(MAGMATRON_PATH);
		watcher.add(magmatronImage);
	}

	@Override
	public int getLevel()
	{
		return MAGMATRON_LEVEL;
	}
	
	@Override
	public float getExpGiven()
	{
		return EXP_MODIFIER;
	}
	
	public void initDropRates()
	{
//		addDropRate("golds", 1.0f);
		addDropRate("page", 1.0f);
//		addDropRate("weapon", 0.01f);
	}
	
	@Override
	public void checkDrops()
	{
		float value = new Random().nextFloat();
		float golds = 0.0f;
		List<Page> pages = new LinkedList<Page>();
		List<Weapon> weapons = new LinkedList<Weapon>();
		List<Potion> potions = new LinkedList<Potion>();
		
		if (!hasBeenKilled())
		{
			if (getDropRates().get("golds") >= value)
				golds = new Random().nextInt((int) (MAX_GOLDS - MIN_GOLDS)) + MIN_GOLDS;
			if (getDropRates().get("page") >= value)
			{
				for (int i = 0; i < PAGE_NUMBER; i++)
				{
					DataQuality domain = new Random().nextBoolean() ? DataQuality.TOVALIDATE : DataQuality.NEGATIVE;
					String mapId = getMap().getAnnotation().getRandomElement(domain);
					pages.add(new Page(getMap().getGame(), i, domain, getMap().getAnnotation().getValueElement(domain, mapId), mapId));
				}
			}
			if (getDropRates().get("weapon") >= value)
				weapons.add(new Weapon(WeaponType.SPEAR, 1));
		}
		
		if (getDropRates().get("golds") >= value)
			golds = Math.round(MIN_GOLDS + (MAX_GOLDS - MIN_GOLDS) * new Random().nextFloat());
		initBag(potions, weapons, pages, golds);
	}
	
	@Override
	public float getDefaultSpeed()
	{
		return MAGMATRON_SPEED;
	}
	
	@Override
	public IDimension getSize()
	{
		return MAGMATRON_SIZE;
	}
	
	public float getMagicResist()
	{
		return MAGICAL_RESISTANCE;
	}
	
	@Override
	public void setDead(boolean isDead)
	{
		super.setDead(isDead);
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
