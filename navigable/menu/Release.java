package saga.progetto.tesi.navigable.menu;

import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;
import playn.core.AssetWatcher;
import playn.core.Image;
import playn.core.ImageLayer;
import pythagoras.f.Circle;
import pythagoras.f.Dimension;
import pythagoras.f.ICircle;
import pythagoras.f.IRectangle;
import pythagoras.f.Point;
import pythagoras.f.Rectangle;

public class Release
{
	private static final String LADDER_PATH =  "images/menu/release.png";
	public static final ICircle RELEASE_CROSS = new Circle(new Point(565.0f, 101.0f), 12.0f);
	public static final IRectangle BUTTON = new Rectangle(new Point(348.0f, 272.0f), new Dimension(106.0f, 22.0f));
	private static Release instance;
	private static Image releaseImage;
	
	private ImageLayer releaseLayer;
	
	private Release()
	{
		releaseLayer = graphics().createImageLayer(releaseImage);
		releaseLayer.setVisible(false);
		releaseLayer.setDepth(2.0f);
		graphics().rootLayer().add(releaseLayer);
	}
	
	public static Release getInstance()
	{
		if (instance == null)
			instance = new Release();
		
		return instance;
	}
	
	public static void loadAssets(AssetWatcher watcher)
	{
		releaseImage = assets().getImage(LADDER_PATH);
		watcher.add(releaseImage);
	}

	public void setVisible (boolean visible)
	{
		releaseLayer.setVisible(visible);
	}
	
	public boolean visible()
	{
		return releaseLayer.visible();
	}
	
	public void clear()
	{
		releaseLayer.destroy();
	}
}
