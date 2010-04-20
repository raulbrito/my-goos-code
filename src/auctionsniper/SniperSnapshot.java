package auctionsniper;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class SniperSnapshot {
	public final String itemId;
	public final int lastPrice;
	public final int lastBid;
	public final SniperState sniperState;
	
	public SniperSnapshot(final String itemId, final int lastPrice, final int lastBid, SniperState sniperState) {
		this.itemId = itemId;
		this.lastPrice = lastPrice;
		this.lastBid = lastBid;
		this.sniperState = sniperState;
	}
	
    @Override
    public boolean equals(Object obj) {
    	return EqualsBuilder.reflectionEquals(this, obj);
    }
    
    @Override
    public int hashCode() {
    	return HashCodeBuilder.reflectionHashCode(this);
    }
    
    @Override
    public String toString() {
    	return ToStringBuilder.reflectionToString(this);
    }

	public static SniperSnapshot joining(String itemId) {
		return new SniperSnapshot(itemId, 0, 0, SniperState.JOINING);
	}

	public SniperSnapshot winning(int newLastPrice) {
		return new SniperSnapshot(itemId, newLastPrice, lastBid, SniperState.WINNING);
	}

	public SniperSnapshot losing(int newLastPrice) {
		return new SniperSnapshot(itemId, newLastPrice, lastBid, SniperState.LOSING);
	}

	public SniperSnapshot bidding(int newLastPrice, int newLastBid) {
		return new SniperSnapshot(itemId, newLastPrice, newLastBid, SniperState.BIDDING);
	}

	public SniperSnapshot closed() {
		return new SniperSnapshot(itemId, lastPrice, lastBid, sniperState.whenAuctionClosed());
	}
	
	public boolean isForSameItemAs(SniperSnapshot sniperSnapshot) {
		return itemId.equals(sniperSnapshot.itemId);
	}

	public SniperSnapshot failed() {
		return new SniperSnapshot(itemId, 0, 0, SniperState.FAILED);
	}


}
