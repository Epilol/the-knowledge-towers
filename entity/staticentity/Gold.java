package saga.progetto.tesi.entity.staticentity;

import static playn.core.PlayN.assets;
import playn.core.AssetWatcher;
import playn.core.Image;

public class Gold extends Item
{
	private static final String ICON_PATH = "images/static_objects/gold_icon.png";
	private static Image iconImage;
	
	private float golds;
	
	public Gold(float golds, int index)
	{
		super(index, iconImage,String.valueOf(golds).replaceAll("\\..*$", "") + " golds");
		this.golds = golds;
	}
	
	public static void loadAssets(AssetWatcher watcher)
	{
		iconImage = assets().getImage(ICON_PATH);
		watcher.add(iconImage);
	}

	public float getGolds()
	{
		return golds;
	}

	public void setGolds(float golds)
	{
		this.golds = golds;
	}
	
	@Override
	public void translateTooltip()
	{
		
	}
	
	@Override
	public void hideTooltip(boolean hide)
	{
		
	}

	@Override
	public void showItem(boolean visible)
	{
		super.setVisible(visible);
	}
	
	@Override
	public String toString()
	{
		return String.valueOf(golds);
	}
}
