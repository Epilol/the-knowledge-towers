package saga.progetto.tesi.entity.dynamicentity.pet;

import static playn.core.PlayN.assets;
import org.jbox2d.dynamics.World;
import playn.core.AssetWatcher;
import playn.core.Image;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import saga.progetto.tesi.entity.Sprite;
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.entity.dynamicentity.equip.Fang;
import saga.progetto.tesi.gui.NPCBar;
import saga.progetto.tesi.map.GameMap;

public class Bear extends Pet
{
	private static final IDimension BEAR_SIZE = new Dimension(56, 56);
	private static final String BEAR_PATH = "images/characters/pets/bear.png";
	private static final float BEAR_SPEED = 3.0f;
	private static final float TOTAL_LIFE = 15.0f;
	private static final int VIEW_DISTANCE = 350;
	private static final float MAGICAL_RESISTANCE = 0.0f;
	private static final float TOTAL_ENDURANCE = 40.0f;
	private static Image bearImage;
	
	private float strengthModifier;
	private float skillModifier;

	public Bear(Player player, float strengthModifier, float skillModifier)
	{
		super(player, BEAR_SIZE);
		this.strengthModifier = strengthModifier;
		this.skillModifier = skillModifier;
		setSprite(new Sprite(bearImage, getFrameDuration(), BEAR_SIZE.width(), BEAR_SIZE.height()));
		getSprite().setLastDirection(player.getLastDirection());
		setTotalLife(TOTAL_LIFE * (skillModifier + strengthModifier) * ((float) player.getLevel() - 1) / 10 + 1);
		setCurrentLife(TOTAL_LIFE * (skillModifier + strengthModifier) * ((float) player.getLevel() - 1) / 10 + 1);
		setTotalEndurance(TOTAL_ENDURANCE);
		setCurrentEndurance(TOTAL_ENDURANCE);
		setBar(new NPCBar(this, BEAR_SIZE));
		setWeapon(new Fang(x() + SUMMON_SPOT, y(), player.getMap(), true, (int) player.getCurrentJob().getFirstSkillLevel() + 1));
		getWeapon().setActive(false);
		setViewDistance(VIEW_DISTANCE);
		setHasTaunt(true);
	}

	public static void loadAssets(AssetWatcher watcher) 
	{
		bearImage = assets().getImage(BEAR_PATH);
		watcher.add(bearImage);
	}

	
	@Override
	public float getDefaultSpeed()
	{
		return BEAR_SPEED;
	}
	
	@Override
	public IDimension getSize()
	{
		return BEAR_SIZE;
	}
	
	public float getMagicResist()
	{
		return MAGICAL_RESISTANCE;
	}
	
	public void setNewMap(float x, float y, World world, GameMap map)
	{
		super.setNewMap(x, y, world, map);
		getWeapon().clear();
		setWeapon(new Fang(x() + SUMMON_SPOT, y(), getPlayer().getMap(), true, (int) getPlayer().getCurrentJob().getFirstSkillLevel() + 1));
		getWeapon().setActive(false);
	}
	
	@Override
	public float getDamageModifier()
	{
		return strengthModifier * skillModifier;
	}
}
