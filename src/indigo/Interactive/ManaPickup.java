package indigo.Interactive;

import indigo.Manager.ContentManager;
import indigo.Stage.Stage;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

public class ManaPickup extends Interactive
{
	private final int IDLE = 0;
	private final int SPAWN = 1;
	private final int DEATH = 2;

	public final static int MANA = 100;
	public final static int WIDTH = 50;
	public final static int HEIGHT = 50;

	public ManaPickup(Stage stage, double x, double y)
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
		g.drawImage(animation.getImage(), (int)(getX() - getWidth() / 2), (int)(getY() - getHeight() / 2), null);
	}

	public String getName()
	{
		return "a mana pickup";
	}

	public Shape getHitbox()
	{
		return new Ellipse2D.Double(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
	}

	public void collide()
	{
		if(player.getMana() < player.getMaxMana())
		{
			player.setHealth(player.getMana() + MANA);
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