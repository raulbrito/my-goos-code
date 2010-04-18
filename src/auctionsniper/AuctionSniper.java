package auctionsniper;

import java.util.ArrayList;
import java.util.List;


public class AuctionSniper implements AuctionEventListener {
	private final List<SniperListener> sniperListeners = new ArrayList<SniperListener>();
	private final Auction auction;
	private final Item item;
	private SniperSnapshot snapshot;

	public AuctionSniper(Item item, Auction auction) {
		this.auction = auction;
		this.snapshot = SniperSnapshot.joining(item.identifier);
		this.item = item;
	}
	
	public void addSniperListener(SniperListener sniperListener) {
		this.sniperListeners.add(sniperListener);
	}


	@Override
	public void auctionClosed() {
		snapshot = snapshot.closed();
		notifyChange();
	}

	private void notifyChange() {
		for (SniperListener listener : sniperListeners) {
			listener.sniperStateChanged(snapshot);
		}
			
		
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
		  

}
