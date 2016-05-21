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

public class Cyclone extends AnimatedSpell
{
	// TODO sistemare interfaccia e questi campi (che vanno bene pubblici)
	private static final IDimension SIZE = new Dimension(60.0f, 51.0f);
	private static final String PATH = "images/spells/cyclone.png";
	private static final IPoint FRAME_LOCATION = new Point(graphics().width() / 2, graphics().height() / 2);
	private static final float RADIUS = 80.0f;
	public static final float MIN_DAMAGE = 0.0f;
	public static final float MAX_DAMAGE = 0.0f;
	public static final float CRITICAL_CHANCE = 0.0f;
	public static final float COOLDOWN = 15000.0f;
	public static final float PHYSICS = 100.0f;
	public static final float MANA_COST = 5.0f;
	public static final float KNOCKBACK_RATE = 0.0f;
	public static final float BASE_CYCLONE_DURATION = 3000.0f;
	
	private static Image image;
	private float cycloneDurationModifier;
	
	public Cyclone(float x, float y, Player player, float cycloneDurationModifier)
	{
		super(x, y, SIZE, player);
		this.cycloneDurationModifier = cycloneDurationModifier;
		initRadialBody(BodyType.DYNAMIC, RADIUS, Material.AIR, 0x0028, 0xFFFF & ~0x0032);
		setSprite(new Sprite(image, getFrameDuration(), SIZE.width(), SIZE.height()));
		getLayer().setVisible(true);
		getLayer().setDepth(0.0f);
	}
	
	public static void loadAssets(AssetWatcher watcher)
	{
		image = assets().getImage(PATH);
		watcher.add(image);
	}
	
	@Override
	public void applyEffect()
	{
		
	}
	
	@Override
	public void applyEffect(Enemy enemy)
	{
		getSprite().resetDelta();
		enemy.cyclone(BASE_CYCLONE_DURATION + cycloneDurationModifier);
	}
	
	@Override
	public float getFrameDuration()
	{
		return 40.0f;
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
		return MANA_COST;
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
		getLayer().setTranslation(+5, 0);
		getSprite().update(delta);
		
		if (getSprite().isOver(Animation.IDLE))
			clear();
	}
}
