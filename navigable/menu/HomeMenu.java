package saga.progetto.tesi.navigable.menu;

import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;
import java.util.Map;
import playn.core.AssetWatcher;
import playn.core.Font;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Key;
import playn.core.Mouse;
import playn.core.Keyboard.Event;
import playn.core.Mouse.ButtonEvent;
import playn.core.util.Callback;
import pythagoras.f.Circle;
import pythagoras.f.Dimension;
import pythagoras.f.ICircle;
import pythagoras.f.IDimension;
import pythagoras.f.IPoint;
import pythagoras.f.IRectangle;
import pythagoras.f.Point;
import pythagoras.f.Rectangle;
import saga.progetto.tesi.media.Text;
import saga.progetto.tesi.core.ServerConnection;
import saga.progetto.tesi.core.TheKnowledgeTowers;
import saga.progetto.tesi.core.reposity.Language;
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.navigable.Gameloop;
import saga.progetto.tesi.navigable.Navigable;

// rappresenta il menù principale di gioco.
public class HomeMenu extends IndexMenu
{
	private static final String TITLE_PATH = "images/menu/main_background.png";
	private static final String SELECTION_PATH =  "images/menu/selection.png";
	private static final String LOADING_SUCCESS_PATH =  "images/menu/loading_success.png";
	private static final String LOADING_ERROR_PATH =  "images/menu/loading_error.png";
	private static final String LOGIN_PATH =  "images/menu/login.png";
	private static final IPoint VERSION_POINT = new Point(740.0f, 563.0f);
	private static final IDimension BUTTON_SIZE = new Dimension(204.0f, 31.0f);
	private static final IRectangle NEW_GAME = new Rectangle(new Point(21.0f, 420.0f), BUTTON_SIZE);
	private static final IRectangle LOAD_GAME = new Rectangle(new Point(21.0f, 451.0f), BUTTON_SIZE);
	private static final IRectangle CONTACT = new Rectangle(new Point(21.0f, 482.0f), BUTTON_SIZE);
	private static final IRectangle LADDER = new Rectangle(new Point(21.0f, 513.0f), BUTTON_SIZE);
	private static final IRectangle RELEASE = new Rectangle(new Point(21.0f, 544.0f), BUTTON_SIZE);
	private static final IPoint LOGIN_POINT = new Point(279.0f, 254.0f);
	private static final IPoint NAME_POINT = new Point(305.0f, 296.0f);
	private static final IPoint PASSWORD_POINT = new Point(305.0f, 373.0f);
	private static final IPoint LOADING_POPUP_POINT = new Point(264.0f, 243.0f);
	private static final IPoint LOADING_BUTTON_POINT = new Point(307.0f, 309.0f);
	private static final IDimension LOADING_BUTTON_SIZE = new Dimension(187.0f, 22.0f);
	private static final ICircle LOADING_CANCEL_BUTTON = new Circle(new Point(509.0f, 267.0f), 12.0f);
	private static final IRectangle NAME_HIT_BOX = new Rectangle(NAME_POINT, new Dimension(192.0f, 28.0f));
	private static final IRectangle PASSWORD_HIT_BOX = new Rectangle(PASSWORD_POINT, new Dimension(192.0f, 28.0f));
	private static final float END_NAME_TIME = 400.0f;
	private static Image selectionImage;
	private static Image titleImage;
	private static Image loginImage;
	private static Image loadingSuccessImage;
	private static Image loadingErrorImage;
	private static ImageLayer newGameSelection;
	private static ImageLayer loadGameSelection;
	private static ImageLayer contactSelection;
	private static ImageLayer ladderSelection;
	private static ImageLayer releaseSelection;
	private static ImageLayer titleLayer;
	private static ImageLayer loginLayer;
	private static ImageLayer loadingSuccessLayer;
	private static ImageLayer loadingErrorLayer;
	
	private ImageLayer currentVisibleLayer;
	private Navigable newGame;
	private Navigable loadGame;
	private Text versionText;
	private Text name;
	private Text endName;
	private Text password;
	private Text endPassword;
	private String currentName = "";
	private String currentPassword = "";
	private String tempName = "";
	private float currentTime;
	private float currentLoad;
	private boolean hasRequestedLogin;
	private boolean canLogin;
	private boolean isLoggingIn;
	private boolean isLoaded;
	private boolean isTypingName;
	private boolean isTypingPassword;
	
