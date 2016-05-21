package saga.progetto.tesi.entity.dynamicentity;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import pythagoras.f.Dimension;
import pythagoras.f.IDimension;
import saga.progetto.tesi.entity.staticentity.Item;
import saga.progetto.tesi.entity.staticentity.LifePotion;
import saga.progetto.tesi.entity.staticentity.Potion.PotionType;
import saga.progetto.tesi.entity.staticentity.Weapon;
import saga.progetto.tesi.entity.staticentity.Weapon.WeaponType;
import saga.progetto.tesi.job.Berserker;
import saga.progetto.tesi.job.BlackMage;
import saga.progetto.tesi.job.Druid;
import saga.progetto.tesi.job.Job;
import saga.progetto.tesi.job.Ranger;
import saga.progetto.tesi.job.Thief;
import saga.progetto.tesi.job.Warrior;
import saga.progetto.tesi.map.GameMap;
import saga.progetto.tesi.navigable.menu.JobMenu.JobType;

public class PlayerStats
{
	private static final IDimension SIZE = new Dimension(32, 32);
	public static final float SPEED = 3.8f;
	private static final float STRENGTH = 5.0f;
	private static final float DEXTERITY = 5.0f;
	private static final float INTELLIGENCE = 5.0f;
	private static final float LIFE = 20.0f;
	private static final float ENDURANCE = 20.0f;
	private static final float MANA = 20.0f;
	private static final float STARTING_RAGE = 0.0f;
	private static final float TOTAL_RAGE = 50.0f;
	private static final float COMBAT_TIME = 1650.0f;
	private static final float SHIELD_COOLDOWN = 10000.0f;
	private static final float EXP_REQUIRED = 50.0f;
	private static final float JOB_EXP_REQUIRED = 20.0f;
	
	// <JobType, level _ currentPoints _ firstSkillLevel _ secondSkillLevel _ thirdSkillLevel _ ultimateSkillLevel _ freePoints
	private Map<String, String> jobInfo;
	private int towerCompleted;
	private float bestScore;
	private long negativeGathered;
	private long negativeDiscarded;
	private long killedEnemies;
	private Player player;
	
	public PlayerStats()
	{
		jobInfo = new HashMap<String, String>();
		for (JobType job : JobType.values())
			jobInfo.put(job.toString(), 0 + "_" + 0 + "_" + 0 + "_" + 0 + "_" + 0 + "_" + 0 + "_" + 0);
	}
	
	public void setPlayer(Player player)
	{
		this.player = player;
	}
	
	public Map<String, String>  getJobInfo()
	{
		return jobInfo;
	}
	
	public String getJobInfo(String job)
	{
		return jobInfo.get(job);
	}
	
	public String getJobLevel(String job)
	{
		return jobInfo.get(job).replace(".0f", "").split("_")[0];
	}
	
	public String getJobExperience(String job)
	{
		return jobInfo.get(job).split("_")[1];
	}
	
	public String getFirstSkillLevel(String job)
	{
		return jobInfo.get(job).split("_")[2];
	}
	
	public String getSecondSkillLevel(String job)
	{
		return jobInfo.get(job).split("_")[3];
	}
	
	public String getThirdSkillLevel(String job)
	{
		return jobInfo.get(job).split("_")[4];
	}
	
	public String getUltimateSkillLevel(String job)
	{
		return jobInfo.get(job).split("_")[5];
	}
	
	public String getFreePoints(String job)
	{
		return jobInfo.get(job).split("_")[6];
	}
	
	public void updateJobInfo(String job, String info)
	{
		jobInfo.put(job, info);
	}
	
	public void updateJobInfo(String job, String level, String currentPoints, String firstSkillLevel, String secondSkillLevel, String thirdSkillLevel, String ultimateSkillLevel, String freePoints)
	{
		jobInfo.put(job, level + "_" + currentPoints + "_" + firstSkillLevel + "_" + secondSkillLevel + "_" + thirdSkillLevel + "_" + ultimateSkillLevel + "_" + freePoints);
	}
	
