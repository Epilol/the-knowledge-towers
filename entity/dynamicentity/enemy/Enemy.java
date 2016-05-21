package saga.progetto.tesi.entity.dynamicentity.enemy;

import static playn.core.PlayN.graphics;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import playn.core.AssetWatcher;
import playn.core.Font;
import pythagoras.f.IDimension;
import pythagoras.f.IPoint;
import pythagoras.f.Point;
import saga.progetto.tesi.core.TheKnowledgeTowers;
import saga.progetto.tesi.entity.Animation;
import saga.progetto.tesi.entity.dynamicentity.Direction;
import saga.progetto.tesi.entity.dynamicentity.DynamicEntity;
import saga.progetto.tesi.entity.dynamicentity.GroupRenderedDynamicEntity;
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.entity.dynamicentity.equip.PhysicsWeapon;
import saga.progetto.tesi.entity.staticentity.Bag;
import saga.progetto.tesi.entity.staticentity.Gold;
import saga.progetto.tesi.entity.staticentity.Page;
import saga.progetto.tesi.entity.staticentity.Potion;
import saga.progetto.tesi.entity.staticentity.Weapon;
import saga.progetto.tesi.gui.NPCBar;
import saga.progetto.tesi.map.GameMap;
import saga.progetto.tesi.media.Text;

public abstract class Enemy extends GroupRenderedDynamicEntity
{
	private static final float DEPTH = -0.5f;
	
	private IPoint frameLocation;
	private Player player;
	private Map<String, Float> dropRates = new HashMap<String, Float>();
	private PhysicsWeapon weapon;
	private NPCBar bar;
	private Path path;
	private String id;
	private Bag bag;
	private Direction oldDirection;
	private int levelDifference;
	private float viewDistance;
	private boolean isPathStart = true;
	private boolean isFollowing;
	private boolean isAttacked;
	private boolean hasBeenKilled;
	private boolean deadCheck;
	
	public Enemy(float x, float y, Player player, IDimension size, GameMap map, boolean hasBeenKilled, String id, float life)
	{
		super(x, y, size, map, DEPTH);
		this.id = id;
		this.player = player;
		initPhysicalBody(BodyType.DYNAMIC, getSize().width(), getSize().height(), Material.LIVING, 0x0004, 0xFFFF);
		setSpeed(1.0f);
		path = new Path(new Point(x, y), new Point());
		frameLocation = new Point(graphics().width() / 2 - (int)map.getPlayer().x() + x(), 
				graphics().height() / 2 - (int)map.getPlayer().y() + y());
		this.setHasBeenKilled(hasBeenKilled);
		levelDifference = getLevel() - player.getLevel();
		setTotalLife(getLifeBonus(life, player));
		setCurrentLife(getLifeBonus(life, player));
	}
	
	public static void loadAssets(AssetWatcher watcher) 
	{
		Angel.loadAssets(watcher);
		BlackKnight.loadAssets(watcher);
		Devil.loadAssets(watcher);
		Emeralda.loadAssets(watcher);
		Farebell.loadAssets(watcher);
		GoldKnight.loadAssets(watcher);
		Guard.loadAssets(watcher);
		Lucius.loadAssets(watcher);
		Merciless.loadAssets(watcher);
		Nemesis.loadAssets(watcher);
		Newbie.loadAssets(watcher);
		OldWarrior.loadAssets(watcher);
		Rivy.loadAssets(watcher);
		Saphir.loadAssets(watcher);
		Setian.loadAssets(watcher);
		Siegfried.loadAssets(watcher);
		SilverKnight.loadAssets(watcher);
		Soldier.loadAssets(watcher);
		Warden.loadAssets(watcher);
		Yojimbo.loadAssets(watcher);
	}
	
	public String getId()
	{
		return id;
	}

	public void setId(String id)
	{
		this.id = id;
	}

	@Override
	public IPoint getFrameLocation()
	{
		return frameLocation;
	}

	public float getLifeBonus(float life, Player player)
	{
		return ((((float) player.getLevel() - 1) / 10) + 1) * life * (player.getNewGame() * TheKnowledgeTowers.NEW_GAME_RATE + 1);
	}

