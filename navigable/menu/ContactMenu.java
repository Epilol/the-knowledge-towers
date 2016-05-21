package saga.progetto.tesi.navigable.menu;

import java.util.LinkedList;
import java.util.List;
import playn.core.Key;
import playn.core.Keyboard.Event;
import playn.core.Mouse;
import playn.core.Mouse.ButtonEvent;
import pythagoras.f.Point;
import saga.progetto.tesi.core.TheKnowledgeTowers;
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.media.Text;
import saga.progetto.tesi.navigable.Gameloop;
import saga.progetto.tesi.navigable.Navigable;

public class ContactMenu extends GameMenu
{
	private static final int MENU_INDEX = 6;
	
	private List<Text> staticText;
	
	public ContactMenu(TheKnowledgeTowers game, Player player, Gameloop gameloop)
	{
		super(game, player, gameloop);
		staticText = new LinkedList<Text>();
		init();
		Contact.getInstance().setTranslation(-105, 55);
	}
	
	@Override
	public void init()
	{
		super.init();
		staticText.add(initText("Contact", 80.0f, 50.0f, 22, false));
	}
	
	@Override
	public int getMenuIndex()
	{
		return MENU_INDEX;
	}
	
	@Override
	public Navigable onMouseDown(ButtonEvent event)
	{
		if (event.button() == Mouse.BUTTON_LEFT && Contact.getInstance().click(new Point(event.localX(), event.localY())))
			Contact.getInstance().openURL();
		
		return super.onMouseDown(event);
	}
	
	@Override
	public Navigable onKeyDown(Event event)
	{
		if (event.key() == Key.O)
			Contact.getInstance().openURL();
		
		return super.onKeyDown(event);
	}
	
	@Override
	public void setVisible(boolean visible)
	{
		super.setVisible(visible);
		
		Contact.getInstance().setVisible(visible);
		
		for (Text text : staticText)
			text.setVisible(visible);
	}

}