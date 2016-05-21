package saga.progetto.tesi.entity.dynamicentity.pet;

// TODO
// terremoto

import static playn.core.PlayN.graphics;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;
import playn.core.AssetWatcher;
import pythagoras.f.IDimension;
import pythagoras.f.IPoint;
import pythagoras.f.Point;
import saga.progetto.tesi.entity.Animation;
import saga.progetto.tesi.entity.dynamicentity.Direction;
import saga.progetto.tesi.entity.dynamicentity.DynamicEntity;
import saga.progetto.tesi.entity.dynamicentity.GroupRenderedDynamicEntity;
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.entity.dynamicentity.enemy.Enemy;
import saga.progetto.tesi.entity.dynamicentity.equip.PhysicsWeapon;
import saga.progetto.tesi.gui.NPCBar;
import saga.progetto.tesi.map.GameMap;

public abstract class Pet extends GroupRenderedDynamicEntity
{
	private static final float DEPTH = 0.1f;
	protected static final float SUMMON_SPOT = 15.0f;
	
	private IPoint frameLocation;
	private Player player;
	private PhysicsWeapon weapon;
	private DynamicEntity currentFollowed;
	private NPCBar bar;
	private float viewDistance;
	private boolean deadCheck;
	private boolean isFollowing;
	private boolean hasTaunt;
	
	public Pet(Player player, IDimension size)
	{
		super(player.x() + SUMMON_SPOT, player.y(), size, player.getMap(), DEPTH);
		this.player = player;
		this.currentFollowed = player;
		//TODO gestire collisioni
		initPhysicalBody(BodyType.DYNAMIC, getSize().width() - 5, getSize().height() - 5, Material.LIVING, 0x0044, 0xFFFF & ~0x0008 & ~0x0002);
		setSpeed(1.0f);
		frameLocation = new Point(graphics().width() / 2 - (int)player.x() + x(), 
				graphics().height() / 2 - (int)player.y() + y());
	}
	
	public static void loadAssets(AssetWatcher watcher) 
	{
		Bear.loadAssets(watcher);
	}
	
	@Override
	public IPoint getFrameLocation()
	{
		return frameLocation;
	}
	
	public void setVisible(boolean visible)
	{
		super.setVisible(visible);
		bar.setVisible(visible);
	}
	
	public abstract float getDamageModifier();
	
	public abstract float getDefaultSpeed();
	
	public NPCBar getBar()
	{
		return bar;
	}

	public void setBar(NPCBar bar)
	{
		this.bar = bar;
	}
	
	public float getViewDistance()
	{
		return viewDistance;
	}
	
	public DynamicEntity getCurrentFollowed()
	{
		return currentFollowed;
	}

	public void setViewDistance(float viewDistance)
	{
		this.viewDistance = viewDistance;
	}

	public void equipUpdate(int delta)
	{
		weapon.update(delta);
		weapon.bodyTransform(this, getLastDirection());
		
		if(isWeaponVisible() && weapon.getSprite().isOver(getCurrentAnimation()))
			setWeaponVisibility(false);
	}

	public PhysicsWeapon getWeapon()
	{
		return weapon;
	}
	
	public void setWeapon(PhysicsWeapon weapon)
	{
		this.weapon = weapon;
	}
	
	public Body getWeaponBody()
	{
		return weapon.getBody();
	}

	public float getWeaponDamage()
	{
		return weapon.getWeaponDamage();
	}
	
	public float getCriticalDamage(float critic)
	{
		return weapon.getDamage() * critic;
	}
	
	public boolean hasEndurance()
	{
		return getCurrentEndurance() >= weapon.getCost();
	}
	
	public void attack()
	{
		getSprite().resetDelta();
		setCurrentAnimation(Animation.ATTACK);
		setCurrentEndurance(getCurrentEndurance() - weapon.getCost());
		setWeaponVisibility(true);
		weapon.getSprite().resetDelta();
		weapon.setSensor(false);
		float currentDamage = getWeaponDamage() * getDamageModifier();
		currentFollowed.takeDamage(this, weapon, currentDamage);
	}
	
