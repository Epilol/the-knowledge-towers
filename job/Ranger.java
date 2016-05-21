package saga.progetto.tesi.job;

import static playn.core.PlayN.assets;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import playn.core.AssetWatcher;
import playn.core.Image;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import saga.progetto.tesi.core.TheKnowledgeTowers;
import saga.progetto.tesi.entity.Animation;
import saga.progetto.tesi.entity.dynamicentity.Direction;
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.entity.dynamicentity.equip.Bow;
import saga.progetto.tesi.entity.dynamicentity.equip.PhysicsWeapon;
import saga.progetto.tesi.entity.dynamicentity.spell.AimShot;
import saga.progetto.tesi.entity.dynamicentity.spell.Arrow;
import saga.progetto.tesi.entity.dynamicentity.spell.SilenceArrow;

public class Ranger extends Job
{
	private static final String PLAYER_PATH = "images/characters/player/ranger/ranger.png";
	private static final String LEFT_CLICK_PATH = "images/spells/arrow_icon.png";
	private static final String RIGHT_CLICK_PATH = "images/spells/leap_icon.png";
	private static final String FIRST_SPELL_PATH = "images/spells/aimshot_icon.png";
	private static final String SECOND_SPELL_PATH = "images/spells/smoke_icon.png";
	private static final String THIRD_SPELL_PATH = "images/spells/multishot_icon.png";
	private static final String ULTIMATE_SPELL_PATH = "images/spells/preparation_icon.png";
	private static final IDimension SIZE = new Dimension(32.0f, 35.0f);
	private static final float GLOBAL_COOLDOWN = 1000.0f;
	private static final float ARROW_DURATION = 8000.0f;
	private static final float BACKWARD_COOLDOWN = 30000.0f;
	private static final float BACKWARD_COST = 0.0f;
	private static final float BACKWARD_DURATION = 4000.0f;
	private static final float RAPIDFIRE_COOLDOWN = 12000.0f;
	private static final float RAPIDFIRE_COST = 50.0f;
	private static final float RAPIDFIRE_TICK_TIME = 225.0f;
	private static final float NUMBER_OF_ARROWS = 3.0f;
	private static final float PREPARATION_COOLDOWN = 120000.0f;
	private static final float PREPARATION_MODIFIER = 15000.0f;
	private static final float PREPARATION_COST = 60.0f;
	private static final float STR_MODIFIER =  1.0f;
	private static final float DEX_MODIFIER =  2.0f;
	private static final float INT_MODIFIER =  1.0f;
	private static final float DAMAGE_REDUCED = 0.1f;
	private static final float MULTISHOT_MODIFIER = 1;
	private static final int PAGES_REQUIRED = 250;
	private static Image characterImage;
	private static Image leftClickImage;
	private static Image rightClickImage;
	private static Image firstSpellImage;
	private static Image secondSpellImage;
	private static Image thirdSpellImage;
	private static Image ultimateSpellImage;
	
	private PhysicsWeapon bow;
	private Arrow arrow;
	private AimShot aimshot;
	private SilenceArrow silenceArrow;
	private List<Arrow> rapidfire;
	private float currentGlobalCD = GLOBAL_COOLDOWN;
	private float currentArrowTime = Arrow.COOLDOWN;
	private float currentArrowDuration = ARROW_DURATION;
	private float currentBackwardTime = BACKWARD_COOLDOWN;
	private float currentAimshotTime = AimShot.COOLDOWN;
	private float currentAimshotDuration = ARROW_DURATION;
	private float currentSilenceArrowTime = SilenceArrow.COOLDOWN;
	private float currentSilenceDuration = ARROW_DURATION;
	private float currentRapidFireTime = RAPIDFIRE_COOLDOWN;
	private float currentPreparationTime = PREPARATION_COOLDOWN;
	private boolean isRapidFiring;
	
	public Ranger(float x, float y, Player player)
	{
		super(player);
		bow = new Bow(x, y - 16.0f, getMap());
		bow.setActive(false);
		initSpellIcons();
		rapidfire = new LinkedList<Arrow>();
	}
	
