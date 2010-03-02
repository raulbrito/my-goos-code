package test.endtoend.auctionsniper;

import auctionsniper.Main;
import auctionsniper.ui.MainWindow;

public class ApplicationRunner {

	protected static final String XMPP_HOSTNAME = "localhost";
	protected static final String SNIPER_PASSWORD = "sniper";
	protected static final String SNIPER_ID = "sniper";
	public static final String SNIPER_XMPP_ID = "sniper@raul-laptop/Auction";
	private AuctionSniperDriver driver;
	

	public void startBiddingIn(final FakeAuctionServer auction) {
		Thread thread = new Thread("Test Application") {
			@Override
			public void run() {
				try {
					Main.main(XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD, auction.getItemId());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		
		thread.setDaemon(true);
		thread.start();
		driver = new AuctionSniperDriver(1000);
		driver.showSniperStatus(MainWindow.STATUS_JOINING);
		
		
	}

	public void showSniperHasLostAuction() {
		driver.showSniperStatus(MainWindow.STATUS_LOST);
		
	}

	public void stop() {
		if (driver != null) {
			driver.dispose();
		}
		
	}

	public void hasShownSnipperIsBidding() {
		driver.showSniperStatus(MainWindow.STATUS_BIDDING);
		
	}

}
