package saga.progetto.tesi.entity.dynamicentity.equip;

import java.util.Random;
import pythagoras.f.IDimension;
import pythagoras.f.IPoint;
import saga.progetto.tesi.core.TheKnowledgeTowers;
import saga.progetto.tesi.entity.Offensive;
import saga.progetto.tesi.map.GameMap;

public abstract class PhysicsWeapon extends Equip implements Offensive
{
	private float minDmg;
	private float maxDmg;
	private float poisonTime;
	private boolean hasPoison;
	private boolean hasSleep;
	private float sleepTime;
	private float poisonDamageModifier;
	private float damageModifier;
	
	public PhysicsWeapon(float x, float y, IDimension size, GameMap map, Material material, boolean isEnemyEquip)
	{
		super(x, y, size, map, material, isEnemyEquip);
	}
	
	public PhysicsWeapon(float x, float y, IDimension size, GameMap map, Material material, boolean isEnemyEquip, IDimension layerSize)
	{
		super(x, y, size, map, material, isEnemyEquip, layerSize);
	}

	public float getWeaponDamage()
	{
		return new Random().nextInt(100) > getCriticalChance() * 100 ? getDamage() : getDamage() * 2;
	}
	
	public abstract float getSwingTime();
	
	public float getMinDmg()
	{
		return minDmg;
	}

	public void setMinDmg(float minDmg)
	{
		this.minDmg = minDmg;
	}

	public float getMaxDmg()
	{
		return maxDmg;
	}

	public void setMaxDmg(float maxDmg)
	{
		this.maxDmg = maxDmg;
	}
	
	public float getDamage()
	{
		return minDmg * damageModifier() + (maxDmg * damageModifier() - minDmg * damageModifier()) * new Random().nextFloat();
	}
	
	public void setFrameLocation(IPoint frameLocation)
	{
		
	}
	
	public boolean hasPoison()
	{
		return hasPoison;
	}
	
	public void setPoison(boolean hasPoison)
	{
		this.hasPoison = hasPoison;
	}
	
	public void addPoison(float poisonTime, float poisonDamageModifier)
	{
		this.hasPoison = true;
		this.poisonTime = poisonTime;
		this.poisonDamageModifier = poisonDamageModifier;
	}
	
	public float getPoisonDamage()
	{
		return poisonDamageModifier * (new Random().nextFloat() + 0.5f);
	}
	
	public float getPoisonTime()
	{
		return poisonTime;
	}
	
	public boolean hasSleep()
	{
		return hasSleep;
	}
	
	public void setSleep(boolean hasSleep)
	{
		this.hasSleep = hasSleep;
	}
	
	public void addSleep(float sleepTime)
	{
		this.hasSleep = true;
		this.sleepTime = sleepTime;
	}
	
	public float getSleepTime()
	{
		return sleepTime;
	}
	
	public float damageModifier()
	{
		return 1 + getDamageModifier();
	}
	
	public float getDamageModifier()
	{
		return damageModifier;
	}

	public void setDamageModifier(float damageModifier)
	{
		this.damageModifier = damageModifier;
	}
	
	@Override
	public String toString()
	{
		return getClass().getName();
	}
	
	public float getDamageBonus(float level)
	{
		return level == 1 ? 1 : ((((float) level) / 10) + 1) * (getMap().getPlayer().getNewGame() * TheKnowledgeTowers.NEW_GAME_RATE + 1);
	}
	
	public void destroyPhysics()
	{
		getMap().getBodies().remove(getBody());
		getMap().getWorld().destroyBody(getBody());
	}
}
