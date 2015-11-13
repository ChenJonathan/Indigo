package indigo.Display;

import java.awt.Color;
import java.awt.Graphics2D;

import indigo.Entity.Entity;

public class HealthBar
{
	private Entity ent;

	private double health;
	private int timer;

	public static final int WIDTH = 100;
	public static final int HEIGHT = 10;
	public static final int OFFSET = 30;
	public static final int DURATION = 300;

	public HealthBar(Entity ent)
	{
		this.ent = ent;
	}

	public void update()
	{
		// Updates health at a gradual rate for visual effect
		health = (health * 2 + ent.getHealth()) / 3;

		timer--;
	}

	public void render(Graphics2D g)
	{
		g.setColor(Color.WHITE);
		g.fillRect((int)(ent.getX() - WIDTH / 2), (int)(ent.getY() - ent.getHeight() / 2 - OFFSET), WIDTH, HEIGHT);
		g.setColor(Color.RED);
		g.fillRect((int)(ent.getX() - WIDTH / 2), (int)(ent.getY() - ent.getHeight() / 2 - OFFSET),
				(int)(health / ent.getMaxHealth() * WIDTH), HEIGHT);
	}

	public boolean isActive()
	{
		return timer > 0;
	}

	public void activate(int health)
	{
		this.health = health;
		timer = DURATION;
	}
}