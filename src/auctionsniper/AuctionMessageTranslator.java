package auctionsniper;

import java.util.HashMap;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

public class AuctionMessageTranslator implements MessageListener {

	private AuctionEventListener listener;

	public AuctionMessageTranslator(AuctionEventListener listener) {
		this.listener = listener;
	}

	@Override
	public void processMessage(Chat chat, Message message) {
		HashMap<String, String> event = unpackFromMessage(message);
		String type = event.get("Event");
		
		if (type.equals("CLOSE")) {
			listener.auctionClosed();
		} else if (type.equals("PRICE")) {
			listener.currentPrice(Integer.parseInt(event.get("CurrentPrice")), 
					              Integer.parseInt(event.get("Increment")));
		}

	}

	private HashMap<String, String> unpackFromMessage(Message message) {
		HashMap<String, String> event = new HashMap<String, String>();
		
		for(String element: message.getBody().split(";")) {
			String [] pair = element.split(":");
			event.put(pair[0].trim(), pair[1].trim());
		}
		return event;
	}

}
