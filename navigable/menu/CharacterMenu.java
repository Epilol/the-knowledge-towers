package saga.progetto.tesi.navigable.menu;

import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;
import java.util.LinkedList;
import java.util.List;
import playn.core.AssetWatcher;
import playn.core.Font.Style;
import playn.core.Mouse.ButtonEvent;
import playn.core.Image;
import playn.core.ImageLayer;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import pythagoras.f.IPoint;
import pythagoras.f.Point;
import saga.progetto.tesi.core.TheKnowledgeTowers;
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.entity.dynamicentity.Player.Statistic;
import saga.progetto.tesi.media.Text;
import saga.progetto.tesi.navigable.Gameloop;
import saga.progetto.tesi.navigable.Navigable;

// la classe rappresenta il menu di creazione del personaggio.
public class CharacterMenu extends GameMenu
{
	private static final String STATS_PATH =  "images/menu/game_menu/bars.png";
	private static final String EXP_PATH =  "images/menu/game_menu/exp_bars.png";
	private static final String LEVEL_UP_PATH =  "images/menu/game_menu/level_up.png";
	private static final String FACE_PATH = "images/menu/game_menu/face.png";
	private static final IPoint HEALTH_POINT = new Point(200.0f, 150.0f);
	private static final IPoint END_POINT = new Point(200.0f, 175.0f);
	private static final IPoint MANA_POINT = new Point(360.0f, 150.0f);
	private static final IPoint STATS_POINT = new Point(115.0f, 260.0f);
	private static final IPoint EXP_POINT = new Point(115.0f, 310.0f);
	private static final IPoint STATS_INCREASE_POINT = new Point(360.0f, 466.0f);
	private static final IPoint FACE_POINT = new Point(93.0f, 121.0f);
	private static final IDimension STATS_SIZE = new Dimension(114.0f, 8.0f);
	private static final IDimension EXP_SIZE = new Dimension(356.0f, 8.0f);
	private static final IDimension LEVEL_UP_SIZE = new Dimension(13.0f, 21.0f);
	private static final int BUTTON_NUMBERS = 8;
	private static final int MENU_INDEX = 0;
	private static Image statsImage;
	private static Image expImage;
	private static Image levelUpImage;
	private static Image faceImage;
	
	private List<Text> staticText;
	private Text healthText;
	private Text endText;
	private Text manaText;
	private Text strengthText;
	private Text dexterityText;
	private Text intelligenceText;
	private Text expText;
	private Text pointsToSpendText;
	private ImageLayer healthBg;
	private ImageLayer endBg;
	private ImageLayer manaBg;
	private ImageLayer healthLayer;
	private ImageLayer endLayer;
	private ImageLayer manaLayer;
	private ImageLayer expBg;
	private ImageLayer expLayer;
	private ImageLayer strengthUpLayer;
	private ImageLayer dexterityUpLayer;
	private ImageLayer intelligenceUpLayer;
	private ImageLayer faceLayer;
	
	public CharacterMenu(TheKnowledgeTowers game, Player player, Gameloop gameLoop)
	{
		super(game, player, gameLoop);
		staticText = new LinkedList<Text>();
		init();
		strengthUpLayer = graphics().createImageLayer(levelUpImage);
		strengthUpLayer.setVisible(false);
		strengthUpLayer.setTranslation(STATS_POINT.x() + 200.0f, STATS_POINT.y() + 106.0f);
		strengthUpLayer.setDepth(12.0f);
		dexterityUpLayer = graphics().createImageLayer(levelUpImage);
		dexterityUpLayer.setVisible(false);
		dexterityUpLayer.setTranslation(STATS_POINT.x() + 200.0f, STATS_POINT.y() + 156.0f);
		dexterityUpLayer.setDepth(12.0f);
		intelligenceUpLayer = graphics().createImageLayer(levelUpImage);
		intelligenceUpLayer.setVisible(false);
		intelligenceUpLayer.setTranslation(STATS_POINT.x() + 200.0f, STATS_POINT.y() + 206.0f);
		intelligenceUpLayer.setDepth(12.0f);
		faceLayer = graphics().createImageLayer(faceImage);
		faceLayer.setVisible(false);
		faceLayer.setTranslation(FACE_POINT.x(), FACE_POINT.y());
		faceLayer.setDepth(12.0f);
		graphics().rootLayer().add(strengthUpLayer);
		graphics().rootLayer().add(dexterityUpLayer);
		graphics().rootLayer().add(intelligenceUpLayer);
		graphics().rootLayer().add(faceLayer);
	}
	