	public HomeMenu(TheKnowledgeTowers game)
	{
		super(game);
		newGameSelection = graphics().createImageLayer(selectionImage.subImage(0, 0 * NEW_GAME.height(), NEW_GAME.width(), NEW_GAME.height()));
		newGameSelection.setVisible(false);
		newGameSelection.setDepth(1.0f);
		newGameSelection.setTranslation(NEW_GAME.x(), NEW_GAME.y());
		loadGameSelection = graphics().createImageLayer(selectionImage.subImage(0, 1 * LOAD_GAME.height(), LOAD_GAME.width(), LOAD_GAME.height()));
		loadGameSelection.setVisible(false);
		loadGameSelection.setDepth(1.0f);
		loadGameSelection.setTranslation(LOAD_GAME.x(), LOAD_GAME.y());
		contactSelection = graphics().createImageLayer(selectionImage.subImage(0, 2 * CONTACT.height(), CONTACT.width(), CONTACT.height()));
		contactSelection.setVisible(false);
		contactSelection.setDepth(1.0f);
		contactSelection.setTranslation(CONTACT.x(), CONTACT.y());
		ladderSelection = graphics().createImageLayer(selectionImage.subImage(0, 3 * LADDER.height(), LADDER.width(), LADDER.height()));
		ladderSelection.setVisible(false);
		ladderSelection.setDepth(1.0f);
		ladderSelection.setTranslation(LADDER.x(), LADDER.y());
		releaseSelection = graphics().createImageLayer(selectionImage.subImage(0, 4 * RELEASE.height(), RELEASE.width(), RELEASE.height()));
		releaseSelection.setVisible(false);
		releaseSelection.setDepth(1.0f);
		releaseSelection.setTranslation(RELEASE.x(), RELEASE.y());
		titleLayer = graphics().createImageLayer(titleImage);
		titleLayer.setVisible(false);
		titleLayer.setDepth(0.0f);
		loginLayer = graphics().createImageLayer(loginImage);
		loginLayer.setVisible(false);
		loginLayer.setDepth(1.0f);
		loginLayer.setTranslation(LOGIN_POINT.x(), LOGIN_POINT.y());
		loadingSuccessLayer = graphics().createImageLayer(loadingSuccessImage);
		loadingSuccessLayer.setVisible(false);
		loadingSuccessLayer.setDepth(2.0f);
		loadingSuccessLayer.setTranslation(LOADING_POPUP_POINT.x(), LOADING_POPUP_POINT.y());
		loadingErrorLayer = graphics().createImageLayer(loadingErrorImage);
		loadingErrorLayer.setVisible(false);
		loadingErrorLayer.setDepth(2.0f);
		loadingErrorLayer.setTranslation(LOADING_POPUP_POINT.x(), LOADING_POPUP_POINT.y());
		graphics().rootLayer().add(newGameSelection);
		graphics().rootLayer().add(loadGameSelection);
		graphics().rootLayer().add(contactSelection);
		graphics().rootLayer().add(ladderSelection);
		graphics().rootLayer().add(releaseSelection);
		graphics().rootLayer().add(titleLayer);
		graphics().rootLayer().add(loginLayer);
		graphics().rootLayer().add(loadingSuccessLayer);
		graphics().rootLayer().add(loadingErrorLayer);
		versionText = new Text(TheKnowledgeTowers.VERSION, Font.Style.BOLD, 16, 0xFFFFFFFF, 0xFF000000);
		versionText.setDepth(3.0f);
		versionText.setVisible(false);
		versionText.setTranslation(VERSION_POINT);
		versionText.init();
		endName = new Text("_", Font.Style.PLAIN, 16, 0xFFFFFFFF);
		endName.setDepth(2.0f);
		endName.setVisible(false);
		endName.init();
		setName();
		endPassword = new Text("_", Font.Style.PLAIN, 16, 0xFFFFFFFF);
		endPassword.setDepth(2.0f);
		endPassword.setVisible(false);
		endPassword.init();
		setPassword();
	}
	
