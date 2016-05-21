package saga.progetto.tesi.entity.dynamicentity.spell;

import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import playn.core.AssetWatcher;
import playn.core.Image;
import playn.core.Surface;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import pythagoras.f.IPoint;
import pythagoras.f.Point;
import saga.progetto.tesi.entity.Sprite;
import saga.progetto.tesi.entity.dynamicentity.Direction;
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.entity.dynamicentity.enemy.Enemy;

public class Swarm extends AnimatedSpell
{
	// TODO sistemare interfaccia e questi campi (che vanno bene pubblici)
	private static final IDimension SIZE = new Dimension(45.0f, 45.0f);
	private static final String PATH = "images/spells/swarm.png";
	private static final IPoint FRAME_LOCATION = new Point(graphics().width() / 2, graphics().height() / 2);
	private static final float SPEED = 2.0f;
	public static final float MIN_DAMAGE = 0.01f;
	public static final float MAX_DAMAGE = 0.01f;
	public static final float MANA_COST = 2.0f;
	public static final float CRITICAL_CHANCE = 0.0f;
	public static final float COOLDOWN = 1000.0f;
	public static final float DURATION = 6000.0f;
	public static final float KNOCKBACK_RATE = 0.0f;

	private static Image image;

	private float currentTimer;
	private boolean isColliding;
	private Enemy enemy;

	public Swarm(float x, float y, Player player, float damageModifier)
	{
		super(x, y, SIZE, player);
		initPhysicalBody(BodyType.DYNAMIC, getSize().width(), getSize().height(), Material.LIVING, 0x0029, 0xFFFF & ~0x0032);
		setSprite(new Sprite(image, getFrameDuration(), SIZE.width(), SIZE.height()));
		getLayer().setVisible(true);
		getLayer().setDepth(6.0f);
		setMinDmg(MIN_DAMAGE * damageModifier);
		setMaxDmg(MAX_DAMAGE * damageModifier);
		setSpeed(SPEED);
		setPerforating(true);
		setSensor(true);
		setRemoveText(true);
	}

	public static void loadAssets(AssetWatcher watcher)
	{
		image = assets().getImage(PATH);
		watcher.add(image);
	}
	
	@Override
	public void applyEffect()
	{
		
	}
	
	@Override
	public void applyEffect(Enemy enemy)
	{
		getPlayer().SpellAttack(enemy, this);
		isColliding = true;
		this.enemy = enemy;
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
	}
	
	@Override
	public void clear()
	{
		super.clear();
	}
	
	@Override
	public void drawFrame(Surface surface)
	{
		if (!isColliding || enemy == null)
			super.drawFrame(surface);
		
		else
			surface.drawImage(getCurrentFrame(), graphics().width() / 2 + (int)enemy.x() - x() - getSize().width() / 2,
				graphics().height() / 2 + (int)enemy.y() - y() - getSize().height() / 2);
	}
	
	@Override
	public void update(int delta)
	{
		super.update(delta);
		currentTimer += delta;
		
		if (currentTimer >= DURATION || enemy != null &&enemy.isDead())
			clear();
		
		getBody().setLinearVelocity(new Vec2(getLastDirection().x() * getLastDirection().getSpeedAdjustment() * getSpeed(), 
				getLastDirection().y() * getLastDirection().getSpeedAdjustment() * getSpeed()));
		
		if (isColliding)
		{
			setLastDirection(enemy.x(), enemy.y());
			bodyTransform(enemy, enemy.getSprite().getLastDirection());
		}
	}
}
