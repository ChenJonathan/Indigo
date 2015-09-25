package indigo.Projectile;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

import indigo.Entity.Entity;
import indigo.Landscape.Wall;
import indigo.Manager.Content;

public class PulseShot extends Projectile
{
	private int timer;
	
	private final int DEFAULT = 0;
	private final int SPARK = 1; //TODO: Remove, only for electric ball test image
	
	public static final int DAMAGE = 50; // Will scale by distance
	public final static int WIDTH = 2000;
	public final static int HEIGHT = 2000;
	public final static double PUSHBACK = 100; //TODO: Change WIDTH, HEIGHT, and PUSHBACK to suit the Pulse Shot, keeping hitbox size in mind
	public final static int DURATION = 5;
	
	public PulseShot(Entity entity, double x, double y, double velX, double velY, int dmg)
	{
		super(entity, x, y, velX, velY, dmg);
		width = WIDTH;
		height = HEIGHT;
		solid = false;
		flying = true;
		
		timer = DURATION;
		
		setAnimation(DEFAULT, Content.ELECTRIC_BALL, -1); //TODO: Change to be that of the pulse shot
	}
	
	public void update()
	{
		super.update();
		timer--;
		
		if(timer == 0)
		{
			dead = true;
		}
		
		//kill projectiles - projectile.die()
		for(int count = 0; count < stage.getProjectiles().size(); count++)
		{
			if(intersects(stage.getProjectiles().get(count)))
			{
				stage.getProjectiles().get(count).die();
			}
		}
	}
	
	public boolean intersects(Projectile proj)
	{
		Area entArea = new Area(getHitbox());
		entArea.intersect(new Area(proj.getHitbox()));
		return !entArea.isEmpty();
	}
	
	public void render(Graphics2D g) 
	{
		//TODO: If necessary, change to be that of the pulse shot
		//g.drawImage(animation.getImage(), (int) getX() - WIDTH / 2, (int) getY() - HEIGHT / 2, WIDTH, HEIGHT, null);
	}
	
	public Shape getHitbox()
	{
		//TODO: If necessary, change to be that of the pulse shot
		return new Ellipse2D.Double(getX() - getWidth() / 2, getY() - getHeight() / 2, getWidth(), getHeight());
	}
	
	public void collide(Entity ent) //pushes enemies
	{
		if(!stage.getPlayer().isGrounded() || ent.getY() < getY() + stage.getPlayer().getHeight() / 2)
		{
			setAnimation(SPARK, Content.ELECTRIC_SPARK, 1); //TODO: Change to pulse animations
			
			ent.removeGround();
			//Push enemy away, further when closer
			double scale = Math.sqrt(Math.pow(ent.getY() - getY(), 2)
					+ Math.pow(ent.getX() - getX(), 2));
			double iDP = 1-(scale/WIDTH); //Inverse distance percentage; TODO: Change WIDTH to Radius here when animation is drawn
			double velX = PUSHBACK * iDP * (stage.getMouseX() - getX())
					/ scale;
			double velY = PUSHBACK * iDP * (stage.getMouseY() - getY())
					/ scale;
			
			if(scale < WIDTH*0.02 || scale < HEIGHT*0.02) // TODO: Change for when get circular hitbox.
			{
				//Directly apply knockback to avoid divide by zero error
				velX = PUSHBACK * (stage.getMouseX() - getX())
						/ scale;
				velY = PUSHBACK * (stage.getMouseY() - getY())
						/ scale;
			}
			
			ent.setVelX(velX); //velocity is set rather than added on
			ent.setVelY(velY);
			
			if(!ent.isDodging()) // damaged, won't hurt if is dodging, scale by distance
			{ 
				ent.setHealth((int)(ent.getHealth()-(DAMAGE*iDP)));
			}
			
			ent.mark();
			// Check for divide by zero when scaling and pushing away
		}
	}
	
	//Not used
	public void collide(Wall wall) { }
	
	//Not used
	public boolean isActive()
	{
		return true;
	}
	
	//Not used
	public void die() { }
}
