package saga.progetto.tesi.entity.dynamicentity.equip;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.BodyType;
import playn.core.AssetWatcher;
import playn.core.Surface;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import saga.progetto.tesi.entity.Animation;
import saga.progetto.tesi.entity.dynamicentity.Direction;
import saga.progetto.tesi.entity.dynamicentity.DynamicEntity;
import saga.progetto.tesi.entity.dynamicentity.GroupRenderedDynamicEntity;
import saga.progetto.tesi.map.GameMap;

public abstract class Equip extends GroupRenderedDynamicEntity
{
	private static final IDimension LAYER_SIZE = new Dimension(80.0f, 80.0f);
	private static final float DEPTH = 1.1f;
	private static final float HIDDEN_DEPTH = -1.1f;
	private static final float ENEMY_DEPTH = 1.2f;
	private static final float ENEMY_HIDDEN_DEPTH = -1.2f;
	
	private Material material;
	private IDimension layerSize;
	private boolean isEnemyEquip;
	private float currentHiddenDepth;
	private float currentVisibleDepth;
	
	public Equip(float x, float y, IDimension size, GameMap map, Material material, boolean isEnemyEquip, IDimension layerSize)
	{
		super(x, y, size, map);
		this.material = material;
		this.isEnemyEquip = isEnemyEquip;
		
		if (isEnemyEquip)
		{
			initPhysicalBody(BodyType.DYNAMIC, getSize().width(), getSize().height(), material, 0x0016, 0xFFFF & ~0x0004);
			currentHiddenDepth = ENEMY_HIDDEN_DEPTH;
			currentVisibleDepth = ENEMY_DEPTH;
			map.getGameloop().removeRenderable(this, 0.0f);
			setDepth(currentVisibleDepth);
			map.getGameloop().addRenderable(this, currentVisibleDepth);
		}
		
		else
		{
			initPhysicalBody(BodyType.DYNAMIC, size.width(), size.height(), material, 0x0008, 0xFFFF & ~0x0002 & 0x0004);
			currentHiddenDepth = HIDDEN_DEPTH;
			currentVisibleDepth = DEPTH;
			map.getGameloop().removeRenderable(this, 0.0f);
			setDepth(currentVisibleDepth);
			map.getGameloop().addRenderable(this, currentVisibleDepth);
		}
		
		this.layerSize = layerSize;
		setVisible(true);
	}
	
	public Equip(float x, float y, IDimension size, GameMap map, Material material, boolean isEnemyEquip)
	{
		this(x, y, size, map, material, isEnemyEquip, LAYER_SIZE);
	}
	
	public static void loadAssets(AssetWatcher watcher)
	{
		Axe.loadAssets(watcher);
		BlackKnightSword.loadAssets(watcher);
		Bow.loadAssets(watcher);
		Destroyer.loadAssets(watcher);
		Dagger.loadAssets(watcher);
		EnemyWardenSword.loadAssets(watcher);
		Excalibur.loadAssets(watcher);
		Fang.loadAssets(watcher);
		OldWarriorSword.loadAssets(watcher);
		Shield.loadAssets(watcher);
		Sword.loadAssets(watcher);
		Spear.loadAssets(watcher);
		Unarmed.loadAssets(watcher);
	}
	
	public IDimension getLayerSize()
	{
		return layerSize;
	}
	
	public void bodyTransform(DynamicEntity entity, Direction lastDirection)
	{
		getBody().setTransform(entity.getBody().getPosition().add(
				new Vec2(lastDirection.x() * getSize().width() / GameMap.PTM_RATIO, 
						lastDirection.y() * getSize().height() / GameMap.PTM_RATIO)), lastDirection.getAngle());
	}
	
	public void initPhysicalBody()
	{
		if (isEnemyEquip)
			initPhysicalBody(BodyType.DYNAMIC, getSize().width(), getSize().height(), material, 0x0016, 0xFFFF & ~0x0004);
		
		else
			initPhysicalBody(BodyType.DYNAMIC, getSize().width(), getSize().height(), material, 0x0008, 0xFFFF & ~0x0002);
	}
	
	@Override
	public void setLastDirection(Direction lastDirection)
	{
		super.setLastDirection(lastDirection);
		
		if (depth() == currentVisibleDepth && (lastDirection == Direction.UP || lastDirection == Direction.TOP_LEFT || lastDirection == Direction.TOP_RIGHT))
		{
			setDepth(currentHiddenDepth);
			getGameloop().removeRenderable(this, currentVisibleDepth);
			getGameloop().addRenderable(this, currentHiddenDepth);
		}
		
		else if(depth() == currentHiddenDepth && lastDirection != Direction.UP && lastDirection != Direction.TOP_LEFT && lastDirection != Direction.TOP_RIGHT)
		{
			setDepth(currentVisibleDepth);
			getGameloop().removeRenderable(this, currentHiddenDepth);
			getGameloop().addRenderable(this, currentVisibleDepth);
		}
		
		getSprite().setLastDirection(lastDirection);
	}
	
	@Override
	public void setVisible(boolean visible)
	{
		if (visible)
			setCurrentAnimation(Animation.WEAPON);
		
		else
			setCurrentAnimation(Animation.NO_WEAPON);
	}

	@Override
	public void drawFrame(Surface surface)
	{
		if (getFrameLocation() != null)
			surface.drawImage(getCurrentFrame(), getFrameLocation().x() - getLayerSize().width() / 2,
				getFrameLocation().y() -  getLayerSize().height() / 2);
	}

	@Override
	public void update(int delta) 
	{
		getSprite().update(delta);
	}
}