	public enum MenuBar
	{
		BG(0), HEALTH(1), JOB_EXP(1), END(2), EXP(2), MANA(3);
		
		private int index;
		
		MenuBar(int index)
		{
			this.index = index;
		}
		
		public int getIndex()
		{
			return index;
		}
	}
	
	public static void loadAssets(AssetWatcher watcher)
	{
		statsImage = assets().getImage(STATS_PATH);
		expImage = assets().getImage(EXP_PATH);
		levelUpImage = assets().getImage(LEVEL_UP_PATH);
		faceImage = assets().getImage(FACE_PATH);
		watcher.add(statsImage);
		watcher.add(expImage);
		watcher.add(levelUpImage);
		watcher.add(faceImage);
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
		staticText.add(initText("Character Menu", 80.0f, 50.0f, 22, false));
		staticText.add(initText(Player.getName(), 200.0f, 125.0f, 16, false));
	}

	public void initBars()
	{
		healthBg = graphics().createImageLayer(statsImage.subImage(0, STATS_SIZE.height() * MenuBar.BG.getIndex(), 
				STATS_SIZE.width(), STATS_SIZE.height())); 
		healthBg.setDepth(11.0f);
		healthBg.setTranslation(HEALTH_POINT.x() - 3.0f, HEALTH_POINT.y() + 20.0f);
		healthLayer = graphics().createImageLayer(statsImage.subImage(0, STATS_SIZE.height() * MenuBar.HEALTH.getIndex(), 
				(getPlayer().getCurrentLife() * STATS_SIZE.width()) / getPlayer().getTotalLife(), STATS_SIZE.height()));
		healthLayer.setDepth(12.0f);
		healthLayer.setTranslation(HEALTH_POINT.x() - 3.0f, HEALTH_POINT.y() + 20.0f);
		endBg =	graphics().createImageLayer(statsImage.subImage(0, STATS_SIZE.height() * MenuBar.BG.getIndex(), 
				STATS_SIZE.width(), STATS_SIZE.height())); 
		endBg.setDepth(11.0f);
		endBg.setTranslation(END_POINT.x() - 3.0f, END_POINT.y() + 20.0f);
		endLayer = graphics().createImageLayer(statsImage.subImage(0, STATS_SIZE.height() * MenuBar.END.getIndex(), 
				(getPlayer().getCurrentEndurance() * STATS_SIZE.width()) / getPlayer().getTotalEndurance(), STATS_SIZE.height()));
		endLayer.setDepth(12.0f);
		endLayer.setTranslation(END_POINT.x() - 3.0f, END_POINT.y() + 20.0f);
		manaBg = graphics().createImageLayer(statsImage.subImage(0, STATS_SIZE.height() * MenuBar.BG.getIndex(), 
				STATS_SIZE.width(), STATS_SIZE.height())); 
		manaBg.setDepth(11.0f);
		manaBg.setTranslation(MANA_POINT.x() - 3.0f, MANA_POINT.y() + 20.0f);
		manaLayer = graphics().createImageLayer(statsImage.subImage(0, STATS_SIZE.height() * MenuBar.MANA.getIndex(), 
				(getPlayer().getCurrentMana() * STATS_SIZE.width()) / getPlayer().getTotalMana(), STATS_SIZE.height()));
		manaLayer.setDepth(12.0f);
		manaLayer.setTranslation(MANA_POINT.x() - 3.0f, MANA_POINT.y() + 20.0f);
		expBg = graphics().createImageLayer(expImage.subImage(0, EXP_SIZE.height() * MenuBar.BG.getIndex(), 
				EXP_SIZE.width(), EXP_SIZE.height())); 
		expBg.setDepth(11.0f);
		expBg.setTranslation(EXP_POINT.x() - 2.0f, EXP_POINT.y() + 20.0f);
		expLayer = graphics().createImageLayer(expImage.subImage(0, EXP_SIZE.height() * MenuBar.EXP.getIndex(), 
				(getPlayer().getCurrentExperience() * EXP_SIZE.width()) / getPlayer().getTotalExperience(), EXP_SIZE.height())); 
		expLayer.setDepth(12.0f);
		expLayer.setTranslation(EXP_POINT.x() - 2.0f, EXP_POINT.y() + 20.0f);
		
		graphics().rootLayer().add(healthBg);
		graphics().rootLayer().add(endBg);
		graphics().rootLayer().add(manaBg);
		graphics().rootLayer().add(healthLayer);
		graphics().rootLayer().add(endLayer);
		graphics().rootLayer().add(manaLayer);
		graphics().rootLayer().add(expBg);
		graphics().rootLayer().add(expLayer);
	}
	