	public void setLastDirection(float x, float y, DynamicEntity currentTarget, boolean isPet)
	{
		float directionAdjustment = 2.0f;
		
		if (isPet)
			directionAdjustment = 4.0f;
		
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
			
			if (isFollowing && !currentTarget.isHidden())
			{
				Direction adjustmentDirection = Direction.DEFAULT;
				//TODO ho aumentato +10 a destra per fixxare. da controllare
				if ((getLastDirection() == Direction.TOP_RIGHT || getLastDirection() == Direction.BOTTOM_RIGHT || 
						getLastDirection() == Direction.RIGHT) && currentTarget.y() - y() < directionAdjustment && currentTarget.x() > x() + 10)
					adjustmentDirection = Direction.RIGHT;
				
				else if ((getLastDirection() == Direction.TOP_LEFT || getLastDirection() == Direction.BOTTOM_LEFT ||
						getLastDirection() == Direction.LEFT) && currentTarget.y() - y() < directionAdjustment && currentTarget.x() < x() - 10)
					adjustmentDirection = Direction.LEFT;
				
				else if ((getLastDirection() == Direction.TOP_LEFT || getLastDirection() == Direction.TOP_RIGHT ||
						getLastDirection() == Direction.UP) && currentTarget.x() - x() < directionAdjustment && currentTarget.y() < y())
					adjustmentDirection = Direction.UP;
				
				else if ((getLastDirection() == Direction.BOTTOM_LEFT || getLastDirection() == Direction.BOTTOM_RIGHT ||
						getLastDirection() == Direction.DOWN)  && currentTarget.x() - x() < directionAdjustment && currentTarget.y() > y())
					adjustmentDirection = Direction.DOWN;
				
				getSprite().setLastDirection(adjustmentDirection);
				weapon.setLastDirection(adjustmentDirection);			
			}
			
			else if (currentTarget.isHidden())
			{
				getSprite().setLastDirection(getLastDirection());
				weapon.setLastDirection(getLastDirection());	
			}
		}

