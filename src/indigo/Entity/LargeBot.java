package indigo.Entity;

import indigo.Manager.Content;
import indigo.Stage.Stage;

import java.awt.Graphics;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

// TODO Finish - Requires ground unit AI
public class LargeBot extends Entity
{
	private final int DEFAULT = 0;
	private final int DEATH = 1;
	
	public final double acceleration = 8;
	public final double moveSpeed = 50;
	public final double jumpSpeed = 50;
	
	public static final double LARGEBOT_WIDTH = 70;
	public static final double LARGEBOT_HEIGHT = 200;
	public static final int BASE_HEALTH = 100;
	
	public LargeBot(Stage stage, double x, double y, int health) 
	{
		super(stage, x, y, health);
		name = "a large bot";
		
		width = LARGEBOT_WIDTH;
		height = LARGEBOT_HEIGHT;

		pushability = 5;
		flying = false;
		frictionless = false;
		
		friendly = false;
		
		setAnimation(DEFAULT, Content.SMALL_BOT_IDLE, -1);
	}
	
	public void update()
	{
		super.update();
		
		if(canAttack())
		{
			attack();
		}
		
	}
	
	public void render(Graphics g) 
	{
		
	}
	
	public double getCenterX()
	{
		return getX();
	}
	
	public double getCenterY()
	{
		return getY() - getHeight() / 2;
	}
	
	public Shape getHitbox() 
	{
		return new Rectangle2D.Double(getX() - getWidth()/2, getY() - getHeight(), getWidth(), getHeight() - getWidth());
	}
	
	public void left()
	{
		setDirection(false);
		if(canMove())
		{
			setVelX(getVelX() - moveSpeed);
		}
	}
	
	public void right()
	{
		setDirection(true);
		if(canMove())
		{
			setVelX(getVelX() + moveSpeed);
		}
	}
	
	public void attack()
	{
		// Set canAttack to true before attacking, set to false after animation plays
	}
	
	public boolean isActive()
	{
		return currentAnimation != DEATH;
	}
	
	public void die()
	{
		setAnimation(DEATH, Content.SMALL_BOT_IDLE, -1);
		dead = true; // Temporary
	}
}