package indigo.Interactive;

import indigo.Entity.Player;
import indigo.Manager.ContentManager;
import indigo.Stage.Stage;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

public class HealthPickup extends Interactive
{
	private final int IDLE = 0;
	private final int SPAWN = 1;
	private final int DEATH = 2;

	public final static int HEALTH = 100;
	public final static int WIDTH = 50;
	public final static int HEIGHT = 50;

	public HealthPickup(Stage stage, double x, double y)
	{
		super(stage, x, y);
		width = WIDTH;
		height = HEIGHT;

		setAnimation(SPAWN, ContentManager.getAnimation(ContentManager.HEALTH_PICKUP_SPAWN), 3);
	}

	public void update()
	{
		super.update();

		if(currentAnimation == SPAWN)
		{
			if(animation.hasPlayedOnce())
			{
				setAnimation(IDLE, ContentManager.getAnimation(ContentManager.HEALTH_PICKUP_IDLE), 6);
			}
		}
		else if(currentAnimation == DEATH)
		{
			if(animation.hasPlayedOnce())
			{
				dead = true;
			}
		}
	}

	public void render(Graphics2D g)
	{
		g.drawImage(animation.getImage(), (int)(getX() - getWidth() / 2), (int)(getY() - getHeight() / 2),
				(int)getWidth(), (int)getHeight(), null);
	}

	public Shape getHitbox()
	{
		return new Ellipse2D.Double(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
	}

	public void activate(Player player)
	{
		if(player.getHealth() < player.getMaxHealth())
		{
			player.setHealth(player.getHealth() + HEALTH);
			die();
		}
	}

	public boolean isActive()
	{
		return currentAnimation != DEATH;
	}

	public void die()
	{
		setAnimation(DEATH, ContentManager.getAnimation(ContentManager.HEALTH_PICKUP_DEATH), 2);
	}
}