	@Override
	public void clear()
	{
		super.clear();
		bar.getLayer().destroy();
		weapon.clear();
	}
	
	@Override
	public void setDead(boolean isDead)
	{
		super.setDead(isDead);
		getMap().getBodies().remove(getBody());
		getMap().getBodies().remove(getWeaponBody());
		getMap().getWorld().destroyBody(getBody());
		getMap().getWorld().destroyBody(getWeaponBody());
	}
	
	public void setClosestEnemy()
	{
		currentFollowed = getMap().getEnemies().get(0);
		
		for (Enemy enemy : getMap().getEnemies())
		{
			if (Math.abs(x() - currentFollowed.x()) > Math.abs(x() - enemy.x()) && 
					Math.abs(y() - currentFollowed.y()) > Math.abs(y() - enemy.y()))
				currentFollowed = enemy;
		}
	}
	
	public void setMovement()
	{
		if (getCurrentAnimation() != Animation.DEATH)
		{
			if (currentFollowed.isDead())
				currentFollowed = player;
			
			if (!player.inCombat() && currentFollowed instanceof Enemy)
				currentFollowed = player;
			
			if (player.inCombat())
			{
				if(currentFollowed.equals(player) && getMap().getEnemies().size() > 0)
					setClosestEnemy();
				
				weapon.setActive(true);
				
				if (!isFollowing)
					isFollowing = true;
			}
			
			else if (weapon.isActive())
				weapon.setActive(false);
			
			if (!isTargetClose() || (player.inCombat() && !currentFollowed.isCycloned()))
			{
				setLastDirection(currentFollowed.x(), currentFollowed.y());
				
				if (!isFollowing)
					isFollowing = true;
			}
			
			else if (isTargetClose() || currentFollowed.isCycloned())
			{
				setLastDirection(Direction.DEFAULT);
				setCurrentAnimation(Animation.IDLE);
				
				if (isFollowing)
					isFollowing = false;
			}
		}
	}
	
	private boolean isTargetClose()
	{
		return 	(Math.abs(currentFollowed.x() - x()) < getSize().width() && Math.abs(currentFollowed.y() - y()) < getSize().height());
	}
	
	public void setLastDirection(float x, float y)
	{
		if (x < x() && y < y())
			setLastDirection(Direction.TOP_LEFT);
		
		else if (x > x() && y < y())
			setLastDirection(Direction.TOP_RIGHT);
		
		else if (x < x() && y > y())
			setLastDirection(Direction.BOTTOM_LEFT);
		
		else if (x > x() && y > y())
			setLastDirection(Direction.BOTTOM_RIGHT);
		
		else if (y > y())
			setLastDirection(Direction.DOWN);
		
		else if (y < y())
			setLastDirection(Direction.UP);
		
		else if (x > x())
			setLastDirection(Direction.RIGHT);
		
		else if (x < x())
			setLastDirection(Direction.LEFT);
		
		else
			setLastDirection(Direction.DOWN);
		
		if (getLastDirection() != Direction.DEFAULT)
		{
			if (getCurrentAnimation() != Animation.ENEMY_ATTACK)
				setCurrentAnimation(Animation.RUN);

			Direction adjustmentDirection = Direction.DEFAULT;
			if ((getLastDirection() == Direction.TOP_RIGHT || getLastDirection() == Direction.BOTTOM_RIGHT || 
					getLastDirection() == Direction.RIGHT) && currentFollowed.y() - y() < 4 && currentFollowed.x() > x() + 10)
				adjustmentDirection = Direction.RIGHT;
			
			else if ((getLastDirection() == Direction.TOP_LEFT || getLastDirection() == Direction.BOTTOM_LEFT ||
					getLastDirection() == Direction.LEFT) && currentFollowed.y() - y() < 4 && currentFollowed.x() < x() - 10)
				adjustmentDirection = Direction.LEFT;
			
			else if ((getLastDirection() == Direction.TOP_LEFT || getLastDirection() == Direction.TOP_RIGHT ||
					getLastDirection() == Direction.UP) && currentFollowed.x() - x() < 4 && currentFollowed.y() < y())
				adjustmentDirection = Direction.UP;
			
			else if ((getLastDirection() == Direction.BOTTOM_LEFT || getLastDirection() == Direction.BOTTOM_RIGHT ||
					getLastDirection() == Direction.DOWN)  && currentFollowed.x() - x() < 4 && currentFollowed.y() > y())
				adjustmentDirection = Direction.DOWN;
			
			getSprite().setLastDirection(adjustmentDirection);
			weapon.setLastDirection(adjustmentDirection);			
		}
	}
	
