package saga.progetto.tesi.entity.dynamicentity;

import playn.core.Layer;
import pythagoras.f.IDimension;
import saga.progetto.tesi.entity.Animation;
import saga.progetto.tesi.map.GameMap;
import saga.progetto.tesi.navigable.Gameloop;

public abstract class GroupRenderedDynamicEntity extends DynamicEntity
{
	private float depth;
	
	public GroupRenderedDynamicEntity(float x, float y, IDimension size, GameMap map, float depth)
	{
		super(x, y, map);
		this.depth = depth;
		setSize(size);
		setCurrentAnimation(Animation.IDLE);
		map.getGameloop().addRenderable(this, depth);
	}
	
	public GroupRenderedDynamicEntity(float x, float y, IDimension size, GameMap map)
	{
		super(x, y, map);
		setSize(size);
		setCurrentAnimation(Animation.IDLE);
		map.getGameloop().addRenderable(this, depth);
	}
	
	public float depth()
	{
		return depth;
	}
	
	public void setDepth(float depth)
	{
		this.depth = depth;
	}

	@Override
	public void setPoisoned(boolean isPoisoned)
	{
		super.setPoisoned(isPoisoned);
		
		if (isPoisoned)
		{
			getGameloop().removeRenderable(this, depth);
			getGameloop().addPoisonedRenderable(this);
		}
		
		else
		{
			getGameloop().removePoisonedRenderable(this);
			getGameloop().addRenderable(this, depth);
		}
	}
	
	@Override
	public void slow(float slowTime, float slowValue)
	{
		super.slow(slowTime, slowValue);
		getGameloop().removeRenderable(this, depth);
		getGameloop().addFrozenRenderable(this);
	}
	
	@Override
	public void removeSlow()
	{
		super.removeSlow();
		getGameloop().removeFrozenRenderable(this);
		getGameloop().addRenderable(this, depth);
	}
	
	@Override
	public Layer getLayer()
	{
		return getGameloop().getRendererLayer(depth);
	}

	@Override
	public void setVisible(boolean visible)
	{
		getGameloop().setRenderableVisibility(depth, visible);
	}
	
	@Override
	public void clear() 
	{
		super.clear();
		
		if (isPoisoned())
			getGameloop().removePoisonedRenderable(this);
		
		else if (isFrozen())
			getGameloop().removeFrozenRenderable(this);
			
		else
			getGameloop().removeRenderable(this, depth);
	}
	
	public Gameloop getGameloop()
	{
		return getMap().getGameloop();
	}
}
