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

public class LevelUp extends AnimatedSpell
{
	private static final IDimension SIZE = new Dimension(192.0f, 192.0f);
	private static final String PATH = "images/spells/levelup_effect.png";
	private static final IPoint FRAME_LOCATION = new Point(graphics().width() / 2, graphics().height() / 2);
	public static final float CRITICAL_CHANCE = 0.0f;
	public static final float COOLDOWN = 0.0f;
	public static final float COST = 0.0f;
	public static final float KNOCKBACK_RATE = 0.0f;
	private static Image image;

	public float stunModifier;
	
	public LevelUp(Player player)
	{
		super(player.x(), player.y(), SIZE, player);
		initPhysicalBody(BodyType.DYNAMIC, getSize().width(), getSize().height(), Material.LIVING, 0x0028, 0xFFFF);
		setSprite(new Sprite(image, getFrameDuration(), SIZE.width(), SIZE.height()));
		getLayer().setVisible(false);
		getLayer().setDepth(3.0f);
		setSensor(true);
	}

	public static void loadAssets(AssetWatcher watcher)
	{
		image = assets().getImage(PATH);
		watcher.add(image);
	}

	@Override
	public void applyEffect()
	{
		bodyTransform(getPlayer(), Direction.DEFAULT);
		getSprite().resetDelta();
		getSprite().setLastDirection(Direction.DEFAULT);
		getLayer().setVisible(true);
	}
	
	@Override
	public void applyEffect(Enemy enemy)
	{
	}

	public boolean visible()
	{
		return getLayer().visible();
	}
	
	@Override
	public IPoint getFrameLocation()
	{
		return FRAME_LOCATION;
	}

	@Override
	public float getFrameDuration()
	{
		return 45.0f;
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
		return COST;
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
	}
	
	@Override
	public void update(int delta) 
	{
		getSprite().update(delta);
		
		if (getSprite().isOver(Animation.IDLE))
			getLayer().setVisible(false);
	}
}

