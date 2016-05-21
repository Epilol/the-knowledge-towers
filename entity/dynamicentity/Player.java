package saga.progetto.tesi.entity.dynamicentity;

import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.World;
import playn.core.AssetWatcher;
import playn.core.Font;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Font.Style;
import playn.core.Key;
import playn.core.Keyboard.Event;
import playn.core.Mouse;
import playn.core.Mouse.ButtonEvent;
import pythagoras.f.Circle;
import pythagoras.f.Dimension;
import pythagoras.f.ICircle;
import pythagoras.f.IPoint;
import pythagoras.f.IRectangle;
import pythagoras.f.Point;
import pythagoras.f.Rectangle;
import saga.progetto.tesi.core.annotation.AnnotationStatistic;
import saga.progetto.tesi.entity.Animation;
import saga.progetto.tesi.entity.Offensive;
import saga.progetto.tesi.entity.Sprite;
import saga.progetto.tesi.entity.dynamicentity.enemy.Enemy;
import saga.progetto.tesi.entity.dynamicentity.equip.PhysicsWeapon;
import saga.progetto.tesi.entity.dynamicentity.pet.Pet;
import saga.progetto.tesi.entity.dynamicentity.spell.LevelUp;
import saga.progetto.tesi.entity.dynamicentity.spell.ManaShield;
import saga.progetto.tesi.entity.dynamicentity.spell.Spell;
import saga.progetto.tesi.entity.staticentity.Corpse;
import saga.progetto.tesi.entity.staticentity.Gold;
import saga.progetto.tesi.entity.staticentity.Item;
import saga.progetto.tesi.entity.staticentity.Page;
import saga.progetto.tesi.entity.staticentity.Potion;
import saga.progetto.tesi.entity.staticentity.StorableDrop;
import saga.progetto.tesi.entity.staticentity.Weapon;
import saga.progetto.tesi.gui.GameGUI;
import saga.progetto.tesi.gui.PlayerBar;
import saga.progetto.tesi.gui.PlayerBar.BarType;
import saga.progetto.tesi.gui.PopupWindow;
import saga.progetto.tesi.job.Berserker;
import saga.progetto.tesi.job.Druid;
import saga.progetto.tesi.job.Job;
import saga.progetto.tesi.job.BlackMage;
import saga.progetto.tesi.job.Ranger;
import saga.progetto.tesi.job.Thief;
import saga.progetto.tesi.job.Warrior;
import saga.progetto.tesi.job.Job.SkillType;
import saga.progetto.tesi.map.GameMap;
import saga.progetto.tesi.map.MapContactListener;
import saga.progetto.tesi.media.Text;
import saga.progetto.tesi.navigable.menu.JobMenu.JobType;

public class Player extends GroupRenderedDynamicEntity
{
	private static final String LEVEL_UP_TEXT_PATH = "images/menu/newpoints.png";
	private static final String DEAD_PATH = "images/gui/dieMessage.png";
	private static final IPoint TEXT_POINT = new Point(graphics().width() / 2, graphics().height() / 2 - 50.0f);
	private static final IPoint FRAME_LOCATION = new Point(graphics().width() / 2, graphics().height() / 2);
	private static final IRectangle DEAD_BUTTON = new Rectangle(new Point(345.0f, 247.0f), new Dimension(115.0f, 22.0f));
	private static final ICircle STRENGTH_UP = new Circle(new Point(26.0f, 497.0f), 12.5f);
	private static final ICircle DEX_UP = new Circle(new Point(26.0f, 537.0f), 12.5f);
	private static final ICircle INT_UP = new Circle(new Point(26.0f, 577.0f), 12.5f);
	private static final float DEPTH = 0.0f;
	public static final int MAX_ITEMS = 8;
	public static final int MAX_PAGES = 8;
	private static String name;
	private static PlayerStats stats = new PlayerStats();
	private static JobType startingJob;
	private static String aspect;
	private static Image levelUpTextImage;
	private static Image deadImage;
	
	private AnnotationStatistic statistic;
	private Direction facingDirection;
	private GameGUI gameGUI;
	private List<PlayerBar> bars;
	private Job currentJob;
	private Text combatText;
	private List<Item> items;
	private List<Spell> offensiveSpells;
	private List<Spell> defensiveSpells;
	private List<Page> pages;
	private List<Weapon> weapons;
	private LevelUp levelUpEffect;
	private ImageLayer levelUpButtonLayer;
	private ImageLayer deadLayer;
	private JobType defaultJob;
	private Pet pet;
	private boolean isSprinting;
	private boolean isAttacking;
	private boolean isPicking;
	private boolean isExhausted;
	private boolean isInsideTower;
	private boolean controlled;
	private boolean isEarthQuaking;
	private boolean hasSelectedItem;
	private float strength;
	private float dexterity;
	private float intelligence;
	private float experience;
	private float golds;
	private float outOfCombat;
	private int newGame;
	private int freePoints;
	private int negativeDiscarded;
	private int currentTower;
	private int usedPages;
	private boolean hasPassed;
	
	public Player(float x, float y, int imageOffsetX, int imageOffsetY, GameMap map)
	{
		super(x, y, stats.getSize(), map, DEPTH);
		setLevel(1);
		facingDirection = Direction.UP;
		hasPassed = true;
		
		if (startingJob == JobType.WARRIOR)
		{
			currentJob = new Warrior(x, y, this);
			defaultJob = JobType.WARRIOR;
		}
			
		else if(startingJob == JobType.BLACK_MAGE)
		{
			currentJob = new BlackMage(this);
			defaultJob = JobType.BLACK_MAGE;
		}
		
		else
		{
			currentJob = new Thief(x, y, this);
			defaultJob = JobType.THIEF;
		}
		
		currentTower = 1;
		setSprite(new Sprite(currentJob.getCharacterImage(), getFrameDuration(), currentJob.getSize().width(), currentJob.getSize().height()));
		// categoria 0x002, collide con tutti (0xFFFF)
		initPhysicalBody(BodyType.DYNAMIC, 32, 32, Material.LIVING, 0x0002, 0xFFFF);
		initStats();
		items = new LinkedList<Item>();
		pages = new LinkedList<Page>();
		weapons = new LinkedList<Weapon>();
		offensiveSpells = new LinkedList<Spell>();
		defensiveSpells = new LinkedList<Spell>();
		bars = new LinkedList<PlayerBar>();
		initBars();
		stats.setPlayer(this);
		levelUpButtonLayer = graphics().createImageLayer(levelUpTextImage);
		levelUpButtonLayer.setVisible(false);
		levelUpButtonLayer.setDepth(15.0f);
		levelUpButtonLayer.setTranslation(14.0f, 485.0f);
		graphics().rootLayer().add(levelUpButtonLayer);	
		deadLayer = graphics().createImageLayer(deadImage);
		deadLayer.setVisible(false);
		deadLayer.setDepth(7.0f);
		graphics().rootLayer().add(deadLayer);
	}
	
	public enum Statistic
	{
		STRENGTH, DEXTERITY, INTELLIGENCE;
	}
	
