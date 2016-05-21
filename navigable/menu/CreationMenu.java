package saga.progetto.tesi.navigable.menu;

import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;
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
import saga.progetto.tesi.navigable.button.Button;
import saga.progetto.tesi.navigable.button.Button.ButtonType;
import saga.progetto.tesi.navigable.button.CharCreationButton;
import saga.progetto.tesi.navigable.button.ConfirmButton;
import saga.progetto.tesi.navigable.menu.JobMenu.JobType;
import saga.progetto.tesi.core.ServerConnection;
import saga.progetto.tesi.core.TheKnowledgeTowers;
import saga.progetto.tesi.core.Version;
import saga.progetto.tesi.core.reposity.Language;
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.entity.dynamicentity.PlayerStats;
import saga.progetto.tesi.navigable.Navigable;

// la classe rappresenta il menu di creazione del personaggio.
public class CreationMenu extends IndexMenu
{
	private static final String BG_PATH = "images/menu/char_creation_menu/char_creation_bg.png";
	private static final String ERROR_MESSAGE = "images/menu/char_creation_menu/char_creation_error.png";
	private static final String NAME_ERROR_MESSAGE = "images/menu/char_creation_menu/name_error.png";
	private static final String CHAR_TEMPLATE = "images/characters/templates/char_template.png";
	private static final String JOB_PATH = "images/menu/char_creation_menu/jobs.png";
	private static final String JOB_STATS = "images/menu/char_creation_menu/char_creation_stats.png";
	private static final String DESCRIPTION_PATH = "images/menu/char_creation_menu/char_creation_description.png";
	private static final int COLORS = 5;
	private static final int HAIR = 6;
	private static final IPoint CHAR_POINT = new Point(384.0f, 305.0f);
	private static final IPoint NAME_POINT = new Point(325.0f, 475.0f);
	private static final IPoint PASSWORD_POINT = new Point(325.0f, 515.0f);
	private static final IRectangle NAME_HIT_BOX = new Rectangle(NAME_POINT, new Dimension(177.0f, 21.0f));
	private static final IRectangle PASSWORD_HIT_BOX = new Rectangle(PASSWORD_POINT, new Dimension(177.0f, 21.0f));
	private static final IRectangle PRIZE_BUTTON = new Rectangle(new Point(347.0f, 334.0f), new Dimension(107.0f, 22.0f));
	private static final ICircle PRIZE_CROSS = new Circle(new Point(564.0f, 101.0f), 12);
	private static final IPoint STATS_POINT = new Point(352.0f, 156.0f);
	private static final IPoint DESCRIPTION_POINT = new Point(598.0f, 171.0f);
	private static final IPoint ERROR_POINT = new Point(263.0f, 242.0f);
	private static final IPoint ERROR_BUTTON_POINT = new Point(302.0f, 304.0f);
	private static final IPoint HAIR_P1 = new Point(348.0f, 385.0f);
	private static final IPoint HAIR_P2 = new Point(439.0f, 385.0f);
	private static final IPoint HAIR_P3 = new Point(348.0f, 401.0f);
	private static final IPoint WARRIOR_POINT = new Point(31.0f, 105.0f);
	private static final IPoint MAGE_POINT = new Point(31.0f, 248.0f);
	private static final IPoint THIEF_POINT = new Point(31.0f, 391.0f);
	private static final IPoint HAIR_P4 = new Point(439.0f, 401.0f);
	private static final IPoint ACCEPT_POINT = new Point(393.0f, 551.0f);
	private static final IPoint BACK_POINT = new Point(320.0f, 552.0f);
	private static final IDimension CHAR_SIZE = new Dimension(32.0f, 32.0f);
	private static final IDimension JOB_SIZE = new Dimension(44.0f, 51.0f);
	private static final IDimension STATS_SIZE = new Dimension(100.0f, 100.0f);
	private static final IDimension DESCRIPTION_SIZE = new Dimension(165.0f, 220.0f);
	private static final IDimension ERROR_BUTTON_SIZE = new Dimension(195.0f, 31.0f);
	private static final float END_NAME_TIME = 400.0f;
	private static Image prizeWindowImage;
	private static Image frameImage;
	private static Image charImage;
	private static Image errorMessageImage;
	private static Image nameErrorMessageImage;
	private static Image jobImage;
	private static Image statsImage;
	private static Image descriptionImage;
	private static ImageLayer prizeWindowLayer;
	private static ImageLayer frameLayer;
	private static ImageLayer charLayer;
	private static ImageLayer nameErrorMessageLayer;
	private static ImageLayer errorMessageLayer;
	private static ImageLayer unselectedWarrior;
	private static ImageLayer unselectedMage;
	private static ImageLayer unselectedThief;
	private static ImageLayer selectedWarrior;
	private static ImageLayer selectedMage;
	private static ImageLayer selectedThief;
	private static ImageLayer chosenWarrior;
	private static ImageLayer chosenMage;
	private static ImageLayer chosenThief;
	private static ImageLayer statsLayer;
	private static ImageLayer warriorStats;
	private static ImageLayer mageStats;
	private static ImageLayer thiefStats;
	private static ImageLayer descriptionLayer;
	private static ImageLayer warriorDescription;
	private static ImageLayer mageDescription;
	private static ImageLayer thiefDescription;
	private static ImageLayer currentChosen;
	private static ImageLayer currentStats;
	private static ImageLayer currentDescription;

