package saga.progetto.tesi.navigable.button;

import static playn.core.PlayN.assets;
import playn.core.Image;
import pythagoras.f.Dimension;
import pythagoras.f.IPoint;
import pythagoras.f.Rectangle;
import saga.progetto.tesi.navigable.Navigable;

// bottone utilizzato nella creazione del personaggio per modificarne l'aspetto.
public class CharCreationButton extends Button 
{

	private static final String BUTTON_PATH = "images/menu/char_creation_menu/hair_button.png";
	private static final Dimension SIZE = new Dimension(13,13);
	private static Image buttonImage;
	
	private Rectangle hitBox;
	
	public CharCreationButton(Navigable nextState, ButtonType buttonType) 
	{
		super(nextState);
		setButtonLayer(buttonImage.subImage(0, SIZE.height * buttonType.getIndex(), SIZE.width, SIZE.height));
		getButtonLayer().setDepth(1.0f);
	}
	
	// carica gli asset in modo asincrono.
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
}
