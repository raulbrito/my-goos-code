package auctionsniper;

import auctionsniper.util.Announcer;


public class AuctionSniper implements AuctionEventListener {
	  private final Announcer<SniperListener> listeners = Announcer.to(SniperListener.class);
	private final Auction auction;
	private final Item item;
	private SniperSnapshot snapshot;

	public AuctionSniper(Item item, Auction auction) {
		this.auction = auction;
		this.snapshot = SniperSnapshot.joining(item.identifier);
		this.item = item;
	}
	
	public void addSniperListener(SniperListener sniperListener) {
		listeners.addListener(sniperListener);
	}


	@Override
	public void auctionClosed() {
		snapshot = snapshot.closed();
		notifyChange();
	}

	private void notifyChange() {
		listeners.announce().sniperStateChanged(snapshot);
		
	}

	@Override
	public void currentPrice(int price, int increment, PriceSource priceSource) {
		switch(priceSource) {
		case FromSniper:
			snapshot = snapshot.winning(price);
			break;
		case FromOtherBidder:
			int bid = price + increment;
			if (item.allowsBid(bid)) {
				auction.bid(bid);
				snapshot = snapshot.bidding(price, bid);
			} else {
				snapshot = snapshot.losing(price);
			}
			break;
		}
		notifyChange();
	}
	
	public SniperSnapshot getSnapshot() {
		return snapshot;
	}

	@Override
	public void auctionFailed() {
		snapshot = snapshot.failed();
		notifyChange();
		
	}
		  

}