	private Button styleMinus;
	private Button stylePlus;
	private Button colorMinus;
	private Button colorPlus;
	private Button accept;
	private Button back;
	private int currentColor;
	private int currentHair;
	private Text name;
	private Text endName;
	private Text password;
	private Text endPassword;
	private float currentTime;
	private String currentName = "";
	private String currentPassword = "";
	private JobType currentJob = JobType.DEFAULT;
	private boolean isTypingName;
	private boolean isTypingPassword;
	private boolean nameExists;
	private boolean isLogging;
	private boolean waitForResp = false;

	public CreationMenu(TheKnowledgeTowers game)
	{
		super(game);
		frameLayer = graphics().createImageLayer(frameImage);
		frameLayer.setVisible(false);
		frameLayer.setDepth(0.0f);
		charLayer = graphics().createImageLayer(charImage.subImage(currentColor, currentHair, CHAR_SIZE.width(), CHAR_SIZE.height()));
		charLayer.setVisible(false);
		charLayer.setTranslation(CHAR_POINT.x(), CHAR_POINT.y());
		charLayer.setDepth(1.0f);
		errorMessageLayer = graphics().createImageLayer(errorMessageImage);
		errorMessageLayer.setVisible(false);
		errorMessageLayer.setDepth(4.0f);
		errorMessageLayer.setTranslation(ERROR_POINT.x(), ERROR_POINT.y());
		nameErrorMessageLayer = graphics().createImageLayer(nameErrorMessageImage);
		nameErrorMessageLayer.setVisible(false);
		nameErrorMessageLayer.setDepth(4.0f);
		nameErrorMessageLayer.setTranslation(ERROR_POINT.x(), ERROR_POINT.y());
		prizeWindowLayer = graphics().createImageLayer(prizeWindowImage);
		prizeWindowLayer.setVisible(false);
		prizeWindowLayer.setDepth(12.0f);
		graphics().rootLayer().add(frameLayer);
		graphics().rootLayer().add(charLayer);
		graphics().rootLayer().add(errorMessageLayer);
		graphics().rootLayer().add(nameErrorMessageLayer);
		graphics().rootLayer().add(prizeWindowLayer);
		endName = new Text("_", Font.Style.PLAIN, 16, 0xFF000000);
		endName.setDepth(1.0f);
		endName.setVisible(false);
		endName.init();
		endPassword = new Text("_", Font.Style.PLAIN, 16, 0xFF000000);
		endPassword.setDepth(1.0f);
		endPassword.setVisible(false);
		endPassword.init();
		setName();
		setPassword();
		initDynamicLayers();
	}

	public static void loadAssets(AssetWatcher watcher)
	{
		frameImage = assets().getImage(BG_PATH);
		watcher.add(frameImage);
		prizeWindowImage = assets().getImage(Version.getWindowPath());
		watcher.add(prizeWindowImage);
		charImage = assets().getImage(CHAR_TEMPLATE);
		watcher.add(charImage);
		errorMessageImage = assets().getImage(ERROR_MESSAGE);
		watcher.add(errorMessageImage);
		nameErrorMessageImage = assets().getImage(NAME_ERROR_MESSAGE);
		watcher.add(nameErrorMessageImage);
		jobImage = assets().getImage(JOB_PATH);
		watcher.add(jobImage);
		statsImage = assets().getImage(JOB_STATS);
		watcher.add(statsImage);
		descriptionImage = assets().getImage(DESCRIPTION_PATH);
		watcher.add(descriptionImage);
	}

