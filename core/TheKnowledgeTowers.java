package saga.progetto.tesi.core;

//stop - undeploy poi scompare la cartella poi sotto la sezione deploy e scegli il tuo WAR (TKT.WAR)
import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;
import static playn.core.PlayN.mouse;
import static playn.core.PlayN.net;
import static playn.core.PlayN.log;
import static playn.core.PlayN.keyboard;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import playn.core.AssetWatcher;
import playn.core.Font;
import playn.core.Game;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Key;
import playn.core.Keyboard;
import playn.core.Keyboard.Event;
import playn.core.Keyboard.TypedEvent;
import playn.core.Mouse;
import playn.core.Mouse.ButtonEvent;
import playn.core.Mouse.MotionEvent;
import playn.core.Mouse.WheelEvent;
import playn.core.util.Callback;
import pythagoras.f.IPoint;
import pythagoras.f.Point;
import saga.progetto.tesi.core.annotation.Annotation;
import saga.progetto.tesi.core.annotation.AnnotationProtocol;
import saga.progetto.tesi.data.MapManager;
import saga.progetto.tesi.data.MapSerializer;
import saga.progetto.tesi.data.MapSerializer.MapData;
import saga.progetto.tesi.entity.dynamicentity.DynamicEntity;
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.entity.staticentity.Drop;
import saga.progetto.tesi.entity.staticentity.Item;
import saga.progetto.tesi.gui.Bar;
import saga.progetto.tesi.gui.GameGUI;
import saga.progetto.tesi.gui.PopupTooltip;
import saga.progetto.tesi.gui.PopupWindow;
import saga.progetto.tesi.job.Job;
import saga.progetto.tesi.map.GameMap;
import saga.progetto.tesi.map.cell.Cell;
import saga.progetto.tesi.media.AudioManager;
import saga.progetto.tesi.media.Text;
import saga.progetto.tesi.navigable.Gameloop;
import saga.progetto.tesi.navigable.LoadingScreen;
import saga.progetto.tesi.navigable.Navigable;
import saga.progetto.tesi.navigable.button.Button;
import saga.progetto.tesi.navigable.menu.CreationMenu;
import saga.progetto.tesi.navigable.menu.GameMenu;
import saga.progetto.tesi.navigable.menu.HomeMenu;
import saga.progetto.tesi.navigable.menu.Ladder;
import saga.progetto.tesi.navigable.menu.Menu;

//find . | xargs wc -l
public class TheKnowledgeTowers extends Game.Default
{
	public static final String VERSION = "v1.03";
	// rappresenta la frequenza di update.
	public static final int UPDATE_RATE = 22;
	public static final boolean isFreeVersion = false;
	private static final float NUMBER_OF_MAPS = 138.0f;
	private static final String LOADING_PATH =  "images/menu/loading.png";
	private static final String LOADING_BAR_PATH =  "images/menu/loading_bar.png";
	private static final IPoint LOADING_POINT = new Point(41.0f, 273.0f);
	private static final IPoint LOADING_TEXT_POINT = new Point(400.0f, 300.0f);
	public static final float NEW_GAME_RATE = 0.25f;
	
	private String serverAddress;
	private String gameName;
	private MapManager manager;
	private InformationRetriever retriever;
	// lo stato corrente del gioco.
	private Navigable gameState;
	private Gameloop gameLoop;
	// la posizione attuale del puntatore del mouse.
	private IPoint pointerLocation;
	private ImageLayer loadingLayer;
	private ImageLayer loadingBar;
	private Image barImage;
	// la lista contiene i tasti premuti.
	private Set<Key> keyboard;
	private Player player;
	private ServerConnection serverConnection;
	private AnnotationProtocol annotationProtocol;
	private Annotation annotation;
	private Map<String, Map<String, String>> state;
	private Text loadingText;
	private boolean texturesLoaded;
	private boolean mapLoaded;
	private boolean isKeyDown;
	private boolean isMouseDown;
	private boolean isDownloadingGlobalState;
	private int mapsDownloaded;

	public TheKnowledgeTowers(InformationRetriever retriever)
	{
		super(UPDATE_RATE);
		this.retriever = retriever;
	}

