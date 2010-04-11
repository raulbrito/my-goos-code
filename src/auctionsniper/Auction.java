package auctionsniper;

public interface Auction {

	public void bid(int amount);
	
	public void join();
	
	public void addAuctionEventListener(AuctionEventListener listener);
}
