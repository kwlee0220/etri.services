package etri.service.image;

import java.awt.image.BufferedImage;
import java.util.concurrent.locks.ReentrantLock;

import camus.service.PowerControlException;
import camus.service.geo.Rectangle;
import camus.service.geo.Size2d;
import camus.service.image.BeamProjector;
import camus.service.image.Color;
import camus.service.vision.Image;

import net.jcip.annotations.GuardedBy;
import utils.Initializable;


/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class SwingBeamProjector implements BeamProjector, Initializable {
	private final SwingScreen m_screen = new SwingScreen();
	private final SwingBasedImageView m_blackWall = new SwingBasedImageView();
	
	private final ReentrantLock m_lock = new ReentrantLock();
	@GuardedBy("m_lock") private boolean m_power;
	
	public SwingBeamProjector() { }
	
	public void setViewSize(Size2d size) {
		m_screen.setViewSize(size);
		m_blackWall.setViewSize(size);
	}
	
	public void setFrameDecoration(boolean flag) {
		m_screen.setFrameDecoration(flag);
		m_blackWall.setFrameDecoration(flag);
	}
	
	public void setMonitorIndex(int monitorIndex) {
		m_screen.setMonitorIndex(monitorIndex);
		m_blackWall.setMonitorIndex(monitorIndex);
	}
	
	@Override
	public void initialize() throws Exception {
		m_power = false;
		
		m_screen.setAlwaysOnTop(false);
		m_screen.initialize();
		m_screen.setVisible(false);
		
		m_blackWall.setAlwaysOnTop(false);
		m_blackWall.setFrameDecoration(false);
		m_blackWall.initialize();
		m_blackWall.drawRect(new Rectangle(m_screen.getScreenSize()), Color.BLACK, -1);
		m_blackWall.updateView();
		m_blackWall.setVisible(false);
	}
	
	@Override
	public void destroy() throws Exception {
		m_blackWall.destroy();
		m_screen.destroy();
	}

	@Override
	public Size2d getScreenSize() {
		return m_screen.getScreenSize();
	}
	
	public int getMonitorIndex() {
		return m_screen.getMonitorIndex();
	}
	
	public SwingScreen getImageView() {
		return m_screen;
	}

	@Override
	public void setVisible(boolean visible) {
		m_lock.lock();
		try {
			if ( visible ) {
				if ( m_power ) {
					m_screen.setVisible(true);
					m_blackWall.setVisible(false);
				}
				else {
					m_screen.setVisible(false);
					m_blackWall.setVisible(true);
				}
			}
			else {
				m_screen.setVisible(false);
				m_blackWall.setVisible(false);
			}
		}
		finally {
			m_lock.unlock();
		}
	}

	@Override
	public boolean getVisible() {
		m_lock.lock();
		try {
			return ( m_power ) ? m_screen.getVisible() : m_blackWall.getVisible();
		}
		finally {
			m_lock.unlock();
		}
	}

	@Override
	public boolean getPower() throws PowerControlException {
		m_lock.lock();
		try {
			return m_power;
		}
		finally {
			m_lock.unlock();
		}
	}

	@Override
	public boolean setPower(boolean power) throws PowerControlException {
		m_lock.lock();
		try {
			if ( m_power != power ) {
				if ( m_power = power ) {
					m_screen.updateView();
					m_screen.setVisible(true);
					m_blackWall.setVisible(false);
				}
				else {
					m_blackWall.setVisible(true);
				}
				
				return !power;
			}
			else {
				return power;
			}
		}
		finally {
			m_lock.unlock();
		}
	}

	@Override
	public void show(Image image) {
		show(image.toBufferedImage());
	}

	public void show(BufferedImage bimage) {
		m_lock.lock();
		try {
			m_screen.draw(bimage);
			if ( m_power ) {
				m_screen.updateView();
			}
		}
		finally {
			m_lock.unlock();
		}
	}
	
	@Override
	public void clear() {
		m_lock.lock();
		try {
			m_screen.clear();
			if ( m_power ) {
				m_screen.updateView();
			}
		}
		finally {
			m_lock.unlock();
		}
	}
	
	public static final int[] getMonitorIndices() {
		return SwingScreen.getMonitorIndices();
	}
}
