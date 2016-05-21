package saga.progetto.tesi.job;

import static playn.core.PlayN.assets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import playn.core.AssetWatcher;
import playn.core.Image;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import saga.progetto.tesi.core.TheKnowledgeTowers;
import saga.progetto.tesi.entity.dynamicentity.Direction;
import saga.progetto.tesi.entity.dynamicentity.DynamicEntity;
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.entity.dynamicentity.enemy.Enemy;
import saga.progetto.tesi.entity.dynamicentity.equip.PhysicsWeapon;
import saga.progetto.tesi.entity.dynamicentity.pet.Bear;
import saga.progetto.tesi.entity.dynamicentity.spell.CircleOfHealing;
import saga.progetto.tesi.entity.dynamicentity.spell.Cyclone;
import saga.progetto.tesi.entity.dynamicentity.spell.Earthquake;
import saga.progetto.tesi.entity.dynamicentity.spell.Swarm;

public class Druid extends Job
{
	private static final String PLAYER_PATH = "images/characters/player/druid/druid.png";
	private static final String LEFT_CLICK_PATH = "images/spells/swarm_icon.png";
	private static final String RIGHT_CLICK_PATH = "images/spells/hawk_icon.png";
	private static final String FIRST_SPELL_PATH = "images/spells/summon_icon.png";
	private static final String SECOND_SPELL_PATH = "images/spells/cyclone_icon.png";
	private static final String THIRD_SPELL_PATH = "images/spells/circleofhealing_icon.png";
	private static final String ULTIMATE_SPELL_PATH = "images/spells/earthquake_icon.png";
	private static final IDimension SIZE = new Dimension(32.0f, 47.0f);
	private static final float GLOBAL_COOLDOWN = 1000.0f;
	private static final float BECKON_COOLDOWN = 5000.0f;
	private static final float BECKON_COST = 5.0f;
	private static final float SUMMON_COOLDOWN = 180000.0f;
	private static final float SUMMON_COST = 50.0f;
	private static final float STR_MODIFIER =  1.5f;
	private static final float DEX_MODIFIER =  1.0f;
	private static final float INT_MODIFIER =  1.5f;
	private static final float DAMAGE_REDUCED = 0.0f;
	private static final float SCORE_RECORD_REQUIRED = 90000;
	private static Image characterImage;
	private static Image leftClickImage;
	private static Image rightClickImage;
	private static Image firstSpellImage;
	private static Image secondSpellImage;
	private static Image thirdSpellImage;
	private static Image ultimateSpellImage;

	private List<Swarm> swarms;
	private Cyclone cyclone;
	private CircleOfHealing heal;
	private Earthquake earthquake;
	private float currentGlobalCD = GLOBAL_COOLDOWN;
	private float currentSwarmTime = Swarm.COOLDOWN;
	private float currentBeckonTime = BECKON_COOLDOWN;
	private float currentSummonTime = SUMMON_COOLDOWN;
	private float currentCycloneTime = Cyclone.COOLDOWN;
	private float currentHealingTime = CircleOfHealing.COOLDOWN;
	private float currentEarthquakeTime = Earthquake.COOLDOWN;

	public Druid(float x, float y, Player player)
	{
		super(player);
		initSpellIcons();
		swarms = new LinkedList<Swarm>();
		setEarthquakeDistance(Earthquake.DISTANCE);
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
		return SCORE_RECORD_REQUIRED;
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
		if (getPlayer().getCurrentMana() > Swarm.MANA_COST && currentSwarmTime > Swarm.COOLDOWN && currentGlobalCD > GLOBAL_COOLDOWN)
		{
			Swarm swarm = new Swarm(getPlayer().x(), getPlayer().y(), getPlayer(), getDamageModifier());
			getPlayer().getOffensiveSpells().add(swarm);
			swarm.setLastDirection(getPlayer().getFacingDirection());
			swarm.bodyTransform(getPlayer(), getPlayer().getFacingDirection());
			getPlayer().setCurrentMana(getPlayer().getCurrentMana() - Swarm.MANA_COST);
			swarms.add(swarm);
			currentSwarmTime = 0.0f;
		}
	}

	@Override
	public void rightClick()
	{
		super.rightClick();
		if (getPlayer().getCurrentMana() > BECKON_COST && currentBeckonTime > BECKON_COOLDOWN && 
				currentGlobalCD > GLOBAL_COOLDOWN && getPlayer().getPet() != null)
		{
			getPlayer().getPet().teleportBody(getPlayer().x(), getPlayer().y());
			getPlayer().setCurrentMana(getPlayer().getCurrentMana() - BECKON_COST);
			currentBeckonTime = 0.0f;
		}
	}

