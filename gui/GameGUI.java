package saga.progetto.tesi.gui;

import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import playn.core.AssetWatcher;
import playn.core.Font;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Layer;
import pythagoras.f.IPoint;
import pythagoras.f.Point;
import saga.progetto.tesi.core.TheKnowledgeTowers;
import saga.progetto.tesi.job.Job;
import saga.progetto.tesi.job.Job.SkillType;
import saga.progetto.tesi.media.Text;
import saga.progetto.tesi.navigable.Gameloop;

public class GameGUI extends GUIComponent
{
	private static final String PATH = "images/gui/gui2.png";
	private static final String TITLE_PATH = "images/gui/tower_title.png";
	private static final String SPELL_TOOLTIP_PATH = "images/gui/spell_tooltip.png";
	private static final String KEYS_PATH = "images/gui/keys.png";
	private static final String RAGE_PATH = "images/gui/rage_layout.png";
	private static final IPoint GUI_POINT = new Point(0.0f, 445.0f);
	private static final IPoint TOOLTIP_POINT = new Point(105.0f, 330.0f);
	private static final IPoint RAGE_POINT = new Point(38f, 549.0f);
	private static final IPoint KEYS_POINT = new Point(236.0f, 551.0f);
	private static final IPoint TITLE_TEXT_POINT = new Point(120.0f, 351.0f);
	private static final IPoint COST_TEXT_POINT = new Point(120.0f, 376.0f);
	private static final IPoint DESCRIPTION_TEXT_POINT = new Point(120.0f, 425.0f);
	private static final IPoint LEVEL_TEXT_POINT = new Point(120.0f, 485.0f);
	//TODO rendere visibile nuovamente buttoni e implementarli con tooltip
//	private static final ICircle STATS_BUTTON = new Circle(new Point(624.0f, 481.0f), 12.0f);
//	private static final ICircle EQUIP_BUTTON = new Circle(new Point(659.0f, 481.0f), 12.0f);
//	private static final ICircle INVENTORY_BUTTON = new Circle(new Point(693.0f, 481.0f), 12.0f);
//	private static final ICircle OPTIONS_BUTTON = new Circle(new Point(728.0f, 481.0f), 12.0f);
	private static Image guiImage;
	private static Image titleImage;
	private static Image spellTooltipImage;
	private static Image rageImage;
	private static Image keysImage;

	private Map<SkillType, List<Text>> descriptions;
	private ImageLayer guiLayer;
	private ImageLayer titleLayer;
	private ImageLayer spellTooltipLayer;
	private ImageLayer rageLayer;
	private ImageLayer keysLayer;
	private Text titleText;
	private Job job;
	private TheKnowledgeTowers game;
	private boolean tooltipShowed;
	
	public GameGUI(TheKnowledgeTowers game)
	{
		this.game = game;
		guiLayer = graphics().createImageLayer(guiImage);
		guiLayer.setVisible(false);
		guiLayer.setTranslation(GUI_POINT.x(), GUI_POINT.y());
		guiLayer.setDepth(7.0f);
		graphics().rootLayer().add(guiLayer);
		titleLayer = graphics().createImageLayer(titleImage);
		titleLayer.setVisible(false);
		titleLayer.setTranslation(graphics().width() / 2 - titleLayer.width() / 2, 0.0f);
		titleLayer.setDepth(7.0f);
		graphics().rootLayer().add(titleLayer);
		spellTooltipLayer = graphics().createImageLayer(spellTooltipImage);
		spellTooltipLayer.setVisible(false);
		spellTooltipLayer.setDepth(11.0f);
		graphics().rootLayer().add(spellTooltipLayer);
		rageLayer = graphics().createImageLayer(rageImage);
		rageLayer.setVisible(false);
		rageLayer.setTranslation(RAGE_POINT.x(), RAGE_POINT.y());
		rageLayer.setDepth(8.0f);
		graphics().rootLayer().add(rageLayer);
		keysLayer = graphics().createImageLayer(keysImage);
		keysLayer.setVisible(false);
		keysLayer.setTranslation(KEYS_POINT.x(), KEYS_POINT.y());
		keysLayer.setDepth(10.0f);
		graphics().rootLayer().add(keysLayer);
		descriptions = new HashMap<SkillType, List<Text>>();
	}
	