	public static void loadCharacter(String selectedCharacter) 
	{
		Job.loadCharacter(selectedCharacter);
	}
	
	public static void loadAssets(AssetWatcher watcher)
	{
		levelUpTextImage = assets().getImage(LEVEL_UP_TEXT_PATH);
		watcher.add(levelUpTextImage);
		deadImage = assets().getImage(DEAD_PATH);
		watcher.add(deadImage);
	}
	
	public static String getAspect()
	{
		return aspect;
	}
	
	public static void setAspect(String selectedCharacter)
	{
		aspect = selectedCharacter;
	}
	
	
	public float getStrength()
	{
		return strength;
	}

	public void setStrength(float strength)
	{
		this.strength = strength;
	}

	public float getDexterity()
	{
		return dexterity;
	}

	public void setDexterity(float dexterity)
	{
		this.dexterity = dexterity;
	}

	public float getIntelligence()
	{
		return intelligence;
	}

	public void setIntelligence(float intelligence)
	{
		this.intelligence = intelligence;
	}
	
	public float getCurrentExperience()
	{
		return experience;
	}
	
	public void setCurrentExperience(float experience)
	{
		this.experience = experience;
	}
	
	public float getTotalExperience()
	{
		return stats.getExpRequired() * getLevel();
	}

	public float getMagicResist()
	{
		return currentJob.getMagicResist();
	}
	
	public void gainExp(float xpPoints)
	{
		if (experience + xpPoints < getTotalExperience())
			experience += xpPoints;
		else
		{
			float restantPoints = getTotalExperience() - experience;
			xpPoints -= restantPoints;
			gainLevel();
			experience = xpPoints;
			freePoints += 3;
			save();
		}
	}
	
	@Override
	public void gainLevel()
	{
		super.gainLevel();
		
		if (getLevel() == 2)
			PopupWindow.getInstance().show("You gained a new experience level. You can train the new statistics by pressing ESC.");
		
		if (levelUpEffect == null)
			levelUpEffect = new LevelUp(this);
		
		for (PlayerBar bar : bars)
			if (bar.getType() == BarType.XP)
				bar.setTotal(getTotalExperience());
		
		levelUpButtonLayer.setVisible(true);
		levelUpEffect.applyEffect();
	}
	
	public boolean canTrain()
	{
		return freePoints > 0;
	}
	
	public void train(Statistic stat)
	{
		switch (stat)
		{
			case STRENGTH:  	strength++;
								setCurrentLife(getTotalLife());
								break;
			case DEXTERITY: 	dexterity++;
								setCurrentEndurance(getTotalEndurance());
								break;
			case INTELLIGENCE:  intelligence++;
								setCurrentMana(getTotalMana());
								break;
		}
		
		for (PlayerBar bar : bars)
		{
			if (bar.getType() == BarType.HEALTH)
			{
				bar.setCurrent(getCurrentLife());
				bar.setTotal(getTotalLife());
			}
			
			if (bar.getType() == BarType.ENDURANCE)
			{
				bar.setCurrent(getCurrentEndurance());
				bar.setTotal(getTotalEndurance());
			}
			
			if (bar.getType() == BarType.MANA)
			{
				bar.setCurrent(getCurrentMana());
				bar.setTotal(getTotalMana());
			}
			
			bar.updateBar(bar.getCurrent(), false);
		}
		
		freePoints--;
		
		if (freePoints == 0)
		{
			levelUpButtonLayer.setVisible(false);
			save();
		}
		
		gameGUI.destroyDescription();
		gameGUI.initTooltipTexts();
	}
	
	public int getFreePoints()
	{
		return freePoints;
	}
	
	public void setFreePoints(int freePoints)
	{
		this.freePoints = freePoints;
	}
	
	public GameGUI getGUI()
	{
		return gameGUI;
	}
	
	public PlayerStats getPlayerStats()
	{
		return stats;
	}
	
	@Override
	public float getTotalLife()
	{
		float totalLife = super.getTotalLife() + strength * currentJob.getStrengthModifier();
		
		// passiva warrior + 20% life
		if (currentJob instanceof Warrior)
			return totalLife + totalLife * 0.2f;
					
		return totalLife;
	}
	
	@Override
	public float getTotalEndurance()
	{
		return super.getTotalEndurance() + dexterity * currentJob.getDexterityModifier();
	}
	
	public float getTotalMana()
	{
		return super.getTotalMana() + intelligence * currentJob.getIntelligenceModifier();
	}
	
	public void initStats()
	{
		setStrength(stats.getStrength());
		setDexterity(stats.getDexterity());
		setIntelligence(stats.getIntelligence());
		setSpeed(stats.getSpeed());
		setTotalLife(stats.getLife());
		setCurrentLife(getTotalLife());
		setTotalEndurance(stats.getEndurance());
		setCurrentEndurance(getTotalEndurance());
		setTotalMana(stats.getMana());
		setCurrentMana(getTotalMana());
		setCurrentRage(stats.getStartingRage());
		setTotalRage(stats.getTotalRage());
		outOfCombat = stats.getCombatTime() + 1.0f;
	}
	
	
	public float getDefaultSpeed()
	{
		// passiva Ranger
		if (currentJob instanceof Ranger)
			return 1.3f;
		return 1.0f;
	}
	
	public void initBars()
	{
		gameGUI = new GameGUI(getMap().getGame());
		gameGUI.setJob(currentJob);
		bars.add(new PlayerBar(getCurrentLife(), getTotalLife(), BarType.HEALTH));
		bars.add(new PlayerBar(getCurrentEndurance(), getTotalEndurance(), BarType.ENDURANCE));
		bars.add(new PlayerBar(getCurrentMana(), getTotalMana(), BarType.MANA));
		bars.add(new PlayerBar(getCurrentExperience(), getTotalExperience(), BarType.XP));
		
		if (currentJob instanceof Berserker)
			bars.add(new PlayerBar(getCurrentRage(), getTotalRage(), BarType.RAGE));
		
		for (PlayerBar bar : bars)
			bar.setVisible(false);
	}
	
	public void loadBars()
	{
		for (PlayerBar bar : bars)
		{
			if (bar.getType() == BarType.HEALTH)
			{
				bar.setCurrent(getCurrentLife());
				bar.setTotal(getTotalLife());
			}
			
			if (bar.getType() == BarType.ENDURANCE)
			{
				bar.setCurrent(getCurrentEndurance());
				bar.setTotal(getTotalEndurance());
			}
			
			if (bar.getType() == BarType.MANA)
			{
				bar.setCurrent(getCurrentMana());
				bar.setTotal(getTotalMana());
			}
			
			if (bar.getType() == BarType.XP)
			{
				bar.setCurrent(getCurrentExperience());
				bar.setTotal(getTotalExperience());
			}
			
			bar.updateBar(bar.getCurrent(), false);
		}
		
		currentJob.getJobBar().setCurrent(currentJob.getJobExperience());
		currentJob.getJobBar().setTotal(stats.getJobExpRequired());
	}

	public List<PlayerBar> getBars()
	{
		return bars;
	}
	
