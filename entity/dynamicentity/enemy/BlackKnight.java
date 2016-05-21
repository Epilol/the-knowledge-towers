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
import saga.progetto.tesi.entity.dynamicentity.equip.EnemyBlackKnightSword;
import saga.progetto.tesi.entity.staticentity.Page;
import saga.progetto.tesi.entity.staticentity.Potion;
import saga.progetto.tesi.entity.staticentity.Weapon;
import saga.progetto.tesi.gui.NPCBar;
import saga.progetto.tesi.map.GameMap;

public class BlackKnight extends Enemy
{
	private static final IDimension BLACK_KNIGHT_SIZE = new Dimension(32, 42);
	private static final String BLACK_KNIGHT_PATH = "images/characters/enemies/black_knight.png";
	private static final int BLACK_KNIGHT_LEVEL = 999; // 3
	private static final float EXP_MODIFIER = 120.0f;
	private static final float BLACK_KNIGHT_SPEED = 2.0f;
	private static final float TOTAL_LIFE = 21.0f;
	private float TOTAL_ENDURANCE = 70.0f;
	private static final int VIEW_DISTANCE = 190;
	private static final float MAGICAL_RESISTANCE = 0.3f;
	private static final int NUMBER_OF_PAGES = 5;
	private static Image blackKnightImage;

	public BlackKnight(float x, float y, Direction direction, Player player, GameMap map, boolean hasBeenKilled, String id)
	{
		super(x, y, player, BLACK_KNIGHT_SIZE, map, hasBeenKilled, id, TOTAL_LIFE);
		setSprite(new Sprite(blackKnightImage, getFrameDuration(), BLACK_KNIGHT_SIZE.width(), BLACK_KNIGHT_SIZE.height()));
		getSprite().setLastDirection(direction);
		setTotalEndurance(TOTAL_ENDURANCE);
		setCurrentEndurance(TOTAL_ENDURANCE);
		setBar(new NPCBar(this, BLACK_KNIGHT_SIZE));
		setWeapon(new EnemyBlackKnightSword(x, y, map, player.getLevel() + 1));
		getWeapon().setActive(false);
		setViewDistance(VIEW_DISTANCE);
		initDropRates();
	}

	public static void loadAssets(AssetWatcher watcher) 
	{
		blackKnightImage = assets().getImage(BLACK_KNIGHT_PATH);
		watcher.add(blackKnightImage);
	}

	@Override
	public int getLevel()
	{
		return BLACK_KNIGHT_LEVEL;
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
		return BLACK_KNIGHT_SPEED;
	}
	
	@Override
	public IDimension getSize()
	{
		return BLACK_KNIGHT_SIZE;
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