	private void updateJobInfo(String job, String[] info)
	{
		String updatedInfo = "";
		for (int i = 0; i < info.length - 1; i++)
			updatedInfo += info[i] + "_";
		updatedInfo += info[info.length - 1];
		updateJobInfo(job, updatedInfo);
	}
	
	public void updateJobLevel(String job, String level)
	{
		String[] info = jobInfo.get(job).split("_");
		info[0] = level.replaceAll("\\..*$", "");
		updateJobInfo(job, info);
	}
	
	public void updateJobExperience(String job, String currentPoints)
	{
		String[] info = jobInfo.get(job).split("_");
		info[1] = currentPoints.replaceAll("\\..*$", "");
		updateJobInfo(job, info);
	}
	
	public void updateFirstSkillLevel(String job, String firstSkillLevel)
	{
		String[] info = jobInfo.get(job).split("_");
		info[2] = firstSkillLevel.replaceAll("\\..*$", "");
		updateJobInfo(job, info);
	}
	
	public void updateSecondSkillLevel(String job, String secondSkillLevel)
	{
		String[] info = jobInfo.get(job).split("_");
		info[3] = secondSkillLevel.replaceAll("\\..*$", "");
		updateJobInfo(job, info);
	}
	
	public void updateThirdSkillLevel(String job, String thirdSkillLevel)
	{
		String[] info = jobInfo.get(job).split("_");
		info[4] = thirdSkillLevel.replaceAll("\\..*$", "");
		updateJobInfo(job, info);
	}
	
	public void updateUltimateSkillLevel(String job, String ultimateSkillLevel)
	{
		String[] info = jobInfo.get(job).split("_");
		info[5] = ultimateSkillLevel.replaceAll("\\..*$", "");
		updateJobInfo(job, info);
	}
	
	public void updateFreePoints(String job, String freePoints)
	{
		String[] info = jobInfo.get(job).split("_");
		info[6] = freePoints.replaceAll("\\..*$", "");
		updateJobInfo(job, info);
	}
	
	public IDimension getSize()
	{
		return SIZE;
	}
	
	public float getSpeed()
	{
		return SPEED;
	}
	
	public float getStrength()
	{
		return STRENGTH;
	}
	
	public float getDexterity()
	{
		return DEXTERITY;
	}
	
	public float getIntelligence()
	{
		return INTELLIGENCE;
	}
	
	public float getLife()
	{
		return LIFE;
	}
	
	public float getEndurance()
	{
		return ENDURANCE;
	}
	
	public float getMana()
	{
		return MANA;
	}

	public float getStartingRage()
	{
		return STARTING_RAGE;
	}
	
	public float getTotalRage()
	{
		return TOTAL_RAGE;
	}
	
	public float getCombatTime()
	{
		return COMBAT_TIME;
	}

	public float getShieldCooldown()
	{
		return SHIELD_COOLDOWN;
	}

	public float getExpRequired()
	{
		return EXP_REQUIRED;
	}

	public float getJobExpRequired()
	{
		return JOB_EXP_REQUIRED;
	}
	
	public int getTowerCompleted()
	{
		return towerCompleted;
	}

	public void setTowerCompleted(int towerCompleted)
	{
		this.towerCompleted = towerCompleted;
	}
	
	public void increaseTowerCompleted()
	{
		towerCompleted +=1 ;
	}

	public float getBestScore()
	{
		return bestScore;
	}

	public void setBestScore(float bestScore)
	{
		this.bestScore = bestScore;
	}

	public long getNegativeDiscarded()
	{
		return negativeDiscarded;
	}

	public void setNegativeDiscarded(long negativeDiscarded)
	{
		this.negativeDiscarded = negativeDiscarded;
	}
	
	public void increaseNegativeDiscarded()
	{
		negativeDiscarded += 1;
	}
	
	public long getNegativeGathered()
	{
		return negativeGathered;
	}

	public void setNegativeGathered(long negativeGathered)
	{
		this.negativeGathered = negativeGathered;
	}
	
	public void increaseNegativeGathered()
	{
		negativeGathered += 1;
	}

	public long getKilledEnemies()
	{
		return killedEnemies;
	}

