package saga.progetto.tesi.job;

import static playn.core.PlayN.assets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import playn.core.AssetWatcher;
import playn.core.Image;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import saga.progetto.tesi.core.TheKnowledgeTowers;
import saga.progetto.tesi.entity.dynamicentity.Direction;
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.entity.dynamicentity.equip.Axe;
import saga.progetto.tesi.entity.dynamicentity.equip.PhysicsWeapon;
import saga.progetto.tesi.entity.dynamicentity.spell.Fear;
import saga.progetto.tesi.entity.dynamicentity.spell.Whirldwind;

public class Berserker extends Job
{
	private static final String PLAYER_PATH = "images/characters/player/berserker/berserker_sheet00.png";
	private static final String WOLF_PATH = "images/characters/player/berserker/wolf_sheet.png";
	private static final IDimension WOLF_SIZE = new Dimension(32.0f, 38.0f);
	private static final String LEFT_CLICK_PATH = "images/spells/axe_icon.png";
	private static final String RIGHT_CLICK_PATH = "images/spells/berserk_icon.png";
	private static final String FIRST_SPELL_PATH = "images/spells/bloodthirst_icon.png";
	private static final String SECOND_SPELL_PATH = "images/spells/fear_icon.png";
	private static final String THIRD_SPELL_PATH = "images/spells/whirlwind_icon.png";
	private static final String ULTIMATE_SPELL_PATH = "images/spells/wolf_icon.png";
	private static final float STR_MODIFIER =  1.5f;
	private static final float DEX_MODIFIER =  1.5f;
	private static final float INT_MODIFIER =  1.0f;
	private static final float BLOOD_THIRST_CD =  10000.0f;
	private static final float BLOOD_THIRST_DURATION =  5000.0f;
	private static final float BLOOD_THIRST_COST = 0.5f;
	private static final float BLOODTHIRST_MODIFIER = 0.25f;
	private static final float WOLF_CD =  60000.0f;
	private static final float WOLF_DURATION =  5000.0f;
	private static final float WOLF_COST =  40.0f;
	private static final float WOLF_MODIFIER = 5000.0f;
	private static final float BERSERK_CD =  500.0f;
	private static final float DAMAGE_REDUCED = 0.1f;
	private static final int ENEMIES_KILLED_REQUIRED = 250;
	private static Image characterImage;
	private static Image wolfImage;
	private static Image leftClickImage;
	private static Image rightClickImage;
	private static Image firstSpellImage;
	private static Image secondSpellImage;
	private static Image thirdSpellImage;
	private static Image ultimateSpellImage;
	
	private PhysicsWeapon axe;
	private Fear fear;
	private Whirldwind whirlwind;
	private float currentBerserkTime = 500.0f;
	private float currentBloodThirstTime = BLOOD_THIRST_CD;
	private float currentBloodThirstDuration = BLOOD_THIRST_DURATION;
	private float currentFearTime = Fear.COOLDOWN;
	private float currentWhirlwindTime = Whirldwind.COOLDOWN;
	private float currentWolfTime = WOLF_CD;
	private float cdReducer = 1.0f;
	
	public Berserker(float x, float y, Player player)
	{
		super(player);
		axe = new Axe(x, y - 16.0f, getMap());
		axe.setActive(false);
		initSpellIcons();
	}

	public static void loadAssets(AssetWatcher watcher)
	{
		wolfImage = assets().getImage(WOLF_PATH);
		leftClickImage = assets().getImage(LEFT_CLICK_PATH);
		rightClickImage = assets().getImage(RIGHT_CLICK_PATH);
		firstSpellImage = assets().getImage(FIRST_SPELL_PATH);
		secondSpellImage = assets().getImage(SECOND_SPELL_PATH);
		thirdSpellImage = assets().getImage(THIRD_SPELL_PATH);
		ultimateSpellImage = assets().getImage(ULTIMATE_SPELL_PATH);
		watcher.add(wolfImage);
		watcher.add(leftClickImage);
		watcher.add(rightClickImage);
		watcher.add(firstSpellImage);
		watcher.add(secondSpellImage);
		watcher.add(thirdSpellImage);
		watcher.add(ultimateSpellImage);
	}
	