	public static void loadAssets(AssetWatcher watcher)
	{
		leftClickImage = assets().getImage(LEFT_CLICK_PATH);
		rightClickImage = assets().getImage(RIGHT_CLICK_PATH);
		firstSpellImage = assets().getImage(FIRST_SPELL_PATH);
		secondSpellImage = assets().getImage(SECOND_SPELL_PATH);
		thirdSpellImage = assets().getImage(THIRD_SPELL_PATH);
		ultimateSpellImage = assets().getImage(ULTIMATE_SPELL_PATH);
		watcher.add(leftClickImage);
		watcher.add(rightClickImage);
		watcher.add(firstSpellImage);
		watcher.add(secondSpellImage);
		watcher.add(thirdSpellImage);
		watcher.add(ultimateSpellImage);
	}
	
	// aggiungere requirement a metodo sopraclasse
	public static Number lockRequirement()
	{
		return PAGES_REQUIRED;
	}

	public static void loadCharacter() 
	{
		characterImage = assets().getImage(PLAYER_PATH);
	}

	@Override
	public List<Image> getSpellImages()
	{
		List<Image> spellImages = new ArrayList<Image>();
		spellImages.add(leftClickImage);
		spellImages.add(rightClickImage);
		spellImages.add(firstSpellImage);
		spellImages.add(secondSpellImage);
		spellImages.add(thirdSpellImage);
		spellImages.add(ultimateSpellImage);
		return spellImages;
	}
	
	@Override
	public float getStrengthModifier()
	{
		return STR_MODIFIER;
	}

	@Override
	public float getDexterityModifier()
	{
		return DEX_MODIFIER;
	}

	@Override
	public float getIntelligenceModifier()
	{
		return INT_MODIFIER;
	}
	
	@Override
	public Image getCharacterImage()
	{
		return characterImage;
	}

	@Override
	public void leftClick()
	{
		if (getPlayer().getCurrentEndurance() >= Arrow.ENDURANCE_COST && currentArrowTime > Arrow.COOLDOWN &&
				currentGlobalCD  > GLOBAL_COOLDOWN && !isRapidFiring)
		{
			getWeapon().setVisible(true);
			getPlayer().getSprite().resetDelta();
			getWeapon().getSprite().resetDelta();
			getPlayer().setCurrentAnimation(Animation.ATTACK);
			getWeapon().setCurrentAnimation(Animation.WEAPON);
			
			if (arrow != null)
				arrow.clear();
			
			arrow = new Arrow(getPlayer().x(), getPlayer().y(), getPlayer(), getDamageModifier());
			getPlayer().getOffensiveSpells().add(arrow);
			Direction facingDirection = getPlayer().getFacingDirection();
			
			if(isBackwarding() && getPlayer().isMoving())
				facingDirection = Direction.getOppositeDirection(getPlayer().getLastDirection());
			
			arrow.setLastDirection(facingDirection);
			arrow.bodyTransform(getPlayer(), facingDirection);
			arrow.getSprite().setLastDirection(facingDirection);
			getPlayer().setCurrentEndurance(getPlayer().getCurrentEndurance() - Arrow.ENDURANCE_COST);
			currentGlobalCD = 0.0f;
			currentArrowTime = 0.0f;
			currentArrowDuration = 0.0f;
		}
	}

	@Override
	public void rightClick()
	{
		super.rightClick();
		
		if (getPlayer().getCurrentEndurance() >= BACKWARD_COST && currentBackwardTime > BACKWARD_COOLDOWN &&
				currentGlobalCD  > GLOBAL_COOLDOWN && !isRapidFiring)
		{
			setBackward(true);
			Direction facingDirection = Direction.getOppositeDirection(getPlayer().getFacingDirection());
			getPlayer().setFacingDirection(facingDirection);
			currentBackwardTime = 0.0f;
			currentGlobalCD = 0.0f;
		}
	}

	@Override
	public void firstSkill()
	{
		super.firstSkill();
		
		if (getPlayer().getCurrentEndurance() >= AimShot.ENDURANCE_COST && currentAimshotTime > AimShot.COOLDOWN &&
				currentGlobalCD  > GLOBAL_COOLDOWN && getFirstSkillLevel() > 0 && !isRapidFiring)
		{
			getWeapon().setVisible(true);
			getPlayer().getSprite().resetDelta();
			getWeapon().getSprite().resetDelta();
			getPlayer().setCurrentAnimation(Animation.ATTACK);
			getWeapon().setCurrentAnimation(Animation.WEAPON);
			aimshot = new AimShot(getPlayer().x(), getPlayer().y(), getPlayer(), getFirstSkillModifier() + getDamageModifier());
			getPlayer().getOffensiveSpells().add(aimshot);
			Direction facingDirection = getPlayer().getFacingDirection();
			
			if(isBackwarding() && getPlayer().isMoving())
				facingDirection = Direction.getOppositeDirection(getPlayer().getLastDirection());
			
			aimshot.setLastDirection(facingDirection);
			aimshot.bodyTransform(getPlayer(), facingDirection);
			aimshot.getSprite().setLastDirection(facingDirection);
			getPlayer().setCurrentEndurance(getPlayer().getCurrentEndurance() - AimShot.ENDURANCE_COST);
			currentGlobalCD = 0.0f;
			currentAimshotTime = 0.0f;
			currentAimshotDuration = 0.0f;
		}
	}

