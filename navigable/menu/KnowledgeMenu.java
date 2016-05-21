package saga.progetto.tesi.navigable.menu;

import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;
import java.util.LinkedList;
import java.util.List;
import playn.core.AssetWatcher;
import playn.core.Font;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Mouse.ButtonEvent;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import pythagoras.f.IPoint;
import pythagoras.f.Point;
import saga.progetto.tesi.core.TheKnowledgeTowers;
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.entity.staticentity.Page;
import saga.progetto.tesi.media.Text;
import saga.progetto.tesi.navigable.Gameloop;
import saga.progetto.tesi.navigable.Navigable;

public class KnowledgeMenu extends GameMenu
{
	private static final String LEFT_ARROW_PATH =  "images/menu/game_menu/left_arrow.png";
	private static final String RIGHT_ARROW_PATH =  "images/menu/game_menu/right_arrow.png";
	public static final IPoint IMAGE_POINT = new Point(74.0f, 101.0f);
	private static final IPoint LEFT_ARROW_POINT = new Point(232.0f, 435.0f);
	private static final IPoint RIGHT_ARROW_POINT = new Point(332.0f, 435.0f);
	private static final IDimension BOX_SIZE = new Dimension(433.0f, 398.0f);
	private static final int MENU_INDEX = 3;
	private static Image leftArrowImage;
	private static Image rightArrowImage;
	
	private ImageLayer leftArrowLayer;
	private ImageLayer rightArrowLayer;
	private List<Text> staticText;
	private Text pageMissing;
	private Page currentPage;
	private int index;
	
	public KnowledgeMenu(TheKnowledgeTowers game, Player player, Gameloop gameloop)
	{
		super(game, player, gameloop);
		staticText = new LinkedList<Text>();
		leftArrowLayer = graphics().createImageLayer(leftArrowImage);
		leftArrowLayer.setVisible(false);
		leftArrowLayer.setTranslation(LEFT_ARROW_POINT.x(), LEFT_ARROW_POINT.y());
		leftArrowLayer.setDepth(12.0f);
		rightArrowLayer = graphics().createImageLayer(rightArrowImage);
		rightArrowLayer.setVisible(false);
		rightArrowLayer.setTranslation(RIGHT_ARROW_POINT.x(), RIGHT_ARROW_POINT.y());
		rightArrowLayer.setDepth(12.0f);
		graphics().rootLayer().add(leftArrowLayer);
		graphics().rootLayer().add(rightArrowLayer);
		pageMissing = new Text("you haven't found any images yet", Font.Style.PLAIN, 14, 0xFFFFFFFF);
		pageMissing.setTranslation(IMAGE_POINT.x() + (BOX_SIZE.width() - pageMissing.width()) / 2,
				IMAGE_POINT.y() + (BOX_SIZE.height() - pageMissing.height()) / 2);
		pageMissing.setDepth(12.0f);
		pageMissing.init();
		init();
	}
	
	public static void loadAssets(AssetWatcher watcher)
	{
		leftArrowImage = assets().getImage(LEFT_ARROW_PATH);
		rightArrowImage = assets().getImage(RIGHT_ARROW_PATH);
		watcher.add(leftArrowImage);
		watcher.add(rightArrowImage);
	}
	
	@Override
	public void init()
	{
		super.init();
		staticText.add(initText("Knowledge Menu", 80.0f, 50.0f, 22, false));
	}
	
	@Override
	public int getMenuIndex()
	{
		return MENU_INDEX;
	}
	
	private boolean hasPages()
	{
		return getPlayer().getPages().size() > 0;
	}
	
	private boolean hasPages(int number)
	{
		return getPlayer().getPages().size() > number;
	}
	
	public Page getPage()
	{
		return getPlayer().getPages().get(index);
	}
	
	@Override
	public void setVisible(boolean visible)
	{
		super.setVisible(visible);
		
		if (hasPages())
		{
			leftArrowLayer.setVisible(visible);
			rightArrowLayer.setVisible(visible);
		}
		
		else
			pageMissing.setVisible(visible);
		
		if (visible)
		{
			if (hasPages())
				currentPage = getPage();
			
			for (Page page : getPlayer().getPages())
				if (page.getImageLayer() != null)
					adjustLayer(page.getImageLayer());
		}
		
		if (currentPage != null)
		{
			if (currentPage.getImageLayer() != null)
				currentPage.getImageLayer().setVisible(visible);
		}
		
		for (Text text : staticText)
			text.setVisible(visible);
	}
	
	@Override
	public Navigable onMouseDown(ButtonEvent event)
	{
		IPoint p = new Point(event.localX(), event.localY());
		if (p.x() >= leftArrowLayer.tx() && p.x() <= leftArrowLayer.tx() + leftArrowLayer.width() && 
				p.y() >= leftArrowLayer.ty() && p.y() <= leftArrowLayer.ty() + leftArrowLayer.height() && index > 0)
		{
			getPage().getImageLayer().setVisible(false);
			index--;
			currentPage = getPage();
			getPage().getImageLayer().setVisible(true);
		}
		
		if (p.x() >= rightArrowLayer.tx() && p.x() <= rightArrowLayer.tx() + rightArrowLayer.width() && 
				p.y() >= rightArrowLayer.ty() && p.y() <= rightArrowLayer.ty() + rightArrowLayer.height() && hasPages(index + 1))
		{
			getPage().getImageLayer().setVisible(false);
			index++;
			currentPage = getPage();
			getPage().getImageLayer().setVisible(true);
		}
		return super.onMouseDown(event);
	}
	
	public static void adjustLayer(ImageLayer layer)
	{
		layer.setTranslation(IMAGE_POINT.x() + (BOX_SIZE.width() - layer.width() / 2) / 2,
				IMAGE_POINT.y() + (BOX_SIZE.height() - layer.height() / 2) / 2);
		layer.setScale(0.5f);
		layer.setDepth(11.0f);
	}
	
	public static void adjustText(Text text)
	{
		text.getLayer().setTranslation(IMAGE_POINT.x() + (BOX_SIZE.width() - text.getLayer().width()) / 2,
				IMAGE_POINT.y() + 20.0f);
	}

}