	public Text initDynamicText(String textMessage, float x, float y)
	{
		String currentStat = "";
		
		if (textMessage.equals("HP"))
			currentStat = String.valueOf(getPlayer().getCurrentLife()).replaceAll("\\..*$", "") + "/" + 
					String.valueOf(getPlayer().getTotalLife()).replaceAll("\\..*$", "");
			
		else if (textMessage.equals("END"))
			currentStat = String.valueOf(getPlayer().getCurrentEndurance()).replaceAll("\\..*$", "") + "/" + 
					String.valueOf(getPlayer().getTotalEndurance()).replaceAll("\\..*$", "");
		
		else if (textMessage.equals("MP"))
			currentStat = String.valueOf(getPlayer().getCurrentMana()).replaceAll("\\..*$", "") + "/" + 
					String.valueOf(getPlayer().getTotalMana()).replaceAll("\\..*$", "");
		
		else if (textMessage.equals("Experience"))
			currentStat = String.valueOf(getPlayer().getCurrentExperience()).replaceAll("\\..*$", "") + "/" + 
					String.valueOf(getPlayer().getTotalExperience()).replaceAll("\\..*$", "");
		
		else if (textMessage.equals("Strength"))
			currentStat = String.valueOf(getPlayer().getStrength()).replaceAll("\\..*$", "");
		
		else if (textMessage.equals("Dexterity"))
			currentStat = String.valueOf(getPlayer().getDexterity()).replaceAll("\\..*$", "");
		
		else if (textMessage.equals("Intelligence"))
			currentStat = String.valueOf(getPlayer().getIntelligence()).replaceAll("\\..*$", "");
		
		if (textMessage.equals("Experience"))
			return initText("Experience Level:    " + getPlayer().getLevel() + "                         " + currentStat, x + 10, y + 7, 16, true);
		
		Text newText = null;
		
		if (staticText.size() < BUTTON_NUMBERS)
		{
			newText = initText(textMessage, x + 10, y + 7, 16, true);
			staticText.add(newText);
		}
		
		Text textStat = new Text(currentStat, Style.PLAIN, 16, 0xFFFFFFFF);
		
		if (textMessage.equals("HP") || textMessage.equals("END") || textMessage.equals("MP"))
			textStat.setTranslation(x + 60.0f, y + 7);
		else
			textStat.setTranslation(x + 160.0f, y + 7);
		
		textStat.setDepth(14.0f);
		textStat.setVisible(true);
		textStat.init();
		
		return textStat;
		
	}
	
