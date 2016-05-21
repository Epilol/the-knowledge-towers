package saga.progetto.tesi.navigable.button;

import static playn.core.PlayN.assets;
import playn.core.Image;
import pythagoras.f.Dimension;
import pythagoras.f.IPoint;
import pythagoras.f.IRectangle;
import pythagoras.f.Rectangle;
import saga.progetto.tesi.navigable.Navigable;

// la classe rappresenta un bottone per la conferma o l'annullamento di un'operazione.
public class ConfirmButton extends Button 
{
	private static final String ACCEPT_BUTTON = "images/menu/other_buttons/start_button.png";
	private static final String BACK_BUTTON = "images/menu/other_buttons/back_button.png";
	private static final String OVERLAY_PATH = "images/menu/other_buttons/confirm_button_overlay.png";
	private static final Dimension ACCEPT_SIZE = new Dimension(87, 33);
	private static final Dimension BACK_SIZE = new Dimension(67,25);
	private static Image accept;
	private static Image back;
	private static Image overlayImage;
	
	private Dimension size;
	private IRectangle hitBox;
	
	public ConfirmButton(Navigable nextState, ButtonType buttonType) 
	{
		super(nextState);
		
		if (buttonType == ButtonType.ACCEPT)
		{
			size = ACCEPT_SIZE;
			setButtonLayer(accept.subImage(0, size.height * buttonType.getIndex(), size.width, size.height));
		}
		
		if (buttonType == ButtonType.BACK)
		{
			size = BACK_SIZE;
			setButtonLayer(back.subImage(0, size.height * buttonType.getIndex(), size.width, size.height));
		}
		
		setOverlayLayer(overlayImage.subImage(0, ACCEPT_SIZE.height * buttonType.getIndex(), ACCEPT_SIZE.width, ACCEPT_SIZE.height));
	}
	
	// carica gli asset in modo sincrono.
	public static void loadAssets() 
	{
		accept = assets().getImage(ACCEPT_BUTTON);
		back = assets().getImage(BACK_BUTTON);
		overlayImage = assets().getImage(OVERLAY_PATH);
	}
	
	@Override
	public boolean intersects(IPoint point) 
	{
		return hitBox.contains(point);
	}
	
	@Override
	public void setHitBox(IPoint point) 
	{
		hitBox = new Rectangle(point, size);
	}
}
