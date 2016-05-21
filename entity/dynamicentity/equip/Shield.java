package saga.progetto.tesi.entity.dynamicentity.equip;

import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;
import org.jbox2d.common.Vec2;
import playn.core.AssetWatcher;
import playn.core.Image;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import pythagoras.f.IPoint;
import pythagoras.f.Point;
import saga.progetto.tesi.entity.Animation;
import saga.progetto.tesi.entity.Sprite;
import saga.progetto.tesi.entity.dynamicentity.Direction;
import saga.progetto.tesi.entity.dynamicentity.DynamicEntity;
import saga.progetto.tesi.map.GameMap;

public class Shield extends Equip
{
	private static final IDimension SIZE = new Dimension(18.0f, 18.0f);
	
	private static final IPoint FRAME_LOCATION = new Point(graphics().width() / 2, graphics().height() / 2);
	private static final String PATH = "images/equip/shield2.png";
	private static Image image;
	
	public Shield(float x, float y, GameMap map, boolean isEnemyEquip)
	{
		super(x, y, SIZE, map, Material.SHIELD, isEnemyEquip);
		setSprite(new Sprite(image, getFrameDuration(), getLayerSize().width(), getLayerSize().height()));
		getSprite().setLastDirection(Direction.UP);
	}
	
	public Shield(float x, float y, GameMap map)
	{
		this(x, y, map, false);
	}
	
	//TODO sistemare problema coordinate qui e spear (magari lavorare con size e fare tentativi)
	public void bodyTransform(DynamicEntity entity, Direction lastDirection)
	{
		getBody().setTransform(entity.getBody().getPosition().add(
				new Vec2(lastDirection.x() * 20 / GameMap.PTM_RATIO, lastDirection.y() * 20 / GameMap.PTM_RATIO)), 
				lastDirection.getAngle());
	}
	
	public static void loadAssets(AssetWatcher watcher)
	{
		image = assets().getImage(PATH);
		watcher.add(image);
	}
	
	public boolean visible()
	{
		return getCurrentAnimation() == Animation.WEAPON;
	}
	
	@Override
	public IPoint getFrameLocation()
	{
		return FRAME_LOCATION;
	}

	@Override
	public IDimension getSize()
	{
		return SIZE;
	}
}
