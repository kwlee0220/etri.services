package etri.service.sound;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executor;

import javax.sound.sampled.UnsupportedAudioFileException;

import camus.service.PauseControl;
import camus.service.media.MediaPlayTimeControl;
import camus.service.media.MediaPlayerException;
import camus.service.media.UnsupportedMediaTypeException;
import camus.service.sound.MuteControl;
import camus.service.sound.PcmPlayerException;
import camus.service.sound.VolumeControl;

import async.OperationSchedulerProvider;
import async.support.ThreadedAsyncOperation2;
import utils.io.SoundPlay;


/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class JavaSoundPlay extends ThreadedAsyncOperation2<Void>
						implements PauseControl, MuteControl, VolumeControl, MediaPlayTimeControl {
	private final SoundPlay m_play;
	
	public JavaSoundPlay() {
		m_play = new SoundPlay();
	}
	
	public JavaSoundPlay(Executor executor) {
		super(executor);
		
		m_play = new SoundPlay();
	}
	
	public JavaSoundPlay(OperationSchedulerProvider scheduler) {
		super(scheduler);
		
		m_play = new SoundPlay();
	}

	@Override
	public Void executeOperation() throws Exception {
		m_play.play();
		
		return null;
	}

	@Override
	public void cancelOperation() {
		m_play.stop();
	}

	@Override
	public boolean isPaused() {
		return m_play.isPaused();
	}
	
	@Override
	public void pause() throws IllegalStateException {
		m_play.pause();
	}

	@Override
	public void resume() throws IllegalStateException {
		m_play.resume();
	}

	public boolean getMute() {
		return m_play.getMute();
	}

	public void setMute(boolean flag) {
		m_play.setMute(flag);
	}

	public int getVolumeLevel() {
		return Math.round(m_play.getVolume());
	}

	public void setVolumeLevel(int level) {
		m_play.setVolume(level/10.0f);
	}

	@Override
	public int getMaxVolumeLevel() {
		return 10;
	}

	@Override
	public int getMinVolumeLevel() {
		return -10;
	}

	@Override
	public long getPlayTimeInMillis() {
		return m_play.getCurrentTimeInMillis();
	}

	@Override
	public void setPlayTimeInMillis(long time) {
		m_play.setStartTimeInMillis(time);
	}

	public void setMediaUrl(String mediaUrl) throws MediaPlayerException,
			UnsupportedMediaTypeException, IllegalArgumentException {
		try {
			m_play.setPlayMedia(new URL(mediaUrl));
		}
		catch ( MalformedURLException e ) {
			throw new IllegalArgumentException("invalid URL=" + mediaUrl);
		}
		catch ( UnsupportedAudioFileException e ) {
			throw new UnsupportedMediaTypeException(e.getMessage() + ", URL=" + mediaUrl);
		}
		catch ( IllegalStateException e ) {
			throw e;
		}
		catch ( Exception e ) {
			throw new MediaPlayerException("" + e);
		}
	}

	public void setMediaInputStream(InputStream mediaStream) throws MediaPlayerException,
		UnsupportedMediaTypeException, IllegalArgumentException {
		try {
			m_play.setPlayMedia(mediaStream);
		}
		catch ( UnsupportedAudioFileException e ) {
			throw new UnsupportedMediaTypeException(e.getMessage());
		}
		catch ( IllegalStateException e ) {
			throw e;
		}
		catch ( Exception e ) {
			throw new MediaPlayerException("" + e);
		}
	}

	public void setPcmMediaInputStream(InputStream mediaStream, int sampleRate, int sampleSize)
		throws PcmPlayerException {
		try {
			m_play.setPcmPlayMedia(mediaStream, sampleRate, sampleSize);
		}
		catch ( IllegalStateException e ) {
			throw e;
		}
		catch ( Exception e ) {
			throw new PcmPlayerException("" + e);
		}
	}
}