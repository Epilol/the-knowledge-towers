package saga.progetto.tesi.navigable.menu;

import static playn.core.PlayN.assets;
import static playn.core.PlayN.graphics;
import java.util.LinkedList;
import java.util.List;
import playn.core.AssetWatcher;
import playn.core.Font;
import playn.core.Image;
import playn.core.ImageLayer;
import playn.core.Mouse.ButtonEvent;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import pythagoras.f.IPoint;
import pythagoras.f.Point;
import saga.progetto.tesi.core.TheKnowledgeTowers;
import saga.progetto.tesi.entity.dynamicentity.Player;
import saga.progetto.tesi.job.Berserker;
import saga.progetto.tesi.job.BlackMage;
import saga.progetto.tesi.job.Druid;
import saga.progetto.tesi.job.Ranger;
import saga.progetto.tesi.job.Thief;
import saga.progetto.tesi.job.Warrior;
import saga.progetto.tesi.media.Text;
import saga.progetto.tesi.navigable.Gameloop;
import saga.progetto.tesi.navigable.Navigable;

public class JobMenu extends GameMenu
{
	private static final String JOB_PATH =  "images/menu/game_menu/jobs.png";
	private static final String WARRIOR_DESCRIPTION_PATH =  "images/menu/game_menu/warrior_description.png";
	private static final String BLACK_MAGE_DESCRIPTION_PATH =  "images/menu/game_menu/blackmage_description.png";
	private static final String THIEF_DESCRIPTION_PATH =  "images/menu/game_menu/thief_description.png";
	private static final String BERSERKER_DESCRIPTION_PATH =  "images/menu/game_menu/berserker_description.png";
	private static final String RANGER_DESCRIPTION_PATH =  "images/menu/game_menu/ranger_description.png";
	private static final String DRUID_DESCRIPTION_PATH =  "images/menu/game_menu/druid_description.png";
	private static final String WARRIOR_JOB_PATH =  "images/menu/game_menu/warrior_job.png";
	private static final String BLACK_MAGE_JOB_PATH =  "images/menu/game_menu/blackmage_job.png";
	private static final String THIEF_JOB_PATH =  "images/menu/game_menu/thief_job.png";
	private static final String BERSERKER_JOB_PATH =  "images/menu/game_menu/berserker_job.png";
	private static final String RANGER_JOB_PATH =  "images/menu/game_menu/ranger_job.png";
	private static final String DRUID_JOB_PATH =  "images/menu/game_menu/druid_job.png";
	private static final String UNLOCKED_PATH =  "images/menu/game_menu/unavailable_description.png";
	private static final String SELECTED_PATH =  "images/menu/game_menu/selected_job.png";
	private static final IDimension JOB_SIZE = new Dimension(59.0f, 79.0f);
	private static final IPoint JOB_POINT = new Point(95.0f, 140.0f);
	private static final IPoint LEVEL_POINT = new Point(99.0f, 223.0f);
	private static final IPoint REQUIREMENT_POINT = new Point(50.0f, 24.0f);
	private static final IDimension REQUIREMENT_SIZE = new Dimension(466.0f, 70.0f);
	private static final IDimension OFFSET = new Dimension(84.0f, 116.0f);
	private static final IPoint DESCRIPTION_POINT = new Point(87.0f, 362.0f);
	private static final int MENU_INDEX = 4;
	private static Image jobsImage;
	private static Image warriorDescriptionImage;
	private static Image blackMageDescriptionImage;
	private static Image thiefDescriptionImage;
	private static Image berserkerDescriptionImage;
	private static Image rangerDescriptionImage;
	private static Image druidDescriptionImage;
	private static Image warriorJobImage;
	private static Image blackMageJobImage;
	private static Image thiefJobImage;
	private static Image berserkerJobImage;
	private static Image rangerJobImage;
	private static Image druidJobImage;
	private static Image unavailableDescriptionImage;
	private static Image selectedImage;
	private static float totalPages;
	