		else setCurrentAnimation(Animation.IDLE);
	}
	
	public void setVisible(boolean visible)
	{
		super.setVisible(visible);
		bar.setVisible(visible);
	}
	
	public abstract int getLevel();
	
	public abstract float getExpGiven();
	
	public float giveExp()
	{
		float experienceGiven = 1.0f;
		
		if (levelDifference >= 0 && levelDifference < 2)
			experienceGiven = getExpGiven() * (getLevelDifference() + 1);
		else if (levelDifference < 0)
			
			experienceGiven = getExpGiven() / (Math.abs(getLevelDifference()) + 1);
		else if (levelDifference > 3)
			
			experienceGiven = getExpGiven() * 3;
		
		return experienceGiven;
	}
	
	public int getLevelDifference()
	{
		return levelDifference;
	}
	
	// per la gestione dello speed modifier
	
	public abstract float getDefaultSpeed();
	
	@Override
	public void stun(float stunTime)
	{
		super.stun(stunTime);
		setWeaponVisibility(false);
	}
	
	@Override
	public void sleep(float sleepTime)
	{
		super.sleep(sleepTime);
		setWeaponVisibility(false);
		setCurrentAnimation(Animation.SLEEPING_ENEMY);
	}
	
	@Override
	public void fear(float fearTime)
	{
		super.fear(fearTime);
		oldDirection = getLastDirection();
	}
	
	@Override
	public void cyclone(float cycloneTime)
	{
		super.cyclone(cycloneTime);
		setWeaponVisibility(false);
	}
	
	public NPCBar getBar()
	{
		return bar;
	}

	public void setBar(NPCBar bar)
	{
		this.bar = bar;
	}
	
	public Path getPath()
	{
		return path;
	}

	public void setPath(Path path)
	{
		this.path = path;
	}
	
	public void setEndPath(IPoint endPoint)
	{
		path.setEndPoint(endPoint);
	}

	public boolean isFollowing()
	{
		return isFollowing;
	}

	public float getViewDistance()
	{
		return viewDistance;
	}

	public void setViewDistance(float viewDistance)
	{
		this.viewDistance = viewDistance;
	}

	public void equipUpdate(int delta)
	{
		weapon.update(delta);
		weapon.bodyTransform(this, getLastDirection());
		
		if(weapon.getLayer().visible() && weapon.getSprite().isOver(getCurrentAnimation()))
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
	
	public boolean isAttacked()
	{
		return isAttacked;
	}

	public void setAttacked(boolean isAttacked)
	{
		this.isAttacked = isAttacked;
	}

	public void attack(boolean isDefending, DynamicEntity currentTarget)
	{
		getSprite().resetDelta();
		setCurrentAnimation(Animation.ENEMY_ATTACK);
		
		setCurrentEndurance(getCurrentEndurance() - weapon.getCost());
		setWeaponVisibility(true);
		weapon.getSprite().resetDelta();
		weapon.setSensor(false);
		float currentDamage = getWeaponDamage();
		
		if (!currentTarget.isEvading())
		{
			if (isDefending)
			{
				if (currentTarget.getCurrentEndurance() - currentDamage < 0)
					currentTarget.setCurrentEndurance(0);
				
				else
					currentTarget.setCurrentEndurance(currentTarget.getCurrentEndurance() - currentDamage);
			}
			
			else
				currentTarget.takeDamage(this, weapon, currentDamage);
		}
	}
	
	public void setWeaponVisibility(boolean visible)
	{
		if (visible)
			getWeapon().setCurrentAnimation(Animation.ENEMY_WEAPON);
		
		else
			getWeapon().setCurrentAnimation(Animation.NO_WEAPON);
	}
	
	public boolean isWeaponVisible()
	{
		return getWeapon().getCurrentAnimation() != Animation.NO_WEAPON;
	}
	
	public void setPoisonText(String damage)
	{
		Text text = new Text(damage, Font.Style.BOLD, 12, 0xFFa7f785);
		Random random = new Random();
		IPoint randomPoint = new Point(graphics().width() / 2 + (100 - random.nextInt(35)) * (random.nextInt(2) * -1),
				graphics().height() / 2 + (100 - random.nextInt(35)) * (random.nextInt(2) * -1));
		text.setTranslation(randomPoint);
		text.setDepth(6.0f);
		text.setVisible(true);
		text.init();
		getMap().getHitNumbers().add(text);
	}
	
	public void setFearDirection()
	{
		if (oldDirection == Direction.UP)
			setLastDirection(Direction.DOWN);
		
		else if (oldDirection == Direction.TOP_RIGHT)
			setLastDirection(Direction.BOTTOM_LEFT);
		
		else if (oldDirection == Direction.RIGHT)
			setLastDirection(Direction.LEFT);
		
		else if (oldDirection == Direction.BOTTOM_RIGHT)
			setLastDirection(Direction.TOP_LEFT);
		
		else if (oldDirection == Direction.DOWN)
			setLastDirection(Direction.UP);
		
		else if (oldDirection == Direction.BOTTOM_LEFT)
			setLastDirection(Direction.TOP_RIGHT);
		
		else if (oldDirection == Direction.LEFT)
			setLastDirection(Direction.RIGHT);
		
		else
			setLastDirection(Direction.BOTTOM_RIGHT);
		
		getSprite().setLastDirection(getLastDirection());
		getWeapon().setLastDirection(getLastDirection());
	}
	
	public boolean hasBeenKilled()
	{
		return hasBeenKilled;
	}

	public void setHasBeenKilled(boolean hasBeenKilled)
	{
		this.hasBeenKilled = hasBeenKilled;
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
		if (isDead)
		{
			if (levelDifference != getLevel() - player.getLevel())
				levelDifference = getLevel() - player.getLevel();
			
			player.gainExp(giveExp());
			player.getPlayerStats().increaseKilledEnemies();
			getMap().getBodies().remove(getBody());
			getMap().getBodies().remove(getWeaponBody());
			getMap().getWorld().destroyBody(getBody());
			getMap().getWorld().destroyBody(getWeaponBody());
		}
	}
	
	public boolean petIsCloser(DynamicEntity pet)
	{
		float playerDistance = Math.abs(player.x() - x()) + Math.abs(player.y() - y());
		float petDistance =  Math.abs(pet.x() - x()) + Math.abs(pet.y() - y());
		return playerDistance > petDistance;
	}
	
	public void setMovement()
	{
		if (getCurrentAnimation() != Animation.DEATH && !isCycloned())
		{
			DynamicEntity currentTarget = player;
			boolean isPet = false;
			
			if (player.getPet() != null && player.getPet().hasTaunt() && petIsCloser(player.getPet()))
			{
				currentTarget = player.getPet();
				isPet = true;
			}
			
			if ((!currentTarget.isHidden()) && (getMap().inRange(new Point(currentTarget.x(), currentTarget.y()), this) || 
					player.inCombat()) && !isFeared() && !isCycloned() && !player.isDead())
			{
				setLastDirection(currentTarget.x(), currentTarget.y(), currentTarget, isPet);
				
				isFollowing = true;
				
				if (getCurrentAnimation() == Animation.ENEMY_ATTACK && getSprite().isOver(getCurrentAnimation()))
				{
					setCurrentAnimation(Animation.IDLE);
					weapon.setSensor(true);
				}
					
				weapon.setActive(true);
			}
			
			else
			{
				setSpeedModifier(1.0f);
				weapon.setActive(false);
				isFollowing = false;

				if (path.getStartPoint().equals(path.getEndPoint()) && !isFeared())
				{
					if (path.getStartPoint().x() != x() || path.getStartPoint().y() != y() && getLastDirection() != Direction.DEFAULT)
						setLastDirection(path.getStartPoint().x(), path.getStartPoint().y(), player, false);
					else
					{
						setLastDirection(Direction.DEFAULT);
						getSprite().setLastDirection(Direction.DOWN);
						setCurrentAnimation(Animation.IDLE);
					}
				}

				else if (!isFeared() && !isCycloned())
				{
					if (isPathStart)
						setLastDirection(path.getEndPoint().x(), path.getEndPoint().y(), player, false);

					else
						setLastDirection(path.getStartPoint().x(), path.getStartPoint().y(), player, false);
					
					if (x() == path.getStartPoint().x() && y() == path.getStartPoint().y())
						isPathStart = true;
					
					else if (x() == path.getEndPoint().x() && y() == path.getEndPoint().y())
						isPathStart = false;
					
					getSprite().setLastDirection(getLastDirection());
				}
			}
		}
	}
	
	public Bag getBag()
	{
		return bag;
	}

	public void initBag(List<Potion> potions, List<Weapon> weapons, List<Page> pages, float golds)
	{
		bag = new Bag(x(), y(), getMap(), false, "bag" + id);
		bag.addAllPages(pages);
		bag.addAllWeapons(weapons);
		bag.addAllPotions(potions);
		
		if (golds > 0)
			bag.addGolds(new Gold(golds, bag.getItems().size()));
		
		bag.initDrops();
		setHasBeenKilled(true);
	}
	
	public boolean getDeadCheck()
	{
		return deadCheck;
	}

	public void setDeadCheck(boolean deadCheck)
	{
		this.deadCheck = deadCheck;
	}

	@Override
	public void dead()
	{
		super.dead();
		getWeapon().setVisible(false);
	}
	
	public abstract void checkDrops();
	
	public java.util.Map<String, Float> getDropRates()
	{
		return dropRates;
	}
	
	public void addDropRate(String drop, float rate)
	{
		dropRates.put(drop, rate);
	}
	
	public abstract void initDropRates();
	
	@Override
	public void update(int delta)
	{
		super.update(delta);
		
		if (isFeared())
		{
			setLastDirection(oldDirection);
			getSprite().setLastDirection(oldDirection);
		
			if (oldDirection == getLastDirection())
				setFearDirection();
			
			if (getCurrentFearTime() > getFearTime())
				getWeapon().setLastDirection(oldDirection);			
		}
		
		if (!isSleeping() && !isStunned())
		{
			setMovement();
			equipUpdate(delta);
		}
		
		frameLocation = new Point(graphics().width() / 2 - (int)player.x() + x(), 
				graphics().height() / 2 - (int)player.y() + y());
		weapon.setFrameLocation(frameLocation);

		if (getCurrentLife() <= 0 && !isDead()) 
			setDead(true);
		
		if (getCurrentAnimation() == Animation.DEATH && getSprite().isOver(getCurrentAnimation()))
		{
			checkDrops();
			clear();
			getMap().getEnemies().remove(this);
		}
		
		bar.update(getCurrentLife());
		
		if (isPoisoned() && getPoisonTime() % 1000 >= 0 && getPoisonTime() % 1000 <= TheKnowledgeTowers.UPDATE_RATE)
				setPoisonText(String.valueOf(getPoisonDamage() + 1).replaceAll("\\..*$", ""));
	}
}
