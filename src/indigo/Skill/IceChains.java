package indigo.Skill;

import indigo.Manager.InputManager;
import indigo.Phase.Phase;
import indigo.Projectile.IceChainParticle;

public class IceChains extends Skill
{
	
	public IceChains(Phase phase, int position)
	{
		super(phase, position);
		id = Skill.CHAINS;
		castOnSelect = true;
	}
	
    public void update()
    {
        super.update();
        
        if (player.getMana() >= 0)
        {
	        if(castTime == 0)
	        {
	        	player.setIceChains(true);
	        }
	        player.setMana(player.getMana() - 0);

        }
        else
        {
        	
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
