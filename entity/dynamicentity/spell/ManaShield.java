package saga.progetto.tesi.entity.dynamicentity.spell;

import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;
import playn.core.AssetWatcher;
import playn.core.Image;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import pythagoras.f.IPoint;
import pythagoras.f.Point;
import saga.progetto.tesi.entity.Sprite;
import saga.progetto.tesi.entity.dynamicentity.Direction;
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.entity.dynamicentity.enemy.Enemy;

public class ManaShield extends AnimatedSpell
{
	// TODO sistemare interfaccia e questi campi (che vanno bene pubblici)
	private static final IDimension SIZE = new Dimension(24.0f, 28.0f);
	private static final String PATH = "images/spells/mana_shield.png";
	private static final IPoint FRAME_LOCATION = new Point(graphics().width() / 2, graphics().height() / 2);
	public static final float CRITICAL_CHANCE = 0.0f;
	public static final float COOLDOWN = 20000.0f;
	public static final float DURATION = 300000.0f;
	public static final float MANA_COST = 10.0f;
	public static final float DAMAGE_REDUCED = 0.95f;
	public static final float KNOCKBACK_RATE = 0.0f;
	private static Image image;
	
	public ManaShield(float x, float y, Player player)
	{
		super(x, y, SIZE, player);
		setSprite(new Sprite(image, getFrameDuration(), SIZE.width(), SIZE.height()));
		getLayer().setVisible(true);
	}
	
	public static void loadAssets(AssetWatcher watcher)
	{
		image = assets().getImage(PATH);
		watcher.add(image);
	}
	
	@Override
	public void applyEffect()
	{
		getPlayer().setManaShield(true);
	}
	
	@Override
	public void applyEffect(Enemy enemy)
	{

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
	public void clear()
	{
		getLayer().destroy();
	}

	@Override
	public void update(int delta) 
	{
		getLayer().setTranslation(0, -30);
		getSprite().update(delta);
	}
}
