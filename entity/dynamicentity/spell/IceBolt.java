package saga.progetto.tesi.entity.dynamicentity.spell;

import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import playn.core.AssetWatcher;
import playn.core.Image;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import pythagoras.f.IPoint;
import pythagoras.f.Point;
import saga.progetto.tesi.entity.Sprite;
import saga.progetto.tesi.entity.dynamicentity.Direction;
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.entity.dynamicentity.enemy.Enemy;

public class IceBolt extends StaticSpell
{
	// TODO sistemare interfaccia e questi campi (che vanno bene pubblici)
	private static final IDimension SIZE = new Dimension(13.0f, 36.0f);
	private static final String PATH = "images/spells/icebolt.png";
	private static final IPoint FRAME_LOCATION = new Point(graphics().width() / 2, graphics().height() / 2);
	private static final float SPEED = 7.0f;
	public static final float MIN_DAMAGE = 2.5f;
	public static final float MAX_DAMAGE = 2.9f;
	public static final float CRITICAL_CHANCE = 0.1f;
	public static final float COOLDOWN = 7000.0f;
	public static final float MANA_COST = 15.0f;
	public static final float RANGE = 150.0f;
	public static final float KNOCKBACK_RATE = 0.0f;
	public static final float SLOW_TIME = 3000.0f;
	public static final float SLOW_VALUE = 0.3f;

	private static Image image;

	private float castingX;
	private float castingY;
	private float timeModifier;

	public IceBolt(float x, float y, Player player, float damageModifier, float timeModifier)
	{
		super(x, y, SIZE, player);
		this.timeModifier = timeModifier;
		initPhysicalBody(BodyType.DYNAMIC, getSize().width(), getSize().height(), Material.ICE, 0x0029, 0xFFFF & ~0x0032);
		setSprite(new Sprite(image, getFrameDuration(), SIZE.width(), SIZE.height()));
		setMinDmg(MIN_DAMAGE * damageModifier);
		setMaxDmg(MAX_DAMAGE * damageModifier);
		setSpeed(SPEED);
		this.castingX = x;
		this.castingY = y;
	}

	public static void loadAssets(AssetWatcher watcher)
	{
		image = assets().getImage(PATH);
		watcher.add(image);
	}
	
	@Override
	public Image getImage()
	{
		return image;
	}
	
	@Override
	public void applyEffect()
	{
		
	}
	
	@Override
	public void applyEffect(Enemy enemy)
	{
		if (getPlayer().SpellAttack(enemy, this) > 0)
			enemy.slow(SLOW_TIME + timeModifier, SLOW_VALUE);
		clear();
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
	public float getCriticalChance()
	{
		return CRITICAL_CHANCE;
	}

	@Override
	public float getCost()
	{
		return MANA_COST;
	}

	@Override
	public float getCooldown()
	{
		return COOLDOWN;
	}

	@Override
	public float getKnockbackRate()
	{
		return KNOCKBACK_RATE;
	}

	@Override
	public void setLastDirection(Direction lastDirection)
	{
		super.setLastDirection(lastDirection);
		getSprite().setLastDirection(lastDirection);
	}
	
	@Override
	public void update(int delta)
	{
		super.update(delta);
		if (getLastDirection() == Direction.UP && castingY > y() + RANGE || getLastDirection() == Direction.DOWN
				&& castingY < y() - RANGE || getLastDirection() == Direction.LEFT && castingX > x() + RANGE || 
					getLastDirection() == Direction.RIGHT && castingX < x() - RANGE)
			clear();
		
		getBody().setLinearVelocity(new Vec2(getLastDirection().x() * getLastDirection().getSpeedAdjustment() * getSpeed(), 
				getLastDirection().y() * getLastDirection().getSpeedAdjustment() * getSpeed()));
	}
}