	public static void loadAssets(AssetWatcher watcher)
	{
		guiImage = assets().getImage(PATH);
		watcher.add(guiImage);
		titleImage = assets().getImage(TITLE_PATH);
		watcher.add(titleImage);
		spellTooltipImage = assets().getImage(SPELL_TOOLTIP_PATH);
		watcher.add(spellTooltipImage);
		rageImage = assets().getImage(RAGE_PATH);
		watcher.add(rageImage);
		keysImage = assets().getImage(KEYS_PATH);
		watcher.add(keysImage);
	}
	
	public void setJob(Job job)
	{
		this.job = job;
		destroyDescription();
		initTooltipTexts();
	}
	
	public void initTooltipTexts()
	{
		for (SkillType type : SkillType.values())
		{
			Map<Integer, String> description = null;
			
			if (type == SkillType.LEFT)
				description = job.leftClickDescription();
			
			else if (type == SkillType.RIGHT)
				description = job.rightClickDescription();
			
			else if (type == SkillType.FIRST)
				description = job.firstSkillDescription();
			
			else if (type == SkillType.SECOND)
				description = job.secondSkillDescription();
			
			else if (type == SkillType.THIRD)
				description = job.thirdSkillDescription();
			
			else
				description = job.ultimateSkillDescription();
			
			List<Text> skillDescription = new LinkedList<Text>();

			if (description.containsKey(0))
			{
				Text text = new Text(description.get(0), Font.Style.BOLD, 18, 0xFFFFFFFF);
				text.setTranslation(TITLE_TEXT_POINT.x() + type.ordinal() * (Job.ICON.width() + 1), TITLE_TEXT_POINT.y());
				text.setVisible(false);
				text.setDepth(12.0f);
				text.init();
				skillDescription.add(text);
			}
			
			if (description.containsKey(1))
			{
				int color = 0xFFFFFFFF;
				
				String s = description.get(1);
				
				if (s.contains("rage"))
					color = 0xFFe8d321;
				
				else if (s.contains("life") || s.contains("Strength"))
					color = 0xFFd52121;
				
				else if (s.contains("endurance") || s.contains("Dexterity"))
					color = 0xFF2fe821;
				
				else if (s.contains("mana") || s.contains("Intelligence"))
					color = 0xFF2160d5;
				
				Text text = new Text(description.get(1), Font.Style.BOLD, 12, color);
				text.setTranslation(COST_TEXT_POINT.x() + type.ordinal() * (Job.ICON.width() + 1), COST_TEXT_POINT.y());
				text.setVisible(false);
				text.setDepth(12.0f);
				text.init();
				skillDescription.add(text);
			}
			
			if (description.containsKey(2))
			{
				Text text = new Text(description.get(2), Font.Style.BOLD, 12, 0xFFFFFFFF);
				text.setTranslation(DESCRIPTION_TEXT_POINT.x() + type.ordinal() * (Job.ICON.width() + 1), DESCRIPTION_TEXT_POINT.y());
				text.setVisible(false);
				text.setDepth(12.0f);
				text.init();
				skillDescription.add(text);
			}
			
			if (description.containsKey(3))
			{
				Text text = null;
				
				if (job.getUltimateSkillLevel() == 0 && type == SkillType.ULTIMATE)
					text = new Text(description.get(3), Font.Style.BOLD, 14, 0xFFFF0000);
				
				else
					text = new Text(description.get(3), Font.Style.BOLD, 9, 0xFFFEE5A9);

				text.setTranslation(LEVEL_TEXT_POINT.x() + type.ordinal() * (Job.ICON.width() + 1), LEVEL_TEXT_POINT.y());
				text.setVisible(false);
				text.setDepth(12.0f);
				text.init();
				skillDescription.add(text);
			}
			
			descriptions.put(type, skillDescription);
		}		
	}
	
