package indigo.Skill;

import java.util.ArrayList;

import indigo.Landscape.Land;
import indigo.Landscape.Platform;
import indigo.Landscape.Wall;
import indigo.Manager.ContentManager;
import indigo.Manager.SoundManager;
import indigo.Phase.Phase;
import indigo.Stage.Stage;

public class Mist extends Skill
{
	double deltaX;
	double deltaY;

	public Mist(Phase phase, int position)
	{
		super(phase, position);
		id = Skill.MIST;
		icon = ContentManager.getImage(ContentManager.SKILL_MIST);
		castOnSelect = true;
	}

	public void update()
	{
		super.update();

		if(castTime == 0)
		{
			player.setMana(player.getMana() - 40);
			player.shift();

			double distance = Math.sqrt((Math.pow(stage.getMouseX() - player.getX(), 2) + Math.pow(stage.getMouseY()
					- player.getY(), 2)));
			deltaX = (stage.getMouseX() - player.getX()) / distance;
			deltaY = (stage.getMouseY() - player.getY()) / distance;
			
			SoundManager.play(ContentManager.TELEPORT_EFFECT);
		}
		else if(player.shifted())
		{
			for(int count = 0; count < 120; count++)
			{
				player.updateTravelLine();
				player.setX(player.getX() + deltaX * 5);
				player.setY(player.getY() + deltaY * 5);
				
				// Entity-land: Colliding with and landing on land
				ArrayList<Land> intersectedLand = new ArrayList<Land>();
				for(Wall wall : stage.getWalls())
				{
					if(stage.inProximity(player, wall) && player.intersects(wall))
					{
						intersectedLand.add(wall);
					}
				}
				for(Platform plat : stage.getPlatforms())
				{
					if(stage.inProximity(player, plat) && stage.intersectsFeet(player, plat))
					{
						intersectedLand.add(plat);
					}
				}
				if(intersectedLand.size() > 0)
				{
					stage.sortLandByDistance(player, intersectedLand);

					for(Land land : intersectedLand)
					{
						if(land instanceof Platform)
						{
							while(stage.intersectsFeet(player, land))
							{
								player.setY(player.getY() - Stage.PUSH_AMOUNT);
								player.setVelY(Math.min(player.getVelY(), 0));
							}
							continue;
						}
						if(((Wall)land).killsEntities() && player.isActive())
						{
							player.die();
							stage.trackDeath(((Wall)land).getName(), player);
							endCast();
							return;
						}
						if(((Wall)land).blocksEntities())
						{
							if(!land.isHorizontal())
							{
								if(stage.rightOfLand(player, land))
								{
									while(player.intersects((Wall)land))
									{
										player.setX(player.getX() + Stage.PUSH_AMOUNT);
										player.setVelX(Math.max(player.getVelX(), 0));
									}
								}
								// Rightward collision into wall
								else
								{
									while(player.intersects((Wall)land))
									{
										player.setX(player.getX() - Stage.PUSH_AMOUNT);
										player.setVelX(Math.min(player.getVelX(), 0));
									}
								}
							}
							else
							{
								// Downward collision into wall
								if(stage.aboveLand(player, land))
								{
									while(player.intersects((Wall)land))
									{
										player.setY(player.getY() - Stage.PUSH_AMOUNT);
										player.setVelY(Math.min(player.getVelY(), 0));
									}
								}
								// Upward collision into wall
								else if(!player.isGrounded())
								{
									while(player.intersects((Wall)land))
									{
										player.setY(player.getY() + Stage.PUSH_AMOUNT);
										player.setVelY(Math.max(player.getVelY(), 0));
									}
								}
							}
						}
					}
				}
			}

			endCast();
		}
	}

	public boolean canCast()
	{
		return player.getMana() >= 40 && player.canAttack() && player.canMove() && player.canTurn();
	}

	public void endCast()
	{
		phase.setAttackTimer(10);
		super.endCast();
	}
}