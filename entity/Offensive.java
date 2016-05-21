package saga.progetto.tesi.entity;

import saga.progetto.tesi.entity.dynamicentity.Direction;
import saga.progetto.tesi.entity.dynamicentity.DynamicEntity;

public interface Offensive
{
	public static final float KNOCKBACK_RECOVER = 5.0f;
	
	float getCriticalChance();

	float getCooldown();
	
	float getCost();
	
	void bodyTransform(DynamicEntity entity, Direction lastDirection);
	
	// realisticamente da 0 a 100
	float getKnockbackRate();
}
