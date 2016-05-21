package saga.progetto.tesi.job;

import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;
import playn.core.AssetWatcher;
import playn.core.Font;
import playn.core.Image;
import playn.core.ImageLayer;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import pythagoras.f.IPoint;
import pythagoras.f.Point;
import saga.progetto.tesi.core.TheKnowledgeTowers;
import saga.progetto.tesi.entity.Animation;
import saga.progetto.tesi.entity.Sprite;
import saga.progetto.tesi.entity.dynamicentity.Direction;
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.entity.dynamicentity.PlayerStats;
import saga.progetto.tesi.entity.dynamicentity.enemy.Enemy;
import saga.progetto.tesi.entity.dynamicentity.equip.PhysicsWeapon;
import saga.progetto.tesi.gui.PlayerBar;
import saga.progetto.tesi.gui.PlayerBar.BarType;
import saga.progetto.tesi.map.GameMap;
import saga.progetto.tesi.map.MapContactListener;
import saga.progetto.tesi.media.Text;
import saga.progetto.tesi.navigable.menu.JobMenu.JobType;

public abstract class Job
{
	private static final String LEVELUP_EFFECT_PATH = "images/gui/levelup_effect.png";
	public static final IPoint LEFT_POINT = new Point(206.0f, 515.0f);
	public static final IPoint RIGHT_POINT = new Point(271.0f, 515.0f);
	public static final IPoint FIRST_SKILL_POINT = new Point(336.0f, 515.0f);
	public static final IPoint SECOND_SKILL_POINT = new Point(401.0f, 515.0f);
	public static final IPoint THIRD_SKILL_POINT = new Point(466.0f, 515.0f);
	public static final IPoint ULTIMATE_SKILL_POINT = new Point(531.0f, 515.0f);
	protected static final float STEALTH_SPEED = 0.25f;
	public static final int MAXIMUM_JOB_LEVEL = 15;
	public static final IDimension ICON = new Dimension(64.0f, 64.0f);
	private static Image levelUpImage;
	
	private List<ImageLayer> spellLayers;
	private List<ImageLayer> levelUpEffects;
	private PlayerStats stats;
	private PlayerBar jobBar;
	private Player player;
	private float jobLevel;
	private float jobExperience;
	private float firstSkillLevel;
	private float secondSkillLevel;
	private float thirdSkillLevel;
	private float ultimateSkillLevel;
	private float freePoints;
	private float currentSwing;
	private float temporaryKnowledgePoints;
	private int earthquakeDistance;
	private boolean isExecuting;
	private boolean isHidden;
	private boolean isWhirlWinding;
	private boolean isEvading;
	private boolean hasManaShield;
	private boolean isBerserking;
	private boolean isWolf;
	private boolean isPetUp;
	private boolean isBloodThirsting;
	private boolean backward;
	private Map<String, Text> cdTexts;
	
	public Job(Player player)
	{
		stats = new PlayerStats();
		this.player = player;
		spellLayers = new LinkedList<ImageLayer>();
		cdTexts = new HashMap<String, Text>();
		initLevelUpEffects();
		jobBar = new PlayerBar(getJobExperience(), getTotalExperience(), BarType.JOB);
	}
	
	public enum SkillType
	{
		LEFT, RIGHT, FIRST, SECOND, THIRD, ULTIMATE;
	}
		
	public static void loadAssets(AssetWatcher watcher)
	{
		Warrior.loadAssets(watcher);
		BlackMage.loadAssets(watcher);
		Thief.loadAssets(watcher);
		Berserker.loadAssets(watcher);
		Ranger.loadAssets(watcher);
		Druid.loadAssets(watcher);
		levelUpImage = assets().getImage(LEVELUP_EFFECT_PATH);
		watcher.add(levelUpImage);
	}
	
	public static void loadCharacter(String selectedCharacter) 
	{
		Warrior.loadCharacter(selectedCharacter);
		BlackMage.loadCharacter(selectedCharacter);
		Thief.loadCharacter();
		Berserker.loadCharacter();
		Ranger.loadCharacter();
		Druid.loadCharacter();
	}	
	
