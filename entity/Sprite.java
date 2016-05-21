package saga.progetto.tesi.entity;

import playn.core.Image;
import saga.progetto.tesi.entity.dynamicentity.Direction;

// la classe serve per la gestione delle entità animate.
public class Sprite 
{
	// la sheet associata alla sprite.
	private Image spriteSheet;
	// i campi indicano la durata e le dimensioni di un frame.
	private float frameTime;
	private float frameWidth;
	private float frameHeight;
	private int elapsed;
	// indica la direzione corrente verso la quale la sprite è rivolta.
	private Direction lastDirection;
	
	public Sprite(Image spriteSheet, float frameTime, float frameWidth, float frameHeight) 
	{
		this.spriteSheet = spriteSheet;
	    this.frameTime = frameTime;
	    this.frameWidth = frameWidth;
	    this.frameHeight = frameHeight;
	    lastDirection = Direction.DEFAULT;
	}
	
	// ritorna l'immagine da mostrare rispetto all'animazione corrente.
	public Image getCurrentImage(Animation animation)
	{
		return spriteSheet.subImage(getCurrentFrame(animation.getCoefficient()), (animation.getIndex() + lastDirection.getUtilityIndex()) * frameHeight, frameWidth, frameHeight);
	}
	
	public Image getSpriteImage()
	{
		return spriteSheet;
	}
	
	// ritorna la coordinata orizzontale del frame corrente.
	public float getCurrentFrame(int coefficient)
	{
		return ((int) (elapsed % (frameTime * coefficient * getFramesPerLine()) / (frameTime * coefficient))) * frameWidth;
	}
	
	// ritorna il numero di frame appartenenti a ciascuna animazione di una sprite.
	private float getFramesPerLine() 
	{
		return spriteSheet.width() / frameWidth;
	}
	
	// ritorna vero ogni qual volta l'animazione termina.
	public boolean isOver(Animation animation)
	{
		return elapsed >= frameTime * animation.getCoefficient() * (getFramesPerLine());
	}
	
	public boolean isOver(Animation animation, boolean cutFrame)
	{ 
		return cutFrame ? elapsed >= frameTime * animation.getCoefficient() * (getFramesPerLine() - 1 ) : isOver(animation);
	}
	
	public Direction getLastDirection()
	{
		return lastDirection;
	}
	
	// aggiorna la direzione della sprite
	public void setLastDirection(Direction direction) 
	{
		this.lastDirection = direction;
	}
	
	// riporta a 0 il tempo trascorso. Viene utilizzato per permettere alla sprite di ricominciare dall'inizio quando cambia animazione.
	public void resetDelta()
	{
		elapsed = 0;
	}

	// permette alla sprite di aggiornarsi. Il metodo è chiamato dalla mappa di gioco attuale ad ogni update.
	public void update(int delta)
	{
		elapsed += delta;
	}
}

