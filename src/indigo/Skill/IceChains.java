package indigo.Skill;

import indigo.Phase.Phase;
import indigo.Projectile.IceChainHook;

public class IceChains extends Skill
{
	private IceChainHook hook;

	public IceChains(Phase phase, int position)
	{
		super(phase, position);
		id = Skill.CHAINS;
		castOnSelect = false;
	}

	public void update()
	{
		super.update();

		if(castTime == 0)
		{
			double dx = playState.getMouseX() - player.getX();
			double dy = playState.getMouseY() - player.getY();
			double scale = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));

			hook = new IceChainHook(player, player.getX(), player.getY(), dx / scale * IceChainHook.SPEED, dy
					/ scale * IceChainHook.SPEED, 0);
			playState.getProjectiles().add(hook);
			
			player.setMana(player.getMana() - 1);
			player.canAttack(false);
			player.canTurn(false);
			phase.resetAttackTimer();
		}
		else if(hook.isDead() || !playState.getProjectiles().contains(hook))
		{
			hook = null;
			endCast();
		}
	}

	public boolean canCast()
	{
		return player.getMana() > 1 && player.canAttack();
	}

	public void endCast()
	{
		super.endCast();
		player.canAttack(true);
		player.canTurn(true);
	}
}
