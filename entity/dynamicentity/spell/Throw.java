package saga.progetto.tesi.entity.dynamicentity.spell;

import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import playn.core.AssetWatcher;
import playn.core.Image;
import playn.core.ImageLayer;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import pythagoras.f.IPoint;
import pythagoras.f.Point;
import saga.progetto.tesi.entity.Sprite;
import saga.progetto.tesi.entity.dynamicentity.Direction;
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.entity.dynamicentity.enemy.Enemy;

public class Throw extends StaticSpell
{
	// TODO sistemare interfaccia e questi campi (che vanno bene pubblici)
	private static final IDimension SIZE = new Dimension(8.0f, 30.0f);
	private static final String PATH = "images/spells/throw.png";
	private static final IPoint FRAME_LOCATION = new Point(graphics().width() / 2, graphics().height() / 2);
	private static final float SPEED = 7.0f;
	public static final float MIN_DAMAGE = 1.3f;
	public static final float MAX_DAMAGE = 4.2f;
	public static final float CRITICAL_CHANCE = 0.2f;
	public static final float COOLDOWN = 5000.0f;
	public static final float ENDURANCE_COST = 20.0f;
	public static final float RANGE = 150.0f;
	public static final float KNOCKBACK_RATE = 20.0f;

	private static Image image;

	private float castingX;
	private float castingY;

	public Throw(float x, float y, Player player, float damageModifier)
	{
		super(x, y, SIZE, player, true);
		initPhysicalBody(BodyType.DYNAMIC, getSize().width(), getSize().height(), Material.LIVING, 0x0028, 0xFFFF & ~0x0032);
		setSprite(new Sprite(image, getFrameDuration(), SIZE.width(), SIZE.height()));
		setMinDmg(MIN_DAMAGE * damageModifier);
		setMaxDmg(MAX_DAMAGE * damageModifier);
		ImageLayer layer = graphics().createImageLayer(getImage());
		layer.setVisible(true);
		layer.setOrigin(getSize().width() / 2, getSize().height() / 2);
		layer.setTranslation(getFrameLocation().x() - getSize().width() / 2, getFrameLocation().y() - getSize().height() / 2);
		layer.setDepth(4.0f);
		layer.setRotation((float)Math.toRadians(player.getFacingDirection().getDegree()));
		graphics().rootLayer().add(layer);
		setLayer(layer);
		
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
		getPlayer().SpellAttack(enemy, this);
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