	@Override
	public void firstSkill()
	{
		super.firstSkill();
		
		if (getPlayer().getCurrentMana() >= SUMMON_COST && currentSummonTime > SUMMON_COOLDOWN && currentGlobalCD > GLOBAL_COOLDOWN &&
				getFirstSkillLevel() > 0)
		{
			if (getPlayer().getPet() != null)
				getPlayer().getPet().clear();
			
			getPlayer().setPet(new Bear(getPlayer(), getStrengthModifier(), getFirstSkillModifier()));
			getPlayer().setCurrentMana(getPlayer().getCurrentMana() - SUMMON_COST);
			currentSummonTime = 0.0f;
			setPetUp(true);
		}
	}

	@Override
	public void secondSkill()
	{
		super.secondSkill();
		
		if (currentCycloneTime >= Cyclone.COOLDOWN && getPlayer().getCurrentMana() >= Cyclone.MANA_COST &&
				getSecondSkillLevel() > 0)
		{
			cyclone = new Cyclone(getPlayer().x(), getPlayer().y(), getPlayer(), getSecondSkillModifier());
			getPlayer().getOffensiveSpells().add(cyclone);
			cyclone.setLastDirection(Direction.DEFAULT);
			cyclone.bodyTransform(getPlayer(), Direction.DEFAULT);
			cyclone.getSprite().setLastDirection(Direction.DEFAULT);
			getPlayer().setCurrentMana(getPlayer().getCurrentMana() - Cyclone.MANA_COST);
			
			for (Swarm swarm : swarms)
				swarm.clear();
			
			currentCycloneTime = 0.0f;
		}
	}
	
	@Override
	public void thirdSkill()
	{
		super.thirdSkill();
		
		if (currentHealingTime >= CircleOfHealing.COOLDOWN && getPlayer().getCurrentMana() >= CircleOfHealing.MANA_COST &&
				getThirdSkillLevel() > 0)
		{
			heal = new CircleOfHealing(getPlayer().x(), getPlayer().y(), getPlayer(), getThirdSkillModifier());
			getPlayer().getDefensiveSpells().add(heal);
			heal.setLastDirection(Direction.DEFAULT);
			heal.bodyTransform(getPlayer(), Direction.DEFAULT);
			heal.getSprite().setLastDirection(Direction.DEFAULT);
			getPlayer().setCurrentMana(getPlayer().getCurrentMana() - CircleOfHealing.MANA_COST);
			currentHealingTime = 0.0f;
		}
	}
	
	@Override
	public void ultimateSkill()
	{
		super.ultimateSkill();
		
		if (getUltimateSkillLevel() > 0 && currentEarthquakeTime >= Earthquake.COOLDOWN && getPlayer().getCurrentMana() >= Earthquake.COST)
		{
			getPlayer().setEarthQuaking(true);
			earthquake = new Earthquake(getUltimateSkillModifier() + getDamageModifier());
			currentEarthquakeTime = 0.0f;
		}
	}
	
	public void earthQuakeDamage()
	{
		getPlayer().takeDamage(earthquake.getDamage() / 2);
		
		for (Enemy enemy : getMap().getEnemies())
		{
			if (inEarthquakeRange(enemy))
			{
				getPlayer().resetOutOfCombat();
				enemy.takeDamage(earthquake.getDamage());
			}
		}
		
		if (getPlayer().getPet() != null && inEarthquakeRange(getPlayer().getPet()))
			getPlayer().getPet().takeDamage(earthquake.getDamage() / 2);
	}
	
	private boolean inEarthquakeRange(DynamicEntity entity)
	{
		if (Math.abs(getPlayer().x() - entity.x()) < Earthquake.RADIUS && Math.abs(getPlayer().y() - entity.y()) < Earthquake.RADIUS)
			return true;
		
		return false;
	}

	@Override
	public float rightClickCD()
	{
		if (BECKON_COOLDOWN - 100.0f > currentBeckonTime)
			return BECKON_COOLDOWN - currentBeckonTime;
		return 0.0f;
	}
	
	@Override
	public float firstSkillCD()
	{
		if (SUMMON_COOLDOWN - 100.0f > currentSummonTime)
			return SUMMON_COOLDOWN - currentSummonTime;
		return 0.0f;
	}
	
	@Override
	public float secondSkillCD()
	{
		if (Cyclone.COOLDOWN - 100.0f > currentCycloneTime)
			return Cyclone.COOLDOWN - currentCycloneTime;
		return 0.0f;
	}
	
	@Override
	public float thirdSkillCD()
	{
		if (CircleOfHealing.COOLDOWN - 100.0f > currentHealingTime)
			return CircleOfHealing.COOLDOWN - currentHealingTime;
		return 0.0f;
	}
	
