package saga.progetto.tesi.entity.dynamicentity.spell;

import static playn.core.PlayN.graphics;
import org.jbox2d.dynamics.BodyType;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import pythagoras.f.IPoint;
import pythagoras.f.Point;
import saga.progetto.tesi.core.TheKnowledgeTowers;
import saga.progetto.tesi.entity.dynamicentity.Direction;
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.entity.dynamicentity.enemy.Enemy;

public class Whirldwind extends AnimatedSpell
{
	// TODO sistemare interfaccia e questi campi (che vanno bene pubblici)
	private static final IDimension SIZE = new Dimension(35.0f, 35.0f);
	private static final IPoint FRAME_LOCATION = new Point(graphics().width() / 2, graphics().height() / 2);
	public static final float RADIUS = 35.0f;
	public static final float MIN_DAMAGE = 0.2f;
	public static final float MAX_DAMAGE = 0.6f;
	public static final float CRITICAL_CHANCE = 0.1f;
	public static final float COOLDOWN = 5000.0f;
	public static final float DURATION = 1000.0f;
	public static final float RAGE_COST = 25.0f;
	public static final float KNOCKBACK_RATE = 70.0f;
	
	private float currentWhirlwindTime;
	
	public Whirldwind(float x, float y, Player player, float damageModifier)
	{
		super(x, y, SIZE, player);
		initRadialBody(BodyType.DYNAMIC, RADIUS, Material.LIVING, 0x0028, 0xFFFF & ~0x0032);
		setMinDmg(MIN_DAMAGE * damageModifier);
		setMaxDmg(MAX_DAMAGE * damageModifier);
		setRemoveText(true);
	}
	
	@Override
	public void applyEffect()
	{
	}
	
	@Override
	public void applyEffect(Enemy enemy)
	{
		getPlayer().getWeapon().setVisible(true);
		getPlayer().SpellAttack(enemy, this);
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
		return RAGE_COST;
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
	public void update(int delta) 
	{
		getPlayer().getWeapon().setVisible(true);
		if (currentWhirlwindTime % (COOLDOWN / 8) >= 0 || currentWhirlwindTime % (COOLDOWN / 8) < TheKnowledgeTowers.UPDATE_RATE)
			getPlayer().setLastDirection(Direction.getNextDirection(getPlayer().getLastDirection()));
		
		currentWhirlwindTime += delta;
	}
}
