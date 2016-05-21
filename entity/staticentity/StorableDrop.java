package saga.progetto.tesi.entity.staticentity;

import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;
import java.util.LinkedList;
import java.util.List;
import playn.core.AssetWatcher;
import playn.core.Image;
import playn.core.ImageLayer;
import pythagoras.f.Dimension;
import pythagoras.f.IPoint;
import pythagoras.f.IRectangle;
import pythagoras.f.Point;
import pythagoras.f.Rectangle;
import saga.progetto.tesi.map.GameMap;

public abstract class StorableDrop extends Drop
{
	private static final String WINDOW_PATH = "images/static_objects/drops.png";
	private static final String SELECTED_PATH = "images/static_objects/drops_selection.png";
	private static final IPoint WINDOW_POINT = new Point(320.0f, 38.0f);
	private static final IPoint SELECTED_POINT = WINDOW_POINT.add(25.0f, 55.0f);
	private static final IRectangle DISCARD = new Rectangle(new Point(362.0f, 282.0f), new Dimension(77.0f, 22.0f));
	private static final float LAST_CLICK_CD = 250.0f;
	private static Image windowImage;
	
	private ImageLayer windowLayer;
	private ImageLayer selectionLayer;
	private List<Item> items;
	private List<IRectangle> selectionBoxes;
	private Item selected;
	private boolean isOpen;
	private boolean isSelected;
	private boolean isDoubleClick;
	private boolean isTaken;
	private int index;
	private float lastClick = LAST_CLICK_CD; 
	
	public StorableDrop(float x, float y, GameMap map, boolean isTaken, String id)
	{
		super(x, y, map, id);
		windowLayer = graphics().createImageLayer(windowImage);
		windowLayer.setVisible(false);
		windowLayer.setDepth(7.0f);
		windowLayer.setTranslation(WINDOW_POINT.x(), WINDOW_POINT.y());
		selectionLayer = graphics().createImageLayer(assets().getImage(SELECTED_PATH));
		selectionLayer.setVisible(false);
		selectionLayer.setDepth(8.0f);
		graphics().rootLayer().add(windowLayer);
		graphics().rootLayer().add(selectionLayer);
		items = new LinkedList<Item>();
		selectionBoxes = new LinkedList<IRectangle>();
	}
	
	public static void loadAssets(AssetWatcher watcher)
	{
		Bag.loadAssets(watcher);
		Book.loadAssets(watcher);
		Chest.loadAssets(watcher);
		Corpse.loadAssets(watcher);
		Heart.loadAssets(watcher);
		PhysicsPage.loadAssets(watcher);
		windowImage = assets().getImage(WINDOW_PATH);
		watcher.add(windowImage);
	}
	
	public List<Item> getItems()
	{
		return items;
	}
	
	public void addAllPages(List<Page> pages)
	{
		items.addAll(pages);
	}
	
	public void addAllWeapons(List<Weapon> weapons)
	{
		items.addAll(weapons);
	}
	
	public void addAllPotions(List<Potion> potions)
	{
		items.addAll(potions);
	}
	
	public void addGolds(Gold golds)
	{
		items.add(golds);
	}
	
	public void initDrops()
	{
		int drop = 0;
		
		while (drop < items.size())
		{
			items.get(drop).translateTooltip();
			selectionBoxes.add(new Rectangle(new Point(SELECTED_POINT.x(), SELECTED_POINT.y() + 29.0f * drop++), new Dimension(26.0f, 26.0f)));
		}
	}
	
	public boolean isEmpty()
	{
		return items.size() == 0;
	}
	
	public boolean isTaken()
	{
		return isTaken;
	}
	
	public void setTaken()
	{
		isTaken = true;
		clear();
	}
	
	public void select(int index)
	{
		this.index = index;
		
		if (items.size() > 0)
		{
			if (isSelected)
				selected.hideTooltip(true);
			selectionLayer.setTranslation(SELECTED_POINT.x() - 2.0f, (SELECTED_POINT.y() - 2.0f) + 30.0f * (index));
			selectionLayer.setVisible(true);
			isSelected = true;
			selected = items.get(index);
			selected.showItem(true);
		}
	}
	
	public void select(IPoint p)
	{
		if (lastClick < LAST_CLICK_CD)
			isDoubleClick = true;
		
		else
			lastClick = 0.0f;
		
		int drop = 0;
		
		while (drop < selectionBoxes.size())
		{
			IRectangle hitBox = selectionBoxes.get(drop);
			if (hitBox.contains(p))
				select(drop);
			
			drop++;
		}
	}

	public int getIndex()
	{
		return index;
	}
	
	public void setIndex(int index)
	{
		this.index = index;
		if (isSelected)
			selectionLayer.setTranslation(SELECTED_POINT.x() - 2.0f, (SELECTED_POINT.y() - 2.0f) + 30.0f * (index));
	}
	
	public Item getSelected()
	{
		return selected;
	}
	
	public void removePages()
	{
		items.clear();
	}
	
	public void removeSelected()
	{
		isSelected = false;
		selectionLayer.setVisible(false);
		selected.showItem(false);
		items.remove(selected);
		selectionBoxes.clear();
		initDrops();
		
		for (int i = 0; i < items.size(); i++)
		{
			items.get(i).updateIndex(i);
			items.get(i).translateTooltip();
			items.get(i).translateIcon();
		}
		
		getMap().getData().getPagesInfo().put(getId(), "true");
		getMap().getGameloop().addItem(getId(), true);
	}
	
	public void open(boolean isOpen)
	{
		this.isOpen = isOpen;
		windowLayer.setVisible(isOpen);
		getMap().getGameloop().scrollMenu();
	
		if (isOpen)
			for (Item item : items)
				item.setVisible(true);
		else
		{
			selectionLayer.setVisible(false);
			for (Item item : items)
				item.showItem(false);
		}
			
	}
	
	public boolean isDiscarded(Point p)
	{
		return DISCARD.contains(p);
	}
	
	public boolean isOpen()
	{
		return isOpen;
	}

	public boolean isSelected()
	{
		return isSelected;
	}
	
	public void setSelected(boolean isSelected)
	{
		this.isSelected = isSelected;
	}

	@Override
	public void clear()
	{
		super.clear();
		windowLayer.destroy();
		selectionLayer.destroy();
		
		for (Item item : items)
		{
			item.clear();
			item.destroy();
		}
	}
	
	@Override
	public void update(int delta)
	{
		super.update(delta);
		
		lastClick += delta;
		
		for (Item item : items)
			item.update(delta);
		
		if (isDoubleClick && isSelected)
		{
			isDoubleClick = false;
			getMap().getPlayer().gatherSelectedItem(this);
		}
	}
}
