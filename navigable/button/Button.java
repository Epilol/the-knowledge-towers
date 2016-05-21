package saga.progetto.tesi.navigable.button;

import playn.core.Image;
import playn.core.ImageLayer;
import pythagoras.f.IPoint;
import saga.progetto.tesi.navigable.Navigable;
import static playn.core.PlayN.graphics;

// rappresenta un bottone generico.
public abstract class Button 
{
	private Navigable nextState;
	private ImageLayer buttonLayer;
	private ImageLayer overlayLayer;
	
	public Button(Navigable nextState)
	{
		this.nextState = nextState;
	}
	
	// indica la tipologia di bottone.
	public enum ButtonType
	{
		NEW(0), LOAD(1), CREDITS(2),
		ACCEPT(0), BACK(0),
		PLUS(0), MINUS(1);
		
		private int index;
		
		ButtonType(int index)
		{
			this.index = index;
		}
		
		public int getIndex()
		{
			return index;
		}
	}
	
	// ritorna lo stato linkato al bottone.
	public Navigable getNextState()
	{
		return nextState;
	}
	
	// ritorna il layer associato al bottone.
	public ImageLayer getButtonLayer()
	{
		return buttonLayer;
	}
	
	
	// ritorna il layer di overlay del bottone.
	public ImageLayer getOverlayLayer()
	{
		return overlayLayer;
	}
	
	// aggiunge l'immagine al bottone.
	public void setButtonLayer(Image buttonImage)
	{
		buttonLayer = graphics().createImageLayer(buttonImage);
		buttonLayer.setVisible(false);
		buttonLayer.setDepth(2.0f);
		graphics().rootLayer().add(buttonLayer);
	}
	
	// aggiunge l'immagine di overlay al bottone.
	public void setOverlayLayer(Image overlayImage)
	{
		overlayLayer = graphics().createImageLayer(overlayImage);
		overlayLayer.setVisible(false);
		overlayLayer.setDepth(3.0f);
		graphics().rootLayer().add(overlayLayer);
	}
	
	// modifica la visibilità dell'immagine del bottone.
	public void setVisible(boolean visible)
	{
		buttonLayer.setVisible(visible);
		overlayLayer.setVisible(false);
	}
	
	// modifica la visibilità dell'immagine di overlay.
	public void mouseOver(boolean visible)
	{
		overlayLayer.setVisible(visible);
	}
	
	// posiziona il bottone
	public void setTranslation(IPoint p)
	{
		buttonLayer.setTranslation(p.x(), p.y());
		overlayLayer.setTranslation(p.x(), p.y());
		setHitBox(p);
	}
	
	// carica gli assets.
	public static void loadAssets()
	{
		MainMenuButton.loadAssets();
		ConfirmButton.loadAssets();
		CharCreationButton.loadAssets();
	}
	
	// ritorna vero se il punto associato al click del mouse si trova all'interno del bottone.
	public abstract boolean intersects(IPoint point);
	
	
	// modifica la hitbox del bottone.
	public abstract void setHitBox(IPoint point);
}
