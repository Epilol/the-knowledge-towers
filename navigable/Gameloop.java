package saga.progetto.tesi.navigable;

import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import playn.core.Font;
import playn.core.ImageLayer;
import playn.core.Key;
import playn.core.Keyboard.Event;
import playn.core.Layer;
import playn.core.Mouse.ButtonEvent;
import playn.core.util.Callback;
import pythagoras.f.Circle;
import pythagoras.f.ICircle;
import pythagoras.f.IPoint;
import pythagoras.f.Point;
import saga.progetto.tesi.core.ServerConnection;
import saga.progetto.tesi.core.TheKnowledgeTowers;
import saga.progetto.tesi.core.annotation.Annotation;
import saga.progetto.tesi.core.annotation.AnnotationProtocol;
import saga.progetto.tesi.core.annotation.AnnotationStatistic;
import saga.progetto.tesi.core.reposity.DataQuality;
import saga.progetto.tesi.core.reposity.DataType;
import saga.progetto.tesi.entity.Animation;
import saga.progetto.tesi.entity.GroupRenderer;
import saga.progetto.tesi.entity.Renderable;
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.entity.staticentity.Corpse;
import saga.progetto.tesi.gui.PopupTooltip;
import saga.progetto.tesi.gui.PopupWindow;
import saga.progetto.tesi.map.EighthBossMap;
import saga.progetto.tesi.map.EighthEndMap;
import saga.progetto.tesi.map.EighthTowerMap;
import saga.progetto.tesi.map.FifthBossMap;
import saga.progetto.tesi.map.FifthEndMap;
import saga.progetto.tesi.map.FifthTowerMap;
import saga.progetto.tesi.map.FirstEndMap;
import saga.progetto.tesi.map.FourthBossMap;
import saga.progetto.tesi.map.FourthEndMap;
import saga.progetto.tesi.map.FourthTowerMap;
import saga.progetto.tesi.map.HouseMap;
import saga.progetto.tesi.map.FirstBossMap;
import saga.progetto.tesi.map.NinthBossMap;
import saga.progetto.tesi.map.NinthEndMap;
import saga.progetto.tesi.map.NinthTowerMap;
import saga.progetto.tesi.map.OutsideMap;
import saga.progetto.tesi.map.FirstTowerMap;
import saga.progetto.tesi.map.GameMap;
import saga.progetto.tesi.map.SecondBossMap;
import saga.progetto.tesi.map.SecondEndMap;
import saga.progetto.tesi.map.SecondTowerMap;
import saga.progetto.tesi.map.SeventhBossMap;
import saga.progetto.tesi.map.SeventhEndMap;
import saga.progetto.tesi.map.SeventhTowerMap;
import saga.progetto.tesi.map.SixthBossMap;
import saga.progetto.tesi.map.SixthEndMap;
import saga.progetto.tesi.map.SixthTowerMap;
import saga.progetto.tesi.map.TenthBossMap;
import saga.progetto.tesi.map.TenthEndMap;
import saga.progetto.tesi.map.TenthTowerMap;
import saga.progetto.tesi.map.ThirdBossMap;
import saga.progetto.tesi.map.ThirdEndMap;
import saga.progetto.tesi.map.ThirdTowerMap;
import saga.progetto.tesi.map.TownMap;
import saga.progetto.tesi.media.AudioManager;
import saga.progetto.tesi.media.Text;
import saga.progetto.tesi.navigable.menu.KeysMenu;
import saga.progetto.tesi.navigable.menu.CharacterMenu;
import saga.progetto.tesi.navigable.menu.ContactMenu;
import saga.progetto.tesi.navigable.menu.EquipMenu;
import saga.progetto.tesi.navigable.menu.GameMenu;
import saga.progetto.tesi.navigable.menu.HomeMenu;
import saga.progetto.tesi.navigable.menu.ItemInventory;
import saga.progetto.tesi.navigable.menu.ItemMenu;
import saga.progetto.tesi.navigable.menu.JobMenu;
import saga.progetto.tesi.navigable.menu.ScrollInventory;
import saga.progetto.tesi.navigable.menu.GameMenu.ButtonType;
import saga.progetto.tesi.navigable.menu.KnowledgeMenu;
import saga.progetto.tesi.navigable.menu.NewGameMenu;