	public Map<String, Text> getCdTexts()
	{
		return cdTexts;
	}
	
	public void initSpellIcons()
	{
		spellLayers.add(graphics().createImageLayer(getSpellImages().get(0).subImage(0, 0, ICON.width(), ICON.height())));
		spellLayers.get(0).setTranslation(LEFT_POINT.x(), LEFT_POINT.y());
		spellLayers.add(graphics().createImageLayer(getSpellImages().get(0).subImage(0, ICON.height(), ICON.width(), ICON.height())));
		spellLayers.get(1).setTranslation(LEFT_POINT.x(), LEFT_POINT.y());
		spellLayers.add(graphics().createImageLayer(getSpellImages().get(1).subImage(0, 0, ICON.width(), ICON.height())));
		spellLayers.get(2).setTranslation(RIGHT_POINT.x(), RIGHT_POINT.y());
		spellLayers.add(graphics().createImageLayer(getSpellImages().get(1).subImage(0, ICON.height(), ICON.width(), ICON.height())));
		spellLayers.get(3).setTranslation(RIGHT_POINT.x(), RIGHT_POINT.y());
		spellLayers.add(graphics().createImageLayer(getSpellImages().get(2).subImage(0, 0, ICON.width(), ICON.height())));
		spellLayers.get(4).setTranslation(FIRST_SKILL_POINT.x(), FIRST_SKILL_POINT.y());
		spellLayers.add(graphics().createImageLayer(getSpellImages().get(2).subImage(0, ICON.height(), ICON.width(), ICON.height())));
		spellLayers.get(5).setTranslation(FIRST_SKILL_POINT.x(), FIRST_SKILL_POINT.y());
		spellLayers.add(graphics().createImageLayer(getSpellImages().get(3).subImage(0, 0, ICON.width(), ICON.height())));
		spellLayers.get(6).setTranslation(SECOND_SKILL_POINT.x(), SECOND_SKILL_POINT.y());
		spellLayers.add(graphics().createImageLayer(getSpellImages().get(3).subImage(0, ICON.height(), ICON.width(), ICON.height())));
		spellLayers.get(7).setTranslation(SECOND_SKILL_POINT.x(), SECOND_SKILL_POINT.y());
		spellLayers.add(graphics().createImageLayer(getSpellImages().get(4).subImage(0, 0, ICON.width(), ICON.height())));
		spellLayers.get(8).setTranslation(THIRD_SKILL_POINT.x(), THIRD_SKILL_POINT.y());
		spellLayers.add(graphics().createImageLayer(getSpellImages().get(4).subImage(0, ICON.height(), ICON.width(), ICON.height())));
		spellLayers.get(9).setTranslation(THIRD_SKILL_POINT.x(), THIRD_SKILL_POINT.y());
		spellLayers.add(graphics().createImageLayer(getSpellImages().get(5).subImage(0, 0, ICON.width(), ICON.height())));
		spellLayers.get(10).setTranslation(ULTIMATE_SKILL_POINT.x(), ULTIMATE_SKILL_POINT.y());
		spellLayers.add(graphics().createImageLayer(getSpellImages().get(5).subImage(0, ICON.height(), ICON.width(), ICON.height())));
		spellLayers.get(11).setTranslation(ULTIMATE_SKILL_POINT.x(), ULTIMATE_SKILL_POINT.y());
		
		for (ImageLayer spellLayer : spellLayers)
		{
			spellLayer.setVisible(false);
			spellLayer.setDepth(9.0f);
			graphics().rootLayer().add(spellLayer);
		}
		
	}
	
