package test.endtoend.auctionsniper;

import org.jivesoftware.smack.XMPPException;
import org.junit.After;
import org.junit.Test;

public class AuctionSniperEndToEndTest {

	private final FakeAuctionServer auction = new FakeAuctionServer("item-54321");
	private final ApplicationRunner application = new ApplicationRunner();

	@After
	public void stopAuction() {
		auction.stop();
	}
	
	@After
	public void stopApplication() {
		application.stop();
	}
	
	@Test
	public void sniperJoinsAuctionUntilAuctionCloses() throws XMPPException, InterruptedException {
		auction.startSellingItem();
		application.startBiddingIn(auction);
		auction.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
		auction.announceClosed();
		application.showSniperHasLostAuction();
	}
	
	@Test
	public void sniperMakesAHigherBidButLoses() throws Exception {
		auction.startSellingItem();
		application.startBiddingIn(auction);
		auction.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
		auction.reportPrice(100, 98, "other bidder");
		application.hasShownSnipperIsBidding();
		auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);
		auction.announceClosed();
		application.showSniperHasLostAuction();
	}
	
}