	public boolean getDeadCheck()
	{
		return deadCheck;
	}

	public void setDeadCheck(boolean deadCheck)
	{
		this.deadCheck = deadCheck;
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	public void setWeaponVisibility(boolean visible)
	{
		if (visible)
			weapon.setCurrentAnimation(Animation.WEAPON);
		
		else
			weapon.setCurrentAnimation(Animation.NO_WEAPON);
	}
	
	public boolean isWeaponVisible()
	{
		return weapon.getCurrentAnimation() != Animation.NO_WEAPON;
	}
	

	public boolean hasTaunt()
	{
		return hasTaunt;
	}

	public void setHasTaunt(boolean hasTaunt)
	{
		this.hasTaunt = hasTaunt;
	}
	
	public void setNewMap(float x, float y, World world, GameMap map)
	{
		setWorld(world);
		setMap(map);
		initPhysicalBody(BodyType.DYNAMIC, getSize().width() - 5, getSize().height() - 5, Material.LIVING, 0x0044, 0xFFFF & ~0x0008 & ~0x0002);
		teleportBody(x, y);
	}
	
	public void teleportBody(float x, float y)
	{
		getBody().setTransform(new Vec2((x + SUMMON_SPOT) / GameMap.PTM_RATIO, y / GameMap.PTM_RATIO), player.getFacingDirection().getAngle());	
		
		if(currentFollowed.equals(player) && getMap().getEnemies().size() > 0)
			setClosestEnemy();
	}
	
	public void adjustStats()
	{
		if (getCurrentLife() < 0.0f) 
			setCurrentLife(0);
		
		if (getCurrentLife() > getTotalLife())
			setCurrentLife(getTotalLife());
		
		if (getCurrentEndurance() < 0.0f) 
			setCurrentEndurance(0);
		
		if (getCurrentEndurance() > getTotalEndurance())
			setCurrentEndurance(getTotalEndurance());
	}
	
	public void regen()
	{
		
		if (getCurrentEndurance() < getTotalEndurance())
			setCurrentEndurance(getCurrentEndurance() + 0.15f);
		
		if (!player.inCombat())
			setCurrentLife(getCurrentLife() + getTotalLife() * 0.0001f);
	}
	
	@Override
	public void update(int delta)
	{
		super.update(delta);
		frameLocation = new Point(graphics().width() / 2 - (int)player.x() + x(), graphics().height() / 2 - (int)player.y() + y());
		weapon.setFrameLocation(frameLocation);
		equipUpdate(delta);

		if (getCurrentLife() <= 0 && !isDead()) 
			setDead(true);
		
		if (getCurrentAnimation() == Animation.DEATH && getSprite().isOver(getCurrentAnimation()))
			clear();
		
		if (!isDead())
		{
			regen();
			adjustStats();
		}
		
		setMovement();
		bar.update(getCurrentLife());
		
		if (currentFollowed instanceof Enemy && getMap().getContactListener().isColliding(getWeaponBody(), currentFollowed.getBody())
				&& getCurrentEndurance() >= weapon.getCost())
			attack();
	}
}
