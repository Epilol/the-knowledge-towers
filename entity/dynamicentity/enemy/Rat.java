package saga.progetto.tesi.entity.dynamicentity.enemy;

import static playn.core.PlayN.assets;
import java.util.Random;
import playn.core.AssetWatcher;
import playn.core.Image;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import saga.progetto.tesi.entity.Animation;
import saga.progetto.tesi.entity.Sprite;
import saga.progetto.tesi.entity.dynamicentity.Direction;
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.entity.dynamicentity.equip.EnemyUnarmed;
import saga.progetto.tesi.entity.staticentity.Heart;
import saga.progetto.tesi.entity.staticentity.Heart.HeartType;
import saga.progetto.tesi.gui.NPCBar;
import saga.progetto.tesi.map.GameMap;

public class Rat extends Enemy
{
	private static final IDimension RAT_SIZE = new Dimension(32, 32);
	private static final int RAT_LEVEL = 1;
	private static final float EXP_GIVEN = 5.0f;
	private static final String RAT_PATH = "images/characters/enemies/rat.png";
	private static final float RAT_SPEED = 2.0f;
	private static final float TOTAL_LIFE = 2.0f;
	private float TOTAL_ENDURANCE = 5.0f;
	private static final int VIEW_DISTANCE = 650;
	private static final float MAGICAL_RESISTANCE = 0.01f;
	private static Image ratImage;
	
	public Rat(float x, float y, Direction direction, Player player, GameMap map, boolean hasBeenKilled, String id)
	{
		super(x, y, player, RAT_SIZE, map, hasBeenKilled, id, TOTAL_LIFE);
		setSprite(new Sprite(ratImage, getFrameDuration(), RAT_SIZE.width(), RAT_SIZE.height()));
		setTotalEndurance(TOTAL_ENDURANCE);
		setCurrentEndurance(TOTAL_ENDURANCE);
		setBar(new NPCBar(this, RAT_SIZE));
		setLastDirection(direction);
		getSprite().setLastDirection(direction);
		setWeapon(new EnemyUnarmed(x, y, map, player.getLevel() + 1));
		getWeapon().setActive(false);
		setViewDistance(VIEW_DISTANCE);
	}

	public Rat(float x, float y, Direction direction, Player player, GameMap map)
	{
		this(x, y, direction, player, map, false, "");
	}
	
	public static void loadAssets(AssetWatcher watcher) 
	{
		ratImage = assets().getImage(RAT_PATH);
		watcher.add(ratImage);
	}

	@Override
	public int getLevel()
	{
		return RAT_LEVEL;
	}
	
	@Override
	public float getExpGiven()
	{
		return EXP_GIVEN;
	}
	
	@Override
	public float getDefaultSpeed()
	{
		return RAT_SPEED;
	}
	
	@Override
	public IDimension getSize()
	{
		return RAT_SIZE;
	}
	
	public float getMagicResist()
	{
		return MAGICAL_RESISTANCE;
	}
	
	@Override
	public void checkDrops()
	{
		addDropRate("heart", 0.3f);
		addDropRate("maxheart", 0.01f);
	}
	
	@Override
	public void initDropRates()
	{
		float value = new Random().nextFloat();
		
		if (getDropRates().get("maxheart") >= value)
			getMap().addHeart(new Heart(x(), y(), getMap(), HeartType.MAX));
		
		else if (getDropRates().get("heart") >= value)
			getMap().addHeart(new Heart(x(), y(), getMap(), HeartType.SMALL));
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
		if (getCurrentLife() == 0 && getCurrentAnimation() == Animation.DEATH && getSprite().isOver(getCurrentAnimation()))
			initDropRates();
	}
}