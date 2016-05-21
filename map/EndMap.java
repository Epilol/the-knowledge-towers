package saga.progetto.tesi.map;

import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;
import java.util.Map;
import playn.core.AssetWatcher;
import playn.core.Font;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Key;
import playn.core.util.Callback;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import pythagoras.f.IPoint;
import pythagoras.f.Point;
import saga.progetto.tesi.core.TheKnowledgeTowers;
import saga.progetto.tesi.entity.Animation;
import saga.progetto.tesi.entity.dynamicentity.Direction;
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.entity.dynamicentity.spell.EndTowerArea;
import saga.progetto.tesi.entity.staticentity.Page;
import saga.progetto.tesi.media.Text;
import saga.progetto.tesi.navigable.Gameloop;
import saga.progetto.tesi.navigable.menu.JobMenu;

public abstract class EndMap extends GameMap
{
	public static final int HEIGHT = 35;
	public static final int WIDTH = 35;
	private static final String END_TOWER_PATH = "images/decoration/end_tower.png";
	private static final String COMPLETED_MENU_PATH = "images/gui/tower_completed.png";
	private static final String FAILED_MENU_PATH = "images/gui/tower_failed.png";
	private static final IPoint END_LAYER_POINT = new Point(337.0f, 240.0f);
	private static final IPoint END_MENU_POINT = new Point(150.0f, 65.0f);
	private static final IPoint END_BUTTON_POINT = new Point(306.0f, 393.0f);
	private static final IDimension END_BUTTON_SIZE = new Dimension(187.0f, 22.0f);
	private static final int POINTS_PER_PAGE = 2100;
	private static final int MAX_NUMBER_OF_PAGES = 20;
	private static final String VICTORY_TITLE = "CONGRATULATIONS!";
	private static final String VICTORY_MESSAGE = "You gained enough knowledge to teleport to the next \ntower.";
	private static final String LOSS_TITLE = "YOU FAILED";
	private static final String LOSS_MESSAGE = "You didn't gain enough knowledge to teleport to the next \ntower. You will repeat the level.";
	private static final String POINTS_MESSAGE = "Knowledge earned: ";
	private static Image endTowerImage;
	private static Image completedMenuImage;
	private static Image failedMenuImage;
	
	private ImageLayer endTowerLayer;
	private ImageLayer endMenuLayer;
	private Text endMenuTitle;
	private Text endMenuMessage;
	private Text endMenuPoints;
	private EndTowerArea endTower;
	private int numberOfPages;
	private float score;
	private boolean isReadyToLoad;
	
	public EndMap(Player player, TheKnowledgeTowers game, String mapId, String previousMap, Gameloop gameloop)
	{
		super(player, game, mapId, gameloop);
		gameloop.setHasShowedTowerPopup(false);
		mapInit(game.getMapData(mapId), previousMap);
		endTowerLayer = graphics().createImageLayer(endTowerImage);
		endTowerLayer.setDepth(1.0f);
		endTowerLayer.setTranslation(END_LAYER_POINT.x(), END_LAYER_POINT.y());
		getFloorLayer().add(endTowerLayer);
		endTower = new EndTowerArea(END_LAYER_POINT.x() + 190.0f, END_LAYER_POINT.y() + 266.0f, getPlayer());
		endTower.applyEffect();
		player.setHasPassed(true);
		
		if (player.getStatistic().hasPassed())
			endMenuLayer = graphics().createImageLayer(completedMenuImage);
		
		else
			endMenuLayer = graphics().createImageLayer(failedMenuImage);
		
		endMenuLayer.setTranslation(END_MENU_POINT.x(), END_MENU_POINT.y());
		endMenuLayer.setDepth(8.0f);
		endMenuLayer.setVisible(false);
		graphics().rootLayer().add(endMenuLayer);
		numberOfPages = getPlayer().getNegativeDiscarded();
		
		if (numberOfPages >= MAX_NUMBER_OF_PAGES)
			numberOfPages = MAX_NUMBER_OF_PAGES;
		
		for (Page page : getPlayer().getPages())
			page.send(true, getAnnotationProtocol());
		
		score = ((60 * getPlayer().getStatistic().score() - 30) * POINTS_PER_PAGE) + numberOfPages * POINTS_PER_PAGE;
		
		if (player.getStatistic().hasPassed())
		{
			if (player.getPlayerStats().getBestScore() < score)
				player.getPlayerStats().setBestScore(score);

			player.getPlayerStats().increaseTowerCompleted();
		}
	}
	
	public static void loadAssets(AssetWatcher watcher)
	{
		endTowerImage = assets().getImage(END_TOWER_PATH);
		watcher.add(endTowerImage);
		completedMenuImage = assets().getImage(COMPLETED_MENU_PATH);
		watcher.add(completedMenuImage);
		failedMenuImage = assets().getImage(FAILED_MENU_PATH);
		watcher.add(failedMenuImage);
	}
	
