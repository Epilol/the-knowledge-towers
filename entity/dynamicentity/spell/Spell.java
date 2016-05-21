package saga.progetto.tesi.entity.dynamicentity.spell;

import java.util.Random;
import playn.core.AssetWatcher;
import pythagoras.f.IDimension;
import saga.progetto.tesi.entity.Offensive;
import saga.progetto.tesi.entity.dynamicentity.Direction;
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.entity.dynamicentity.SingleRenderedDynamicEntity;
import saga.progetto.tesi.entity.dynamicentity.enemy.Enemy;

// TODO fare interfaccia per spell e armi gg
public abstract class Spell extends SingleRenderedDynamicEntity implements Offensive
{
	private float minDmg;
	private float maxDmg;
	private Player player;
	private boolean isPerforating;
	private boolean removeText;
	private boolean cannotMiss;
	
	public Spell(float x, float y, IDimension size, Player player)
	{
		super(x, y, size, player.getMap());
		this.player = player;
	}
	
	public static void loadAssets(AssetWatcher watcher)
	{
		Wand.loadAssets(watcher);
		ManaShield.loadAssets(watcher);
		FireBall.loadAssets(watcher);
		IceBolt.loadAssets(watcher);
		Storm.loadAssets(watcher);
		Fear.loadAssets(watcher);
		Throw.loadAssets(watcher);
		Stun.loadAssets(watcher);
		Arrow.loadAssets(watcher);
		AimShot.loadAssets(watcher);
		SilenceArrow.loadAssets(watcher);
		EndTowerArea.loadAssets(watcher);
		Swarm.loadAssets(watcher);
		Cyclone.loadAssets(watcher);
		CircleOfHealing.loadAssets(watcher);
		LevelUp.loadAssets(watcher);
	}
	
	public float getMinDmg()
	{
		return minDmg;
	}

	public void setMinDmg(float minDmg)
	{
		this.minDmg = minDmg;
	}

	public float getMaxDmg()
	{
		return maxDmg;
	}

	public void setMaxDmg(float maxDmg)
	{
		this.maxDmg = maxDmg;
	}
	
	public float getDamage()
	{
		return minDmg + (maxDmg - minDmg) * (float)(new Random().nextDouble());
	}
	
	public float getSpellDamage()
	{
		return new Random().nextInt(100) > getCriticalChance() * 100 ? getDamage() : getDamage() * 2;
	}
	
	public Player getPlayer()
	{
		return player;
	}


	public boolean cannotMiss()
	{
		return cannotMiss;
	}

	public void setCannotMiss(boolean cannotMiss)
	{
		this.cannotMiss = cannotMiss;
	}
	
	public abstract void applyEffect();
	
	public abstract void applyEffect(Enemy enemy);
	
	public boolean isPerforating()
	{
		return isPerforating;
	}

	public void setPerforating(boolean isPerforating)
	{
		this.isPerforating = isPerforating;
	}
	
	public boolean removeText()
	{
		return removeText;
	}

	public void setRemoveText(boolean removeText)
	{
		this.removeText = removeText;
	}

	@Override
	public void setLastDirection(Direction lastDirection)
	{
		super.setLastDirection(lastDirection);
	}
	
	@Override
	public void clear()
	{
		super.clear();
		getPlayer().getOffensiveSpells().remove(this);
		getPlayer().getDefensiveSpells().remove(this);
	}
	
	public boolean destroyOnCollision()
	{
		return true;
	}
}