	@Override
	public void init()
	{
		serverAddress = retriever.getServerAddress();
		gameName = retriever.getGameName();
//		JsonGenerator.getInstance().initJson();
		AudioManager audio = new AudioManager();
		loadingLayer = graphics().createImageLayer(assets().getImage(LOADING_PATH));
		loadingLayer.setDepth(1.0f);
		graphics().rootLayer().add(loadingLayer);
		barImage = assets().getImage(LOADING_BAR_PATH);
		manager = MapManager.getInstance();
		pointerLocation = new Point();
		// caricamento degli asset dei bottoni e dei menu.
		Button.loadAssets();
		loadAssets();
		// istanziazione di tutti gli oggetti navigabili.
		final HomeMenu home = new HomeMenu(this);
//		gameLoop = new Gameloop(this, "2towerend", "2towerboss");
		gameLoop = new Gameloop(audio, this, "start1", "1towerend");
		gameLoop.setHomeMenu(home);
		CreationMenu creationMenu = new CreationMenu(this);
		LoadingScreen loadingScreen = new LoadingScreen(this, gameLoop);

		// inizializzazione dei bottoni di ciascun menù.
		home.setButtons(creationMenu, loadingScreen, null);
		creationMenu.setButtons(loadingScreen, home);

		// il menù iniziale viene reso visibile e settato come stato corrente.
		setGameState(home);

		// inizializzazione dei listener di mouse e tastiera.
		setMouseListener();
		setKeyboardListener();
		keyboard = new HashSet<Key>();
		
		final MapSerializer mapSerializer = new MapSerializer();
		
		for (int i = 0; i < NUMBER_OF_MAPS; i++)
		{
			net().get(serverAddress + "/maps?mapId=" + i, new Callback<String>()
			{
				@Override
				public void onSuccess(String json)
				{
					manager.addMap(mapSerializer.deserializeFromJson(json));
					mapsDownloaded++;
					
					if (loadingBar != null)
						loadingBar.destroy();
					
					if (loadingText != null)
						loadingText.destroy();
					
					loadingText = new Text(String.valueOf((int)(mapsDownloaded / NUMBER_OF_MAPS * 100)) + "%", Font.Style.BOLD, 12, 0xFFFFFFFF, 0xFF000000);
					loadingText.setVisible(true);
					loadingText.setDepth(3.0f);
					loadingText.setTranslation(LOADING_TEXT_POINT.add(loadingText.width() / 2, loadingText.height() / 2));
					loadingText.init();
					
					loadingBar = graphics().createImageLayer(barImage.subImage(0, 0, barImage.width() * mapsDownloaded / NUMBER_OF_MAPS, barImage.height()));
					loadingBar.setDepth(2.0f);
					loadingBar.setTranslation(LOADING_POINT.x(), LOADING_POINT.y());
					loadingBar.setVisible(true);
					graphics().rootLayer().add(loadingBar);
					
					if (mapsDownloaded == NUMBER_OF_MAPS)
					{
						mapLoaded = true;
						loadingLayer.setVisible(false);
						loadingBar.destroy();
						loadingText.destroy();
					}
				}
	
				@Override
				public void onFailure(Throwable err)
				{
					log().error("Error loading string!", err);
				}
			});
		}
		
		serverConnection = new ServerConnection(serverAddress, gameName);
		isDownloadingGlobalState = true;
	}

	public Gameloop getGameLoop()
	{
		return gameLoop;
	}

	public void setGameLoop(Gameloop gameLoop)
	{
		this.gameLoop = gameLoop;
	}

	// caricamento asincrono degli assets attraverso l'asset watcher.
	public void loadAssets()
	{
		AssetWatcher watcher = new AssetWatcher(new AssetWatcher.Listener()
		{
			@Override
			public void error(Throwable e)
			{
				log().error(e.getMessage());
			}

			@Override
			public void done()
			{
				texturesLoaded = true;
			}
		});

		Menu.loadAssets(watcher);
		Player.loadAssets(watcher);
		GameGUI.loadAssets(watcher);
		PopupWindow.loadAssets(watcher);
		PopupTooltip.loadAssets(watcher);
		GameMap.loadAssets(watcher);
		GameMenu.loadAssets(watcher);
		DynamicEntity.loadAssets(watcher);
		Job.loadAssets(watcher);
		Cell.loadAssets(watcher);
		Bar.loadAssets(watcher);
		Drop.loadAssets(watcher);
		Item.loadAssets(watcher);
		watcher.start();
	}

	public Navigable getGameState()
	{
		return gameState;
	}

