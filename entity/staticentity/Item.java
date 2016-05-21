package saga.progetto.tesi.entity.staticentity;

import static playn.core.PlayN.graphics;
import playn.core.AssetWatcher;
import playn.core.Font;
import playn.core.Image;
import playn.core.ImageLayer;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import pythagoras.f.IPoint;
import pythagoras.f.Point;
import saga.progetto.tesi.core.annotation.AnnotationProtocol;
import saga.progetto.tesi.media.Text;

public abstract class Item
{
	private static final IDimension SIZE = new Dimension(32.0f, 32.0f);
	private static final IPoint ICON_POINT = new Point(graphics().width() /2 - 117.0f / 2, graphics().height() / 2 - 420.0f / 2);
	private static final IPoint TEXT_POINT = ICON_POINT.add(SIZE.width(), SIZE.height() / 4);
			
	private ImageLayer iconLayer;
	private ImageLayer tooltipLayer;
	private Text text;
	private int index;
	private IPoint tooltipPoint;
	
	public Item(int index, Image iconImage, String name, IPoint tooltipPoint)
	{
		this(index, iconImage, name);
		this.tooltipPoint = tooltipPoint;
	}
	
	public Item(int index, Image iconImage, String name)
	{
		this.index = index;
		iconLayer = graphics().createImageLayer(iconImage);
		iconLayer.setVisible(false);
		iconLayer.setTranslation(ICON_POINT.x(), ICON_POINT.y() + index * 30.0f);
		iconLayer.setDepth(15.0f);
		graphics().rootLayer().add(iconLayer);
		text = new Text(name, Font.Style.BOLD, 10, 0xFFFFFFFF, 0xFF000000);
		text.setTranslation(TEXT_POINT.x(), TEXT_POINT.y() + index * 30.0f);
		text.setDepth(15.0f);
		text.init();
	}
	
	public static void loadAssets(AssetWatcher watcher)
	{
		Page.loadAssets(watcher);
		Weapon.loadAssets(watcher);
		Gold.loadAssets(watcher);
		Potion.loadAssets(watcher);
	}
	
	public int index()
	{
		return index;
	}
	
	public ImageLayer getTooltipLayer()
	{
		return tooltipLayer;
	}

	public void setTooltipLayer(ImageLayer tooltipLayer)
	{
		this.tooltipLayer = tooltipLayer;
	}

	public ImageLayer getIconLayer()
	{
		return iconLayer;
	}

	public void setIconLayer(ImageLayer iconLayer)
	{
		this.iconLayer = iconLayer;
	}

	
	public void translateTooltip()
	{
		tooltipLayer.setTranslation(tooltipPoint.x(), tooltipPoint.y() + 30.0f * index);
	}
	
	public void translateIcon()
	{
		iconLayer.setTranslation(ICON_POINT.x(), ICON_POINT.y() + index * 30.0f);
		text.setTranslation(TEXT_POINT.x(), TEXT_POINT.y() + index * 30.0f);
	}
	
	public void updateIndex(int index)
	{
		this.index = index;
	}

	public static IDimension getSize()
	{
		return SIZE;
	}
	
	public void setVisible(boolean visible)
	{
		iconLayer.setVisible(visible);
		text.setVisible(visible);
	}
	
	public abstract void showItem(boolean visible);

	public abstract void hideTooltip(boolean hide);
	
	public boolean isSeen()
	{
		return false;
	}

	public boolean isValidation()
	{
		return false;
	}
	
	public void send(boolean isPicked, AnnotationProtocol annotationProtocol)
	{
		
	}
	
	public void clear()
	{
		iconLayer.destroy();
		text.destroy();
		
		if (tooltipLayer != null)
			tooltipLayer.destroy();
	}
	
	public void destroy()
	{
		
	}
	
	public void update(int delta)
	{
		
	}
}
