package indigo.Display;

import indigo.Entity.Player;
import indigo.GameState.PlayState;
import indigo.Main.Game;
import indigo.Manager.ContentManager;
import indigo.Manager.Data;
import indigo.Phase.Phase;
import indigo.Stage.BattleStage;
import indigo.Stage.DefendStage;
import indigo.Stage.Stage;
import indigo.Stage.SurvivalStage;
import indigo.Stage.TravelStage;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

// Shows information about the player
// Created and deleted alongside the PlayState game state
public class HUD
{
	// Retrieve current class and cooldowns from Data
	// Timer is in ticks
	private PlayState playState;
	private Stage stage;
	private Data data;

	private Player player;
	private Phase phase;

	private double health;
	private double mana;
	private double experience;

	public static final int WIDTH = Game.WIDTH;
	public static final int HEIGHT = 135;

	public HUD(PlayState playState, Stage stage)
	{
		// Gives the HUD access to other information
		this.playState = playState;
		this.stage = stage;
		data = playState.getData();

		player = (Player)playState.getPlayer();

		health = player.getHealth();
		mana = player.getMana();
		experience = data.getExperience();
	}

	public void update()
	{
		// Updates health and mana at a gradual rate for visual effect
		health = (health * 2 + player.getHealth()) / 3;
		mana = (mana * 2 + player.getMana()) / 3;
		experience = (experience * 2 + data.getExperience()) / 3;
	}

	public void render(Graphics2D g)
	{
		// Draws health, mana, and experience bars
		int anchorX = 230;
		int anchorY = 1020;
		g.setColor(Color.BLACK);
		g.fill(new Rectangle2D.Double(0, Game.HEIGHT - HEIGHT, WIDTH, HEIGHT));
		g.setColor(Color.RED);
		g.fill(new Rectangle2D.Double(anchorX + 34, anchorY - 25, health, 11));
		g.setColor(Color.BLUE);
		g.fill(new Rectangle2D.Double(anchorX + 34, anchorY - 12, mana, 11));
		g.setColor(Color.YELLOW);
		g.fill(new Rectangle2D.Double(anchorX + 34, anchorY + 1, 200 * experience / data.getMaxExperience(), 11));

		// Draws the decorative indicator on the left
		g.drawImage(ContentManager.getImage(ContentManager.INDICATOR), anchorX - 86, anchorY - 46, 100, 100, null);

		// Draws the stamina pointer
		double pointerAngle = Math.toRadians(55 * (((double)player.getStamina() - Player.BASE_STAMINA / 2) / 50));
		g.rotate(pointerAngle, anchorX, anchorY);
		g.drawImage(ContentManager.getImage(ContentManager.POINTER), anchorX - 90, anchorY - 3, 100, 7, null);
		g.rotate(-pointerAngle, anchorX, anchorY);

		// Draws the HUD
		g.drawImage(ContentManager.getImage(ContentManager.PLAYER_HUD), anchorX - 88, anchorY - 50, 350, 100, null);

		// Writes the player's level
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
		g.setColor(Color.BLACK);
		g.drawString(data.getLevel() + "", anchorX + 25 + 2, anchorY + 35);

		// Draw skill icons and cooldowns
		for(int i = 0; i < Data.NUM_SKILLS; i++)
		{
			if(phase.getSkillState(i) == Phase.SELECT)
			{
				g.setColor(Color.YELLOW);
				g.fill(new Rectangle2D.Double(anchorX + 300 + 150 * i, anchorY - 50, 90, 90));
			}
			else if(phase.getSkillState(i) == Phase.CAST)
			{
				g.setColor(Color.RED);
				g.fill(new Rectangle2D.Double(anchorX + 300 + 150 * i, anchorY - 50, 90, 90));
			}
			else
			{
				g.setColor(Color.GREEN);
				g.fill(new Rectangle2D.Double(anchorX + 300 + 150 * i, anchorY - 50, 90, 90));
				g.setColor(Color.BLUE);
				g.fill(new Rectangle2D.Double(anchorX + 300 + 150 * i, anchorY - 50, 90, (double)phase.getCooldown(i)
						/ phase.getMaxCooldown(i) * 90));
			}
		}

		// Draw phase state
		if(phase.canSwap() && playState.getSwapCooldown() == 0)
		{
			g.setColor(Color.WHITE);
			g.fill(new Rectangle2D.Double(anchorX - 200, anchorY - 30, 50, 50));
		}
		else
		{
			g.setColor(Color.GRAY);
			g.fill(new Rectangle2D.Double(anchorX - 200, anchorY - 30, 50, 50));
		}
		if(playState.getSwapCooldown() > 0)
		{
			g.setColor(Color.BLUE);
			g.fill(new Rectangle2D.Double(anchorX - 200, anchorY - 30, 50, (double)playState.getSwapCooldown()
					/ playState.getMaxSwapCooldown() * 50));
		}

		// Draw stage specific information
		g.setColor(Color.WHITE);
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
		g.setStroke(new BasicStroke(4));
		if(stage instanceof BattleStage)
		{
			BattleStage battleStage = (BattleStage)stage;
			g.drawString("Enemies defeated: " + battleStage.getEnemiesDefeated(), Game.WIDTH - 250, Game.HEIGHT - 70);
			g.drawString("Enemies to defeat: " + battleStage.getEnemiesToDefeat(), Game.WIDTH - 250, Game.HEIGHT - 40);
		}
		else if(stage instanceof DefendStage)
		{
			DefendStage defendStage = (DefendStage)stage;
			g.drawString("Core health: " + defendStage.getCoreHealth() + " / " + defendStage.getCoreMaxHealth(),
					Game.WIDTH - 250, Game.HEIGHT - 70);
			g.drawString("Time remaining: " + ((defendStage.getSurvivalDuration() - playState.getTime()) / 30),
					Game.WIDTH - 250, Game.HEIGHT - 40);
		}
		else if(stage instanceof SurvivalStage)
		{
			SurvivalStage survivalStage = (SurvivalStage)stage;
			g.drawString("Time remaining: " + ((survivalStage.getSurvivalDuration() - playState.getTime()) / 30),
					Game.WIDTH - 250, Game.HEIGHT - 40);
		}
		else if(stage instanceof TravelStage)
		{
			TravelStage travelStage = (TravelStage)stage;
			g.drawString("Time remaining: " + ((travelStage.getTimeLimit() - playState.getTime()) / 30),
					Game.WIDTH - 250, Game.HEIGHT - 40);
		}
	}

	public void setPhase(Phase phase)
	{
		this.phase = phase;
	}
}