public class Gameloop implements Navigable
{
	private final static String GLOSS_PATH = "images/menu/gloss.png";
	private final static String TUTORIAL_PATH = "images/tutorial/tutorial";
	private static final int TUTORIAL_SIZE = 7;
	private static final float GLOSS_DEPTH = 16.0f;
	private static final IPoint TUTORIAL_BUTTON_POINT = new Point(669.0f, 560.0f);
	private static final float TUTORIAL_RADIUS = 24.0f;
	private static final int FROZEN_TINT = 0xFF99F3FF;
	private static final int POISON_TINT = 0xFFB8D1B5;
	private static final float TITLE_HEIGHT_POINT = 80.0f;
	private static final float HELP_HEIGHT_POINT = 150.0f;
	private static final float DESCRIPTION_OFFSET_Y = 35.0f;
	private static final float DESCRIPTION_HEIGHT_POINT = 250.0f;
	private static final float CLICK_HEIGHT_POINT = 520.0f;
	private static boolean isLoading;
	
	private TheKnowledgeTowers game;
	private AudioManager audio;
	private HomeMenu home;
	private GameMap map;
	private Map<String, Boolean> itemRespawnInfo;
	private List<GameMenu> gameMenu;
	private Map<String, NewGameMenu> newGameMenu;
	private List<String> savedOpenMenu;
	private String currentMap;
	private String previousMap;
	private String currentTowerTitle = "";
	private Player player;
	private ImageLayer glossLayer;
	private List<GroupRenderer> rendered;
	private GroupRenderer frozenRendered;
	private GroupRenderer poisonedRendered;
	private List<ImageLayer> tutorial;
	private List<Text> glosses = new LinkedList<Text>();
	private Text title;
	private Text glossHelp;
	private Text click;
	private ICircle tutorialButton;
	private Corpse corpse;
	private int currentTutorialPage;
	private boolean mapLoaded;
	private boolean isLoaded;
	private boolean glossSkip;
	private boolean annotationsLoaded;
	private boolean textCreated;
	private boolean isTutorial;
	private boolean isMenuOpen;
	private boolean isStartingMap = true;
	private boolean isLoadingAnnotation;
	private boolean isMenuAvailable;
	private boolean isFirstItem;
	private boolean isFirstRoom;
	private boolean hasShowedTowerPopup;
	
	public Gameloop(AudioManager audio, TheKnowledgeTowers game, String currentMap, String previousMap)
	{
		this.game = game;
		this.audio = audio;
		this.currentMap = currentMap;
		this.previousMap = previousMap;
		isFirstItem = true;
		isLoadingAnnotation = true;
		itemRespawnInfo = new HashMap<String, Boolean>();
		glossLayer = graphics().createImageLayer(assets().getImage(GLOSS_PATH));
		glossLayer.setDepth(15.0f);
		glossLayer.setVisible(false);
		graphics().rootLayer().add(glossLayer);
		rendered = new LinkedList<GroupRenderer>();
		frozenRendered = new GroupRenderer(FROZEN_TINT);
		poisonedRendered = new GroupRenderer(POISON_TINT);
		savedOpenMenu = new LinkedList<String>();
		initTutorial();
	}
	
	public void setHomeMenu(HomeMenu home)
	{
		this.home = home;
	}

	public void initTutorial()
	{
		tutorial = new LinkedList<ImageLayer>();
		
		for (int i = 0; i < TUTORIAL_SIZE; i++)
		{
			ImageLayer tutorialLayer = graphics().createImageLayer(assets().getImage(TUTORIAL_PATH + i + ".png"));
			tutorialLayer.setVisible(false);
			tutorialLayer.setDepth(GLOSS_DEPTH);
			graphics().rootLayer().add(tutorialLayer);
			tutorial.add(tutorialLayer);
		}
		
		tutorialButton = new Circle(TUTORIAL_BUTTON_POINT.x(), TUTORIAL_BUTTON_POINT.y(), TUTORIAL_RADIUS);
	}
	