	public void setKilledEnemies(long killedEnemies)
	{
		this.killedEnemies = killedEnemies;
	}
	
	public void increaseKilledEnemies()
	{
		killedEnemies += 1 ;
	}


	public static Map<String, Map<String, String>> saveDefault(String currentName, JobType currentJob)
	{
		Map<String, Map<String, String>> state = new HashMap<String, Map<String,String>>();
		// namespace = player
		Map<String, String> playerState = new HashMap<String, String>();
		playerState.put("name", Player.getName());
		playerState.put("aspect", Player.getAspect());
		playerState.put("level", String.valueOf(1));
		playerState.put("xp", String.valueOf(0.0f));
		playerState.put("strength", String.valueOf(STRENGTH));
		playerState.put("dexterity", String.valueOf(DEXTERITY));
		playerState.put("intelligence", String.valueOf(INTELLIGENCE));
		playerState.put("unspent_points", String.valueOf(0));
		playerState.put("job", currentJob.toString());
		playerState.put("default_job", currentJob.toString());
		playerState.put("ng", "0");
		//TODO mappe su metodo
		state.put("player", playerState);
		// namespace = jobs
		Map<String, String> jobInfo = new HashMap<String, String>();
		
		for (JobType job : JobType.values())
			jobInfo.put(job.toString(), 0 + "_" + 0 + "_" + 0 + "_" + 0 + "_" + 0 + "_" + 0 + "_" + 0);
		
		state.put("jobs", jobInfo);
		// namespace = items
		Map<String, String> itemState = new HashMap<String, String>();
		List<Item> items = new LinkedList<Item>();
		itemState.put("golds", String.valueOf(0.0f));
		itemState.put("MINOR_LIFE_POTION", String.valueOf(Collections.frequency(items, "MINOR_LIFE_POTION")));
		itemState.put("LIFE_POTION", String.valueOf(Collections.frequency(items, "LIFE_POTION")));
		itemState.put("MAJOR_LIFE_POTION", String.valueOf(Collections.frequency(items, "MAJOR_LIFE_POTION")));
		itemState.put("MINOR_MANA_POTION", String.valueOf(Collections.frequency(items, "MINOR_MANA_POTION")));
		itemState.put("MANA_POTION", String.valueOf(Collections.frequency(items, "MANA_POTION")));
		itemState.put("MAJOR_MANA_POTION", String.valueOf(Collections.frequency(items, "MAJOR_MANA_POTION")));
		itemState.put("STEALTH_POTION", String.valueOf(Collections.frequency(items, "STEALTH_POTION")));
		state.put("items", itemState);
		// namespace = weapons
		List<Weapon> weapons = new LinkedList<Weapon>();
		Map<String, String> weaponState = new HashMap<String, String>();
		weaponState.put("DAGGER", String.valueOf(Collections.frequency(weapons, "DAGGER")));
		weaponState.put("SWORD", String.valueOf(Collections.frequency(weapons, "SWORD")));
		weaponState.put("AXE", String.valueOf(Collections.frequency(weapons, "AXE")));
		weaponState.put("SPEAR", String.valueOf(Collections.frequency(weapons, "SPEAR")));
		state.put("weapons", weaponState);
		// namespace = score
		Map<String, String> scoreState = new HashMap<String, String>();
		scoreState.put("tower_completed", String.valueOf(0));
		scoreState.put("best_score", String.valueOf(0.0f));
		scoreState.put("correct_pages", String.valueOf(0));
		scoreState.put("total_pages", String.valueOf(0));
		scoreState.put("killed_enemies", String.valueOf(0));
		state.put("score", scoreState);
		
		return state;
	}
	
