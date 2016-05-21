package saga.progetto.tesi.navigable.menu;

import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;
import playn.core.AssetWatcher;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Font.Style;
import playn.core.Mouse.ButtonEvent;
import pythagoras.f.Circle;
import pythagoras.f.Dimension;
import pythagoras.f.ICircle;
import pythagoras.f.IDimension;
import pythagoras.f.IPoint;
import pythagoras.f.IRectangle;
import pythagoras.f.Point;
import pythagoras.f.Rectangle;
import saga.progetto.tesi.core.TheKnowledgeTowers;
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.media.Text;

public abstract class Inventory extends NewGameMenu
{
	
	private static final String GOLDS_PATH =  "images/menu/game_menu/inventory/gold_menu.png";
	private static final IPoint GOLDS_POINT = new Point(626.0f, 427.0f);
	private static final IPoint GOLDS_TEXT_POINT = new Point(746.0f, 432.0f);
	private static final ICircle CROSS = new Circle(777.0f, 275.0f, 12.0f);
	private static final String SELECTION_PATH = "images/menu/game_menu/inventory/selected_item.png";
	protected static final IPoint SELECTION_POINT = new Point(528.0f, 423.0f);
	protected static final IDimension SELECTION_SIZE = new Dimension(34.0f, 35.0f);
	protected static final IPoint ICON_POINT = new Point(521.0f, 290.0f);
	protected static final IDimension ICON_SIZE = new Dimension(62.0f, 62.0f);
	protected static final IPoint INVENTORY_POINT = new Point(508.0f, 258.0f);
	private static final IDimension INVENTORY_SIZE = new Dimension(281.0f, 273.0f);
	private static final IRectangle WINDOW = new Rectangle(INVENTORY_POINT, INVENTORY_SIZE);
	protected static final IDimension BUTTON_SIZE = new Dimension(63.0f, 18.0f);
	private static Image goldsImage;
	private static Image selectionImage;
	
	private ImageLayer inventoryLayer;
	private ImageLayer goldLayer;
	private ImageLayer selectionLayer;
	private Text goldText;

	public Inventory(TheKnowledgeTowers game, Player player)
	{
		super(game, player);
		goldLayer = graphics().createImageLayer(goldsImage);
		goldLayer.setVisible(false);
		goldLayer.setTranslation(GOLDS_POINT.x(), GOLDS_POINT.y());
		goldLayer.setDepth(8.0f);
		graphics().rootLayer().add(goldLayer);
		selectionLayer = graphics().createImageLayer(selectionImage);
		selectionLayer.setDepth(13.0f);
		selectionLayer.setVisible(false);
		selectionLayer.setTranslation(SELECTION_POINT.x(), SELECTION_POINT.y());
		graphics().rootLayer().add(selectionLayer);
	}
	
	public static void loadAssets(AssetWatcher watcher)
	{
		ItemInventory.loadAssets(watcher);
		ScrollInventory.loadAssets(watcher);
		goldsImage = assets().getImage(GOLDS_PATH);
		watcher.add(goldsImage);
		selectionImage = assets().getImage(SELECTION_PATH);
		watcher.add(selectionImage);
	}
	
	protected void initInventoryLayer(Image inventoryImage)
	{
		inventoryLayer = graphics().createImageLayer(inventoryImage);
		inventoryLayer.setVisible(false);
		inventoryLayer.setTranslation(INVENTORY_POINT.x(), INVENTORY_POINT.y());
		inventoryLayer.setDepth(8.0f);
		graphics().rootLayer().add(inventoryLayer);
	}
	
	public ImageLayer getInventoryLayer()
	{
		return inventoryLayer;
	}

	public void setInventoryLayer(ImageLayer inventoryLayer)
	{
		this.inventoryLayer = inventoryLayer;
	}

	public ImageLayer getSelectionLayer()
	{
		return selectionLayer;
	}

	public void setSelectionLayer(ImageLayer selectionLayer)
	{
		this.selectionLayer = selectionLayer;
	}

	@Override
	public void setVisible(boolean visible)
	{
		goldLayer.setVisible(visible);
		inventoryLayer.setVisible(visible);
		
		if (visible)
		{
			if (goldText != null)
				goldText.destroy();
			
			goldText = new Text(String.valueOf(getPlayer().getGolds()).replaceAll("\\..*$", ""), Style.PLAIN, 14, 0xFFFFFFFF);
			goldText.setTranslation(GOLDS_TEXT_POINT.subtract(goldText.width(), 0));
			goldText.setDepth(12.0f);
			goldText.setVisible(true);
			goldText.init();
		}
		
		else if (goldText != null)
		{
			goldText.destroy();
			goldText = null;
		}
	}
	
	@Override
	public boolean visible()
	{
		return inventoryLayer.visible();
	}
	
	public boolean intersectsWindow(IPoint p)
	{
		return WINDOW.contains(p);
	}
	
	public static boolean isOverIcon(ButtonEvent event, int i)
	{
		return (i < 4 && event.localX() >= ICON_POINT.x() + (ICON_SIZE.width() + 4) * i && event.localY() >= ICON_POINT.y() && 
					event.localX() <= ICON_POINT.x() + ICON_SIZE.width() + (ICON_SIZE.width() + 4) * i && 
						event.localY() <= ICON_POINT.y() + ICON_SIZE.height()) || (i >= 4 && event.localX() >= ICON_POINT.x() + 
							(ICON_SIZE.width() + 4) * (i - 4) && event.localY() >= ICON_POINT.y() + (ICON_SIZE.height() + 4) && 
								event.localX() <= ICON_POINT.x() + ICON_SIZE.width() + (ICON_SIZE.width() + 4) * (i - 4) && 
									event.localY() <= ICON_POINT.y() + ICON_SIZE.height() + (ICON_SIZE.height() + 4));
	}
	
	protected boolean isTrashing(ButtonEvent event)
	{
		return event.localX() >= SELECTION_POINT.x() && event.localX() <= SELECTION_POINT.x() + SELECTION_SIZE.width() &&
				event.localY() >= SELECTION_POINT.y() && event.localY() <= SELECTION_POINT.y() + SELECTION_SIZE.height();
	}
	
	@Override
	public boolean intersectsClose(IPoint p)
	{
		return CROSS.contains(p);
	}
	
	@Override
	public void update(int delta)
	{
	}
}