	@Override
	public float ultimateSkillCD()
	{
		if (Earthquake.COOLDOWN - 100.0f > currentEarthquakeTime)
			return Earthquake.COOLDOWN - currentEarthquakeTime;
		return 0.0f;
	}

	@Override
	public boolean canLeftClick()
	{
		return getPlayer().getCurrentMana() >= Swarm.MANA_COST  && !getPlayer().isSilenced() &&
				!getPlayer().isCrowdControlled() && getPlayer().isInsideTower();
	}
	
	@Override
	public boolean canRightClick()
	{
		return rightClickCD() == 0 && getPlayer().getCurrentMana() > BECKON_COST &&
				!getPlayer().isSilenced() && !getPlayer().isCrowdControlled() && getPlayer().isInsideTower();
	}

	@Override
	public boolean canFirstSkill()
	{
		return firstSkillCD() == 0 && !getPlayer().isSilenced() && getPlayer().getCurrentMana() >= SUMMON_COST &&
				!getPlayer().isCrowdControlled() && getPlayer().isInsideTower() && getFirstSkillLevel() > 0;
	}

	@Override
	public boolean canSecondSkill()
	{
		return secondSkillCD() == 0 && !getPlayer().isSilenced() && getPlayer().getCurrentMana() >= Cyclone.MANA_COST &&
				!getPlayer().isCrowdControlled() && getPlayer().isInsideTower() && getSecondSkillLevel() > 0;
	}

	@Override
	public boolean canThirdSkill()
	{
		return thirdSkillCD() == 0 && !getPlayer().isSilenced() && getPlayer().getCurrentMana() >= CircleOfHealing.MANA_COST && 
				!getPlayer().isCrowdControlled() && getPlayer().isInsideTower() && getThirdSkillLevel() > 0;
	}
	
	@Override
	public boolean canUltimateSkill()
	{
		return ultimateSkillCD() == 0 && getPlayer().getCurrentMana() >= Earthquake.COST && !getPlayer().isCrowdControlled()
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
		return (getSecondSkillLevel() - 1);
	}

