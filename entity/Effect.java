package saga.progetto.tesi.entity;

import playn.core.Image;
import playn.core.Surface;
import pythagoras.f.IDimension;
import pythagoras.f.IPoint;
import pythagoras.f.Point;
import saga.progetto.tesi.entity.dynamicentity.DynamicEntity;
import saga.progetto.tesi.navigable.Gameloop;

public class Effect implements Renderable
{
	private Gameloop gameloop;
	private Sprite sprite;
	private Animation animation;
	private IDimension size;
	private IPoint frameLocation;
	private DynamicEntity entity;
	private IPoint offset;
	private float depth;
	private float frameDuration;

	public Effect(Image image, float frameDuration, IDimension size, float depth, Animation animation, DynamicEntity entity)
	{
		this.gameloop = entity.getGameloop();
		this.size = size;
		this.depth = depth;
		this.frameDuration = frameDuration;
		this.animation = animation;
		this.entity = entity;
		sprite = new Sprite(image, frameDuration, size.width(), size.height());
		gameloop.addRenderable(this, depth);
		frameLocation = entity.getFrameLocation();
		offset = new Point();
	}

	@Override
	public void drawFrame(Surface surface)
	{
		surface.drawImage(sprite.getCurrentImage(animation), getFrameLocation().x() + offset.x() - size.width() / 2,
			getFrameLocation().y() - offset.y() -  size.height() / 2);		
	}

	@Override
	public IPoint getFrameLocation()
	{
		return frameLocation;
	}

	@Override
	public Sprite getSprite()
	{
		return sprite;
	}

	@Override
	public void setSprite(Sprite sprite)
	{
		this.sprite = sprite;
	}

	@Override
	public Image getCurrentFrame()
	{
		return sprite.getCurrentImage(animation);
	}

	@Override
	public void setCurrentAnimation(Animation currentAnimation)
	{
		animation = currentAnimation;
	}

	@Override
	public Animation getCurrentAnimation()
	{
		return animation;
	}

	public void setOffset(Point offset)
	{
		this.offset = offset;
	}

	@Override
	public float getFrameDuration()
	{
		return frameDuration;
	}
	
	@Override
	public void clear()
	{
		gameloop.removeRenderable(this, depth);
	}

	@Override
	public void update(int delta)
	{
		frameLocation = entity.getFrameLocation();
		sprite.update(delta);
	}
}