	private void initDynamicLayers()
	{
		initLayer(
				unselectedWarrior = graphics().createImageLayer(
						jobImage.subImage(0 * JOB_SIZE.width(), (JobType.WARRIOR.ordinal() - 1) * JOB_SIZE.height(), JOB_SIZE.width(),
								JOB_SIZE.height())), WARRIOR_POINT, 1.0f);
		initLayer(
				unselectedMage = graphics().createImageLayer(
						jobImage.subImage(0 * JOB_SIZE.width(), (JobType.BLACK_MAGE.ordinal() - 1) * JOB_SIZE.height(), JOB_SIZE.width(),
								JOB_SIZE.height())), MAGE_POINT, 1.0f);
		initLayer(
				unselectedThief = graphics().createImageLayer(
						jobImage.subImage(0 * JOB_SIZE.width(), (JobType.THIEF.ordinal() - 1) * JOB_SIZE.height(), JOB_SIZE.width(),
								JOB_SIZE.height())), THIEF_POINT, 1.0f);
		initLayer(
				selectedWarrior = graphics().createImageLayer(
						jobImage.subImage(1 * JOB_SIZE.width(), (JobType.WARRIOR.ordinal() - 1) * JOB_SIZE.height(), JOB_SIZE.width(),
								JOB_SIZE.height())), WARRIOR_POINT, 1.0f);
		initLayer(
				selectedMage = graphics().createImageLayer(
						jobImage.subImage(1 * JOB_SIZE.width(), (JobType.BLACK_MAGE.ordinal() - 1) * JOB_SIZE.height(), JOB_SIZE.width(),
								JOB_SIZE.height())), MAGE_POINT, 1.0f);
		initLayer(
				selectedThief = graphics().createImageLayer(
						jobImage.subImage(1 * JOB_SIZE.width(), (JobType.THIEF.ordinal() - 1) * JOB_SIZE.height(), JOB_SIZE.width(),
								JOB_SIZE.height())), THIEF_POINT, 1.0f);
		initLayer(
				chosenWarrior = graphics().createImageLayer(
						jobImage.subImage(2 * JOB_SIZE.width(), (JobType.WARRIOR.ordinal() - 1) * JOB_SIZE.height(), JOB_SIZE.width(),
								JOB_SIZE.height())), WARRIOR_POINT, 1.0f);
		initLayer(
				chosenMage = graphics().createImageLayer(
						jobImage.subImage(2 * JOB_SIZE.width(), (JobType.BLACK_MAGE.ordinal() - 1) * JOB_SIZE.height(), JOB_SIZE.width(),
								JOB_SIZE.height())), MAGE_POINT, 1.0f);
		initLayer(
				chosenThief = graphics().createImageLayer(
						jobImage.subImage(2 * JOB_SIZE.width(), (JobType.THIEF.ordinal() - 1) * JOB_SIZE.height(), JOB_SIZE.width(),
								JOB_SIZE.height())), THIEF_POINT, 1.0f);
		initLayer(
				statsLayer = graphics().createImageLayer(
						statsImage.subImage(0, JobType.DEFAULT.ordinal() * STATS_SIZE.height(), STATS_SIZE.width(), STATS_SIZE.height())),
				STATS_POINT, 1.0f);
		initLayer(
				warriorStats = graphics().createImageLayer(
						statsImage.subImage(0, JobType.WARRIOR.ordinal() * STATS_SIZE.height(), STATS_SIZE.width(), STATS_SIZE.height())),
				STATS_POINT, 1.0f);
		initLayer(
				mageStats = graphics()
						.createImageLayer(
								statsImage.subImage(0, JobType.BLACK_MAGE.ordinal() * STATS_SIZE.height(), STATS_SIZE.width(),
										STATS_SIZE.height())), STATS_POINT, 1.0f);
		initLayer(
				thiefStats = graphics().createImageLayer(
						statsImage.subImage(0, JobType.THIEF.ordinal() * STATS_SIZE.height(), STATS_SIZE.width(), STATS_SIZE.height())),
				STATS_POINT, 1.0f);
		initLayer(
				descriptionLayer = graphics().createImageLayer(
						descriptionImage.subImage(0, JobType.DEFAULT.ordinal() * DESCRIPTION_SIZE.height(), DESCRIPTION_SIZE.width(),
								DESCRIPTION_SIZE.height())), DESCRIPTION_POINT, 1.0f);
		initLayer(
				warriorDescription = graphics().createImageLayer(
						descriptionImage.subImage(0, JobType.WARRIOR.ordinal() * DESCRIPTION_SIZE.height(), DESCRIPTION_SIZE.width(),
								DESCRIPTION_SIZE.height())), DESCRIPTION_POINT, 1.0f);
		initLayer(
				mageDescription = graphics().createImageLayer(
						descriptionImage.subImage(0, JobType.BLACK_MAGE.ordinal() * DESCRIPTION_SIZE.height(), DESCRIPTION_SIZE.width(),
								DESCRIPTION_SIZE.height())), DESCRIPTION_POINT, 1.0f);
		initLayer(
				thiefDescription = graphics().createImageLayer(
						descriptionImage.subImage(0, JobType.THIEF.ordinal() * DESCRIPTION_SIZE.height(), DESCRIPTION_SIZE.width(),
								DESCRIPTION_SIZE.height())), DESCRIPTION_POINT, 1.0f);

	}