	public Job getCurrentJob()
	{
		return currentJob;
	}
	
	public PhysicsWeapon getWeapon()
	{
		return currentJob.getWeapon();
	}
	
	public boolean hasWeapon()
	{
		return currentJob.hasWeapon();
	}
	
	public Body getWeaponBody()
	{
		return currentJob.getWeaponBody();
	}
	
	public boolean isHidden()
	{
		return currentJob.isHidden();
	}
	
	public void setHidden(boolean isHidden)
	{
		currentJob.setHidden(isHidden);
	}
	
	public boolean isEvading()
	{
		return currentJob.isEvading();
	}
	
	public void setEvading(boolean isEvading)
	{
		currentJob.setEvading(isEvading);
	}
	
	public boolean isMoving()
	{
		Set<Key> keyboard = getMap().getGame().getKeyboard();
		return keyboard.contains(Key.W) || keyboard.contains(Key.UP) || keyboard.contains(Key.A) || keyboard.contains(Key.LEFT) || 
				keyboard.contains(Key.S) || keyboard.contains(Key.DOWN) || keyboard.contains(Key.D) || keyboard.contains(Key.RIGHT);
	}
	
	public boolean isBackwarding()
	{
		return currentJob.isBackwarding();
	}
	
	public boolean hasManaShield()
	{
		return currentJob.hasManaShield();
	}
	
	public void setManaShield(boolean hasManaShield)
	{
		currentJob.setManaShield(hasManaShield);
	}
	
	public boolean isBerserking()
	{
		return currentJob.isBerserking();
	}
	
	public boolean isWolf()
	{
		return currentJob.isWolf();
	}
	
	@Override
	public void takeDamage(DynamicEntity hitter, Offensive offensive, float damage)
	{
		if (isBerserking() || isWolf())
		{
			if (hitter.getFacingDirection() == getFacingDirection())
				super.takeDamage(hitter, offensive, damage * 4.0f - damage * currentJob.getDamageReduced());
			else
				super.takeDamage(hitter, offensive, damage * 2.0f - damage * currentJob.getDamageReduced());
		}
		
		else if (hasManaShield())
		{
			super.takeDamage(hitter, offensive, damage * (1 - ManaShield.DAMAGE_REDUCED) - damage * currentJob.getDamageReduced());
			
			if (getCurrentMana() - (damage * ManaShield.DAMAGE_REDUCED) <= 0)
				setManaShield(false);
			
			setCurrentMana(getCurrentMana() - (damage * ManaShield.DAMAGE_REDUCED * 5));
		}
		
		else
		{
			if (hitter.getFacingDirection() == getFacingDirection())
				super.takeDamage(hitter, offensive, (damage - damage * currentJob.getDamageReduced()) * 2.0f);
		
			else
				super.takeDamage(hitter, offensive, (damage - damage * currentJob.getDamageReduced()));
		}
	}
	
	public void meleeAttack(Enemy enemy)
	{
		resetOutOfCombat();
		
		if (isExecuting() && enemy.getCurrentLife() <= enemy.getTotalLife() * currentJob.getUltimateSkillModifier())
		{
			enemy.dead();
			setAttackText(0xFFbbbbbb, 14.0f, Font.Style.BOLD, "Executed!");
		}
		
		else
		{
			float weaponDamage = currentJob.getWeaponDamage();
			float damageModifier = currentJob.getDamageModifier();
			int color = 0xFFe1ebf2;
			float size = 14.0f;
			Style style = Font.Style.PLAIN;
	
			if (enemy.getFacingDirection() == getFacingDirection() && !enemy.isFollowing() && !isHidden() && !isBerserking() && !isWolf())
				damageModifier = 2.0f * currentJob.getDamageModifier();
				
			if (isHidden())
			{
				damageModifier = 4.0f * currentJob.getDamageModifier();
				currentJob.setHidden(false);
				size = 16.0f;
			}
			
			if (isBerserking())
			{
				// moltiplicatore basato sulla quantita di rage corrente (da x1 a x3)
				damageModifier = (getCurrentRage() / getTotalRage() + 2.0f) * currentJob.getDamageModifier();
				size = 16.0f;
				color = 0xFFd42424;
				if (enemy.getCurrentLife() <= 0)
					setCurrentRage(getCurrentRage() + getTotalRage() / 2);
			}
			
			if (isWolf())
			{
				damageModifier = 2.0f * currentJob.getDamageModifier();
				size = 16.0f;
				color = 0xFF839eca;
			}
				
			float damage = weaponDamage * damageModifier + 1;
			setAttackText(color, size, style, String.valueOf(damage).replaceAll("\\..*$", ""));
			enemy.takeDamage(this, getWeapon(), damage);
			enemy.setAttacked(true);
		}
	}
	
	public float SpellAttack(Enemy enemy, Spell spell)
	{
		float enemyLife = enemy.getCurrentLife();
		float spellDamage = spell.getSpellDamage();
		enemy.takeSpellDamage(this, spell, spellDamage);
		resetOutOfCombat();
		
		if (!spell.removeText())
		{
			int color = 0xFFfdce68;
			float size = 14.0f;
			Style style = Font.Style.PLAIN;
			
			if (enemyLife - enemy.getCurrentLife() >= 2 * spell.getSpellDamage())
				style = Font.Style.PLAIN;
			else
				size = 12.0f;
			
			if (enemyLife - enemy.getCurrentLife() == .0f)
				setAttackText(color, size, style, "Miss");
			else 
				setAttackText(color, size, style, String.valueOf(spellDamage + 1).replaceAll("\\..*$", ""));
	
			enemy.setAttacked(true);
		}
		return spellDamage;
	}
	
	public void setAttackText(int color, float size, Style style, String damage)
	{
		Text text = new Text(damage, style, size, color);
		Random random = new Random();
		IPoint randomPoint = new Point(graphics().width() / 2 + (100 - random.nextInt(35)) * (random.nextInt(2) * -1),
				graphics().height() / 2 + (100 - random.nextInt(35)) * (random.nextInt(2) * -1));
		text.setTranslation(randomPoint);
		text.setDepth(6.0f);
		text.setVisible(true);
		text.init();
		getMap().getHitNumbers().add(text);
	}
	
	public boolean shieldCollision(MapContactListener contactListener, Enemy enemy)
	{
		return currentJob.shieldCollision(contactListener, enemy);
	}
	
	public boolean isInsideTower()
	{
		return isInsideTower;
	}

	public void setInsideTower(boolean isInsideTower)
	{
		this.isInsideTower = isInsideTower;
	}
	
	public boolean isEarthQuacking()
	{
		return isEarthQuaking;
	}
	
	public void setEarthQuaking(boolean isEarthQuaking)
	{
		this.isEarthQuaking = isEarthQuaking;
	}
	
	public boolean controlled()
	{
		return controlled;
	}

	public void setControlled(boolean controlled)
	{
		this.controlled = controlled;
	}
	
	public boolean inCombat()
	{
		return outOfCombat <= stats.getCombatTime();
	}
	
