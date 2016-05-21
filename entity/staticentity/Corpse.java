package saga.progetto.tesi.entity.staticentity;

import static playn.core.PlayN.assets;
import org.jbox2d.dynamics.BodyType;
import playn.core.AssetWatcher;
import playn.core.Image;
import playn.core.Surface;
import saga.progetto.tesi.entity.Animation;
import saga.progetto.tesi.entity.Sprite;
import saga.progetto.tesi.map.GameMap;

public class Corpse extends StorableDrop
{
	private static final String BAG_PATH = "images/static_objects/corpse.png";
	private static Image bagImage;
	
	private Animation animation;
	private String mapId;
	
	public Corpse(float x, float y, GameMap map)
	{
		super(x, y, map, false, "corpse");
		initPhysicalBody(BodyType.STATIC, getWidth(), getHeight(), Material.FABRIC, 0x032, 0xFFFF);
		setSprite(new Sprite(bagImage, getFrameDuration(), getSize().width(), getSize().height()));
		animation = Animation.STATIC_OBJECT_DEFAULT;
		setVisible(true);
		getMap().getStorableDrops().add(this);
		setSensor(true);
	}
	
	public static void loadAssets(AssetWatcher watcher)
	{
		bagImage = assets().getImage(BAG_PATH);
		watcher.add(bagImage);
	}
	
	@Override
	public Image getCurrentFrame()
	{
		return getSprite().getCurrentImage(animation);
	}
	
	@Override
	public void setCurrentAnimation(Animation currentAnimation)
	{
	}

	@Override
	public Animation getCurrentAnimation()
	{
		return animation;
	}
	
	@Override
	public Image getImage()
	{
		return bagImage;
	}

	public String getMapId()
	{
		return mapId;
	}
	
	public void setMapId(String mapId)
	{
		this.mapId = mapId;
	}
	
	public void initPhysicalBody()
	{
		initPhysicalBody(BodyType.STATIC, getWidth(), getHeight(), Material.FABRIC, 0x032, 0xFFFF);
		setSensor(true);
	}
	
	@Override
	public void drawFrame(Surface surface)
	{
		if (visible())
			surface.drawImage(getCurrentFrame(), getFrameLocation().x() - getSize().width() / 2, getFrameLocation().y() -  getSize().height() / 2);
	}

}