	// inizializza la mappa di gioco
	public void mapInit(String nextMap, String currentMap)
	{
		this.currentMap = nextMap;
		PopupWindow.getInstance().hide();

		if (currentMap.equals("-1towerend"))
			this.previousMap = "1towerend";
		
		else
			this.previousMap = currentMap;
		
		if (nextMap.contains("house"))
			map = new HouseMap(player, game, nextMap, this.previousMap, this);

		if (nextMap.contains("town"))
			map = new TownMap(player, game, nextMap, this.previousMap, this);

		if (nextMap.contains("outside"))
			map = new OutsideMap(player, game, nextMap, this.previousMap, this);

		if (nextMap.equals("1towerboss"))
			map = new FirstBossMap(player, game, nextMap, this.previousMap, this);
		
		else if (nextMap.equals("1towerend"))
			map = new FirstEndMap(player, game, nextMap, this.previousMap, this);
			
		else if (nextMap.contains("1tower") || nextMap.equals("start1"))
			map = new FirstTowerMap(player, game, nextMap, this.previousMap, this);
		
		if (nextMap.equals("2towerboss"))
			map = new SecondBossMap(player, game, nextMap, this.previousMap, this);
		
		else if (nextMap.equals("2towerend"))
			map = new SecondEndMap(player, game, nextMap, this.previousMap, this);
		
		else if (nextMap.contains("2tower") || nextMap.equals("start2"))
			map = new SecondTowerMap(player, game, nextMap, this.previousMap, this);
		
		if (nextMap.equals("3towerboss"))
			map = new ThirdBossMap(player, game, nextMap, this.previousMap, this);
		
		else if (nextMap.equals("3towerend"))
			map = new ThirdEndMap(player, game, nextMap, this.previousMap, this);
		
		else if (nextMap.contains("3tower") || nextMap.equals("start3"))
			map = new ThirdTowerMap(player, game, nextMap, this.previousMap, this);
		
		if (nextMap.equals("4towerboss"))
			map = new FourthBossMap(player, game, nextMap, this.previousMap, this);
		
		else if (nextMap.equals("4towerend"))
			map = new FourthEndMap(player, game, nextMap, this.previousMap, this);
		
		else if (nextMap.contains("4tower") || nextMap.equals("start4"))
			map = new FourthTowerMap(player, game, nextMap, this.previousMap, this);
		
		if (nextMap.equals("5towerboss"))
			map = new FifthBossMap(player, game, nextMap, this.previousMap, this);
		
		else if (nextMap.equals("5towerend"))
			map = new FifthEndMap(player, game, nextMap, this.previousMap, this);
		
		else if (nextMap.contains("5tower") || nextMap.equals("start5"))
			map = new FifthTowerMap(player, game, nextMap, this.previousMap, this);
		
		if (nextMap.equals("6towerboss"))
			map = new SixthBossMap(player, game, nextMap, this.previousMap, this);
		
		else if (nextMap.equals("6towerend"))
			map = new SixthEndMap(player, game, nextMap, this.previousMap, this);
			
		else if (nextMap.contains("6tower") || nextMap.equals("start6"))
			map = new SixthTowerMap(player, game, nextMap, this.previousMap, this);
		
		if (nextMap.equals("7towerboss"))
			map = new SeventhBossMap(player, game, nextMap, this.previousMap, this);
		
		else if (nextMap.equals("7towerend"))
			map = new SeventhEndMap(player, game, nextMap, this.previousMap, this);
		
		else if (nextMap.contains("7tower") || nextMap.equals("start7"))
			map = new SeventhTowerMap(player, game, nextMap, this.previousMap, this);
		
		if (nextMap.equals("8towerboss"))
			map = new EighthBossMap(player, game, nextMap, this.previousMap, this);
		
		else if (nextMap.equals("8towerend"))
			map = new EighthEndMap(player, game, nextMap, this.previousMap, this);
		
		else if (nextMap.contains("8tower") || nextMap.equals("start8"))
			map = new EighthTowerMap(player, game, nextMap, this.previousMap, this);
		
		if (nextMap.equals("9towerboss"))
			map = new NinthBossMap(player, game, nextMap, this.previousMap, this);
		
		else if (nextMap.equals("9towerend"))
			map = new NinthEndMap(player, game, nextMap, this.previousMap, this);
		
		else if (nextMap.contains("9tower") || nextMap.equals("start9"))
			map = new NinthTowerMap(player, game, nextMap, this.previousMap, this);
		
		if (nextMap.equals("10towerboss"))
			map = new TenthBossMap(player, game, nextMap, this.previousMap, this);
		
		else if (nextMap.equals("10towerend"))
			map = new TenthEndMap(player, game, nextMap, this.previousMap, this);
		
		else if (nextMap.contains("10tower") || nextMap.equals("start10"))
			map = new TenthTowerMap(player, game, nextMap, this.previousMap, this);

		player = map.getPlayer();
		
		if (isLoading)
		{
			player.load();
			isLoading = false;
		}
		
		if (game.getPlayer() == null)
			game.setPlayer(player);
		
		player.setVisible(false);
		
		if (corpse != null && corpse.getMapId().equals(map.getMapId()))
		{
			corpse.setMap(map);
			map.getStorableDrops().add(corpse);
			corpse.setVisible(true);
			corpse.initPhysicalBody();
		}
		
		if (corpse != null && corpse.visible() && !corpse.getMapId().equals(map.getMapId()))
			corpse.setVisible(false);
		
		if (map.getMapId().contains("start") && isLoadingAnnotation)
		{
			player.saveMap();
			loadSynset();
			playAudio();
			isLoadingAnnotation = false;
		}
		
		else
			displayMap();
	}
	
