package saga.progetto.tesi.entity.dynamicentity.spell;

import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;
import org.jbox2d.dynamics.BodyType;
import playn.core.AssetWatcher;
import playn.core.Image;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import pythagoras.f.IPoint;
import pythagoras.f.Point;
import saga.progetto.tesi.entity.Animation;
import saga.progetto.tesi.entity.Sprite;
import saga.progetto.tesi.entity.dynamicentity.Direction;
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.entity.dynamicentity.enemy.Enemy;

public class Stun extends AnimatedSpell
{
	private static final IDimension SIZE = new Dimension(35.0f, 15.0f);
	private static final IDimension STUN_SIZE = new Dimension(60.0f, 51.0f);
	private static final String PATH = "images/spells/stun.png";
	private static final IPoint FRAME_LOCATION = new Point(graphics().width() / 2, graphics().height() / 2);
	public static final float MIN_DAMAGE = 2.3f;
	public static final float MAX_DAMAGE = 4.2f;
	public static final float CRITICAL_CHANCE = 0.1f;
	public static final float COOLDOWN = 15000.0f;
	public static final float DURATION = 300.0f;
	public static final float STUN_VALUE = 2000.0f;
	public static final float SPEED = 6.0f;
	public static final float ENDURANCE_COST = 25.0f;
	public static final float RANGE = 25.0f;
	public static final float KNOCKBACK_RATE = 30.0f;
	private static Image image;

	public float stunModifier;
	
	public Stun(float x, float y, Player player, float damageModifier, float stunModifier)
	{
		super(x, y, SIZE, player);
		this.stunModifier = stunModifier;
		initPhysicalBody(BodyType.DYNAMIC, getSize().width(), getSize().height(), Material.LIVING, 0x0028, 0xFFFF & ~0x0032);
		setSprite(new Sprite(image, getFrameDuration(), STUN_SIZE.width(), STUN_SIZE.height()));
		getLayer().setVisible(true);
		getLayer().setDepth(0.0f);
		setMinDmg(MIN_DAMAGE * damageModifier);
		setMaxDmg(MAX_DAMAGE * damageModifier);
		setSpeed(SPEED);
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
		getSprite().resetDelta();
		if (getPlayer().SpellAttack(enemy, this) > 0)
			enemy.stun(STUN_VALUE + stunModifier);
		clear();
	}

	@Override
	public IPoint getFrameLocation()
	{
		return FRAME_LOCATION;
	}

	@Override
	public float getFrameDuration()
	{
		return 10.0f;
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
		getSprite().update(delta);
		getLayer().setTranslation(-13,-10);
		if (getSprite().isOver(Animation.IDLE))
			clear();
	}
}

