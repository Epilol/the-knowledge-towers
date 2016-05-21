package saga.progetto.tesi.entity.staticentity;

import static playn.core.PlayN.assets;
import org.jbox2d.dynamics.BodyType;
import playn.core.AssetWatcher;
import playn.core.Image;
import saga.progetto.tesi.entity.Animation;
import saga.progetto.tesi.entity.Sprite;
import saga.progetto.tesi.map.GameMap;

public class Chest extends StorableDrop
{
	private static final String CHEST_PATH = "images/static_objects/chest.png";
	private static Image chestImage;
	
	private Animation animation;

	public Chest(float x, float y, GameMap map, boolean isTaken, boolean isOpen, String id)
	{
		super(x, y, map, isTaken, id);
		initPhysicalBody(BodyType.STATIC, getWidth(), getHeight(), Material.METAL, 0x032, 0xFFFF);
		setSprite(new Sprite(chestImage, getFrameDuration(), getSize().width(), getSize().height()));
		
		if (isOpen)
			animation = Animation.STATIC_OBJECT_USED;
		else
			animation = Animation.STATIC_OBJECT_DEFAULT;
	}
	
	public Chest(float x, float y, GameMap map)
	{
		this(x, y, map, false, false, "");
	}
	
	
	public static void loadAssets(AssetWatcher watcher)
	{
		chestImage = assets().getImage(CHEST_PATH);
		watcher.add(chestImage);
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
		return chestImage;
	}

	@Override
	public void setTaken()
	{
		getSprite().resetDelta();
		
		if (getCurrentAnimation() == Animation.STATIC_OBJECT_DEFAULT)
			animation = Animation.STATIC_OBJECT_ANIMATED;
	}
	
	@Override
	public void update(int delta)
	{
		super.update(delta);
		
		if (isOpen() && getSprite().isOver(Animation.STATIC_OBJECT_ANIMATED))
			animation = Animation.STATIC_OBJECT_USED;
	}
}
