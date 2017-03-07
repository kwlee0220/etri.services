package etri.service.image;

import planet.Servant;

import camus.service.geo.Size2d;
import camus.service.image.ImageView;
import camus.service.image.Screen;
import camus.service.vision.Image;

import utils.swing.SwingUtils;


/**
 * 
 * @author Kang-Woo Lee (ETRI)
 */
public class SwingScreen extends SwingBasedImageView implements Screen, Servant {
	public SwingScreen() {
		setTitle("screen");
		setAlwaysOnTop(true);
		setFrameDecoration(false);
	}
	
	public void setMonitorIndex(int monitorIndex) {
		super.setMonitorIndex(monitorIndex);
	}

	@Override
	public void initialize() throws Exception {
		if ( getViewSize() == null ) {
			super.setViewSize(new Size2d(SwingUtils.getScreenRectangle(getMonitorIndex())));
		}
		super.initialize();
	}

	@Override
	public Size2d getScreenSize() {
		return getViewSize();
	}

	@Override
	public void show(Image image) {
		drawImage(image);
		updateView();
	}

	@Override
	public void clear() {
		super.clear();
		super.updateView();
	}
	
	public static final int[] getMonitorIndices() {
		int[] indices = new int[SwingUtils.getScreens().length];
		for ( int i =0; i < indices.length; ++i ) {
			indices[i] = i;
		}
		
		return indices;
	}

	private static final Class<?>[] REMOTE_INTERFACES = new Class[] { ImageView.class, };
	@Override
	public Class<?>[] getRemoteInterfaces() {
		return REMOTE_INTERFACES;
	}
	
	@Override
	public final String toString() {
		return getClass().getSimpleName() + "[monitor=" + getMonitorIndex()
				+ ", size=" + getScreenSize() + "]";
	}
}
