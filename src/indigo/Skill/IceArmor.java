package indigo.Skill;

import indigo.Manager.InputManager;
import indigo.Phase.Phase;

public class IceArmor extends Skill
{

    public IceArmor(Phase phase, int position)
    {
        super(phase, position);
        id = Skill.ICE_ARMOR;
    }
    
    public void update()
    {
        super.update();
        
        if(castTime == 0)
        {
        	player.setIceArmor(true);
        	phase.resetAttackTimer();
        }
        else if(player.getMana() == 0 || input.keyPress(InputManager.K3))
        {
            endCast();
        }
    }
    
    public boolean canCast()
    {
        return (player.getMana() > 0 && player.canAttack());
    }
    
    public void endCast()
    {
        super.endCast();
        player.setIceArmor(false);
    }
}