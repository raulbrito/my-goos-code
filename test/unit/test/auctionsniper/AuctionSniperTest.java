package test.auctionsniper;

import static org.hamcrest.Matchers.equalTo;

import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.States;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import auctionsniper.Auction;
import auctionsniper.AuctionSniper;
import auctionsniper.Item;
import auctionsniper.SniperListener;
import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;
import auctionsniper.AuctionEventListener.PriceSource;
import static auctionsniper.SniperState.WINNING;
import static auctionsniper.SniperState.BIDDING;
import static auctionsniper.SniperState.WON;
import static auctionsniper.SniperState.LOST;
import static auctionsniper.SniperState.LOSING;


@RunWith(JMock.class)
public class AuctionSniperTest {
	protected static final String ITEM_ID = "123";
	private static final Item ITEM = new Item(ITEM_ID, 1234);
	private final Mockery context = new Mockery();
	private final SniperListener sniperListener = context.mock(SniperListener.class);
	private final Auction auction = context.mock(Auction.class);
	private final AuctionSniper sniper = new AuctionSniper(ITEM, auction);
	private final States sniperState = context.states("sniper");
	
	@Before
	public void addListener() {
		sniper.addSniperListener(sniperListener);
	}
	
	@Test
	public void reportsLostIfAuctionClosesImmediately() {
		context.checking(new Expectations() {{
		      atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 0, 0, LOST));  
		}});
		
		sniper.auctionClosed();
	}
	
	@Test
	public void reportsLostIfAuctionClosesWhenBidding() {
		allowingSniperBidding();
		context.checking(new Expectations() {{
			ignoring(auction);
			atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID,  123, 168, LOST)); 
                 when(sniperState.is("bidding")); 
		}});
		
		sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
		sniper.auctionClosed();
	}
	
	private Matcher<SniperSnapshot> aSniperThatIs(SniperState state) {
		return new FeatureMatcher<SniperSnapshot, SniperState>(
				equalTo(state), "sniper that is", "wa2s") {
					@Override
					protected SniperState featureValueOf(SniperSnapshot actual) {
						return actual.sniperState;
					}
		};
	}


	
	@Test
	public void reportsWonIfAuctionClosesWhenWinning() {
		context.checking(new Expectations() {{
			ignoring(auction);
			allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(WINNING)));
				then(sniperState.is("winning"));
			atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 123, 0, WON));
				when(sniperState.is("winning"));
		}});
		
		sniper.currentPrice(123, 45, PriceSource.FromSniper);
		sniper.auctionClosed();
	}
	
	@Test
	public void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
		final int price = 1001;
		final int increment = 25;
		final int bid = price + increment;
		
		context.checking(new Expectations() {{
			one(auction).bid(bid);
			atLeast(1).of(sniperListener).sniperStateChanged(with(any(SniperSnapshot.class)));
		}});
		
		sniper.currentPrice(price, increment, PriceSource.FromOtherBidder);
	}
	
	@Test
	public void reportsIsWinningWhenCurrentPriceComesFromSniper() {
		allowingSniperBidding();
		context.checking(new Expectations() {{
			ignoring(auction);
			atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 135, 135, WINNING));
				when(sniperState.is("bidding"));
		}});
		
		sniper.currentPrice(123, 12, PriceSource.FromOtherBidder);
		sniper.currentPrice(135, 45, PriceSource.FromSniper);
	}
	
	@Test
	public void doesNotBidAndReportsLosingIfSubsequentPriceIsAboveStopPrice() {
		allowingSniperBidding();
		context.checking(new Expectations() {{
			int bid = 123 + 45;
			allowing(auction).bid(bid);
			atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 2345, bid, LOSING));
				when(sniperState.is("bidding"));
		}});
		sniper.currentPrice(123, 45, PriceSource.FromOtherBidder);
		sniper.currentPrice(2345, 25, PriceSource.FromOtherBidder);
		
	}

	private void allowingSniperBidding() {
		context.checking(new Expectations() {{
			allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(BIDDING)));
				then(sniperState.is("bidding"));
		}});
	}

}
