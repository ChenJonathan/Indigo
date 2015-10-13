package indigo.Skill;

import indigo.Phase.Phase;

public class LockedSkill extends Skill
{
	public LockedSkill(Phase phase, int position)
	{
		super(phase, position);
		id = Skill.EMPTY;
	}

	public boolean canCast()
	{
		return false;
	}
}