	@Override
	public float getThirdSkillModifier()
	{
		return getThirdSkillLevel() + getDamageModifier();
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
	public PhysicsWeapon getWeapon()
	{
		return null;
	}
	
	@Override
	public float getDamageReduced()
	{
		return DAMAGE_REDUCED;
	}
	
	@Override
	public String toString()
	{
		return "DRUID";
	}
	
	@Override
	public Map<Integer, String> leftClickDescription()
	{
		Map<Integer, String> description = new HashMap<Integer, String>();
		description.put(0, "Swarm");
		description.put(1, "Cost: " + String.valueOf(Swarm.MANA_COST).replaceAll("\\..*$", "") +  " mana (Intelligence Damage)");
		description.put(2, "Deals from " + fixDecimal(Swarm.MIN_DAMAGE * getDamageModifier() * Swarm.DURATION / TheKnowledgeTowers.UPDATE_RATE) +
				" to " + fixDecimal(Swarm.MAX_DAMAGE * getDamageModifier() * Swarm.DURATION / TheKnowledgeTowers.UPDATE_RATE) + " damage to an enemy.");
		return description;
	}

	@Override
	public Map<Integer, String> rightClickDescription()
	{
		Map<Integer, String> description = new HashMap<Integer, String>();
		description.put(0, "Beckon");
		description.put(1, "Cost: " + String.valueOf(BECKON_COST).replaceAll("\\..*$", "") +  " mana ");
		description.put(2, "Recalls the pet to the caster.");
		return description;
	}

	@Override
	public Map<Integer, String> firstSkillDescription()
	{
		Map<Integer, String> description = new HashMap<Integer, String>();
		description.put(0, "Summon Bear (Level " + String.valueOf(getFirstSkillLevel()).replaceAll("\\..*$", "") + ")");
		description.put(1, "Cost: " + String.valueOf(SUMMON_COST).replaceAll("\\..*$", "") +  " mana (Life and Damage based on Strenght)");
		description.put(2, "Summons a bear to fight with the caster.");
		description.put(3, "More points will increase the life and the damage of the bear");
		return description;
	}

	@Override
	public Map<Integer, String> secondSkillDescription()
	{
		Map<Integer, String> description = new HashMap<Integer, String>();
		description.put(0, "Cyclone (Level " + String.valueOf(getSecondSkillLevel()).replaceAll("\\..*$", "") + ")");
		description.put(1, "Cost: " + String.valueOf(Cyclone.MANA_COST).replaceAll("\\..*$", "") + " mana");
		
		if (getSecondSkillLevel() == 0)
			description.put(2, "Tosses enemies in the air. Enemies are unable to move, \nattack and get incoming damage");
		else
			description.put(2, "Tosses enemies in the air for " + String.valueOf(Cyclone.BASE_CYCLONE_DURATION / 1000 + getSecondSkillModifier()).replaceAll("\\..*$", "") +
					" seconds. \nEnemies are unable to move, attack and take \nany incoming damage while under this effect.");
		
		description.put(3, "More points will increase the cyclone duration.");
		return description;
	}

	@Override
	public Map<Integer, String> thirdSkillDescription()
	{
		Map<Integer, String> description = new HashMap<Integer, String>();
		description.put(0, "Circle of Healing" + "(Level " + String.valueOf(getThirdSkillLevel()).replaceAll("\\..*$", "") + ")");
		description.put(1, "Cost: " + String.valueOf(CircleOfHealing.MANA_COST).replaceAll("\\..*$", "") + " mana");
		
		if (getThirdSkillLevel() == 0)
			description.put(2, "Heals the caster and all the friendly units within \na range of " + 
					String.valueOf(CircleOfHealing.RADIUS / 10).replaceAll("\\..*$", "") + " yards.");
		else
			description.put(2, "Heals the caster and all the friendly units for " + String.valueOf(CircleOfHealing.MIN_HEAL * getThirdSkillModifier()).replaceAll("\\..*$", "") + 
					"\nto " + String.valueOf(CircleOfHealing.MAX_HEAL * getThirdSkillModifier()).replaceAll("\\..*$", "") + " life within a range of " + 
					String.valueOf(CircleOfHealing.RADIUS / 10).replaceAll("\\..*$", "") + " yards.");
		
		description.put(3, "More points will increase the healing value.");
		return description;
	}

	@Override
	public Map<Integer, String> ultimateSkillDescription()
	{
		Map<Integer, String> description = new HashMap<Integer, String>();
		description.put(0, "Earthquake" + "(Level " + String.valueOf(getUltimateSkillLevel()).replaceAll("\\..*$", "") + ")");
		description.put(1, "Cost: " + String.valueOf(Earthquake.COST).replaceAll("\\..*$", "") + " mana (Intelligence Damage)");
		
		if (getUltimateSkillLevel() == 0)
			description.put(2, "The earth trembles and and hits everyone for " + fixDecimal(Earthquake.MIN_DAMAGE) +
				" to \n" + fixDecimal(Earthquake.MAX_DAMAGE) + " damage every 1 second" + " in a range of " + 
					String.valueOf(Earthquake.RADIUS / 10).replaceAll("\\..*$", "") + " yards.\nLasts " + 
						String.valueOf(Earthquake.DURATION / 1000).replaceAll("\\..*$", "") + " seconds.");
		else
			description.put(2, "The earth trembles and and hits everyone for " + fixDecimal(Earthquake.MIN_DAMAGE * (getUltimateSkillModifier() + getDamageModifier())) +
					" to \n" + fixDecimal(Earthquake.MAX_DAMAGE * (getUltimateSkillModifier() + getDamageModifier())) + " damage every 1 second" + " in a range of " + 
						String.valueOf(Earthquake.RADIUS / 10).replaceAll("\\..*$", "") + " yards.\nLasts " + 
							String.valueOf(Earthquake.DURATION / 1000).replaceAll("\\..*$", "") + " seconds.");
		
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
	public void clear()
	{
		super.clear();
		
		Iterator<Swarm> swarmIterator = swarms.iterator();
		while (swarmIterator.hasNext())
		{
			Swarm swarm = swarmIterator.next();
			swarm.clear();
			swarmIterator.remove();
		}
		
		if (cyclone != null)
			cyclone.clear();
		
		if (heal != null)
			heal.clear();
	}
	
	@Override
	public void update(int delta)
	{
		super.update(delta);

		currentGlobalCD += delta;
		currentSwarmTime += delta;
		currentBeckonTime += delta;
		currentSummonTime += delta;
		currentCycloneTime += delta;
		currentHealingTime += delta;
		currentEarthquakeTime += delta;
		
		for (Swarm swarm : swarms)
			if (swarm != null)
				swarm.update(delta);
		
		if (cyclone != null)
		{
			cyclone.update(delta);
			
			if (currentCycloneTime > Cyclone.PHYSICS)
				cyclone.setActive(false);
		}
		
		if (heal != null)
		{
			heal.update(delta);
			
			if (currentHealingTime > CircleOfHealing.CIRCLE_DURATION)
				heal.clear();
		}
		
		if (currentEarthquakeTime > Earthquake.DURATION)
		{
			getPlayer().setEarthQuaking(false);
			earthquake = null;
		}
		
		else if (currentEarthquakeTime % Earthquake.TICK_TIME >= 0 && currentEarthquakeTime % Earthquake.TICK_TIME < TheKnowledgeTowers.UPDATE_RATE)
			earthQuakeDamage();
	}
}