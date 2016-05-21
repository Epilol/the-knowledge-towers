package saga.progetto.tesi.entity.dynamicentity;

import static playn.core.PlayN.graphics;
import playn.core.ImmediateLayer;
import playn.core.Layer;
import pythagoras.f.IDimension;
import saga.progetto.tesi.entity.Animation;
import saga.progetto.tesi.entity.EntityRenderer;
import saga.progetto.tesi.map.GameMap;

public abstract class SingleRenderedDynamicEntity extends DynamicEntity
{
	private ImmediateLayer layer;
	
	public SingleRenderedDynamicEntity(float x, float y, IDimension size, GameMap map)
	{
		super(x, y, map);
		setSize(size);
		setCurrentAnimation(Animation.IDLE);
		layer = graphics().createImmediateLayer(new EntityRenderer(this));
		layer.setVisible(false);
		graphics().rootLayer().add(layer);
	}
	
	@Override
	public Layer getLayer()
	{
		return layer;
	}
	
	@Override
	public void setVisible(boolean visible)
	{
		super.setVisible(visible);
		layer.setVisible(visible);
	}
	
	@Override
	public void clear() 
	{
		super.clear();
		layer.destroy();
	}
}
