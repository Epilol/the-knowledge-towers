package saga.progetto.tesi.entity.dynamicentity.spell;

import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;
import java.util.Random;
import org.jbox2d.dynamics.BodyType;
import playn.core.AssetWatcher;
import playn.core.Image;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import pythagoras.f.IPoint;
import pythagoras.f.Point;
import saga.progetto.tesi.core.TheKnowledgeTowers;
import saga.progetto.tesi.entity.Sprite;
import saga.progetto.tesi.entity.dynamicentity.Direction;
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.entity.dynamicentity.enemy.Enemy;

public class CircleOfHealing extends AnimatedSpell
{
	// TODO sistemare interfaccia e questi campi (che vanno bene pubblici)
	private static final IDimension CIRCLE_SIZE = new Dimension(192.0f, 192.0f);
	private static final String HEALING_PATH = "images/spells/circle_of_healing_effect.png";
	private static final IPoint FRAME_LOCATION = new Point(graphics().width() / 2, graphics().height() / 2 - 24);
	public static final float RADIUS = 60.0f;
	public static final float MIN_DAMAGE = 0.0f;
	public static final float MAX_DAMAGE = 0.0f;
	public static final float MIN_HEAL = 2.0f;
	public static final float MAX_HEAL = 3.0f;
	public static final float CRITICAL_CHANCE = 0.0f;
	public static final float COOLDOWN = 12000.0f;
	public static final float PHYSICS = 100.0f;
	public static final float MANA_COST = 16.0f;
	public static final float KNOCKBACK_RATE = 0.0f;
	public static final float CIRCLE_DURATION = 8000.0f;
	public static final float HEALING_TICK_TIME = 1000.0f;
	private static Image circleImage;
	
	private float circleModifier;
	private float currentCircleTime;
	
	public CircleOfHealing(float x, float y, Player player, float circleModifier)
	{
		super(x, y, CIRCLE_SIZE, player);
		this.circleModifier = circleModifier;
		initRadialBody(BodyType.DYNAMIC, RADIUS, Material.LIVING, 0x0028, 0xFFFF);
		setSprite(new Sprite(circleImage, getFrameDuration(), CIRCLE_SIZE.width(), CIRCLE_SIZE.height()));
		getLayer().setVisible(true);
		getLayer().setDepth(0.0f);
		setSensor(true);
	}
	
	public static void loadAssets(AssetWatcher watcher)
	{
		circleImage = assets().getImage(HEALING_PATH);
		watcher.add(circleImage);
	}
	
	@Override
	public void applyEffect()
	{
		if (currentCircleTime % HEALING_TICK_TIME >= 0 && currentCircleTime % HEALING_TICK_TIME <= TheKnowledgeTowers.UPDATE_RATE)
		{
			if (getMap().getContactListener().isColliding(getPlayer().getBody(), getBody()))
				getPlayer().heal((new Random().nextInt((int)(MAX_HEAL - MIN_HEAL)) + MIN_HEAL) * circleModifier);
			
			if (getPlayer().getPet() != null && getMap().getContactListener().isColliding(getPlayer().getPet().getBody(), getBody()))
				getPlayer().getPet().heal((new Random().nextInt((int)(MAX_HEAL - MIN_HEAL)) + MIN_HEAL) * circleModifier);
		}
	}
	
	@Override
	public void applyEffect(Enemy enemy)
	{
	}
	
	@Override
	public float getFrameDuration()
	{
		return 100.0f;
	}

	@Override
	public IPoint getFrameLocation()
	{
		return FRAME_LOCATION;
	}
	
	@Override
	public IDimension getSize()
	{
		return CIRCLE_SIZE;
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
	public boolean destroyOnCollision()
	{
		return false;
	}
	
	@Override
	public void update(int delta)
	{
		super.update(delta);
		currentCircleTime += delta;
	}
}
