package saga.progetto.tesi.entity.kinematicentity;

import static playn.core.PlayN.graphics;
import org.jbox2d.dynamics.BodyType;
import playn.core.Image;
import playn.core.ImageLayer;
import saga.progetto.tesi.entity.PhysicsEntity;
import saga.progetto.tesi.map.GameMap;

// La classe rappresenta entità generiche che sono in grado di muoversi.
public abstract class KinematicEntity extends PhysicsEntity
{
	private ImageLayer layer;

	public KinematicEntity(float x, float y, Material material, GameMap map)
	{
		super(x, y, map);
		initPhysicalBody(BodyType.KINEMATIC, getSize().width(), getSize().height(), material, 0);
	}
	
	protected void gfxInit(Image image)
	{
		setLayer(graphics().createImageLayer(image));
		getLayer().setVisible(false);
		getLayer().setDepth(3);
		graphics().rootLayer().add(getLayer());
	}

	public ImageLayer getLayer()
	{
		return layer;
	}

	public void setLayer(ImageLayer layer)
	{
		this.layer = layer;
	}
	
	
	public void setVisible(boolean visible)
	{
		layer.setVisible(visible);
	}
	
	public void clear()
	{
		layer.destroy();
	}
}
