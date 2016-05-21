package saga.progetto.tesi.entity.dynamicentity;

import static playn.core.PlayN.assets;
import java.util.Random;
import org.jbox2d.common.Vec2;
import playn.core.AssetWatcher;
import playn.core.Image;
import playn.core.Surface;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import pythagoras.f.Point;
import saga.progetto.tesi.core.TheKnowledgeTowers;
import saga.progetto.tesi.entity.Animation;
import saga.progetto.tesi.entity.Effect;
import saga.progetto.tesi.entity.Offensive;
import saga.progetto.tesi.entity.PhysicsEntity;
import saga.progetto.tesi.entity.Renderable;
import saga.progetto.tesi.entity.Sprite;
import saga.progetto.tesi.entity.dynamicentity.enemy.Enemy;
import saga.progetto.tesi.entity.dynamicentity.equip.Equip;
import saga.progetto.tesi.entity.dynamicentity.pet.Pet;
import saga.progetto.tesi.entity.dynamicentity.spell.Spell;
import saga.progetto.tesi.map.GameMap;
import saga.progetto.tesi.navigable.Gameloop;

public abstract class DynamicEntity extends PhysicsEntity implements Renderable
{
	private static final float FRAME_DURATION = 120.0f;
	private static final IDimension SLEEP_SIZE = new Dimension(46.0f, 64.0f);
	private static final IDimension FEAR_SIZE = new Dimension(25.0f, 25.0f);
	private static final IDimension STUN_SIZE = new Dimension(75.0f, 95.0f);
	private static final IDimension CYCLONE_SIZE = new Dimension(100.0f, 100.0f);
	private static final float DEPTH = 6.1f;
	private static final String SLEEP_PATH = "images/spells/sleep.png";
	private static final String FEAR_PATH = "images/spells/fear_effect.png";
	private static final String STUN_PATH = "images/spells/stun_effect.png";
	private static final String CYCLONE_PATH = "images/spells/cyclone_effect.png";
	private static Image sleepImage;
	private static Image fearImage;
	private static Image stunImage;
	private static Image cycloneImage;
	
	private Direction lastDirection = Direction.DEFAULT;
	private Sprite sprite;
	private Animation animation;
	private IDimension size;
	private DynamicEntity lastHitter;
	private Effect sleepEffect;
	private Effect fearEffect;
	private Effect stunEffect;
	private Effect cycloneEffect;
	private int level;
	private float speedModifier = 1.0f;
	private float speed;
	private float totalLife;
	private float currentLife;
	private float totalEndurance;
	private float currentEndurance;
	private float totalRage;
	private float currentRage;
	private float totalMana;
	private float currentMana;
	private float xImpulse;
	private float yImpulse;
	private float currentKnockbackSpeed;
	private float slowTime;
	private float currentSlowTime;
	private float fearTime;
	private float currentFearTime;
	private float poisonTime;
	private float poisonDamage;
	private float sleepTime;
	private float stunTime;
	private float currentStunTime;
	private float silenceTime;
	private float currentSilenceTime;
	private float disarmTime;
	private float currentDisarmTime;
	private float cycloneTime;
	private float currentCycloneTime;
	private boolean isDead;
	private boolean isFrozen;
	private boolean isFeared;
	private boolean isPoisoned;
	private boolean isSleeping;
	private boolean isStunned;
	private boolean isSilenced;
	private boolean isDisarmed;
	private boolean isCycloned;
	
	public DynamicEntity(float x, float y, GameMap map)
	{
		super(x, y, map);
	}
	
	public static void loadAssets(AssetWatcher watcher)
	{
		Enemy.loadAssets(watcher);
		Pet.loadAssets(watcher);
		Npc.loadAssets(watcher);
		Equip.loadAssets(watcher);
		Spell.loadAssets(watcher);
		
		sleepImage = assets().getImage(SLEEP_PATH);
		watcher.add(sleepImage);
		fearImage = assets().getImage(FEAR_PATH);
		watcher.add(fearImage);
		stunImage = assets().getImage(STUN_PATH);
		watcher.add(stunImage);
		cycloneImage = assets().getImage(CYCLONE_PATH);
		watcher.add(cycloneImage);
	}
	
	public int getLevel()
	{
		return level;
	}

	public void setLevel(int level)
	{
		this.level = level;
	}
	