	// assegna lo stato corrente del gioco
	public void setGameState(Navigable gameState)
	{
		this.gameState = gameState;
	}

	public void setMouseListener()
	{
		mouse().setListener(new Mouse.Listener()
		{
			@Override
			public void onMouseDown(ButtonEvent event)
			{
				setGameState(gameState.onMouseDown(event));
				isMouseDown = true;
			}

			@Override
			public void onMouseMove(MotionEvent event)
			{
				pointerLocation = new Point(event.localX(), event.localY());
			}

			@Override
			public void onMouseUp(ButtonEvent event)
			{
				gameState.onMouseUp(event);
				isMouseDown = false;
			}

			@Override
			public void onMouseWheelScroll(WheelEvent event)
			{

			}
		});
	}

	public void setKeyboardListener()
	{
		keyboard().setListener(new Keyboard.Listener()
		{
			@Override
			public void onKeyDown(Event event)
			{
				if (!keyboard.contains(event.key()))
					keyboard.add(event.key());
				setGameState(gameState.onKeyDown(event));
			}

			@Override
			public void onKeyUp(Event event)
			{
				isKeyDown = false;
				
				if (keyboard.contains(event.key()))
					keyboard.remove(event.key());
				
				gameLoop.onKeyUp(event);
			}

			@Override
			public void onKeyTyped(TypedEvent event)
			{

			}
		});
	}

	public MapManager getSave()
	{
		return manager;
	}

	// ritorna la lista di tasti premuti
	public Set<Key> getKeyboard()
	{
		return keyboard;
	}

	// ritorna vero se gli assets sono stati caricati (Utilizzato dall'asset
	// watcher)
	public boolean texturesLoaded()
	{
		return texturesLoaded;
	}

	// ritorna la posizione corrente del puntatore
	public IPoint getPointerLocation()
	{
		return pointerLocation;
	}

	public Player getPlayer()
	{
		return player;
	}

	public void setPlayer(Player player)
	{
		this.player = player;
	}

	public MapData getMapData(String id)
	{
		return manager.getMap(id);
	}

	public boolean isKeyDown()
	{
		return isKeyDown;
	}
	
	public boolean isMouseDown()
	{
		return isMouseDown;
	}

	public void setKeyDown(boolean isKeyDown)
	{
		this.isKeyDown = isKeyDown;
	}
	
//	public static void testHtml(String s, float x, float y)
//	{
//		Text goldText = new Text(s, "Gabriola", Style.PLAIN, 24, 0xFFFFFFFF);
//		goldText.setTranslation(x, y);
//		goldText.setDepth(10.0f);
//		goldText.setVisible(true);
//		goldText.init();
//	}

	public ServerConnection getServerConnection()
	{
		return serverConnection;
	}

	public void setServerConnection(ServerConnection serverConnection)
	{
		this.serverConnection = serverConnection;
	}

	public Annotation getAnnotation()
	{
		return annotation;
	}

	public void setAnnotation(Annotation annotation)
	{
		this.annotation = annotation;
	}

	public AnnotationProtocol getAnnotationProtocol()
	{
		return annotationProtocol;
	}

	public void setAnnotationProtocol(AnnotationProtocol annotationProtocol)
	{
		this.annotationProtocol = annotationProtocol;
	}

	public Map<String, Map<String, String>> getState()
	{
		return state;
	}
	
	public void setState(Map<String, Map<String, String>> state)
	{
		this.state = state;
	}
	
	public String getServerAddress()
	{
		return serverAddress;
	}

	public String getGameName()
	{
		return gameName;
	}
	
	public InformationRetriever getRetriever()
	{
		return retriever;
	}

	@Override
	public void update(int delta)
	{
		if (mapLoaded)
		{
			if (gameState instanceof HomeMenu && !((HomeMenu) gameState).visible())
				gameState.setVisible(true);

			gameState.update(delta);
		}
		
		if (isDownloadingGlobalState)
		{
			isDownloadingGlobalState = false;
			
			serverConnection.getGlobalState(new Callback<Map<String,String>>()
			{

				@Override
				public void onSuccess(Map<String, String> result)
				{
					Ladder ladder = Ladder.getInstance();
					ladder.setData(result);
					serverConnection = null;
				}

				@Override
				public void onFailure(Throwable cause)
				{
					
				}
							
			}, "player_info");
		}
	}
	
	@Override
	public void paint(float alpha)
	{
	}
}