	public void resetOutOfCombat()
	{
		if (!inCombat())
		{
			if (combatText != null)
				combatText.destroy();
			
			combatText = new Text("In Combat", Font.Style.PLAIN, 16, 0xFFf04444);
			combatText.setTranslation(TEXT_POINT.subtract(42.0f, 42.0f));
			combatText.setDepth(7.0f);
			combatText.setVisible(true);
			combatText.init();
		}
		
		outOfCombat = 0.0f;
	}
	
	public void putOutOfCombat()
	{
		outOfCombat = stats.getCombatTime() + 1;
	}
	public void resetOutOfCombatTimer()
	{
		outOfCombat = 0.0f;
	}
	
	public boolean isExecuting()
	{
		return currentJob.isExecuting();
	}
	
	public boolean isDefending()
	{
		return currentJob.isDefending();
	}
	
	//TODO DA RIVEDERE
	public boolean canCarryItem()
	{
		return weapons.size() + items.size() < MAX_ITEMS;
	}
	
	public boolean canCarryPage()
	{
		return pages.size() < MAX_PAGES;
	}
	
	@Override
	public IPoint getFrameLocation()
	{
		return FRAME_LOCATION;
	}
	
	public static String getName()
	{
		return name;
	}

	public static void setName(String setName)
	{
		name = setName;
	}
	
	public static void setJob(JobType setJob)
	{
		startingJob = setJob;
	}
	
	public List<Item> getItems()
	{
		return items;
	}
	
	public List<Page> getPages()
	{
		return pages;
	}
	
	public void resetPages()
	{
		pages.clear();
		negativeDiscarded = 0;
	}
	
	public int getUsedPages()
	{
		return usedPages;
	}

	public void setUsedPages(int usedPages)
	{
		this.usedPages = usedPages;
	}

	public void addItem(Item item)
	{
		items.add(item);
	}

	public void addItems(List<Item> items)
	{
		this.items.addAll(items);	
	}
	
	public void addPage(Page page)
	{
		page.setText(new Text(page.toString(), Font.Style.PLAIN, 28, 0xFFFFFFFF));
		pages.add(page);
	}

	public void addPages(List<Page> pages)
	{
		for (Page page : pages)
		{
			page.setText(new Text(page.toString(), Font.Style.PLAIN, 28, 0xFFFFFFFF));
		}
		this.pages.addAll(pages);
	}
	
	public int getNegativeDiscarded()
	{
		if (negativeDiscarded < 0)
			return 0;
		return negativeDiscarded;
	}
	
	public void increaseNegativeDiscarded()
	{
		negativeDiscarded += 1;
		stats.increaseNegativeDiscarded();
	}
	
	public void decreaseNegativeDiscarded()
	{
		negativeDiscarded -= 1;
		stats.increaseNegativeGathered();
	}
	
	public List<Weapon> getWeapons()
	{
		return weapons;
	}
	
	public void addWeapon(Weapon weapon)
	{
		weapons.add(weapon);
	}

	public void addWeapons(List<Weapon> weapons)
	{
		this.weapons.addAll(weapons);
	}
	
	public float getGolds()
	{
		return golds;
	}
	
	public void addGolds(float golds)
	{
		this.golds += golds;
	}
	
	public List<Spell> getOffensiveSpells()
	{
		return offensiveSpells;
	}

	public List<Spell> getDefensiveSpells()
	{
		return defensiveSpells;
	}

	public Pet getPet()
	{
		return pet;
	}
	
	public void setPet(Pet pet)
	{
		this.pet = pet;
	}
	
	public boolean isAttacking()
	{
		return isAttacking;
	}

	public void setAttacking(boolean isAttacking)
	{
		this.isAttacking = isAttacking;
	}

	public boolean isSprinting()
	{
		return isSprinting;
	}
	
	public void setSprinting(boolean isSprinting)
	{
		this.isSprinting = isSprinting;
	}
	
	public boolean isPicking()
	{
		return isPicking;
	}

	public void setPicking(boolean isPicking)
	{
		this.isPicking = isPicking;
	}

	public PlayerStats getStats()
	{
		return stats;
	}

	public boolean isExhausted()
	{
		return isExhausted && !deadLayer.visible();
	}

	public void setExhausted(boolean isExhausted)
	{
		this.isExhausted = isExhausted;
	}
	
	@Override
	public void setDead(boolean isDead)
	{
		super.setDead(isDead);
		
		if (isDead)
		{
			if (getGameloop().getCorpse() != null)
			{
				getGameloop().getCorpse().clear();
				getGameloop().setCorpse(null);
			}

			if (getPages().size() > 0)
			{
				getGameloop().setCorpse(new Corpse(x(), y(), getMap()));
				getGameloop().getCorpse().addAllPages(getPages());
				getGameloop().getCorpse().setMapId(getMap().getMapId());
				if (getGameloop().getNewGameMenu().get("SCROLL").visible())
					getGameloop().getNewGameMenu().get("SCROLL").setVisible(false);
				resetPages();
			}
			
			setUsedPages(0);
			hasPassed = false;
			if (isEarthQuaking)
				setEarthQuaking(false);
		}
	}
	
	@Override
	public void dead()
	{
		super.dead();
		
		if (getWeapon() != null)
			getWeapon().setVisible(false);
	}
	
	public int getNewGame()
	{
		return newGame;
	}

	public void setNewGame(int newGame)
	{
		this.newGame = newGame;
	}

	public void MouseListener(ButtonEvent event)
	{		
		Point p = new Point(event.localX(), event.localY());
		
		if (!isDead() && isInsideTower && !getMap().isSpecialChestOpen() && !PopupWindow.getInstance().visible() && !getMap().popupShown() && event.localY() <= graphics().height() 
				- gameGUI.height() && (!getGameloop().isMenuOpen() || !getGameloop().intersectsMenu(new Point(event.localX(), event.localY()))))
		{
			if (event.button() ==  Mouse.BUTTON_LEFT)
			{
				if (currentJob.isDefending())
					currentJob.setDefending(false);
				
				if (getMap().getCurrentDrop() == null)
					currentJob.leftClick();
				
				else
					getMap().getCurrentDrop().select(p);
			}
			
			if (event.button() ==  Mouse.BUTTON_RIGHT && !hasSelectedItem())
			{
				if (currentJob instanceof Warrior)
					currentJob.rightClick(true);
				
				else
					currentJob.rightClick();
			}
		}
		
		if (currentJob.canTrain() && currentJob.canFirstSkillTrain() && event.localX() >= Job.FIRST_SKILL_POINT.x() &&
					event.localY() >= Job.FIRST_SKILL_POINT.y() && event.localX() <= Job.FIRST_SKILL_POINT.x() + Job.ICON.width() && 
						event.localX() <= Job.FIRST_SKILL_POINT.x() + Job.ICON.height())
			currentJob.train(SkillType.FIRST);
		
		if (currentJob.canTrain() && currentJob.canSecondSkillTrain() && event.localX() >= Job.SECOND_SKILL_POINT.x() && 
					event.localY() >= Job.SECOND_SKILL_POINT.y() && event.localX() <= Job.SECOND_SKILL_POINT.x() + Job.ICON.width() && 
						event.localX() <= Job.SECOND_SKILL_POINT.x() + Job.ICON.height())
			currentJob.train(SkillType.SECOND);
		
		if (currentJob.canTrain() && currentJob.canThirdSkillTrain() && event.localX() >= Job.THIRD_SKILL_POINT.x() &&
					event.localY() >= Job.THIRD_SKILL_POINT.y() && event.localX() <= Job.THIRD_SKILL_POINT.x() + Job.ICON.width() && 
						event.localX() <= Job.THIRD_SKILL_POINT.x() + Job.ICON.height())
			currentJob.train(SkillType.THIRD);
		
		if (currentJob.canTrain() && currentJob.canUltimateSkillTrain() && event.localX() >= Job.ULTIMATE_SKILL_POINT.x() &&
					event.localY() >= Job.ULTIMATE_SKILL_POINT.y() && event.localX() <= Job.ULTIMATE_SKILL_POINT.x() + Job.ICON.width() &&
						event.localX() <= Job.ULTIMATE_SKILL_POINT.x() + Job.ICON.height())
			currentJob.train(SkillType.ULTIMATE);
		
		if (getMap().getCurrentDrop() != null && getMap().getCurrentDrop().isDiscarded(p))
		{
			StorableDrop currentDrop = getMap().getCurrentDrop();
			
			if (currentDrop.getSelected() instanceof Page)
				currentDrop.getSelected().send(false, getMap().getGame().getAnnotationProtocol());
			
			currentDrop.removeSelected();
			
			if (!currentDrop.isEmpty())
				currentDrop.select(0);
		}
		
		if (deadLayer.visible() && DEAD_BUTTON.contains(p))
			deadLayer.setVisible(false);
	}
	
