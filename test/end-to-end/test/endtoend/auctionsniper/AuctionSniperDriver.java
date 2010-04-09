package test.endtoend.auctionsniper;

import static org.hamcrest.Matchers.equalTo;

import javax.swing.table.JTableHeader;

import auctionsniper.Main;

import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.JFrameDriver;
import com.objogate.wl.swing.driver.JTableDriver;
import com.objogate.wl.swing.driver.JTableHeaderDriver;
import com.objogate.wl.swing.gesture.GesturePerformer;
import static com.objogate.wl.swing.matcher.JLabelTextMatcher.withLabelText;
import static com.objogate.wl.swing.matcher.IterableComponentsMatcher.matching;

@SuppressWarnings("unchecked")
public class AuctionSniperDriver extends JFrameDriver {

	@SuppressWarnings("unchecked")
	public AuctionSniperDriver(int timeout) {
		super(new GesturePerformer(), 
			  JFrameDriver.topLevelFrame(named(Main.MAIN_WINDOW_NAME), showingOnScreen()), 
			  new AWTEventQueueProber(timeout, 100));
	}

	@SuppressWarnings("unchecked")
	public void showSniperStatus(String itemId, int lastPrice, int lastBid, String statusText) {
		JTableDriver table = new JTableDriver(this);
		table.hasRow(
				matching(withLabelText(equalTo(itemId)), withLabelText(String.valueOf(lastPrice)),
						withLabelText(String.valueOf(lastBid)), withLabelText(String.valueOf(statusText))));
	}

	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	public void hasColumnTitles() {
		JTableHeaderDriver headers = new JTableHeaderDriver(this, JTableHeader.class);
		headers.hasHeaders(matching(withLabelText("Item"), withLabelText("Last Price"),
							withLabelText("Last Bid"), withLabelText("State")));
		
	}

}
