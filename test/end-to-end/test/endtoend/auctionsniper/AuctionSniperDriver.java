package test.endtoend.auctionsniper;

import auctionsniper.Main;

import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.JFrameDriver;
import com.objogate.wl.swing.driver.JLabelDriver;
import com.objogate.wl.swing.gesture.GesturePerformer;

import static org.hamcrest.Matchers.*;

public class AuctionSniperDriver extends JFrameDriver {

	public AuctionSniperDriver(int timeout) {
		super(new GesturePerformer(), 
			  JFrameDriver.topLevelFrame(named(Main.MAIN_WINDOW_NAME), showingOnScreen()), 
			  new AWTEventQueueProber(timeout, 100));
	}

	public void showSniperStatus(String statusText) {
		new JLabelDriver(this,
				         named(Main.SNIPER_STATUS_NAME)).hasText(equalTo(statusText));
	}

	public void dispose() {
		// TODO Auto-generated method stub
		
	}

}
