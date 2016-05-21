package saga.progetto.tesi.navigable.menu;

import java.util.LinkedList;
import java.util.List;
import saga.progetto.tesi.core.TheKnowledgeTowers;
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.media.Text;
import saga.progetto.tesi.navigable.Gameloop;

public class ItemMenu extends GameMenu
{
	private static final int MENU_INDEX = 1;
	
	private List<Text> staticText;
	
	public ItemMenu(TheKnowledgeTowers game, Player player, Gameloop gameloop)
	{
		super(game, player, gameloop);
		staticText = new LinkedList<Text>();
		init();
	}
	
	@Override
	public void init()
	{
		super.init();
		staticText.add(initText("Item Menu", 80.0f, 50.0f, 22, false));
	}
	
	@Override
	public int getMenuIndex()
	{
		return MENU_INDEX;
	}
	
	@Override
	public void setVisible(boolean visible)
	{
		super.setVisible(visible);
		
		for (Text text : staticText)
			text.setVisible(visible);
	}

}