	public Corpse getCorpse()
	{
		return corpse;
	}
	
	public void setCorpse(Corpse corpse)
	{
		this.corpse = corpse;
	}
	
	private void playAudio()
	{
		audio.playFirstTheme(true);
	}
	
	private void displayMap()
	{
		map.setMapType();
		map.display();
		mapLoaded = true;
		
		if (!isLoaded)
		{
			isLoaded = true;
			initGameMenu();
		}
		
		if (!map.getMapId().contains("end"))
		{
			player.setSpeedModifier(player.getDefaultSpeed());
			player.setControlled(true);
		}
		
		else
		{
			player.setCurrentAnimation(Animation.WALK);
			player.setControlled(false);
		}
	}

	public void initGameMenu()
	{
		gameMenu = new LinkedList<GameMenu>();
		gameMenu.add(new CharacterMenu(game, player, this));
		gameMenu.add(new ItemMenu(game, player, this));
		gameMenu.add(new EquipMenu(game, player, this));
		gameMenu.add(new KnowledgeMenu(game, player, this));
		gameMenu.add(new JobMenu(game, player, this));
		gameMenu.add(new KeysMenu(game, player, this));
		gameMenu.add(new ContactMenu(game, player, this));

		int i = 0;
		for (ButtonType buttonType : ButtonType.values())
			gameMenu.get(i++).initButtons(buttonType);
		
		//TODO nuovo menu
		newGameMenu = new HashMap<String,NewGameMenu>();
		newGameMenu.put("ITEM", new ItemInventory(game, player));
		newGameMenu.put("SCROLL", new ScrollInventory(game, player));
		
	}

	public void addRenderable(Renderable renderable, float depth)
	{
		boolean containsRenderer = false;
		for (GroupRenderer renderer : rendered)
			if (renderer.depth() == depth)
			{
				renderer.addRender(renderable);
				containsRenderer = true;
			}
		
		if (!containsRenderer)
		{
			GroupRenderer renderer = new GroupRenderer(depth);
			renderer.addRender(renderable);
			rendered.add(renderer);
		}
	}
	
	public void removeRenderable(Renderable renderable, float depth)
	{
		for (GroupRenderer renderer: rendered)
			if (renderer.depth() == depth)
				renderer.removeRender(renderable);
	}
	
	public Layer getRendererLayer(float depth)
	{
		for (GroupRenderer renderer: rendered)
			if (renderer.depth() == depth)
				return renderer.getLayer();
		return null;
	}
	
	public void setRenderableVisibility(float depth, boolean visible)
	{
		for (GroupRenderer renderer: rendered)
			if (renderer.depth() == depth)
				renderer.setVisible(visible);
	}
	
	public void addFrozenRenderable(Renderable renderable)
	{
		frozenRendered.addRender(renderable);
	}
	
	public void removeFrozenRenderable(Renderable renderable)
	{
		frozenRendered.removeRender(renderable);
	}
	
	public Layer getFrozenLayer()
	{
		return frozenRendered.getLayer();
	}
	
	public void setFrozenVisibility(boolean visible)
	{
		frozenRendered.setVisible(visible);
	}
	
	public void addPoisonedRenderable(Renderable renderable)
	{
		poisonedRendered.addRender(renderable);
	}
	
	public void removePoisonedRenderable(Renderable renderable)
	{
		poisonedRendered.removeRender(renderable);
	}
	
	public Layer getPoisonedLayer()
	{
		return poisonedRendered.getLayer();
	}
	
	public void setPoisonedVisibility(boolean visible)
	{
		poisonedRendered.setVisible(visible);
	}
	
	public List<GameMenu> getMenu()
	{
		return gameMenu;
	}

	public HomeMenu getHome()
	{
		return home;
	}

	public Player getPlayer()
	{
		return player;
	}

	public void setPlayer(Player player)
	{
		this.player = player;
	}
	
	public List<ImageLayer> getTutorial()
	{
		return tutorial;
	}

