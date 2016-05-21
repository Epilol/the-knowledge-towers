package saga.progetto.tesi.entity.staticentity;

import playn.core.Image;
import saga.progetto.tesi.entity.PhysicsEntity;
import saga.progetto.tesi.map.GameMap;

// TODO RIVEDERE 
// rappresenta un'entità che non può essere mossa.
public abstract class StaticEntity extends PhysicsEntity
{
	private boolean xSet;
	private boolean ySet;
	
	// indica l'altezza della cella in pixel.
	public static final float HEIGHT = 32.0f;
	// indica la larghezza della cella in pixel.
	public static final float WIDTH = 32.0f;
	
	public StaticEntity(float x, float y, GameMap map)
	{
		super(x, y, map);
	}
	
	@Override
	public void setX(float x)
	{
		if (xSet) throw new StaticEntityException();
		xSet = true;
		super.setX(x);
	}
	
	@Override
	public void setY(float y)
	{
		if (ySet) throw new StaticEntityException();
		ySet = true;
		super.setY(y);
	}
	
	public float getWidth()
	{
		return WIDTH;
	}
	
	public float getHeight()
	{
		return HEIGHT;
	}

	public abstract Image getImage();
}