package saga.progetto.tesi.navigable.menu;

import playn.core.AssetWatcher;
import saga.progetto.tesi.navigable.button.Button;
import saga.progetto.tesi.core.TheKnowledgeTowers;
import saga.progetto.tesi.navigable.Navigable;

// rappresenta un menu generico.
public abstract class IndexMenu extends Menu implements Navigable
{
	public IndexMenu(TheKnowledgeTowers game)
	{
		super(game);
	}
	
	public static void loadAssets(AssetWatcher watcher)
	{
		HomeMenu.loadAssets(watcher);
	}

	// gestisce il click del mouse
	public Navigable clickButton(Button button, String currentName)
	{	
		setVisible(false);
		button.getNextState().setVisible(true);
		return button.getNextState();
	}
	
	public Navigable clickButton(Button button)
	{	
		setVisible(false);
		button.getNextState().setVisible(true);
		return button.getNextState();
	}
}
