package saga.progetto.tesi.navigable.menu;

import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;
import playn.core.AssetWatcher;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Key;
import playn.core.Keyboard.Event;
import playn.core.Mouse;
import playn.core.Mouse.ButtonEvent;
import pythagoras.f.IPoint;
import pythagoras.f.IRectangle;
import pythagoras.f.Point;
import pythagoras.f.Rectangle;
import saga.progetto.tesi.core.TheKnowledgeTowers;
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.entity.staticentity.Page;
import saga.progetto.tesi.entity.staticentity.SpecialChest;
import saga.progetto.tesi.navigable.Navigable;

public class ScrollInventory extends Inventory
{
	private static final String SCROLL_INVENTORY_PATH = "images/menu/game_menu/inventory/scroll_menu.png";
	private static final IRectangle WINDOW_BUTTON = new Rectangle(new Point(583.0f, 268.0f), BUTTON_SIZE);
	private static Image scrollInventoryImage;
	
	private Page currentPage;
	private ImageLayer currentPageLayer;
	private float activeCooldown;
	private int currentIndex;

	public ScrollInventory(TheKnowledgeTowers game, Player player)
	{
		super(game, player);
		initInventoryLayer(scrollInventoryImage);
	}

	public static void loadAssets(AssetWatcher watcher)
	{
		scrollInventoryImage = assets().getImage(SCROLL_INVENTORY_PATH);
		watcher.add(scrollInventoryImage);
	}

	@Override
	public Navigable onMouseDown(ButtonEvent event)
	{
		if (event.button() == Mouse.BUTTON_LEFT && !getPlayer().getMap().getMapId().contains("boss"))
		{
			for (int i = 0; i < Player.MAX_PAGES; i++)
				if (isOverIcon(event, i) && i < getPlayer().getPages().size() && currentPage == null)
				{
					activeCooldown = 0.0f;
					setActive(true);
					
					if (currentPage == null)
						getPlayer().setHasSelectedItem(true);
					
					currentPage = getPlayer().getPages().get(i);
					currentPageLayer = graphics().createImageLayer(currentPage.getPageImage());
					adjustLayer(currentPageLayer);
					currentPageLayer.setDepth(15.0f);
					currentPageLayer.setTranslation(event.localX() - currentPageLayer.scaledWidth() / 2, 
							event.localY() - currentPageLayer.scaledHeight() / 2);
					graphics().rootLayer().add(currentPageLayer);
					getSelectionLayer().setVisible(true);
					currentIndex = i;
				}
			
			if (currentPage != null)
			{
				if (isTrashing(event))
				{
					getPlayer().setHasSelectedItem(false);
					currentPage.destroy();
					currentPage.send(false, getGame().getAnnotationProtocol());
					getPlayer().getPages().remove(currentPage);
					
					if(!currentPage.isValidation())
						getPlayer().increaseNegativeDiscarded();
					
					currentPageLayer.destroy();
					currentPage = null;
					getSelectionLayer().setVisible(false);
					setVisible(true);
				}
				
				else if (specialChestIntersection(event))
				{
					activeCooldown = 0.0f;
					setActive(true);
					getSpecialChest().addPage(currentPage);
					getPlayer().getPages().remove(currentPage);
					getPlayer().setHasSelectedItem(false);
					currentPageLayer.destroy();
					currentPage = null;
					getSelectionLayer().setVisible(false);
					setVisible(true);
					getSpecialChest().translateLastPage();
					getSpecialChest().updateVisibility(true);
				}
				
				else if (isOverIcon(event, currentIndex) && activeCooldown > 0.0f)
				{
					currentPage = null;
					currentPageLayer.destroy();
					getPlayer().setHasSelectedItem(false);
					getSelectionLayer().setVisible(false);
				}
			}
		}
		
		else if (event.button() == Mouse.BUTTON_RIGHT && !getPlayer().getMap().getMapId().contains("boss") && 
				getSpecialChest() != null && getSpecialChest().visible() && !getSpecialChest().isFull())
		{
			for (int i = 0; i < Player.MAX_PAGES; i++)
				if (isOverIcon(event, i) && i < getPlayer().getPages().size() && currentPage == null)
				{
					getSpecialChest().addPage(getPlayer().getPages().get(i));
					getPlayer().getPages().remove(getPlayer().getPages().get(i));
					setVisible(true);
					getSpecialChest().translateLastPage();
					getSpecialChest().updateVisibility(true);
				}
		}
		
		return this;
	}
	
