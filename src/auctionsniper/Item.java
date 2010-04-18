package auctionsniper;

public class Item {
	public final String identifier;
	public final int stopPrice;
	
	public Item(String identifier, int stopPrice) {
		this.identifier = identifier;
		this.stopPrice = stopPrice;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((identifier == null) ? 0 : identifier.hashCode());
		result = prime * result + stopPrice;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Item other = (Item) obj;
		if (identifier == null) {
			if (other.identifier != null)
				return false;
		} else if (!identifier.equals(other.identifier))
			return false;
		if (stopPrice != other.stopPrice)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Item [identifier=" + identifier + ", stopPrice=" + stopPrice
				+ "]";
	}

	public boolean allowsBid(int bid) {
		return bid <= stopPrice;
	}
	
	

}
