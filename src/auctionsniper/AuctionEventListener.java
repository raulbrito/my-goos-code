package auctionsniper;

public interface AuctionEventListener {

	void auctionClosed();

	void currentPrice(int i, int j);

}
