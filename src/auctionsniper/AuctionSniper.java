package auctionsniper;

import java.util.ArrayList;
import java.util.List;

public class AuctionSniper implements AuctionEventListener {

	private final List<SniperListener> sniperListeners = new ArrayList<SniperListener>();
	private final Auction auction;
	
	private SniperSnapshot snapshot;

	public AuctionSniper(String itemId, Auction auction) {
		this.auction = auction;
		this.snapshot = SniperSnapshot.joining(itemId);
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
			auction.bid(bid);
			snapshot = snapshot.bidding(price, bid);
			break;
		}
		notifyChange();
	}
	
	public SniperSnapshot getSnapshot() {
		return snapshot;
	}
		  

}
