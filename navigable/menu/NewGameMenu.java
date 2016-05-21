package saga.progetto.tesi.navigable.menu;

import playn.core.AssetWatcher;
import pythagoras.f.IPoint;
import saga.progetto.tesi.core.TheKnowledgeTowers;
import saga.progetto.tesi.entity.dynamicentity.Player;

public abstract class NewGameMenu extends Menu
{
	private Player player;
	private boolean canEscape;
	private boolean isActive;

	public NewGameMenu(TheKnowledgeTowers game, Player player)
	{
		super(game);
		this.setPlayer(player);
		canEscape = true;
	}
	
	public static void loadAssets(AssetWatcher watcher)
	{
		Inventory.loadAssets(watcher);
	}
	
	public Player getPlayer()
	{
		return player;
	}

	public void setPlayer(Player player)
	{
		this.player = player;
	}

	public boolean canEscape()
	{
		return canEscape;
	}

	public void setCanEscape(boolean canEscape)
	{
		this.canEscape = canEscape;
	}

	public boolean isActive()
	{
		return isActive;
	}

	public void setActive(boolean isActive)
	{
		this.isActive = isActive;
	}

	public abstract boolean visible();

	public abstract boolean intersectsWindow(IPoint p);
	
	public abstract boolean intersectsSwitch(IPoint p);
	
	public abstract boolean intersectsClose(IPoint p);
}
