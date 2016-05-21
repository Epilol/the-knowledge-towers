package saga.progetto.tesi.navigable.menu;

import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;
import java.util.LinkedList;
import java.util.List;
import playn.core.AssetWatcher;
import playn.core.Image;
import playn.core.ImageLayer;
import saga.progetto.tesi.core.TheKnowledgeTowers;
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.media.Text;
import saga.progetto.tesi.navigable.Gameloop;

public class KeysMenu extends GameMenu
{
	private static final int MENU_INDEX = 5;
	private static final String TUTORIAL_PATH = "images/tutorial/keybinds.png";
	private static Image tutorialImage;
	
	private List<Text> staticText;
	private ImageLayer tutorialLayer;
	
	public KeysMenu(TheKnowledgeTowers game, Player player, Gameloop gameloop)
	{
		super(game, player, gameloop);
		tutorialLayer = graphics().createImageLayer(tutorialImage);
		tutorialLayer.setVisible(false);
		tutorialLayer.setDepth(15.0f);
		tutorialLayer.setTranslation(82, 170);
		graphics().rootLayer().add(tutorialLayer);
		staticText = new LinkedList<Text>();
		init();
	}
	
	public static void loadAssets(AssetWatcher watcher)
	{
		tutorialImage = assets().getImage(TUTORIAL_PATH);
		watcher.add(tutorialImage);
	}
	
	@Override
	public int getMenuIndex()
	{
		return MENU_INDEX;
	}
	
	@Override
	public void init()
	{
		super.init();
		staticText.add(initText("Key Bindings", 80.0f, 50.0f, 22, false));
	}
	
	@Override
	public void setVisible(boolean visible)
	{
		super.setVisible(visible);
		
		for (Text text : staticText)
			text.setVisible(visible);
		
		tutorialLayer.setVisible(visible);
	}
}
