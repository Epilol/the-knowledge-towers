package saga.progetto.tesi.entity.dynamicentity.enemy;

import static playn.core.PlayN.assets;
import playn.core.AssetWatcher;
import playn.core.Image;
import saga.progetto.tesi.entity.dynamicentity.Direction;
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.map.GameMap;

public class WardenBoss extends Warden
{
	private static final String WARDEN_PATH = "images/characters/enemies/warden_boss.png";
	private static Image wardenImage;
	private static final float TOTAL_LIFE = 25.0f;
	
	public WardenBoss(float x, float y, Direction direction, Player player, GameMap map, boolean hasBeenKilled, String id)
	{
		super(x, y, direction, player, map, hasBeenKilled, id, TOTAL_LIFE);
	}

	public static void loadAssets(AssetWatcher watcher) 
	{
		wardenImage = assets().getImage(WARDEN_PATH);
		watcher.add(wardenImage);
	}
	
	@Override
	public Image getImage()
	{
		return wardenImage;
	}
}