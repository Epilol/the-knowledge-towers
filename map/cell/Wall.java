package saga.progetto.tesi.map.cell;

import static playn.core.PlayN.assets;
import playn.core.AssetWatcher;
import playn.core.Image;
import saga.progetto.tesi.map.GameMap;

public abstract class Wall extends Cell
{
	private static final String SECRET_PASSAGE_PATH = "images/cell/secret_passage.png";
	private static Image secretPassageImage;

	public Wall(float x, float y, Material material, GameMap map, boolean isHiddenPath)
	{
		super(x, y, material, map);
		
		if (isHiddenPath)
		{
//			ImageLayer secretPassageLayer = graphics().createImageLayer(secretPassageImage);
//			secretPassageLayer.setTranslation(x() - getSize().width() / 2, y() - getSize().height() / 2);
//			map.getThiefAbilityLayer().add(secretPassageLayer);
		}
	}

	public static void loadAssets(AssetWatcher watcher) 
	{
		HouseWall.loadAssets(watcher);
		OutsideWall.loadAssets(watcher);
		FirstTowerWall.loadAssets(watcher);
		SecondTowerWall.loadAssets(watcher);
		ThirdTowerWall.loadAssets(watcher);
		FourthTowerWall.loadAssets(watcher);
		FifthTowerWall.loadAssets(watcher);
		SixthTowerWall.loadAssets(watcher);
		SeventhTowerWall.loadAssets(watcher);
		EighthTowerWall.loadAssets(watcher);
		NinthTowerWall.loadAssets(watcher);
		TenthTowerWall.loadAssets(watcher);
		secretPassageImage = assets().getImage(SECRET_PASSAGE_PATH);
		watcher.add(secretPassageImage);
	}
}