	public static void loadCharacter() 
	{
		characterImage = assets().getImage(PLAYER_PATH);
	}

	// aggiungere requirement a metodo sopraclasse
	public static Number lockRequirement()
	{
		return ENEMIES_KILLED_REQUIRED;
	}
	
	@Override
	public List<Image> getSpellImages()
	{
		List<Image> spellImages = new LinkedList<Image>();
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
		if (isWolf())
			return wolfImage;
		
		return characterImage;
	}
	
	@Override
	public IDimension getSize()
	{
		if (isWolf())
			return WOLF_SIZE;

		return super.getSize();
	}
	@Override
	public void leftClick()
	{
		meleeAttack();
	}

	@Override
	public void rightClick()
	{
		super.rightClick();
		getPlayer().setSprinting(false);
		
		if (currentBerserkTime >= BERSERK_CD && getPlayer().getCurrentRage() - getPlayer().getTotalRage() / 10 >= 0 && !isWolf())
		{
			currentBerserkTime = 0.0f;
			if (!isBerserking())
			{
				setBerserk(true);
				getPlayer().setSpeedModifier(2.0f);
				getPlayer().setCurrentRage(getPlayer().getCurrentRage() - (getPlayer().getTotalRage() / 10));
			}
			
			else
			{
				setBerserk(false);
				getPlayer().setSpeedModifier(1.0f);
			}
		}
	}

	@Override
	public void firstSkill()
	{
		super.firstSkill();
		
		if (((isBerserking() && currentBloodThirstTime >= BLOOD_THIRST_CD ) || isWolf()) && 
				getPlayer().getCurrentRage() > 0 && getFirstSkillLevel() > 0)
		{
			setBloodThirsting(true);
			currentBloodThirstTime = 0.0f;
		}
	}
	
	@Override
	public void secondSkill()
	{
		super.secondSkill();
		
		if (currentFearTime * cdReducer > Fear.COOLDOWN && getSecondSkillLevel() > 0 && 
				((isBerserking() && getPlayer().getCurrentRage() >= Fear.RAGE_COST) || isWolf()))
		{
			fear = new Fear(getPlayer().x(), getPlayer().y(), getPlayer(), getSecondSkillModifier());
			getPlayer().getOffensiveSpells().add(fear);
			fear.setLastDirection(Direction.DEFAULT);
			fear.bodyTransform(getPlayer(), Direction.DEFAULT);
			fear.getSprite().setLastDirection(Direction.DEFAULT);
			
			if (!isWolf())
				getPlayer().setCurrentRage(getPlayer().getCurrentRage() - fear.getCost());
			
			currentFearTime = 0.0f;
		}
	}
	
	@Override
	public void thirdSkill()
	{
		super.thirdSkill();
		
		if (currentWhirlwindTime * cdReducer > Whirldwind.COOLDOWN  && getThirdSkillLevel() > 0 
				&& ((isBerserking() && getPlayer().getCurrentRage() >= Whirldwind.RAGE_COST) || isWolf()))
		{
			whirlwind = new Whirldwind(getPlayer().x(), getPlayer().y(), getPlayer(), 
					getThirdSkillModifier() + getPlayer().getDexterity() / 10);
			getPlayer().getOffensiveSpells().add(whirlwind);
			
			if (!isWolf())
				getPlayer().setCurrentRage(getPlayer().getCurrentRage() - whirlwind.getCost());
			
			currentWhirlwindTime = 0.0f;
			setWhirlWinding(true);
		}
	}
	
	@Override
	public void ultimateSkill()
	{
		super.ultimateSkill();
		
		if (currentWolfTime >= WOLF_CD && getPlayer().getCurrentRage() >= WOLF_COST && getUltimateSkillLevel() > 0)
		{
			currentWolfTime = 0.0f;
			setWolf(true);
			getPlayer().setCurrentRage(0.0f);
			getPlayer().setCurrentLife(getPlayer().getCurrentLife() + getPlayer().getTotalLife() / 2);
			cdReducer = 2.0f;
		}
	}
	
