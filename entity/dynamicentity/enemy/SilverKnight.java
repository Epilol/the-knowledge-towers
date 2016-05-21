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
import saga.progetto.tesi.entity.dynamicentity.equip.EnemyExcalibur;
import saga.progetto.tesi.entity.staticentity.Page;
import saga.progetto.tesi.entity.staticentity.Potion;
import saga.progetto.tesi.entity.staticentity.Weapon;
import saga.progetto.tesi.gui.NPCBar;
import saga.progetto.tesi.map.GameMap;

public class SilverKnight extends Enemy
{
	private static final IDimension SILVER_KNIGHT_SIZE = new Dimension(32, 42);
	private static final String SILVER_KNIGHT_PATH = "images/characters/enemies/silver_knight.png";
	private static final int SILVER_KNIGHT_LEVEL = 999; // 3
	private static final float EXP_MODIFIER = 240.0f;
	private static final float SILVER_KNIGHT_SPEED = 3.0f;
	private static final float TOTAL_LIFE = 180.0f;
	private float TOTAL_ENDURANCE = 130.0f;
	private static final int VIEW_DISTANCE = 190;
	private static final float MAGICAL_RESISTANCE = 0.3f;
	private static final int NUMBER_OF_PAGES = 5;
	private static Image silverKnightImage;

	public SilverKnight(float x, float y, Direction direction, Player player, GameMap map, boolean hasBeenKilled, String id)
	{
		super(x, y, player, SILVER_KNIGHT_SIZE, map, hasBeenKilled, id, TOTAL_LIFE);
		setSprite(new Sprite(silverKnightImage, getFrameDuration(), SILVER_KNIGHT_SIZE.width(), SILVER_KNIGHT_SIZE.height()));
		getSprite().setLastDirection(direction);
		setTotalEndurance(TOTAL_ENDURANCE);
		setCurrentEndurance(TOTAL_ENDURANCE);
		setBar(new NPCBar(this, SILVER_KNIGHT_SIZE));
		setLastDirection(direction);
		setWeapon(new EnemyExcalibur(x, y, map, player.getLevel() + 1));
		getWeapon().setActive(false);
		setViewDistance(VIEW_DISTANCE);
		initDropRates();
	}

	public static void loadAssets(AssetWatcher watcher) 
	{
		silverKnightImage = assets().getImage(SILVER_KNIGHT_PATH);
		watcher.add(silverKnightImage);
	}

	@Override
	public int getLevel()
	{
		return SILVER_KNIGHT_LEVEL;
	}
	
	@Override
	public float getExpGiven()
	{
		return EXP_MODIFIER;
	}
	
	public void initDropRates()
	{
		addDropRate("page1", 1.0f);
		addDropRate("page2", 0.8f);
		addDropRate("page3", 0.6f);
		addDropRate("page4", 0.4f);
		addDropRate("page5", 0.3f);
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
		return SILVER_KNIGHT_SPEED;
	}
	
	@Override
	public IDimension getSize()
	{
		return SILVER_KNIGHT_SIZE;
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
			setCurrentEndurance(getCurrentEndurance() + 0.45f);
	}
}
