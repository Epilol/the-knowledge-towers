package saga.progetto.tesi.navigable.button;

import static playn.core.PlayN.assets;
import playn.core.Image;
import pythagoras.f.Dimension;
import pythagoras.f.IPoint;
import pythagoras.f.Rectangle;
import saga.progetto.tesi.navigable.Navigable;

// rappresenta un bottone
public class MainMenuButton extends Button 
{
	private static final String BUTTON_PATH = "images/menu/main_menu/main_button.png";
	private static final Dimension SIZE = new Dimension(204,15);
	private static Image buttonImage;
	
	private Rectangle hitBox;
	
	public MainMenuButton(Navigable nextState, ButtonType buttonType) 
	{
		super(nextState);
		setButtonLayer(buttonImage.subImage(0, SIZE.height * buttonType.getIndex(), SIZE.width, SIZE.height));
		getButtonLayer().setDepth(1.0f);
	}
	
	// carica gli assets
	public static void loadAssets() 
	{
		buttonImage = assets().getImage(BUTTON_PATH);
	}

	@Override
	public boolean intersects(IPoint point) 
	{
		return hitBox.contains(point);
	}
	
	@Override
	public void setHitBox(IPoint point) 
	{
		hitBox = new Rectangle(point, SIZE);
	}
	
	@Override
	public void setTranslation(IPoint p)
	{
		getButtonLayer().setTranslation(p.x(), p.y());
		setHitBox(p);
	}
	
	@Override
	public void setVisible(boolean visible)
	{
		getButtonLayer().setVisible(visible);
	}
}
