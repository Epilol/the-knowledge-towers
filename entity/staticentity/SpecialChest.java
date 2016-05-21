package saga.progetto.tesi.entity.staticentity;

import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.jbox2d.dynamics.BodyType;
import playn.core.AssetWatcher;
import playn.core.Font;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Key;
import playn.core.Mouse;
import playn.core.Keyboard.Event;
import playn.core.Mouse.ButtonEvent;
import pythagoras.f.Circle;
import pythagoras.f.Dimension;
import pythagoras.f.ICircle;
import pythagoras.f.IDimension;
import pythagoras.f.IPoint;
import pythagoras.f.IRectangle;
import pythagoras.f.Point;
import pythagoras.f.Rectangle;
import saga.progetto.tesi.core.reposity.DataQuality;
import saga.progetto.tesi.entity.Animation;
import saga.progetto.tesi.entity.Sprite;
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.map.GameMap;
import saga.progetto.tesi.media.Text;
import saga.progetto.tesi.navigable.menu.NewGameMenu;

public class SpecialChest extends Drop
{
	private static final String CHEST_PATH = "images/static_objects/special_chest.png";
	private static final String WINDOW_PATH = "images/static_objects/special_chest_window.png";
	private static final IPoint WINDOW_POINT = new Point(5.0f, 139.0f);
	private static final IRectangle TEXT_AREA = new Rectangle(new Point(87.0f, 163.0f), new Dimension(187.0f, 24.0f));
	private static final IDimension ICON_SIZE = new Dimension(62.0f, 62.0f);
	private static final IPoint FIRST_ICON = new Point(18.0f, 168.0f);
	private static final IPoint SECOND_ICON = new Point(282.0f, 168.0f);
	private static final ICircle CROSS = new Circle(353.0f, 151.0f, 12.0f);
	private static final IRectangle ACTIVATE = new Rectangle(new Point(88.0f, 397.0f), new Dimension(187.0f, 122.0f));
	private static final int MAX_PAGES = 8;
	private static Image chestImage;
	private static Image windowImage;
	
	private List<Page> pages;
	private ImageLayer windowLayer;
	private Animation animation;
	private Page currentPage;
	private ImageLayer currentPageLayer;
	private Text text;
	private String[] prize;
	private boolean isOpen;
	private boolean activated;
	private int currentIndex;
	

	public SpecialChest(float x, float y, GameMap map, String id, String[] prize)
	{
		super(x, y, map, id);
		this.prize = prize;
		windowLayer = graphics().createImageLayer(windowImage);
		windowLayer.setVisible(false);
		windowLayer.setDepth(7.0f);
		windowLayer.setTranslation(WINDOW_POINT.x(), WINDOW_POINT.y());
		graphics().rootLayer().add(windowLayer);
		text = new Text("Chest of " + map.getGameloop().getTowerTitle(), Font.Style.BOLD, 14, 0xFFFFFFFF);
		text.setTranslation(TEXT_AREA.x() + TEXT_AREA.width() / 2 - text.width() / 2, TEXT_AREA.y() + TEXT_AREA.height() / 2 - text.height() / 2);
		text.setDepth(8.0f);
		text.setVisible(false);
		text.init();
		initPhysicalBody(BodyType.STATIC, getWidth(), getHeight(), Material.METAL, 0x032, 0xFFFF);
		setSprite(new Sprite(chestImage, getFrameDuration(), getSize().width(), getSize().height()));
		animation = Animation.STATIC_OBJECT_DEFAULT;
		pages = new LinkedList<Page>();
	}
	
	public static void loadAssets(AssetWatcher watcher)
	{
		chestImage = assets().getImage(CHEST_PATH);
		watcher.add(chestImage);
		windowImage = assets().getImage(WINDOW_PATH);
		watcher.add(windowImage);
	}
	
	public void addPage(Page page)
	{
		pages.add(page);
	}
	
	public Page getPage(int index)
	{
		return pages.get(index);
	}
	
	public void activate()
	{
		int reward = 0;
		
		Iterator<Page> pageIterator = pages.iterator();

		while (pageIterator.hasNext())
		{
			Page page = pageIterator.next();
			page.send(true, getMap().getGame().getAnnotationProtocol());
			
			if (page.getDomain() == DataQuality.TOVALIDATE)
				reward++;
			
			else
				reward--;
			
			if (reward != 0)
			{
				float kpPrize = reward > 0 ? Float.parseFloat(prize[reward - 1].replace("kp", "")) * 2 : 
												Float.parseFloat(prize[(reward * -1) + 1].replace("kp", "")) * -2; 
				getMap().getPlayer().getCurrentJob().gainJobExp(kpPrize);
			}

			pageIterator.remove();
			page.clear();
			page.getImageLayer().destroy();
			getPlayer().setUsedPages(getPlayer().getUsedPages() + 1);
		}
		
		updateVisibility(true);
	}
	
	public boolean isActivated()
	{
		return activated;
	}

	public void setActivated(boolean activated)
	{
		this.activated = activated;
	}

	public boolean intersectsActivate(IPoint p)
	{
		return ACTIVATE.contains(p);
	}
	
	public boolean intersectsIcon(ButtonEvent event)
	{
		if (!visible() || isFull())
			return false;
		
		for (int i = 0; i < 2; i++)
		{
			for (int j = 0; j < MAX_PAGES / 2; j++)
			{
				float iconX = i == 0 ? FIRST_ICON.x() : SECOND_ICON.x();
				IRectangle iconArea = new Rectangle(new Point(iconX, FIRST_ICON.y() + j * (ICON_SIZE.height() + 4)), 
														new Dimension(ICON_SIZE.width(), ICON_SIZE.height()));
				
				if (iconArea.contains(new Point(event.localX(), event.localY())))
				{
					currentIndex = j + i * (MAX_PAGES / 2);
					return true;
				}
			}
		}
		
		return false;
	}
	
