package saga.progetto.tesi.navigable;

import playn.core.Keyboard.Event;
import playn.core.Mouse.ButtonEvent;
import saga.progetto.tesi.core.TheKnowledgeTowers;
import saga.progetto.tesi.navigable.menu.CreationMenu;
import saga.progetto.tesi.navigable.menu.HomeMenu;

// schermata di caricamento
public class LoadingScreen implements Navigable
{
	private TheKnowledgeTowers game;
	private Gameloop gameLoop;
	
	public LoadingScreen(TheKnowledgeTowers game, Gameloop gameLoop)
	{
		this.game = game;
		this.gameLoop = gameLoop;
	}
	
	// carica gli assets in modo sincrono.
	public static void loadAssets()
	{
		//TODO immagine di caricamento
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
	public void setVisible(boolean visibility) 
	{

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
	
	@Override
	public void update(int delta)
	{
		//TODO aggiunto server connection
		if (game.texturesLoaded() && game.getServerConnection().isReady())
		{
			HomeMenu.clear();
			CreationMenu.clear();
			game.setGameState(gameLoop);
			setVisible(false);
			gameLoop.setVisible(true);
		}
	}
}

