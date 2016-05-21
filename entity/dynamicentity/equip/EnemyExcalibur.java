package saga.progetto.tesi.entity.dynamicentity.equip;

import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import pythagoras.f.IPoint;
import saga.progetto.tesi.map.GameMap;

public class EnemyExcalibur extends Excalibur
{
	private IPoint frameLocation;
	private static final IDimension SIZE = new Dimension(19.0f, 19.0f);

	public EnemyExcalibur(float x, float y, GameMap map, int level)
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
}
