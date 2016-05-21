package saga.progetto.tesi.media;

import static playn.core.PlayN.*;
import playn.core.Sound;

public class AudioManager 
{
	public static final String THEME_1 = "sound/music/act_raiser1";
	public static final String THEME_2 = "sound/music/act_raiser2";
	public static final String THEME_3 = "sound/music/act_raiser3";
	
	private Sound firstTheme;
	private Sound secondTheme;
	private Sound thirdTheme;
	private float musicVolume;
	private float effectVolume;
	
	public AudioManager()
	{
		firstTheme = assets().getMusic(THEME_1);
		secondTheme = assets().getMusic(THEME_2);
		thirdTheme = assets().getMusic(THEME_3);
		
		musicVolume = 0.1f;
		
		firstTheme.setVolume(musicVolume);
		secondTheme.setVolume(musicVolume);
		thirdTheme.setVolume(musicVolume);
		
		firstTheme.setLooping(true);
		secondTheme.setLooping(true);	
		thirdTheme.setLooping(true);	
		
		firstTheme.prepare();
		secondTheme.prepare();
		thirdTheme.prepare();
	}
	
	public float getMusicVolume()
	{
		return musicVolume;
	}
	
	public float getEffectVolume()
	{
		return effectVolume;
	}

	public void playFirstTheme(boolean play)
	{
		if (play && !firstTheme.isPlaying()) 
			firstTheme.play();
		
		else if (!play) 
			firstTheme.stop();
	}
	
	public void playSecondTheme(boolean play)
	{
		if (play && !secondTheme.isPlaying()) 
			secondTheme.play();
		
		else if (!play) 
			secondTheme.stop();
	}
	
	public void playThirdTheme(boolean play)
	{
		if (play && !thirdTheme.isPlaying()) 
			thirdTheme.play();
		
		else if (!play) 
			thirdTheme.stop();
	}

	public void increaseMusicVolume()
	{
		musicVolume += 0.1f;
	}
	
	public void decreaseMusicVolume()
	{		
		musicVolume -= 0.1f;
	}
	
	public void increaseEffectVolume()
	{
		effectVolume += 0.1f;
	}
	
	/**
	 * Decreases the effect volume and saves the new settings.
	 */
	public void decreaseEffectVolume()
	{
		effectVolume -= 0.1f;
	}
}
