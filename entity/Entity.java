package saga.progetto.tesi.entity;

import playn.core.Layer;
import pythagoras.f.IDimension;

// rappresenta una qualsiasi entità del gioco, statica o dinamica.
public abstract class Entity
{
	// vero se l'entità è visibile
	private boolean visible;
	
	// modifica la visibilità dell'entità con il parametro in input
	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}
	
	// ritorna vero se l'entità è visibile
	public boolean visible()
	{
		return visible;
	}
	
	public abstract float x();
	
	public abstract float y();
	
	// ritorna l'immediatelayer associato all'entità.
	public abstract Layer getLayer();
	
	public abstract IDimension getSize();
}