	public boolean hasClickedLevelup(IPoint p)
	{
		return levelUpButtonLayer.visible() && (STRENGTH_UP.contains(p) || DEX_UP.contains(p) || INT_UP.contains(p));
	}
	
	public void releaseMouse(ButtonEvent event)
	{		
		if (!isDead() && event.button() ==  Mouse.BUTTON_RIGHT)
			currentJob.rightClick(false);
	}
	
	public void keyboardListener(Set<Key> keyboard)
	{
		if (!isDead() && !currentJob.isWhirlWinding() && controlled)
		{
			if ((keyboard.contains(Key.SPACE) || keyboard.contains(Key.ENTER)) && getMap().getCurrentDrop() == null)
			{
				Direction face = getFacingDirection();
				setLastDirection(Direction.DEFAULT);
				getSprite().setLastDirection(face);
				keyboard.remove(Key.SPACE);
				keyboard.remove(Key.ENTER);
				setPicking(true);
				
				if (!getGameloop().getNewGameMenu().get("SCROLL").visible() && getMap().getCurrentDrop() != null)
					getGameloop().scrollMenu();
			}
			
			else if ((keyboard.contains(Key.SPACE) || keyboard.contains(Key.ENTER)) && !PopupWindow.getInstance().visible())
			{
				gatherSelectedItem(getMap().getCurrentDrop());
				keyboard.remove(Key.SPACE);
				keyboard.remove(Key.ENTER);
			}
				
			else
				setPicking(false);
			
			if ((getMap().getCurrentDrop() != null && getMap().getCurrentDrop().isSelected()) || getMap().isSpecialChestOpen())
			{
				if (isHidden())
					setCurrentAnimation(Animation.STEALTH_IDLE);

				else if (isEvading())
					setCurrentAnimation(Animation.EVADE_IDLE);
				
				else if (isBerserking())
					setCurrentAnimation(Animation.BERSERK_IDLE);
				
				else if (isWolf())
					setCurrentAnimation(Animation.WOLF_IDLE);
				
				else
				{
					isSprinting = false;
					setCurrentAnimation(Animation.IDLE);
				}
				
				if (!getMap().isSpecialChestOpen())
				{
					if ((keyboard.contains(Key.W) && !keyboard.contains(Key.S)) || (keyboard.contains(Key.UP) && !keyboard.contains(Key.DOWN)))
						getMap().decreaseDropIndex();
					
					else if ((keyboard.contains(Key.S) && !keyboard.contains(Key.W)) || (keyboard.contains(Key.DOWN) && !keyboard.contains(Key.UP)))
						getMap().increaseDropIndex();
		
					getMap().getCurrentDrop().select(getMap().getCurrentDrop().getIndex());
					keyboard.remove(Key.W);
					keyboard.remove(Key.UP);
					keyboard.remove(Key.S);
					keyboard.remove(Key.DOWN);
				}
			}
			
			else
			{
				if (isInsideTower)
				{
					if (keyboard.contains(Key.F) && getMap().getCurrentDrop() == null)
						currentJob.leftClick();
					
					if (keyboard.contains(Key.G) && !hasSelectedItem())
					{

						if (currentJob instanceof Warrior)
							currentJob.rightClick(true);
						
						else
							currentJob.rightClick();
					}
					
					if (keyboard.contains(Key.Z))
						currentJob.firstSkill();
					
					if (keyboard.contains(Key.X))
						currentJob.secondSkill();
					
					if (keyboard.contains(Key.C))
						currentJob.thirdSkill();
					
					if (keyboard.contains(Key.V))
						currentJob.ultimateSkill();
				}
				
				if ((keyboard.contains(Key.W) || keyboard.contains(Key.UP)) && (keyboard.contains(Key.D) || keyboard.contains(Key.RIGHT)))
					facingDirection = Direction.TOP_RIGHT;
				
				else if ((keyboard.contains(Key.W) || keyboard.contains(Key.UP)) && (keyboard.contains(Key.A) || keyboard.contains(Key.LEFT)))
					facingDirection = Direction.TOP_LEFT;
				
				else if ((keyboard.contains(Key.S) || keyboard.contains(Key.DOWN)) && (keyboard.contains(Key.A) || keyboard.contains(Key.LEFT)))
					facingDirection = Direction.BOTTOM_LEFT;
				
				else if ((keyboard.contains(Key.S) || keyboard.contains(Key.DOWN)) && (keyboard.contains(Key.D) || keyboard.contains(Key.RIGHT)))
					facingDirection = Direction.BOTTOM_RIGHT;
				
				else if ((keyboard.contains(Key.W) && !keyboard.contains(Key.S)) || (keyboard.contains(Key.UP) && !keyboard.contains(Key.DOWN)))
					facingDirection = Direction.UP;
				
				else if ((keyboard.contains(Key.A) && !keyboard.contains(Key.D)) || (keyboard.contains(Key.LEFT) && !keyboard.contains(Key.RIGHT)))
					facingDirection = Direction.LEFT;
				
				else if ((keyboard.contains(Key.S) && !keyboard.contains(Key.W)) || (keyboard.contains(Key.DOWN) && !keyboard.contains(Key.UP)))
					facingDirection = Direction.DOWN;
				
				else if ((keyboard.contains(Key.D) && !keyboard.contains(Key.A)) || (keyboard.contains(Key.RIGHT) && !keyboard.contains(Key.LEFT)))
					facingDirection = Direction.RIGHT;
				
				if (isMoving())
				{
					if (isBackwarding())
					{
						getSprite().setLastDirection(Direction.getOppositeDirection(facingDirection));
						currentJob.setLastDirection(Direction.getOppositeDirection(facingDirection));
					}
					
					else
					{
						getSprite().setLastDirection(facingDirection);
						currentJob.setLastDirection(facingDirection);
					}
					
					setPosition(facingDirection);
					
					if (endAttackAnimation())
					{
						currentJob.setAttacking(false);
						
						if (currentJob.isDefending())
							setCurrentAnimation(Animation.WALK);
						
						else if (isEvading())
							setCurrentAnimation(Animation.EVADE_RUN);
						
						else if (isHidden())
							setCurrentAnimation(Animation.STEALTH_WALK);
						
						else if (isBerserking())
							setCurrentAnimation(Animation.BERSERK_RUN);
						
						else if (isWolf())
							setCurrentAnimation(Animation.WOLF_RUN);
						
						else
							setCurrentAnimation(Animation.RUN);
					}
					
					if (keyboard.contains(Key.SHIFT) && getCurrentEndurance() > 0 && !currentJob.isDefending() && !isHidden() && !isBerserking())
					{
						if (getCurrentEndurance() > 1.0f)
						{
							setSpeedModifier(getDefaultSpeed() * 1.5f);
							setCurrentEndurance(getCurrentEndurance() - 0.5f);
							isSprinting = true;
						}
						
						else
						{
							setCurrentEndurance(0.0f);
							setSpeedModifier(getDefaultSpeed());
							isSprinting = false;
						}
					}
					
					else if (!currentJob.isDefending() && !isHidden() && !isBerserking())
					{
						setSpeedModifier(getDefaultSpeed());
						isSprinting = false;
					}
					
					else if (currentJob.isDefending())
						setSpeedModifier(getDefaultSpeed() / 2);
					
					else if (currentJob.isHidden())
					{
						setSpeedModifier(currentJob.getStealthSpeed());
					}
					
					else if (isBerserking())
						setSpeedModifier(getDefaultSpeed() * 1.5f);
				}
				
				else
				{
					setPosition(Direction.DEFAULT);
					
					if (endAttackAnimation())
					{
						if (hasWeapon())
							setWeaponVisibility(false);
						
						if (isHidden())
							setCurrentAnimation(Animation.STEALTH_IDLE);
						
						else if (isEvading())
							setCurrentAnimation(Animation.EVADE_IDLE);
						
						else if (isBerserking())
							setCurrentAnimation(Animation.BERSERK_IDLE);
						
						else if (isWolf())
							setCurrentAnimation(Animation.WOLF_IDLE);
						
						else
						{
							isSprinting = false;
							setCurrentAnimation(Animation.IDLE);
						}
					}
				}
			}
		}
		
		if (getMap().getCurrentDrop() != null && keyboard.contains(Key.R))
		{
			keyboard.remove(Key.R);

			StorableDrop currentDrop = getMap().getCurrentDrop();
			if (currentDrop.getSelected() instanceof Page)
			{
				if (!currentDrop.getSelected().isValidation())
					increaseNegativeDiscarded();
				currentDrop.getSelected().send(false, getMap().getGame().getAnnotationProtocol());
			}
			
			currentDrop.removeSelected();
			
			if (!currentDrop.isEmpty())
				currentDrop.select(0);
		}
	}
	
