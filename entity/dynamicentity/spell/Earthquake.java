package saga.progetto.tesi.entity.dynamicentity.spell;

import java.util.Random;

public class Earthquake
{
	public static final float RADIUS = 300.0f;
	public static final int DISTANCE = 8;
	public static final float COOLDOWN = 120000.0f;
	public static final float DURATION = 6000.0f;
	public static final float TICK_TIME = 1000.0f;
	public static final float COST = 50.0f;
	public static final float MIN_DAMAGE = 6.5f;
	public static final float MAX_DAMAGE = 7.5f;
	public static final float CRITICAL_CHANCE = 0.25f;
	
	private float minDmg;
	private float maxDmg;
	
	public Earthquake(float damageModifier)
	{
		minDmg = MIN_DAMAGE * damageModifier;
		maxDmg = MAX_DAMAGE * damageModifier;
	}
	
	public float getDamage()
	{
		return minDmg + (maxDmg - minDmg) * (float)(new Random().nextDouble());
	}
}
