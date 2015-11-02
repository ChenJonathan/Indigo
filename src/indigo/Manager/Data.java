package indigo.Manager;

import java.io.File;
import java.io.FileWriter;

import javax.swing.JOptionPane;

import org.json.simple.JSONObject;

/**
 * Stores general persisting information (player statistics, map statistics, etc).
 */
public class Data
{
	private String name;
	private int level;
	private int experience;
	private int maxExperience;

	private int[] talents;

	private JSONObject currentStage;
	private int unlockedStages;
	private int clearTime;
	private boolean victory = true;

	private String killer;
	private String deathMessage;

	private int currentSlot;
	private boolean autosave;

	public static final int NUM_STAGES = 10;
	public static final int NUM_PHASES = 2;
	public static final int NUM_SKILLS = 3;
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
		deathMessage = "You were killed by _";

		JSONObject settings = ContentManager.load("/settings.json");
		autosave = Boolean.parseBoolean(settings.get("autosave") + "");
		currentSlot = -1;
	}

	/**
	 * Resets data between levels.
	 */
	public void resetLevelData()
	{
		currentStage = null;
		clearTime = 0;
		victory = false;

		killer = "";
		deathMessage = "You were killed by _";
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

	/**
	 * @param name The name of the player's killer.
	 */
	public void setKiller(String name)
	{
		killer = name;
	}

	/**
	 * Returns a message identifying the player's killer.
	 * 
	 * @return The message.
	 */
	public String getDeathMessage()
	{
		if(killer.equals("") && deathMessage.contains("_"))
		{
			return "You were vaporized instantly"; // Default death message
		}
		return deathMessage.replace("_", killer);
	}

	/**
	 * Sets a message identifying how the player died.
	 * 
	 * @param message The message.
	 */
	public void setDeathMessage(String message)
	{
		deathMessage = message;
	}

	/**
	 * Checks if the autosave function is on or off.
	 * 
	 * @return Whether autosave is turned on or not.
	 */
	public boolean getAutosave()
	{
		return autosave;
	}

	/**
	 * Turns the autosave function on or off.
	 * 
	 * @param autosave Whether autosave will be turned on or not.
	 */
	public void setAutosave(boolean autosave)
	{
		this.autosave = autosave;
	}

	/**
	 * Returns the slot that is being used by the autosave function.
	 * 
	 * @return The slot being used.
	 */
	public int getCurrentSlot()
	{
		return currentSlot;
	}

	/**
	 * Wrapper function to change the volume of the audio clips.
	 * 
	 * @param newVolume The new sound volume.
	 */
	public void setVolume(int newVolume)
	{
		SoundManager.changeVolume(newVolume);
	}

	/**
	 * Saves the game in the designated save slot.
	 * 
	 * @param slot The save slot.
	 */
	public void save(int slot)
	{
		if(slot == -1)
		{
			return;
		}

		if(name == null)
		{
			name = JOptionPane.showInputDialog("Enter a save name:");
			if(name == null)
			{
				JOptionPane.showMessageDialog(null, "Invalid name.");
				return;
			}
		}

		String string = "";
		string += "    \"name\":\"" + name + "\",\n";
		string += "    \"level\":" + level + ",\n";
		string += "    \"experience\":" + experience;
		string = "{\n" + string + "\n}";

		try
		{
			String fileName = "slot" + slot;
			String filePath = new File("").getAbsolutePath() + "/resources/data/saves/" + fileName + ".json";
			FileWriter fileWriter = new FileWriter(filePath);
			fileWriter.write(string);
			fileWriter.flush();
			fileWriter.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		currentSlot = slot;
	}

	/**
	 * Loads the game from the designated save slot.
	 * 
	 * @param slot The save slot.
	 */
	public void load(int slot)
	{
		String fileName = "slot" + slot;
		JSONObject json = ContentManager.load("/saves/" + fileName + ".json");

		if(json != null)
		{
			name = json.get("name") + "";
			level = Integer.parseInt(json.get("level") + "");
			experience = Integer.parseInt(json.get("experience") + "");
			maxExperience = level * 100;

			currentSlot = slot;
		}
	}
}