	public void gainLevel()
	{
		level++;
		setCurrentLife(getTotalLife());
		setCurrentEndurance(getTotalEndurance());
		setCurrentMana(getTotalMana());
	}
	
	public void setSpeed(float speed)
	{
		this.speed = speed;
	}

	// ritorna il modificatore di velocità dell'entità.
	public float getSpeedModifier()
	{
		return speedModifier;
	}
	
	// modifica il il modificatore di velocità corrente.
	public void setSpeedModifier(float speedModifier)
	{
		this.speedModifier = speedModifier;
	}
	
	@Override
	public IDimension getSize()
	{
		return size;
	}

	public void setSize(IDimension size)
	{
		this.size = size;
	}
	
	@Override
	public void setCurrentAnimation(Animation currentAnimation)
	{
		this.animation = currentAnimation;
	}
	
	@Override
	public Animation getCurrentAnimation()
	{
		return animation;
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
	
	public float getTotalLife()
	{
		return totalLife;
	}

	public void setTotalLife(float totalLife)
	{
		this.totalLife = totalLife;
	}

	public float getCurrentLife()
	{
		return currentLife;
	}

	public void setCurrentLife(float currentLife)
	{
		this.currentLife = currentLife;
	}
	
	public void heal(float life)
	{
		if (getTotalLife() >= currentLife + life)
			currentLife += life;
		
		else
			currentLife = getTotalLife();
	}
	
	public float getTotalEndurance()
	{
		return totalEndurance;
	}

	public void setTotalEndurance(float totalEndurance)
	{
		this.totalEndurance = totalEndurance;
	}

	public float getCurrentEndurance()
	{
		return currentEndurance;
	}

	public void setCurrentEndurance(float currentEndurance)
	{
		this.currentEndurance = currentEndurance;
	}
	
	public float getTotalRage()
	{
		return totalRage;
	}

	public void setTotalRage(float totalRage)
	{
		this.totalRage = totalRage;
	}

	public float getCurrentRage()
	{
		return currentRage;
	}

	public void setCurrentRage(float currentRage)
	{
		this.currentRage = currentRage;
	}
	
	public float getTotalMana()
	{
		return totalMana;
	}

	public void setTotalMana(float totalMana)
	{
		this.totalMana = totalMana;
	}

	public float getCurrentMana()
	{
		return currentMana;
	}

	public void setCurrentMana(float currentMana)
	{
		this.currentMana = currentMana;
	}

	public boolean isDead()
	{
		return isDead;
	}

	public void setDead(boolean isDead)
	{
		this.isDead = isDead;
		
		if (sleepEffect != null)
		{
			isSleeping = false;
			sleepEffect.clear();
			sleepEffect = null;
		}
		
		if (fearEffect != null)
		{
			isFeared = false;
			fearEffect.clear();
			fearEffect = null;
		}
		
		if (stunEffect != null)
		{
			isStunned = false;
			stunEffect.clear();
			stunEffect = null;
		}
		
		if (cycloneEffect != null)
		{
			isCycloned = false;
			cycloneEffect.clear();
			cycloneEffect = null;
		}
	}
	
	public boolean isWolf()
	{
		return false;
	}

	public void dead()
	{
		setDead(true);
		getSprite().resetDelta();
		setCurrentLife(0.0f);
		setCurrentRage(0.0f);
		setLastDirection(Direction.DEFAULT);
		
		if (isWolf() && getCurrentAnimation() != Animation.WOLF_DEATH)
			setCurrentAnimation(Animation.WOLF_DEATH);
		
		else if (getCurrentAnimation() != Animation.DEATH)
			setCurrentAnimation(Animation.DEATH);
	}
	
	public void takeDamage(DynamicEntity hitter, Offensive offensive, float damage)
	{
		if (getCurrentLife() - damage <= 0 && !isDead)
			dead();
		
		else
		{
			setCurrentLife(getCurrentLife() - damage);
			xImpulse = offensive.getKnockbackRate();
			yImpulse = offensive.getKnockbackRate();
			lastHitter = hitter;
			currentKnockbackSpeed = Offensive.KNOCKBACK_RECOVER;
			
			if (isSleeping)
			{
				isSleeping = false;
				sleepEffect.clear();
				sleepEffect = null;
			}
			
			if (isFeared)
			{
				isFeared = false;
				fearEffect.clear();
				fearEffect = null;
			}
		}
	}
	
	public void takeDamage(float damage)
	{
		setCurrentLife(getCurrentLife() - damage);
		
		if (getCurrentLife() < 1 && !isDead)
			dead();
		
		if (isSleeping)
		{
			isSleeping = false;
			sleepEffect.clear();
			sleepEffect = null;
		}
		
		if (isFeared)
		{
			isFeared = false;
			fearEffect.clear();
			fearEffect = null;
		}
	}

	public void takeSpellDamage(DynamicEntity hitter, Spell spell, float spellDamage)
	{
		if (spell.cannotMiss() || new Random().nextFloat() > getMagicResist())
			takeDamage(hitter, spell, spellDamage);
		
		if (isSleeping && sleepEffect != null)
		{
			isSleeping = false;
			sleepEffect.clear();
			sleepEffect = null;
		}
		
		if (isFeared && fearEffect != null)
		{
			isFeared = false;
			fearEffect.clear();
			fearEffect = null;
		}
	}
	
	public void takePoisonDamage(float poisonDamage, float poisonTime)
	{
		setCurrentLife(getCurrentLife() - poisonDamage);
		setPoisoned(true);
		this.poisonDamage = poisonDamage;
		this.poisonTime = poisonTime;
		
		if (isSleeping)
		{
			isSleeping = false;
			sleepEffect.clear();
			sleepEffect = null;
		}
		
		if (isFeared)
		{
			isFeared = false;
			fearEffect.clear();
			fearEffect = null;
		}
	}
	
	public void removeSlow()
	{
		setFrozen(false);
		setCurrentSlowTime(0.0f);
		setSlowTime(0.0f);
		setSpeedModifier(getDefaultSpeed());
	}
	
	public void slow(float slowTime, float slowValue)
	{
		setFrozen(true);
		setCurrentSlowTime(0.0f);
		setSlowTime(slowTime);
		setSpeedModifier(getDefaultSpeed() * slowValue);
	}
	
	public void sleep(float sleepTime)
	{
		if (!isCycloned)
		{
			if (isPoisoned)
				setPoisoned(false);
			
			if (isFrozen)
				isFrozen = false;
			
			if (isFeared)
			{
				isFeared = false;
				fearEffect.clear();
				fearEffect = null;
			}
			
			if (isStunned)
			{
				isStunned = false;
				stunEffect.clear();
				stunEffect = null;
			}
			
			if (sleepEffect == null)
			{
				sleepEffect = new Effect(sleepImage, getFrameDuration(), SLEEP_SIZE, DEPTH, Animation.CC_SLOW, this);
				sleepEffect.setOffset(new Point(30.0f, 60.0f));
			}
			
			isSleeping = true;
			
			if (sleepTime == -1.0f)
				this.sleepTime = 2147483647.0f;
			else
				this.sleepTime = sleepTime;
			
			setCurrentAnimation(Animation.IDLE);
			setLastDirection(Direction.DEFAULT);
		}
	}
	
	public void awake()
	{
		sleepTime = 0.0f;
	}
	
	public void fear(float fearTime)
	{
		if (!isCycloned)
		{
			if (isPoisoned)
				setPoisoned(false);
			
			if (isFrozen)
				isFrozen = false;
			
			if (isSleeping)
			{
				isSleeping = false;
				sleepEffect.clear();
				sleepEffect = null;
			}
			
			if (isStunned)
			{
				isStunned = false;
				stunEffect.clear();
				stunEffect = null;
			}
			
			if (fearEffect == null)
			{
				fearEffect = new Effect(fearImage, getFrameDuration(), FEAR_SIZE, DEPTH, Animation.CC_FAST, this);
				fearEffect.setOffset(new Point(0.0f, 60.0f));
			}
			
			isFeared = true;
			this.fearTime = fearTime;
			this.currentFearTime = 0;
			
			setCurrentAnimation(Animation.RUN);
		}
	}
	
	public void stun(float stunTime)
	{
		if (!isCycloned)
		{
			if (stunEffect == null)
				stunEffect = new Effect(stunImage, getFrameDuration(), STUN_SIZE, DEPTH, Animation.CC_FAST, this);
			
			setStunned(true);
			setCurrentStunTime(0.0f);
			setStunTime(stunTime);
			
			if (!isDead)
			{
				setCurrentAnimation(Animation.IDLE);
				setLastDirection(Direction.DEFAULT);
			}
		}
	}
	
	public void silence(float silenceTime)
	{
		if (!isCycloned)
		{
			setSilenced(true);
			setCurrentSilenceTime(0.0f);
			setSilenceTime(silenceTime);
			
			if (!isDead)
			{
				setCurrentAnimation(Animation.IDLE);
				setLastDirection(Direction.DEFAULT);
			}
		}
	}
	
	public void disarm(float disarmTime)
	{
		if (!isCycloned)
		{
			setDisarmed(true);
			setCurrentDisarmTime(0.0f);
			setDisarmTime(disarmTime);
			
			if (!isDead)
			{
				setCurrentAnimation(Animation.IDLE);
				setLastDirection(Direction.DEFAULT);
			}
		}
	}
	
	public void cyclone(float cycloneTime)
	{
		if (cycloneEffect == null)
		{
			cycloneEffect = new Effect(cycloneImage, getFrameDuration(), CYCLONE_SIZE, DEPTH, Animation.CC_FAST, this);
			cycloneEffect.setOffset(new Point(4.0f, 0.0f));
		}
		
		if (isSleeping)
		{
			isSleeping = false;
			sleepEffect.clear();
			sleepEffect = null;
		}
		
		if (isFeared)
		{
			isFeared = false;
			fearEffect.clear();
			fearEffect = null;
		}
		
		if (isStunned)
		{
			isStunned = false;
			stunEffect.clear();
			stunEffect = null;
		}

		setCycloned(true);
		setActive(false);
		setCurrentCycloneTime(0.0f);
		setCycloneTime(cycloneTime);
		
		if (!isDead)
			setCurrentAnimation(Animation.IDLE);
	}
	
	public float getMagicResist()
	{
		return 0;
	}
	
	public float getDefaultSpeed()
	{
		return 1.0f;
	}

	public void setPosition(Direction direction)
	{
		lastDirection = direction;
		setX(x() + direction.x() * getSpeed() * direction.getSpeedAdjustment());
		setY(y() + direction.y() * getSpeed() * direction.getSpeedAdjustment());
	}
	
	// ritorna la durata di un frame
	@Override
	public float getFrameDuration() 
	{
		return FRAME_DURATION;
	}
	
	// ritorna la velocità dell'entità.
	public float getSpeed()
	{
		return speed * getSpeedModifier();
	}
	
	public float getSlowTime()
	{
		return slowTime;
	}

	public void setSlowTime(float slowTime)
	{
		this.slowTime = slowTime;
	}
	
	public float getCurrentSlowTime()
	{
		return currentSlowTime;
	}

	public void setCurrentSlowTime(float currentSlowTime)
	{
		this.currentSlowTime = currentSlowTime;
	}

	public float getFearTime()
	{
		return fearTime;
	}
	
	public void setFearTime(float fearTime)
	{
		this.fearTime = fearTime;
	}
	
	public float getCurrentFearTime()
	{
		return currentFearTime;
	}

	public void setCurrentFearTime(float currentFearTime)
	{
		this.currentFearTime = currentFearTime;
	}
	
	public float getPoisonTime()
	{
		return poisonTime;
	}

	public void setPoisonTime(float poisonTime)
	{
		this.poisonTime = poisonTime;
	}

	public float getPoisonDamage()
	{
		return poisonDamage;
	}

	public void setPoisonDamage(float poisonDamage)
	{
		this.poisonDamage = poisonDamage;
	}

	public float getSleepTime()
	{
		return sleepTime;
	}

	public void setSleepTime(float sleepTime)
	{
		this.sleepTime = sleepTime;
	}
	
	public float getStunTime()
	{
		return stunTime;
	}
	
	public void setStunTime(float stunTime)
	{
		this.stunTime = stunTime;
	}
	
	public float getCurrentStunTime()
	{
		return currentStunTime;
	}
	
	public void setCurrentStunTime(float currentStunTime)
	{
		this.currentStunTime = currentStunTime;
	}
	
	public float getSilenceTime()
	{
		return silenceTime;
	}
	
	public void setSilenceTime(float silenceTime)
	{
		this.silenceTime = silenceTime;
	}
	
	public float getCurrentSilenceTime()
	{
		return currentSilenceTime;
	}

	public void setCurrentSilenceTime(float currentSilenceTime)
	{
		this.currentSilenceTime = currentSilenceTime;
	}
	
	public float getDisarmTime()
	{
		return disarmTime;
	}
	
	public void setDisarmTime(float disarmTime)
	{
		this.disarmTime = disarmTime;
	}
	
	public float getCurrentDisarmTime()
	{
		return currentDisarmTime;
	}

	public void setCurrentDisarmTime(float currentDisarmTime)
	{
		this.currentDisarmTime = currentDisarmTime;
	}
	
	public float getCycloneTime()
	{
		return cycloneTime;
	}
	
	public void setCycloneTime(float cycloneTime)
	{
		this.cycloneTime = cycloneTime;
	}
	
	public float getCurrentCycloneTime()
	{
		return currentCycloneTime;
	}
	
	public void setCurrentCycloneTime(float currentCycloneTime)
	{
		this.currentCycloneTime = currentCycloneTime;
	}
	
	public boolean isFrozen()
	{
		return isFrozen;
	}

	public void setFrozen(boolean isFrozen)
	{
		this.isFrozen = isFrozen;
	}

	public boolean isFeared()
	{
		return isFeared;
	}
	
	public void setFeared(boolean isFeared)
	{
		this.isFeared = isFeared;
	}
	
	public boolean isPoisoned()
	{
		return isPoisoned;
	}

	public void setPoisoned(boolean isPoisoned)
	{
		this.isPoisoned = isPoisoned;
	}

	public boolean isSleeping()
	{
		return isSleeping;
	}

	public void setSleeping(boolean isSleeping)
	{
		this.isSleeping = isSleeping;
	}

	public boolean isStunned()
	{
		return isStunned;
	}

	public void setStunned(boolean isStunned)
	{
		this.isStunned = isStunned;
	}
	
	public boolean isSilenced()
	{
		return isSilenced;
	}
	
	public void setSilenced(boolean isSilenced)
	{
		this.isSilenced = isSilenced;
	}
	
	public boolean isDisarmed()
	{
		return isDisarmed;
	}
	
	public void setDisarmed(boolean isDisarmed)
	{
		this.isDisarmed = isDisarmed;
	}
	
	public boolean isCycloned()
	{
		return isCycloned;
	}
	
	public void setCycloned(boolean isCycloned)
	{
		this.isCycloned = isCycloned;
	}
	
	public boolean isCrowdControlled()
	{
		return isStunned || isFeared || isSleeping || isCycloned;
	}

	public boolean isHidden()
	{
		return false;
	}
	
	public boolean isEvading()
	{
		return false;
	}
	
	public Gameloop getGameloop()
	{
		return getMap().getGameloop();
	}
	
	public Direction getLastDirection()
	{
		return lastDirection;
	}
	
	public Direction getFacingDirection()
	{
		return lastDirection;
	}
	
	public void setLastDirection(Direction lastDirection)
	{
		this.lastDirection = lastDirection;
	}
	
	public void setLastDirection(float targetX, float targetY)
	{
		if (targetX > x() && targetY > y())
			this.lastDirection = Direction.BOTTOM_RIGHT;
		
		else if (targetX < x() && targetY < y())
			this.lastDirection = Direction.BOTTOM_LEFT;
		
		else if (targetX > x() && targetY < y())
			this.lastDirection = Direction.TOP_RIGHT;
		
		else if (targetX < x() && targetY < y())
			this.lastDirection = Direction.TOP_LEFT;

		else if (targetX > x())
			this.lastDirection = Direction.RIGHT;

		else if (targetX < x())
			this.lastDirection = Direction.LEFT;
		
		else if (targetY > y())
			this.lastDirection = Direction.DOWN;

		else if (targetY < y())
			this.lastDirection = Direction.UP;
		
		else
			this.lastDirection = Direction.DEFAULT;
	}
	
	@Override
	public void drawFrame(Surface surface)
	{
		surface.drawImage(getCurrentFrame(), getFrameLocation().x() - getLayerSize().width() / 2,
				getFrameLocation().y() -  getLayerSize().height() / 2);
		
		if (isSleeping() && getCurrentAnimation() != Animation.DEATH)
			sleepEffect.drawFrame(surface);
		
		if (isFeared() && getCurrentAnimation() != Animation.DEATH)
			fearEffect.drawFrame(surface);
		
		if (isStunned() && getCurrentAnimation() != Animation.DEATH)
			stunEffect.drawFrame(surface);

		if (isCycloned() && getCurrentAnimation() != Animation.DEATH)
			cycloneEffect.drawFrame(surface);
	}
	
	public IDimension getLayerSize()
	{
		return getSize();
	}
	
	@Override
	public void clear()
	{
		super.clear();
		
		if (sleepEffect != null)
		{
			isSleeping = false;
			sleepEffect.clear();
			sleepEffect = null;
		}
		
		if (fearEffect != null)
		{
			isFeared = false;
			fearEffect.clear();
			fearEffect = null;
		}
		
		if (stunEffect != null)
		{
			isStunned = false;
			stunEffect.clear();
			stunEffect = null;
		}
		
		if (cycloneEffect != null)
		{
			isCycloned = false;
			cycloneEffect.clear();
			cycloneEffect = null;
		}
	}
	
	private void knockback()
	{
			setVelocity(1 * lastHitter.getFacingDirection().x() * lastHitter.getFacingDirection().getSpeedAdjustment() * getSpeed() * 2, 
					1 * lastHitter.getFacingDirection().y() * lastHitter.getFacingDirection().getSpeedAdjustment() * getSpeed() * 2);
			
			xImpulse -= currentKnockbackSpeed;
			yImpulse -= currentKnockbackSpeed;
	}
	
	@Override
	public void update(int delta) 
	{
		sprite.update(delta);
		
		if (xImpulse >= 1 && yImpulse >= 1)
			knockback();
		
		else 
			getBody().setLinearVelocity(new Vec2(getLastDirection().x() * getLastDirection().getSpeedAdjustment() * getSpeed(), 
				getLastDirection().y() * getLastDirection().getSpeedAdjustment() * getSpeed()));
		
		if (poisonTime <= 0.0f && isPoisoned())
		{
			setPoisoned(false);
			poisonTime = 0.0f;
			poisonDamage = 0.0f;
		}
		
		if (isPoisoned)
		{
			poisonTime -= delta;
			
			if (poisonTime % 1000 >= 0 && poisonTime % 1000 <= TheKnowledgeTowers.UPDATE_RATE)
				takeDamage(poisonDamage);
		}
		
		if (isSleeping)
		{
			sleepEffect.update(delta);
			sleepTime -= delta;
		}

		if (sleepTime <= 0.0f)
		{
			isSleeping = false;
			sleepTime = 0.0f;
			
			if (sleepEffect != null)
			{
				sleepEffect.clear();
				sleepEffect = null;
			}
		}
		
		if (currentSlowTime > slowTime)
		{
			if (isFrozen)
				removeSlow();
			
			setSpeedModifier(getDefaultSpeed());
		}
		
		else
			currentSlowTime += delta;
		
		if (isStunned)
		{
			stunEffect.update(delta);
			
			currentStunTime += delta;
			
			if (currentStunTime > stunTime)
			{
				isStunned = false;
				
				if (stunEffect != null)
				{
					stunEffect.clear();
					stunEffect = null;
				}
			}
		}
		
		else
			currentStunTime += delta;
		
		if (isSilenced)
		{
			currentSilenceTime += delta;
			
			if (currentSilenceTime > silenceTime)
				isSilenced = false;
		}
		
		else
			currentSilenceTime += delta;
		
		if (isDisarmed)
		{
			currentDisarmTime += delta;
			
			if (currentDisarmTime > disarmTime)
				isDisarmed = false;
		}
		
		else
			currentDisarmTime += delta;
		
		if (isFeared)
		{
			fearEffect.update(delta);
			currentFearTime += delta;

			if (currentFearTime > fearTime)
			{
				isFeared = false;
				
				if (fearEffect != null)
				{
					fearEffect.clear();
					fearEffect = null;
				}
			}
		}
		
		if (isCycloned)
		{
			cycloneEffect.update(delta);
			currentCycloneTime += delta;
			
			if (currentCycloneTime % (cycloneTime / 8) >= 0 || currentCycloneTime % (currentCycloneTime / 8) < TheKnowledgeTowers.UPDATE_RATE)
			{
				setLastDirection(Direction.getNextDirection(getLastDirection()));
				getSprite().setLastDirection(getLastDirection());
			}
			
			if (currentCycloneTime > cycloneTime)
			{
				setActive(true);
				isCycloned = false;
				
				if (cycloneEffect != null)
				{
					cycloneEffect.clear();
					cycloneEffect = null;
				}
			}
		}
		
		else
			currentCycloneTime += delta;
	}
}
