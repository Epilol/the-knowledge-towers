package saga.progetto.tesi.entity.dynamicentity.spell;

import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
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

public class Arrow extends StaticSpell
{
	private static final IDimension SIZE = new Dimension(10.0f, 32.0f);
	private static final String PATH = "images/spells/arrow.png";
	private static final IPoint FRAME_LOCATION = new Point(graphics().width() / 2, graphics().height() / 2);
	private static final float SPEED = 13.0f;
	private static final float SNIPE_DAMAGE = 8.0f;
	private static final float SNIPE_DAMAGE_RANGE = 120.0f;
	public static final float MIN_DAMAGE = 0.2f;
	public static final float MAX_DAMAGE = 0.4f;
	public static final float CRITICAL_CHANCE = 0.3f;
	public static final float COOLDOWN = 1000.0f;
	public static final float ENDURANCE_COST = 40.0f;
	public static final float RANGE = 400.0f;
	public static final float KNOCKBACK_RATE = 30.0f;
	private static Image image;
	
	private List<Enemy> enemies;
	private float rangeModifier = 1.0f; 
	private float castingX;
	private float castingY;
	
	public Arrow(float x, float y, Player player, float damageModifier)
	{
		super(x, y, SIZE, player);
		initPhysicalBody(BodyType.DYNAMIC, getSize().width(), getSize().height(), Material.WOOD, 0x0029, 0xFFFF & ~0x0032 & ~0x0028);
		setSprite(new Sprite(image, getFrameDuration(), SIZE.width(), SIZE.height()));
		setMinDmg(MIN_DAMAGE * damageModifier);
		setMaxDmg(MAX_DAMAGE * damageModifier);
		setSpeed(SPEED);
		castingX = x;
		castingY = y;
		enemies = new LinkedList<Enemy>();
		setSensor(true);
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
		if (!enemies.contains(enemy))
		{
			if (!isPerforating() && (Math.abs(getPlayer().x() - enemy.x()) > SNIPE_DAMAGE_RANGE || Math.abs(getPlayer().y() - enemy.y()) > SNIPE_DAMAGE_RANGE))
				rangeModifier = SNIPE_DAMAGE;
			
			getPlayer().SpellAttack(enemy, this);
		}

		enemies.add(enemy);
		
		if (!isPerforating())
			clear();
	}

	@Override
	public float getDamage()
	{
		return (getMinDmg() + (getMaxDmg() - getMinDmg()) * (float)(new Random().nextDouble())) * rangeModifier;
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
		return ENDURANCE_COST;
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
	public void clear()
	{
		super.clear();
		enemies.clear();
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