	@Override
	public void secondSkill()
	{
		super.secondSkill();
		
		if (getPlayer().getCurrentEndurance() >= SilenceArrow.ENDURANCE_COST && currentSilenceArrowTime > SilenceArrow.COOLDOWN &&
				currentGlobalCD  > GLOBAL_COOLDOWN && getSecondSkillLevel() > 0 && !isRapidFiring)
		{
			getWeapon().setVisible(true);
			getPlayer().getSprite().resetDelta();
			getWeapon().getSprite().resetDelta();
			getPlayer().setCurrentAnimation(Animation.ATTACK);
			getWeapon().setCurrentAnimation(Animation.WEAPON);
			silenceArrow = new SilenceArrow(getPlayer().x(), getPlayer().y(), getPlayer(), getSecondSkillModifier(), getDamageModifier() + getSecondSkillLevel());
			getPlayer().getOffensiveSpells().add(silenceArrow);
			Direction facingDirection = getPlayer().getFacingDirection();
			
			if(isBackwarding() && getPlayer().isMoving())
				facingDirection = Direction.getOppositeDirection(getPlayer().getLastDirection());
			
			silenceArrow.setLastDirection(facingDirection);
			silenceArrow.bodyTransform(getPlayer(), facingDirection);
			silenceArrow.getSprite().setLastDirection(facingDirection);
			getPlayer().setCurrentEndurance(getPlayer().getCurrentEndurance() - SilenceArrow.ENDURANCE_COST);
			currentGlobalCD = 0.0f;
			currentSilenceArrowTime = 0.0f;
			currentSilenceDuration = 0.0f;
		}
	}
	
	@Override
	public void thirdSkill()
	{
		super.thirdSkill();
		
		if (getPlayer().getCurrentEndurance() >= RAPIDFIRE_COST && currentRapidFireTime > RAPIDFIRE_COOLDOWN &&
				currentGlobalCD  > GLOBAL_COOLDOWN && getThirdSkillLevel() > 0 && !isRapidFiring)
		{

			getWeapon().setVisible(true);
			getPlayer().getSprite().resetDelta();
			getWeapon().getSprite().resetDelta();
			getPlayer().setCurrentAnimation(Animation.ATTACK);
			getWeapon().setCurrentAnimation(Animation.WEAPON);
			isRapidFiring = true;
			getPlayer().setCurrentEndurance(getPlayer().getCurrentEndurance() - RAPIDFIRE_COST);
			currentGlobalCD = 0.0f;
			currentRapidFireTime = 0.0f;
		}
	}
	
	@Override
	public void ultimateSkill()
	{
		super.ultimateSkill();
		
		if (currentPreparationTime >= PREPARATION_COOLDOWN - getUltimateSkillModifier() && getUltimateSkillLevel() > 0 && 
				getPlayer().getCurrentEndurance() >= PREPARATION_COST && !isRapidFiring)
		{
			currentPreparationTime = 0.0f;
			currentGlobalCD = 0.0f;
			currentBackwardTime = BACKWARD_COOLDOWN;
			currentAimshotTime = AimShot.COOLDOWN;
			currentSilenceArrowTime = SilenceArrow.COOLDOWN;
			currentRapidFireTime = RAPIDFIRE_COOLDOWN;
			
			if (getCdTexts().get("rightClick") != null)
				getCdTexts().remove("rightClick").destroy();
			
			if (getCdTexts().get("firstSkill") != null)
				getCdTexts().remove("firstSkill").destroy();
			
			if (getCdTexts().get("secondSkill") != null)
				getCdTexts().remove("secondSkill").destroy();
			
			if (getCdTexts().get("thirdSkill") != null)
				getCdTexts().remove("thirdSkill").destroy();
			
			getPlayer().setCurrentEndurance(getPlayer().getCurrentEndurance() - PREPARATION_COST);
		}
	}

