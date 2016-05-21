package saga.progetto.tesi.entity.staticentity;

import playn.core.AssetWatcher;
import playn.core.Image;
import pythagoras.f.IPoint;
import pythagoras.f.Point;
import saga.progetto.tesi.media.Text;

public abstract class Potion extends Item
{
	private static final IPoint TOOLTIP_POINT = new Point(157.0f, 76.0f);
	protected static final IPoint TEXT_POINT = new Point(168.0f, 84.0f);
	
	private Text tooltipText;
	private PotionType type;
	
	public Potion(PotionType type, int index, Image iconImage)
	{
		super(index, iconImage.subImage(0, getSize().height() * type.getIndex(), getSize().width(), getSize().height()), 
		type.name().charAt(0) + type.name().substring(1).toLowerCase().replace("_", " "), TOOLTIP_POINT);
		this.type = type;
	}
	
	public static void loadAssets(AssetWatcher watcher)
	{
		LifePotion.loadAssets(watcher);
		ManaPotion.loadAssets(watcher);
		InvisibilityPotion.loadAssets(watcher);
	}
	
	public abstract void initText();
	
	public enum PotionType
	{
		MINOR_LIFE_POTION(0, 15), LIFE_POTION(1, 25), MAJOR_LIFE_POTION(2, 50),
		MINOR_MANA_POTION(0, 10), MANA_POTION(1, 20), MAJOR_MANA_POTION(2, 30),
		STEALTH_POTION(0,0);
		
		private int index;
		private float value;
		
		PotionType(int index, float value)
		{
			this.index = index;
			this.value = value;
		}

		public int getIndex()
		{
			return index;
		}
		
		public float getValue()
		{
			return value;
		}
	}
	
	@Override
	public void translateTooltip()
	{
		super.translateTooltip();
		tooltipText.setTranslation(TEXT_POINT.add(0.0f, index() * 30.0f));
	}
	
	public float getLifeAmount()
	{
		return type.getValue();
	}
	
	
	public float getManaAmount()
	{
		return 0.0f;
	}
	
	public void special()
	{
		
	}
	
	public Text getTooltipText()
	{
		return tooltipText;
	}
	
	public void setTooltipText(Text tooltipText)
	{
		this.tooltipText = tooltipText;
	}
	
	@Override
	public void hideTooltip(boolean hide)
	{
		getTooltipLayer().setVisible(!hide);
		tooltipText.setVisible(!hide);
	}
	
	@Override
	public void clear()
	{
		super.clear();
		tooltipText.destroy();
	}
}
