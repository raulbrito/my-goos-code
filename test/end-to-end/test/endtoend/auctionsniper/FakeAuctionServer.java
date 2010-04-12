package test.endtoend.auctionsniper;

import org.hamcrest.Matcher;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import auctionsniper.xmpp.XMPPAuction;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;



public class FakeAuctionServer {
	public static final String XMPP_HOSTNAME = "localhost";
	private static final String AUCTION_RESOURCE = "Auction";
	private static final String AUCTION_PASSWORD = "auction";
	private static final String ITEM_ID_AS_LOGIN = "auction-%s";
	private String itemId;
	private final XMPPConnection connection;
	private Chat currentChat;
	
	private final SingleMessageListener messageListener = new SingleMessageListener();

	public FakeAuctionServer(String itemId) {
		this.itemId = itemId;
		this.connection = new XMPPConnection(XMPP_HOSTNAME);
	}

	public void startSellingItem() throws XMPPException {
		connection.connect();
		connection.login(String.format(ITEM_ID_AS_LOGIN, itemId), AUCTION_PASSWORD, AUCTION_RESOURCE);
		connection.getChatManager().addChatListener( new ChatManagerListener() {
			

			@Override
			public void chatCreated(Chat chat, boolean createdLocally) {
				currentChat = chat;
				chat.addMessageListener(messageListener);
				
			}
		});
		
	}

	public void hasReceivedJoinRequestFromSniper(String sniperId) throws InterruptedException {
		Matcher<? super String> messageMatcher = equalTo(XMPPAuction.JOIN_COMMAND_FORMAT);
		receivesAMessageMatching(sniperId, messageMatcher);
	}


	public void announceClosed() throws XMPPException {
		currentChat.sendMessage("SOLVersion: 1.1; Event: CLOSE;");
	}

	public void stop() {
		connection.disconnect();
	}

	public String getItemId() {
		return this.itemId;
	}

	public void reportPrice(int price, int increment, String bidder) throws XMPPException {
		currentChat.sendMessage(String.format("SOL version: 1.1; Event: PRICE; CurrentPrice: %d; Increment: %d; Bidder: %s;", 
				price, increment, bidder));
	}

	public void hasReceivedBid(int bid, String sniperId) throws InterruptedException {
		Matcher<? super String> messageMatcher = equalTo(String.format(XMPPAuction.BID_COMMAND_FORMAT, bid));
		receivesAMessageMatching(sniperId, messageMatcher);
		
	}

	private void receivesAMessageMatching(String sniperId,
			Matcher<? super String> messageMatcher) throws InterruptedException {
		messageListener.receivesAMessage(messageMatcher);
		assertThat(currentChat.getParticipant(), equalTo(sniperId));
	}

}