	public static void loadAssets(AssetWatcher watcher)
	{
		selectionImage = assets().getImage(SELECTION_PATH);
		titleImage = assets().getImage(TITLE_PATH);
		loginImage = assets().getImage(LOGIN_PATH);
		loadingSuccessImage = assets().getImage(LOADING_SUCCESS_PATH);
		loadingErrorImage = assets().getImage(LOADING_ERROR_PATH);
		watcher.add(selectionImage);
		watcher.add(titleImage);
		watcher.add(loginImage);
		watcher.add(loadingSuccessImage);
		watcher.add(loadingErrorImage);
		Ladder.loadAssets(watcher);
		Contact.loadAssets(watcher);
		Release.loadAssets(watcher);
	}
	
	@Override
	public void setVisible(boolean visible)
	{
		if (visible)
		{
			newGameSelection.setVisible(true);
			currentVisibleLayer = newGameSelection;
		}
		
		else
		{
			newGameSelection.setVisible(false);
			loadGameSelection.setVisible(false);
			contactSelection.setVisible(false);
			ladderSelection.setVisible(false);
			releaseSelection.setVisible(false);
		}
		
		titleLayer.setVisible(visible);
		versionText.setVisible(visible);
		if (!visible && loginLayer.visible())
			loginLayer.setVisible(false);
	}
	
	public boolean visible()
	{
		return titleLayer.visible();
	}
	
	public void setName()
	{
		if (name != null)
			name.destroy();
		
		name = new Text(currentName, Font.Style.PLAIN, 16, 0xFFFFFFFF);
		name.setTranslation(NAME_POINT.add((BUTTON_SIZE.width() - name.width()) / 2 - 12, (BUTTON_SIZE.height() - name.height()) / 2 + 2));
		name.setDepth(2.0f);
		name.setVisible(true);
		name.init();
		endName.setTranslation(NAME_POINT.add((BUTTON_SIZE.width() - name.width()) / 2 + name.getLayer().width() - 12,
				(BUTTON_SIZE.height() - name.height()) / 2 + 2));
	}
	
	public void setPassword()
	{
		if (password != null)
			password.destroy();
		
		String hidden = "";
		
		for (int i = 0; i < currentPassword.length(); i++)
			hidden += "*";
		
		password = new Text(hidden, Font.Style.PLAIN, 16, 0xFFFFFFFF);
		password.setTranslation(PASSWORD_POINT.add((BUTTON_SIZE.width() - password.width()) / 2 - 12, (BUTTON_SIZE.height() - password.height()) / 2 + 2));
		password.setDepth(2.0f);
		password.setVisible(true);
		password.init();
		endPassword.setTranslation(PASSWORD_POINT.add((BUTTON_SIZE.width() - password.width()) / 2 + password.getLayer().width() - 12,
				(BUTTON_SIZE.height() - password.height()) / 2 + 2));
	}
	
	public void setButtons(Navigable newGame, Navigable loadGame, Navigable release)
	{
		this.newGame = newGame;
		this.loadGame = loadGame;
	}
	
