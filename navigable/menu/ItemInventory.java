package saga.progetto.tesi.navigable.menu;

import static playn.core.PlayN.assets;
import playn.core.AssetWatcher;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Key;
import playn.core.Mouse;
import playn.core.Keyboard.Event;
import playn.core.Mouse.ButtonEvent;
import pythagoras.f.IPoint;
import pythagoras.f.IRectangle;
import pythagoras.f.Point;
import pythagoras.f.Rectangle;
import saga.progetto.tesi.core.TheKnowledgeTowers;
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.entity.staticentity.Item;
import saga.progetto.tesi.navigable.Navigable;

public class ItemInventory extends Inventory
{
	private static final String INVENTORY_PATH =  "images/menu/game_menu/inventory/item_menu.png";
	private static final IRectangle WINDOW_BUTTON = new Rectangle(new Point(515.0f, 265.0f), BUTTON_SIZE);
	private static Image inventoryImage;
	
	private Item currentItem;
	private int currentItemIndex;
	
	public ItemInventory(TheKnowledgeTowers game, Player player)
	{
		super(game, player);
		initInventoryLayer(inventoryImage);
	}

	public static void loadAssets(AssetWatcher watcher)
	{
		inventoryImage = assets().getImage(INVENTORY_PATH);
		watcher.add(inventoryImage);
	}
	
	@Override
	public Navigable onMouseDown(ButtonEvent event)
	{
		if (event.button() == Mouse.BUTTON_LEFT)
		{
			for (int i = 0; i < Player.MAX_ITEMS; i++)
				if (isOverIcon(event, i) && i < getPlayer().getItems().size() && currentItem == null)
				{
					getPlayer().setHasSelectedItem(true);
					currentItem = getPlayer().getItems().get(i);
					currentItemIndex = i;
					currentItem.getIconLayer().setTranslation(event.localX() - currentItem.getIconLayer().width() / 2, 
							event.localY() - currentItem.getIconLayer().height() / 2);
					getSelectionLayer().setVisible(true);
				}
			
			if (currentItem != null && isTrashing(event))
			{
				getPlayer().setHasSelectedItem(false);
				currentItem.clear();
				getPlayer().getItems().remove(currentItem);
				currentItem = null;
				getSelectionLayer().setVisible(false);
				setVisible(true);
			}
		}
		return this;
	}
	
	@Override
	public Navigable onMouseUp(ButtonEvent event)
	{
		if (event.button() == Mouse.BUTTON_RIGHT && currentItem != null)
		{
			translateIcon(currentItem.getIconLayer(), currentItemIndex);
			currentItem = null;
			getPlayer().setHasSelectedItem(false);
			getSelectionLayer().setVisible(false);
		}
		
		return this;
	}

	@Override
	public Navigable onKeyDown(Event event)
	{
		if (event.key() == Key.ESCAPE && currentItem != null)
		{
			translateIcon(currentItem.getIconLayer(), currentItemIndex);
			currentItem = null;
			getPlayer().setHasSelectedItem(false);
			getSelectionLayer().setVisible(false);
		}
		
		return this;
	}
	
	@Override
	public void setVisible(boolean visible)
	{
		super.setVisible(visible);

		if (visible)
			for (int index = 0; index < getPlayer().getItems().size(); index++)
			{
				Item item = getPlayer().getItems().get(index);
				translateIcon(item.getIconLayer(), index);
				item.getIconLayer().setVisible(true);
			}
		
		else
		{
			if (currentItem != null)
			{
				currentItem = null;
				getPlayer().setHasSelectedItem(false);
				getSelectionLayer().setVisible(false);
			}
			
			for (Item item : getPlayer().getItems())
				item.getIconLayer().setVisible(false);
		}
	}

	
	@Override
	public boolean intersectsSwitch(IPoint p)
	{
		return WINDOW_BUTTON.contains(p);
	}
	
	protected void translateIcon(ImageLayer imageLayer, int index)
	{
		if (index < 4)
			imageLayer.setTranslation(ICON_POINT.x() + index * (ICON_SIZE.width() + 4) + imageLayer.scaledWidth() / 2,
										ICON_POINT.y() + imageLayer.scaledHeight() / 2);

		else
			imageLayer.setTranslation(ICON_POINT.x() + (index - 4 )* (ICON_SIZE.width() + 4) + imageLayer.scaledWidth() / 2,
										ICON_POINT.y() + ICON_SIZE.height() + 4 + imageLayer.scaledHeight() / 2);
	}
	
	@Override
	public void update(int delta)
	{
		super.update(delta);
		
		if (currentItem != null)
		{
			currentItem.getIconLayer().setTranslation(getGame().getPointerLocation().x() - currentItem.getIconLayer().width() / 2, 
					getGame().getPointerLocation().y() - currentItem.getIconLayer().height() / 2);
			
			if (canEscape())
				setCanEscape(false);
		}

		else if (!canEscape())
			setCanEscape(true);
	}
}
