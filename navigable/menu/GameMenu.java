package saga.progetto.tesi.navigable.menu;

import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;
import java.util.LinkedHashMap;
import java.util.Map;
import playn.core.Font.Style;
import playn.core.AssetWatcher;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Key;
import playn.core.Keyboard.Event;
import playn.core.Mouse.ButtonEvent;
import pythagoras.f.IPoint;
import pythagoras.f.Point;
import saga.progetto.tesi.core.TheKnowledgeTowers;
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.media.Text;
import saga.progetto.tesi.navigable.Gameloop;
import saga.progetto.tesi.navigable.Navigable;

public abstract class GameMenu extends Menu
{
	private static final String BG_PATH =  "images/menu/game_menu/background.png";
	private static final String LAYOUT_PATH =  "images/menu/game_menu/layout.png";
	private static final String GOLDS_PATH =  "images/menu/game_menu/golds.png";
	private static final String SELECTION_PATH =  "images/menu/selection_arrow.png";
	private static final IPoint LAYOUT_POINT = new Point(51.0f, 25.0f);
	private static final IPoint HELP_POINT = new Point(65.0f, 12.0f);
	private static final IPoint GOLDS_POINT = new Point(570.0f, 522.0f);
	private static final IPoint BUTTON_POINT = new Point(570.0f, 57.0f);
	private static Image bgImage;
	private static Image layoutImage;
	private static Image goldsImage;
	private static Image selectionImage;
	
	private IPoint selectionPoint;
	private ImageLayer layoutLayer;
	private ImageLayer goldsLayer;
	private ImageLayer selectionLayer;
	private ImageLayer bgLayer;
	private Player player;
	private Map<Text, GameMenu> buttons;
	private Text goldText;
	private Text helpText;
	private Gameloop gameLoop;
	private int currentMenu;
	private boolean isShowingTutorial;
	
	public GameMenu(TheKnowledgeTowers game, Player player, Gameloop gameloop)
	{
		super(game);
		this.gameLoop = gameloop;
		this.player = player;
		selectionPoint = BUTTON_POINT.add(-30.0f, + 45.0f * currentMenu + 10.0f);
		bgLayer = graphics().createImageLayer(bgImage);
		bgLayer.setVisible(false);
		bgLayer.setDepth(9.0f);
		layoutLayer = graphics().createImageLayer(layoutImage);
		layoutLayer.setVisible(false);
		layoutLayer.setTranslation(LAYOUT_POINT.x(), LAYOUT_POINT.y());
		layoutLayer.setDepth(10.0f);
		goldsLayer = graphics().createImageLayer(goldsImage);
		goldsLayer.setVisible(false);
		goldsLayer.setTranslation(GOLDS_POINT.x(), GOLDS_POINT.y());
		goldsLayer.setDepth(11.0f);
		selectionLayer = graphics().createImageLayer(selectionImage);
		selectionLayer.setVisible(false);
		selectionLayer.setTranslation(selectionPoint.x(), selectionPoint.y());
		selectionLayer.setDepth(12.0f);
		graphics().rootLayer().add(bgLayer);
		graphics().rootLayer().add(layoutLayer);
		graphics().rootLayer().add(goldsLayer);
		graphics().rootLayer().add(selectionLayer);
		buttons = new  LinkedHashMap<Text, GameMenu>();
	}
	
	public enum ButtonType
	{
		CHARACTER("Character"), ITEM("Item"), EQUIP("Equip"), KNOWLEDGE("Knowledge"), JOB("Job"), TUTORIAL("Key Bindings"), CONTACT("Contact");
		
		private String text;
		
		private ButtonType(String text)
		{
			this.text = text;
		}
		
		public String getText()
		{
			return text;
		}
	}
	
	public static void loadAssets(AssetWatcher watcher)
	{
		bgImage = assets().getImage(BG_PATH);
		layoutImage = assets().getImage(LAYOUT_PATH);
		goldsImage = assets().getImage(GOLDS_PATH);
		selectionImage = assets().getImage(SELECTION_PATH);
		watcher.add(bgImage);
		watcher.add(layoutImage);
		watcher.add(goldsImage);
		watcher.add(selectionImage);
		CharacterMenu.loadAssets(watcher);
		JobMenu.loadAssets(watcher);
		KnowledgeMenu.loadAssets(watcher);
		KeysMenu.loadAssets(watcher);
	}
	
	public abstract int getMenuIndex();
	
	public void init()
	{
		helpText = new Text("Help", Style.PLAIN, 22, 0xFFFFFFFF, 0xFF000000);
		helpText.setTranslation(HELP_POINT.x(), HELP_POINT.y());
		helpText.setDepth(12.0f);
		helpText.init();
	}
	
