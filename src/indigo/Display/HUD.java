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

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
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
	public static final int HEIGHT = Game.HEIGHT / 8;

	public static final int HEALTH_BAR_LENGTH = 522;
	public static final int MANA_BAR_LENGTH = 510;
	public static final int EXPERIENCE_BAR_LENGTH = 502;

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
		experience = Math.min((experience * 2 + data.getExperience()) / 3, data.getMaxExperience());
	}

	public void render(Graphics2D g)
	{
		// Draws health, mana, and experience bars
		g.setColor(Color.WHITE);
		g.fillRect(350, 950, 670, 130);
		g.setColor(Color.RED);
		g.fill(new Rectangle2D.Double(490, 969, health / player.getMaxHealth() * HEALTH_BAR_LENGTH, 18));
		g.setColor(Color.BLUE);
		g.fill(new Rectangle2D.Double(502, 988, mana / player.getMaxMana() * MANA_BAR_LENGTH, 18));
		g.setColor(Color.YELLOW);
		g.fill(new Rectangle2D.Double(506, 1007, experience / data.getMaxExperience() * EXPERIENCE_BAR_LENGTH, 4));

		// Draw skill icons and cooldowns
		for(int i = 0; i < Data.NUM_SKILLS; i++)
		{
			if(phase.getSkillState(i) == Phase.SELECT)
			{
				g.setColor(Color.YELLOW);
				g.fill(new Rectangle2D.Double(1306 + 187 * i, 962, 101, 101));
			}
			else if(phase.getSkillState(i) == Phase.CAST)
			{
				g.setColor(Color.RED);
				g.fill(new Rectangle2D.Double(1306 + 187 * i, 962, 101, 101));
			}
			else
			{
				g.setColor(Color.GREEN);
				g.fill(new Rectangle2D.Double(1306 + 187 * i, 962, 101, 101));
				Graphics2D gClip = (Graphics2D)g.create();
				gClip.clipRect(1306 + 187 * i, 962, 101, 101);
				gClip.setColor(Color.BLUE);
				gClip.fill(new Arc2D.Double(1285 + 187 * i, 941, 143, 143, 90, 360.0 * (double)phase.getCooldown(i)
						/ phase.getMaxCooldown(i), Arc2D.PIE));
			}
		}

		// Draws main HUD bar
		if(phase.id() == Phase.WATER)
		{
			g.drawImage(ContentManager.getImage(ContentManager.HUD_WATER), 0, Game.HEIGHT - HEIGHT, null);
		}
		else
		{
			g.drawImage(ContentManager.getImage(ContentManager.HUD_ICE), 0, Game.HEIGHT - HEIGHT, null);
		}

		// Draws the phase swap cooldown
		g.setColor(Color.BLUE);
		g.fill(new Arc2D.Double(489, 1040, 32, 33, 90, 360.0 * playState.getSwapCooldown()
				/ playState.getMaxSwapCooldown(), Arc2D.PIE));

		// Writes the player's level
		g.setColor(Color.BLACK);
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 48));
		FontMetrics fontMetrics = g.getFontMetrics();
		String text = data.getLevel() + "";
		g.drawString(text, 438 - fontMetrics.stringWidth(text) / 2, 1012 + fontMetrics.getHeight() / 4);

		// Draws the stamina spring
		double staminaRatio = 1 - (double)player.getStamina() / player.getMaxStamina();
		if(phase.id() == Phase.WATER)
		{
			g.drawImage(ContentManager.getImage(ContentManager.SPRING_TOP_WATER), 1040, (int)(963 + 89 * staminaRatio),
					null);
		}
		else
		{
			g.drawImage(ContentManager.getImage(ContentManager.SPRING_TOP_ICE), 1040, (int)(963 + 89 * staminaRatio),
					null);
		}
		g.drawImage(ContentManager.getImage(ContentManager.SPRING), 1040, (int)(973 + 89 * staminaRatio), 101,
				1062 - (int)(973 + 89 * staminaRatio), null);

		// Draw stage specific information
		g.setColor(Color.BLACK);
		g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
		fontMetrics = g.getFontMetrics();
		String[] objectiveText = new String[0];
		if(stage instanceof BattleStage)
		{
			objectiveText = new String[3];
			objectiveText[0] = "Name: " + stage.getName();
			objectiveText[1] = "Objective: Battle";
			BattleStage battleStage = (BattleStage)stage;
			objectiveText[2] = "Enemies defeated: " + battleStage.getEnemiesDefeated() + " / "
					+ battleStage.getEnemiesToDefeat();
		}
		else if(stage instanceof DefendStage)
		{
			objectiveText = new String[4];
			objectiveText[0] = "Name: " + stage.getName();
			objectiveText[1] = "Objective: Defend";
			DefendStage defendStage = (DefendStage)stage;
			objectiveText[2] = "Core health: " + defendStage.getCoreHealth() + " / " + defendStage.getCoreMaxHealth();
			objectiveText[3] = "Time remaining: " + ((defendStage.getSurvivalDuration() - playState.getTime()) / 30);
		}
		else if(stage instanceof SurvivalStage)
		{
			objectiveText = new String[3];
			objectiveText[0] = "Name: " + stage.getName();
			objectiveText[1] = "Objective: Survival";
			SurvivalStage survivalStage = (SurvivalStage)stage;
			objectiveText[2] = "Time remaining: " + ((survivalStage.getSurvivalDuration() - playState.getTime()) / 30);
		}
		else if(stage instanceof TravelStage)
		{
			objectiveText = new String[3];
			objectiveText[0] = "Name: " + stage.getName();
			objectiveText[1] = "Objective: Travel";
			TravelStage travelStage = (TravelStage)stage;
			objectiveText[2] = "Time remaining: " + ((travelStage.getTimeLimit() - playState.getTime()) / 30);
		}
		for(int count = 0; count < objectiveText.length; count++)
		{
			g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
			String[] line = objectiveText[count].split(":");
			g.drawString(line[0] + ":", 30, 965 + (fontMetrics.getHeight() / 2 + 10) * (count + 1));

			g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
			g.drawString(line[1], 30 + fontMetrics.stringWidth(line[0] + ": "), 965
					+ (fontMetrics.getHeight() / 2 + 10) * (count + 1));
		}
	}

	public void setPhase(Phase phase)
	{
		this.phase = phase;
	}
}