	public void initLevelUpEffects()
	{
		levelUpEffects = new LinkedList<ImageLayer>();
		levelUpEffects.add(graphics().createImageLayer(levelUpImage));
		levelUpEffects.get(0).setTranslation(FIRST_SKILL_POINT.x(), FIRST_SKILL_POINT.y());
		levelUpEffects.add(graphics().createImageLayer(levelUpImage));
		levelUpEffects.get(1).setTranslation(SECOND_SKILL_POINT.x(), SECOND_SKILL_POINT.y());
		levelUpEffects.add(graphics().createImageLayer(levelUpImage));
		levelUpEffects.get(2).setTranslation(THIRD_SKILL_POINT.x(), THIRD_SKILL_POINT.y());
		levelUpEffects.add(graphics().createImageLayer(levelUpImage));
		levelUpEffects.get(3).setTranslation(ULTIMATE_SKILL_POINT.x(), ULTIMATE_SKILL_POINT.y());
		
		for (ImageLayer levelUpEffect : levelUpEffects)
		{
			levelUpEffect.setVisible(false);
			levelUpEffect.setDepth(10.0f);
			graphics().rootLayer().add(levelUpEffect);
		}
	}
	
	public static boolean isUnlocked(JobType job, Number currentStat)
	{
		switch (job)
		{
			case BERSERKER: return currentStat.longValue() >= Berserker.lockRequirement().longValue();
			case BLACK_MAGE: return currentStat.longValue() >= BlackMage.lockRequirement().longValue();
			case DRUID: return currentStat.longValue() >= Druid.lockRequirement().longValue();
			case RANGER: return currentStat.longValue() >= Ranger.lockRequirement().longValue();
			case THIEF: return currentStat.longValue() >= Thief.lockRequirement().longValue();
			case WARRIOR: return currentStat.longValue() >= Warrior.lockRequirement().longValue();
			default: return false;
		}
	}

	public abstract List<Image> getSpellImages();
	
	public abstract PhysicsWeapon getWeapon();
	
	public abstract float getDamageModifier();
	
	public PlayerBar getJobBar()
	{
		return jobBar;
	}
	
	public float getStealthSpeed()
	{
		return STEALTH_SPEED * getThirdSkillLevel();
	}
	
	public Body getWeaponBody()
	{
		return getWeapon().getBody();
	}

	public float getWeaponDamage()
	{
		return getWeapon().getWeaponDamage();
	}
	
	public boolean hasBlinked()
	{
		return false;
	}
	
	public boolean hasWeapon()
	{
		return getWeapon() != null;
	}
	
	public abstract void leftClick();
	
	public void rightClick()
	{
		if (spellLayers.get(2).visible())
		{
			spellLayers.get(2).setVisible(false);
			spellLayers.get(3).setVisible(true);
		}
	}
	
	public void rightClick(boolean isMouseDown)
	{
		
	}
	
	public void firstSkill()
	{
		if (spellLayers.get(4).visible())
		{
			spellLayers.get(4).setVisible(false);
			spellLayers.get(5).setVisible(true);
		}
	}
	
	public void secondSkill()
	{
		if (spellLayers.get(6).visible())
		{
			spellLayers.get(6).setVisible(false);
			spellLayers.get(7).setVisible(true);
		}
	}
	
	public void thirdSkill()
	{
		if (spellLayers.get(8).visible())
		{
			spellLayers.get(8).setVisible(false);
			spellLayers.get(9).setVisible(true);
		}
	}
	
	public void ultimateSkill()
	{
		if (spellLayers.get(10).visible())
		{
			spellLayers.get(10).setVisible(false);
			spellLayers.get(11).setVisible(true);
		}
	}
	
	public abstract float rightClickCD();
	
	public abstract float firstSkillCD();
	
	public abstract float secondSkillCD();
	
	public abstract float thirdSkillCD();

	public abstract float ultimateSkillCD();
	
	public abstract boolean canLeftClick();
	
	public abstract boolean canRightClick();
	
	public abstract boolean canFirstSkill();
	
	public abstract boolean canSecondSkill();
	
	public abstract boolean canThirdSkill();
	
	public abstract boolean canUltimateSkill();
	
