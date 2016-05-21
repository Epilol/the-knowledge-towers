package saga.progetto.tesi.entity;

import playn.core.ImmediateLayer.Renderer;
import playn.core.Surface;

// decorazione dell'interfaccia Renderer
public class EntityRenderer implements Renderer
{
	private Renderable renderable;
	
	public EntityRenderer(Renderable renderable)
	{
		this.renderable = renderable;
	}
	
	@Override
	public void render(Surface surface) 
	{
		renderable.drawFrame(surface);
	}
}
