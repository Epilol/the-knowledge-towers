package saga.progetto.tesi.entity.dynamicentity.spell;

import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;
import org.jbox2d.dynamics.BodyType;
import playn.core.AssetWatcher;
import playn.core.Image;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import pythagoras.f.IPoint;
import pythagoras.f.Point;
import saga.progetto.tesi.entity.Animation;
import saga.progetto.tesi.entity.Sprite;
import saga.progetto.tesi.entity.dynamicentity.Direction;
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.entity.dynamicentity.enemy.Enemy;

public class EndTowerArea extends AnimatedSpell
{
	// TODO sistemare interfaccia e questi campi (che vanno bene pubblici)
	private static final IDimension SIZE = new Dimension(192.0f, 192.0f);
	private static final String PATH = "images/decoration/end_tower_effect.png";
	private static final IPoint FRAME_LOCATION = new Point(graphics().width() / 2, graphics().height() / 2);
	public static final float MIN_DAMAGE = 0.0f;
	public static final float MAX_DAMAGE = 0.0f;
	public static final float RADIUS = 30.0f;
	public static final float CRITICAL_CHANCE = 0.0f;
	public static final float COOLDOWN = 0.0f;
	public static final float PHYSICS = 100.0f;
	public static final float RAGE_COST = 5.0f;
	public static final float KNOCKBACK_RATE = 0.0f;
	
	private static Image image;

	public EndTowerArea(float x, float y, Player player)
	{
		super(x, y, SIZE, player);
		initRadialBody(BodyType.STATIC, RADIUS, Material.LIVING, 0x0040, 0xFFFF);
		setSensor(true);
		setSprite(new Sprite(image, getFrameDuration(), SIZE.width(), SIZE.height()));
		getLayer().setVisible(true);
		getLayer().setDepth(1.0f);
	}
	
	public static void loadAssets(AssetWatcher watcher)
	{
		image = assets().getImage(PATH);
		watcher.add(image);
	}
	
	@Override
	public void applyEffect()
	{
		getSprite().resetDelta();
	}
	
	@Override
	public void applyEffect(Enemy enemy)
	{

	}
	
	@Override
	public float getFrameDuration()
	{
		return 80.0f;
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

	@Override
	public float getCriticalChance()
	{
		return CRITICAL_CHANCE;
	}

	@Override
	public float getCost()
	{
		return RAGE_COST;
	}

	@Override
	public float getCooldown()
	{
		return COOLDOWN;
	}

	@Override
	public float getKnockbackRate()
	{
		return KNOCKBACK_RATE;
	}
	
	@Override
	public void setLastDirection(Direction lastDirection)
	{
		super.setLastDirection(lastDirection);
		getSprite().setLastDirection(lastDirection);
	}
	
	@Override
	public boolean destroyOnCollision()
	{
		return false;
	}
	
	@Override
	public void update(int delta) 
	{
		super.update(delta);
		if (getSprite().isOver(Animation.IDLE))
			getSprite().resetDelta();
	}
}
