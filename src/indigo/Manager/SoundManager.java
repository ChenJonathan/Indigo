package indigo.Manager;

import java.io.File;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

/**
 * Handles the sound for the application.
 */
public class SoundManager{
	
	//List of currently playing sounds
	private static ArrayList<Clip> playingSounds = new ArrayList<>();
	
	static{
		//TODO Load long sound files here to reduce wait time in-game.
		//ContentManager.getSound(ContentManager.BACKGROUND_1);
		//ContentManager.getSound(ContentManager.BACKGROUND_2);
		//ContentManager.getSound(ContentManager.BACKGROUND_3);
		
		/**
		 * This thread acts like a monitor for the SoundManager, culling finished
		 * sounds from playingSounds so that we don't hog all the RAM.
		 */
		/*new Thread(new Runnable(){
			@Override
			public void run()
			{
				synchronized(playingSounds)
				{
					for(int i = 0; i < playingSounds.size(); i++)
					{
						if(!playingSounds.get(i).isRunning())
							playingSounds.get(i).stop();
							playingSounds.get(i).close();
							playingSounds.remove(i);
							break;
					}
				}
			}
		}).start();*/
	}
	
	/**
	 * Retrieves the requested sound from the ContentManager and plays
	 * the sound.
	 * 
	 * @param snd The SoundData of the sound to be played
	 */
	public static void play(ContentManager.SoundData snd)
	{
		try
		{
			//byte[] SoundBytes = ContentManager.getSound(snd);
			File AudioFile = new File(SoundManager.class.getResource(snd.path()).getPath());
			AudioInputStream ais = AudioSystem.getAudioInputStream(AudioFile);
			Clip AudioClip = AudioSystem.getClip();
			AudioClip.open(ais);
			ais.close();
			//Clip AudioClip = AudioSystem.getClip();
			//AudioClip.open(Format, SoundBytes, 0, SoundBytes.length);
			if(snd.doesLoop()){
				AudioClip.setLoopPoints(0, -1);
				AudioClip.loop(Clip.LOOP_CONTINUOUSLY);
			}
			//Handles the removing of AudioClips when finished playing
			AudioClip.addLineListener(new LineListener()
			{
				@Override
				public void update(LineEvent arg0)
				{
					if(arg0.getType() == LineEvent.Type.STOP)
						synchronized(playingSounds){
							for(int i = 0; i < playingSounds.size(); i++)
							{
								if(!playingSounds.get(i).isRunning())
								{
									playingSounds.get(i).stop();
									playingSounds.get(i).close();
									playingSounds.remove(i);
									break;
								}
							}
						}
				}
			});
			playingSounds.add(AudioClip);
			AudioClip.start();
		}catch(Exception Ex)
		{
			Ex.printStackTrace();
			System.out.println("Error playing sound.");
			System.exit(0);
		}
	}
	
	/**
	 * Pauses every currently playing sound
	 * 
	 * @param snd The SoundData of the sound to be stopped
	 */
	public static void stopAll()
	{
		synchronized(playingSounds)
		{
			for(Clip C: playingSounds)
			{
				C.stop();
			}
		}
	}
}