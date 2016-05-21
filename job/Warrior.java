package saga.progetto.tesi.job;

import static playn.core.PlayN.assets;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;
import playn.core.AssetWatcher;
import playn.core.Image;
import saga.progetto.tesi.entity.dynamicentity.Direction;
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.entity.dynamicentity.enemy.Enemy;
import saga.progetto.tesi.entity.dynamicentity.equip.Shield;
import saga.progetto.tesi.entity.dynamicentity.equip.Sword;
import saga.progetto.tesi.entity.dynamicentity.equip.PhysicsWeapon;
import saga.progetto.tesi.entity.dynamicentity.spell.Stun;
import saga.progetto.tesi.entity.dynamicentity.spell.Throw;
import saga.progetto.tesi.map.GameMap;
import saga.progetto.tesi.map.MapContactListener;

public class Warrior extends Job
{
	private static final String PLAYER_PATH = "images/characters/player/warrior/warrior_sheet";
	private static final String LEFT_CLICK_PATH = "images/spells/sword_icon.png";
	private static final String RIGHT_CLICK_PATH = "images/spells/shield_icon.png";
	private static final String FIRST_SPELL_PATH = "images/spells/throw_icon.png";
	private static final String SECOND_SPELL_PATH = "images/spells/stun_icon.png";
	private static final String THIRD_SPELL_PATH = "images/spells/purge_icon.png";
	private static final String ULTIMATE_SPELL_PATH = "images/spells/execute_icon.png";
	private static final float STR_MODIFIER =  2.0f;
	private static final float DEX_MODIFIER =  1.0f;
	private static final float INT_MODIFIER =  1.0f;
	private static final float PURGE_COOLDOWN = 120000.0f;
	private static final float PURGE_MODIFIER = 15000.0f;
	private static final float DAMAGE_REDUCED = 0.3f;
	private static final float EXECUTE_COOLDOWN = 20000.0f;
	private static final float EXECUTE_COST = 15.0f;
	public static final float EXECUTE_RATE = 0.20f;
	public static final float EXECUTE_MODIFIER = 0.10f;
	private static final float STUN_MODIFIER = 1000.0f;
	private static final float TOWERS_REQUIRED = 5;
	private static Image characterImage;
	private static Image leftClickImage;
	private static Image rightClickImage;
	private static Image firstSpellImage;
	private static Image secondSpellImage;
	private static Image thirdSpellImage;
	private static Image ultimateSpellImage;
	
	private PhysicsWeapon sword;
	private Shield shield;
	private Throw throwed;
	private Stun smash;
	private float currentShieldTime;
	private float currentThrowTime = Throw.COOLDOWN;
	private float currentSmashTime = Stun.COOLDOWN;
	private float currentPurgeTime = getThirdSkillModifier();
	private float currentExecuteTime = EXECUTE_COOLDOWN;

	public Warrior(float x, float y, Player player)
	{
		super(player);
		sword = new Sword(x, y - 16.0f, getMap());
		sword.setActive(false);
		shield = new Shield(x, y, getMap());
		shield.setLastDirection(player.getFacingDirection());
		shield.setActive(false);
		currentShieldTime = getStats().getShieldCooldown();
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
	
	// carica gli assets dell'oggetto
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
		meleeAttack();
	}

	@Override
	public void rightClick(boolean isMouseDown)
	{
		if (canDefend())
		{
			shield.setActive(isMouseDown);
			shield.setVisible(isMouseDown);
			
			if (isMouseDown)
				getPlayer().setSpeedModifier(getPlayer().getSpeedModifier() / 2);
			else
				getPlayer().setSpeedModifier(getPlayer().getSpeedModifier() * 2);
		}
	}

	@Override
	public void firstSkill()
	{
		super.firstSkill();
		
		if (getPlayer().getCurrentEndurance() >= Throw.ENDURANCE_COST && currentThrowTime > Throw.COOLDOWN && getFirstSkillLevel() > 0)
		{
			throwed = new Throw(getPlayer().x(), getPlayer().y(), getPlayer(), getFirstSkillModifier() + getDamageModifier());
			getPlayer().getOffensiveSpells().add(throwed);
			throwed.setLastDirection(getPlayer().getFacingDirection());
			throwed.bodyTransform(getPlayer(), getPlayer().getFacingDirection());
			throwed.getSprite().setLastDirection(getPlayer().getFacingDirection());
			getPlayer().setCurrentEndurance(getPlayer().getCurrentEndurance() - throwed.getCost());
			currentThrowTime = 0.0f;
		}
	}
	
