package saga.progetto.tesi.gui;

import playn.core.AssetWatcher;
import playn.core.ImageLayer;
import playn.core.Layer;

public abstract class Bar extends GUIComponent
{
	private ImageLayer barLayer;
	private float current;
	
	public float getCurrent()
	{
		return current;
	}

	public void setCurrent(float current)
	{
		this.current = current;
	}
	
	
	public static void loadAssets(AssetWatcher watcher) 
	{
		PlayerBar.loadAssets(watcher);
		NPCBar.loadAssets(watcher);
	}
	
	@Override
	public Layer getLayer()
	{
		return barLayer;
	}
	
	public void setLayer(ImageLayer barLayer)
	{
		this.barLayer = barLayer;
	}
	
	public void setVisible(boolean visible)
	{
		barLayer.setVisible(visible);
	}
	
	public abstract void update(float current);
	
}
