package saga.progetto.tesi.entity.dynamicentity.equip;

import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;
import playn.core.AssetWatcher;
import playn.core.Image;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import pythagoras.f.IPoint;
import pythagoras.f.Point;
import saga.progetto.tesi.entity.Animation;
import saga.progetto.tesi.entity.Sprite;
import saga.progetto.tesi.entity.dynamicentity.Direction;
import saga.progetto.tesi.map.GameMap;

public class Destroyer extends PhysicsWeapon
{
	private static final IDimension SIZE = new Dimension(40.0f, 40.0f);
	private static final IDimension LAYER_SIZE = new Dimension(100.0f, 100.0f);
	private static final IPoint FRAME_LOCATION = new Point(graphics().width() / 2, graphics().height() / 2);
	private static final String PATH = "images/equip/destroyer.png";
	private static final float MIN_DAMAGE = 3.7f;
	private static final float MAX_DAMAGE = 5.6f;
	private static final float CRITICAL_CHANCE = 0.25f;
	private static final float SWING_TIME = 30.0f;
	private static final float SWING_COOLDOWN = 1500.0f;
	private static final float ENDURANCE_COST = 50.0f;
	public static final float KNOCKBACK_RATE = 70.0f;
	private static Image image;
	
	public Destroyer(float x, float y, GameMap map, boolean isEnemyEquip, int level)
	{
		super(x, y, SIZE, map, Material.AXE, isEnemyEquip, LAYER_SIZE);
		setSprite(new Sprite(image, getFrameDuration(), getLayerSize().width(), getLayerSize().height()));
		getSprite().setLastDirection(Direction.UP);
		setCurrentAnimation(Animation.NO_WEAPON);
		setMinDmg(MIN_DAMAGE * getDamageBonus(level));
		setMaxDmg(MAX_DAMAGE * getDamageBonus(level));
	}
	
	public Destroyer(float x, float y, GameMap map)
	{
		this(x, y, map, false, 1);
	}
	
	public static void loadAssets(AssetWatcher watcher)
	{
		image = assets().getImage(PATH);
		watcher.add(image);
	}
	
	public static String getTooltip()
	{
		return "name: Warden Sword" + "\n" +
				"damage: 2.8 to 3.4" + "\n" +
				"critical chance: 70%" + "\n" +
				"attack speed: medium" + "\n" +
				"endurance cost : 7" + "\n" +
				"high knockback";
	}
	
	@Override
	public IPoint getFrameLocation()
	{
		return FRAME_LOCATION;
	}

	@Override
	public IDimension getSize()
	{
		return SIZE;
	}

	@Override
	public float getSwingTime()
	{
		return SWING_TIME;
	}

	@Override
	public float getCooldown()
	{
		return SWING_COOLDOWN;
	}

	@Override
	public float getCriticalChance()
	{
		return CRITICAL_CHANCE;
	}

	@Override
	public float getCost()
	{
		return ENDURANCE_COST;
	}

	@Override
	public float getKnockbackRate()
	{
		return KNOCKBACK_RATE;
	}
}