	private void initLayer(ImageLayer layer, IPoint point, float depth)
	{
		layer.setVisible(false);
		layer.setTranslation(point.x(), point.y());
		layer.setDepth(depth);
		graphics().rootLayer().add(layer);
	}

	// crea i bottoni del menu passandogli i relativi link in input.
	public void setButtons(Navigable loadingScreen, Navigable mainMenu)
	{
		styleMinus = new CharCreationButton(this, ButtonType.MINUS);
		styleMinus.getButtonLayer().setTranslation(HAIR_P1.x(), HAIR_P1.y());
		styleMinus.setHitBox(HAIR_P1);
		stylePlus = new CharCreationButton(this, ButtonType.PLUS);
		stylePlus.getButtonLayer().setTranslation(HAIR_P2.x(), HAIR_P2.y());
		stylePlus.setHitBox(HAIR_P2);
		colorMinus = new CharCreationButton(this, ButtonType.MINUS);
		colorMinus.getButtonLayer().setTranslation(HAIR_P3.x(), HAIR_P3.y());
		colorMinus.setHitBox(HAIR_P3);
		colorPlus = new CharCreationButton(this, ButtonType.PLUS);
		colorPlus.getButtonLayer().setTranslation(HAIR_P4.x(), HAIR_P4.y());
		colorPlus.setHitBox(HAIR_P4);
		accept = new ConfirmButton(loadingScreen, ButtonType.ACCEPT);
		accept.setTranslation(ACCEPT_POINT);
		back = new ConfirmButton(mainMenu, ButtonType.BACK);
		back.setTranslation(BACK_POINT);
		super.setPreviousMenu(mainMenu);
		super.setNextMenu(loadingScreen);
	}

	@Override
	public void setVisible(boolean visible)
	{
		prizeWindowLayer.setVisible(false);
		styleMinus.getButtonLayer().setVisible(visible);
		stylePlus.getButtonLayer().setVisible(visible);
		colorMinus.getButtonLayer().setVisible(visible);
		colorPlus.getButtonLayer().setVisible(visible);
		accept.setVisible(visible);
		back.setVisible(visible);
		frameLayer.setVisible(visible);
		charLayer.setVisible(visible);
		unselectedWarrior.setVisible(visible);
		unselectedMage.setVisible(visible);
		unselectedThief.setVisible(visible);
		selectedWarrior.setVisible(false);
		selectedMage.setVisible(false);
		selectedThief.setVisible(false);
		chosenWarrior.setVisible(false);
		chosenMage.setVisible(false);
		chosenThief.setVisible(false);
		statsLayer.setVisible(visible);
		warriorStats.setVisible(false);
		mageStats.setVisible(false);
		thiefStats.setVisible(false);
		descriptionLayer.setVisible(visible);
		warriorDescription.setVisible(false);
		mageDescription.setVisible(false);
		thiefDescription.setVisible(false);
		endName.setVisible(false);
		endPassword.setVisible(false);

		if (!visible)
			currentJob = JobType.DEFAULT;
	}

	public void setName()
	{
		if (name != null)
			name.destroy();
		name = new Text(currentName, Font.Style.PLAIN, 16, 0xFF000000);
		name.setTranslation(NAME_POINT);
		name.setDepth(1.0f);
		name.setVisible(true);
		name.init();
		endName.setTranslation(NAME_POINT.add(name.width(), -2.0f));
	}