	public abstract Image getCharacterImage();
	
	public abstract float getStrengthModifier();
	
	public abstract float getDexterityModifier();
	
	public abstract float getIntelligenceModifier();
	
	public abstract float getFirstSkillModifier();
	
	public abstract float getSecondSkillModifier();
	
	public abstract float getThirdSkillModifier();

	public abstract float getUltimateSkillModifier();
	
	public void meleeAttack()
	{
		if (getCurrentSwing() == 0 && (!isWolf && player.getCurrentEndurance() >= getWeapon().getCost()) ||
				(isWolf && player.getCurrentEndurance() >= getWeapon().getCost() / 2))
		{
			getWeapon().setVisible(true);
			getWeapon().setActive(true);
			player.getSprite().resetDelta();
			getWeapon().getSprite().resetDelta();
			
			if (isBerserking)
			{
				player.setCurrentAnimation(Animation.BERSERK_ATTACK);
				getWeapon().setCurrentAnimation(Animation.BERSERK_WEAPON);
			}
			
			else if (isWolf)
			{
				player.setCurrentAnimation(Animation.WOLF_ATTACK);
				getWeapon().setCurrentAnimation(Animation.WEAPON);
			}
			
			else if (isEvading())
			{
				player.setCurrentAnimation(Animation.EVADE_ATTACK);
				getWeapon().setCurrentAnimation(Animation.EVADE_WEAPON);
			}
			
			else if (isHidden())
			{
				player.setCurrentAnimation(Animation.STEALTH_ATTACK);
				getWeapon().setCurrentAnimation(Animation.STEALTH_WEAPON);
			}
			
			else if (isExecuting)
			{
				player.setCurrentAnimation(Animation.ATTACK);
				getWeapon().setCurrentAnimation(Animation.EXECUTE_WEAPON);
			}
			
			else 
			{
				player.setCurrentAnimation(Animation.ATTACK);
				getWeapon().setCurrentAnimation(Animation.WEAPON);
			}
			
			if (isWolf)
				player.setCurrentEndurance(player.getCurrentEndurance() - getWeapon().getCost() / 2);
			
			else
				player.setCurrentEndurance(player.getCurrentEndurance() - getWeapon().getCost());
		}
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	public boolean isHidden()
	{
		return isHidden;
	}
	
	public void setHidden(boolean isHidden)
	{
		this.isHidden = isHidden;
	}
	
	public boolean isBerserking()
	{
		return isBerserking;
	}
	
	public void setBerserk(boolean isBerserking)
	{
		this.isBerserking = isBerserking;
	}
	
	public boolean isWolf()
	{
		return isWolf;
	}
	
	public void setWolf(boolean isWolf)
	{
		isBerserking = false;
		this.isWolf = isWolf;
		
		getPlayer().setSprite(new Sprite(getCharacterImage(), getPlayer().getFrameDuration(), getSize().width(), getSize().height()));
		getPlayer().getSprite().setLastDirection(getPlayer().getFacingDirection());
	}

	public boolean isPetUp()
	{
		return isPetUp;
	}
	
	public void setPetUp(boolean isPetUp)
	{
		this.isPetUp = isPetUp;
	}
	
	public boolean isExecuting()
	{
		return isExecuting;
	}

	public void setExecute(boolean isExecuting)
	{
		this.isExecuting = isExecuting;
	}
	
	
	public boolean isBloodThirsting()
	{
		return isBloodThirsting;
	}

	public void setBloodThirsting(boolean isBloodThirsting)
	{
		this.isBloodThirsting = isBloodThirsting;
	}
	
	public boolean isBackwarding()
	{
		return backward;
	}
	
	public void setBackward(boolean backward)
	{
		this.backward = backward;
	}
	
	public boolean isEvading()
	{
		return isEvading;
	}
	
	public void setEvading(boolean isEvading)
	{
		this.isEvading = isEvading;
	}
	
	public boolean hasManaShield()
	{
		return hasManaShield;
	}
	
	public void setManaShield(boolean hasManaShield)
	{
		this.hasManaShield = hasManaShield;
	}
	
	public boolean isDefending()
	{
		return false;
	}
	
	public void setDefending(boolean isDefending)
	{
		
	}
	
	public boolean canDefend()
	{
		return false;
	}
	
	public boolean shieldCollision(MapContactListener contactListener, Enemy enemy)
	{
		return false;
	}
	
	public PlayerStats getStats()
	{
		return stats;
	}

	public void setStats(PlayerStats stats)
	{
		this.stats = stats;
	}

	public World getWorld()
	{
		return getPlayer().getWorld();
	}
	
	public GameMap getMap()
	{
		return getPlayer().getMap();
	}

	public float getCurrentSwing()
	{
		return currentSwing;
	}

	public void setCurrentSwing(float currentSwing)
	{
		this.currentSwing = currentSwing;
	}
	
	public boolean isAttacking()
	{
		return hasWeapon() && getWeapon().isActive();
	}
	
	public void setAttacking(boolean isAttacking)
	{
		if (hasWeapon())
		{
			getWeapon().setVisible(isAttacking);
			getWeapon().setActive(isAttacking);
		}
	}
	
	public void setLastDirection(Direction lastDirection)
	{
		if (hasWeapon())
			getWeapon().setLastDirection(lastDirection);
	}
	
	public void setNewMap(float x, float y, World world, GameMap map)
	{
		if (hasWeapon())
		{
			getWeapon().setMap(map);
			getWeapon().setWorld(world);
			getWeapon().initPhysicalBody();
		}
	}
	
	public float getJobLevel()
	{
		return jobLevel;
	}

	public void setJobLevel(float jobLevel)
	{
		this.jobLevel = jobLevel;
	}
	
	public float getJobExperience()
	{
		return jobExperience;
	}

	public void setJobExperience(float jobExperience)
	{
		this.jobExperience = jobExperience;
	}

	public float getFirstSkillLevel()
	{
		return firstSkillLevel;
	}
	
	public void setFirstSkillLevel(float firstSkillLevel)
	{
		this.firstSkillLevel = firstSkillLevel;
	}
	
	public float getSecondSkillLevel()
	{
		return secondSkillLevel;
	}
	
	public void setSecondSkillLevel(float secondSkillLevel)
	{
		this.secondSkillLevel = secondSkillLevel;
	}
	
	public float getThirdSkillLevel()
	{
		return thirdSkillLevel;
	}
	
	public void setThirdSkillLevel(float thirdSkillLevel)
	{
		this.thirdSkillLevel = thirdSkillLevel;
	}
	
	public float getUltimateSkillLevel()
	{
		return ultimateSkillLevel;
	}

	public void setUltimateSkillLevel(float ultimateSkillLevel)
	{
		this.ultimateSkillLevel = ultimateSkillLevel;
	}
	
	public float getFreePoints()
	{
		return freePoints;
	}
	
	public void setFreePoints(float freePoints)
	{
		this.freePoints = freePoints;
	}
	
	public float getTotalExperience()
	{
		return stats.getJobExpRequired();
	}
	
	public boolean isRewardFinished()
	{
		return temporaryKnowledgePoints == 0.0f;
	}
	
	public void gainJobExp(float knowledgePoints)
	{
		temporaryKnowledgePoints = knowledgePoints;
	}

	public int getEarthquakeDistance()
	{
		return earthquakeDistance;
	}

	public void setEarthquakeDistance(int earthquakeDistance)
	{
		this.earthquakeDistance = earthquakeDistance;
	}
	
	public void gainLevel()
	{
		setJobLevel(getJobLevel() + 1);
		jobBar.setTotal(getTotalExperience());
		setFreePoints(getFreePoints() + 1);
		setEffectsVisibility();
	}
	
	public void train(SkillType skill)
	{
		switch (skill)
		{
			case FIRST:  	setFirstSkillLevel(getFirstSkillLevel() + 1);
							setFreePoints(getFreePoints() - 1);
							break;
							
			case SECOND: 	setSecondSkillLevel(getSecondSkillLevel() + 1);
							setFreePoints(getFreePoints() - 1);
							break;
							
			case THIRD:		setThirdSkillLevel(getThirdSkillLevel() + 1);
							setFreePoints(getFreePoints() - 1);
							break;
								
			case ULTIMATE:  setUltimateSkillLevel(getUltimateSkillLevel() + 1);
							setFreePoints(getFreePoints() - 1);
							break;
							
			default:		break;
		}
		
		player.getGUI().destroyDescription();
		player.getGUI().initTooltipTexts();
		
		if (getFreePoints() == 0)
			for (ImageLayer layer : levelUpEffects)
				layer.setVisible(false);
		
		else
			setEffectsVisibility();
		
		getPlayer().updateJobInfo();
		getPlayer().save();
	}
	
	public boolean canTrain()
	{
		return getFreePoints() > 0;
	}
	
	public boolean canFirstSkillTrain()
	{
		return getFirstSkillLevel() < 4 && getFirstSkillLevel() <= getSecondSkillLevel() + getThirdSkillLevel();
	}
	
	public boolean canSecondSkillTrain()
	{
		return getSecondSkillLevel() < 4 && getSecondSkillLevel() <= getFirstSkillLevel() + getThirdSkillLevel();
	}
	
	public boolean canThirdSkillTrain()
	{
		return getThirdSkillLevel() < 4 && getThirdSkillLevel() <= getFirstSkillLevel() + getSecondSkillLevel();
	}
	
	public boolean canUltimateSkillTrain()
	{
		return getUltimateSkillLevel() < 3 && getJobLevel() >= 5 && getUltimateSkillLevel() == 0 || getJobLevel() >= 10 && getUltimateSkillLevel() == 1 ||
				getJobLevel() == 15 && getUltimateSkillLevel() == 2;
	}
	
	public abstract Map<Integer, String> leftClickDescription();
	
	public abstract Map<Integer, String> rightClickDescription();
	
	public abstract Map<Integer, String> firstSkillDescription();
	
	public abstract Map<Integer, String> secondSkillDescription();
	
	public abstract Map<Integer, String> thirdSkillDescription();
	
	public abstract Map<Integer, String> ultimateSkillDescription();
	
	public static String fixDecimal(float number)
	{
		String value = String.valueOf(number + 1);
		String[] tokenized = value.split("\\.");
		return tokenized[0];
	}
	
	public float getMagicResist()
	{
		return getIntelligenceModifier() / 20;
	}

	public boolean isWhirlWinding()
	{
		return isWhirlWinding;
	}

	public void setWhirlWinding(boolean isWhirlWinding)
	{
		this.isWhirlWinding = isWhirlWinding;
	}
	
	public void addCDText(String skill, float time, IPoint position)
	{
		Text text = null;
		
		if (time < 60000.0f)
			text = new Text(String.valueOf(time / 1000.0f + 1).replaceAll("\\..*$", ""),
				Font.Style.PLAIN, 23, 0xFFFFFFFF, 0xFF000000);
		
		else
			text = new Text(String.valueOf(time / 60000.0f).replaceAll("\\..*$", "") + "m",
					Font.Style.PLAIN, 23, 0xFFFFFFFF, 0xFF000000);
		
		text.setTranslation(position.add(32.0f - text.width() / 2, 32.0f - text.height() / 2));
		text.setDepth(11.0f);
		text.setVisible(true);
		text.init();
		cdTexts.put(skill, text);
	}
	
	public void updateSpellLayers()
	{
		if (!spellLayers.get(0).visible() && canLeftClick())
			swapVisibility(spellLayers.get(1), spellLayers.get(0));
		
		else if (!canLeftClick())
			swapVisibility(spellLayers.get(0), spellLayers.get(1));
		
		if (!spellLayers.get(2).visible() && canRightClick())
			swapVisibility(spellLayers.get(3), spellLayers.get(2));
		
		else if (!canRightClick())
		{
			swapVisibility(spellLayers.get(2), spellLayers.get(3));
			
			if (rightClickCD() >= 150.0f && (rightClickCD() % 1000 >= 0 || rightClickCD() % 1000 < TheKnowledgeTowers.UPDATE_RATE))
			{
				if (cdTexts.containsKey("rightClick"))
					cdTexts.get("rightClick").destroy();
				
				addCDText("rightClick", rightClickCD(), RIGHT_POINT);
			}
			
			else if (cdTexts.containsKey("rightClick"))
				cdTexts.get("rightClick").destroy();
		}
		
		if (!spellLayers.get(4).visible() && canFirstSkill())
			swapVisibility(spellLayers.get(5), spellLayers.get(4));
		
		else if (!canFirstSkill())
		{
			swapVisibility(spellLayers.get(4), spellLayers.get(5));
			
			if (firstSkillCD() >= 150.0f && (firstSkillCD() % 1000 >= 0 || firstSkillCD() % 1000 < TheKnowledgeTowers.UPDATE_RATE * 2))
			{
				if (cdTexts.containsKey("firstSkill"))
					cdTexts.get("firstSkill").destroy();
					
				addCDText("firstSkill", firstSkillCD(), FIRST_SKILL_POINT);
			}
			
			else if (cdTexts.containsKey("firstSkill"))
				cdTexts.get("firstSkill").destroy();
		}
		
		if (!spellLayers.get(6).visible() && canSecondSkill())
			swapVisibility(spellLayers.get(7), spellLayers.get(6));
		
		else if (!canSecondSkill())
		{
			swapVisibility(spellLayers.get(6), spellLayers.get(7));
			
			if (secondSkillCD() >= 150.0f && (secondSkillCD() % 1000 >= 0 || secondSkillCD() % 1000 < TheKnowledgeTowers.UPDATE_RATE * 2))
			{
				if (cdTexts.containsKey("secondSkill"))
					cdTexts.get("secondSkill").destroy();
				
				addCDText("secondSkill", secondSkillCD(), SECOND_SKILL_POINT);
			}
			
			else if (cdTexts.containsKey("secondSkill"))
				cdTexts.get("secondSkill").destroy();
		}

		if (!spellLayers.get(8).visible() && canThirdSkill())
			swapVisibility(spellLayers.get(9), spellLayers.get(8));
		
		else if (!canThirdSkill())
		{
			swapVisibility(spellLayers.get(8), spellLayers.get(9));
			
			if (thirdSkillCD() >= 150.0f && (thirdSkillCD() % 1000 >= 0 || thirdSkillCD() % 1000 < TheKnowledgeTowers.UPDATE_RATE * 2))
			{
				if (cdTexts.containsKey("thirdSkill"))
					cdTexts.get("thirdSkill").destroy();
				
				addCDText("thirdSkill", thirdSkillCD(), THIRD_SKILL_POINT);
			}
			
			else if (cdTexts.containsKey("thirdSkill"))
				cdTexts.get("thirdSkill").destroy();
		}
		
		if (!spellLayers.get(10).visible() && canUltimateSkill())
			swapVisibility(spellLayers.get(11), spellLayers.get(10));
		
		else if (!canUltimateSkill())
		{
			swapVisibility(spellLayers.get(10), spellLayers.get(11));
			
			if (ultimateSkillCD() >= 150.0f && (ultimateSkillCD() % 1000 >= 0 || ultimateSkillCD() % 1000 < TheKnowledgeTowers.UPDATE_RATE * 2))
			{
				if (cdTexts.containsKey("ultimateSkill"))
					cdTexts.get("ultimateSkill").destroy();
				
				addCDText("ultimateSkill", ultimateSkillCD(), ULTIMATE_SKILL_POINT);
			}
			
			else if (cdTexts.containsKey("ultimateSkill"))
				cdTexts.get("ultimateSkill").destroy();
		}
	}
	
	public void setVisible(boolean visible)
	{
		for (Map.Entry<String, Text> text : cdTexts.entrySet())
			text.getValue().setVisible(visible);
		
		if (visible)
			updateSpellLayers();
		
		else
			for (ImageLayer spellLayer : spellLayers)
				spellLayer.setVisible(false);
		
		if (visible)
			setEffectsVisibility();
		
		else
			for (ImageLayer levelUpEffect : levelUpEffects)
				if (levelUpEffect.visible())
					levelUpEffect.setVisible(false);
			
		
		jobBar.setVisible(visible);
		
		if (getWeapon() != null)
			getWeapon().setVisible(false);
	}
	
	public void setEffectsVisibility()
	{
		levelUpEffects.get(0).setVisible(canTrain() && canFirstSkillTrain());
		levelUpEffects.get(1).setVisible(canTrain() && canSecondSkillTrain());
		levelUpEffects.get(2).setVisible(canTrain() && canThirdSkillTrain());
		levelUpEffects.get(3).setVisible(canTrain() && canUltimateSkillTrain());
	}
	
	public void swapVisibility(ImageLayer currentVisible, ImageLayer currentHidden)
	{
		currentVisible.setVisible(false);
		currentHidden.setVisible(true);
	}
	
	public abstract float getDamageReduced();
	
	public IDimension getSize()
	{
		player.setSize(stats.getSize());
		return stats.getSize();
	}
	
	@Override
	public abstract String toString();
	
	public void clear()
	{
		for (ImageLayer spellLayer : spellLayers)
			spellLayer.setVisible(false);
		
		for (ImageLayer levelUpEffect : levelUpEffects)
			if (levelUpEffect.visible())
				levelUpEffect.setVisible(false);
		
		for (Map.Entry<String, Text> text : cdTexts.entrySet())
		{
			text.getValue().destroy();
			cdTexts.remove(text);
		}
		
		jobBar.setVisible(false);
	}
	
	public void update(int delta)
	{
		if (hasWeapon())
		{
			getWeapon().bodyTransform(player, player.getFacingDirection());
			getWeapon().update(delta);
			
			if (currentSwing >= getWeapon().getSwingTime())
				getWeapon().setActive(false);

			if (currentSwing >= getWeapon().getCooldown())
				currentSwing = 0.0f;
			
			if (isAttacking() || currentSwing >= getWeapon().getSwingTime())
				currentSwing += delta;
		}
		
		if (getJobLevel() == MAXIMUM_JOB_LEVEL)
		{
			temporaryKnowledgePoints = 0.0f;
			jobBar.setCurrent(temporaryKnowledgePoints);
		}
			
		if (temporaryKnowledgePoints != 0.0f && getJobLevel() < MAXIMUM_JOB_LEVEL)
		{
			if (temporaryKnowledgePoints > -1.0f && temporaryKnowledgePoints < 1.0f)
				temporaryKnowledgePoints = 0.0f;
			
			if (temporaryKnowledgePoints < 0.0f)
			{
				if (getJobExperience() < 1.0f)
				{
					temporaryKnowledgePoints = 0.0f;
					setJobExperience(0.0f);
				}
				
				jobExperience -= 1.0f;
				temporaryKnowledgePoints += 1.0f;
			}
			
			if (temporaryKnowledgePoints > 0.0f)
			{
				jobExperience += 1.0f;
				temporaryKnowledgePoints -= 1.0f;
				
				if (getJobExperience() > getTotalExperience() - 1.0f)
				{
					gainLevel();
					setJobExperience(0.0f);
				}
			}
			
			jobBar.setCurrent(getJobExperience());
			jobBar.updateBar(getJobExperience(), true);
		}
		
		if (jobBar.getCurrent() != getJobExperience())
			jobBar.setCurrent(getJobExperience());
		
		updateSpellLayers();
	}
}