	public Map<String, Map<String, String>> saveState()
	{
		Map<String, Map<String, String>> state = new HashMap<String, Map<String,String>>();
		// namespace = player
		Map<String, String> playerState = new HashMap<String, String>();
		playerState.put("name", Player.getName());
		playerState.put("aspect", Player.getAspect());
		playerState.put("level", String.valueOf(player.getLevel()));
		playerState.put("xp", String.valueOf(player.getCurrentExperience()));
		playerState.put("strength", String.valueOf(player.getStrength()));
		playerState.put("dexterity", String.valueOf(player.getDexterity()));
		playerState.put("intelligence", String.valueOf(player.getIntelligence()));
		playerState.put("unspent_points", String.valueOf(player.getFreePoints()));
		playerState.put("job", player.getCurrentJob().toString());
		playerState.put("default_job", player.getDefaultJob().toString());
		playerState.put("ng", String.valueOf(player.getNewGame()));
		//TODO mappe su metodo
		state.put("player", playerState);
		// namespace = jobs
		state.put("jobs", jobInfo);
		// namespace = items
		Map<String, String> itemState = new HashMap<String, String>();
		itemState.put("golds", String.valueOf(player.getGolds()));
		itemState.put("MINOR_LIFE_POTION", String.valueOf(Collections.frequency(player.getItems(), "MINOR_LIFE_POTION")));
		itemState.put("LIFE_POTION", String.valueOf(Collections.frequency(player.getItems(), "LIFE_POTION")));
		itemState.put("MAJOR_LIFE_POTION", String.valueOf(Collections.frequency(player.getItems(), "MAJOR_LIFE_POTION")));
		itemState.put("MINOR_MANA_POTION", String.valueOf(Collections.frequency(player.getItems(), "MINOR_MANA_POTION")));
		itemState.put("MANA_POTION", String.valueOf(Collections.frequency(player.getItems(), "MANA_POTION")));
		itemState.put("MAJOR_MANA_POTION", String.valueOf(Collections.frequency(player.getItems(), "MAJOR_MANA_POTION")));
		itemState.put("STEALTH_POTION", String.valueOf(Collections.frequency(player.getItems(), "STEALTH_POTION")));
		state.put("items", itemState);
		// namespace = weapons
		Map<String, String> weaponState = new HashMap<String, String>();
		weaponState.put("DAGGER", String.valueOf(Collections.frequency(player.getWeapons(), "DAGGER")));
		weaponState.put("SWORD", String.valueOf(Collections.frequency(player.getWeapons(), "SWORD")));
		weaponState.put("AXE", String.valueOf(Collections.frequency(player.getWeapons(), "AXE")));
		weaponState.put("SPEAR", String.valueOf(Collections.frequency(player.getWeapons(), "SPEAR")));
		state.put("weapons", weaponState);
		// namespace = score
		Map<String, String> scoreState = new HashMap<String, String>();
		scoreState.put("tower_completed", String.valueOf(towerCompleted));
		scoreState.put("best_score", String.valueOf(bestScore));
		scoreState.put("correct_pages", String.valueOf(negativeDiscarded));
		scoreState.put("total_pages", String.valueOf(negativeGathered));
		scoreState.put("killed_enemies", String.valueOf(killedEnemies));
		state.put("score", scoreState);
		
		return state;
	}
	