	public void onKeyUp(Event event)
	{
		if (event.key() == Key.G)
			currentJob.rightClick(false);
		
		if (isDead() && deadLayer.visible() && (event.key() == Key.ENTER || event.key() == Key.ESCAPE))
			deadLayer.setVisible(false);
	}
	
	public void gatherSelectedItem(StorableDrop currentDrop)
	{
		if (currentDrop.isSelected())
		{
			Item item = currentDrop.getSelected();
			
			if (item instanceof Gold)
			{
				currentDrop.removeSelected();
				addGolds(((Gold) item).getGolds());
				getGameloop().updateMenuVisibility("ITEM");
				getGameloop().updateMenuVisibility("SCROLL");
			}
			
			if (item instanceof Weapon)
			{
				currentDrop.removeSelected();
				addWeapon((Weapon)item);
			}
			
			if (item instanceof Page && canCarryPage())
			{
				currentDrop.removeSelected();
				addPage((Page)item);
				getGameloop().updateMenuVisibility("SCROLL");
				
				if (item.isValidation())
					statistic.setTruePositive(statistic.getTruePositive() + 1);
				
				else
				{
					statistic.setFalsePositive(statistic.getFalsePositive() + 1);
					decreaseNegativeDiscarded();
				}
				
				if (getGameloop().getNewGameMenu().get("SCROLL").visible())
					getGameloop().getNewGameMenu().get("SCROLL").setVisible(true);
			}
			
			else if (!canCarryPage())
				PopupWindow.getInstance().show("Your book is full. Please drop some pages or go to the special chest!");
			
			if (item instanceof Potion)
			{
				currentDrop.removeSelected();
				addItem(item);
				if (getGameloop().getNewGameMenu().get("ITEM").visible())
					getGameloop().getNewGameMenu().get("ITEM").setVisible(true);
			}
			
			
			if (getMap().getCurrentDrop() != null)
			{
				if (getMap().getCurrentDrop().getIndex() == 0)
					getMap().getCurrentDrop().select(0);
				else if (getMap().getCurrentDrop().getIndex() == getMap().getCurrentDrop().getItems().size())
					getMap().getCurrentDrop().select(getMap().getCurrentDrop().getIndex() - 1);
				else
					getMap().getCurrentDrop().select(getMap().getCurrentDrop().getIndex());
			}
		}
	}
	
	public void setNewMap(float x, float y, World world)
	{
		setWorld(world);
		initPhysicalBody(BodyType.DYNAMIC, 32, 32, Material.LIVING, 0x0002, 0xFFFF & ~0x0008);
		getBody().setTransform(new Vec2(x / GameMap.PTM_RATIO, y / GameMap.PTM_RATIO), facingDirection.getAngle());
		currentJob.setNewMap(x, y, world, getMap());
		
		if (currentJob.isPetUp())
			pet.setNewMap(x, y, world, getMap());
	}
	
	public boolean isJobUnlocked(JobType job)
	{
		if (job == defaultJob)
			return true;
		
		switch (job)
		{
			case BERSERKER: return Job.isUnlocked(job, stats.getKilledEnemies());
			case BLACK_MAGE: return Job.isUnlocked(job, stats.getTowerCompleted());
			case DRUID: return Job.isUnlocked(job, stats.getBestScore());
			case RANGER: return Job.isUnlocked(job, stats.getNegativeDiscarded());
			case THIEF: return Job.isUnlocked(job, stats.getTowerCompleted());
			case WARRIOR: return Job.isUnlocked(job, stats.getTowerCompleted());
			default: return false;
		}
	}
	
	public JobType getDefaultJob()
	{
		return defaultJob;
	}
	