	@Override
	public Navigable onMouseDown(ButtonEvent event)
	{	
		if (event.button() ==  Mouse.BUTTON_LEFT && !isLoggingIn)
		{
			Point p = new Point(event.localX(), event.localY());
			
			if (!(isLoggingIn || loadingSuccessLayer.visible() || loadingErrorLayer.visible()) && NEW_GAME.contains(p) && 
						!Contact.getInstance().visible() && !Ladder.getInstance().visible() && !Release.getInstance().visible())
			{
				setVisible(false);
				newGame.setVisible(true);
				return newGame;
			}
			
			if (LOAD_GAME.contains(p) && !Contact.getInstance().visible() && !Ladder.getInstance().visible() && !Release.getInstance().visible())
			{
				String sessionId = getGame().getRetriever().isUserLogged();
				
				if (sessionId == null)
				{
					isLoggingIn = true;
					isTypingName = true;
					endName.setVisible(true);
					loginLayer.setVisible(true);
					currentTime = 0.0f;
				}
				
				else
					newLogin(sessionId);
			}
			
			if (!Ladder.getInstance().visible() && !Contact.getInstance().visible() && !Release.getInstance().visible())
			{
				if (LADDER.contains(p))
					Ladder.getInstance().setVisible(true);
				
				if (CONTACT.contains(p))
					Contact.getInstance().setVisible(true);
				
				if (RELEASE.contains(p))
					Release.getInstance().setVisible(true);
			}
			
			if (Ladder.getInstance().visible() && Ladder.LADDER_CROSS.contains(p))
				Ladder.getInstance().setVisible(false);
			
			if (Ladder.getInstance().visible() && Ladder.getInstance().click(p))
				Ladder.getInstance().openURL();
			
			if (Contact.getInstance().visible() && Contact.CONTACT_CROSS.contains(p))
				Contact.getInstance().setVisible(false);
			
			if (Contact.getInstance().visible() && Contact.getInstance().click(p))
				Contact.getInstance().openURL();
			
			if (Release.getInstance().visible() && Release.RELEASE_CROSS.contains(p))
				Release.getInstance().setVisible(false);
			
			if (loadingSuccessLayer.visible() && getGame().getPointerLocation().x() >= LOADING_BUTTON_POINT.x() && 
					getGame().getPointerLocation().y() >= LOADING_BUTTON_POINT.y() && 
					getGame().getPointerLocation().x() <= LOADING_BUTTON_POINT.x() + LOADING_BUTTON_SIZE.width() &&
							getGame().getPointerLocation().y() <= LOADING_BUTTON_POINT.y() + LOADING_BUTTON_SIZE.height())
			{
				loadingSuccessLayer.setVisible(false);
				Player.loadCharacter(getState().get("player").get("aspect"));
				Player.setName(getState().get("player").get("name"));
				Player.setAspect(getState().get("player").get("aspect"));
				getGame().getGameLoop().setHomeMenu(this);
				setVisible(false);
				loadGame.setVisible(true);
				Gameloop.setIsLoading(true);
				return loadGame;
			}
		}
		
		else if (event.button() == Mouse.BUTTON_LEFT)
		{
			Point p = new Point(event.localX(), event.localY());

			if (loadingErrorLayer.visible() && getGame().getPointerLocation().x() >= LOADING_BUTTON_POINT.x() && 
					getGame().getPointerLocation().y() >= LOADING_BUTTON_POINT.y() && 
					getGame().getPointerLocation().x() <= LOADING_BUTTON_POINT.x() + LOADING_BUTTON_SIZE.width() &&
							getGame().getPointerLocation().y() <= LOADING_BUTTON_POINT.y() + LOADING_BUTTON_SIZE.height())
			{
				loadingErrorLayer.setVisible(false);
				isLoggingIn = true;
				loginLayer.setVisible(true);
				endName.setVisible(true);
				isTypingName = true;
				currentName = "";
				endPassword.setVisible(false);
				isTypingPassword = false;
				currentPassword = "";
				setName();
				setPassword();
				
			}
			
			if (LOADING_CANCEL_BUTTON.contains(p))
			{
				isLoggingIn = false;
				loginLayer.setVisible(false);
				endName.setVisible(false);
				currentName = "";
				setName();
				endPassword.setVisible(false);
				currentPassword = "";
				setPassword();
			}
			

			else if (NAME_HIT_BOX.contains(p))
			{
				endName.setVisible(true);
				currentTime = 0.0f;
				isTypingName = true;
				endPassword.setVisible(false);
				isTypingPassword = false;
			}
			
			else if (PASSWORD_HIT_BOX.contains(p))
			{
				endPassword.setVisible(true);
				currentTime = 0.0f;
				isTypingPassword = true;
				endName.setVisible(false);
				isTypingName = false;
			}
		}
		
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
		if (event.key() == Key.ENTER && loginLayer.visible())
		{
			tempName = currentName;
			ServerConnection server = new ServerConnection(getGame().getServerAddress(), Language.EN, getGame().getGameName(), currentName);
			getGame().setServerConnection(server);
			hasRequestedLogin = true;
		}
		
		if (!(isLoggingIn || loadingSuccessLayer.visible() || loadingErrorLayer.visible()) && event.key() == Key.N && !loginLayer.visible() && 
				!Contact.getInstance().visible() && !Ladder.getInstance().visible() && !Release.getInstance().visible())
		{
			setVisible(false);
			newGame.setVisible(true);
			return newGame;
		}
	
		if (event.key() == Key.L && !Contact.getInstance().visible() && !Ladder.getInstance().visible() && 
				!Release.getInstance().visible() && !loginLayer.visible())
		{
			isLoggingIn = true;
			isTypingName = true;
			endName.setVisible(true);
			loginLayer.setVisible(true);
			currentTime = 0.0f;
			currentLoad = 0.0f;
		}
	
		if (!Ladder.getInstance().visible() && !Contact.getInstance().visible() && !Release.getInstance().visible() && !loginLayer.visible())
		{
			if (event.key() == Key.D)
				Ladder.getInstance().setVisible(true);
		
			if (event.key() == Key.C)
				Contact.getInstance().setVisible(true);
		
			if (event.key() == Key.R)
				Release.getInstance().setVisible(true);
		}
	
		if (isLoggingIn)
		{
			if (event.key() == Key.ESCAPE || (event.key() == Key.ENTER && !loginLayer.visible()))
			{
				isLoggingIn = false;
				loginLayer.setVisible(false);
				endName.setVisible(false);
				isTypingName = false;
				currentName = "";
				endPassword.setVisible(false);
				isTypingPassword = false;
				currentPassword = "";
				setName();
				setPassword();
			}
			
			if (event.key() == Key.BACKSPACE && currentName.length() > 0 && isTypingName)
			{
				name.destroy();
				currentName = currentName.substring(0, currentName.length() - 1);
				setName();
			}
			
			if (event.key() == Key.BACKSPACE && currentPassword.length() > 0 && isTypingPassword)
			{
				password.destroy();
				currentPassword = currentPassword.substring(0, currentPassword.length() - 1);
				setPassword();
			}
			
			if (event.key() == Key.TAB && isTypingName)
			{
				endPassword.setVisible(true);
				currentTime = 0.0f;
				isTypingPassword = true;
				endName.setVisible(false);
				isTypingName = false;
			}
			
			else if (event.key() == Key.TAB && isTypingPassword)
			{
				endName.setVisible(true);
				currentTime = 0.0f;
				isTypingName = true;
				endPassword.setVisible(false);
				isTypingPassword = false;
			}
			
			else if (currentName.length() < 14 && isTypingName &&  currentLoad > TheKnowledgeTowers.UPDATE_RATE)
			{
				String key = event.key().toString().replace("Key.", "");

				if (key.length() == 1)
				{
					if (getGame().getKeyboard().contains(Key.SHIFT))
						currentName += key;
					else
						currentName += key.toLowerCase();
				}
				
				setName();
			}
			
			else if (currentPassword.length() < 14 && isTypingPassword)
			{
				String key = event.key().toString().replace("Key.", "");
				if (key.length() == 1)
				{
					if (getGame().getKeyboard().contains(Key.SHIFT))
						currentPassword += key;
					else
						currentPassword += key.toLowerCase();
				}
				else if (key.matches("K[0-9]"))
					currentPassword += key.replace("K", "");
				setPassword();
			}
		}
		
		if (!isLoggingIn)
		{
			if (Ladder.getInstance().visible() && event.key() == Key.ESCAPE)
				Ladder.getInstance().setVisible(false);
			
			if (Contact.getInstance().visible() && event.key() == Key.ESCAPE)
				Contact.getInstance().setVisible(false);

			if (Release.getInstance().visible() && event.key() == Key.ESCAPE)
				Release.getInstance().setVisible(false);
			
			else if (loadingErrorLayer.visible() && (event.key() == Key.ESCAPE || event.key() == Key.ENTER))
			{
				loadingErrorLayer.setVisible(false);
				isLoggingIn = true;
				loginLayer.setVisible(true);
				endName.setVisible(true);
				isTypingName = true;
			}
			
			else if (loadingSuccessLayer.visible() && event.key() == Key.ENTER)
			{
				loadingSuccessLayer.setVisible(false);
				Player.loadCharacter(getState().get("player").get("aspect"));
				Player.setName(getState().get("player").get("name"));
				Player.setAspect(getState().get("player").get("aspect"));
				getGame().getGameLoop().setHomeMenu(this);
				setVisible(false);
				loadGame.setVisible(true);
				Gameloop.setIsLoading(true);
				return loadGame;
			}
		}	
		
		return this;
	}
	