	private List<Text> staticText;
	private List<Text> jobLevels;
	private ImageLayer jobsLayer;
	private ImageLayer warriorDescriptionLayer;
	private ImageLayer blackMageDescriptionLayer;
	private ImageLayer thiefDescriptionLayer;
	private ImageLayer berserkerDescriptionLayer;
	private ImageLayer rangerDescriptionLayer;
	private ImageLayer druidDescriptionLayer;
	private ImageLayer warriorJobLayer;
	private ImageLayer blackMageJobLayer;
	private ImageLayer thiefJobLayer;
	private ImageLayer berserkerJobLayer;
	private ImageLayer rangerJobLayer;
	private ImageLayer druidJobLayer;
	private ImageLayer unavailableLayer;
	private ImageLayer selectedLayer;
	private Text warriorRequirement;
	private Text blackMageRequirement;
	private Text thiefRequirement;
	private Text berserkerRequirement;
	private Text rangerRequirement;
	private Text druidRequirement;
	
	public JobMenu(TheKnowledgeTowers game, Player player, Gameloop gameloop)
	{
		super(game, player, gameloop);
		jobsLayer = graphics().createImageLayer(jobsImage);
		jobsLayer.setVisible(false);
		jobsLayer.setTranslation(JOB_POINT.x(), JOB_POINT.y());
		jobsLayer.setDepth(11.0f);
		selectedLayer = graphics().createImageLayer(selectedImage);
		selectedLayer.setVisible(false);
		translate(player, selectedLayer);
		selectedLayer.setDepth(14.0f);
		graphics().rootLayer().add(jobsLayer);
		graphics().rootLayer().add(selectedLayer);
		initJobLayer(warriorJobLayer = graphics().createImageLayer(warriorJobImage), JobType.WARRIOR);
		initJobLayer(blackMageJobLayer = graphics().createImageLayer(blackMageJobImage), JobType.BLACK_MAGE);
		initJobLayer(thiefJobLayer = graphics().createImageLayer(thiefJobImage), JobType.THIEF);
		initJobLayer(berserkerJobLayer = graphics().createImageLayer(berserkerJobImage), JobType.BERSERKER);
		initJobLayer(rangerJobLayer = graphics().createImageLayer(rangerJobImage), JobType.RANGER);
		initJobLayer(druidJobLayer = graphics().createImageLayer(druidJobImage), JobType.DRUID);
		initDescriptionLayer(warriorDescriptionLayer = graphics().createImageLayer(warriorDescriptionImage));
		initDescriptionLayer(blackMageDescriptionLayer = graphics().createImageLayer(blackMageDescriptionImage));
		initDescriptionLayer(thiefDescriptionLayer = graphics().createImageLayer(thiefDescriptionImage));
		initDescriptionLayer(berserkerDescriptionLayer = graphics().createImageLayer(berserkerDescriptionImage));
		initDescriptionLayer(rangerDescriptionLayer = graphics().createImageLayer(rangerDescriptionImage));
		initDescriptionLayer(druidDescriptionLayer = graphics().createImageLayer(druidDescriptionImage));
		initDescriptionLayer(unavailableLayer = graphics().createImageLayer(unavailableDescriptionImage));
		totalPages = player.getStats().getNegativeDiscarded();
		staticText = new LinkedList<Text>();
		jobLevels = new LinkedList<Text>();
		init();
	}
	
	public enum JobType
	{
		DEFAULT(0.0f, 0.0f),
		WARRIOR(93.0f, 138.0f), BLACK_MAGE(177.0f, 138.0f), THIEF(261.0f, 138.0f), BERSERKER(345.0f, 138.0f), RANGER(429.0f, 138.0f),  
		DRUID(93.0f, 252.0f),  CLASS7(177.0f, 252.0f),  CLASS8(261.0f, 252.0f),  CLASS9(345.0f, 252.0f),  CLASS10(429.0f, 252.0f); 
		
		private float x;
		private float y;
		
		JobType(float x, float y)
		{
			this.x = x;
			this.y = y;
		}
		
		public float getX()
		{
			return x;
		}
		
		public float getY()
		{
			return y;
		}
	}
	
