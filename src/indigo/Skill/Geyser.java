package indigo.Skill;

import indigo.Manager.ContentManager;
import indigo.Manager.Manager;
import indigo.Manager.SoundManager;
import indigo.Phase.Phase;
import indigo.Projectile.GeyserParticle;
import indigo.Projectile.GeyserBase;
import indigo.Weapon.Staff;

public class Geyser extends Skill
{
	private GeyserBase geyser;

	public Geyser(Phase phase, int position)
	{
		super(phase, position);
		id = Skill.GEYSER;
		icon = ContentManager.getImage(ContentManager.SKILL_GEYSER);
	}

	public void update()
	{
		super.update();

		if(player.getMana() >= 2 && (Manager.input.mouseDown() || castTime < 10))
		{
			if(castTime == 0)
			{
				player.canAttack(false);
				geyser = new GeyserBase(player, playState.getMouseX(), playState.getMapY() - GeyserBase.HEIGHT / 2, 0,
						0, 0);
				playState.getProjectiles().add(geyser);

				((Staff)player.getWeapon()).cast();

				SoundManager.play(ContentManager.GEYSER_START_EFFECT);
				SoundManager.play(ContentManager.GEYSER_MID_EFFECT);
			}

			playState.getProjectiles().add(
					0,
					new GeyserParticle(player, geyser.getX(), playState.getMapY() - GeyserParticle.HEIGHT / 2
							+ GeyserParticle.SPEED, 0, -GeyserParticle.SPEED, GeyserParticle.DAMAGE));
			player.setMana(player.getMana() - 1); // TODO Revert

			((Staff)player.getWeapon()).holdCast();
		}
		else
		{
			endCast();
			
			SoundManager.removeSound(ContentManager.GEYSER_MID_EFFECT);
		}
	}

	public boolean canCast()
	{
		return player.getMana() >= 20 && player.canAttack();
	}

	public void endCast()
	{
		playState.getProjectiles().remove(geyser);
		geyser = null;
		player.canAttack(true);

		phase.setAttackTimer(10);
		super.endCast();
	}
}