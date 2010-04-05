package test.endtoend.auctionsniper;

import auctionsniper.Main;
import auctionsniper.ui.MainWindow;

public class ApplicationRunner {

	protected static final String XMPP_HOSTNAME = "localhost";
	protected static final String SNIPER_PASSWORD = "sniper";
	protected static final String SNIPER_ID = "sniper";
	public static final String SNIPER_XMPP_ID = "sniper@localhost/Auction";
	private AuctionSniperDriver driver;
	private String itemId;

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
		driver.showSniperStatus(itemId, 0, 0, MainWindow.STATUS_JOINING);
		itemId = auction.getItemId();
		
	}

	public void showSniperHasLostAuction() {
		driver.showSniperStatus(itemId, 0, 0, MainWindow.STATUS_LOST);
		
	}

	public void stop() {
		if (driver != null) {
			driver.dispose();
		}
		
	}

	public void hasShownSnipperIsBidding(int lastPrice, int lastBid) {
		driver.showSniperStatus(itemId, lastPrice, lastBid, MainWindow.STATUS_BIDDING);
		
	}

	public void hasShownSnipperIsWinning(int winningBid) {
		driver.showSniperStatus(itemId, winningBid, winningBid, MainWindow.STATUS_WINNING);
		
	}

	public void showSniperHasWonAuction(int lastPrice) {
		driver.showSniperStatus(itemId, lastPrice, lastPrice, MainWindow.STATUS_WON);
		
	}

}
