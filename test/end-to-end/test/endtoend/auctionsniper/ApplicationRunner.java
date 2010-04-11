package test.endtoend.auctionsniper;

import auctionsniper.Main;
import auctionsniper.SniperState;
import auctionsniper.ui.MainWindow;

import static auctionsniper.ui.SnipersTableModel.textFor;



public class ApplicationRunner {

	protected static final String XMPP_HOSTNAME = "localhost";
	protected static final String SNIPER_PASSWORD = "sniper";
	protected static final String SNIPER_ID = "sniper";
	public static final String SNIPER_XMPP_ID = "sniper@localhost/Auction";
	private AuctionSniperDriver driver;
	private String itemId;

	public void startBiddingIn(final FakeAuctionServer... auctions) {
		Thread thread = new Thread("Test Application") {
			@Override
			public void run() {
				try {
					Main.main(arguments(auctions));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		};
		
		
		thread.setDaemon(true);
		thread.start();
		driver = new AuctionSniperDriver(1000);
		driver.hasTitle(MainWindow.APPLICATION_TITLE);
		driver.hasColumnTitles();
		for(FakeAuctionServer auction: auctions) {
			driver.showSniperStatus(auction.getItemId(), 0, 0, MainWindow.STATUS_JOINING);
		}
		
	}

	protected static String[] arguments(FakeAuctionServer[] auctions) {
		String [] arguments = new String[auctions.length + 3];
		arguments[0] = XMPP_HOSTNAME;
		arguments[1] = SNIPER_ID;
		arguments[2] = SNIPER_PASSWORD;
		for(int i = 0; i < auctions.length; i++) {
			arguments[i + 3] = auctions[i].getItemId();
		}
		
		return arguments;
	}

	public void showSniperHasLostAuction(FakeAuctionServer auction, int lastPrice, int lastBid) {
		driver.showSniperStatus(auction.getItemId(), lastPrice, lastBid, MainWindow.STATUS_LOST);
		
	}

	public void stop() {
		if (driver != null) {
			driver.dispose();
		}
		
	}

	public void hasShownSnipperIsBidding(FakeAuctionServer auction, int lastPrice, int lastBid) {
		driver.showSniperStatus(auction.getItemId(), lastPrice, lastBid, textFor(SniperState.BIDDING));
		
	}

	public void hasShownSnipperIsWinning(FakeAuctionServer auction, int winningBid) {
		driver.showSniperStatus(auction.getItemId(), winningBid, winningBid, textFor(SniperState.WINNING));
		
	}

	public void showSniperHasWonAuction(FakeAuctionServer auction, int lastPrice) {
		driver.showSniperStatus(auction.getItemId(), lastPrice, lastPrice, textFor(SniperState.WON));
		
	}

}