	public void setVisible(boolean visible)
	{
		guiLayer.setVisible(visible);
		keysLayer.setVisible(visible);
		
		getRageLayer().setVisible(visible);
		
		if (!visible)
		{
			spellTooltipLayer.setVisible(false);
			spellTooltipVisibility(false);
		}
	}
	
	public ImageLayer getTitleLayer()
	{
		return titleLayer;
	}
	
	public void spellTooltipVisibility(boolean visible)
	{
		spellTooltipLayer.setVisible(visible);
		
		for (Map.Entry<SkillType, List<Text>> entry : descriptions.entrySet())
			for (Text text : entry.getValue())
				text.setVisible(visible);
	}
	
	public void spellTooltipVisibility(boolean visible, SkillType skill)
	{
		spellTooltipLayer.setTranslation(TOOLTIP_POINT.x() + skill.ordinal() * Job.ICON.width(), TOOLTIP_POINT.y());
		spellTooltipLayer.setVisible(visible);
		
		List<Text> description = descriptions.get(skill);
		
		for (Text text : description)
			text.setVisible(visible);
	}
	
	@Override
	public Layer getLayer()
	{
		return guiLayer;
	}
	
	public Layer getRageLayer()
	{
		return rageLayer;
	}
	
	public void clear()
	{
		guiLayer.destroy();
		spellTooltipLayer.destroy();
		rageLayer.destroy();
		keysLayer.destroy();
		destroyDescription();
	}
	
	public void destroyDescription()
	{
		for (Map.Entry<SkillType, List<Text>> entry : descriptions.entrySet())
			for (Text text : entry.getValue())
				text.destroy();
	}

	public float height()
	{
		return 138.0f;
	}
	
	public void setTowerTitle(boolean isInsideTower, String title)
	{
		if(!titleLayer.visible() && isInsideTower)
		{
			titleLayer.setVisible(true);
			titleText = new Text("The tower of " + title, Font.Style.BOLD, 12, 0xFFFFFFFF);
			titleText.setDepth(8.0f);
			titleText.setVisible(true);
			titleText.setTranslation(graphics().width() / 2 - titleText.width() / 2, 10.0f);
			titleText.init();
		}
		
		else if (titleLayer.visible() && !isInsideTower)
		{
			titleLayer.setVisible(false);
			
			if (titleText != null)
				titleText.destroy();
		}
	}
	
	public boolean checkSelectedSkill(SkillType skill)
	{
		return game.getPointerLocation().x() >= Job.LEFT_POINT.x() + (Job.ICON.width() + 1) * skill.ordinal() && 
				game.getPointerLocation().x() <= Job.LEFT_POINT.x() + (Job.ICON.width() + 1) * skill.ordinal() + Job.ICON.width() &&
				 game.getPointerLocation().y() >= Job.LEFT_POINT.y() && game.getPointerLocation().y() <= Job.LEFT_POINT.y() +
				  Job.ICON.height();
	}

	public void update()
	{
		for (SkillType skill : SkillType.values())
		{
			if (checkSelectedSkill(skill))
			{
				for (Map.Entry<SkillType, List<Text>> entry : descriptions.entrySet())
					for (Text text : entry.getValue())
						text.setVisible(false);
				
				spellTooltipVisibility(true, skill);
				tooltipShowed = true;
			}
			
			if (tooltipShowed && game.getPointerLocation().x() < Job.LEFT_POINT.x() || game.getPointerLocation().y() < Job.LEFT_POINT.y()
					|| game.getPointerLocation().x() > Job.LEFT_POINT.x() + (Job.ICON.width() + 1) * SkillType.values().length || 
						game.getPointerLocation().y() > Job.LEFT_POINT.y() + Job.ICON.height())
			{
				spellTooltipVisibility(false);
				tooltipShowed = false;
			}
		}
	}

	public Gameloop getGameloop()
	{
		return game.getGameLoop();
	}
}