	@Override
	public float rightClickCD()
	{
		if (BACKWARD_COOLDOWN - 100.0f > currentBackwardTime)
			return BACKWARD_COOLDOWN - currentBackwardTime;
		return 0.0f;
	}
	
	@Override
	public float firstSkillCD()
	{
		if (AimShot.COOLDOWN - 100.0f > currentAimshotTime)
			return AimShot.COOLDOWN - currentAimshotTime;
		return 0.0f;
	}
	
	@Override
	public float secondSkillCD()
	{
		if (SilenceArrow.COOLDOWN - 100.0f > currentSilenceArrowTime)
			return SilenceArrow.COOLDOWN - currentSilenceArrowTime;
		return 0.0f;
	}
	
	@Override
	public float thirdSkillCD()
	{
		if (RAPIDFIRE_COOLDOWN - 100.0f > currentRapidFireTime)
			return RAPIDFIRE_COOLDOWN - currentRapidFireTime;
		return 0.0f;
	}
	
	@Override
	public float ultimateSkillCD()
	{
		if (PREPARATION_COOLDOWN - getUltimateSkillModifier() - 100.0f > currentPreparationTime)
			return PREPARATION_COOLDOWN - getUltimateSkillModifier() - currentPreparationTime;
		return 0.0f;
	}

	@Override
	public boolean canLeftClick()
	{
		return !getPlayer().isDisarmed() && getPlayer().getCurrentEndurance() >= Arrow.ENDURANCE_COST && currentArrowTime > Arrow.COOLDOWN
					&& !getPlayer().isCrowdControlled() && getPlayer().isInsideTower() && !isRapidFiring;
	}
	
	@Override
	public boolean canRightClick()
	{
		return rightClickCD() == 0 && getPlayer().getCurrentEndurance() >= BACKWARD_COST && !getPlayer().isCrowdControlled() && 
					getPlayer().isInsideTower() && !isRapidFiring;
	}

	@Override
	public boolean canFirstSkill()
	{
		return firstSkillCD() == 0 && !getPlayer().isDisarmed() && getPlayer().getCurrentEndurance() >= AimShot.ENDURANCE_COST &&
				!getPlayer().isCrowdControlled() && getPlayer().isInsideTower() && getFirstSkillLevel() > 0 && !isRapidFiring;
	}

	@Override
	public boolean canSecondSkill()
	{
		return secondSkillCD() == 0 && !getPlayer().isDisarmed() && getPlayer().getCurrentEndurance() >= SilenceArrow.ENDURANCE_COST &&
				!getPlayer().isCrowdControlled() && getPlayer().isInsideTower() && getSecondSkillLevel() > 0 && !isRapidFiring;
	}

	@Override
	public boolean canThirdSkill()
	{
		return thirdSkillCD() == 0 && getPlayer().getCurrentEndurance() >= RAPIDFIRE_COST && 
				!getPlayer().isCrowdControlled() && !getPlayer().isDisarmed() && getPlayer().isInsideTower() && 
					getThirdSkillLevel() > 0 && !isRapidFiring;
	}
	
	@Override
	public boolean canUltimateSkill()
	{
		return ultimateSkillCD() == 0 && getPlayer().getCurrentEndurance() >= PREPARATION_COST &&
				getPlayer().isInsideTower() && !getPlayer().isSilenced() && getUltimateSkillLevel() > 0 && !isRapidFiring;
	}
	
	@Override
	public float getFirstSkillModifier()
	{
		return getFirstSkillLevel();
	}

	@Override
	public float getSecondSkillModifier()
	{
		return getSecondSkillLevel();
	}

	@Override
	public float getThirdSkillModifier()
	{
		return (getThirdSkillLevel() - 1) * MULTISHOT_MODIFIER;
	}

	@Override
	public float getUltimateSkillModifier()
	{
		return getUltimateSkillLevel() * PREPARATION_MODIFIER;
	}
	
	@Override
	public float getDamageModifier()
	{
		if (getPlayer().getDexterity() / 10 < 1.0f)
			return 1.0f;
		
		return getPlayer().getDexterity() / 10;
	}
	
	@Override
	public PhysicsWeapon getWeapon()
	{
		return bow;
	}
	
	@Override
	public float getDamageReduced()
	{
		return DAMAGE_REDUCED;
	}
	
