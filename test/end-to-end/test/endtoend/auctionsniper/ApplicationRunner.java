package test.endtoend.auctionsniper;

import java.io.IOException;

import auctionsniper.Main;
import auctionsniper.SniperState;
import auctionsniper.ui.MainWindow;

import static org.hamcrest.Matchers.containsString;

import static auctionsniper.ui.SnipersTableModel.textFor;





public class ApplicationRunner {

	protected static final String XMPP_HOSTNAME = "localhost";
	public static final String SNIPER_PASSWORD = "sniper";
	public static final String SNIPER_ID = "sniper";
	public static final String SNIPER_XMPP_ID = "sniper@localhost/Auction";
	private AuctionSniperDriver driver;
	private AuctionLogDriver logDriver = new AuctionLogDriver();

	public void startBiddingIn(final FakeAuctionServer... auctions) {
		startSniper(auctions);
		for(FakeAuctionServer auction: auctions) {
			String itemId = auction.getItemId();
			driver.startBiddingWithStopPrice(itemId, Integer.MAX_VALUE);
			driver.showSniperStatus(itemId, 0, 0, MainWindow.STATUS_JOINING);
		}
		
	}

	private void startSniper(final FakeAuctionServer... auctions) {
		logDriver.clearLog();
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
		driver.showSniperStatus(auction.getItemId(), lastPrice, lastBid, textFor(SniperState.LOST));
		
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

	public void startBiddingWithStopPrice(FakeAuctionServer auction, int stopPrice) {
		startSniper(auction);
		driver.startBiddingWithStopPrice(auction.getItemId(), stopPrice);
		
	}

	public void hasShownSniperIsLosing(FakeAuctionServer auction, int lastPrice, int lastBid) {
		driver.showSniperStatus(auction.getItemId(), lastPrice, lastBid, textFor(SniperState.LOSING));
		
	}

	public void showsSniperHasFailed(FakeAuctionServer auction) {
		driver.showSniperStatus(auction.getItemId(), 0, 0, textFor(SniperState.FAILED));
		
	}

	public void reportsInvalidMessage(FakeAuctionServer auction,
			String brokenMessage) throws IOException {
		logDriver.hasEntry(containsString(brokenMessage));
	}

}
