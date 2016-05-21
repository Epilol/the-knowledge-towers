package saga.progetto.tesi.entity.staticentity;

import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;
import playn.core.AssetWatcher;
import playn.core.Image;
import playn.core.ImageLayer;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import pythagoras.f.IPoint;
import pythagoras.f.Point;
import saga.progetto.tesi.core.TheKnowledgeTowers;
import saga.progetto.tesi.core.annotation.AnnotationProtocol;
import saga.progetto.tesi.core.reposity.DataQuality;
import saga.progetto.tesi.media.Text;

public class Page extends Item
{
	private static final String ICON_PATH = "images/static_objects/page_icon.png";
	private static final String TOOLTIP_PATH = "images/gui/page_window.png";
	private static final IPoint TOOLTIP_POINT = new Point(0.0f, 2.0f);
	private static final IPoint IMAGE_POINT = new Point(24.0f, 51.0f);
	private static final IDimension MAX_SIZE = new Dimension(300.0f, 365.0f);
	private static Image iconImage;
	
	private ImageLayer pageImageLayer;
	private Image pageImage;
	private DataQuality domain;
	private String title;
	private String synset;
	private boolean isSeen;
	private boolean isFixedSize;
	
	public Page(TheKnowledgeTowers game, int index, DataQuality domain, String title, String synset)
	{
		super(index, iconImage, "page", TOOLTIP_POINT);
		this.domain = domain;
		this.title = title;
		this.synset = synset;
		pageImage = game.getServerConnection().getImage(synset);
		initImage(pageImage);
		setTooltipLayer(graphics().createImageLayer(assets().getImage(TOOLTIP_PATH)));
		getTooltipLayer().setVisible(false);
		getTooltipLayer().setDepth(13.0f);
		graphics().rootLayer().add(getTooltipLayer());
	}
	
	public static void loadAssets(AssetWatcher watcher)
	{
		iconImage = assets().getImage(ICON_PATH);
		watcher.add(iconImage);
	}
	
	public void initImage(Image pageImage)
	{
		pageImageLayer = graphics().createImageLayer(pageImage);
		pageImageLayer.setVisible(false);
		pageImageLayer.setDepth(14.0f);
		graphics().rootLayer().add(pageImageLayer);
	}
	
	private void fixSize()
	{
		isFixedSize = true;
		float scale = 1.0f;

		if (pageImageLayer.width() > MAX_SIZE.width())
		{
			scale = 1.0f - (pageImageLayer.width() - MAX_SIZE.width()) / pageImageLayer.width();
			pageImageLayer.setScale(scale);
		}
		
		if (pageImageLayer.height() * scale > MAX_SIZE.height())
		{
			scale = 1.0f - (pageImageLayer.height() - MAX_SIZE.height()) / pageImageLayer.height();
			pageImageLayer.setScale(scale);
		}
			
		pageImageLayer.setTranslation(IMAGE_POINT.x() - 2.0f , IMAGE_POINT.y());
	}
	
	@Override
	public void showItem(boolean visible)
	{
		super.setVisible(visible);
		hideTooltip(!visible);

	}
	
	@Override
	public void hideTooltip(boolean hide)
	{
		getTooltipLayer().setVisible(!hide);
		if (pageImageLayer != null)
			pageImageLayer.setVisible(!hide);
	}
	
	public ImageLayer getImageLayer()
	{
		return pageImageLayer;
	}
	
	public boolean isValidation()
	{
		return domain == DataQuality.TOVALIDATE;
	}
	
	public DataQuality getDomain()
	{
		return domain;
	}
	
	public boolean isSeen()
	{
		return isSeen;
	}

	public void setSeen(boolean isSeen)
	{
		this.isSeen = isSeen;
	}

	public void send(boolean isPicked, AnnotationProtocol annotationProtocol)
	{
		annotationProtocol.sendAnnotation(synset, domain, isPicked, title);
	}
	
	public void setText(Text tooltipText)
	{
		tooltipText.setVisible(false);
		tooltipText.setDepth(13.0f);
		tooltipText.init();
	}
	
	public Image getPageImage()
	{
		return pageImage;
	}
	
	@Override
	public void translateTooltip()
	{
		getTooltipLayer().setTranslation(TOOLTIP_POINT.x(), TOOLTIP_POINT.y());
		
		if (pageImageLayer != null)
			pageImageLayer.setTranslation(IMAGE_POINT.x() - 2.0f, IMAGE_POINT.y());
	}
	
	public void destroy()
	{
		if (pageImageLayer != null)
			pageImageLayer.destroy();
	}
	
	public void update(int delta)
	{
		if (!isFixedSize && pageImageLayer.width() > 0.0f)
			fixSize();
	}
}
