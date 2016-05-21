package saga.progetto.tesi.entity.dynamicentity.spell;

import static playn.core.PlayN.graphics;
import org.jbox2d.common.Vec2;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Surface;
import pythagoras.f.IDimension;
import saga.progetto.tesi.entity.Offensive;
import saga.progetto.tesi.entity.dynamicentity.Direction;
import saga.progetto.tesi.entity.dynamicentity.DynamicEntity;
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.map.GameMap;

// TODO fare interfaccia per spell e armi gg
public abstract class StaticSpell extends Spell implements Offensive
{
	protected static final float PHYSICS_WIDTH_ADJUSTMENT = 5.0f;

	private ImageLayer layer;
	
	public StaticSpell(float x, float y, IDimension size, Player player, boolean hasLayer)
	{
		super(x, y, size, player);
		
		if (!hasLayer)
		{
			layer = graphics().createImageLayer(getImage());
			layer.setOrigin(getSize().width() / 2, getSize().height() / 2);
			layer.setTranslation(getFrameLocation().x() - getSize().width() / 2, getFrameLocation().y() - getSize().height() / 2);
			layer.setDepth(4.0f);
			layer.setVisible(false);
			
			if (player.isBackwarding() && player.isMoving())
				layer.setRotation((float)Math.toRadians(Direction.getOppositeDirection(player.getFacingDirection()).getDegree()));
			
			else
				layer.setRotation((float)Math.toRadians(player.getFacingDirection().getDegree()));
			
			graphics().rootLayer().add(layer);
		}
		
	}
	
	public StaticSpell(float x, float y, IDimension size, Player player)
	{
		this(x, y, size, player, false);
	}
	
	public abstract Image getImage();
	
	@Override
	public void drawFrame(Surface surface)
	{
	}
	
	@Override
	public ImageLayer getLayer()
	{
		return layer;
	}
	
	public void setLayer(ImageLayer layer)
	{
		this.layer = layer;
	}
	
	public void bodyTransform(DynamicEntity entity, Direction lastDirection)
	{
		if (lastDirection == Direction.DOWN)
			getBody().setTransform(entity.getBody().getPosition().add(
				new Vec2((lastDirection.x() * getSize().width() / 2 + 5.0f)/ GameMap.PTM_RATIO, 
						(lastDirection.y() * getSize().height() + 10.0f) / GameMap.PTM_RATIO)), lastDirection.getAngle());
		
		else
			getBody().setTransform(entity.getBody().getPosition().add(
				new Vec2((lastDirection.x() * getSize().width() / 2 + 5.0f) / GameMap.PTM_RATIO, 
						(lastDirection.y() * getSize().height() + 20.0f) / GameMap.PTM_RATIO)), lastDirection.getAngle());
	}
	
	@Override
	public void clear()
	{
		super.clear();
		layer.destroy();
	}
	
	@Override
	public void update(int delta)
	{
		super.update(delta);
		getLayer().setTranslation(getFrameLocation().x() - getSize().width() / 2 + x() - getPlayer().x(),
				getFrameLocation().y() - getSize().height() / 2 + y() - getPlayer().y());
		
		if (!visible())
			layer.setVisible(true);
	}
}
