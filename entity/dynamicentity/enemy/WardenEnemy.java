package saga.progetto.tesi.entity.dynamicentity.enemy;

import static playn.core.PlayN.assets;
import playn.core.AssetWatcher;
import playn.core.Image;
import saga.progetto.tesi.entity.dynamicentity.Direction;
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.map.GameMap;

public class WardenEnemy extends Warden
{
	private static final String WARDEN_PATH = "images/characters/enemies/warden.png";
	private static Image wardenImage;
	private static final float TOTAL_LIFE = 12.0f;
	
	public WardenEnemy(float x, float y, Direction direction, Player player, GameMap map, boolean hasBeenKilled, String id)
	{
		super(x, y, direction, player, map, hasBeenKilled, id, TOTAL_LIFE);
	}

	public static void loadAssets(AssetWatcher watcher) 
	{
		wardenImage = assets().getImage(WARDEN_PATH);
		watcher.add(wardenImage);
	}
	
	public Image getImage()
	{
		return wardenImage;
	}
}
