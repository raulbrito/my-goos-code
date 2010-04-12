package auctionsniper.xmpp;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import auctionsniper.Auction;
import auctionsniper.AuctionEventListener;
import auctionsniper.AuctionMessageTranslator;
import auctionsniper.util.Announcer;


public class XMPPAuction implements Auction {
	Announcer<AuctionEventListener> auctionEventListeners = Announcer.to(AuctionEventListener.class);

	private final Chat chat;

	public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;";
	public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";
	
	public XMPPAuction(XMPPConnection connection, String itemId) {
		chat = connection.getChatManager().createChat(
					auctionId(itemId, connection), 
					new AuctionMessageTranslator(connection.getUser(),auctionEventListeners.announce()));
	}
	
	public void addAuctionEventListener(AuctionEventListener listener) {
		auctionEventListeners.addListener(listener);
	}
	
	private static String auctionId(String itemId, XMPPConnection connection) {
		return String.format(XMPPAuctionHouse.AUCTION_ID_FORMAT, itemId, connection.getServiceName());
	}

	public void bid(int amount) {
		sendMessage(String.format(XMPPAuction.BID_COMMAND_FORMAT, amount));
	}

	@Override
	public void join() {
		sendMessage(XMPPAuction.JOIN_COMMAND_FORMAT);
	}
	
	private void sendMessage(final String message) {
		try {
			chat.sendMessage(message);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}
}
