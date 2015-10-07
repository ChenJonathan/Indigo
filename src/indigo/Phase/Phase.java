package indigo.Phase;

import indigo.Entity.Player;
import indigo.GameState.PlayState;
import indigo.Manager.Data;
import indigo.Manager.InputManager;
import indigo.Skill.Skill;

// Handles condition checking for attacking and skillcasting
public abstract class Phase
{
	protected PlayState playState;
	protected InputManager input;

	protected Player player;
	protected int id;

	protected int attackStartTime = 0; // Time when last attack was initiated

	protected int selectedSkill; // Only one skill can be selected at a time
	protected int[] skillStates;
	protected int[] cooldowns;
	protected int[] maxCooldowns;

	protected Skill[] skills; // Initialize in subclass constructors

	public static final int NO_SKILL_SELECTED = -1;

	public static final int IDLE = 0;
	public static final int SELECT = 1;
	public static final int CAST = 2;

	public static final int WATER = 0;
	public static final int ICE = 1;

	public Phase(PlayState playState)
	{
		this.playState = playState;
		input = playState.getInput();
		player = (Player)playState.getPlayer();

		selectedSkill = NO_SKILL_SELECTED;
		skillStates = new int[Data.NUM_SKILLS];
		cooldowns = new int[Data.NUM_SKILLS];

		skills = new Skill[Data.NUM_SKILLS];
	}

	public void update()
	{
		for(int count = 0; count < Data.NUM_SKILLS; count++)
		{
			if(skillStates[count] == CAST)
			{
				if(!player.isActive())
				{
					skills[count].endCast();
				}
				else
				{
					skills[count].update();
				}
			}
		}

		lowerCooldowns();
	}

	public boolean canCast(int skill)
	{
		return (selectedSkill() == skill || skills[skill].isCastOnSelect()) && skills[skill].canCast();
	}

	public void cast()
	{
		skillStates[selectedSkill] = CAST;
		selectedSkill = NO_SKILL_SELECTED;
	}

	public void endCast(int skill)
	{
		skillStates[skill] = IDLE;
		cooldowns[skill] = maxCooldowns[skill];
		resetAttackTimer();
	}

	public void resetAttackTimer()
	{
		attackStartTime = playState.getTime();
	}

	public void setAttackTimer(int time) // TODO Append to the start / end of each skillcast
	{
		attackStartTime = playState.getTime() + time;
	}

	public void lowerCooldowns()
	{
		for(int count = 0; count < cooldowns.length; count++)
		{
			cooldowns[count] = Math.max(cooldowns[count] - 1, 0);
		}
	}

	// Returns if a skill can be selected
	public boolean canSelect(int skill)
	{
		if(skills[skill].id() == Skill.EMPTY)
		{
			return false;
		}
		else if(skills[skill].isCastOnSelect())
		{
			return skillStates[skill] == IDLE && cooldowns[skill] == 0 && canCast(skill);
		}
		return skillStates[skill] == IDLE && cooldowns[skill] == 0;
	}

	// Returns if a skill is selected
	public boolean skillSelected()
	{
		return selectedSkill != NO_SKILL_SELECTED;
	}

	// Returns the selected skill; -1 if none
	public int selectedSkill()
	{
		return selectedSkill;
	}

	public void selectSkill(int skill)
	{
		if(skillSelected())
		{
			skillStates[selectedSkill] = IDLE;
		}

		selectedSkill = skill;
		skillStates[skill] = SELECT;

		if(skills[skill].isCastOnSelect())
		{
			cast();
		}
	}

	public void deselectSkill()
	{
		skillStates[selectedSkill] = IDLE;
		selectedSkill = NO_SKILL_SELECTED;
	}

	public int getSkillState(int skill)
	{
		return skillStates[skill];
	}

	public void resetSkillStates()
	{
		for(int count = 0; count < Data.NUM_SKILLS; count++)
		{
			skillStates[count] = 0;
		}
		selectedSkill = NO_SKILL_SELECTED;
	}

	public int getCooldown(int skill)
	{
		return cooldowns[skill];
	}

	public abstract boolean canNormalAttack();

	public abstract boolean canShift();

	public boolean canSwap()
	{
		// Makes sure no skills are casting
		boolean casting = false;
		for(int count = 0; count < Data.NUM_SKILLS; count++)
		{
			if(skillStates[count] == CAST)
			{
				casting = true;
			}
		}
		return player.canAttack() && player.canMove() && player.canTurn() && !player.hasWeaponHitbox() && !casting;
	}

	public abstract void unlockSkill();

	public PlayState getPlayState()
	{
		return playState;
	}

	public InputManager getInput()
	{
		return input;
	}

	public Player getPlayer()
	{
		return player;
	}

	public int id()
	{
		return id;
	}
}