	@Override
	public void secondSkill()
	{
		super.secondSkill();
		if (getPlayer().getCurrentEndurance() >= Stun.ENDURANCE_COST && currentSmashTime > Stun.COOLDOWN && getSecondSkillLevel() > 0)
		{
			smash = new Stun(getPlayer().x(), getPlayer().y(), getPlayer(), getDamageModifier(), getSecondSkillModifier());
			getPlayer().getOffensiveSpells().add(smash);
			smash.setLastDirection(getPlayer().getFacingDirection());
			smash.bodyTransform(getPlayer(), getPlayer().getFacingDirection());
			smash.getSprite().setLastDirection(Direction.DEFAULT);
			getPlayer().setCurrentEndurance(getPlayer().getCurrentEndurance() - smash.getCost());
			currentSmashTime = 0.0f;
			shield.setVisible(true);
		}
	}
	
	@Override
	public void thirdSkill()
	{
		super.thirdSkill();
		if (currentPurgeTime >= getThirdSkillModifier() && getThirdSkillLevel() > 0)
		{
			currentPurgeTime = 0.0f;
			getPlayer().setSleeping(false);
			getPlayer().setPoisoned(false);
			getPlayer().setFeared(false);
			getPlayer().setFrozen(false);
			getPlayer().setDisarmed(false);
			getPlayer().setSilenced(false);
		}
	}
	
	@Override
	public void ultimateSkill()
	{
		super.ultimateSkill();
		
		if (getCurrentSwing() == 0 && getPlayer().getCurrentEndurance() - EXECUTE_COST >= 0 && currentExecuteTime > EXECUTE_COOLDOWN
				&& getUltimateSkillLevel() > 0)
		{
			currentExecuteTime = 0.0f;
			setExecute(true);
			meleeAttack();
		}
	}
	
	@Override
	public float rightClickCD()
	{
		if (getStats().getShieldCooldown() - 100.0f > currentShieldTime)
			return getStats().getShieldCooldown() - currentShieldTime;
		return 0.0f;
	}
	
	@Override
	public float firstSkillCD()
	{
		if (Throw.COOLDOWN - 100.0f > currentThrowTime)
			return Throw.COOLDOWN - currentThrowTime;
		return 0.0f;
	}
	
	@Override
	public float secondSkillCD()
	{
		if (Stun.COOLDOWN - 100.0f > currentSmashTime)
			return Stun.COOLDOWN - currentSmashTime;
		return 0.0f;
	}
	
	@Override
	public float thirdSkillCD()
	{
		if (getThirdSkillModifier() - 100.0f > currentPurgeTime)
			return getThirdSkillModifier() - currentPurgeTime;
		return 0.0f;
	}
	
	@Override
	public float ultimateSkillCD()
	{
		if (EXECUTE_COOLDOWN - 100.0f > currentExecuteTime)
			return EXECUTE_COOLDOWN - currentExecuteTime;
		return 0.0f;
	}

	public Shield getShield()
	{
		return shield;
	}

	public void setShield(Shield shield)
	{
		this.shield = shield;
	}
	
	public Body getShieldBody()
	{
		return shield.getBody();
	}
	
	public void setShieldCD(float shieldCD)
	{
		this.currentShieldTime = shieldCD;
	}

	@Override
	public boolean canLeftClick()
	{
		return  getPlayer().getCurrentEndurance() >= getWeapon().getCost() && 
				!getPlayer().isDisarmed() && !getPlayer().isCrowdControlled() && getPlayer().isInsideTower();
	}
	
	@Override
	public boolean canRightClick()
	{
		return rightClickCD() == 0 && !getPlayer().isCrowdControlled() && canDefend() &&
				!getPlayer().isCrowdControlled() && getPlayer().isInsideTower();
	}

	@Override
	public boolean canFirstSkill()
	{
		return firstSkillCD() == 0 && getPlayer().getCurrentEndurance() >= Throw.ENDURANCE_COST && !getPlayer().isCrowdControlled() 
				&& getFirstSkillLevel() > 0 && !getPlayer().isDisarmed() && getPlayer().isInsideTower();
	}