	@Override
	public void setVisible(boolean visible)
	{
		if (isTutorial)
			tutorial.get(currentTutorialPage).setVisible(true);
		
		if (visible)
		{
			if (isStartingMap && game.getState() != null)
			{
				currentMap = game.getState().get("player").get("bind");
				previousMap = game.getState().get("player").get("previous_map");
				
				if (currentMap.equals("start0"))
					currentMap = "start1";
				
				if (Integer.parseInt(currentMap.replace("start", "")) > GameMap.NUMBER_OF_TOWERS)
					currentMap = "start"+ GameMap.NUMBER_OF_TOWERS;
				
				if (previousMap.equals("0towerend"))
					previousMap = "1towerend";
				
				mapInit(currentMap, previousMap);
				
				isStartingMap = false;
			}
			
			else
				mapInit(currentMap, previousMap);
		}
		
		else if (gameLoaded())
		{
			map.clear();
			map = null;
			mapLoaded = false;
		}
		
		setMenuVisibility(visible);
	}

	public static void setIsLoading(boolean b)
	{
		isLoading = b;
	}
	
	public boolean isFirstRoom()
	{
		return isFirstRoom;
	}

	public void setFirstRoom(boolean isFirstRoom)
	{
		this.isFirstRoom = isFirstRoom;
	}

	public boolean isFirstItem()
	{
		return isFirstItem;
	}

	public void setFirstItem(boolean isFirstItem)
	{
		this.isFirstItem = isFirstItem;
	}
	
	public void setMenuVisibility(boolean visible)
	{
		if (visible && savedOpenMenu.size() > 0)
		{
			Iterator<String> menuIterator = savedOpenMenu.iterator();
			while (menuIterator.hasNext())
			{
				String menu = menuIterator.next();
				newGameMenu.get(menu).setVisible(true);
				menuIterator.remove();
			}
		}
		
		else if (!visible)
			for (Map.Entry<String, NewGameMenu> entry : newGameMenu.entrySet())
			{
				if (entry.getValue().visible())
				{
					savedOpenMenu.add(entry.getKey());
					entry.getValue().setVisible(false);
				}
			}
	}
	
	public void scrollMenu()
	{
		getNewGameMenu().get("SCROLL").setVisible(!(getNewGameMenu().get("SCROLL").visible()));
		
		if (getNewGameMenu().get("ITEM").visible())
			getNewGameMenu().get("ITEM").setVisible(false);
		
		if (getNewGameMenu().get("SCROLL").visible() && !isMenuOpen())
			setMenuOpen(true);
		
		else
			updateMenuOpen();
	}
	
	public void updateMenuVisibility(String menu)
	{
		if (newGameMenu.get(menu).visible())
			newGameMenu.get(menu).setVisible(true);
	}
	
	public boolean isLoadingAnnotation()
	{
		return isLoadingAnnotation;
	}
	
	public void setLoadingAnnotation(boolean isLoadingAnnotation)
	{
		this.isLoadingAnnotation = isLoadingAnnotation;
	}

	public Map<String, NewGameMenu> getNewGameMenu()
	{
		return newGameMenu;
	}
	
	public void loadNextMap(String nextMap)
	{
		setMenuVisibility(true);
		mapInit(nextMap, previousMap);
	}

	public String getPreviousMap()
	{
		return previousMap;
	}

	public void setPreviousMap(String previousMap)
	{
		this.previousMap = previousMap;
	}

	public String getCurrentMap()
	{
		return currentMap;
	}

	public void setCurrentMap(String currentMap)
	{
		this.currentMap = currentMap;
	}
	
	public void setTutorial(boolean isTutorial)
	{
		this.isTutorial = isTutorial;
	}