	public Text initButtonText(String buttonName, IPoint buttonPoint)
	{
		Text buttonText = new Text(buttonName, Font.Style.PLAIN, 16, 0xFFFFFFFF);
		buttonText.setDepth(2.0f);
		buttonText.setTranslation(buttonPoint.add((BUTTON_SIZE.width() - buttonText. width()) / 2, (BUTTON_SIZE.height() - buttonText. height()) / 2));
		buttonText.init();
		return buttonText;
	}
	
	public void newLogin()
	{
		hasRequestedLogin = false;
		getGame().getServerConnection().loginIn(currentName, currentPassword, 
				getGame().getRetriever(), new Callback<String>()
		{
			@Override
			public void onSuccess(String accounts)
			{
				if (!accounts.contains("\"null\""))
				{
					getGame().getServerConnection().getState(new Callback<Map<String, Map<String, String>>>()
					{
						@Override
						public void onSuccess(Map<String, Map<String, String>> namespace)
						{
							getGame().setState(namespace);
							currentName = "";
							currentPassword = "";
							isLoggingIn = false;
							endName.setVisible(false);
							endName.setTranslation(NAME_POINT.add(BUTTON_SIZE.width() / 2 - 12, BUTTON_SIZE.height() / 2 - 6));
							endPassword.setVisible(false);
							endPassword.setTranslation(PASSWORD_POINT.add(BUTTON_SIZE.width() / 2 - 12, BUTTON_SIZE.height() / 2 - 6));
							name.destroy();
							password.destroy();
							isLoaded = true;
						}

						@Override
						public void onFailure(Throwable t)
						{
							t.printStackTrace();
						}
					});
					canLogin = true;
				}
				
				else
				{
					canLogin = false;
					isLoaded = true;
				}
			}

			@Override
			public void onFailure(Throwable t)
			{
				t.printStackTrace();
			}
		});
	}
	
