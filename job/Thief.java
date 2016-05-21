package saga.progetto.tesi.job;

import static playn.core.PlayN.assets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import playn.core.AssetWatcher;
import playn.core.Image;
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.entity.dynamicentity.equip.Dagger;
import saga.progetto.tesi.entity.dynamicentity.equip.PhysicsWeapon;

public class Thief extends Job
{
	private static final String PLAYER_PATH = "images/characters/player/thief/thief_sheet00.png";
	private static final String LEFT_CLICK_PATH = "images/spells/dagger_icon.png";
	private static final String RIGHT_CLICK_PATH = "images/spells/evade_icon.png";
	private static final String FIRST_SPELL_PATH = "images/spells/envenom_icon.png";
	private static final String SECOND_SPELL_PATH = "images/spells/sleep_icon.png";
	private static final String THIRD_SPELL_PATH = "images/spells/stealth_icon.png";
	private static final String ULTIMATE_SPELL_PATH = "images/spells/vanish_icon.png";
	private static final float STR_MODIFIER =  1.0f;
	private static final float DEX_MODIFIER =  1.5f;
	private static final float INT_MODIFIER =  1.5f;
	private static final float EVADE_DURATION = 1000.0f;
	private static final float EVADE_COOLDOWN = 2000.0f;
	private static final float EVADE_COST = 10.0f;
	private static final float ENVENOM_TIME = 5000.0f;
	private static final float ENVENOM_COST = 3.0f;
	private static final float ENVENOM_MODIFIER = 1.0f;
	private static final float SLEEP_COOLDOWN = 20000.0f;
	private static final float SLEEP_TIME = 10000.0f;
	private static final float SLEEP_COST = 5.0f;
	private static final float STEALTH_COOLDOWN = 2000.0f;
	private static final float STEALTH_COST = 1.0f;
	private static final float STEALTH_TICK_COST = 0.04f;
	private static final float STEALTH_MODIFIER = 0.33f;
	private static final float VANISH_COOLDOWN = 180000.0f;
	private static final float VANISH_MODIFIER = 60000.0f;
	private static final float VANISH_COST = 25.0f;
	private static final float DAMAGE_REDUCED = 0.1f;
	private static final float TOWERS_REQUIRED = 5;
	private static Image characterImage;
	private static Image leftClickImage;
	private static Image rightClickImage;
	private static Image firstSpellImage;
	private static Image secondSpellImage;
	private static Image thirdSpellImage;
	private static Image ultimateSpellImage;
	
	private PhysicsWeapon dagger;
	private float currentEvadeTime = EVADE_COOLDOWN;
	private float currentEnvenomTime = ENVENOM_TIME;
	private float currentSleepTime = SLEEP_COOLDOWN;
	private float currentStealthTime = STEALTH_COOLDOWN;
	private float currentVanishTime = getUltimateSkillModifier();
	private boolean isHidden;
	