	@Override
	public Navigable onMouseDown(ButtonEvent event)
	{
		IPoint p = new Point(event.localX(), event.localY());
		
		if (isMenuAvailable)
			for (Map.Entry<String, NewGameMenu> entry : newGameMenu.entrySet())
				if (entry.getValue().visible())
					entry.getValue().onMouseDown(event);

		if (isTutorial)
		{
			if (tutorialButton.contains(p))
			{
				tutorial.get(currentTutorialPage).setVisible(false);
				currentTutorialPage++;
				
				if (currentTutorialPage == TUTORIAL_SIZE)
					isTutorial = false;
				
				else
					tutorial.get(currentTutorialPage).setVisible(true);
			}
		}
			
		else
		{
			if (gameLoaded())
			{
				player.MouseListener(event);
				map.specialChestMouseListener(event);
				
				if (PopupWindow.getInstance().click(p))
				{
					PopupWindow.getInstance().hide();
					map.keepBossMessageHidden();
				}
				
			}
	
			if (click != null && click.visible())
				glossSkip = true;
			
			if (isMenuAvailable)
			{
				
				for (Map.Entry<String, NewGameMenu> entry : newGameMenu.entrySet())
					if (entry.getValue().intersectsClose(p))
					{
						entry.getValue().setVisible(false);
						updateMenuOpen();
					}
				
				if (!newGameMenu.get("ITEM").visible() && newGameMenu.get("SCROLL").visible() && newGameMenu.get("ITEM").intersectsSwitch(p))
				{
					newGameMenu.get("ITEM").setVisible(true);
					
					if (newGameMenu.get("SCROLL").visible())
						newGameMenu.get("SCROLL").setVisible(false);
				}
				
				else if (!newGameMenu.get("SCROLL").visible() && newGameMenu.get("ITEM").visible() &&  newGameMenu.get("SCROLL").intersectsSwitch(p))
				{
					newGameMenu.get("SCROLL").setVisible(true);
					
					if (newGameMenu.get("ITEM").visible())
						newGameMenu.get("ITEM").setVisible(false);
				}
			}
			

			if (player.hasClickedLevelup(p) && player.controlled() && map.getCurrentDrop() == null && !map.popupShown()
					&& !map.isSpecialChestOpen())
			{
				
				boolean canEscape = true;
				
				if (isMenuAvailable)
					for (Map.Entry<String, NewGameMenu> entry : newGameMenu.entrySet())
						if (!entry.getValue().canEscape())
							canEscape = false;
				
				if (canEscape)
				{
					setMenuVisibility(false);
					map.setVisible(false);
					gameMenu.get(0).setVisible(true);
					player.clearText();
					player.getCurrentJob().setVisible(false);
					setMenuVisibility(false);
					return gameMenu.get(0);
				}
			}
		}
		return this;
	}

	@Override
	public Navigable onMouseUp(ButtonEvent event)
	{
		if (gameLoaded() && !isTutorial)
			player.releaseMouse(event);
		
		if (isMenuAvailable)
			for (Map.Entry<String, NewGameMenu> entry : newGameMenu.entrySet())
				if (entry.getValue().visible())
					entry.getValue().onMouseUp(event);
		
		return this;
	}

	@Override
	public Navigable onKeyDown(Event event)
	{
		if (gameLoaded() && !game.isKeyDown())
		{
			game.setKeyDown(true);

			if ((event.key() == Key.ESCAPE) && player.controlled() && map.getCurrentDrop() == null && !PopupWindow.getInstance().visible()
					&& !map.isSpecialChestOpen())
			{
				boolean canEscape = true;
				
				if (PopupTooltip.getInstance().visible())
					PopupTooltip.getInstance().hide();
				
				if (isMenuAvailable)
					for (Map.Entry<String, NewGameMenu> entry : newGameMenu.entrySet())
						if (!entry.getValue().canEscape())
							canEscape = false;
				
				if (canEscape)
				{
					setMenuVisibility(false);
					map.setVisible(false);
					gameMenu.get(0).setVisible(true);
					player.clearText();
					player.getCurrentJob().setVisible(false);
					setMenuVisibility(false);
					return gameMenu.get(0);
				}
			}
			
			if (isMenuAvailable)
				for (Map.Entry<String, NewGameMenu> entry : newGameMenu.entrySet())
					if (entry.getValue().visible())
						entry.getValue().onKeyDown(event);
		}
		
		if (gameLoaded())
		{
			if (event.key() == Key.I)
			{
				newGameMenu.get("ITEM").setVisible(!(newGameMenu.get("ITEM").visible()));
				
				if (newGameMenu.get("SCROLL").visible())
					newGameMenu.get("SCROLL").setVisible(false);
				
				if (newGameMenu.get("ITEM").visible() && !isMenuOpen)
					isMenuOpen = true;
				
				else
					updateMenuOpen();
			}
			
			else if (event.key() == Key.O)
			{
				newGameMenu.get("SCROLL").setVisible(!(newGameMenu.get("SCROLL").visible()));
				
				if (newGameMenu.get("ITEM").visible())
					newGameMenu.get("ITEM").setVisible(false);
				
				if (newGameMenu.get("SCROLL").visible() && !isMenuOpen)
					isMenuOpen = true;
				
				else
					updateMenuOpen();
			}
			
			if ((event.key() == Key.ENTER || event.key() == Key.ESCAPE || event.key() == Key.SPACE) && 
					PopupWindow.getInstance().visible())
			{
				game.getKeyboard().remove(Key.SPACE);
				game.getKeyboard().remove(Key.ENTER);
				game.getKeyboard().remove(Key.ESCAPE);
				PopupWindow.getInstance().hide();
				map.keepBossMessageHidden();
			}
			
			map.specialChestKeyboardListener(event);
		}
		
		if (game.getKeyboard().contains(Key.ENTER) && click != null && click.visible())
			glossSkip = true;
		
		else if (game.getKeyboard().contains(Key.ENTER) && isTutorial)
		{
			tutorial.get(currentTutorialPage).setVisible(false);
			currentTutorialPage++;
			
			if (currentTutorialPage == TUTORIAL_SIZE)
				isTutorial = false;
			
			else
				tutorial.get(currentTutorialPage).setVisible(true);
		}

		return this;
	}
	