	public void setPassword()
	{
		if (password != null)
			password.destroy();
		password = new Text(currentPassword, Font.Style.PLAIN, 16, 0xFF000000);
		password.setTranslation(PASSWORD_POINT);
		password.setDepth(1.0f);
		password.setVisible(true);
		password.init();
		endPassword.setTranslation(PASSWORD_POINT.add(password.width(), -2.0f));
	}

	@Override
	public Navigable onMouseDown(ButtonEvent event)
	{
		if (event.button() == Mouse.BUTTON_LEFT)
		{
			Point p = new Point(event.localX(), event.localY());

			if (prizeWindowLayer.visible() && (PRIZE_BUTTON.contains(p) || PRIZE_CROSS.contains(p)))
				prizeWindowLayer.setVisible(false);

			if (errorMessageLayer.visible() && p.x() >= ERROR_BUTTON_POINT.x() && p.y() >= ERROR_BUTTON_POINT.y()
					&& p.x() <= ERROR_BUTTON_POINT.x() + ERROR_BUTTON_SIZE.width()
					&& p.y() <= ERROR_BUTTON_POINT.y() + ERROR_BUTTON_SIZE.height())
				errorMessageLayer.setVisible(false);

			if (nameErrorMessageLayer.visible() && p.x() >= ERROR_BUTTON_POINT.x() && p.y() >= ERROR_BUTTON_POINT.y()
					&& p.x() <= ERROR_BUTTON_POINT.x() + ERROR_BUTTON_SIZE.width()
					&& p.y() <= ERROR_BUTTON_POINT.y() + ERROR_BUTTON_SIZE.height())
				nameErrorMessageLayer.setVisible(false);

			if (!errorMessageLayer.visible() && !nameErrorMessageLayer.visible() && !prizeWindowLayer.visible())
			{
				if (getGame().getPointerLocation().x() >= WARRIOR_POINT.x()
						&& getGame().getPointerLocation().x() <= WARRIOR_POINT.x() + JOB_SIZE.width()
						&& getGame().getPointerLocation().y() >= WARRIOR_POINT.y()
						&& getGame().getPointerLocation().y() <= WARRIOR_POINT.y() + JOB_SIZE.height())
				{
					currentChosen = chosenWarrior;
					currentStats = warriorStats;
					currentDescription = warriorDescription;
					swapVisibility(selectedWarrior, currentChosen);
					currentJob = JobType.WARRIOR;

					if (chosenMage.visible())
					{
						swapVisibility(chosenMage, unselectedMage);
						swapVisibility(mageStats, currentStats);
						swapVisibility(mageDescription, currentDescription);
					}

					if (chosenThief.visible())
					{
						swapVisibility(chosenThief, unselectedThief);
						swapVisibility(thiefStats, currentStats);
						swapVisibility(thiefDescription, currentDescription);
					}
				}

				else if (getGame().getPointerLocation().x() >= MAGE_POINT.x()
						&& getGame().getPointerLocation().x() <= MAGE_POINT.x() + JOB_SIZE.width()
						&& getGame().getPointerLocation().y() >= MAGE_POINT.y()
						&& getGame().getPointerLocation().y() <= MAGE_POINT.y() + JOB_SIZE.height())
				{
					currentChosen = chosenMage;
					currentStats = mageStats;
					currentDescription = mageDescription;
					swapVisibility(selectedMage, currentChosen);
					currentJob = JobType.BLACK_MAGE;

					if (chosenWarrior.visible())
					{
						swapVisibility(chosenWarrior, unselectedWarrior);
						swapVisibility(warriorStats, currentStats);
						swapVisibility(warriorDescription, currentDescription);
					}

					if (chosenThief.visible())
					{
						swapVisibility(chosenThief, unselectedThief);
						swapVisibility(thiefStats, currentStats);
						swapVisibility(thiefDescription, currentDescription);
					}
				}

				else if (getGame().getPointerLocation().x() >= THIEF_POINT.x()
						&& getGame().getPointerLocation().x() <= THIEF_POINT.x() + JOB_SIZE.width()
						&& getGame().getPointerLocation().y() >= THIEF_POINT.y()
						&& getGame().getPointerLocation().y() <= THIEF_POINT.y() + JOB_SIZE.height())
				{
					currentChosen = chosenThief;
					currentStats = thiefStats;
					currentDescription = thiefDescription;
					swapVisibility(selectedThief, currentChosen);
					currentJob = JobType.THIEF;

					if (chosenWarrior.visible())
					{
						swapVisibility(chosenWarrior, unselectedWarrior);
						swapVisibility(warriorStats, currentStats);
						swapVisibility(warriorDescription, currentDescription);
					}

					if (chosenMage.visible())
					{
						swapVisibility(chosenMage, unselectedMage);
						swapVisibility(mageStats, currentStats);
						swapVisibility(mageDescription, currentDescription);
					}

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

				else if (styleMinus.intersects(p))
				{
					currentHair = (currentHair - 1) % HAIR;
					if (currentHair < 0)
						currentHair = HAIR - 1;
					updateCharacter();
				}

				else if (stylePlus.intersects(p))
				{
					currentHair = (currentHair + 1) % (HAIR);
					updateCharacter();
				}

				else if (colorMinus.intersects(p))
				{
					currentColor = (currentColor - 1) % COLORS;
					if (currentColor < 0)
						currentColor = COLORS;
					updateCharacter();
				}

				else if (colorPlus.intersects(p))
				{
					currentColor = (currentColor + 1) % (COLORS + 1);
					updateCharacter();
				}

				else if (accept.intersects(p))
				{

					if (currentName.length() <= 2 || currentPassword.length() <= 2 || currentJob == JobType.DEFAULT)
						errorMessageLayer.setVisible(true);
					
					else
						getGame().setServerConnection(
								new ServerConnection(getGame().getServerAddress(), Language.EN, getGame().getGameName(), currentName));
				}

				else if (back.intersects(p))
				{
					currentColor = 0;
					currentHair = 0;
					updateCharacter();
					currentName = "";
					isTypingName = false;
					endName.setVisible(false);
					endName.setTranslation(NAME_POINT.add(0.0f, -2.0f));
					name.destroy();
					currentPassword = "";
					isTypingPassword = false;
					endPassword.setVisible(false);
					endPassword.setTranslation(PASSWORD_POINT.add(0.0f, -2.0f));
					password.destroy();

					return clickButton(back);
				}

				else
				{
					currentTime = 0.0f;
					endName.setVisible(false);
					isTypingName = false;
					endPassword.setVisible(false);
					isTypingPassword = false;
				}
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
		if (event.key() == Key.ESCAPE && !prizeWindowLayer.visible() && !errorMessageLayer.visible() && !nameErrorMessageLayer.visible())
		{
			currentColor = 0;
			currentHair = 0;
			updateCharacter();
			currentName = "";
			currentStats = statsLayer;
			currentDescription = descriptionLayer;
			isTypingName = false;
			endName.setVisible(false);
			endName.setTranslation(NAME_POINT.add(0.0f, -2.0f));
			name.destroy();
			currentPassword = "";
			isTypingPassword = false;
			endPassword.setVisible(false);
			endPassword.setTranslation(PASSWORD_POINT.add(0.0f, -2.0f));
			password.destroy();
			return clickButton(back);
		}

		if ((event.key() == Key.ENTER || event.key() == Key.ESCAPE) && prizeWindowLayer.visible() && !errorMessageLayer.visible())
		{
			prizeWindowLayer.setVisible(false);
			getGame().getKeyboard().remove(Key.ENTER);
			getGame().getKeyboard().remove(Key.ESCAPE);

		}

		else if ((event.key() == Key.ENTER || event.key() == Key.ESCAPE) && errorMessageLayer.visible() && !prizeWindowLayer.visible())
		{
			errorMessageLayer.setVisible(false);
			getGame().getKeyboard().remove(Key.ENTER);
			getGame().getKeyboard().remove(Key.ESCAPE);

		}

		else if (nameErrorMessageLayer.visible())
		{
			nameErrorMessageLayer.setVisible(false);
			getGame().getKeyboard().remove(Key.ENTER);
			getGame().getKeyboard().remove(Key.ESCAPE);
		}

		else if (event.key() == Key.ENTER && !prizeWindowLayer.visible())
		{
			if (currentName.length() <= 2 || currentPassword.length() <= 2 || currentJob == JobType.DEFAULT)
				errorMessageLayer.setVisible(true);

			else
				getGame().setServerConnection(
						new ServerConnection(getGame().getServerAddress(), Language.EN, getGame().getGameName(), currentName));
		}

		if (!prizeWindowLayer.visible() && !errorMessageLayer.visible() && isTypingName)
		{
			if (event.key() == Key.BACKSPACE && currentName.length() > 0)
			{
				name.destroy();
				currentName = currentName.substring(0, currentName.length() - 1);
			}

			if (event.key() == Key.TAB)
			{
				endPassword.setVisible(true);
				currentTime = 0.0f;
				isTypingPassword = true;
				endName.setVisible(false);
				isTypingName = false;
			}

			else if (currentName.length() < 14)
			{
				String key = event.key().toString().replace("Key.", "");
				if (key.length() == 1)
				{
					if (getGame().getKeyboard().contains(Key.SHIFT))
						currentName += key;
					else
						currentName += key.toLowerCase();
				}
			}

			setName();
		}

		else if (!errorMessageLayer.visible() && isTypingPassword)
		{
			if (event.key() == Key.BACKSPACE && currentPassword.length() > 0)
			{
				password.destroy();
				currentPassword = currentPassword.substring(0, currentPassword.length() - 1);
			}

			if (event.key() == Key.TAB)
			{
				endName.setVisible(true);
				currentTime = 0.0f;
				isTypingName = true;
				endPassword.setVisible(false);
				isTypingPassword = false;
			}

			else if (currentPassword.length() < 14)
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
			}

			setPassword();
		}

		return this;
	}

	// aggiorna l'aspetto corrente del personaggio creato.
	private void updateCharacter()
	{
		charLayer.destroy();
		charLayer = graphics().createImageLayer(
				assets().getImage(CHAR_TEMPLATE).subImage(currentColor * CHAR_SIZE.width(), currentHair * CHAR_SIZE.height(),
						CHAR_SIZE.width(), CHAR_SIZE.height()));
		charLayer.setTranslation(CHAR_POINT.x(), CHAR_POINT.y());
		charLayer.setDepth(2.0f);
		graphics().rootLayer().add(charLayer);
	}

	public void setNameExists()
	{
		waitForResp = true;
		final String username = currentName, psw = currentPassword;

		getGame().getServerConnection().register(username, psw, new Callback<String>()
		{
			@Override
			public void onSuccess(String resp)
			{
				if (!resp.equals("true"))
				{
					isLogging = true;
					waitForResp = false;
					nameExists = true;
				}
				
				else
				{
					getGame().getServerConnection().loginIn(username, psw, getGame().getRetriever(), new Callback<String>()
					{
						@Override
						public void onSuccess(String accounts)
						{
							if (accounts.contains("\"null\""))
								nameExists = true;
							
							isLogging = true;
							waitForResp = false;
						}

						@Override
						public void onFailure(Throwable t)
						{
							waitForResp = false;
						}
					});
				}
			}

			@Override
			public void onFailure(Throwable t)
			{
				waitForResp = false;
			}

		});
	}

	// returns the string associated with the selected character
	public String selectedCharacter()
	{
		if (currentHair == 0)
			return "00";
		return String.valueOf(currentHair) + "" + String.valueOf(currentColor);
	}

	private void swapVisibility(ImageLayer layer1, ImageLayer layer2)
	{
		layer1.setVisible(false);
		layer2.setVisible(true);
	}

	public static void clear()
	{
		frameLayer.destroy();
		charLayer.destroy();
		errorMessageLayer.destroy();
		nameErrorMessageLayer.destroy();
		unselectedWarrior.destroy();
		unselectedMage.destroy();
		unselectedThief.destroy();
		selectedWarrior.destroy();
		selectedMage.destroy();
		selectedThief.destroy();
		chosenWarrior.destroy();
		chosenMage.destroy();
		chosenThief.destroy();
		statsLayer.destroy();
		warriorStats.destroy();
		mageStats.destroy();
		thiefStats.destroy();
		descriptionLayer.destroy();
		warriorDescription.destroy();
		mageDescription.destroy();
		thiefDescription.destroy();
		prizeWindowLayer.destroy();
	}

	@Override
	public void update(int delta)
	{
		if (!errorMessageLayer.visible() && !nameErrorMessageLayer.visible() && !prizeWindowLayer.visible())
		{
			currentTime += delta;

			if (currentTime >= END_NAME_TIME && isTypingName)
			{
				endName.setVisible(!endName.visible());
				currentTime = 0.0f;
			}

			if (currentTime >= END_NAME_TIME && isTypingPassword)
			{
				endPassword.setVisible(!endPassword.visible());
				currentTime = 0.0f;
			}

			if (getGame().getPointerLocation().x() >= WARRIOR_POINT.x()
					&& getGame().getPointerLocation().x() <= WARRIOR_POINT.x() + JOB_SIZE.width()
					&& getGame().getPointerLocation().y() >= WARRIOR_POINT.y()
					&& getGame().getPointerLocation().y() <= WARRIOR_POINT.y() + JOB_SIZE.height())
			{
				if (unselectedWarrior.visible())
					swapVisibility(unselectedWarrior, selectedWarrior);

				if (statsLayer.visible())
					swapVisibility(statsLayer, warriorStats);

				if (descriptionLayer.visible())
					swapVisibility(descriptionLayer, warriorDescription);

				if (currentChosen != null && !chosenWarrior.visible())
				{
					swapVisibility(currentStats, warriorStats);
					swapVisibility(currentDescription, warriorDescription);
				}

			}

			else if (getGame().getPointerLocation().x() >= MAGE_POINT.x()
					&& getGame().getPointerLocation().x() <= MAGE_POINT.x() + JOB_SIZE.width()
					&& getGame().getPointerLocation().y() >= MAGE_POINT.y()
					&& getGame().getPointerLocation().y() <= MAGE_POINT.y() + JOB_SIZE.height())
			{
				if (unselectedMage.visible())
					swapVisibility(unselectedMage, selectedMage);

				if (statsLayer.visible())
					swapVisibility(statsLayer, mageStats);

				if (descriptionLayer.visible())
					swapVisibility(descriptionLayer, mageDescription);

				if (currentChosen != null && !chosenMage.visible())
				{
					swapVisibility(currentStats, mageStats);
					swapVisibility(currentDescription, mageDescription);
				}
			}

			else if (getGame().getPointerLocation().x() >= THIEF_POINT.x()
					&& getGame().getPointerLocation().x() <= THIEF_POINT.x() + JOB_SIZE.width()
					&& getGame().getPointerLocation().y() >= THIEF_POINT.y()
					&& getGame().getPointerLocation().y() <= THIEF_POINT.y() + JOB_SIZE.height())
			{
				if (unselectedThief.visible())
					swapVisibility(unselectedThief, selectedThief);

				if (statsLayer.visible())
					swapVisibility(statsLayer, thiefStats);

				if (descriptionLayer.visible())
					swapVisibility(descriptionLayer, thiefDescription);

				if (currentChosen != null && !chosenThief.visible())
				{
					swapVisibility(currentStats, thiefStats);
					swapVisibility(currentDescription, thiefDescription);
				}
			}

			else
			{
				if (selectedWarrior.visible() && !chosenWarrior.visible())
				{
					swapVisibility(selectedWarrior, unselectedWarrior);
					warriorStats.setVisible(false);
					warriorDescription.setVisible(false);
				}

				if (selectedMage.visible() && !chosenMage.visible())
				{
					swapVisibility(selectedMage, unselectedMage);
					mageStats.setVisible(false);
					mageDescription.setVisible(false);
				}

				if (selectedThief.visible() && !chosenThief.visible())
				{
					swapVisibility(selectedThief, unselectedThief);
					thiefStats.setVisible(false);
					thiefDescription.setVisible(false);
				}

				if (currentChosen != null)
				{
					currentStats.setVisible(true);
					currentDescription.setVisible(true);
				}

				else
				{
					statsLayer.setVisible(true);
					descriptionLayer.setVisible(true);
				}

				if (!waitForResp && getGame().getServerConnection() != null && !isLogging)
					setNameExists();

				if (isLogging)
				{
					isLogging = false;

					if (nameExists)
					{
						nameExists = false;
						getGame().setServerConnection(null);
						nameErrorMessageLayer.setVisible(true);
					}

					else
					{
						// getGame().getServerConnection().saveGlobalState("accounts",
						// currentName, currentPassword);
						Player.loadCharacter(selectedCharacter());
						Player.setName(currentName);
						Player.setJob(currentJob);
						Player.setAspect(selectedCharacter());
						currentColor = 0;
						currentHair = 0;
						updateCharacter();
						currentName = "";
						isTypingName = false;
						endName.setVisible(false);
						endName.setTranslation(NAME_POINT.add(0.0f, -2.0f));
						name.destroy();
						getGame().getServerConnection().saveState(PlayerStats.saveDefault(currentName, currentJob));
						getGame().getGameLoop().setTutorial(true);
						getGame().setGameState(clickButton(accept, Player.getName()));
					}

				}
			}
		}
	}
}
