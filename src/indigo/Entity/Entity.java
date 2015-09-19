package indigo.Entity;

import indigo.Landscape.Land;
import indigo.Manager.Animation;
import indigo.Projectile.Projectile;
import indigo.Stage.Stage;
import indigo.Weapon.Weapon;

import java.awt.Graphics;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public abstract class Entity
{
	protected Stage stage;
	
	protected String name; // Used upon defeat to show who killed you
	
	private double x, y;
	private double velX, velY;
	
	private double prevX, prevY;
	private Line2D.Double travel; // Line formed by previous and current positions; used for collision checking
	
	protected double width, height;
	protected double movability; // How much an entity moves when pushed by another entity
	
	private int health, maxHealth;
	
	protected Animation animation; // Used to render entities
	protected int currentAnimation; // Current animation frame for the entity

	protected boolean facingRight;
	
	protected boolean dodging; // Prevents all collision
	protected boolean blocking; // Prevents (or halves) damage
	
	protected Land ground; // Ground that the entity is standing on (null if in air)
	protected Weapon weapon; // Melee weapon (null if none)
	
	private boolean canAttack;
	private boolean canMove;
	
	protected boolean flying;
	protected boolean frictionless;

	protected boolean friendly;
	protected boolean marked;
	protected boolean dead;
	
	// Subclasses - Initialize name, width, height, solid, flying, frictionless, movability, move speed, jump speed
	public Entity(Stage stage, double x, double y, int health)
	{
		this.stage = stage;
		name = "";

		velX = velY = 0;
		
		this.x = prevX = x;
		this.y = prevY = y;
		travel = new Line2D.Double(prevX, prevY, x, y); // Line formed by previous and current position; used to check for collision
		
		maxHealth = this.health = health;
		
		canAttack = true;
		canMove = true;
		
		marked = false;
		
		animation = new Animation();
	}
	
	// Method used to change animation
	protected void setAnimation(int count, BufferedImage[] images, int delay)
	{
		currentAnimation = count;
		animation.setFrames(images);
		animation.setDelay(delay);
	}
	
	// Standard position updating stuff: gravity, friction, etc
	public void update()
	{
		// Prevents x or y velocity from being too extreme (glitching through walls)
		if(velX < -Stage.TERMINAL_VELOCITY)
		{
			velX = -Stage.TERMINAL_VELOCITY;
		}
		else if(velX > Stage.TERMINAL_VELOCITY)
		{
			velX = Stage.TERMINAL_VELOCITY;
		}
		if(velY < -Stage.TERMINAL_VELOCITY)
		{
			velY = -Stage.TERMINAL_VELOCITY;
		}
		else if(velY > Stage.TERMINAL_VELOCITY)
		{
			velY = Stage.TERMINAL_VELOCITY;
		}
		
		prevX = x;
		prevY = y;
		
		x += velX;
		y += velY;
		
		// Friction and gravity
		if(flying)
		{
			if(!frictionless)
			{
				double vel = Math.sqrt(Math.pow(velX, 2) + Math.pow(velY, 2));
				
				// Flying entities gradually slow down
				if(vel > Stage.FRICTION)
				{
					if(velX < 0)
					{
						velX = velX + Stage.FRICTION * -velX / vel;
					}
					else if(velX > 0)
					{
						velX = velX - Stage.FRICTION * velX / vel;
					}
					if(velY < 0)
					{
						velY = velY + Stage.FRICTION * -velY / vel;
					}
					else if(velY > 0)
					{
						velY = velY - Stage.FRICTION * velY / vel;
					}
				}
				else
				{
					velX = 0;
					velY = 0;
				}
			}
		}
		else
		{
			if(!frictionless)
			{
				// Ground entities slow down horizontally
				if(velX < 0)
				{
					velX = Math.min(velX + Stage.FRICTION, 0);
				}
				else if(velX > 0)
				{
					velX = Math.max(velX - Stage.FRICTION, 0);
				}
			}
			
			// Applying gravity to ground entities
			if(ground == null)
			{
				velY += Stage.GRAVITY;
			}
		}
		
		// If the entity is grounded, take the y position corresponding to the ground - Important for slanted surfaces
		if(ground != null)
		{
			y = ground.getSurface(x) - getHeight() / 2;
		}
		
		updateTravelLine();
		
		animation.update();
	}
	
	public abstract void render(Graphics g); // Draws the entity
	
	public abstract Shape getHitbox(); // Returns hitbox
	
	public abstract boolean isActive(); // Returns whether the entity is dying or not
	public abstract void die(); // Triggers death animation
	
	// Used for entity-entity collision
	public boolean intersects(Entity ent)
	{
		Area entArea = new Area(getHitbox());
		entArea.intersect(new Area(ent.getHitbox()));
		return !entArea.isEmpty();
	}
	
	// Used for entity-wall and entity-melee collision
	// Checks intersection of hitbox and line and if the entity passed through the wall completely
	public boolean intersects(Line2D.Double line)
	{
		boolean intersects = false;
		
		if(getHitbox() instanceof Rectangle2D.Double)
		{
			intersects = line.intersects((Rectangle2D.Double)getHitbox());
		}
		else if(getHitbox() instanceof Ellipse2D.Double)
		{
			intersects = line.ptSegDist(getX(), getY()) < getHeight() / 2;
		}

		return intersects || line.intersectsLine(travel);
	}
	
	// Used for entity-wall collision - Utilizes previous entity position
	public boolean isRightOfLine(Line2D.Double line)
	{
		double deltaY = line.getP2().getY() - line.getP1().getY();
		// Formula to calculate if a point is located on the right or left side of a line
		double value = (line.getP2().getX() - line.getP1().getX()) * (getPrevY() - line.getP1().getY()) - (getPrevX() - line.getP1().getX()) * (line.getP2().getY() - line.getP1().getY());
		return value * deltaY < 0;
	}
	
	// Used for entity-wall collision - Utilizes previous entity position
	public boolean isAboveLine(Line2D.Double line)
	{
		double deltaX = line.getP2().getX() - line.getP1().getX();
		// Formula to calculate if a point is located above the line
		double value = (line.getP2().getY() - line.getP1().getY()) * (getPrevX() - line.getP1().getX()) - (getPrevY() - line.getP1().getY()) * (line.getP2().getX() - line.getP1().getX());
		return value * deltaX > 0;
	}
	
	// Used for entity-projectile collision - TODO Not foolproof (Consider adding travel-line check)
	public boolean intersects(Projectile proj)
	{
		Area entArea = new Area(getHitbox());
		entArea.intersect(new Area(proj.getHitbox()));
		return !entArea.isEmpty();
	}
		
	public String getName()
	{
		return name;
	}
	
	public double getX()
	{
		return x;
	}

	public void setX(double x)
	{
		this.x = x;
		updateTravelLine();
	}

	public double getY()
	{
		return y;
	}

	public void setY(double y)
	{
		this.y = y;
		updateTravelLine();
	}
	
	public double getVelX()
	{
		return velX;
	}
	
	public void setVelX(double velX)
	{
		this.velX = velX;
		if(velX < -Stage.TERMINAL_VELOCITY)
		{
			velX = -Stage.TERMINAL_VELOCITY;
		}
		else if(velX > Stage.TERMINAL_VELOCITY)
		{
			velX = Stage.TERMINAL_VELOCITY;
		}
	}
	
	public double getVelY()
	{
		return velY;
	}
	
	public void setVelY(double velY)
	{
		this.velY = velY;
		if(velY < -Stage.TERMINAL_VELOCITY)
		{
			velY = -Stage.TERMINAL_VELOCITY;
		}
		else if(velY > Stage.TERMINAL_VELOCITY)
		{
			velY = Stage.TERMINAL_VELOCITY;
		}
	}
	
	public double getPrevX()
	{
		return prevX;
	}
	
	public double getPrevY()
	{
		return prevY;
	}
	
	public Line2D.Double getTravelLine()
	{
		return travel;
	}
	
	public void updateTravelLine()
	{
		travel = new Line2D.Double(prevX, prevY, x, y);
	}
	
	public double getWidth()
	{
		return width;
	}
	
	public double getHeight()
	{
		return height;
	}
	
	public double getMovability()
	{
		return movability;
	}
	
	public int getMaxHealth()
	{
		return maxHealth;
	}
	
	public int getHealth()
	{
		return health;
	}

	public void setHealth(int health)
	{
		this.health = health;
		if(this.health > maxHealth)
		{
			this.health = maxHealth;
		}
		else if(health <= 0)
		{
			this.health = 0;
			die();
		}
	}
	
	public boolean isFacingRight()
	{
		return facingRight;
	}
	
	public void setDirection(boolean right)
	{
		facingRight = right;
	}
	
	public boolean isGrounded()
	{
		return ground != null;
	}
	
	public void setGround(Land ground)
	{
		velY = 0;
		this.ground = ground;
	}
	
	public void removeGround()
	{
		ground = null;
	}
	
	public double getSlope()
	{
		if(ground != null)
		{
			return ground.getSlope();
		}
		return 0;
	}
	
	public boolean hasWeapon()
	{
		return weapon != null;
	}
	
	public Weapon getWeapon()
	{
		return weapon;
	}
	
	public void removeWeapon()
	{
		weapon = null;
	}
	
	public boolean canAttack()
	{
		return canAttack;
	}
	
	public void canAttack(boolean canAttack)
	{
		this.canAttack = canAttack;
	}
	
	public boolean canMove()
	{
		return canMove;
	}
	
	public void canMove(boolean canMove)
	{
		this.canMove = canMove;
	}
	
	public boolean isFriendly()
	{
		return friendly;
	}
	
	public boolean isFlying()
	{
		return flying;
	}
	
	public boolean isBlocking(boolean projDirection)
	{
		return blocking && facingRight != projDirection;
	}
	
	public boolean isDodging()
	{
		return dodging;
	}
	
	public boolean isMarked()
	{
		return marked;
	}
	
	public void mark()
	{
		marked = true;
	}
	
	public boolean isDead()
	{
		return dead;
	}
	
	public Stage getStage()
	{
		return stage;
	}
}