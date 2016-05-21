package saga.progetto.tesi.entity.staticentity;

import static playn.core.PlayN.assets;
import org.jbox2d.dynamics.BodyType;
import playn.core.AssetWatcher;
import playn.core.Image;
import saga.progetto.tesi.entity.Animation;
import saga.progetto.tesi.entity.Sprite;
import saga.progetto.tesi.map.GameMap;

public class Book extends StorableDrop
{
	private static final Animation animation = Animation.STATIC_OBJECT_DEFAULT;
	private static final String BOOK_PATH = "images/static_objects/book.png";
	private static Image bookImage;
	
	public Book(float x, float y, GameMap map, boolean isTaken, String id)
	{
		super(x, y, map, isTaken, id);
		initPhysicalBody(BodyType.STATIC, getWidth(), getHeight(), Material.PAPER, 0x032, 0xFFFF);
		setSprite(new Sprite(bookImage, getFrameDuration(), getSize().width(), getSize().height()));
	}
	
	public Book(float x, float y, GameMap map)
	{
		this(x, y, map, false, "");
	}
	
	public static void loadAssets(AssetWatcher watcher)
	{
		bookImage = assets().getImage(BOOK_PATH);
		watcher.add(bookImage);
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
		return bookImage;
	}
}
