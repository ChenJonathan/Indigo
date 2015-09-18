package indigo.Manager;

import java.awt.image.BufferedImage;

/**
 * Handles the playing of animations.
 */
public class Animation
{
	private BufferedImage[] frames;
	private int currentFrame;
	private int numFrames;
	
	private int count;
	private int delay;
	private boolean reverse;
	
	private int timesPlayed;

	/**
	 * Initializes the animation as never played.
	 */
	public Animation()
	{
		timesPlayed = 0;
	}

    /**
     * Sets the frames for the animation and prepares them for use.
     * @param frames The frames for the animation.
     */
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

    /**
     * @param i The time between frames.
     */
	public void setDelay(int i) { delay = i; }

    /**
     * @param i The current frame.
     */
	public void setFrame(int i) { currentFrame = i; }

    /**
     * @param b Whether to play the animation in reverse.
     */
	public void setReverse(boolean b) { reverse = b; }

    /**
     * Updates animation data for continued play.
     */
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

    /**
     * @return The current frame.
     */
	public int getFrame() { return currentFrame; }

    /**
     * @return The current count.
     */
	public int getCount() { return count; }

    /**
     * @return The current frame in the animation.
     */
	public BufferedImage getImage() { return frames[currentFrame]; }

    /**
     * @return Whether the animation has been played before.
     */
	public boolean hasPlayedOnce() { return timesPlayed > 0; }

    /**
     * Returns whether the animation has been played a certain number of times.
     * @param i The number of times.
     * @return Whether the animation has been played that number of times.
     */
	public boolean hasPlayed(int i) { return timesPlayed == i; }
	
}