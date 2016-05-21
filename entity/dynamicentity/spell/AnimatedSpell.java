package saga.progetto.tesi.entity.dynamicentity.spell;

import org.jbox2d.common.Vec2;
import pythagoras.f.IDimension;
import pythagoras.f.Point;
import saga.progetto.tesi.entity.Offensive;
import saga.progetto.tesi.entity.dynamicentity.Direction;
import saga.progetto.tesi.entity.dynamicentity.DynamicEntity;
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.map.GameMap;

// TODO fare interfaccia per spell e armi gg
public abstract class AnimatedSpell extends Spell implements Offensive
{
	private Point frameLocation;
	
	public AnimatedSpell(float x, float y, IDimension size, Player player)
	{
		super(x, y, size, player);
		getLayer().setDepth(4.0f);
	}
	
	public void bodyTransform(DynamicEntity entity, Direction lastDirection)
	{
		if (lastDirection == Direction.DOWN)
		getBody().setTransform(entity.getBody().getPosition().add(
				new Vec2((lastDirection.x() * getSize().width() / 2)/ GameMap.PTM_RATIO, 
						(lastDirection.y() * getSize().height() - 7.0f) / GameMap.PTM_RATIO)), lastDirection.getAngle());
		else
			getBody().setTransform(entity.getBody().getPosition().add(
					new Vec2((lastDirection.x() * getSize().width() / 2)/ GameMap.PTM_RATIO, 
							(lastDirection.y() * getSize().height() + 9.0f) / GameMap.PTM_RATIO)), lastDirection.getAngle());
	}
	
	@Override
	public void update(int delta)
	{
		super.update(delta);
		frameLocation = new Point(x() - (int)getPlayer().x(), 
				y() - (int)getPlayer().y());
		getLayer().setTranslation(frameLocation.x(), frameLocation.y());
	}
}
