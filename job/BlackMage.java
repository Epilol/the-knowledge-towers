package saga.progetto.tesi.job;

import static playn.core.PlayN.assets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import playn.core.AssetWatcher;
import playn.core.Image;
import saga.progetto.tesi.core.TheKnowledgeTowers;
import saga.progetto.tesi.entity.dynamicentity.Direction;
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.entity.dynamicentity.equip.PhysicsWeapon;
import saga.progetto.tesi.entity.dynamicentity.spell.FireBall;
import saga.progetto.tesi.entity.dynamicentity.spell.IceBolt;
import saga.progetto.tesi.entity.dynamicentity.spell.ManaShield;
import saga.progetto.tesi.entity.dynamicentity.spell.Spell;
import saga.progetto.tesi.entity.dynamicentity.spell.Storm;
import saga.progetto.tesi.entity.dynamicentity.spell.Wand;
import saga.progetto.tesi.map.GameMap;

public class BlackMage extends Job
{	
	private static final String PLAYER_PATH = "images/characters/player/wizard/wizard_sheet";
	private static final String LEFT_CLICK_PATH = "images/spells/wand_icon.png";
	private static final String RIGHT_CLICK_PATH = "images/spells/manashield_icon.png";
	private static final String FIRST_SPELL_PATH = "images/spells/fireball_icon.png";
	private static final String SECOND_SPELL_PATH = "images/spells/icebolt_icon.png";
	private static final String THIRD_SPELL_PATH = "images/spells/blink_icon.png";
	private static final String ULTIMATE_SPELL_PATH = "images/spells/storm_icon.png";
	private static final float STR_MODIFIER =  1.0f;
	private static final float DEX_MODIFIER =  1.0f;
	private static final float INT_MODIFIER =  2.0f;
	private static final float GLOBAL_CD = 1000.0f;
	private static final float BLINK_CD = 12000.0f;
	private static final float BLINK_COST = 12.0f;
	private static final float BLINK_RANGE = 200.0f;
	private static final float BLINK_MODIFIER = 3000.0f;
	private static final float STORM_DURATION_MODIFIER = 1000.0f;
	private static final float STORM_TIME_MODIFIER = 200.0f;
	private static final float DAMAGE_REDUCED = 0.0f;
	private static final float TOWERS_REQUIRED = 5;
	private static Image characterImage;
	private static Image leftClickImage;
	private static Image rightClickImage;
	private static Image firstSpellImage;
	private static Image secondSpellImage;
	private static Image thirdSpellImage;
	private static Image ultimateSpellImage;
	
	private Wand wand;
	private ManaShield manaShield;
	private FireBall fireBall;
	private IceBolt iceBolt;
	private List<Storm> storms;
	private Vec2 castPoint;
	private float currentCd = GLOBAL_CD;
	private float currentWandTime = Wand.COOLDOWN;
	private float currentManaShieldTime = ManaShield.COOLDOWN;
	private float currentFireballTime = FireBall.COOLDOWN;
	private float currentIceboltTime = IceBolt.COOLDOWN;
	private float currentBlinkTime = getThirdSkillModifier();
	private float currentStormTime = Storm.COOLDOWN;
	private boolean hasBlinked;
	private boolean isStorming;
	