	@Override
	public String toString()
	{
		return "RANGER";
	}
	
	@Override
	public Map<Integer, String> leftClickDescription()
	{
		Map<Integer, String> description = new HashMap<Integer, String>();
		description.put(0, "Shot");
		description.put(1, "Cost: " + String.valueOf(Arrow.ENDURANCE_COST).replaceAll("\\..*$", "") + " endurance (Dexterity Damage)");
		description.put(2, "Deals from " + fixDecimal(Arrow.MIN_DAMAGE * getDamageModifier()) + " to " 
								+ fixDecimal(Arrow.MAX_DAMAGE * getDamageModifier())  + " damage to an enemy. \nShotting at long range increases the damage.");
		return description;
	}

	@Override
	public Map<Integer, String> rightClickDescription()
	{
		Map<Integer, String> description = new HashMap<Integer, String>();
		description.put(0, "Back steps");
		description.put(1, "Cost: " + String.valueOf(BACKWARD_COST).replaceAll("\\..*$", ""));
		description.put(2, "Allows the hunter to shoot and face the enemies \nwhile moving");
		return description;
	}

	@Override
	public Map<Integer, String> firstSkillDescription()
	{
		Map<Integer, String> description = new HashMap<Integer, String>();
		description.put(0, "Aim Shot (Level " + String.valueOf(getFirstSkillLevel()).replaceAll("\\..*$", "") + ")");
		description.put(1, "Cost: " + String.valueOf(AimShot.ENDURANCE_COST).replaceAll("\\..*$", "") +  " endurance (Dexterity Damage)");
		
		if (getFirstSkillLevel() == 0)
			description.put(2, "Deals from " + fixDecimal(AimShot.MIN_DAMAGE) + " to " + fixDecimal(AimShot.MAX_DAMAGE) + " damage to an enemy. \nShotting at long range increases the damage.");
		else
			description.put(2, "Deals from " + fixDecimal(AimShot.MIN_DAMAGE * (getFirstSkillModifier() + getDamageModifier())) + " to " + 
					fixDecimal(AimShot.MAX_DAMAGE * (getFirstSkillModifier() + getDamageModifier())) + " damage to an enemy. \nShotting at long range increases the damage.");
		description.put(3, "More points will increase the damage");
		return description;
	}

	@Override
	public Map<Integer, String> secondSkillDescription()
	{
		Map<Integer, String> description = new HashMap<Integer, String>();
		description.put(0, "Silencing Arrow (Level " + String.valueOf(getSecondSkillLevel()).replaceAll("\\..*$", "") + ")");
		description.put(1, "Cost: " + String.valueOf(SilenceArrow.ENDURANCE_COST).replaceAll("\\..*$", "") +  " endurance (Dexterity Damage)");
		
		if (getSecondSkillLevel() == 0)
			description.put(2, "Deals from " + fixDecimal(SilenceArrow.MIN_DAMAGE) + " to " + fixDecimal(SilenceArrow.MAX_DAMAGE) + " damage to an enemy and\n" +
					"silences the target.");
		else
			description.put(2, "Deals from " + fixDecimal(SilenceArrow.MIN_DAMAGE * (getDamageModifier() + getSecondSkillLevel())) + " to " + 
					fixDecimal(SilenceArrow.MAX_DAMAGE * (getDamageModifier() + getSecondSkillLevel())) + " damage to an enemy and\n" +
					"silences the target for " + String.valueOf(SilenceArrow.SILENCE_VALUE * getSecondSkillModifier() / 1000.0f).replaceAll("\\..*$", "")
					+ " seconds.");
		description.put(3, "More points will increase the silence duration");
		return description;
	}

	@Override
	public Map<Integer, String> thirdSkillDescription()
	{
		Map<Integer, String> description = new HashMap<Integer, String>();
		description.put(0, "Rapid Fire (Level " + String.valueOf(getThirdSkillLevel()).replaceAll("\\..*$", "") + ")");
		description.put(1, "Cost: " + String.valueOf(RAPIDFIRE_COST).replaceAll("\\..*$", "") +  " endurance (Dexterity Damage)");
		
		if (getThirdSkillLevel() == 0)
			description.put(2, "Shoots several perforating arrows dealing " + fixDecimal(Arrow.MIN_DAMAGE) +
					" to \n" + fixDecimal(Arrow.MAX_DAMAGE) + " damage for each arrow.");
		else
			description.put(2, "Shoots " + String.valueOf(NUMBER_OF_ARROWS + getThirdSkillModifier()).replaceAll("\\..*$", "") + " perforating arrows dealing " + 
					fixDecimal(Arrow.MIN_DAMAGE * getDamageModifier()) + " to \n" + fixDecimal(Arrow.MAX_DAMAGE * getDamageModifier()) + " damage for each arrow.");
		description.put(3, "More points will increase the number of arrows.");
		return description;
	}

