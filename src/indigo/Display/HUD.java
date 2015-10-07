package indigo.Display;

import indigo.Entity.Player;
import indigo.GameState.PlayState;
import indigo.Manager.ContentManager;
import indigo.Manager.Data;
import indigo.Phase.Phase;

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
	private Data data;

	private Player player;
	private Phase phase;

	public HUD(PlayState playState)
	{
		// Gives the HUD access to other information
		this.playState = playState;
		data = playState.getData();

		player = (Player)playState.getEntities().get(0);
	}

	public void update()
	{
		// Consider doing Zeno's Paradox shit for cool heal/mana effect
	}

	public void render(Graphics2D g)
	{
		// Draws health and mana bars
		int anchorX = 88;
		int anchorY = 90;
		g.setColor(Color.RED);
		g.fill(new Rectangle2D.Double(anchorX + 34, anchorY - 25, player.getHealth(), 11));
		g.setColor(Color.BLUE);
		g.fill(new Rectangle2D.Double(anchorX + 34, anchorY - 12, player.getMana(), 11));

		// TODO Draw the experience bar

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

		// TODO Draw cooldowns and timer
	}

	public void setPhase(Phase phase)
	{
		this.phase = phase;
	}
}