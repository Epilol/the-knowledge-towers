package saga.progetto.tesi.entity.staticentity;

import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;
import playn.core.AssetWatcher;
import playn.core.Font;
import playn.core.Image;
import pythagoras.f.IPoint;
import pythagoras.f.Point;
import saga.progetto.tesi.entity.dynamicentity.equip.Axe;
import saga.progetto.tesi.entity.dynamicentity.equip.Dagger;
import saga.progetto.tesi.entity.dynamicentity.equip.Spear;
import saga.progetto.tesi.entity.dynamicentity.equip.Sword;
import saga.progetto.tesi.media.Text;

public class Weapon extends Item
{
	private static final String ICON_PATH = "images/static_objects/weapon_icons.png";
	private static final String TOOLTIP_PATH = "images/static_objects/tooltip.png";
	private static final IPoint TOOLTIP_POINT = new Point(157.0f, 76.0f);
	private static final IPoint TEXT_POINT = new Point(168.0f, 84.0f);
	private static Image iconImage;
	
	private Text tooltipText;
	private WeaponType type;
	
	public Weapon(WeaponType type, int index)
	{
		super(index, iconImage.subImage(0, getSize().height() * type.getIndex(), getSize().width(), getSize().height()), 
				type.name().charAt(0) + type.name().substring(1).toLowerCase(), TOOLTIP_POINT);
		setTooltipLayer(graphics().createImageLayer(assets().getImage(TOOLTIP_PATH)));
		getTooltipLayer().setVisible(false);
		getTooltipLayer().setDepth(7.0f);
		graphics().rootLayer().add(getTooltipLayer());
		this.type = type;
		initText();
	}
	
	public enum WeaponType
	{
		DAGGER(0), SWORD(1), AXE(2), SPEAR(3);
		
		private int index;
		
		WeaponType(int index)
		{
			this.index = index;
		}

		public int getIndex()
		{
			return index;
		}
	}
	
	public static void loadAssets(AssetWatcher watcher)
	{
		iconImage = assets().getImage(ICON_PATH);
		watcher.add(iconImage);
	}
	
	public void initText()
	{
		switch (type)
		{
			case DAGGER : tooltipText = new Text(Dagger.getTooltip(), Font.Style.PLAIN, 12, 0xFFFFFFFF); break;
			case SWORD: tooltipText = new Text(Sword.getTooltip(), Font.Style.PLAIN, 12, 0xFFFFFFFF); break;
			case AXE: tooltipText = new Text(Axe.getTooltip(), Font.Style.PLAIN, 12, 0xFFFFFFFF); break;
			case SPEAR: tooltipText = new Text(Spear.getTooltip(), Font.Style.PLAIN, 12, 0xFFFFFFFF); break;
		}
		
		tooltipText.setVisible(false);
		tooltipText.setTranslation(TEXT_POINT.add(0.0f, index() * 30.0f));
		tooltipText.setDepth(8.0f);
		tooltipText.setAlpha(0.8f);
		tooltipText.init();
	}
	
	public WeaponType getWeaponType()
	{
		return type;
	}

	@Override
	public void translateTooltip()
	{
		super.translateTooltip();
		tooltipText.setTranslation(TEXT_POINT.add(0.0f, index() * 30.0f));
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
		tooltipText.setVisible(!hide);
	}
	
	@Override
	public String toString()
	{
		return type.toString();
	}
	
	@Override
	public void clear()
	{
		super.clear();
		tooltipText.destroy();
	}
}