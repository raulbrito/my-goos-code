/**
 * 
 */
package auctionsniper;

import java.util.ArrayList;
import java.util.List;

import auctionsniper.ui.SnipersTableModel;
import auctionsniper.xmpp.XMPPAuctionHouse;

public class SniperLauncher implements UserRequestListener {
	private final List<Auction> notToBeGCd = new ArrayList<Auction>();
	private final XMPPAuctionHouse auctionHouse;
	private SnipersTableModel snipers;

	public SniperLauncher(XMPPAuctionHouse auctionHouse, SnipersTableModel snipers) {
		this.auctionHouse = auctionHouse;
		this.snipers = snipers;
	}

	@Override
	public void joinAuction(String itemId) {
		snipers.addSniper(SniperSnapshot.joining(itemId));
		Auction auction = auctionHouse.auctionFor(itemId);
		notToBeGCd.add(auction);
		
		SwingThreadSniperListener sniperListener = new SwingThreadSniperListener(snipers);
		auction.addAuctionEventListener(new AuctionSniper(itemId, auction, sniperListener));
		
		auction.join();
	}
}