	public boolean specialChestIntersection(ButtonEvent event)
	{
		return getPlayer().getMap().specialChestIntersection(event);
	}
	
	public SpecialChest getSpecialChest()
	{
		return getPlayer().getMap().getSpecialChest();
	}

	@Override
	public Navigable onMouseUp(ButtonEvent event)
	{
		if (event.button() == Mouse.BUTTON_RIGHT && currentPage != null)
		{
			currentPage = null;
			currentPageLayer.destroy();
			getPlayer().setHasSelectedItem(false);
			getSelectionLayer().setVisible(false);
		}
		
		return this;
	}
	
	@Override
	public Navigable onKeyDown(Event event)
	{
		if (event.key() == Key.ESCAPE && currentPage != null)
		{
			currentPage = null;
			currentPageLayer.destroy();
			getPlayer().setHasSelectedItem(false);
			getSelectionLayer().setVisible(false);
		}
		
		return this;
	}
	
	@Override
	public boolean intersectsSwitch(IPoint p)
	{
		return WINDOW_BUTTON.contains(p);
	}

	@Override
	public void setVisible(boolean visible)
	{
		super.setVisible(visible);

		if (visible)
			for (int index = 0; index < getPlayer().getPages().size(); index++)
			{
				Page page = getPlayer().getPages().get(index);
				if (page.getImageLayer() != null)
				{
					adjustLayer(page.getImageLayer());
					translateIcon(page.getImageLayer(), index);
					page.getImageLayer().setVisible(true);
				}
			}
		
		else
		{
			if (currentPage != null)
			{
				currentPage = null;
				currentPageLayer.destroy();
				getPlayer().setHasSelectedItem(false);
				getSelectionLayer().setVisible(false);
			}
			
			for (Page page : getPlayer().getPages())
				if (page.getImageLayer() != null)
					page.getImageLayer().setVisible(false);
		}
	}

	private void adjustLayer(ImageLayer pageImageLayer)
	{
		float scale = 1.0f;

		if (pageImageLayer.width() > ICON_SIZE.width())
		{
			scale = 1.0f - (pageImageLayer.width() - ICON_SIZE.width()) / pageImageLayer.width();
			pageImageLayer.setScaleX(scale);
		}

		scale = 1.0f;

		if (pageImageLayer.height() * scale > ICON_SIZE.height())
		{
			scale = 1.0f - (pageImageLayer.height() - ICON_SIZE.height()) / pageImageLayer.height();
			pageImageLayer.setScaleY(scale);
		}
	}
	
	protected void translateIcon(ImageLayer imageLayer, int index)
	{
		if (index < 4)
			imageLayer.setTranslation(ICON_POINT.x() + index * (ICON_SIZE.width() + 4), ICON_POINT.y());

		else
			imageLayer.setTranslation(ICON_POINT.x() + (index - 4 ) * (ICON_SIZE.width() + 4), ICON_POINT.y() + ICON_SIZE.height() + 4);
	}

	@Override
	public void update(int delta)
	{
		super.update(delta);
		
		if (isActive())
		{
			activeCooldown += delta;
			
			if (activeCooldown > TheKnowledgeTowers.UPDATE_RATE && currentPage == null)
			{
				activeCooldown = 0.0f;
				setActive(false);
			}
		}
		
		if (currentPage != null && getInventoryLayer().visible())
		{
			currentPageLayer.setTranslation(getGame().getPointerLocation().x() - currentPageLayer.scaledWidth() / 2, 
					getGame().getPointerLocation().y() - currentPageLayer.scaledHeight() / 2);
			
			if (canEscape())
				setCanEscape(false);
		}

		else if (!canEscape())
			setCanEscape(true);
	}
}
