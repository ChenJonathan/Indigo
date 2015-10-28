package indigo.Interactive;

import indigo.Manager.ContentManager;
import indigo.Stage.Stage;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

public class ManaPickup extends Interactive
{
	private final int DEFAULT = 0;

	public final static int MANA = 100;
	public final static int WIDTH = 50;
	public final static int HEIGHT = 50;
	public final static double SPEED = 0;

	private int timer = 0;

	public ManaPickup(Stage stage, double x, double y)
	{
		super(stage, x, y);
		width = WIDTH;
		height = HEIGHT;

		setAnimation(DEFAULT, ContentManager.getAnimation(ContentManager.MANA_PICKUP), -1);
	}

	public void update()
	{
		super.update();
		
		timer++;
		if(timer > 40)
		{
			timer = 0;
		}
	}

	public void render(Graphics2D g)
	{
		if(timer < 5)
		{
			g.drawImage(animation.getImage(), (int)(getX() - getWidth() / 2), (int)(getY() - getHeight() / 2), (int)getWidth(), (int)getHeight(), null);
		}
		else if(timer < 10)
		{
			g.drawImage(animation.getImage(), (int)(getX() - getWidth() / 2), (int)(getY() - getHeight() / 2) + 2, (int)getWidth(), (int)getHeight(), null);
		}
		else if(timer < 15)
		{
			g.drawImage(animation.getImage(), (int)(getX() - getWidth() / 2), (int)(getY() - getHeight() / 2) + 3, (int)getWidth(), (int)getHeight(), null);
		}
		else if(timer < 20)
		{
			g.drawImage(animation.getImage(), (int)(getX() - getWidth() / 2), (int)(getY() - getHeight() / 2) + 2, (int)getWidth(), (int)getHeight(), null);
		}
		else if(timer < 25)
		{
			g.drawImage(animation.getImage(), (int)(getX() - getWidth() / 2), (int)(getY() - getHeight() / 2), (int)getWidth(), (int)getHeight(), null);
		}
		else if(timer < 30)
		{
			g.drawImage(animation.getImage(), (int)(getX() - getWidth() / 2), (int)(getY() - getHeight() / 2) - 2, (int)getWidth(), (int)getHeight(), null);
		}
		else if(timer < 35)
		{
			g.drawImage(animation.getImage(), (int)(getX() - getWidth() / 2), (int)(getY() - getHeight() / 2) - 3, (int)getWidth(), (int)getHeight(), null);
		}
		else
		{
			g.drawImage(animation.getImage(), (int)(getX() - getWidth() / 2), (int)(getY() - getHeight() / 2) - 2, (int)getWidth(), (int)getHeight(), null);
		}
	}
	
	public String getName()
	{
		return "a mana pickup";
	}

	public Shape getHitbox()
	{
		return new Ellipse2D.Double(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
	}

	public void activate()
	{
		if(player.getMana() < player.getMaxMana())
		{
			player.setMana(player.getMana() + MANA);
			die();
		}
	}
	
	public boolean isActive() // TODO Add death animation
	{
		return true;
	}

	// TODO Death animation
	public void die()
	{
		dead = true;
	}
}