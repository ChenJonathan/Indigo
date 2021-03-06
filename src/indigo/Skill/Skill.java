package indigo.Skill;

import java.awt.image.BufferedImage;

import indigo.Entity.Player;
import indigo.GameState.PlayState;
import indigo.Phase.Phase;
import indigo.Stage.Stage;

public abstract class Skill
{
	protected PlayState playState;
	protected Stage stage;
	protected Phase phase;

	protected Player player;
	protected BufferedImage icon;
	protected int id; // Initialize in each skill constructor
	protected int position;

	protected int castTime;
	protected boolean castOnSelect;

	public static final int EMPTY = 0;
	public static final int MIST = 1;
	public static final int GEYSER = 2;
	public static final int PULSE = 3;
	public static final int WHIRLWIND = 4;
	public static final int CHAINS = 5;
	public static final int ARMOR = 6;

	public Skill(Phase phase, int position)
	{
		playState = phase.getPlayState();
		stage = playState.getStage();
		this.phase = phase;
		player = phase.getPlayer();

		this.position = position;
		castTime = -1;
		castOnSelect = false;
	}

	public void update()
	{
		castTime++;
	}

	public abstract boolean canCast();

	public void endCast()
	{
		castTime = -1;
		phase.endCast(position); // Resets skill icon and resets attack timer
	}
	
	public BufferedImage icon()
	{
		return icon;
	}

	public int id()
	{
		return id;
	}

	public boolean isCastOnSelect()
	{
		return castOnSelect;
	}
}