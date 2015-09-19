package indigo.Entity;

import indigo.Manager.Content;
import indigo.Projectile.ElectricBall;
import indigo.Stage.Stage;

import java.awt.Graphics;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

public class SmallBot extends Entity 
{
	private final int DEFAULT = 0;
	private final int DEATH = 1;
	
	private int timer; // When timer hits zero, move and reset timer
	
	private final int MOVE_SPEED = 30;
	private final int MAX_RANGE = 1000;
	
	public static final double SMALLBOT_WIDTH = 60;
	public static final double SMALLBOT_HEIGHT = 60;
	public static final int BASE_HEALTH = 50;
	public static final int DEFAULT_TIMER = 30;
	
	public SmallBot(Stage stage, double x, double y, int health) 
	{
		super(stage, x, y, health);
		name = "a small bot";
		
		width = SMALLBOT_WIDTH;
		height = SMALLBOT_HEIGHT;
		
		movability = 5;
		flying = true;
		frictionless = false;
		
		friendly = false;
		
		timer = DEFAULT_TIMER;
		
		setAnimation(DEFAULT, Content.SMALL_BOT_IDLE, -1);
	}
	
	public void update()
	{
		if(currentAnimation == DEATH)
		{
			// Run dying animation and don't do anything else
			animation.update();
			if(animation.hasPlayedOnce())
			{
				// Mark entity as dead if dying animation has finished playing
				dead = true;
			}
			
			return;
		}
		
		super.update();
		
		if(timer == 0)
		{
			timer = DEFAULT_TIMER;
			
			if(Math.random() < 0.5)
			{
				// 50% chance to move every time the timer counts down
				move();
			}
		}
		else
		{
			timer--;
		}
		
		if(canAttack() && Math.random() < 0.02)
		{
			// 2% chance to attack every tick
			attack();
		}
	}
	
	public void render(Graphics g) 
	{
		g.drawImage(animation.getImage(), (int)(getX() - getWidth() / 2), (int)(getY() - getHeight() / 2), (int)getWidth(), (int)getHeight(), null);
	}
	
	public Shape getHitbox() 
	{
		return new Ellipse2D.Double(getX() - getWidth()/2, getY() - getHeight()/2, getWidth(), getHeight());
	}
	
	public void attack()
	{
		// Scale is the distance from entity to player
		double scale = Math.sqrt(Math.pow(stage.getPlayer().getY() - getY(), 2) + Math.pow(stage.getEntities().get(0).getX() - getX(), 2));
		if(scale > MAX_RANGE)
		{
			return;
		}
		// Splits the direction of the projectile into x and y components
		double velX = ElectricBall.SPEED * (stage.getEntities().get(0).getX() - getX()) / scale;
		double velY = ElectricBall.SPEED * (stage.getEntities().get(0).getY() - getY()) / scale;
		
		stage.getProjectiles().add(new ElectricBall(this, getX() + velX / 2, getY() + velY / 2, velX, velY, ElectricBall.DAMAGE));
	}
	
	public void move()
	{
		boolean outOfBounds = false;
		
		do
		{
			double rand = 0;
			if(getY() <= 0)
			{
				// If above the map, only downwards movement is allowed
				outOfBounds = true;
				rand = Math.random() / 2 + 0.5;
			}
			else
			{
				rand = Math.random();
			}
			
			if(rand >= 0.25 || rand > 0.75) // If facing left
			{
				setDirection(false);
			}
			else // Facing right
			{
				setDirection(true);
			}
			
			// Splits movement into x and y components
			setVelX(Math.cos(2 * rand * Math.PI) * MOVE_SPEED);
			setVelY(Math.sin(2 * rand * Math.PI) * MOVE_SPEED);
		}
		while(getX() + 30 * getVelX() > stage.getMapX() || getX() + 30 * getVelX() < 0 || getY() + 30 * getVelY() > stage.getMapY() || (getY() + 30 * getVelY() < 0 && !outOfBounds));
		// Repeat the loop if this movement would carry the entity outside of map boundaries
	}
	
	public boolean isActive()
	{
		return currentAnimation != DEATH;
	}
	
	public void die()
	{
		setAnimation(DEATH, Content.SMALL_BOT_DEATH, 5);
	}
}