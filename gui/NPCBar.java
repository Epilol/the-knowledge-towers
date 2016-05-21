package saga.progetto.tesi.gui;

import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;
import playn.core.AssetWatcher;
import playn.core.Image;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import saga.progetto.tesi.entity.dynamicentity.DynamicEntity;

public class NPCBar extends Bar
{
	private static final String PATH =  "images/characters/enemies/health_bar.png";
	private static final float HEIGHT = 3.0f;
	private static Image barImage;
	
	private float total;
	private DynamicEntity entity;
	private IDimension size;
	
	public NPCBar(DynamicEntity entity, IDimension size)
	{
		this.entity = entity;
		this.size = new Dimension(entity.getSize().width(), HEIGHT);
		setCurrent(entity.getCurrentLife());
		this.total = entity.getTotalLife();
		setLayer(graphics().createImageLayer(barImage.subImage(0, 0, getCurrent() * this.size.width() / total, this.size.height())));
		getLayer().setVisible(true);
		getLayer().setDepth(4.0f);
		getLayer().setTranslation(entity.getFrameLocation().x() - size.width() / 2, 
				entity.getFrameLocation().y() - (entity.getSize().height() / 2 + this.size.height() * 2));
		graphics().rootLayer().add(getLayer());
	}
	
	public static void loadAssets(AssetWatcher watcher) 
	{
		barImage = assets().getImage(PATH);
		watcher.add(barImage);
	}
	
	public void update(float current)
	{
		if (Math.round((current / entity.getTotalLife()) * size.width()) != getCurrent())
		{
			if (entity.getCurrentLife() >= 0.0f)
			{
				setCurrent(entity.getCurrentLife());
				graphics().rootLayer().remove(getLayer());
				setLayer(graphics().createImageLayer(barImage.subImage(0, 0, getCurrent() * size.width() / total, size.height())));
				getLayer().setVisible(true);
				getLayer().setDepth(4);
				graphics().rootLayer().add(getLayer());
			}
			
			else
				setCurrent(0.0f);
		}
		getLayer().setTranslation(entity.getFrameLocation().x() - size.width() / 2, 
				entity.getFrameLocation().y() - (entity.getSize().height() / 2 + size.height() * 2));
	}
}
