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
import saga.progetto.tesi.entity.dynamicentity.equip.OldWarriorSword;
import saga.progetto.tesi.entity.staticentity.Page;
import saga.progetto.tesi.entity.staticentity.Potion;
import saga.progetto.tesi.entity.staticentity.Weapon;
import saga.progetto.tesi.gui.NPCBar;
import saga.progetto.tesi.map.GameMap;

public class Nemesis extends Enemy
{
	private static final IDimension NEMESIS_SIZE = new Dimension(32, 47);
	private static final String NEMESIS_PATH = "images/characters/enemies/nemesis.png";
	private static final int NEMESIS_LEVEL = 999; // 3
	private static final float EXP_MODIFIER = 80.0f;
	private static final float NEMESIS_SPEED = 2.0f;
	private static final float TOTAL_LIFE = 23.0f;
	private float TOTAL_ENDURANCE = 40.0f;
	private static final int VIEW_DISTANCE = 220;
	private static final float MAGICAL_RESISTANCE = 0.15f;
	private static final int NUMBER_OF_PAGES = 3;
	private static Image nemesisImage;

	public Nemesis(float x, float y, Direction direction, Player player, GameMap map, boolean hasBeenKilled, String id)
	{
		super(x, y, player, NEMESIS_SIZE, map, hasBeenKilled, id, TOTAL_LIFE);
		setSprite(new Sprite(nemesisImage, getFrameDuration(), NEMESIS_SIZE.width(), NEMESIS_SIZE.height()));
		getSprite().setLastDirection(direction);
		setTotalEndurance(TOTAL_ENDURANCE);
		setCurrentEndurance(TOTAL_ENDURANCE);
		setBar(new NPCBar(this, NEMESIS_SIZE));
		setWeapon(new OldWarriorSword(x, y, map, true, player.getLevel() + 1));
		getWeapon().setActive(false);
		setViewDistance(VIEW_DISTANCE);
		initDropRates();
	}

	public static void loadAssets(AssetWatcher watcher) 
	{
		nemesisImage = assets().getImage(NEMESIS_PATH);
		watcher.add(nemesisImage);
	}

	@Override
	public int getLevel()
	{
		return NEMESIS_LEVEL;
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
	}
	
	@Override
	public void checkDrops()
	{
		float value = new Random().nextFloat();
		float golds = 0.0f;
		List<Page> pages = new LinkedList<Page>();
		List<Weapon> weapons = new LinkedList<Weapon>();
		List<Potion> potions = new LinkedList<Potion>();
		
//		if (!hasBeenKilled())
//		{
//			if (getDropRates().get("golds") >= value)
//				golds = new Random().nextInt((int) (MAX_GOLDS - MIN_GOLDS)) + MIN_GOLDS;
//			
//			if (getDropRates().get("weapon") >= value)
//				weapons.add(new Weapon(WeaponType.SPEAR, 1));
//			
//			else if (getDropRates().get("life") >= value)
//				potions.add(new LifePotion(PotionType.LIFE_POTION, 1));
//			
//			else if (getDropRates().get("minor mana") >= value)
//				potions.add(new ManaPotion(PotionType.MINOR_MANA_POTION, 1));
//
//			else if (getDropRates().get("stealth") >= value)
//				potions.add(new InvisibilityPotion(PotionType.STEALTH_POTION, 1, getMap().getPlayer()));
//		}
//		
//		else
//			if (getDropRates().get("golds") >= value)
//				golds = new Random().nextInt((int) (MAX_GOLDS - MIN_GOLDS)) + 1;
		
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
		return NEMESIS_SPEED;
	}
	
	@Override
	public IDimension getSize()
	{
		return NEMESIS_SIZE;
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