	@Override
	public void onKeyUp(Event event)
	{
		if (gameLoaded())
			player.onKeyUp(event);
	}
	
	public void addItem(String itemId, boolean isLocked)
	{
		itemRespawnInfo.put(itemId, isLocked);
	}
	
	public Boolean getItemInfo(String itemId)
	{
		return itemRespawnInfo.get(itemId);
	}
	
	public void resetRespawnInfo()
	{
		for (Map.Entry<String, Boolean> entry : itemRespawnInfo.entrySet())
			itemRespawnInfo.put(entry.getKey(), false);
	}
	
	public void updateMenuOpen()
	{
		boolean isMenuOpen = false;
		for (Map.Entry<String, NewGameMenu> entry : newGameMenu.entrySet())
			if (entry.getValue().visible())
				isMenuOpen = true;
		
		this.isMenuOpen = isMenuOpen;
	}

	// modifica la posizione del player in base ai tasti premuti.
	public void setPlayerDestination(Set<Key> keyboard)
	{
		player.keyboardListener(keyboard);
	}
	
	public boolean isMenuOpen()
	{
		return isMenuOpen;
	}
	
	public void setMenuOpen(boolean isMenuOpen)
	{
		this.isMenuOpen = isMenuOpen;
	}

	public boolean hasShowedTowerPopup()
	{
		return hasShowedTowerPopup;
	}

	public void setHasShowedTowerPopup(boolean hasShowedTowerPopup)
	{
		this.hasShowedTowerPopup = hasShowedTowerPopup;
	}

	public void initGlossText()
	{
		textCreated = true;
		currentTowerTitle = game.getAnnotation().getToValidateTitle().replace("_", " ").replaceAll("#...", "");
		title = new Text("The Tower of " + currentTowerTitle , Font.Style.BOLD, 45, 0xFF000000, 0xFFFFFFFF);
		title.setTranslation(graphics().width() / 2 - title.width() / 2, TITLE_HEIGHT_POINT);
		title.setDepth(GLOSS_DEPTH);
		title.setAlpha(0.0f);
		title.setVisible(true);
		title.init();
		
		glossHelp = new Text("Collect the images matching the concept.", Font.Style.BOLD, 28,
				0xFF000000, 0xFFFFFFFF);
		glossHelp.setTranslation(graphics().width() / 2 - glossHelp.width() / 2, HELP_HEIGHT_POINT);
		glossHelp.setDepth(GLOSS_DEPTH);
		glossHelp.setAlpha(0.0f);
		glossHelp.setVisible(true);
		glossHelp.init();
		
		String[] tokenizedGloss = splitGloss(game.getAnnotation().getToValidateGloss(), 32);
		float currentIndex = DESCRIPTION_HEIGHT_POINT;
		
		for (int i = 0; i < tokenizedGloss.length; i++)
		{
			Text gloss = new Text(tokenizedGloss[i], Font.Style.BOLD, 32, 0xFF000000, 0xFFFFFFFF);
			gloss.setTranslation(graphics().width() / 2 - gloss.width() / 2, currentIndex += DESCRIPTION_OFFSET_Y);
			gloss.setDepth(GLOSS_DEPTH);
			gloss.setAlpha(0.0f);
			gloss.setVisible(true);
			gloss.init();
			glosses.add(gloss);
		}

		click = new Text("press enter or click to start the tower.", Font.Style.BOLD, 28, 0xFF000000, 0xFFFFFFFF);
		click.setTranslation(graphics().width() / 2 - click.width() / 2, CLICK_HEIGHT_POINT);
		click.setDepth(GLOSS_DEPTH);
		click.setVisible(false);
		click.init();
	}