	public void loadState(Map<String, Map<String, String>> state)
	{
		player.setLevel(Integer.parseInt(state.get("player").get("level")));
		player.setCurrentExperience(Float.parseFloat(state.get("player").get("xp")));
		player.setStrength(Float.parseFloat(state.get("player").get("strength")));
		player.setDexterity(Float.parseFloat(state.get("player").get("dexterity")));
		player.setIntelligence(Float.parseFloat(state.get("player").get("intelligence")));
		player.setFreePoints(Integer.parseInt(state.get("player").get("unspent_points")));

		if (state.get("player").get("ng") != null)
			player.setNewGame(Integer.parseInt(state.get("player").get("ng")));
		
		int currentMap = Integer.parseInt(state.get("player").get("bind").replace("start", ""));
		
		if (currentMap > GameMap.NUMBER_OF_TOWERS)
			currentMap = GameMap.NUMBER_OF_TOWERS;
		
		player.setCurrentTower(currentMap);
		String jobString = state.get("player").get("job");
		Job currentJob = null;
		
		if (jobString.equals("WARRIOR"))
			currentJob = new Warrior(player.x(), player.y(), player);
		
		else if (jobString.equals("BLACK_MAGE"))
			currentJob = new BlackMage(player);
		
		else if (jobString.equals("THIEF"))
			currentJob = new Thief(player.x(), player.y(), player);
		
		else if (jobString.equals("BERSERKER"))
			currentJob = new Berserker(player.x(), player.y(), player);
		
		else if (jobString.equals("RANGER"))
			currentJob = new Ranger(player.x(), player.y(), player);
		
		else if (jobString.equals("DRUID"))
			currentJob = new Druid(player.x(), player.y(), player);
		
		player.setCurrentJob(currentJob);
		
		jobString = state.get("player").get("default_job");
		
		if (jobString.equals("WARRIOR"))
			player.setDefaultJob(JobType.WARRIOR);
		
		else if (jobString.equals("BLACK_MAGE"))
			player.setDefaultJob(JobType.BLACK_MAGE);
		
		else if (jobString.equals("THIEF"))
			player.setDefaultJob(JobType.THIEF);
		
		else if (jobString.equals("BERSERKER"))
			player.setDefaultJob(JobType.BERSERKER);
		
		else if (jobString.equals("RANGER"))
			player.setDefaultJob(JobType.RANGER);
		
		else if (jobString.equals("DRUID"))
			player.setDefaultJob(JobType.DRUID);
		
		jobInfo = state.get("jobs");
		player.loadJobInfo();
		player.addGolds(Float.parseFloat(state.get("items").get("golds")));
		
		int count = 0;
		
		for (int i = 0; i < Integer.parseInt(state.get("items").get("MINOR_LIFE_POTION")); i++)
			player.getItems().add(new LifePotion(PotionType.MINOR_LIFE_POTION, count++));
		
		for (int i = 0; i < Integer.parseInt(state.get("items").get("LIFE_POTION")); i++)
			player.getItems().add(new LifePotion(PotionType.LIFE_POTION, count++));
		
		for (int i = 0; i < Integer.parseInt(state.get("items").get("MAJOR_LIFE_POTION")); i++)
			player.getItems().add(new LifePotion(PotionType.MAJOR_LIFE_POTION, count++));
		
		for (int i = 0; i < Integer.parseInt(state.get("items").get("MINOR_MANA_POTION")); i++)
			player.getItems().add(new LifePotion(PotionType.MINOR_MANA_POTION, count++));
		
		for (int i = 0; i < Integer.parseInt(state.get("items").get("MANA_POTION")); i++)
			player.getItems().add(new LifePotion(PotionType.MANA_POTION, count++));

		for (int i = 0; i < Integer.parseInt(state.get("items").get("MAJOR_MANA_POTION")); i++)
			player.getItems().add(new LifePotion(PotionType.MAJOR_MANA_POTION, count++));
		
		for (int i = 0; i < Integer.parseInt(state.get("items").get("STEALTH_POTION")); i++)
			player.getItems().add(new LifePotion(PotionType.STEALTH_POTION, count++));
		
		count = 0;
		
		for (int i = 0; i < Integer.parseInt(state.get("weapons").get("DAGGER")); i++)
			player.getWeapons().add(new Weapon(WeaponType.DAGGER, count++));
		
		for (int i = 0; i < Integer.parseInt(state.get("weapons").get("SWORD")); i++)
			player.getWeapons().add(new Weapon(WeaponType.SWORD, count++));
		
		for (int i = 0; i < Integer.parseInt(state.get("weapons").get("AXE")); i++)
			player.getWeapons().add(new Weapon(WeaponType.AXE, count++));
		
		for (int i = 0; i < Integer.parseInt(state.get("weapons").get("SPEAR")); i++)
			player.getWeapons().add(new Weapon(WeaponType.SPEAR, count++));
		
		towerCompleted = Integer.parseInt(state.get("score").get("tower_completed"));
		bestScore = Float.parseFloat(state.get("score").get("best_score"));
		negativeDiscarded = Long.parseLong(state.get("score").get("correct_pages"));
		
		if (state.get("score").get("total_pages") != null)
			negativeGathered = Long.parseLong(state.get("score").get("total_pages"));
		
		killedEnemies = Long.parseLong(state.get("score").get("killed_enemies"));
		
		player.loadBars();
		player.initBars();
	}
}
