package saga.progetto.tesi.entity.staticentity;

import static playn.core.PlayN.graphics;
import playn.core.AssetWatcher;
import playn.core.ImmediateLayer;
import playn.core.Layer;
import playn.core.Surface;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import pythagoras.f.IPoint;
import pythagoras.f.Point;
import saga.progetto.tesi.entity.EntityRenderer;
import saga.progetto.tesi.entity.Renderable;
import saga.progetto.tesi.entity.Sprite;
import saga.progetto.tesi.map.GameMap;

public abstract class Drop extends StaticEntity implements Renderable
{
	private static final float FRAME_DURATION = 120.0f;
	private static final IDimension SIZE = new Dimension(32.0f, 32.0f);
	
	private ImmediateLayer dropLayer;
	private Sprite sprite;
	private IPoint frameLocation;
	private String id;
	
	public Drop(float x, float y, GameMap map, String id)
	{
		super(x, y, map);
		this.id = id;
		dropLayer = graphics().createImmediateLayer(new EntityRenderer(this));
		dropLayer.setVisible(false);
		dropLayer.setDepth(-1.0f);
		graphics().rootLayer().add(dropLayer);
		frameLocation = new Point(graphics().width() / 2 - (int)map.getPlayer().x() + x, 
				graphics().height() / 2 - (int)map.getPlayer().y() + y);
	}
	
	public static void loadAssets(AssetWatcher watcher)
	{
		StorableDrop.loadAssets(watcher);
		SpecialChest.loadAssets(watcher);
	}
	
	@Override
	public float getFrameDuration() 
	{
		return FRAME_DURATION;
	}
	
	@Override
	public IDimension getSize()
	{
		return SIZE;
	}
	
	@Override
	public Layer getLayer()
	{
		return dropLayer;
	}
	
	@Override
	public void setVisible(boolean visible)
	{
		super.setVisible(visible);
		dropLayer.setVisible(visible);
	}
	
	@Override
	public void clear() 
	{
		super.clear();
		dropLayer.destroy();
		getMap().getStorableDrops().remove(this);
	}
	
	@Override
	public Sprite getSprite() 
	{
		return sprite;
	}

	@Override
	public void setSprite(Sprite sprite) 
	{
		this.sprite = sprite;
	}
	
	@Override
	public IPoint getFrameLocation()
	{
		return frameLocation;
	}
	
	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	@Override
	public void drawFrame(Surface surface)
	{
		surface.drawImage(getCurrentFrame(), getFrameLocation().x() - getSize().width() / 2, getFrameLocation().y() -  getSize().height() / 2);
	}

	@Override
	public void update(int delta) 
	{
		sprite.update(delta);
		frameLocation = new Point(graphics().width() / 2 - (int)getMap().getPlayer().x() + x(), 
				graphics().height() / 2 - (int)getMap().getPlayer().y() + y());
	}
}
