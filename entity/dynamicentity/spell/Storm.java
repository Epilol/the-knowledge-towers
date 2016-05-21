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
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.entity.dynamicentity.enemy.Enemy;

public class Storm extends AnimatedSpell
{
	// TODO sistemare interfaccia e questi campi (che vanno bene pubblici)
	private static final IDimension SIZE = new Dimension(50.0f, 104.0f);
	private static final String PATH = "images/spells/storm.png";
	private static final IPoint FRAME_LOCATION = new Point(graphics().width() / 2, graphics().height() / 2);
	public static final int RADIUS = 250;
	public static final float MIN_DAMAGE = 0.05f;
	public static final float MAX_DAMAGE = 30.0f;
	public static final float CRITICAL_CHANCE = 0.3f;
	public static final float COOLDOWN = 120000.0f;
	public static final float DURATION = 4000.0f;
	public static final float TICK_TIME = 800.0f;
	public static final float MANA_COST = 35.0f;
	public static final float KNOCKBACK_RATE = 0.0f;

	private static Image image;

	public Storm(float x, float y, Player player, float damageModifier)
	{
		super(x, y, SIZE, player);
		initPhysicalBody(BodyType.DYNAMIC, getSize().width(), getSize().height(), Material.AIR, 0x0028, 0xFFFF & ~0x0002 & ~0x0028 & ~0x0032);
		setSprite(new Sprite(image, getFrameDuration(), SIZE.width(), SIZE.height()));
		getLayer().setVisible(true);
		getLayer().setDepth(-1.0f);
		setMinDmg(MIN_DAMAGE * damageModifier);
		setMaxDmg(MAX_DAMAGE * damageModifier);
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
		getSprite().resetDelta();
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
	public void clear()
	{
		getMap().getBodies().remove(getBody());
		getMap().getWorld().destroyBody(getBody());
	}
	
	public void destroy()
	{
		super.clear();
	}
	
	@Override
	public void update(int delta)
	{
		super.update(delta);
		
		if (getSprite().isOver(Animation.IDLE))
			super.clear();
	}
}