	public Thief(float x, float y, Player player)
	{
		super(player);
		dagger = new Dagger(x, y - 16.0f, getMap());
		dagger.setActive(false);
		initSpellIcons();
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

	public static void loadCharacter() 
	{
		characterImage = assets().getImage(PLAYER_PATH);
	}

	// aggiungere requirement a metodo sopraclasse
	public static Number lockRequirement()
	{
		return TOWERS_REQUIRED;
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
		return characterImage;
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
		
		if (getPlayer().getCurrentMana() >= EVADE_COST && currentEvadeTime > EVADE_COOLDOWN && !isHidden)
		{
			currentEvadeTime = 0.0f;
			setEvading(true);
			getPlayer().setCurrentMana(getPlayer().getCurrentMana() - EVADE_COST);
		}
	}

	// danno int
	@Override
	public void firstSkill()
	{
		super.firstSkill();
		
		if (getPlayer().getCurrentMana() >= ENVENOM_COST && currentEnvenomTime > ENVENOM_TIME && getFirstSkillLevel() > 0)
		{
			currentEnvenomTime = 0.0f;
			getWeapon().addPoison(ENVENOM_TIME, getFirstSkillModifier() + getPlayer().getIntelligence() / 5);
			getPlayer().setCurrentMana(getPlayer().getCurrentMana() - ENVENOM_COST);
		}
	}

	@Override
	public void secondSkill()
	{
		super.secondSkill();
		
		if (getPlayer().getCurrentMana() >= SLEEP_COST && currentSleepTime > SLEEP_COOLDOWN && getSecondSkillLevel() > 0)
		{
			currentSleepTime = 0.0f;
			getWeapon().addSleep(getSecondSkillModifier());
			getPlayer().setCurrentMana(getPlayer().getCurrentMana() - SLEEP_COST);
		}
	}
	
	@Override
	public void thirdSkill()
	{
		super.thirdSkill();
		
		if (currentStealthTime >= STEALTH_COOLDOWN && getPlayer().getCurrentMana() - STEALTH_COST >= 0 
				&& !getPlayer().inCombat() && !isEvading() && getThirdSkillLevel() > 0)
		{
			currentStealthTime = 0.0f;
			setHidden(!isHidden);
			
			if (isHidden)
			{
				getPlayer().setSprinting(false);
				getPlayer().setSpeedModifier(getPlayer().getSpeedModifier() / 2);
				getPlayer().setCurrentMana(getPlayer().getCurrentMana() - STEALTH_COST);
			}
			
			else
				getPlayer().setSpeedModifier(getPlayer().getSpeedModifier() * 2);
		}
	}
	
	@Override
	public void ultimateSkill()
	{
		super.ultimateSkill();
		
		if (currentVanishTime >= getUltimateSkillModifier() && getPlayer().getCurrentMana() >= VANISH_COST && !isEvading() && !isHidden &&
				 getUltimateSkillLevel() > 0)
		{
			currentVanishTime = 0.0f;
			
			setHidden(true);
			getPlayer().putOutOfCombat();
			
			if (isHidden)
			{
				getPlayer().setSprinting(false);
				getPlayer().setSpeedModifier(getPlayer().getSpeedModifier() / 2);
				getPlayer().setCurrentMana(getPlayer().getCurrentMana() - VANISH_COST);
			}
		}
	}

	@Override
	public float rightClickCD()
	{
		if (EVADE_COOLDOWN - 100.0f > currentEvadeTime)
			return EVADE_COOLDOWN - currentEvadeTime;
		return 0.0f;
	}
	
	@Override
	public float firstSkillCD()
	{
		if (ENVENOM_TIME - 100.0f > currentEnvenomTime)
			return ENVENOM_TIME - currentEnvenomTime;
		return 0.0f;
	}
	
	@Override
	public float secondSkillCD()
	{
		if (SLEEP_COOLDOWN - 100.0f > currentSleepTime)
			return SLEEP_COOLDOWN - currentSleepTime;
		return 0.0f;
	}
	
	@Override
	public float thirdSkillCD()
	{
		if (STEALTH_COOLDOWN - 100.0f > currentStealthTime)
			return STEALTH_COOLDOWN - currentStealthTime;
		return 0.0f;
	}
	
	@Override
	public float ultimateSkillCD()
	{
		if (getUltimateSkillModifier() - 100.0f > currentVanishTime)
			return getUltimateSkillModifier() - currentVanishTime;
		return 0.0f;
	}

	@Override
	public boolean canLeftClick()
	{
		return   getPlayer().getCurrentEndurance() >= getWeapon().getCost() && 
				!getPlayer().isDisarmed() && !getPlayer().isCrowdControlled() && getPlayer().isInsideTower();
	}
	
	@Override
	public boolean canRightClick()
	{
		return rightClickCD() == 0 && getPlayer().getCurrentMana() >= EVADE_COST && !getPlayer().isCrowdControlled() && 
				!getPlayer().isSilenced() && getPlayer().isInsideTower();
	}

	@Override
	public boolean canFirstSkill()
	{
		return firstSkillCD() == 0 && getPlayer().getCurrentMana() >= ENVENOM_COST && !getPlayer().isCrowdControlled() &&
				!getPlayer().isDisarmed() && getPlayer().isInsideTower() && getFirstSkillLevel() > 0;
	}

	@Override
	public boolean canSecondSkill()
	{
		return secondSkillCD() == 0 && getPlayer().getCurrentMana() >= SLEEP_COST && !getPlayer().isCrowdControlled() &&
				!getPlayer().isDisarmed() && getPlayer().isInsideTower() && getSecondSkillLevel() > 0;
	}

	@Override
	public boolean canThirdSkill()
	{
		return thirdSkillCD() == 0 && !isHidden && !isEvading() && getPlayer().getCurrentMana() >= STEALTH_COST && !getPlayer().inCombat()
				&& !getPlayer().isCrowdControlled() && !getPlayer().isSilenced() && getPlayer().isInsideTower() && getThirdSkillLevel() > 0;
	}
	
	@Override
	public boolean canUltimateSkill()
	{
		return ultimateSkillCD() == 0 && !isHidden && !isEvading() && getPlayer().getCurrentMana() >= VANISH_COST && 
				!getPlayer().isCrowdControlled() && !getPlayer().isSilenced() && getPlayer().isInsideTower() && getUltimateSkillLevel() > 0;
	}
	
	@Override
	public float getFirstSkillModifier()
	{
		return ENVENOM_MODIFIER * getFirstSkillLevel();
	}

	@Override
	public float getSecondSkillModifier()
	{
		return SLEEP_TIME * getSecondSkillLevel();
	}

	@Override
	public float getThirdSkillModifier()
	{
		return 1 - ((getThirdSkillLevel() - 1) * STEALTH_MODIFIER);
	}

	@Override
	public float getUltimateSkillModifier()
	{
		return VANISH_COOLDOWN - (getUltimateSkillLevel() - 1) * VANISH_MODIFIER;
	}
	
	@Override
	public float getDamageModifier()
	{
		if (getPlayer().getDexterity() / 10 < 1.0f)
			return 1.0f;
		
		return getPlayer().getDexterity() / 10;
	}
	
	@Override
	public boolean isHidden()
	{
		return isHidden;
	}
	
	@Override
	public void setHidden(boolean isHidden)
	{
		this.isHidden = isHidden;
	}
	
	@Override
	public PhysicsWeapon getWeapon()
	{
		return dagger;
	}
	
	@Override
	public float getDamageReduced()
	{
		return DAMAGE_REDUCED;
	}
	
	@Override
	public String toString()
	{
		return "THIEF";
	}
	
	@Override
	public Map<Integer, String> leftClickDescription()
	{
		Map<Integer, String> description = new HashMap<Integer, String>();
		description.put(0, "Piercing Strike");
		description.put(1, "(Dexterity Damage)");
		description.put(2, "Deals from " + fixDecimal(getWeapon().getMinDmg() * getDamageModifier()) + " to " 
								+ fixDecimal(getWeapon().getMaxDmg() * getDamageModifier())  + " damage to an enemy.");
		return description;
	}

	@Override
	public Map<Integer, String> rightClickDescription()
	{
		Map<Integer, String> description = new HashMap<Integer, String>();
		description.put(0, "Evasion");
		description.put(1, "Requires: " + String.valueOf(EVADE_COST).replaceAll("\\..*$", "") +  " mana");
		description.put(2, "becomes momentarily invulnerable for " + String.valueOf(EVADE_DURATION / 1000.0f).replaceAll("\\..*$", "") + " second");
		return description;
	}

	@Override
	public Map<Integer, String> firstSkillDescription()
	{
		Map<Integer, String> description = new HashMap<Integer, String>();
		description.put(0, "Envenom (Level " + String.valueOf(getFirstSkillLevel()).replaceAll("\\..*$", "") + ")");
		description.put(1, "Requires: " + String.valueOf(ENVENOM_COST).replaceAll("\\..*$", "") +  " mana (Intelligence Damage)");
		description.put(2, "Applies poison to the weapon.");
		description.put(3, "More points will increase the poison damage.");
		return description;
	}

	@Override
	public Map<Integer, String> secondSkillDescription()
	{
		Map<Integer, String> description = new HashMap<Integer, String>();
		description.put(0, "Sleeping Poison (Level " + String.valueOf(getSecondSkillLevel()).replaceAll("\\..*$", "") + ")");
		description.put(1, "Cost: " + String.valueOf(SLEEP_COST).replaceAll("\\..*$", "") + " mana");
		
		if (getSecondSkillLevel() == 0)
			description.put(2, "Applies a sleeping poison to the weapon.");
		else
			description.put(2, "Applies a sleeping poison to the weapon." + "The effect \nwill last for " + 
							String.valueOf(getSecondSkillModifier() / 1000).replaceAll("\\..*$", "") + " seconds");
		description.put(3, "More points will increase the sleep duration");
		return description;
	}

	@Override
	public Map<Integer, String> thirdSkillDescription()
	{
		Map<Integer, String> description = new HashMap<Integer, String>();
		description.put(0, "Stealth (Level " + String.valueOf(getThirdSkillLevel()).replaceAll("\\..*$", "") + ")");
		description.put(1, "Cost: " + String.valueOf(STEALTH_TICK_COST * getThirdSkillModifier() * 1000.0f / 11.0f).replaceAll("\\..*$", "") 
				+ " mana per second");
		description.put(2, "The character hides in the shadows and cannot \nbe seen by the enemies. The next attack will deal \nvery high damage. Cannot be used in combat");
		description.put(3, "More points will decrease the cost and raise the movement speed");
		return description;
	}

	@Override
	public Map<Integer, String> ultimateSkillDescription()
	{
		Map<Integer, String> description = new HashMap<Integer, String>();
		description.put(0, "Vanish (Level " + String.valueOf(getUltimateSkillLevel()).replaceAll("\\..*$", "") + ")");
		description.put(1, "Cost: " + String.valueOf(VANISH_COST).replaceAll("\\..*$", "") + " mana");
		description.put(2, "Allows the thief to stealth in combat.");
		
		if (getUltimateSkillLevel() == 0)
			description.put(3, "Requires knowledge level 5.");
		
		else
			description.put(3, "More points will decrease the life requirement.");
		
		return description;
	}
	
	@Override
	public void update(int delta)
	{
		currentEvadeTime += delta;
		currentEnvenomTime += delta;
		currentSleepTime += delta;
		currentVanishTime += delta;
		
		super.update(delta);
		currentStealthTime += delta;
		
		if (isEvading() && currentEvadeTime > EVADE_DURATION)
			setEvading(false);
		
		if (getPlayer().getCurrentMana() == 0 && isHidden)
			setHidden(false);
		
		if (isHidden())
			getPlayer().setCurrentMana(getPlayer().getCurrentMana() - STEALTH_TICK_COST * getThirdSkillModifier());
	}
}
