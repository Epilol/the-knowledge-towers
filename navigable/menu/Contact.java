package saga.progetto.tesi.navigable.menu;

import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;
import static playn.core.PlayN.platform;
import playn.core.AssetWatcher;
import playn.core.Image;
import playn.core.ImageLayer;
import pythagoras.f.Circle;
import pythagoras.f.Dimension;
import pythagoras.f.ICircle;
import pythagoras.f.IPoint;
import pythagoras.f.IRectangle;
import pythagoras.f.Point;
import pythagoras.f.Rectangle;
import saga.progetto.tesi.core.Version;

public class Contact
{
	private static final String LADDER_PATH =  "images/menu/contact.png";
	public static final ICircle CONTACT_CROSS = new Circle(new Point(564.0f, 133.0f), 12.0f);
	private static Contact instance;
	private static Image contactImage;
	private IRectangle button = new Rectangle(new Point(348.0f, 272.0f), new Dimension(106.0f, 22.0f));
	
	private ImageLayer contactLayer;
	
	private Contact()
	{
		contactLayer = graphics().createImageLayer(contactImage);
		contactLayer.setVisible(false);
		contactLayer.setDepth(18.0f);
		graphics().rootLayer().add(contactLayer);
	}
	
	public static Contact getInstance()
	{
		if (instance == null)
			instance = new Contact();
		
		return instance;
	}
	
	public static void loadAssets(AssetWatcher watcher)
	{
		contactImage = assets().getImage(LADDER_PATH);
		watcher.add(contactImage);
	}

	public void setVisible (boolean visible)
	{
		contactLayer.setVisible(visible);
	}
	
	public boolean visible()
	{
		return contactLayer.visible();
	}
	
	public void clear()
	{
		contactLayer.destroy();
	}
	
	public void setTranslation(float x, float y)
	{
		contactLayer.setTranslation(x, y);
		button = new Rectangle(new Point(button.x() + x, button.y() + y), new Dimension(button.width(), button.height()));
	}
	
	public boolean click(IPoint p)
	{
		return button.contains(p);
	}
	
	public void openURL()
	{
		platform().openURL(Version.getContactAddress());
	}
}
