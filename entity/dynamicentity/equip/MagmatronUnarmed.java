package saga.progetto.tesi.entity.dynamicentity.equip;

import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import pythagoras.f.IPoint;
import saga.progetto.tesi.map.GameMap;

public class MagmatronUnarmed extends Unarmed
{
	private static final float MIN_DAMAGE = 20.0f;
	private static final float MAX_DAMAGE = 30.0f;
	private static final float ENDURANCE_COST = 8.0f;
	
	private IDimension SIZE = new Dimension(30.0f, 30.0f);
	private IPoint frameLocation;

	public MagmatronUnarmed(float x, float y, GameMap map, int level)
	{
		super(x, y, map, true, level);
		setSize(SIZE);
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
	
	public float getMinDmg()
	{
		return MIN_DAMAGE;
	}
	
	public float getMaxDmg()
	{
		return MAX_DAMAGE;
	}
	
	@Override
	public float getCost()
	{
		return ENDURANCE_COST;
	}
}