	public static void loadAssets(AssetWatcher watcher)
	{
		jobsImage = assets().getImage(JOB_PATH);
		watcher.add(jobsImage);
		warriorDescriptionImage = assets().getImage(WARRIOR_DESCRIPTION_PATH);
		watcher.add(warriorDescriptionImage);
		blackMageDescriptionImage = assets().getImage(BLACK_MAGE_DESCRIPTION_PATH);
		watcher.add(blackMageDescriptionImage);
		thiefDescriptionImage = assets().getImage(THIEF_DESCRIPTION_PATH);
		watcher.add(thiefDescriptionImage);
		berserkerDescriptionImage = assets().getImage(BERSERKER_DESCRIPTION_PATH);
		watcher.add(berserkerDescriptionImage);
		rangerDescriptionImage = assets().getImage(RANGER_DESCRIPTION_PATH);
		watcher.add(rangerDescriptionImage);
		druidDescriptionImage = assets().getImage(DRUID_DESCRIPTION_PATH);
		watcher.add(druidDescriptionImage);
		warriorJobImage = assets().getImage(WARRIOR_JOB_PATH);
		watcher.add(warriorJobImage);
		blackMageJobImage = assets().getImage(BLACK_MAGE_JOB_PATH);
		watcher.add(blackMageJobImage);
		thiefJobImage = assets().getImage(THIEF_JOB_PATH);
		watcher.add(thiefJobImage);
		berserkerJobImage = assets().getImage(BERSERKER_JOB_PATH);
		watcher.add(berserkerJobImage);
		rangerJobImage = assets().getImage(RANGER_JOB_PATH);
		watcher.add(rangerJobImage);
		druidJobImage = assets().getImage(DRUID_JOB_PATH);
		watcher.add(druidJobImage);
		unavailableDescriptionImage = assets().getImage(UNLOCKED_PATH);
		watcher.add(unavailableDescriptionImage);
		selectedImage = assets().getImage(SELECTED_PATH);
		watcher.add(selectedImage);
	}
	
	@Override
	public void init()
	{
		super.init();
		staticText.add(initText("Job Menu", 80.0f, 50.0f, 22, false));
	}
	
	private void initDescriptionLayer(ImageLayer descriptionLayer)
	{
		descriptionLayer.setVisible(false);
		descriptionLayer.setTranslation(DESCRIPTION_POINT.x(), DESCRIPTION_POINT.y());
		descriptionLayer.setDepth(11.0f);
		graphics().rootLayer().add(descriptionLayer);
	}
	
	private void initJobLayer(ImageLayer jobLayer, JobType job)
	{
		int i = job.ordinal() - 1;
		int j = 0;
		
		if (i > 4)
		{
			i -= 5;
			j = 1;
		}
		
		jobLayer.setVisible(false);
		jobLayer.setTranslation(JOB_POINT.x() + i * OFFSET.width(), JOB_POINT.y() + j * OFFSET.height());
		jobLayer.setDepth(12.0f);
		graphics().rootLayer().add(jobLayer);
	}
	
	private Text initRequirementText(Number value, String s1, String s2)
	{
		Text requirementText = new Text(s1 + " " + String.valueOf(value).replaceAll("\\..*$", "")  + " " +s2, 
				Font.Style.PLAIN, 14, 0xFFFFFFFF);
		requirementText.setTranslation(REQUIREMENT_POINT.add((REQUIREMENT_SIZE.width() - requirementText.width()) / 2,
				(REQUIREMENT_SIZE.height() - requirementText.height()) / 2 + 5.0f));
		requirementText.setDepth(12.0f);
		requirementText.init();
		return requirementText;
	}
	
	private void showDescription(JobType job)
	{
		switch(job)
		{
			case WARRIOR: showDescription(warriorDescriptionLayer); break;
			case BLACK_MAGE: showDescription(blackMageDescriptionLayer); break;
			case THIEF: showDescription(thiefDescriptionLayer); break;
			case BERSERKER: showDescription(berserkerDescriptionLayer); break;
			case RANGER: showDescription(rangerDescriptionLayer); break;
			case DRUID: showDescription(druidDescriptionLayer); break;
			default: showDescription(unavailableLayer);
		}
	}
	
	private void showRequirement(JobType job, boolean visible)
	{
		
		staticText.get(0).setVisible(!visible);
		
		if (job == JobType.WARRIOR && warriorRequirement != null)
			warriorRequirement.setVisible(visible);
		
		if (job == JobType.BLACK_MAGE && blackMageRequirement != null)
			blackMageRequirement.setVisible(visible);
		
		if (job == JobType.THIEF && thiefRequirement != null)
			thiefRequirement.setVisible(visible);
		
		if (job == JobType.BERSERKER && berserkerRequirement != null)
			berserkerRequirement.setVisible(visible);
		
		if (job == JobType.RANGER && rangerRequirement != null)
			rangerRequirement.setVisible(visible);
		
		if (job == JobType.DRUID && druidRequirement != null)
			druidRequirement.setVisible(visible);
	}
	
