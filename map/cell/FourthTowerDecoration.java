package saga.progetto.tesi.map.cell;

import static playn.core.PlayN.assets;
import playn.core.AssetWatcher;
import playn.core.Image;
import playn.core.ImageLayer;
import saga.progetto.tesi.map.GameMap;

public class FourthTowerDecoration extends Cell
{
	private static final String DECORATION_PATH = "images/cell/tower_decoration.png";
	private static Image decorationImage;
	
	private ImageLayer decorationLayer;
	
	public FourthTowerDecoration(float x, float y, GameMap map)
	{
		super(x, y, Material.STONE, map);
	}
	
	// carica gli assets della cella attraverso la classe Tile.
	public static void loadAssets(AssetWatcher watcher) 
	{
		decorationImage = assets().getImage(DECORATION_PATH);
		watcher.add(decorationImage);
	}
	
	// ritorna il layer associato al muro.
	@Override
	public ImageLayer getLayer()
	{
		return decorationLayer;
	}
	
	// ritorna il layer associato al muro.
	@Override
	public void setLayer(ImageLayer decorationLayer)
	{
		this.decorationLayer = decorationLayer;
	}
	
	@Override
	// ritorna il layer associato al muro.
	public Image getImage()
	{
		return decorationImage;
	}
}
