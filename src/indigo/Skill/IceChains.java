package indigo.Skill;

import indigo.Manager.InputManager;
import indigo.Phase.Phase;
import indigo.Projectile.IceChainParticle;

public class IceChains extends Skill
{
	public IceChainParticle hook;
	
	public IceChains(Phase phase, int position)
	{
		super(phase, position);
		id = Skill.CHAINS;
		castOnSelect = true;
	}
	
    public void update()
    {
        super.update();
        
        if (player.getMana() >= 1 && (input.keyPress(InputManager.K4) || castTime < 10))
        {
			if(castTime == 0)
			{
				player.setIceChains(true);
				hook = new IceChainParticle(player, playState.getMouseX(), playState.getMouseY(), 5, -5, 0);
				player.setMana(player.getMana() - 1);
				playState.getProjectiles().add(hook);
			}
		   
		
		}
        else
        {
			playState.getProjectiles().remove(hook);
			hook = null;
			endCast();
        }
    }
    
	public boolean canCast()
	{
		return castOnSelect;
	}
	
    public void endCast()
    {
        super.endCast();
        player.setIceChains(false);
    }
}
