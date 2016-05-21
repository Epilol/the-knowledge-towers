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

public class Sword extends PhysicsWeapon
{
	private static final IDimension SIZE = new Dimension(32.0f, 32.0f);
	private static final IPoint FRAME_LOCATION = new Point(graphics().width() / 2, graphics().height() / 2);
	private static final String PATH = "images/equip/sword.png";
	private static final float MIN_DAMAGE = 2.0f;
	private static final float MAX_DAMAGE = 2.2f;
	private static final float CRITICAL_CHANCE = 0.05f;
	private static final float SWING_TIME = 30.0f;
	private static final float SWING_COOLDOWN = 400.0f;
	private static final float ENDURANCE_COST = 7.0f;
	public static final float KNOCKBACK_RATE = 40.0f;
	private static Image image;
	
	public Sword(float x, float y, GameMap map, boolean isEnemyEquip, int level)
	{
		super(x, y, SIZE, map, Material.SWORD, isEnemyEquip);
		setSprite(new Sprite(image, getFrameDuration(), getLayerSize().width(), getLayerSize().height()));
		getSprite().setLastDirection(Direction.UP);
		setCurrentAnimation(Animation.NO_WEAPON);
		setMinDmg(MIN_DAMAGE * getDamageBonus(level));
		setMaxDmg(MAX_DAMAGE * getDamageBonus(level));
	}
	
	public Sword(float x, float y, GameMap map)
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
		return "name: Sword" + "\n" +
				"damage: 1.8 to 2.4" + "\n" +
				"critical chance: 10%" + "\n" +
				"attack speed: medium" + "\n" +
				"endurance cost : 3" + "\n" +
				"medium knockback";
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
