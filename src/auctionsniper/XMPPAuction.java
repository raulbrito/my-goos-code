package auctionsniper;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import auctionsniper.util.Announcer;
import static auctionsniper.Main.AUCTION_ID_FORMAT;
import static auctionsniper.Main.BID_COMMAND_FORMAT;
import static auctionsniper.Main.JOIN_COMMAND_FORMAT;


public class XMPPAuction implements Auction {
	Announcer<AuctionEventListener> auctionEventListeners = Announcer.to(AuctionEventListener.class);

	private final Chat chat;
	
	public XMPPAuction(XMPPConnection connection, String itemId) {
		chat = connection.getChatManager().createChat(
					auctionId(itemId, connection), 
					new AuctionMessageTranslator(connection.getUser(),auctionEventListeners.announce()));
	}
	
	public void addAuctionEventListener(AuctionEventListener listener) {
		auctionEventListeners.addListener(listener);
	}
	
	private static String auctionId(String itemId, XMPPConnection connection) {
		return String.format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
	}

	public void bid(int amount) {
		sendMessage(String.format(BID_COMMAND_FORMAT, amount));
	}

	@Override
	public void join() {
		sendMessage(JOIN_COMMAND_FORMAT);
	}
	
	private void sendMessage(final String message) {
		try {
			chat.sendMessage(message);
		} catch (XMPPException e) {
			e.printStackTrace();
		}
	}
}