	public BlackMage(Player player)
	{
		super(player);
		initSpellIcons();
		storms = new LinkedList<Storm>();
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
	
	public static void loadCharacter(String selectedCharacter) 
	{
		characterImage = assets().getImage(PLAYER_PATH + selectedCharacter + ".png");
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
		if (getPlayer().getCurrentMana() >= Wand.MANA_COST && currentWandTime > Wand.COOLDOWN && currentCd > GLOBAL_CD)
		{
			wand = new Wand(getPlayer().x(), getPlayer().y(), getPlayer(), getDamageModifier() / 3);
			getPlayer().getOffensiveSpells().add(wand);
			wand.setLastDirection(getPlayer().getFacingDirection());
			wand.bodyTransform(getPlayer(), getPlayer().getFacingDirection());
			wand.getSprite().setLastDirection(getPlayer().getFacingDirection());
			getPlayer().setCurrentMana(getPlayer().getCurrentMana() - wand.getCost());
			currentWandTime = 0.0f;
		}
	}

	@Override
	public void rightClick()
	{
		super.rightClick();
		
		if (currentManaShieldTime > ManaShield.COOLDOWN && currentCd > GLOBAL_CD && getPlayer().getCurrentMana() >= ManaShield.MANA_COST)
		{
			if (getPlayer().hasManaShield())
			{
				manaShield.clear();
				getPlayer().setManaShield(false);
				getPlayer().setCurrentMana(getPlayer().getCurrentMana() - manaShield.getCost());
				currentManaShieldTime = 0.0f;
				manaShield = null;
			}
			
			else
			{
				manaShield = new ManaShield(getPlayer().x(), getPlayer().y(), getPlayer());
				manaShield.applyEffect();
				getPlayer().setCurrentMana(getPlayer().getCurrentMana() - manaShield.getCost());
			}

		}
	}

	public void firstSkill()
	{
		super.firstSkill();
		
		if (getPlayer().getCurrentMana() >= FireBall.MANA_COST * getFirstSkillLevel() && currentFireballTime > FireBall.COOLDOWN && currentCd > GLOBAL_CD
				&& getFirstSkillLevel() > 0)
		{
			fireBall = new FireBall(getPlayer().x(), getPlayer().y(), getPlayer(), 
					getFirstSkillModifier() + getDamageModifier());
			getPlayer().getOffensiveSpells().add(fireBall);
			fireBall.setLastDirection(getPlayer().getFacingDirection());
			fireBall.bodyTransform(getPlayer(), getPlayer().getFacingDirection());
			fireBall.getSprite().setLastDirection(getPlayer().getFacingDirection());
			getPlayer().setCurrentMana(getPlayer().getCurrentMana() - fireBall.getCost() * getFirstSkillLevel());
			currentCd = 0.0f;
			currentFireballTime = 0.0f;
		}
	}

	@Override
	public void secondSkill()
	{
		super.secondSkill();
		
		if (getPlayer().getCurrentMana() >= IceBolt.MANA_COST * getSecondSkillLevel() && currentIceboltTime > IceBolt.COOLDOWN && currentCd > GLOBAL_CD
				&& getSecondSkillLevel() > 0)
		{
			iceBolt = new IceBolt(getPlayer().x(), getPlayer().y(), getPlayer(), 
					getSecondSkillLevel() + getDamageModifier(), getSecondSkillModifier());
			getPlayer().getOffensiveSpells().add(iceBolt);
			iceBolt.setLastDirection(getPlayer().getFacingDirection());
			iceBolt.bodyTransform(getPlayer(), getPlayer().getFacingDirection());
			iceBolt.getSprite().setLastDirection(getPlayer().getFacingDirection());
			getPlayer().setCurrentMana(getPlayer().getCurrentMana() - iceBolt.getCost() * getSecondSkillLevel());
			currentCd = 0.0f;
			currentIceboltTime = 0.0f;
		}
	}
	
	@Override
	public void thirdSkill()
	{
		super.thirdSkill();
		
		if (getPlayer().getCurrentMana() >= BLINK_COST * getThirdSkillLevel() && currentBlinkTime > getThirdSkillModifier() && currentCd > GLOBAL_CD 
				&& getThirdSkillLevel() > 0)
		{
			float range = BLINK_RANGE;
			while (!hasBlinked && range > 0)
			{
				int cellX = (int) ((getPlayer().x() + range * getPlayer().getFacingDirection().x()) / GameMap.PTM_RATIO);
				int cellY = (int) ((getPlayer().y() + range * getPlayer().getFacingDirection().y()) / GameMap.PTM_RATIO);
				if (!getMap().getBackground()[cellX][cellY].isActive())
				{
					getPlayer().getBody().setTransform(getPlayer().getBody().getPosition().add(
						new Vec2((range * getPlayer().getFacingDirection().x())/ GameMap.PTM_RATIO, 
							(range * getPlayer().getFacingDirection().y())/ GameMap.PTM_RATIO)),
								getPlayer().getFacingDirection().getAngle());
					getPlayer().setCurrentMana(getPlayer().getCurrentMana() - BLINK_COST * getThirdSkillLevel());
					hasBlinked = true;
					currentCd = 0.0f;
					currentBlinkTime = 0.0f;
				}
				
				else
					range -= GameMap.PTM_RATIO;
			}
		}
	}
	
	@Override
	public void ultimateSkill()
	{
		super.ultimateSkill();
		
		if (getPlayer().getCurrentMana() >= Storm.MANA_COST * getUltimateSkillLevel() && currentStormTime > Storm.COOLDOWN && currentCd > GLOBAL_CD 
				&& getUltimateSkillLevel() > 0)
		{
			isStorming = true;
			castPoint = getPlayer().getBody().getPosition();
			getPlayer().setCurrentMana(getPlayer().getCurrentMana() - Storm.MANA_COST * getUltimateSkillLevel());
			currentCd = 0.0f;
			currentStormTime = 0.0f;
		}
	}
	
	public float rightClickCD()
	{
		if (ManaShield.COOLDOWN - 100.0f > currentManaShieldTime)
			return ManaShield.COOLDOWN - currentManaShieldTime;
		return 0.0f;
	}
	
	public float firstSkillCD()
	{
		if (FireBall.COOLDOWN - 100.0f > currentFireballTime)
			return FireBall.COOLDOWN - currentFireballTime;
		return 0.0f;
	}
	
	public float secondSkillCD()
	{
		if (IceBolt.COOLDOWN - 100.0f > currentIceboltTime)
			return IceBolt.COOLDOWN - currentIceboltTime;
		return 0.0f;
	}
	
	public float thirdSkillCD()
	{
		if (getThirdSkillModifier() - 100.0f > currentBlinkTime)
			return getThirdSkillModifier() - currentBlinkTime;
		return 0.0f;
	}
	
	@Override
	public float ultimateSkillCD()
	{
		if (Storm.COOLDOWN - 100.0f > currentStormTime)
			return Storm.COOLDOWN - currentStormTime;
		return 0.0f;
	}
	
	@Override
	public boolean canLeftClick()
	{
		return  getPlayer().getCurrentMana() >= Wand.MANA_COST && !getPlayer().isSilenced() && 
				 !getPlayer().isCrowdControlled() && getPlayer().isInsideTower();
	}
	
	@Override
	public boolean canRightClick()
	{
		return rightClickCD() == 0 && getPlayer().getCurrentMana() >= ManaShield.MANA_COST && 
				!getPlayer().isCrowdControlled() && !getPlayer().isSilenced() && getPlayer().isInsideTower();
	}

	@Override
	public boolean canFirstSkill()
	{
		return firstSkillCD() == 0 && getPlayer().getCurrentMana() >= FireBall.MANA_COST * getFirstSkillLevel() && !getPlayer().isCrowdControlled() 
				&& getFirstSkillLevel() > 0 && !getPlayer().isSilenced() && getPlayer().isInsideTower();
	}

	@Override
	public boolean canSecondSkill()
	{
		return secondSkillCD() == 0 && getPlayer().getCurrentMana() >= IceBolt.MANA_COST * getSecondSkillLevel() && !getPlayer().isCrowdControlled()
			&& getSecondSkillLevel() > 0 && !getPlayer().isSilenced() && getPlayer().isInsideTower();
	}

	@Override
	public boolean canThirdSkill()
	{
		return thirdSkillCD() == 0 && getPlayer().getCurrentMana() >= BLINK_COST * getThirdSkillLevel() && !getPlayer().isCrowdControlled()
				&& getThirdSkillLevel() > 0 && !getPlayer().isSilenced() && getPlayer().isInsideTower();
	}
	
	@Override
	public boolean canUltimateSkill()
	{
		return ultimateSkillCD() == 0 && getPlayer().getCurrentMana() >= Storm.MANA_COST * getUltimateSkillLevel() && !getPlayer().isCrowdControlled()
				&& getUltimateSkillLevel() > 0 && !getPlayer().isSilenced() && getPlayer().isInsideTower();
	}

	@Override
	public float getFirstSkillModifier()
	{
		return getFirstSkillLevel();
	}

	@Override
	public float getSecondSkillModifier()
	{
		return (getSecondSkillLevel() - 1) * 1000.0f;
	}

	@Override
	public float getThirdSkillModifier()
	{
		return BLINK_CD - (getThirdSkillLevel() - 1) * BLINK_MODIFIER;
	}

	@Override
	public float getUltimateSkillModifier()
	{
		return getUltimateSkillLevel();
	}
	
	@Override
	public float getDamageModifier()
	{
		if (getPlayer().getIntelligence() / 10 < 1.0f)
			return 1.0f;
		return getPlayer().getIntelligence() / 10;
	}
	
	@Override
	public boolean isHidden()
	{
		return false;
	}
	
	public Spell getSpell()
	{
		return fireBall;
	}
	
	public float getSpellDamage()
	{
		return fireBall.getSpellDamage();
	}
	
	public Body getSpellBody()
	{
		return fireBall.getBody();
	}
	
	@Override
	public PhysicsWeapon getWeapon()
	{
		return null;
	}
	
	@Override
	public boolean hasBlinked()
	{
		return hasBlinked;
	}
	
	@Override
	public String toString()
	{
		return "BLACK_MAGE";
	}
	

	@Override
	public Map<Integer, String> leftClickDescription()
	{
		Map<Integer, String> description = new HashMap<Integer, String>();
		description.put(0, "Wand");
		description.put(1, "Cost: " + String.valueOf(Wand.MANA_COST).replaceAll("\\..*$", "") +  " mana (Intelligence Damage)");
		description.put(2, "Deals from " + fixDecimal(Wand.MIN_DAMAGE * getDamageModifier() / 3) + " to " + 
							fixDecimal(Wand.MAX_DAMAGE * getDamageModifier() / 3) + " damage to an enemy.");
		return description;
	}

	@Override
	public Map<Integer, String> rightClickDescription()
	{
		Map<Integer, String> description = new HashMap<Integer, String>();
		description.put(0, "Mana Shield");
		description.put(1, "Cost: " + String.valueOf(ManaShield.MANA_COST).replaceAll("\\..*$", "") +  " mana");
		description.put(2, "Redirects " + String.valueOf(ManaShield.DAMAGE_REDUCED * 100).replaceAll("\\..*$", "")  + "% of the damage taken from life \nto mana.");
		return description;
	}

	@Override
	public Map<Integer, String> firstSkillDescription()
	{
		Map<Integer, String> description = new HashMap<Integer, String>();
		description.put(0, "Fireball (Level " + String.valueOf(getFirstSkillLevel()).replaceAll("\\..*$", "") + ")");
		
		if (getFirstSkillLevel() == 0)
			description.put(1, "Cost: " + String.valueOf(FireBall.MANA_COST).replaceAll("\\..*$", "") +  " mana (Intelligence Damage)");
		
		else
			description.put(1, "Cost: " + String.valueOf(FireBall.MANA_COST * getFirstSkillLevel()).replaceAll("\\..*$", "") +  " mana (Intelligence Damage)");
		
		if (getFirstSkillLevel() == 0)
			description.put(2, "Deals from " + fixDecimal(FireBall.MIN_DAMAGE) + " to " + fixDecimal(FireBall.MAX_DAMAGE) + " damage to an enemy.");
	
		else
			description.put(2, "Deals from " + fixDecimal(FireBall.MIN_DAMAGE * (getFirstSkillModifier() + getDamageModifier())) + " to " + 
					fixDecimal(FireBall.MAX_DAMAGE * (getFirstSkillModifier() + getDamageModifier())) + " damage to an enemy.");
		description.put(3, "More points will increase the damage");
		return description;
	}

	@Override
	public Map<Integer, String> secondSkillDescription()
	{
		Map<Integer, String> description = new HashMap<Integer, String>();
		description.put(0, "Icebolt (Level " + String.valueOf(getSecondSkillLevel()).replaceAll("\\..*$", "") + ")");
		
		if (getSecondSkillLevel() == 0)
			description.put(1, "Cost: " + String.valueOf(IceBolt.MANA_COST).replaceAll("\\..*$", "") + " mana (Intelligence Damage)");

		else
			description.put(1, "Cost: " + String.valueOf(IceBolt.MANA_COST * getSecondSkillLevel()).replaceAll("\\..*$", "") + " mana (Intelligence Damage)");
		
		if (getSecondSkillLevel() == 0)
			description.put(2, "Deals from " + fixDecimal(IceBolt.MIN_DAMAGE) + " to " + fixDecimal(IceBolt.MAX_DAMAGE) +
				" damage to an enemy and \nsnares the enemy.");
		
		else
			description.put(2, "Deals from " + fixDecimal(IceBolt.MIN_DAMAGE * (getSecondSkillLevel() + getDamageModifier())) + " to " + 
					fixDecimal(IceBolt.MAX_DAMAGE * (getSecondSkillLevel() + getDamageModifier())) + " damage to an enemy" + "\n"
											+ "and snares the enemy for " + String.valueOf((getSecondSkillModifier() / 1000.0f)).replaceAll("\\..*$", "")  + " seconds.");
		description.put(3, "More points will increase the damage and the snare duration");
		return description;
	}

	@Override
	public Map<Integer, String> thirdSkillDescription()
	{
		Map<Integer, String> description = new HashMap<Integer, String>();
		description.put(0, "Blink (Level " + String.valueOf(getThirdSkillLevel()).replaceAll("\\..*$", "") + ")");
		
		if (getThirdSkillLevel() == 0)
			description.put(1, "Cost: " + String.valueOf(BLINK_COST).replaceAll("\\..*$", "") + " mana (Intelligence Damage)");

		else
			description.put(1, "Cost: " + String.valueOf(BLINK_COST * getThirdSkillLevel()).replaceAll("\\..*$", "") + " mana (Intelligence Damage)");
		
		description.put(2, "Teleports the caster.");
		description.put(3, "More points will reduce the cooldown.");
		return description;
	}

	@Override
	public Map<Integer, String> ultimateSkillDescription()
	{
		Map<Integer, String> description = new HashMap<Integer, String>();
		description.put(0, "Thurder Storm (Level " + String.valueOf(getUltimateSkillLevel()).replaceAll("\\..*$", "") + ")");
		
		if (getUltimateSkillLevel() == 0)
			description.put(1, "Cost: " + String.valueOf(Storm.MANA_COST).replaceAll("\\..*$", "") + " mana (Intelligence Damage)");
		
		else
			description.put(1, "Cost: " + String.valueOf(Storm.MANA_COST * getUltimateSkillLevel()).replaceAll("\\..*$", "") + " mana (Intelligence Damage)");
		
		if (getUltimateSkillLevel() == 0)
			description.put(2, "Casts a storm of thunders in an area of " + (Storm.RADIUS / 10) + " yards." + "\n" + 
					"Each thunder deals from " + fixDecimal(Storm.MIN_DAMAGE) + " to " + fixDecimal(Storm.MAX_DAMAGE)  + " damage.");
		
		else
			description.put(2, "Casts a storm of thunders in an area of " + (Storm.RADIUS / 10) + " yards." + "\n" + 
								"Each thunder deals from " + fixDecimal(Storm.MIN_DAMAGE * getDamageModifier()) + " to " + 
								fixDecimal(Storm.MAX_DAMAGE * getDamageModifier()) + " damage.");
		
		if (getUltimateSkillLevel() == 0)
			description.put(3, "Requires knowledge level 5.");
		
		else
			description.put(3, "More points will decrease the life requirement.");
		
		return description;
	}
	
	@Override
	public void setVisible(boolean visible)
	{
		super.setVisible(visible);
		
		if (manaShield != null)
			manaShield.setVisible(visible);
	}
	
	@Override
	public void clear()
	{
		super.clear();
		
		if (wand != null)
			wand.clear();
		
		if (fireBall != null)
			fireBall.clear();
		
		if (iceBolt != null)
			iceBolt.clear();
		
		Iterator<Storm> stormIterator = storms.iterator();
		while (stormIterator.hasNext())
		{
			Storm storm = stormIterator.next();
			storm.destroy();
			stormIterator.remove();
		}
	}
	
	@Override
	public float getDamageReduced()
	{
		return DAMAGE_REDUCED;
	}
	
	@Override
	public void update(int delta)
	{
		super.update(delta);
		currentCd += delta;
		currentWandTime += delta;
		currentManaShieldTime += delta;
		currentFireballTime += delta;
		currentIceboltTime += delta;
		currentBlinkTime += delta;
		currentStormTime += delta;
		
		if (hasBlinked && currentBlinkTime > 500.0f)
			hasBlinked = false;
		
		if (wand != null)
		{
			wand.update(delta);
			if (currentWandTime > Wand.COOLDOWN)
			{
				wand.clear();
				wand = null;
			}
		}
		
		if (manaShield != null)
		{
			manaShield.update(delta);
			if(currentManaShieldTime > ManaShield.DURATION || !hasManaShield())
			{
				manaShield.clear();
				getPlayer().setManaShield(false);
				manaShield = null;
			}
		}
		
		if (fireBall != null)
		{
			fireBall.update(delta);
			if (currentFireballTime > FireBall.COOLDOWN)
			{
				fireBall.clear();
				fireBall = null;
			}
		}
		
		if (iceBolt != null)
		{
			iceBolt.update(delta);
			if(currentIceboltTime > IceBolt.COOLDOWN)
			{
				iceBolt.clear();
				iceBolt = null;
			}
		}
		
		if (isStorming && currentStormTime % (Storm.TICK_TIME - ((getUltimateSkillLevel()  - 1) * STORM_TIME_MODIFIER)) >= 0 && 
				currentStormTime % (Storm.TICK_TIME - ((getUltimateSkillLevel()  - 1) * STORM_TIME_MODIFIER)) < TheKnowledgeTowers.UPDATE_RATE)
		{
			Storm storm = new Storm(getPlayer().x(), getPlayer().y(), getPlayer(), getDamageModifier());
			getPlayer().getOffensiveSpells().add(storm);
			Random r = new Random();
			int sign = r.nextBoolean() ? 1 : -1;
			storm.getBody().setTransform(castPoint.add(new Vec2(r.nextInt(Storm.RADIUS / 2) * sign / GameMap.PTM_RATIO, 
					r.nextInt(Storm.RADIUS / 2) * sign / GameMap.PTM_RATIO)), 0.0f);
			storm.setLastDirection(Direction.DEFAULT);
			storm.getSprite().setLastDirection(Direction.DEFAULT);
			storms.add(storm);
		}
		
		for (Storm storm : storms)
		{
			if (storm != null)
				storm.update(delta);
		}
		
		if (currentStormTime > Storm.DURATION + (STORM_DURATION_MODIFIER * (getJobLevel() - 1)))
			isStorming = false;

		if (storms.size() > 0 && isStorming && currentStormTime > Storm.DURATION + (STORM_DURATION_MODIFIER * (getJobLevel() - 1))
				+ (Storm.TICK_TIME - ((getUltimateSkillLevel() - 1) * STORM_TIME_MODIFIER)))
		{
			for (Storm storm : new LinkedList<Storm>(storms))
			{
				storms.remove(storm);
				storm.destroy();
				storm = null;
			}
		}
	}
}
