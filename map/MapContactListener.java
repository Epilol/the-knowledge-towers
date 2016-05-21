package saga.progetto.tesi.map;

import java.util.ArrayList;
import java.util.List;
import org.jbox2d.callbacks.ContactImpulse;
import org.jbox2d.callbacks.ContactListener;
import org.jbox2d.collision.Manifold;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.contacts.Contact;

public class MapContactListener implements ContactListener
{
	private List<Contact> contacts = new ArrayList<Contact>();
	
	@Override
    public void beginContact(Contact contact) 
	{
		contacts.add(contact);
    }

    @Override
    public void endContact(Contact contact) 
    {
    	contacts.remove(contact);
    }
    
    public boolean isColliding(Body b)
    {
    	for (Contact contact : contacts)
    		if (contact.getFixtureA().equals(b.getFixtureList()) || contact.getFixtureB().equals(b.getFixtureList())) 
    			return true;
    	return false;
    }

    public boolean isColliding(Body b1, Body b2)
    {
    	for (Contact contact : contacts)
    	{
    		if (contact.getFixtureA().equals(b1.getFixtureList()) && contact.getFixtureB().equals(b2.getFixtureList())
    				|| contact.getFixtureA().equals(b2.getFixtureList()) && contact.getFixtureB().equals(b1.getFixtureList()))
    			return true;
    	}
    	return false;
    }
    
	public void preSolve(Contact contact, Manifold oldManifold)
	{
		// TODO Auto-generated method stub	
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse)
	{
		// TODO Auto-generated method stub
	}
}
