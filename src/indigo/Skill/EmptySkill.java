package indigo.Skill;

import indigo.Phase.Phase;

public class EmptySkill extends Skill
{
	public EmptySkill(Phase phase, int position)
	{
		super(phase, position);
		id = Skill.EMPTY;
	}
	
	public boolean canCast()
	{
		return false;
	}

}
