package saga.progetto.tesi.entity;

import static playn.core.PlayN.graphics;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import playn.core.ImmediateLayer;
import playn.core.Layer;
import playn.core.ImmediateLayer.Renderer;
import playn.core.Surface;

public class GroupRenderer implements Renderer, Iterable<Renderable>
{
	private Set<Renderable> renderables;
	private ImmediateLayer layer;
	private float depth;
	
	public GroupRenderer(float depth)
	{
		this.depth = depth;
		renderables = new HashSet<Renderable>();
		layer =  graphics().createImmediateLayer(this);
		layer.setDepth(depth);
		layer.setVisible(true);
		graphics().rootLayer().add(layer);
	}
	
	public GroupRenderer(int tint)
	{
		renderables = new HashSet<Renderable>();
		layer =  graphics().createImmediateLayer(this);
		layer.setTint(tint);
		layer.setVisible(true);
		graphics().rootLayer().add(layer);
	}
	
	public float depth()
	{
		return depth;
	}

	public void setDepth(float depth)
	{
		this.depth = depth;
	}

	public void addRender(Renderable renderable)
	{
		renderables.add(renderable);
	}
	
	public void removeRender(Renderable renderable)
	{
		renderables.remove(renderable);
	}
	
	public Layer getLayer()
	{
		return layer;
	}
	
	public void setVisible(boolean visible)
	{
		layer.setVisible(visible);
	}
	
	public int size()
	{
		return renderables.size();
	}
	
	@Override
	public Iterator<Renderable> iterator()
	{
		return renderables.iterator();
	}
	
	@Override
	public void render(Surface surface) 
	{
		for (Renderable renderable : renderables)
			renderable.drawFrame(surface);
	}
}
