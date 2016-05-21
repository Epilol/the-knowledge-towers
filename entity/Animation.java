package saga.progetto.tesi.entity;

// l'enum rappresenta tutte le possibili animazioni contenute nello sprite sheet di un'entità.
public enum Animation 
{
	IDLE(0, 1),
	STEALTH_IDLE(16, 1),
	BERSERK_IDLE(16, 1),
	WOLF_IDLE(0, 1),
	RUN(4, 1),
	WALK(4, 3),
	STEALTH_WALK(20, 3),
	BERSERK_RUN(20, 1),
	WOLF_RUN(4, 1),
	DEATH(8, 2), 
	WOLF_DEATH(12, 1),
	ATTACK(12, 1),
	STEALTH_ATTACK(24, 1),
	BERSERK_ATTACK(24, 1),
	WOLF_ATTACK(8, 1),
	EVADE_IDLE(28, 1),
	EVADE_RUN(32, 1),
	EVADE_ATTACK(36, 1),
	ENEMY_ATTACK(12, 3),
	SLEEPING_ENEMY(16, 1),
	NO_WEAPON(0, 1),
	WEAPON(4, 1),
	STEALTH_WEAPON(8, 1),
	BERSERK_WEAPON(8, 1),
	EXECUTE_WEAPON(12, 1),
	EVADE_WEAPON(12, 1),
	ENEMY_WEAPON(4, 3),
	
	CC_FAST(0, 1),
	CC_SLOW(0, 3),
	STATIC_OBJECT_DEFAULT(0, 2),
	STATIC_OBJECT_ANIMATED(1, 1),
	STATIC_OBJECT_USED(2, 2);
	
	private int index;
	private int coefficient;
	
	Animation(int index, int coefficient)
	{
		this.index = index;
		this.coefficient = coefficient;
	}
	
	// ritorna l'indice dell'animazione nello sprite sheet.
	public int getIndex()
	{
		return index;
	}
	
	// ritorna un coefficiente che rappresenta la velocità dell'animazione.
	public int getCoefficient()
	{
		return coefficient;
	}
}