	public void initMenuMessage(boolean hasPassed)
	{
		
		if (hasPassed)
		{
			endMenuTitle = new Text(VICTORY_TITLE, Font.Style.BOLD, 25, 0xFFFFFFFF, 0xFF000000);
			endMenuMessage = new Text(VICTORY_MESSAGE, Font.Style.BOLD, 16, 0xFFFFFFFF, 0xFF000000);
			getPlayer().setCurrentTower(getPlayer().getCurrentTower() + 1);
			getPlayer().save();
			updateState();
		}
		
		else
		{
			endMenuTitle = new Text(LOSS_TITLE, Font.Style.BOLD, 25, 0xFFFFFFFF, 0xFF000000);
			endMenuMessage = new Text(LOSS_MESSAGE, Font.Style.BOLD, 14, 0xFFFFFFFF, 0xFF000000);
		}
		
		endMenuTitle.setTranslation(END_MENU_POINT.add(endMenuLayer.width() / 2 - endMenuTitle.width() / 2, 20.0f));
		endMenuTitle.setVisible(true);
		endMenuTitle.setDepth(9.0f);
		endMenuMessage.setTranslation(END_MENU_POINT.add(20.0f, endMenuTitle.height() + 30.0f));
		endMenuMessage.setVisible(true);
		endMenuMessage.setDepth(9.0f);
		endMenuPoints = new Text(POINTS_MESSAGE + String.valueOf(score).replaceAll("\\..*$", ""),
									Font.Style.BOLD, 16, 0xFFFFFFFF, 0xFF000000);
		endMenuPoints.setTranslation(END_MENU_POINT.add(20.0f, endMenuTitle.height() + endMenuMessage.height() + 230.0f));
		endMenuPoints.setVisible(true);
		endMenuPoints.setDepth(9.0f);
		endMenuTitle.init();
		endMenuMessage.init();
		endMenuPoints.init();
	}
	
	private void updateState()
	{
		getGame().getServerConnection().getState(new Callback<Map<String, Map<String, String>>>()
		{
			@Override
			public void onSuccess(Map<String, Map<String, String>> namespace)
			{
				getGame().setState(namespace);
			}

			@Override
			public void onFailure(Throwable t)
			{
			}
		});
	}

	@Override
	public void clear()
	{
		super.clear();
		endTowerLayer.destroy();
		endTower.clear();
		endMenuLayer.destroy();
		endMenuTitle.destroy();
		endMenuMessage.destroy();
		endMenuPoints.destroy();
	}
	
	@Override
	public int getWidth()
	{
		return WIDTH;
	}

	@Override
	public int getHeight()
	{
		return HEIGHT;
	}
	
	@Override
	public void update(int delta)
	{
		super.update(delta);
		endTower.update(delta);
		getPlayer().setSpeedModifier(getPlayer().getDefaultSpeed() / 2);
		
		if (endMenuLayer.visible() && ((getGame().getPointerLocation().x() >= END_BUTTON_POINT.x() && getGame().getPointerLocation().y() >= 
				END_BUTTON_POINT.y() && getGame().getPointerLocation().x() <= END_BUTTON_POINT.x() + END_BUTTON_SIZE.width() && 
				getGame().getPointerLocation().y() <= END_BUTTON_POINT.y() + END_BUTTON_SIZE.height() && getGame().isMouseDown()) ||
				getGame().getKeyboard().contains(Key.ENTER)))
		{
			isReadyToLoad = true;
			endMenuLayer.setVisible(false);
			endMenuTitle.setVisible(false);
			endMenuMessage.setVisible(false);
			endMenuPoints.setVisible(false);
		}
		
		if(isReadyToLoad && getPlayer().getCurrentJob().isRewardFinished())
		{
			isReadyToLoad = false;
			getPlayer().getMap().getLocations().clear();
			getPlayer().getMap().getDirections().clear();
			getGameloop().setVisible(false);
			getGameloop().setPreviousMap(getGameloop().getCurrentMap());
			getGameloop().setCurrentMap(getMapId());
			getGameloop().resetRespawnInfo();
			getPlayer().updateJobInfo();
			JobMenu.addTotalPages(numberOfPages);
			getPlayer().resetPages();
			getPlayer().save();
			
			if (!Player.getName().equals("Admin") && !Player.getName().equals("Adminrule"))
				getPlayer().globalSave();
			
			if (getPlayer().getCurrentTower() > NUMBER_OF_TOWERS)
			{
				getPlayer().setNewGame(getPlayer().getNewGame() + 1);
				getPlayer().setCurrentTower(1);
			}
			
			getGameloop().setLoadingAnnotation(true);
			getGameloop().loadNextMap("start" + getPlayer().getCurrentTower());
			
		}
		
		if (!isReadyToLoad && getContactListener().isColliding(endTower.getBody(), getPlayer().getBody()) && !endMenuLayer.visible())
		{
			endMenuLayer.setVisible(true);
			initMenuMessage(getPlayer().getStatistic().hasPassed());
			getPlayer().getCurrentJob().gainJobExp(60 * getPlayer().getStatistic().score() - 30 + numberOfPages * 0.8f);
			getPlayer().setCurrentAnimation(Animation.IDLE);
			getPlayer().setLastDirection(Direction.DEFAULT);
			getPlayer().getSprite().setLastDirection(Direction.UP);
		}
		
		else if (!getContactListener().isColliding(endTower.getBody(), getPlayer().getBody()) && endMenuLayer.visible())
			endMenuLayer.setVisible(false);
	}
}
