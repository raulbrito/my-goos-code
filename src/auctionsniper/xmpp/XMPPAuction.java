package auctionsniper.xmpp;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import auctionsniper.Auction;
import auctionsniper.AuctionEventListener;
import auctionsniper.AuctionMessageTranslator;
import auctionsniper.Item;
import auctionsniper.util.Announcer;


public class XMPPAuction implements Auction {
	Announcer<AuctionEventListener> auctionEventListeners = Announcer.to(AuctionEventListener.class);

	private final Chat chat;

	private XMPPFailureReporter failureReporter;

	public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;";
	public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";


	
	public XMPPAuction(XMPPConnection connection, Item item, XMPPFailureReporter failureReporter) {
		this.failureReporter = failureReporter;
		AuctionMessageTranslator translator = translatorFor(connection);
		this.chat = connection.getChatManager().createChat(auctionId(item.identifier, connection), translator);
		addAuctionEventListener(chatDisconnectorFor(translator));
	}

	private AuctionEventListener chatDisconnectorFor(final AuctionMessageTranslator translator) { 
	    return new AuctionEventListener() { 
	      public void auctionFailed() { 
	        chat.removeMessageListener(translator); 
	      }
	      public void auctionClosed() { }
	      public void currentPrice(int price, int increment, PriceSource priceSource) { }
	    }; 
	  } 	

	private AuctionMessageTranslator translatorFor(XMPPConnection connection) {
		return new AuctionMessageTranslator(connection.getUser(), auctionEventListeners.announce(), failureReporter);
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