	@Override
	public float rightClickCD()
	{
		if (BERSERK_CD - 100.0f > currentBerserkTime * cdReducer)
			return BERSERK_CD - currentBerserkTime * cdReducer;
		return 0.0f;
	}
	
	@Override
	public float firstSkillCD()
	{
		if (BLOOD_THIRST_CD - 100.0f > currentBloodThirstTime)
			return BLOOD_THIRST_CD - currentBloodThirstTime;
		return 0.0f;
	}
	
	@Override
	public float secondSkillCD()
	{
		if (Fear.COOLDOWN - 100.0f > currentFearTime * cdReducer)
			return Fear.COOLDOWN - currentFearTime * cdReducer;
		return 0.0f;
	}
	
	@Override
	public float thirdSkillCD()
	{
		if (Whirldwind.COOLDOWN - 100.0f > currentWhirlwindTime * cdReducer)
			return Whirldwind.COOLDOWN - currentWhirlwindTime * cdReducer;
		return 0.0f;
	}
	
	
	@Override
	public float ultimateSkillCD()
	{
		if (WOLF_CD - 100.0f > currentWolfTime)
			return WOLF_CD - currentWolfTime;
		return 0.0f;
	}

	
	@Override
	public boolean canLeftClick()
	{
		return  (getPlayer().getCurrentEndurance() >= getWeapon().getCost() ||
				(isWolf() && getPlayer().getCurrentEndurance() >= getWeapon().getCost() / 2)) &&
				!getPlayer().isDisarmed() && !getPlayer().isCrowdControlled() && getPlayer().isInsideTower();
	}
	
	@Override
	public boolean canRightClick()
	{
		return  rightClickCD() == 0 && getPlayer().getCurrentRage() >= getPlayer().getTotalRage() / 10 && !isWolf() &&
				!getPlayer().isCrowdControlled() && getPlayer().isInsideTower();
	}

	@Override
	public boolean canFirstSkill()
	{
		return firstSkillCD() == 0 && ((isBerserking() && getPlayer().getCurrentRage() > 0) || isWolf()) &&
				!getPlayer().isCrowdControlled() && !getPlayer().isSilenced() && getPlayer().isInsideTower() && getFirstSkillLevel() > 0;
	}

	@Override
	public boolean canSecondSkill()
	{
		return secondSkillCD() == 0 && ((isBerserking() && getPlayer().getCurrentRage() >= Fear.RAGE_COST) || isWolf()) && 
				!getPlayer().isCrowdControlled() && !getPlayer().isSilenced() && getPlayer().isInsideTower() && getSecondSkillLevel() > 0;
	}

	@Override
	public boolean canThirdSkill()
	{
		return thirdSkillCD() == 0 && ((isBerserking() && getPlayer().getCurrentRage() >= Whirldwind.RAGE_COST) || isWolf()) && 
				!getPlayer().isDisarmed() && !getPlayer().isCrowdControlled() && getPlayer().isInsideTower() && getThirdSkillLevel() > 0;
	}
	
	@Override
	public boolean canUltimateSkill()
	{
		return ultimateSkillCD() == 0 && getPlayer().getCurrentRage() >= WOLF_COST && !getPlayer().isCrowdControlled() && 
				getPlayer().isInsideTower() && getUltimateSkillLevel() > 0;
	}
	

	@Override
	public float getFirstSkillModifier()
	{
		// cura di 33%, 66%, 100%
		return BLOODTHIRST_MODIFIER * getFirstSkillLevel();
	}

	@Override
	public float getSecondSkillModifier()
	{
		// tempo fear = 2, 4, 6 secondi
		return getSecondSkillLevel();
	}

	@Override
	public float getThirdSkillModifier()
	{
		// danno *1, *2, *3
		return getThirdSkillLevel();
	}

	@Override
	public float getUltimateSkillModifier()
	{
		// durata 10, 15, 20
		return WOLF_MODIFIER * getUltimateSkillLevel();
	}
	
	@Override
	public float getDamageModifier()
	{
		if (getPlayer().getStrength() / 20 + getPlayer().getDexterity() / 20 < 1.0f)
			return 1.0f;
		
		return getPlayer().getStrength() / 20 + getPlayer().getDexterity() / 20;
	}
	