	@Override
	public Image getCurrentFrame()
	{
		return getSprite().getCurrentImage(animation);
	}
	
	@Override
	public void setCurrentAnimation(Animation currentAnimation)
	{
	}

	@Override
	public Animation getCurrentAnimation()
	{
		return animation;
	}
	
	@Override
	public Image getImage()
	{
		return chestImage;
	}

	public void open(boolean isOpen)
	{
		getSprite().resetDelta();
		this.isOpen = isOpen;
		
		getMap().getGameloop().scrollMenu();
		
		if (getCurrentAnimation() == Animation.STATIC_OBJECT_DEFAULT)
			animation = Animation.STATIC_OBJECT_ANIMATED;
		
		windowLayer.setVisible(isOpen);
		text.setVisible(isOpen);
	
		for (Page page : pages)
			page.getImageLayer().setVisible(isOpen);;
	}
	
	public boolean isOpen()
	{
		return isOpen;
	}
	
	public boolean isEmpty()
	{
		return pages.size() == 0;
	}
	
	public boolean isFull()
	{
		return pages.size() == MAX_PAGES;
	}
	
	public void updateVisibility(boolean visible)
	{
		for (Page page : pages)
			page.getImageLayer().setVisible(visible);
	}
	
	public void translateLastPage()
	{
		if (pages.size() > 0)
		{
			int i = pages.size() - 1;
			float iconX = i < 4 ? FIRST_ICON.x() : SECOND_ICON.x();
			float iconY = i < 4 ? FIRST_ICON.y() + i * (ICON_SIZE.height() + 4): SECOND_ICON.y() + (i - 4) * (ICON_SIZE.height() + 4);
			pages.get(i).getImageLayer().setTranslation(iconX, iconY);
		}
	}
	
	public void translateAll()
	{
		for (int i = 0; i < pages.size() ; i++)
		{
				float iconX = i < 4 ? FIRST_ICON.x() : SECOND_ICON.x();
				float iconY = i < 4 ? FIRST_ICON.y() + i * (ICON_SIZE.height() + 4): SECOND_ICON.y() + (i - 4) * (ICON_SIZE.height() + 4);
				pages.get(i).getImageLayer().setTranslation(iconX, iconY);
		}
	}

	public void mouseListener(ButtonEvent event)
	{
		IPoint p = new Point(event.localX(), event.localY());
		
		if (event.button() == Mouse.BUTTON_LEFT)
		{
			if (CROSS.contains(p))
				open(false);
			
			
			if (intersectsActivate(p))
			{
				activate();
				setActivated(true);
			}
				
			if (intersectsIcon(event) && currentIndex < pages.size() && currentPage == null && !getScrollInventory().isActive())
			{
				currentPage = pages.get(currentIndex);
				currentPageLayer = graphics().createImageLayer(currentPage.getPageImage());
				currentPageLayer.setDepth(15.0f);
				currentPageLayer.setWidth(currentPage.getImageLayer().scaledWidth());
				currentPageLayer.setHeight(currentPage.getImageLayer().scaledHeight());
				currentPageLayer.setTranslation(event.localX() - currentPageLayer.scaledWidth() / 2, 
						event.localY() - currentPageLayer.scaledHeight() / 2);
				graphics().rootLayer().add(currentPageLayer);
			}
			
			if (currentPage != null && getScrollInventory().intersectsWindow(new Point(event.localX(), event.localY())) &&
					getPlayer().getPages().size() <= MAX_PAGES)
			{
				pages.remove(currentPage);
				getPlayer().getPages().add(currentPage);
				currentPageLayer.destroy();
				currentPage = null;
				getScrollInventory().setVisible(true);
				translateAll();
				updateVisibility(true);
			}
		}
		
		else if (event.button() == Mouse.BUTTON_RIGHT  && getPlayer().getPages().size() <= MAX_PAGES)
		{
			if (currentPage != null)
			{
				currentPage = null;
				currentPageLayer.destroy();
			}
			
			else if (intersectsIcon(event) && currentIndex < pages.size())
			{
				getPlayer().getPages().add(pages.get(currentIndex));
				pages.remove(pages.get(currentIndex));
				getScrollInventory().setVisible(true);
				translateAll();
				updateVisibility(true);
			}
		}
	}

	public void keyboardListener(Event event)
	{
		if (event.key() == Key.ESCAPE)
			open(false);
	}
	
	public NewGameMenu getScrollInventory()
	{
		return getMap().getGameloop().getNewGameMenu().get("SCROLL");
	}
	
	public boolean visible()
	{
		return windowLayer.visible();
	}
	
	public Player getPlayer()
	{
		return getMap().getPlayer();
	}
	
	@Override
	public void clear()
	{
		super.clear();
		windowLayer.destroy();
		text.destroy();
	}
	
	@Override
	public void update(int delta)
	{
		super.update(delta);
		
		if (isOpen && getSprite().isOver(Animation.STATIC_OBJECT_ANIMATED))
			animation = Animation.STATIC_OBJECT_USED;
		
		if (currentPage != null)
		{
			currentPageLayer.setTranslation(getMap().getGame().getPointerLocation().x() - currentPageLayer.scaledWidth() / 2, 
					getMap().getGame().getPointerLocation().y() - currentPageLayer.scaledHeight() / 2);
		}
	}
}
