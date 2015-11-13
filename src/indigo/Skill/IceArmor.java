package indigo.Skill;

import indigo.Manager.ContentManager;
import indigo.Manager.InputManager;
import indigo.Manager.Manager;
import indigo.Phase.Phase;

public class IceArmor extends Skill
{
	public IceArmor(Phase phase, int position)
	{
		super(phase, position);
		id = Skill.ARMOR;
		icon = ContentManager.getImage(ContentManager.SKILL_ICE_ARMOR);
		castOnSelect = true;
	}

	public void update()
	{
		super.update();

		if(castTime == 0)
		{
			player.setIceArmor(true);
			phase.setAttackTimer(30);
		}
		else if(player.getMana() == 0 || (player.canMove() && Manager.input.keyPress(InputManager.K3)))
		{
			endCast();
		}
	}

	public boolean canCast()
	{
		return player.getMana() > 0 && player.canMove();
	}

	public void endCast()
	{
		player.setIceArmor(false);
		phase.setAttackTimer(30);
		super.endCast();
	}
}