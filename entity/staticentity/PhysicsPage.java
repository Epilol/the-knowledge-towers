package saga.progetto.tesi.entity.staticentity;

import static playn.core.PlayN.assets;
import org.jbox2d.dynamics.BodyType;
import playn.core.AssetWatcher;
import playn.core.Image;
import saga.progetto.tesi.entity.Animation;
import saga.progetto.tesi.entity.Sprite;
import saga.progetto.tesi.map.GameMap;

public class PhysicsPage extends StorableDrop
{
	private static final Animation animation = Animation.STATIC_OBJECT_DEFAULT;
	private static final String PAGE_PATH = "images/static_objects/page.png";
	private static Image pageImage;
	public static final float PAGE_WIDTH = 50.0f;
	public static final float PAGE_HEIGHT = 50.0f;
	
	public PhysicsPage(float x, float y, GameMap map, boolean isTaken, String id)
	{
		super(x, y, map, isTaken, id);
		initPhysicalBody(BodyType.STATIC, getWidth(), getHeight(), Material.FABRIC, 0x032, 0xFFFF);
		setSprite(new Sprite(pageImage, getFrameDuration(), getSize().width(), getSize().height()));
		setSensor(true);
	}
	
	public PhysicsPage(float x, float y, GameMap map)
	{
		this(x, y, map, false, "");
	}
	
	public void addPage(Page page)
	{
		getItems().add(page);
	}
	
	public static void loadAssets(AssetWatcher watcher)
	{
		watcher.add(pageImage = assets().getImage(PAGE_PATH));
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
		return pageImage;
	}

	@Override
	public float getWidth()
	{
		return PAGE_WIDTH;
	}
	
	@Override
	public float getHeight()
	{
		return PAGE_WIDTH;
	}
}
