package saga.progetto.tesi.gui;

import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;
import playn.core.AssetWatcher;
import playn.core.Image;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import pythagoras.f.IPoint;
import pythagoras.f.Point;

public class PlayerBar extends Bar
{
	private static final String PATH =  "images/gui/player_bar.png";
	private static final String RAGE_PATH =  "images/gui/rage_bar.png";
	private static final String XP_PATH =  "images/gui/xp_bar.png";
	private static final String JOB_PATH = "images/gui/job_xp.png";
	private static final IDimension SIZE = new Dimension(128.0f, 10.0f);
	private static final IDimension RAGE_SIZE = new Dimension(133.0f, 8.0f);
	private static final IDimension XP_SIZE = new Dimension(440.0f, 7.0f);
	private static final IDimension JOB_SIZE = new Dimension(352.0f, 5.0f);
	private static final IPoint HEALTH_POINT = new Point(43.0f, 494.0f);
	private static final IPoint ENDURANCE_POINT = new Point(43.0f, 534.0f);
	private static final IPoint MANA_POINT = new Point(43.0f, 574.0f);
	private static final IPoint RAGE_POINT = new Point(42.0f, 552.0f);
	private static final IPoint XP_POINT = new Point(181.0f, 463.0f);
	protected static final IPoint JOB_POINT = new Point(224.0f, 496.0f);
	private static Image barImage;
	private static Image rageImage;
	private static Image xpImage;
	private static Image jobImage;

	private IPoint position;
	private BarType type;
	private float total;
	
	public PlayerBar(float current, float total, BarType type)
	{
		setCurrent(current);
		this.setTotal(total);
		this.type = type;
		
		if (type == BarType.HEALTH)
			position = HEALTH_POINT;
		
		else if (type == BarType.ENDURANCE)
			position = ENDURANCE_POINT;
		
		else if (type == BarType.MANA)
			position = MANA_POINT;
		
		else if (type == BarType.RAGE)
			position = RAGE_POINT;
		
		else if (type == BarType.XP)
			position = XP_POINT;
		
		else
			position = JOB_POINT;
		
		if (type == BarType.RAGE)
			setLayer(graphics().createImageLayer(rageImage.subImage(0, 0, current * RAGE_SIZE.width() / total, RAGE_SIZE.height())));
		
		else if (type == BarType.XP)
			setLayer(graphics().createImageLayer(xpImage.subImage(0, 0, current * XP_SIZE.width() / total, XP_SIZE.height())));
		
		else if (type == BarType.JOB)
			setLayer(graphics().createImageLayer(jobImage.subImage(0, 0, current * JOB_SIZE.width() / total, JOB_SIZE.height())));
		
		else
			setLayer(graphics().createImageLayer(barImage.subImage(0, SIZE.height() * type.index, 
				current * SIZE.width() / total, SIZE.height())));
		
		getLayer().setVisible(true);
		getLayer().setDepth(9.0f);
		getLayer().setTranslation(position.x(), position.y());
		graphics().rootLayer().add(getLayer());
	}
	
	public enum BarType
	{
		HEALTH(0), ENDURANCE(1), MANA(2), RAGE(3), XP(0), JOB(0);
		
		private int index;
		
		BarType(int index)
		{
			this.index = index;
		}
	}
	
	public static void loadAssets(AssetWatcher watcher) 
	{
		barImage = assets().getImage(PATH);
		rageImage = assets().getImage(RAGE_PATH);
		xpImage = assets().getImage(XP_PATH);
		jobImage = assets().getImage(JOB_PATH);
		watcher.add(barImage);
		watcher.add(rageImage);
		watcher.add(xpImage);
		watcher.add(jobImage);
	}
	
	public BarType getType()
	{
		return type;
	}

	public float getTotal()
	{
		return total;
	}

	public void setTotal(float total)
	{
		this.total = total;
	}
	
	public void updateBar(float current, boolean isVisible)
	{
		if (current < 1.0f)
		{
			setCurrent(0.0f);
			getLayer().setVisible(false);
		}
		
		else
		{
			setCurrent(current);
			graphics().rootLayer().remove(getLayer());
			
			if (type == BarType.RAGE)
				setLayer(graphics().createImageLayer(rageImage.subImage(0, 0, current * RAGE_SIZE.width() / total, RAGE_SIZE.height())));
			
			else if (type == BarType.XP)
				setLayer(graphics().createImageLayer(xpImage.subImage(0, 0, current * XP_SIZE.width() / total, XP_SIZE.height())));
			
			else if (type == BarType.JOB)
				setLayer(graphics().createImageLayer(jobImage.subImage(0, 0, current * JOB_SIZE.width() / total, JOB_SIZE.height())));
			
			else
				setLayer(graphics().createImageLayer(barImage.subImage(0, SIZE.height() * type.index, 
					current * SIZE.width() / total, SIZE.height())));
			
			getLayer().setVisible(isVisible);
			getLayer().setDepth(9.0f);
			getLayer().setTranslation(position.x(), position.y());
			graphics().rootLayer().add(getLayer());
		}
	}
	
	public void updateBar(float current)
	{
		updateBar(current, true);
	}
	
	public void clear()
	{
		graphics().rootLayer().remove(getLayer());
		getLayer().destroy();
	}
	
	@Override
	public String toString()
	{
		return "[" + type + ", depth: " + getLayer().depth() + ", visible: " + getLayer().visible() + ", current: " + getCurrent() +
				", total" + getTotal() + ", tx: " + getLayer().tx() + ", ty: " + getLayer().ty() + "]";
	}
	
	@Override
	public void update(float current)
	{
		if (current != getCurrent())
			updateBar(current);
	}
}
