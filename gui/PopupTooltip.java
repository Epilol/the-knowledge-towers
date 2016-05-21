package saga.progetto.tesi.gui;

import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;
import playn.core.AssetWatcher;
import playn.core.Font;
import playn.core.Image;
import playn.core.ImageLayer;
import pythagoras.f.Dimension;
import pythagoras.f.IRectangle;
import pythagoras.f.Point;
import pythagoras.f.Rectangle;
import saga.progetto.tesi.media.Text;

public class PopupTooltip
{
	private final static String PATH = "images/gui/popup_tooltip.png";
	private final float DEPTH = 16.0f;
	private static final IRectangle POPUP = new Rectangle(new Point(336.0f, 35.0f), new Dimension(154.0f, 31.0f));
	private static PopupTooltip instance;
	private static Image popupImage;
	
	private Text text;
	private ImageLayer popupLayer;
	
	public PopupTooltip()
	{
		popupLayer = graphics().createImageLayer(popupImage);
		popupLayer.setVisible(false);
		popupLayer.setDepth(DEPTH);
		popupLayer.setTranslation(POPUP.x(), POPUP.y());
		graphics().rootLayer().add(popupLayer);
	}
	
	public static PopupTooltip getInstance()
	{
		if (instance == null)
			instance = new PopupTooltip();
		return instance;
	}
	
	public static void loadAssets(AssetWatcher watcher)
	{
		popupImage = assets().getImage(PATH);
		watcher.add(popupImage);
	}
	
	public void show(String message)
	{
		if (visible() && text != null)
			hide();
		
		popupLayer.setVisible(true);
		text = new Text(message, Font.Style.BOLD, 14, 0xFFFFFFFF);
		text.setTranslation(POPUP.x() + POPUP.width() / 2 - text.width() / 2, POPUP.y() + POPUP.height() / 2 - text.height() / 2);
		text.setDepth(DEPTH + 1);
		text.setVisible(true);
		text.init();
	}
	
	public void hide()
	{
		popupLayer.setVisible(false);
		
		if (text != null)
			text.destroy();
	}
	
	public boolean visible()
	{
		return popupLayer.visible();
	}
	
}
