package indigo.Manager;

import java.awt.image.BufferedImage;

public class Animation
{
	private BufferedImage[] frames;
	private int currentFrame;
	private int numFrames;
	
	private int count;
	private int delay;
	private boolean reverse;
	
	private int timesPlayed;
	
	public Animation()
	{
		timesPlayed = 0;
	}
	
	public void setFrames(BufferedImage[] frames)
	{
		this.frames = frames;
		currentFrame = 0;
		count = 0;
		timesPlayed = 0;
		delay = 2;
		reverse = false;
		numFrames = frames.length;
	}
	
	public void setDelay(int i) { delay = i; }
	public void setFrame(int i) { currentFrame = i; }
	public void setReverse(boolean b) { reverse = b; }
	
	public void update()
	{
		if(delay == -1)
		{
			return;
		}
		
		// Timer
		count++;
		
		// Change frame every few updates
		if(count == delay)
		{
			count = 0;
			
			if(reverse)
			{
				currentFrame--;
				
				// Loop animation
				if(currentFrame == -1)
				{
					currentFrame = numFrames - 1;
					timesPlayed++;
				}
			}
			else
			{
				currentFrame++;
				
				// Loop animation
				if(currentFrame == numFrames)
				{
					currentFrame = 0;
					timesPlayed++;
				}
			}
		}
	}
	
	public int getFrame() { return currentFrame; }
	public int getCount() { return count; }
	public BufferedImage getImage() { return frames[currentFrame]; }
	public boolean hasPlayedOnce() { return timesPlayed > 0; }
	public boolean hasPlayed(int i) { return timesPlayed == i; }
	
}