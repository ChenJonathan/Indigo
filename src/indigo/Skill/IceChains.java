package indigo.Skill;

import indigo.Manager.ContentManager;
import indigo.Manager.SoundManager;
import indigo.Phase.Phase;
import indigo.Projectile.IceChainHook;

public class IceChains extends Skill
{
	private IceChainHook hook;

	public IceChains(Phase phase, int position)
	{
		super(phase, position);
		id = Skill.CHAINS;
		icon = ContentManager.getImage(ContentManager.SKILL_ICE_CHAINS);
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

			SoundManager.play(ContentManager.ICE_CHAINS_START_EFFECT);
			SoundManager.play(ContentManager.ICE_CHAINS_MID_EFFECT);
		}
		else if(hook.isDead() || !playState.getProjectiles().contains(hook))
		{
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
		hook = null;
		player.canAttack(true);
		player.canTurn(true);

		SoundManager.removeSound(ContentManager.ICE_CHAINS_MID_EFFECT);
		SoundManager.play(ContentManager.ICE_CHAINS_END_EFFECT);
	}
}
