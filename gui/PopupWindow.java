package saga.progetto.tesi.gui;

import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;
import java.util.LinkedList;
import java.util.List;
import playn.core.AssetWatcher;
import playn.core.Font;
import playn.core.Image;
import playn.core.ImageLayer;
import pythagoras.f.Dimension;
import pythagoras.f.IPoint;
import pythagoras.f.IRectangle;
import pythagoras.f.Point;
import pythagoras.f.Rectangle;
import saga.progetto.tesi.media.Text;

public class PopupWindow
{
	private final static String PATH = "images/gui/popup.png";
	private final float DEPTH = 16.0f;
	private final static int LINE_LENGTH = 29;
	private static final float HEIGHT_OFFSET = 22.0f;
	private static final IPoint POPUP_POINT = new Point(241.0f, 35.0f);
	private static final IRectangle TEXT_WINDOW = new Rectangle(new Point(258.0f, 65.0f), new Dimension(283.0f, 148.0f));
	private static final IRectangle BUTTON = new Rectangle(new Point(345.0f, 167.0f), new Dimension(115.0f, 22.0f));
	private static PopupWindow instance;
	private static Image popupImage;
	
	private ImageLayer popupLayer;
	private List<Text> texts;
	
	public PopupWindow()
	{
		popupLayer = graphics().createImageLayer(popupImage);
		popupLayer.setVisible(false);
		popupLayer.setDepth(DEPTH);
		popupLayer.setTranslation(POPUP_POINT.x(), POPUP_POINT.y());
		graphics().rootLayer().add(popupLayer);
		texts = new LinkedList<Text>();
	}
	
	public static PopupWindow getInstance()
	{
		if (instance == null)
			instance = new PopupWindow();
		return instance;
	}
	
	public static void loadAssets(AssetWatcher watcher)
	{
		popupImage = assets().getImage(PATH);
		watcher.add(popupImage);
	}
	
	public boolean click(IPoint p)
	{
		return BUTTON.contains(p);
	}
	
	public void show(String message)
	{
		if (visible())
			hide();
		
		popupLayer.setVisible(true);
		
		String[] tokenized = splitMessage(message, LINE_LENGTH);
		int currentIndex = 0;
		
		for (int i = 0; i < tokenized.length; i++)
		{
			Text text = new Text(tokenized[i], Font.Style.BOLD, 14, 0xFFFFFFFF);
			text.setTranslation(TEXT_WINDOW.x() + TEXT_WINDOW.width() / 2 - text.width() / 2, TEXT_WINDOW.y() + HEIGHT_OFFSET * currentIndex++);
			if (text.toString().contains("\n"))
				currentIndex++;
			
			text.setDepth(DEPTH + 1);
			text.setVisible(true);
			text.init();
			texts.add(text);
		}
	}
	
	public void show(String message, int size)
	{
		if (visible())
			hide();
		
		popupLayer.setVisible(true);
		
		String[] tokenized = splitMessage(message, LINE_LENGTH + 20);
		int currentIndex = 0;
		
		for (int i = 0; i < tokenized.length; i++)
		{
			Text text = new Text(tokenized[i], Font.Style.BOLD, size, 0xFFFFFFFF);
			text.setTranslation(TEXT_WINDOW.x() + TEXT_WINDOW.width() / 2 - text.width() / 2, TEXT_WINDOW.y() - 10 + HEIGHT_OFFSET * currentIndex++);
			if (text.toString().contains("\n"))
				currentIndex++;
			
			text.setDepth(DEPTH + 1);
			text.setVisible(true);
			text.init();
			texts.add(text);
		}
	}
	
	public static String[] splitMessage(String message, int lineSize)
	{
		StringBuilder sb = new StringBuilder(message);
		int i = 0;
		while ((i = sb.indexOf(" ", i + lineSize)) != -1)
			sb.replace(i, i + 1, "\t");
		return sb.toString().split("\t");
	}
	
	public void hide()
	{
		popupLayer.setVisible(false);
		
		for (Text text : texts)
			text.destroy();
	}
	
	public boolean visible()
	{
		return popupLayer.visible();
	}
	
}