	@Override
	public boolean canSecondSkill()
	{
		return secondSkillCD() == 0 && getPlayer().getCurrentEndurance() >= Stun.ENDURANCE_COST && !getPlayer().isCrowdControlled() 
				&& getSecondSkillLevel() > 0 && !getPlayer().isDisarmed() && getPlayer().isInsideTower();
	}

	@Override
	public boolean canThirdSkill()
	{
		return thirdSkillCD() == 0 && getPlayer().isInsideTower() && getThirdSkillLevel() > 0;
	}
	
	@Override
	public boolean canUltimateSkill()
	{
		return ultimateSkillCD() == 0 && getPlayer().getCurrentEndurance() >= EXECUTE_COST && !getPlayer().isDisarmed() 
				&& getUltimateSkillLevel() > 0 && !getPlayer().isCrowdControlled() && getPlayer().isInsideTower();
	}
	
	@Override
	public float getFirstSkillModifier()
	{
		return getFirstSkillLevel();
	}

	@Override
	public float getSecondSkillModifier()
	{
		return (getSecondSkillLevel() - 1) * STUN_MODIFIER;
	}

	@Override
	public float getThirdSkillModifier()
	{
		return PURGE_COOLDOWN - (getThirdSkillLevel() - 1) * PURGE_MODIFIER;
	}

	@Override
	public float getUltimateSkillModifier()
	{
		return EXECUTE_RATE + (getUltimateSkillLevel() - 1) * EXECUTE_MODIFIER;
	}
	
	@Override
	public float getDamageModifier()
	{
		if (getPlayer().getStrength() / 10 < 1.0f)
			return 1.0f;
		
		return getPlayer().getStrength() / 10;
	}
	
	@Override
	public void setVisible(boolean visible)
	{
		super.setVisible(visible);
		
		if (!visible)
			shield.setVisible(false);
	}
	
	@Override
	public boolean canDefend()
	{
		return currentShieldTime >= getStats().getShieldCooldown();
	}
	
	@Override
	public boolean isDefending()
	{
		return shield.isActive();
	}
	
	@Override
	public void setDefending(boolean isDefending)
	{
		shield.setActive(isDefending);
	}
	
	public void shieldDown()
	{
		currentShieldTime = 0.0f;
		shield.setActive(false);
		shield.setVisible(false);
	}
	
	@Override
	public boolean shieldCollision(MapContactListener contactListener, Enemy enemy)
	{
		return contactListener.isColliding(enemy.getWeaponBody(), shield.getBody());
	}
	
	@Override
	public void setLastDirection(Direction lastDirection)
	{
		super.setLastDirection(lastDirection);
		shield.setLastDirection(lastDirection);
	}
	
	public PhysicsWeapon getWeapon()
	{
		return sword;
	}
	
	@Override
	public void setNewMap(float x, float y, World world, GameMap map)
	{
		super.setNewMap(x, y, world, map);
		shield.clear();
		shield = new Shield(x, y, map);
		shield.setMap(map);
		shield.setWorld(world);
		shield.setActive(false);
		shield.setVisible(false);
	}
	
	@Override
	public float getDamageReduced()
	{
		return DAMAGE_REDUCED;
	}
	
	@Override
	public String toString()
	{
		return "WARRIOR";
	}
	
	@Override
	public Map<Integer, String> leftClickDescription()
	{
		Map<Integer, String> description = new HashMap<Integer, String>();
		description.put(0, "Heroic Strike");
		description.put(1, "(Strength Damage)");
		description.put(2, "Deals from " + fixDecimal(getWeapon().getMinDmg() * getDamageModifier()) + " to " 
								+ fixDecimal(getWeapon().getMaxDmg() * getDamageModifier())  + " damage to an enemy.");
		return description;
	}

	@Override
	public Map<Integer, String> rightClickDescription()
	{
		Map<Integer, String> description = new HashMap<Integer, String>();
		description.put(0, "Block");
		description.put(2, "The warrior blocks incoming attacks with his \nshield losing endurance instead of life.");
		return description;
	}