	private boolean isJobVisible(JobType job)
	{
		switch(job)
		{
			case WARRIOR: return warriorRequirement != null ? warriorRequirement.visible() : false;
			case BLACK_MAGE: return blackMageRequirement != null ? blackMageRequirement.visible() : false;
			case THIEF: return thiefRequirement != null ? thiefRequirement.visible() : false;
			case BERSERKER: return berserkerRequirement != null ? berserkerRequirement.visible() : false;
			case RANGER: return rangerRequirement != null ? rangerRequirement.visible() : false;
			case DRUID: return druidRequirement != null ? druidRequirement.visible() : false;
			default : return false;
		}
	}
	
	private void showDescription(ImageLayer descriptionLayer)
	{
		warriorDescriptionLayer.setVisible(false);
		blackMageDescriptionLayer.setVisible(false);
		thiefDescriptionLayer.setVisible(false);
		berserkerDescriptionLayer.setVisible(false);
		rangerDescriptionLayer.setVisible(false);
		druidDescriptionLayer.setVisible(false);
		unavailableLayer.setVisible(false);
		
		if(descriptionLayer.equals(warriorDescriptionLayer))
			warriorDescriptionLayer.setVisible(true);
			
		if(descriptionLayer.equals(blackMageDescriptionLayer))
			blackMageDescriptionLayer.setVisible(true);
			
		if(descriptionLayer.equals(thiefDescriptionLayer))
			thiefDescriptionLayer.setVisible(true);
			
		if(descriptionLayer.equals(berserkerDescriptionLayer))
			berserkerDescriptionLayer.setVisible(true);
		
		if(descriptionLayer.equals(rangerDescriptionLayer))
			rangerDescriptionLayer.setVisible(true);

		if(descriptionLayer.equals(druidDescriptionLayer))
			druidDescriptionLayer.setVisible(true);
		
		if(descriptionLayer.equals(unavailableLayer))
			unavailableLayer.setVisible(true);
	}
	
	private void translate(Player player, ImageLayer layer)
	{
		if (player.getCurrentJob() instanceof Warrior)
			selectedLayer.setTranslation(JobType.WARRIOR.getX(), JobType.WARRIOR.getY());
		
		else if (player.getCurrentJob() instanceof BlackMage)
			selectedLayer.setTranslation(JobType.BLACK_MAGE.getX(), JobType.BLACK_MAGE.getY());
		
		else if (player.getCurrentJob() instanceof Thief)
			selectedLayer.setTranslation(JobType.THIEF.getX(), JobType.THIEF.getY());
		
		if (player.getCurrentJob() instanceof Berserker)
			selectedLayer.setTranslation(JobType.BERSERKER.getX(), JobType.BERSERKER.getY());
		
		if (player.getCurrentJob() instanceof Ranger)
			selectedLayer.setTranslation(JobType.RANGER.getX(), JobType.RANGER.getY());
		
		if (player.getCurrentJob() instanceof Druid)
			selectedLayer.setTranslation(JobType.DRUID.getX(), JobType.DRUID.getY());
	}
	
	@Override
	public int getMenuIndex()
	{
		return MENU_INDEX;
	}
	
	@Override
	public Navigable onMouseDown(ButtonEvent event)
	{
		for (JobType job : JobType.values())
		{
			if (getGame().getPointerLocation().x() >= job.getX() && getGame().getPointerLocation().x() <= job.getX() + JOB_SIZE.width() && 
					getGame().getPointerLocation().y() >= job.getY() && getGame().getPointerLocation().y() <= job.getY() + JOB_SIZE.height() &&
					!getPlayer().isInsideTower() && getPlayer().isJobUnlocked(job))
			{
				selectedLayer.setTranslation(job.getX(), job.getY());
				getPlayer().setCurrentJob(job);
			}
		}
		return super.onMouseDown(event);
	}
	
	private void initJobLevelText()
	{
		String[] jobs = {"WARRIOR", "BLACK_MAGE", "THIEF", "BERSERKER", "RANGER", "DRUID"};
		int i = 0, j = 0;
		for(String job : jobs)
		{
			if (i == 5)
			{
				i = 0;
				j++;
			}
			
			Text jobLevel = new Text("Level: " + String.valueOf(getPlayer().getPlayerStats().getJobLevel(job).replaceAll("\\..*$", "")), 
					Font.Style.PLAIN, 14, 0xFFFFFFFF);
			jobLevel.setVisible(true);
			jobLevel.setDepth(11.0f);
			jobLevel.setTranslation(LEVEL_POINT.add(OFFSET.width() * i, OFFSET.height() * j));
			jobLevel.init();
			jobLevels.add(jobLevel);
			i++;
		}
	}
	