	@Override
	public Navigable onMouseDown(ButtonEvent event) 
	{
		IPoint p = new Point(event.localX(), event.localY());
		
		if (p.x() >= strengthUpLayer.tx() && p.x() <= strengthUpLayer.tx() + LEVEL_UP_SIZE.width() && 
				p.y() >= strengthUpLayer.ty() && p.y() <= strengthUpLayer.ty() + LEVEL_UP_SIZE.height() && getPlayer().canTrain())
		{
			getPlayer().train(Statistic.STRENGTH);
			getPlayer().setCurrentLife(getPlayer().getTotalLife());
			setVisible(false);
			setVisible(true);
		}
		
		if (p.x() >= dexterityUpLayer.tx() && p.x() <= dexterityUpLayer.tx() + LEVEL_UP_SIZE.width() && 
				p.y() >= dexterityUpLayer.ty() && p.y() <= dexterityUpLayer.ty() + LEVEL_UP_SIZE.height() && getPlayer().canTrain())
		{
			getPlayer().train(Statistic.DEXTERITY);
			getPlayer().setCurrentEndurance(getPlayer().getTotalEndurance());
			setVisible(false);
			setVisible(true);
		}
		
		if (p.x() >= intelligenceUpLayer.tx() && p.x() <= intelligenceUpLayer.tx() + LEVEL_UP_SIZE.width() && 
				p.y() >= intelligenceUpLayer.ty() && p.y() <= intelligenceUpLayer.ty() + LEVEL_UP_SIZE.height() && getPlayer().canTrain())
		{
			setVisible(false);
			getPlayer().train(Statistic.INTELLIGENCE);
			getPlayer().setCurrentMana(getPlayer().getTotalMana());
			setVisible(true);
		}
		return super.onMouseDown(event);
	}
	
	@Override
	public void setVisible(boolean visible)
	{
		super.setVisible(visible);
		
		faceLayer.setVisible(visible);
		strengthUpLayer.setVisible(getPlayer().canTrain() && visible);
		dexterityUpLayer.setVisible(getPlayer().canTrain() && visible);
		intelligenceUpLayer.setVisible(getPlayer().canTrain() && visible);
		
		for (Text text : staticText)
			text.setVisible(visible);
		
		if (visible)
		{
			initBars();
			healthText = initDynamicText("HP", HEALTH_POINT.x(), HEALTH_POINT.y());
			endText = initDynamicText("END", END_POINT.x(), END_POINT.y());
			manaText = initDynamicText("MP", MANA_POINT.x(), MANA_POINT.y());
			strengthText = initDynamicText("Strength", STATS_POINT.x(), STATS_POINT.y() + 100.0f);
			dexterityText = initDynamicText("Dexterity", STATS_POINT.x(), STATS_POINT.y() + 150.0f);
			intelligenceText = initDynamicText("Intelligence", STATS_POINT.x(), STATS_POINT.y() + 200.0f);
			expText = initDynamicText("Experience", EXP_POINT.x(), EXP_POINT.y());
			pointsToSpendText = new Text("Points to spend: " + String.valueOf(getPlayer().getFreePoints()).replaceAll("\\..*$", ""), 
					Style.BOLD, 14, 0xFFFF1111);
			pointsToSpendText.setDepth(13.0f);
			pointsToSpendText.setTranslation(STATS_INCREASE_POINT);
			pointsToSpendText.init();
			
			if (getPlayer().getFreePoints() > 0.0f)
				pointsToSpendText.setVisible(true);
		}
		
		else
			destroy();
	}
	
	public void destroy()
	{
		healthBg.destroy();
		endBg.destroy();
		manaBg.destroy();
		healthLayer.destroy();
		endLayer.destroy();
		manaLayer.destroy();
		expBg.destroy();
		expLayer.destroy();
		healthText.destroy();
		endText.destroy();
		manaText.destroy();
		strengthText.destroy();
		dexterityText.destroy();
		intelligenceText.destroy();
		expText.destroy();
		pointsToSpendText.destroy();
	}
}