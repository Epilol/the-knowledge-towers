package saga.progetto.tesi.navigable.menu;

import playn.core.AssetWatcher;
import playn.core.ImageLayer;
import playn.core.Keyboard.Event;
import playn.core.Mouse.ButtonEvent;
import pythagoras.f.IPoint;
import saga.progetto.tesi.core.TheKnowledgeTowers;
import saga.progetto.tesi.navigable.Navigable;

public abstract class Menu implements Navigable
{
	private ImageLayer bgLayer;
	private TheKnowledgeTowers game;
	private Navigable previousMenu;
	private Navigable nextMenu;
	
	public Menu(TheKnowledgeTowers game)
	{
		this.game = game;
	}

	public static void loadAssets(AssetWatcher watcher)
	{
		IndexMenu.loadAssets(watcher);
		CreationMenu.loadAssets(watcher);
		NewGameMenu.loadAssets(watcher);
	}
	
	public ImageLayer getBgLayer()
	{
		return bgLayer;
	}

	// ritorna l'istanza del gioco associata al menu.
	protected TheKnowledgeTowers getGame()
	{
		return game;
	}
	
	// ritorna un IPoint contenente la locazione corrente del puntatore.
	public IPoint getMouseLocation()
	{
		return game.getPointerLocation();
	}
	
	public Navigable getPreviousMenu()
	{
		return previousMenu;
	}

	public void setPreviousMenu(Navigable previousMenu)
	{
		this.previousMenu = previousMenu;
	}

	public Navigable getNextMenu()
	{
		return nextMenu;
	}

	public void setNextMenu(Navigable nextMenu)
	{
		this.nextMenu = nextMenu;
	}
	
	@Override
	public Navigable onMouseDown(ButtonEvent event)
	{	
		return this;
	}

	@Override
	public Navigable onMouseUp(ButtonEvent event) 
	{
		return this;
	}
	
	@Override
	public Navigable onKeyDown(Event event) 
	{
		return this;
	}
	
	@Override
	public void onKeyUp(Event event)
	{
		
	}
}
