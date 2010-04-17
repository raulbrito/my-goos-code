package test.endtoend.auctionsniper;

import org.jivesoftware.smack.XMPPException;
import org.junit.After;
import org.junit.Test;

public class AuctionSniperEndToEndTest {

	private FakeAuctionServer auction = new FakeAuctionServer("item-54321");
	private FakeAuctionServer auction2 = new FakeAuctionServer("item-65432");
	private ApplicationRunner application = new ApplicationRunner();

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
		application.showSniperHasLostAuction(auction, 0, 0);
	}
	
	@Test
	public void sniperMakesAHigherBidButLoses() throws Exception {
		
		auction.startSellingItem();
		application.startBiddingIn(auction);
		auction.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
		auction.reportPrice(1000, 98, "other bidder");
		application.hasShownSnipperIsBidding(auction, 1000, 1098);
		auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);
		auction.announceClosed();
		application.showSniperHasLostAuction(auction, 1000, 1098);
	}
	
	@Test
	public void sniperWinsAuctionByBiddingHigher() throws Exception {
		
		auction.startSellingItem();
		application.startBiddingIn(auction);
		auction.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
		auction.reportPrice(1000, 98, "other bidder");
		application.hasShownSnipperIsBidding(auction, 1000, 1098);
		auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

		auction.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID);
		application.hasShownSnipperIsWinning(auction, 1098);
		
		auction.announceClosed();
		application.showSniperHasWonAuction(auction, 1098);
		
	}
	
	@Test
	public void sniperBidsForMultipleItems() throws Exception {
		auction.startSellingItem();
		auction2.startSellingItem();
		
		application.startBiddingIn(auction, auction2);
		
		auction.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
		auction2.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
		
		auction.reportPrice(1000, 98, "other bidder");
		auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);

		auction2.reportPrice(500, 21, "other bidder");
		auction2.hasReceivedBid(521, ApplicationRunner.SNIPER_XMPP_ID);
		
		auction.reportPrice(1098, 97, ApplicationRunner.SNIPER_XMPP_ID);
		auction2.reportPrice(521, 22, ApplicationRunner.SNIPER_XMPP_ID);
		
		application.hasShownSnipperIsWinning(auction, 1098);
		application.hasShownSnipperIsWinning(auction2, 521);
		
		auction.announceClosed();
		auction2.announceClosed();
		
		application.showSniperHasWonAuction(auction,1098);
		application.showSniperHasWonAuction(auction2, 521);

	}
	
	@Test
	public void sniperLosesAnAuctionWhenThePriceIsTooHigh() throws Exception {
		auction.startSellingItem();
		application.startBiddingWithStopPrice(auction, 1100);
		auction.hasReceivedJoinRequestFromSniper(ApplicationRunner.SNIPER_XMPP_ID);
		auction.reportPrice(1000, 98, "other bidder");
		application.hasShownSnipperIsBidding(auction, 1000, 1098);
		
		auction.hasReceivedBid(1098, ApplicationRunner.SNIPER_XMPP_ID);
		
		auction.reportPrice(1197, 10, "third party");
		application.hasShownSniperIsLosing(auction, 1207, 1098);
		
		auction.reportPrice(1207, 10, "fourth party");
		application.hasShownSniperIsLosing(auction, 1207, 1098);
		
		auction.announceClosed();
		application.showSniperHasLostAuction(auction, 1207, 1098);
	}
	
}
