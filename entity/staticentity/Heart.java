package saga.progetto.tesi.entity.staticentity;

import static playn.core.PlayN.assets;
import org.jbox2d.dynamics.BodyType;
import playn.core.AssetWatcher;
import playn.core.Image;
import saga.progetto.tesi.entity.Animation;
import saga.progetto.tesi.entity.Sprite;
import saga.progetto.tesi.map.GameMap;

public class Heart extends Drop
{
	private static final String HEART_PATH = "images/static_objects/heart.png";
	private static Image heartImage;
	
	private Animation animation;
	private HeartType type;
	private boolean isTaken;
	
	public Heart(float x, float y, GameMap map, HeartType type)
	{
		super(x, y, map, "");
		initPhysicalBody(BodyType.STATIC, getWidth(), getHeight(), Material.FABRIC, 0x032, ~0xFFFF);
		this.type = type;
		getBody().getFixtureList().setSensor(true);
		setSprite(new Sprite(heartImage.subImage(0, 32 * type.getIndex(), 32, 32), getFrameDuration(), getSize().width(), getSize().height()));
		animation = Animation.STATIC_OBJECT_DEFAULT;
		setVisible(true);
	}
	
	public enum HeartType
	{
		SMALL(0, 5.0f), MAX(1, 15.0f);
		
		private int index;
		private float life;
		
		HeartType(int index, float life)
		{
			this.index = index;
			this.life = life;
		}
		
		public int getIndex()
		{
			return index;
		}
		
		public float getLife()
		{
			return life;
		}
	}
	
	public static void loadAssets(AssetWatcher watcher)
	{
		heartImage = assets().getImage(HEART_PATH);
		watcher.add(heartImage);
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
		return heartImage;
	}

	public HeartType getType()
	{
		return type;
	}

	public boolean isOpen()
	{
		return isTaken;
	}

	public void setTaken(boolean isTaken)
	{
		this.isTaken = isTaken;
		if (isTaken)
			clear();
	}
}
