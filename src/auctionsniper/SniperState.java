package auctionsniper;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class SniperState {
	public final String itemId;
	public final int lastPrice;
	public final int lastBid;
	
	public SniperState(final String itemId, final int lastPrice, final int lastBid) {
		this.itemId = itemId;
		this.lastPrice = lastPrice;
		this.lastBid = lastBid;
	}
	
	public boolean equals(SniperState anotherSniperState) {
		return EqualsBuilder.reflectionEquals(this, anotherSniperState);
	}
	
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
	
	public String toString() {
		return ToStringBuilder.reflectionToString(this,
	            ToStringStyle.SHORT_PREFIX_STYLE);
		}

}