	public void setDefaultJob(JobType defaultJob)
	{
		this.defaultJob = defaultJob;
	}
	
	public void updateJobInfo()
	{
		stats.updateJobInfo(currentJob.toString(), String.valueOf(currentJob.getJobLevel()), String.valueOf(currentJob.getJobExperience()),
				String.valueOf(currentJob.getFirstSkillLevel()), String.valueOf(currentJob.getSecondSkillLevel()), 
					String.valueOf(currentJob.getThirdSkillLevel()), String.valueOf(currentJob.getUltimateSkillLevel()), String.valueOf(currentJob.getFreePoints()));
	}
	
	public void loadJobInfo()
	{
		currentJob.setJobLevel(Float.parseFloat(stats.getJobLevel(currentJob.toString())));
		currentJob.setJobExperience(Float.parseFloat(stats.getJobExperience(currentJob.toString())));
		currentJob.getJobBar().updateBar(currentJob.getJobExperience());
		currentJob.setFirstSkillLevel(Float.parseFloat(stats.getFirstSkillLevel(currentJob.toString())));
		currentJob.setSecondSkillLevel(Float.parseFloat(stats.getSecondSkillLevel(currentJob.toString())));
		currentJob.setThirdSkillLevel(Float.parseFloat(stats.getThirdSkillLevel(currentJob.toString())));
		currentJob.setUltimateSkillLevel(Float.parseFloat(stats.getUltimateSkillLevel(currentJob.toString())));
		currentJob.setFreePoints(Float.parseFloat(stats.getFreePoints(currentJob.toString())));
	}
	
	// usato dal load()
	public void setCurrentJob(Job job)
	{
		this.currentJob = job;
		loadJobInfo();
		setSprite(new Sprite(currentJob.getCharacterImage(), getFrameDuration(), currentJob.getSize().width(), currentJob.getSize().height()));
		getCurrentJob().setLastDirection(facingDirection);
		getSprite().setLastDirection(facingDirection);
		
		if (hasWeapon())
			getWeapon().setLastDirection(facingDirection);
		
		gameGUI.setJob(currentJob);
	}
	
	public void setCurrentJob(JobType job)
	{
		updateJobInfo();
		save();
		
		if (isBerserking())
			currentJob.setBerserk(false);
		
		if (isWolf())
			currentJob.setWolf(false);
		
		if (hasWeapon())
			getWeapon().clear();
		
		currentJob.clear();
		
		if(job == JobType.WARRIOR)
			currentJob = new Warrior(x(), y(), this);
		
		if(job == JobType.BLACK_MAGE)
			currentJob = new BlackMage(this);
		
		if(job == JobType.THIEF)
			currentJob = new Thief(x(), y(), this);
		
		if(job == JobType.BERSERKER)
			currentJob = new Berserker(x(), y(), this);
		
		if(job == JobType.RANGER)
			currentJob = new Ranger(x(), y(), this);
		
		if(job == JobType.DRUID)
			currentJob = new Druid(x(), y(), this);
		
		loadJobInfo();
		setSprite(new Sprite(currentJob.getCharacterImage(), getFrameDuration(), currentJob.getSize().width(), currentJob.getSize().height()));
		getCurrentJob().setLastDirection(facingDirection);
		getSprite().setLastDirection(facingDirection);
		bars.clear();
		initBars();
		
		if (hasWeapon())
			getWeapon().setLastDirection(facingDirection);
		
		gameGUI.setJob(currentJob);
	}
	
	@Override
	public void setVisible(boolean visible)
	{
		super.setVisible(visible);
		
		if (isInsideTower)
			gameGUI.setTowerTitle(true, getMap().getGameloop().getTowerTitle());
		
		else
			gameGUI.setTowerTitle(false, "");

		gameGUI.setVisible(visible);
		currentJob.setVisible(visible);
		
		if (pet != null)
			pet.setVisible(visible);
		
		if (combatText != null)
			combatText.destroy();
		
		for (PlayerBar bar : bars)
			bar.setVisible(visible);

		if (freePoints > 0)
			levelUpButtonLayer.setVisible(visible);
	}
	
	@Override
	public void setLastDirection(Direction lastDirection)
	{
		super.setLastDirection(lastDirection);
		getSprite().setLastDirection(lastDirection);
		
		if (hasWeapon())
			getWeapon().setLastDirection(lastDirection);
	}
	
	public void setFacingDirection(Direction facingDirection)
	{
		this.facingDirection = facingDirection;
		getSprite().setLastDirection(facingDirection);
		
		if (hasWeapon())
			getWeapon().setLastDirection(facingDirection);
	}
	
	@Override
	public Direction getFacingDirection()
	{
		return facingDirection;
	}
	
	public AnnotationStatistic getStatistic()
	{
		return statistic;
	}

	public void setStatistic(AnnotationStatistic statistic)
	{
		this.statistic = statistic;
	}
	
	public boolean hasPassed()
	{
		return hasPassed;
	}
	
	public void setHasPassed(boolean hasPassed)
	{
		this.hasPassed = hasPassed;
	}

	public int getCurrentTower()
	{
		return currentTower;
	}

	public void setCurrentTower(int currentTower)
	{
		this.currentTower = currentTower;
	}

	public void regen(float regenModifier)
	{
		// se non sta attaccando
		if (getCurrentEndurance() < getTotalEndurance() && !isSprinting)
		{
			if (currentJob.isDefending() || currentJob.isHidden())
				setCurrentEndurance(getCurrentEndurance() + 0.04f * regenModifier);
			
			else
				setCurrentEndurance((float) (getCurrentEndurance() + 0.01f * regenModifier * getTotalEndurance()));
		}
		
		//passiva Berserker
		if (currentJob instanceof Berserker)
			setCurrentLife(getCurrentLife() + getTotalLife() * 0.003f * regenModifier);
		
		else
			setCurrentLife(getCurrentLife() + getTotalLife() * 0.001f * regenModifier);
			
		if (!hasManaShield() || !inCombat())
		{
			// passiva BlackMage
			if (currentJob instanceof BlackMage)
				setCurrentMana(getCurrentMana() + getTotalMana() * 0.003f * regenModifier);
			
			else if (!isHidden())
				setCurrentMana(getCurrentMana() + getTotalMana() * 0.002f * regenModifier);
		}
		
		else
			setCurrentMana(getCurrentMana() + getTotalMana() * 0.001f * regenModifier);
			
			
	}
	
	public boolean endAttackAnimation()
	{
		return (getCurrentAnimation() != Animation.ATTACK && getCurrentAnimation() != Animation.STEALTH_ATTACK && 
				getCurrentAnimation() != Animation.BERSERK_ATTACK && getCurrentAnimation() != Animation.EVADE_ATTACK  &&
					getCurrentAnimation() != Animation.WOLF_ATTACK || ((getCurrentAnimation() == Animation.ATTACK || 
						getCurrentAnimation() == Animation.STEALTH_ATTACK || getCurrentAnimation() == Animation.BERSERK_ATTACK || 
							getCurrentAnimation() == Animation.EVADE_ATTACK || getCurrentAnimation() == Animation.WOLF_ATTACK) && 
								getSprite().isOver(getCurrentAnimation(), true)));
	}
	
