package indigo.Manager;

// Stores general persisting information (player statistics, map statistics, etc)
// Consider doing unlock stage calculation purely in StageSelectState
public class Data
{
	private int level;
	private int experience;
	
	private int[] talents;
	
	private int currentStage;
	private int unlockedStages;
	private int stagesToUnlock;
	private int clearTime;
	private String killer;
	private boolean victory = false;

	public static final int NUM_STAGES = 10;
	public static final int NUM_PHASES = 2;
	public static final int NUM_SKILLS = 4;
	public static final int NUM_TALENTS = 5;
	
	// Initializes data
	public Data()
	{
		level = 1;
		experience = 0;
		talents = new int[NUM_PHASES * NUM_TALENTS];
		
		unlockedStages = 0;
		stagesToUnlock = 1;
		
		killer = "";
	}
	
	// Resets data between levels
	public void resetLevelData()
	{
		clearTime = 0;
		killer = "";
		victory = false;
	}
	
	// Returns level
	public int getLevel()
	{
		return level;
	}
	
	// Sets level
	public void setLevel(int level)
	{
		this.level = level;
	}
	
	// Returns experience
	public int getExperience()
	{
		return experience;
	}
	
	// Sets experience
	public void setExperience(int experience)
	{
		this.experience = experience;
	}
	
	// Returns a talent
	public int getTalent(int talent)
	{
		return talents[talent];
	}
	
	// Sets a talent to -1, 0, or 1
	// -1 means down, 0 means locked, 1 means up
	public void setTalent(int talent, int state)
	{
		talents[talent] = state;
	}

	// Returns the current stage
	public int getStage()
	{
		return currentStage;
	}
	
	// Sets the current stage
	public void setStage(int stage)
	{
		currentStage = stage;
	}
	
	// Returns number of unlocked stages
	public int getUnlockedStages()
	{
		return unlockedStages;
	}
	
	// Sets the number of unlocked stages
	public void setUnlockedStages(int unlockedStages)
	{
		this.unlockedStages = unlockedStages;
	}
	
	
	// Returns the number of stages to be unlocked
	public int getStagesToUnlock()
	{
		return stagesToUnlock;
	}

	// Sets the number of stages to unlock
	public void setStagesToUnlock(int stagesToUnlock)
	{
		this.stagesToUnlock = stagesToUnlock;
	}
	
	// Returns stage clear time
	public int getClearTime()
	{
		return clearTime;
	}
	
	// Sets the stage clear time
	public void setClearTime(int time)
	{
		clearTime = time;
	}
	
	// Returns the player's killer
	public String getDeathMessage()
	{
		if(killer.equals(""))
		{
			return "You were vaporized instantly"; // Default death message
		}
		return "You were killed by " + killer;
	}
	
	// Sets the player's killer
	public void setKiller(String name)
	{
		killer = name;
	}
	
	// Returns whether the last finished stage was a win or a loss
	public boolean getVictory()
	{
		return victory;
	}
	
	// Stores whether last stage was a win or a loss
	public void setVictory(boolean victory)
	{
		this.victory = victory;
	}
}
