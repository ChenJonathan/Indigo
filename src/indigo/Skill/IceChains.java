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

			player.attackMain();
			if(player.isFacingRight())
			{
				hook = new IceChainHook(player, player.getX() + IceChainHook.EXTENSION_RANGE, player.getY(), dx / scale
						* IceChainHook.SPEED, dy / scale * IceChainHook.SPEED, 0);
			}
			else
			{
				hook = new IceChainHook(player, player.getX() - IceChainHook.EXTENSION_RANGE, player.getY(), dx / scale
						* IceChainHook.SPEED, dy / scale * IceChainHook.SPEED, 0);
			}
			playState.getProjectiles().add(hook);

			player.setMana(player.getMana() - 40);
			player.canAttack(false);
			player.canTurn(false);
		}
		else if(hook.isDead() || !playState.getProjectiles().contains(hook))
		{
			hook = null;
			endCast();
		}
	}

	public boolean canCast()
	{
		return player.getMana() > 40 && player.canAttack();
	}

	public void endCast()
	{
		super.endCast();
		player.canAttack(true);
		player.canTurn(true);
	}
}