	public void updateGlossText()
	{
		if (title.alpha() < 0.99f && !click.visible())
		{
			title.setAlpha(title.alpha() + 0.01f);
			glossHelp.setAlpha(glossHelp.alpha() + 0.01f);
			
			for (Text gloss : glosses)
				gloss.setAlpha(title.alpha() + 0.01f);

			if (title.alpha() >= 0.99f)
				click.setVisible(true);
		}

		else if (click.visible() && glossSkip)
		{
			glossLayer.setAlpha(glossLayer.alpha() - 0.01f);
			title.setAlpha(glossLayer.alpha() - 0.01f);
			glossHelp.setAlpha(glossHelp.alpha() - 0.01f);

			for (Text gloss : glosses)
				gloss.setAlpha(glossLayer.alpha() - 0.01f);

			click.setAlpha(glossLayer.alpha() - 0.01f);

			if (click.alpha() < 0.01f)
				click.setVisible(false);
		}

		if (glossSkip && !click.visible())
		{
			title.destroy();
			glossHelp.destroy();
			
			for (Text gloss : glosses)
				gloss.destroy();

			click.destroy();
			glossLayer.setVisible(false);
			glossLayer.setAlpha(1.0f);
			glossSkip = false;
			textCreated = false;
			annotationsLoaded = false;
			loadNextMap(currentMap);
			isMenuAvailable = true;
		}
	}

	public String getTowerTitle()
	{
		return currentTowerTitle;
	}
	
	public boolean gameLoaded()
	{
		if (map == null)
			return false;
		return map.isLoaded();
	}

	public void loadHome()
	{
		player.setDead(false);
		player.setExhausted(false);
		map.getLocations().clear();
		map.getDirections().clear();
		setVisible(false);
		
		
		if (game.getState() == null)
		{
			String previous = "";
			
			if (player.getCurrentTower() == 1)
				previous = player.getCurrentTower() + "towerend";
			
			else
				previous = (player.getCurrentTower() - 1) + "towerend";
			
			mapInit("start" + player.getCurrentTower(), previous);
		}
			
		else
			mapInit(game.getState().get("player").get("bind"), game.getState().get("player").get("previous_map"));
	}

	public void loadSynset()
	{
		glossLayer.setVisible(true);
		ServerConnection server = game.getServerConnection();
		player.setStatistic(new AnnotationStatistic(1.0f));
		
		if (player.hasPassed() || (!player.hasPassed() && game.getAnnotationProtocol() == null))
		{
			server.getData(new Callback<Map<String, Map<String, String>>>()
			{
				@Override
				public void onSuccess(Map<String, Map<String, String>> namespace)
				{
					//TODO da modificare e togliere l'if
					if (namespace.get("TOVALIDATE").get("lemma").equals("Innocent_III"))
						loadSynset();
					
					else
					{
						game.setAnnotation(new Annotation(namespace));
						game.setAnnotationProtocol(new AnnotationProtocol(game.getServerConnection(), DataType.IMAGES, game.getAnnotation()));
						annotationsLoaded = true;
					}
				}
	
				@Override
				public void onFailure(Throwable t)
				{
					
				}
	
			}, DataType.IMAGES, DataQuality.TOVALIDATE, DataQuality.NEGATIVE);
		}

		else
			annotationsLoaded = true;
	}

	public static String[] splitGloss(String gloss, int lineSize)
	{
		StringBuilder sb = new StringBuilder(gloss);
		int i = 0;
		while ((i = sb.indexOf(" ", i + lineSize)) != -1)
			sb.replace(i, i + 1, "\t");
		return sb.toString().split("\t");
	}
	
	public boolean intersectsMenu(IPoint p)
	{
		boolean intersects = false;
		
		for (Map.Entry<String, NewGameMenu> menu : newGameMenu.entrySet())
			if (menu.getValue().intersectsWindow(p))
				intersects = true;
		
		return intersects;
	}

	@Override
	public void update(int delta)
	{
		if (!isTutorial)
		{
			if (player != null && player.isExhausted())
				loadHome();
			
			else if (gameLoaded() && mapLoaded)
			{
				setPlayerDestination(game.getKeyboard());
				map.update(delta);
			}

			if (annotationsLoaded)
			{
				if (player.hasWeapon())
					player.getWeapon().setVisible(false);
				
				if (!textCreated)
					initGlossText();
				
				updateGlossText();
			}
			
			if (isMenuAvailable)
				for (Map.Entry<String, NewGameMenu> entry : newGameMenu.entrySet())
					entry.getValue().update(delta);
		}
	}
}