	public void initButtons(ButtonType menuButton)
	{
		for (ButtonType buttonType : ButtonType.values())
		{
			Text charText = new Text(buttonType.getText(), Style.PLAIN, 20, 0xFFFFFFFF);

			charText.setTranslation(BUTTON_POINT.x(), BUTTON_POINT.y() + 45 * buttonType.ordinal());
			charText.setDepth(12.0f);
			charText.init();
			
			buttons.put(charText, gameLoop.getMenu().get(buttonType.ordinal()));
		}
		
		Text resumeText = new Text("Resume", Style.PLAIN, 20, 0xFFFFFFFF);
		resumeText.setTranslation(BUTTON_POINT.x(), BUTTON_POINT.y() + 45 * buttons.size());
		resumeText.setDepth(12.0f);
		resumeText.init();
		buttons.put(resumeText, this);
	}
	
	@Override
	public void setVisible(boolean visible)
	{
		bgLayer.setVisible(visible);
		layoutLayer.setVisible(visible);
		goldsLayer.setVisible(visible);
		selectionLayer.setVisible(visible);
		helpText.setVisible(visible);
		
		for (Map.Entry<Text, GameMenu> button : buttons.entrySet())
			button.getKey().setVisible(visible);
		
		if (visible)
		{
			this.currentMenu = getMenuIndex();
			selectionPoint = BUTTON_POINT.add(-30.0f, + 45.0f * currentMenu + 10.0f);
			selectionLayer.setTranslation(selectionPoint.x(), selectionPoint.y());
			goldText = new Text(String.valueOf(getPlayer().getGolds()).replaceAll("\\..*$", ""), Style.PLAIN, 14, 0xFFFFFFFF);
			goldText.setTranslation(GOLDS_POINT.x() + 35.0f, GOLDS_POINT.y());
			goldText.setDepth(12.0f);
			goldText.setVisible(visible);
			goldText.init();
		}
		
		else
			goldText.destroy();
	}
	
	@Override
	public Navigable onMouseDown(ButtonEvent event)
	{	
		IPoint p = new Point(event.localX(), event.localY());
		for (Map.Entry<Text, GameMenu> entry : buttons.entrySet())
		{
			if (p.x() >= entry.getKey().tx() && p.x() <= entry.getKey().tx() + entry.getKey().width() && 
					p.y() >= entry.getKey().ty() && p.y() <= entry.getKey().ty() + entry.getKey().height())
			{
				setVisible(false);
				
				if (entry.getKey().toString().equals("Resume"))
				{
					setVisible(false);
					player.getMap().setVisible(true);
					return gameLoop;
				}
				
				entry.getValue().setVisible(true);
				return entry.getValue();
			}
		}
		
		return this;
	}

	@Override
	public Navigable onMouseUp(ButtonEvent event) 
	{
		return super.onMouseUp(event);
	}
	
	@Override
	public Navigable onKeyDown(Event event)
	{
		if (!getGame().isKeyDown())
		{
			getGame().setKeyDown(true);	
			if (event.key() == Key.ESCAPE)
			{
				setVisible(false);
				player.getMap().setVisible(true);
				gameLoop.setMenuVisibility(true);
				return gameLoop;
			}
		}

		return this;
	}
	
	public Player getPlayer()
	{
		return player;
	}

	public boolean isShowingTutorial()
	{
		return isShowingTutorial;
	}

	public void setShowingTutorial(boolean isShowingTutorial)
	{
		this.isShowingTutorial = isShowingTutorial;
	}

	public Text initText(String textMessage, float x, float y, int size, boolean visible)
	{
		Text text = new Text(textMessage, Style.PLAIN, size, 0xFFFFFFFF);
		text.setTranslation(x, y);
		text.setDepth(14.0f);
		text.setVisible(visible);
		text.init();
		return text;
	}
	
	public IPoint getSelectionPoint()
	{
		return selectionPoint;
	}
	
	public void setSelectionPoint(IPoint selectionPoint)
	{
		this.selectionPoint = selectionPoint;
	}
	
	public ImageLayer getSelectionLayer()
	{
		return selectionLayer;
	}
	
	public void update(int delta)
	{
		int i = 0;
		for (Map.Entry<Text, GameMenu> entry : buttons.entrySet())
		{
			
			if (getGame().getPointerLocation().x() >= BUTTON_POINT.x() && getGame().getPointerLocation().x() <= BUTTON_POINT.x() + entry.getKey().width() && 
					getGame().getPointerLocation().y() >= BUTTON_POINT.y() + 45.0f * i && getGame().getPointerLocation().y() <= BUTTON_POINT.y() + 45.0f * i + entry.getKey().height())
			{
				currentMenu = i;
				selectionPoint = BUTTON_POINT.add(-30.0f, + 45.0f * currentMenu + 10.0f);
				selectionLayer.setTranslation(selectionPoint.x(), selectionPoint.y());
			}
			i++;
		}
	}
}
