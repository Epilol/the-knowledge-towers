package saga.progetto.tesi.entity;

import playn.core.Image;
import playn.core.Surface;
import pythagoras.f.IPoint;

// L'interfaccia Renderable viene utilizzata da tutte quelle entitò che contengono animazioni.
public interface Renderable 
{
	// viene utilizzato per disegnare il frame corrente. E' chiamato dal metodo render di un'immediate layer.
	void drawFrame(Surface surface);
	
	// ritorna un IPoint contenente l'angolo in alto a sinistra di un'immagine che deve essere disegnata.
	IPoint getFrameLocation();
	
	// ritorna la sprite associata.
	Sprite getSprite();
	
	// assegna la sprite associata.
	void setSprite(Sprite sprite);

	// ritorna la subimage contenente il frame della sprite che deve essere disegnato.
	Image getCurrentFrame();
	
	// pulisce la grafica dell'entità.
	void clear();

	// assegna l'animazione corrente all'entità.
	void setCurrentAnimation(Animation currentAnimation);
	
	// ritorna l'animazione corrente dell'entità
	Animation getCurrentAnimation();
	
	// ritorna la durata di ciascun frame di un entità.
	float getFrameDuration();
	
	// permette all'entità di aggiornarsi. Il metodo è chiamato dalla mappa di gioco attuale.
	void update(int delta);
}
