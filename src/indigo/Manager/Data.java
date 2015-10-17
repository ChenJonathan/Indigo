package indigo.Manager;

import org.json.simple.JSONObject;

/**
 * Stores general persisting information (player statistics, map statistics, etc). Consider doing unlock stage
 * calculation purely in StageSelectState.
 */
public class Data
{
	private int level;
	private int experience;
	private int maxExperience;

	private int[] talents;

	private JSONObject currentStage;
	private int unlockedStages;
	private int clearTime;
	private String killer;
	private boolean victory = true;

	public static final int NUM_STAGES = 10;
	public static final int NUM_PHASES = 2;
	public static final int NUM_SKILLS = 4;
	public static final int NUM_TALENTS = 5;

	/**
	 * Initializes data.
	 */
	public Data()
	{
		level = 1;
		experience = 0;
		maxExperience = 100;
		talents = new int[NUM_PHASES * NUM_TALENTS];

		unlockedStages = 0;

		killer = "";
	}

	/**
	 * Resets data between levels.
	 */
	public void resetLevelData()
	{
		clearTime = 0;
		killer = "";
		victory = false;
	}

	/**
	 * @return The player's current level.
	 */
	public int getLevel()
	{
		return level;
	}

	/**
	 * @param level The player's new level.
	 */
	public void setLevel(int level)
	{
		this.level = level;
	}

	/**
	 * @return The player's current experience.
	 */
	public int getExperience()
	{
		return experience;
	}

	/**
	 * @param experience The player's new experience.
	 */
	public void setExperience(int experience)
	{
		this.experience = Math.max(experience, 0);
	}

	/**
	 * @return The player's current maximum experience.
	 */
	public int getMaxExperience()
	{
		return maxExperience;
	}

	/**
	 * @param maxExperience The player's new maximum experience.
	 */
	public void setMaxExperience(int maxExperience)
	{
		this.maxExperience = maxExperience;
	}

	/**
	 * @param talent The id of the talent in question.
	 * @return The state of that talent.
	 */
	public int getTalent(int talent)
	{
		return talents[talent];
	}

	/**
	 * @param talent The id of the talent in question.
	 * @param state -1: down, 0: locked, 1: up.
	 */
	public void setTalent(int talent, int state)
	{
		talents[talent] = state;
	}

	/**
	 * @return The current stage.
	 */
	public JSONObject getStage()
	{
		return currentStage;
	}

	/**
	 * @param stage The new stage.
	 */
	public void setStage(JSONObject stage)
	{
		currentStage = stage;
	}

	/**
	 * @return The current number of unlocked stages.
	 */
	public int getUnlockedStages()
	{
		return unlockedStages;
	}

	/**
	 * @param unlockedStages The new number of unlocked stages.
	 */
	public void setUnlockedStages(int unlockedStages)
	{
		this.unlockedStages = unlockedStages;
	}

	/**
	 * @return The current stage clear time.
	 */
	public int getClearTime()
	{
		return clearTime;
	}

	/**
	 * @param time The new stage clear time.
	 */
	public void setClearTime(int time)
	{
		clearTime = time;
	}

	/**
	 * Returns a message identifying the player's killer.
	 * 
	 * @return The message.
	 */
	public String getDeathMessage()
	{
		if(killer.equals(""))
		{
			return "You were vaporized instantly"; // Default death message
		}
		return "You were killed by " + killer;
	}

	/**
	 * @param name The name of the player's killer.
	 */
	public void setKiller(String name)
	{
		killer = name;
	}

	/**
	 * @return Whether the last finished stage was a win or a loss.
	 */
	public boolean getVictory()
	{
		return victory;
	}

	/**
	 * @param victory Whether last stage was a win or a loss.
	 */
	public void setVictory(boolean victory)
	{
		this.victory = victory;
	}
}
