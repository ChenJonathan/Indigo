package indigo.Phase;

import indigo.GameState.PlayState;

public class Ice extends Phase
{
	public int attackDelay = 30;
	
	public static final int CRASH = 0;
	public static final int GLIDE = 1;
	public static final int SPIRAL = 2;
	public static final int STORM = 3;
	
	public Ice(PlayState playState)
	{
		super(playState);
		id = Phase.ICE;
		
		maxCooldowns = new int[] {0, 300, 300, 1800};
		
		// TODO Add skills to skills array
	}
	
	public boolean canNormalAttack()
	{
		if(player.canAttack() && (playState.getTime() - attackStartTime >= attackDelay))
		{
			return true;
		}
		return false;
	}
	
	public boolean canShift()
	{
		return true; // TODO Finish
	}
}