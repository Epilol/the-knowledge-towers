package saga.progetto.tesi.map.cell;

import static playn.core.PlayN.graphics;
import org.jbox2d.dynamics.BodyType;
import playn.core.AssetWatcher;
import playn.core.ImageLayer;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import saga.progetto.tesi.entity.staticentity.StaticEntity;
import saga.progetto.tesi.map.GameMap;

public abstract class Cell extends StaticEntity
{
	private static final IDimension SIZE = new Dimension(32, 32);
	
	public Cell(float x, float y, Material material, GameMap map)
	{
		super(x, y, map);
		initPhysicalBody(BodyType.STATIC, getWidth(), getHeight(), material, 0x0032, 0xFFFF & ~0x0029);
	}

	public static void loadAssets(AssetWatcher watcher)
	{
		HouseFloor.loadAssets(watcher);
		Wall.loadAssets(watcher);
		HouseDecoration.loadAssets(watcher);
		OutsideFloor.loadAssets(watcher);
		OutsideDecoration.loadAssets(watcher);
		FirstTowerFloor.loadAssets(watcher);
		FirstTowerDecoration.loadAssets(watcher);
		SecondTowerFloor.loadAssets(watcher);
		SecondTowerDecoration.loadAssets(watcher);
		ThirdTowerFloor.loadAssets(watcher);
		ThirdTowerDecoration.loadAssets(watcher);
		FourthTowerFloor.loadAssets(watcher);
		FourthTowerDecoration.loadAssets(watcher);
		FifthTowerFloor.loadAssets(watcher);
		FifthTowerDecoration.loadAssets(watcher);
		SixthTowerFloor.loadAssets(watcher);
		SixthTowerDecoration.loadAssets(watcher);
		SeventhTowerFloor.loadAssets(watcher);
		SeventhTowerDecoration.loadAssets(watcher);
		EighthTowerFloor.loadAssets(watcher);
		EighthTowerDecoration.loadAssets(watcher);
		NinthTowerFloor.loadAssets(watcher);
		NinthTowerDecoration.loadAssets(watcher);
		TenthTowerFloor.loadAssets(watcher);
		TenthTowerDecoration.loadAssets(watcher);
		MagmatronFloor.loadAssets(watcher);
	}
	
	public void gfxInit(float  xOffset, float yOffset)
	{
		setLayer(graphics().createImageLayer(getImage().subImage(xOffset * Cell.WIDTH, yOffset * Cell.HEIGHT, WIDTH, HEIGHT)));
		getLayer().setTranslation(x() - SIZE.width() / 2, y() - SIZE.height() / 2);
	}
	
	@Override
	public IDimension getSize()
	{
		return SIZE;
	}
	
	@Override
	public void clear()
	{
		super.clear();
		getLayer().destroy();
	}
	
	public abstract void setLayer(ImageLayer layer);
}
