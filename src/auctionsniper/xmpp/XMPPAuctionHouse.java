package auctionsniper.xmpp;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import auctionsniper.Auction;
import auctionsniper.AuctionHouse;
import auctionsniper.Item;



public class XMPPAuctionHouse implements AuctionHouse {

	private XMPPConnection connection;
	public static final String ITEM_ID_AS_LOGIN = "auction-%s";
	public static final String AUCTION_RESOURCE = "Auction";
	public static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;

	@Override
	public Auction auctionFor(Item item) {
		return new XMPPAuction(connection, item);
	}
	
	public XMPPAuctionHouse(XMPPConnection connection) {
		this.connection = connection;
	}

	public static XMPPAuctionHouse connect(String hostname, String username,
			String password) throws XMPPException {
		XMPPConnection connection = new XMPPConnection(hostname);
		XMPPAuctionHouse auctionHouse = new XMPPAuctionHouse(connection);
		connection.connect();
		connection.login(username, password, XMPPAuctionHouse.AUCTION_RESOURCE);
		return auctionHouse;
	}

	public void disconnect() {
		connection.disconnect();
		
	}
 
}
