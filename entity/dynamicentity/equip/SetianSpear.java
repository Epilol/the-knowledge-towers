package saga.progetto.tesi.entity.dynamicentity.equip;

import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import pythagoras.f.IPoint;
import saga.progetto.tesi.map.GameMap;

public class SetianSpear extends Spear
{
	private static final float MIN_DAMAGE = 0.9f;
	private static final float MAX_DAMAGE = 8.0f;
	private static final float CRITICAL_CHANCE = 0.3f;
	
	private IPoint frameLocation;
	private static final IDimension SIZE = new Dimension(16.0f, 16.0f);

	public SetianSpear(float x, float y, GameMap map, int level)
	{
		super(x, y, map, true, level);
		setSize(SIZE);
		setMinDmg(MIN_DAMAGE * getDamageBonus(level));
		setMaxDmg(MAX_DAMAGE * getDamageBonus(level));
	}

	@Override
	public IPoint getFrameLocation()
	{
		return frameLocation;
	}

	@Override
	public void setFrameLocation(IPoint frameLocation)
	{
		this.frameLocation = frameLocation;
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
}
