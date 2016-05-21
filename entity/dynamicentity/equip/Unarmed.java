
package saga.progetto.tesi.entity.dynamicentity.equip;

import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;
import org.jbox2d.common.Vec2;
import playn.core.AssetWatcher;
import playn.core.Image;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import pythagoras.f.IPoint;
import pythagoras.f.Point;
import saga.progetto.tesi.entity.Animation;
import saga.progetto.tesi.entity.Sprite;
import saga.progetto.tesi.entity.dynamicentity.Direction;
import saga.progetto.tesi.entity.dynamicentity.DynamicEntity;
import saga.progetto.tesi.map.GameMap;

public class Unarmed extends PhysicsWeapon
{
	private static final IDimension SIZE = new Dimension(7.0f, 7.0f);
	private static final IPoint FRAME_LOCATION = new Point(graphics().width() / 2, graphics().height() / 2);
	private static final String PATH = "images/equip/unarmed.png";
	private static final float MIN_DAMAGE = 0.1f;
	private static final float MAX_DAMAGE = 0.1f;
	private static final float CRITICAL_CHANCE = 0.2f;
	private static final float SWING_TIME = 10.0f;
	private static final float SWING_COOLDOWN = 350.0f;
	private static final float ENDURANCE_COST = 1.0f;
	public static final float KNOCKBACK_RATE = 20.0f;
	private static Image image;

	public Unarmed(float x, float y, GameMap map, boolean isEnemyEquip, int level)
	{
		super(x, y, SIZE, map, Material.DEFAULT, isEnemyEquip);
		setSprite(new Sprite(image, getFrameDuration(), getLayerSize().width(), getLayerSize().height()));
		getSprite().setLastDirection(Direction.UP);
		setCurrentAnimation(Animation.NO_WEAPON);
		setMinDmg(getMinDmg() * getDamageBonus(level));
		setMaxDmg(getMaxDmg() * getDamageBonus(level));
	}

	public Unarmed(float x, float y, GameMap map)
	{
		this(x, y, map, false, 1);
	}
	
	public static void loadAssets(AssetWatcher watcher)
	{
		image = assets().getImage(PATH);
		watcher.add(image);
	}
	
	public float getMinDmg()
	{
		return MIN_DAMAGE;
	}
	
	public float getMaxDmg()
	{
		return MAX_DAMAGE;
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
	
	@Override
	public IPoint getFrameLocation()
	{
		return FRAME_LOCATION;
	}
	
	@Override
	public void setLastDirection(Direction lastDirection)
	{
		getSprite().setLastDirection(lastDirection);
	}
	
	@Override
	public void bodyTransform(DynamicEntity entity, Direction lastDirection)
	{
		getBody().setTransform(entity.getBody().getPosition().add(
				new Vec2(lastDirection.x() * getSize().width() / GameMap.PTM_RATIO, lastDirection.y() * getSize().height() / GameMap.PTM_RATIO)), 
				lastDirection.getAngle());
	}
}