	@Override
	public Map<Integer, String> ultimateSkillDescription()
	{
		Map<Integer, String> description = new HashMap<Integer, String>();
		description.put(0, "Preparation (Level " + String.valueOf(getUltimateSkillLevel()).replaceAll("\\..*$", "") + ")");
		description.put(1, "Cost: " + String.valueOf(PREPARATION_COST).replaceAll("\\..*$", "") +  " endurance");
		description.put(2, "Istantly restores all the cooldowns.");
		
		if (getUltimateSkillLevel() == 0)
			description.put(3, "Requires knowledge level 5.");
		
		else
			description.put(3, "More points will decrease the life requirement.");
		
		return description;
	}

	@Override
	public IDimension getSize()
	{
		getPlayer().setSize(SIZE);
		return SIZE;
	}
	
	@Override
	public void update(int delta)
	{
		super.update(delta);
		currentGlobalCD += delta;
		currentArrowTime += delta;
		currentArrowDuration += delta;
		currentBackwardTime += delta;
		currentAimshotTime += delta;
		currentAimshotDuration += delta;
		currentSilenceArrowTime += delta;
		currentSilenceDuration += delta;
		currentRapidFireTime += delta;
		currentPreparationTime += delta;
		
		if(isBackwarding() && currentBackwardTime > BACKWARD_DURATION)
		{
			setBackward(false);
			Direction facingDirection = Direction.getOppositeDirection(getPlayer().getFacingDirection());
			getPlayer().setFacingDirection(facingDirection);
		}
		
		if (arrow != null)
		{
			arrow.update(delta);
			if (currentArrowDuration > ARROW_DURATION)
			{
				arrow.clear();
				arrow = null;
			}
		}
		
		if (isRapidFiring && currentRapidFireTime % RAPIDFIRE_TICK_TIME >= 0 && 
				currentRapidFireTime % RAPIDFIRE_TICK_TIME < TheKnowledgeTowers.UPDATE_RATE && rapidfire.size() < NUMBER_OF_ARROWS + getThirdSkillModifier())
		{
		
			Arrow rapidArrow = new Arrow(getPlayer().x(), getPlayer().y(), getPlayer(), getDamageModifier());
			getPlayer().getOffensiveSpells().add(rapidArrow);
			
			Direction facingDirection = getPlayer().getFacingDirection();
			
			if(isBackwarding() && getPlayer().isMoving())
				facingDirection = Direction.getOppositeDirection(getPlayer().getLastDirection());
			
			rapidArrow.setPerforating(true);
			rapidArrow.setLastDirection(facingDirection);
			rapidArrow.bodyTransform(getPlayer(), facingDirection);
			rapidArrow.getSprite().setLastDirection(facingDirection);
			rapidfire.add(rapidArrow);

		}
		
		for (Arrow arrow : rapidfire)
			if (arrow != null)
				arrow.update(delta);
		
		if (rapidfire.size() >= NUMBER_OF_ARROWS + getThirdSkillModifier() && currentRapidFireTime > RAPIDFIRE_TICK_TIME * 
				(NUMBER_OF_ARROWS + getThirdSkillModifier()) + 1300.0f)
			isRapidFiring = false;

		if (rapidfire.size() > 0 && !isRapidFiring)
		{
			for (Arrow arrow : new ArrayList<Arrow>(rapidfire))
			{
				rapidfire.remove(arrow);
				arrow.clear();
				arrow = null;
			}
		}
		
		if (aimshot != null)
		{
			aimshot.update(delta);
			if (currentAimshotDuration > ARROW_DURATION)
			{
				aimshot.clear();
				aimshot = null;
			}
		}
		
		if (silenceArrow != null)
		{
			silenceArrow.update(delta);
			
			if (currentSilenceDuration > ARROW_DURATION)
			{
				silenceArrow.clear();
				silenceArrow = null;
			}
		}
			
	}
}