	public void newLogin(String sessionId)
	{
		tempName = "xxx";
		hasRequestedLogin = false;
		getGame().setServerConnection(new ServerConnection(getGame().getServerAddress(), Language.EN, getGame().getGameName(), currentName));
		getGame().getServerConnection().loginIn(sessionId, new Callback<Map<String, String>>()
		{
			@Override
			public void onSuccess(Map<String, String> cb)
			{
				if (cb != null)
				{
					getGame().getServerConnection().getState(new Callback<Map<String, Map<String, String>>>()
					{
						@Override
						public void onSuccess(Map<String, Map<String, String>> namespace)
						{
							getGame().setState(namespace);
							currentName = "";
							currentPassword = "";
							isLoggingIn = false;
							endName.setVisible(false);
							endName.setTranslation(NAME_POINT.add(BUTTON_SIZE.width() / 2 - 12, BUTTON_SIZE.height() / 2 - 6));
							endPassword.setVisible(false);
							endPassword.setTranslation(PASSWORD_POINT.add(BUTTON_SIZE.width() / 2 - 12, BUTTON_SIZE.height() / 2 - 6));
							name.destroy();
							password.destroy();
							isLoaded = true;
						}

						@Override
						public void onFailure(Throwable t)
						{
							t.printStackTrace();
						}
					});
					
					canLogin = true;
				}
				
				else
				{
					isLoaded = true;
					canLogin = false;
				}
			}

			@Override
			public void onFailure(Throwable t)
			{
				t.printStackTrace();
			}
		});
	}
	
//	public void login()
//	{
//		hasRequestedLogin = false;
//		
//		getGame().getServerConnection().getGlobalState(new Callback<String>()
//		{
//			@Override
//			public void onSuccess(String accounts)
//			{
//				if (accounts.equals("true"))
//				{
//					getGame().getServerConnection().getState(new Callback<Map<String, Map<String, String>>>()
//					{
//						@Override
//						public void onSuccess(Map<String, Map<String, String>> namespace)
//						{
//							getGame().setState(namespace);
//							currentName = "";
//							currentPassword = "";
//							isLoggingIn = false;
//							endName.setVisible(false);
//							endName.setTranslation(NAME_POINT.add(BUTTON_SIZE.width() / 2 - 12, BUTTON_SIZE.height() / 2 - 6));
//							endPassword.setVisible(false);
//							endPassword.setTranslation(PASSWORD_POINT.add(BUTTON_SIZE.width() / 2 - 12, BUTTON_SIZE.height() / 2 - 6));
//							name.destroy();
//							password.destroy();
//							isLoaded = true;
//						}
//
//						@Override
//						public void onFailure(Throwable t)
//						{
//						}
//					});
//					
//					canLogin = true;
//				}
//				
//				else
//				{
//					isLoaded = true;
//					canLogin = false;
//				}
//			}
//
//			@Override
//			public void onFailure(Throwable t)
//			{
//			}
//		}, "accounts", currentName + getGame().getGameName() + currentPassword);
//	}
	