	@Override
	public void setBerserk(boolean isBerserking)
	{
		super.setBerserk(isBerserking);
		
		if (isBerserking)
			getPlayer().setSpeedModifier(getPlayer().getSpeedModifier() * 2);
		
		else
			getPlayer().setSpeedModifier(getPlayer().getSpeedModifier() / 2);
	}
	
	@Override
	public PhysicsWeapon getWeapon()
	{
		return axe;
	}
	
	@Override
	public float getDamageReduced()
	{
		return DAMAGE_REDUCED;
	}
	
	@Override
	public String toString()
	{
		return "BERSERKER";
	}
	
	@Override
	public Map<Integer, String> leftClickDescription()
	{
		Map<Integer, String> description = new HashMap<Integer, String>();
		description.put(0, "Brutal Strike");
		description.put(1, "(Strength and Dexterity Damage)");
		description.put(2, "Deals from " + fixDecimal(getWeapon().getMinDmg() * getDamageModifier()) + " to " 
								+ fixDecimal(getWeapon().getMaxDmg() * getDamageModifier())  + " damage to an enemy.");
		return description;
	}

	@Override
	public Map<Integer, String> rightClickDescription()
	{
		Map<Integer, String> description = new HashMap<Integer, String>();
		description.put(0, "Berserk");
		description.put(1, "Requires: " + String.valueOf(getPlayer().getTotalRage() / 10).replaceAll("\\..*$", "") +  " rage");
		description.put(2, "The caster becomes a fury and uses new skills.\nIncrease the damage dealt and the damage taken.");
		return description;
	}

	@Override
	public Map<Integer, String> firstSkillDescription()
	{
		Map<Integer, String> description = new HashMap<Integer, String>();
		description.put(0, "Bloodthirst (Level " + String.valueOf(getFirstSkillLevel()).replaceAll("\\..*$", "") + ")");
		
		if (getFirstSkillLevel() == 0)
			description.put(2, "Consumes some rage to heal the caster.");
		
		else
			description.put(2, "Consumes some rage to heal the caster by " + 
								String.valueOf(getFirstSkillModifier() * 100).replaceAll("\\..*$", "") + "%");
		
		description.put(3, "More points will increase the healing value.");
		return description;
	}

	@Override
	public Map<Integer, String> secondSkillDescription()
	{
		Map<Integer, String> description = new HashMap<Integer, String>();
		description.put(0, "Fear (Level " + String.valueOf(getSecondSkillLevel()).replaceAll("\\..*$", "") + ")");
		description.put(1, "Cost: " + String.valueOf(Fear.RAGE_COST).replaceAll("\\..*$", "") + " rage");
		
		if (getSecondSkillLevel() == 0)
			description.put(2, "All the nearby enemies will run in fear");
		else
			description.put(2, "All the nearby enemies will run in fear for\n" + String.valueOf(Fear.BASE_FEAR_DURATION * getSecondSkillModifier() / 1000).replaceAll("\\..*$", "")
					+ " seconds.");
		description.put(3, "More points will increase the fear duration");
		return description;
	}

	@Override
	public Map<Integer, String> thirdSkillDescription()
	{
		Map<Integer, String> description = new HashMap<Integer, String>();
		description.put(0, "Whirlwind (Level " + String.valueOf(getThirdSkillLevel()).replaceAll("\\..*$", "") + ")");
		description.put(1, "Cost: " + String.valueOf(Whirldwind.RAGE_COST).replaceAll("\\..*$", "") + " rage (Dexterity Damage)");
		
		if (getThirdSkillLevel() == 0)
			description.put(2, "Deals many strikes of " + fixDecimal(Whirldwind.MIN_DAMAGE) + " to " + fixDecimal(Whirldwind.MAX_DAMAGE)  + 
					" damage in an \narea of " + String.valueOf(Whirldwind.RADIUS / 10).replaceAll("\\..*$", "") + " yards.");
		
		else
			description.put(2, "Deals many strikes of " + fixDecimal((Whirldwind.MIN_DAMAGE * getThirdSkillModifier() + getPlayer().getDexterity() / 10)) + 
					" to " + fixDecimal((Whirldwind.MAX_DAMAGE * getThirdSkillModifier() + getPlayer().getDexterity() / 10))  + " damage in an \narea of " +
							String.valueOf(Whirldwind.RADIUS / 10).replaceAll("\\..*$", "") + " yards.");
		
		description.put(3, "More points will increase the damage.");
		return description;
	}

