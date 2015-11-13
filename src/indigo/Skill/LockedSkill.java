package indigo.Skill;

import indigo.Manager.ContentManager;
import indigo.Phase.Phase;

public class LockedSkill extends Skill
{
	public LockedSkill(Phase phase, int position)
	{
		super(phase, position);
		id = Skill.EMPTY;
		icon = ContentManager.getImage(ContentManager.SKILL_LOCKED);
	}

	public boolean canCast()
	{
		return false;
	}
}