	public Map<String, Map<String, String>> getState()
	{
		return getGame().getState();
	}
	
	public static void clear()
	{
		newGameSelection.destroy();
		loadGameSelection.destroy();
		contactSelection.destroy();
		ladderSelection.destroy();
		releaseSelection.destroy();
		titleLayer.destroy();
		loginLayer.destroy();
		loadingSuccessLayer.destroy();
		loadingErrorLayer.destroy();
		Ladder.getInstance().clear();
	}
	
	@Override
	public void update(int delta)
	{
		currentTime += delta;
		currentLoad += delta;
		
		if (currentTime >= END_NAME_TIME && isLoggingIn && isTypingName && !loadingErrorLayer.visible())
		{
			endName.setVisible(!endName.visible());
			currentTime = 0.0f;
		}
		
		if (currentTime >= END_NAME_TIME && isLoggingIn && isTypingPassword && !loadingErrorLayer.visible())
		{
			endPassword.setVisible(!endPassword.visible());
			currentTime = 0.0f;
		}
		
		if (hasRequestedLogin && getGame().getServerConnection() != null)
			newLogin();

		if (NEW_GAME.contains(getGame().getPointerLocation()) && !newGameSelection.visible())
		{
			currentVisibleLayer.setVisible(false);
			newGameSelection.setVisible(true);
			currentVisibleLayer = newGameSelection;
		}
		
		if (LOAD_GAME.contains(getGame().getPointerLocation()) && !loadGameSelection.visible())
		{
			currentVisibleLayer.setVisible(false);
			loadGameSelection.setVisible(true);
			currentVisibleLayer = loadGameSelection;
		}
		
		if (CONTACT.contains(getGame().getPointerLocation()) && !contactSelection.visible())
		{
			currentVisibleLayer.setVisible(false);
			contactSelection.setVisible(true);
			currentVisibleLayer = contactSelection;
		}
		
		if (LADDER.contains(getGame().getPointerLocation()) && !ladderSelection.visible())
		{
			currentVisibleLayer.setVisible(false);
			ladderSelection.setVisible(true);
			currentVisibleLayer = ladderSelection;
		}
		
		if (RELEASE.contains(getGame().getPointerLocation()) && !releaseSelection.visible())
		{
			currentVisibleLayer.setVisible(false);
			releaseSelection.setVisible(true);
			currentVisibleLayer = releaseSelection;
		}
		
		if (isLoaded)
		{
			isLoaded = false;
			loginLayer.setVisible(false);
			endName.setVisible(false);
			endPassword.setVisible(false);
			
			if (canLogin && tempName.length() > 2)
				loadingSuccessLayer.setVisible(true);
			
			else
			{
				loadingErrorLayer.setVisible(true);
				getGame().setServerConnection(null);
				name.destroy();
				password.destroy();
			}
		}
	}
}
