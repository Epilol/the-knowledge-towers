package saga.progetto.tesi.navigable;

import playn.core.Keyboard.Event;
import playn.core.Mouse.ButtonEvent;

// rappresenta un generico stato di gioco.
public interface Navigable 
{
	// rende visibile o meno uno stato di gioco a seconda del parametro in input
	void setVisible(boolean visible);
	
	// gestisce l'input da mouse
	Navigable onMouseDown(ButtonEvent event);
	
	Navigable onMouseUp(ButtonEvent event);
	
	// gestisce l'input da tastiera
	Navigable onKeyDown(Event event);

	// gestisce l'input da tastiera
	void onKeyUp(Event event);
	
	// chiamato dalla classe TheKnowledgeTowers serve a gestire l'aggiornamento di ciascuno stato di gioco.
	void update(int delta);
}