	@Override
	public Map<Integer, String> firstSkillDescription()
	{
		Map<Integer, String> description = new HashMap<Integer, String>();
		description.put(0, "Throw (Level " + String.valueOf(getFirstSkillLevel()).replaceAll("\\..*$", "") + ")");
		description.put(1, "Cost: " + String.valueOf(Throw.ENDURANCE_COST).replaceAll("\\..*$", "") + " endurance (Strength damage)");
		if (getFirstSkillLevel() == 0)
			description.put(2, "Throws a spear against an enemy and deals some \ndamage.");
		
		else
			description.put(2, "Throws a spear against an enemy and deals from \n" + 
					fixDecimal(Throw.MIN_DAMAGE * (getFirstSkillModifier() + getDamageModifier())) + " to " +
					fixDecimal(Throw.MAX_DAMAGE * (getFirstSkillModifier() + getDamageModifier())) + " damage.");
		
		description.put(3, "More points will increase the damage.");
		return description;
	}

	@Override
	public Map<Integer, String> secondSkillDescription()
	{
		Map<Integer, String> description = new HashMap<Integer, String>();
		description.put(0, "Smash (Level " + String.valueOf(getSecondSkillLevel()).replaceAll("\\..*$", "") + ")");
		description.put(1, "Cost: " + String.valueOf(Stun.ENDURANCE_COST).replaceAll("\\..*$", "") + " endurance (Strength damage)");
		
		if (getSecondSkillLevel() == 0)
			description.put(2, "Smashes the target with the shield, stunning it \nand dealing some damage.");
		else
			description.put(2, "Smashes the target with the shield, stunning it \nfor " + 
						String.valueOf((Stun.STUN_VALUE + getSecondSkillModifier()) / 1000.0f).replaceAll("\\..*$", "") + " seconds and dealing from " +
						fixDecimal(Stun.MIN_DAMAGE * getDamageModifier()) + " to " +
						fixDecimal(Stun.MAX_DAMAGE * getDamageModifier()) + " damage.");
		
		description.put(3, "More points will increase the stun duration");
		return description;
	}

	@Override
	public Map<Integer, String> thirdSkillDescription()
	{
		Map<Integer, String> description = new HashMap<Integer, String>();
		description.put(0, "Purge (Level " + String.valueOf(getThirdSkillLevel()).replaceAll("\\..*$", "") + ")");
		description.put(2, "Removes all crowd control effects.");
		description.put(3, "More points will reduce the cooldown.");
		return description;
	}

	@Override
	public Map<Integer, String> ultimateSkillDescription()
	{
		Map<Integer, String> description = new HashMap<Integer, String>();
		description.put(0, "Execute (Level " + String.valueOf(getUltimateSkillLevel()).replaceAll("\\..*$", "") + ")");
		description.put(1, "Cost: " + String.valueOf(EXECUTE_COST).replaceAll("\\..*$", "") + " endurance (Strength damage)");
		
		if (getThirdSkillLevel() == 0)
			description.put(2, "Attacks a target with low life to istantly kill it.");
		
		else
			description.put(2, "Attacks a target with less than " + String.valueOf(getUltimateSkillModifier() * 100).replaceAll("\\..*$", "") + 
					"% life \nto istantly kill it.");
		
		if (getUltimateSkillLevel() == 0)
			description.put(3, "Requires knowledge level 5.");
		
		else
			description.put(3, "More points will decrease the life requirement.");
		
		return description;
	}
	
	@Override
	public void clear()
	{
		super.clear();
		shield.clear();
	}
	
	@Override
	public void update(int delta)
	{
		super.update(delta);
		currentThrowTime += delta;
		currentSmashTime += delta;
		currentPurgeTime += delta;
		currentShieldTime += delta;
		currentExecuteTime += delta;
		
		if (isDefending())
		{
			if (getPlayer().getCurrentEndurance() == 0)
				shieldDown();
			
			if (!shield.visible())
				shield.setVisible(true);
		}
		
		else
			shield.setVisible(false);
		
		shield.bodyTransform(getPlayer(), getPlayer().getFacingDirection());
		shield.update(delta);
		
		if (throwed != null)
		{
			throwed.update(delta);
			if (currentThrowTime > Throw.COOLDOWN)
			{
				throwed.clear();
				throwed = null;
			}
		}
		
		if (smash != null)
		{
			smash.update(delta);
			
			if (currentSmashTime > Stun.COOLDOWN)
			{
				smash.clear();
				smash = null;
			}
			
			if (currentSmashTime > Stun.DURATION && !isDefending())
				shield.setVisible(false);
		}
		
		if (currentExecuteTime >= getWeapon().getCooldown())
			setExecute(false);
	}
}