	public void setWeaponVisibility(boolean visible)
	{
		if (visible)
		{
			if (isHidden())
				getWeapon().setCurrentAnimation(Animation.STEALTH_WEAPON);
			
			else if (isBerserking())
				getWeapon().setCurrentAnimation(Animation.BERSERK_WEAPON);
			
			else if (isEvading())
				getWeapon().setCurrentAnimation(Animation.EVADE_WEAPON);
			
			else if (isExecuting())
				getWeapon().setCurrentAnimation(Animation.EXECUTE_WEAPON);
			
			else 
				getWeapon().setCurrentAnimation(Animation.WEAPON);
		}
		
		else
			getWeapon().setCurrentAnimation(Animation.NO_WEAPON);
	}
	
	public boolean isWeaponVisible()
	{
		return getWeapon().getCurrentAnimation() != Animation.NO_WEAPON;
	}
	
	public boolean hasSelectedItem()
	{
		return hasSelectedItem;
	}

	public void setHasSelectedItem(boolean hasSelectedItem)
	{
		this.hasSelectedItem = hasSelectedItem;
	}

	public void save()
	{
		getMap().getGame().getServerConnection().saveState(stats.saveState());
		saveMap();
	}
	
	public void saveMap()
	{
		getMap().getGame().getServerConnection().saveState("player", "bind", "start" + getCurrentTower());
		
		if (getCurrentTower() == 1)
			getMap().getGame().getServerConnection().saveState("player", "previous_map", getCurrentTower() + "towerend");
		else
			getMap().getGame().getServerConnection().saveState("player", "previous_map", getCurrentTower() - 1 + "towerend");
	}
	
	public void globalSave()
	{
		Map<String, String> value = new HashMap<String, String>();
		int jobScoreValue = 0;
		for (String job : stats.getJobInfo().keySet())
			if (stats.getJobInfo().get(job).split("_")[0].equals("15.0"))
				jobScoreValue += 25;
		
		int bestScoreValue = (int) Math.pow(stats.getBestScore() / 10000, 2.0);
		float annotationRatio = 0.0f;
		
		if (stats.getNegativeDiscarded() + stats.getNegativeGathered() != 0)
			annotationRatio = ((float) (stats.getNegativeDiscarded()) / (float) (stats.getNegativeDiscarded() + stats.getNegativeGathered()));
		
		int ladderScore =  (int) (jobScoreValue + bestScoreValue + (stats.getTowerCompleted() * annotationRatio));
		value.put(Player.getName(), ladderScore + "_" + stats.getBestScore() + "_" + annotationRatio);
		getMap().getGame().getServerConnection().saveGlobalState("player_info", value);
		Map<String, String> newValue = new HashMap<String, String>();
		newValue.put(Player.getName(), String.valueOf(ladderScore));
		getMap().getGame().getServerConnection().saveGlobalState("high-score", newValue);
	}
	
	public void load()
	{
		stats.loadState(getMap().getGame().getState());
		setCurrentLife(getTotalLife());
		setCurrentEndurance(getTotalEndurance());
		setCurrentMana(getTotalMana());
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
		
		if (getCurrentMana() < 0.0f) 
			setCurrentMana(0);
		
		if (getCurrentMana() > getTotalMana())
			setCurrentMana(getTotalMana());
		
		if (getCurrentRage() < 0.0f) 
			setCurrentRage(0);
		
		if (getCurrentRage() > getTotalRage())
			setCurrentRage(getTotalRage());
		
	}
	
	public void updateText()
	{
		combatText.getLayer().setAlpha(combatText.getLayer().alpha() - 0.01f);
		combatText.getLayer().setTranslation(combatText.getLayer().tx(), combatText.getLayer().ty() - 1.0f);
		if (combatText.getLayer().alpha() <= 0)
			combatText.destroy();
	}
	
	public void updateBars(PlayerBar bar)
	{
		if (bar.getType() == BarType.HEALTH)
			bar.update(getCurrentLife());
		
		else if (bar.getType() == BarType.ENDURANCE)
			bar.update(getCurrentEndurance());
		
		else if (bar.getType() == BarType.RAGE)
			bar.update(getCurrentRage());
		
		else if (bar.getType() == BarType.MANA)
			bar.update(getCurrentMana());
		
		else
			bar.update(getCurrentExperience());
	}
	
	public void clearText()
	{
		if (combatText != null)
			combatText.destroy();
		
		for (Text text : getMap().getHitNumbers())
			text.destroy();
	}
	
	@Override
	public void clear()
	{
		setVisible(false);
		
		if (combatText != null)
			combatText.destroy();
		
		if (inCombat())
			outOfCombat = stats.getCombatTime() + 1;
		
		currentJob.clear();
	}
	
	@Override
	public void update(int delta)
	{
		super.update(delta);
		currentJob.update(delta);
		outOfCombat += delta;
		
		if (levelUpEffect != null && levelUpEffect.visible())
			levelUpEffect.update(delta);
		
		if (combatText != null && combatText.visible())
			updateText();
		
		if (outOfCombat < stats.getCombatTime() && getMap().getEnemies().size() == 0)
			outOfCombat = stats.getCombatTime();
			
		if (outOfCombat == stats.getCombatTime())
		{
			if (combatText != null)
				combatText.destroy();
			
			combatText = new Text("Out of Combat", Font.Style.PLAIN, 16, 0xFF44f05c);
			combatText.setTranslation(TEXT_POINT.subtract(42.0f, 42.0f));
			combatText.setDepth(7.0f);
			combatText.setVisible(true);
			combatText.init();
		}

		for(PlayerBar bar : bars)
			updateBars(bar);
		
		if (!isDead())
		{
			if (isHidden())
				regen(0.1f);
			
			else
				regen(0.5f);
			
			adjustStats();
	
			if (!currentJob.isBloodThirsting() && !inCombat())
				setCurrentRage(getCurrentRage() - 0.03f);
		}
		
		if ((getCurrentAnimation() == Animation.DEATH || getCurrentAnimation() == Animation.WOLF_DEATH) && 
				getSprite().isOver(getCurrentAnimation()) && !deadLayer.visible())
		{
			super.setVisible(false);
			currentJob.setVisible(false);
			
			if (pet != null)
				pet.setVisible(false);
			
			for (PlayerBar bar : bars)
				bar.setVisible(false);

			saveMap();
			getMap().getGame().getKeyboard().clear();
			deadLayer.setVisible(true);
			isExhausted = true;
		}
		
		if (isExhausted && getCurrentLife() < getTotalLife())
		{
			setCurrentLife(getTotalLife());
			setCurrentEndurance(getTotalEndurance());
			setCurrentMana(getTotalMana());
		}
		
		if (currentJob.isPetUp())
			pet.update(delta);
		
		gameGUI.update();
		
		if (pet != null && pet.isDead() && pet.getSprite().isOver(Animation.DEATH))
		{
			currentJob.setPetUp(false);
			pet.clear();
			pet = null;
		}
	}
}