	private void destroyJobLevelText()
	{
		for (Text jobLevel : jobLevels)
			jobLevel.destroy();
	}
	
	public static void addTotalPages(int n)
	{
		totalPages += n;
	}
	
	private void initRequirementTexts()
	{
		warriorRequirement = initRequirementText(Warrior.lockRequirement().intValue() - getPlayer().getPlayerStats().getTowerCompleted(), "Requires the completion of", "more towers.");
		blackMageRequirement = initRequirementText(BlackMage.lockRequirement().intValue() - getPlayer().getPlayerStats().getTowerCompleted(), "Requires the completion of", "more towers.");
		thiefRequirement = initRequirementText(Thief.lockRequirement().intValue() - getPlayer().getPlayerStats().getTowerCompleted(), "Requires the completion of", "more towers.");
		berserkerRequirement = initRequirementText(Berserker.lockRequirement().longValue() - getPlayer().getPlayerStats().getKilledEnemies(), "Requires the killing of", "more enemies.");
		rangerRequirement = initRequirementText(Ranger.lockRequirement().longValue() - totalPages, "Requires", "more correct pages to be collected.");
		druidRequirement = initRequirementText(Druid.lockRequirement().floatValue(), "Requires a score of at least", "points");
	}
	
	private void destroyRequirementTexts()
	{
		if (warriorRequirement != null)
			warriorRequirement.destroy();
		
		if (blackMageRequirement != null)
			blackMageRequirement.destroy();
		
		if (thiefRequirement != null)
			thiefRequirement.destroy();
		
		if (berserkerRequirement != null)
			berserkerRequirement.destroy();
		
		if (rangerRequirement != null)
			rangerRequirement.destroy();
		
		if (druidRequirement != null)
			druidRequirement.destroy();
	}
	
	@Override
	public void setVisible(boolean visible)
	{
		super.setVisible(visible);
		jobsLayer.setVisible(visible);
		selectedLayer.setVisible(visible);
		
		if (visible)
		{
			initJobLevelText();
			
			if (getPlayer().isJobUnlocked(JobType.WARRIOR))
				warriorJobLayer.setVisible(true);
			
			if (getPlayer().isJobUnlocked(JobType.BLACK_MAGE))
				blackMageJobLayer.setVisible(true);
			
			if (getPlayer().isJobUnlocked(JobType.THIEF))
				thiefJobLayer.setVisible(true);
			
			if (getPlayer().isJobUnlocked(JobType.BERSERKER))
				berserkerJobLayer.setVisible(true);
			
			if (getPlayer().isJobUnlocked(JobType.RANGER))
				rangerJobLayer.setVisible(true);
			
			if (getPlayer().isJobUnlocked(JobType.DRUID))
				druidJobLayer.setVisible(true);
			
			initRequirementTexts();
		}
		
		else
		{
			warriorDescriptionLayer.setVisible(false);
			blackMageDescriptionLayer.setVisible(false);
			thiefDescriptionLayer.setVisible(false);
			berserkerDescriptionLayer.setVisible(false);
			rangerDescriptionLayer.setVisible(false);
			druidDescriptionLayer.setVisible(false);
			unavailableLayer.setVisible(false);
			warriorJobLayer.setVisible(false);
			blackMageJobLayer.setVisible(false);
			thiefJobLayer.setVisible(false);
			berserkerJobLayer.setVisible(false);
			rangerJobLayer.setVisible(false);
			druidJobLayer.setVisible(false);
			destroyJobLevelText();
			destroyRequirementTexts();
		}
		
		for (Text text : staticText)
			text.setVisible(visible);
	}
	
	@Override
	public void update(int delta)
	{
		super.update(delta);
		for (JobType job : JobType.values())
		{
			if (getGame().getPointerLocation().x() >= job.getX() && getGame().getPointerLocation().x() <= job.getX() + JOB_SIZE.width() && 
					getGame().getPointerLocation().y() >= job.getY() && getGame().getPointerLocation().y() <= job.getY() + JOB_SIZE.height())
			{
				showDescription(job);
				
				if (!getPlayer().isJobUnlocked(job))
					showRequirement(job, true);
			}
			
			else if (isJobVisible(job))
				showRequirement(job, false);
		}
	}

}