	@Override
	public Map<Integer, String> ultimateSkillDescription()
	{
		Map<Integer, String> description = new HashMap<Integer, String>();
		description.put(0, "Werewolf (Level " + String.valueOf(getUltimateSkillLevel()).replaceAll("\\..*$", "") + ")");
		description.put(1, "Uses all the caster's rage.");
		
		if (getUltimateSkillLevel() == 0)
			description.put(2, "The caster uses his rage to transform into a wolf \nincreasing his battle proficiency.");
		
		else
			description.put(2, "The caster uses his rage to transform into a wolf \nfor " +
									String.valueOf((WOLF_DURATION + getUltimateSkillModifier()) / 1000.0f).replaceAll("\\..*$", "") + 
										" seconds, increasing his battle proficiency.");
		
		if (getUltimateSkillLevel() == 0)
			description.put(3, "Requires knowledge level 5.");
		
		else
			description.put(3, "More points will decrease the life requirement.");
		
		return description;
	}
	
	
	public void setBloodThirsting(boolean isBloodThirsting)
	{
		super.setBloodThirsting(isBloodThirsting);
		
		if (isBloodThirsting)
			currentBloodThirstDuration = 0.0f;
	}
	
	@Override
	public void update(int delta)
	{
		super.update(delta);
		
		if (currentBerserkTime <= BERSERK_CD)
			currentBerserkTime += delta;
		
		if (currentBloodThirstTime <= BLOOD_THIRST_CD)
			currentBloodThirstTime += delta;
		
		if (currentBloodThirstDuration <= BLOOD_THIRST_DURATION)
			currentBloodThirstDuration += delta;
		
		if (currentFearTime <= Fear.COOLDOWN)
			currentFearTime += delta;
		
		if (currentWhirlwindTime <= Whirldwind.COOLDOWN)
			currentWhirlwindTime += delta;
		
		if (currentWolfTime <= WOLF_CD)
			currentWolfTime += delta;
		
		if (getPlayer().getCurrentRage() > 0 && getPlayer().getCurrentLife() < getPlayer().getTotalLife() 
				&& isBloodThirsting() && currentBloodThirstDuration <= BLOOD_THIRST_DURATION)
		{
			getPlayer().setCurrentRage(getPlayer().getCurrentRage() - TheKnowledgeTowers.UPDATE_RATE * getPlayer().getTotalRage() *
					BLOOD_THIRST_COST / BLOOD_THIRST_DURATION);
			getPlayer().setCurrentLife(getPlayer().getCurrentLife() + TheKnowledgeTowers.UPDATE_RATE * getPlayer().getTotalRage() *
					getFirstSkillModifier() / BLOOD_THIRST_DURATION);
		}
		
		else if (isBloodThirsting())
		{
			setBloodThirsting(false);
			currentBloodThirstDuration = BLOOD_THIRST_DURATION;
		}
		
		if (isBerserking() && getPlayer().getCurrentRage() <= 0)
			setBerserk(false);
		
		if (fear != null)
		{
			fear.update(delta);
			if (currentFearTime > Fear.PHYSICS)
			{
				fear.clear();
				fear = null;
			}
		}
		
		if (whirlwind != null)
		{
			whirlwind.update(delta);
			if (currentWhirlwindTime > Whirldwind.DURATION)
			{
				setWhirlWinding(false);
				getPlayer().getWeapon().setVisible(false);
				whirlwind.clear();
				whirlwind = null;
			}
		}
		
		if (isWolf() && currentWolfTime > WOLF_DURATION + getUltimateSkillModifier())
		{
			setWolf(false);
			cdReducer = 1.0f;
		}
	}
}
