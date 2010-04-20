package auctionsniper;

import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import auctionsniper.AuctionEventListener.PriceSource;

public class AuctionMessageTranslator implements MessageListener {

	private AuctionEventListener listener;
	private final String sniperId;

	public AuctionMessageTranslator(String sniperId, AuctionEventListener listener) {
		this.listener = listener;
		this.sniperId = sniperId;
	}

	@Override
	public void processMessage(Chat chat, Message message) {
		try {
			translate(message);
		} catch (Exception parseException) {
			listener.auctionFailed();
		}
	}

	private void translate(Message message) throws MissingValueException {
		AuctionEvent event = AuctionEvent.from(message.getBody());
		
		String eventType = event.type();
		
		if (eventType.equals("CLOSE")) {
			listener.auctionClosed();
		} else if (eventType.equals("PRICE")) {
			listener.currentPrice(event.currentPrice(), 
					              event.increment(),
					              event.isFrom(sniperId));
		}
	}
	
	public static class AuctionEvent {
		private final Map<String, String> fields = new HashMap<String, String>();
		
		public String type() throws MissingValueException {
			return get("Event");
		}
		
		public PriceSource isFrom(String sniperId) throws MissingValueException {
			 return sniperId.equals(bidder()) ? PriceSource.FromSniper : PriceSource.FromOtherBidder;
		}

		public int currentPrice() throws NumberFormatException, MissingValueException {
			return getInt("CurrentPrice");
		}
		
		public int increment() throws NumberFormatException, MissingValueException {
			return getInt("Increment");
		}
		
		private int getInt(String fieldName) throws NumberFormatException, MissingValueException {
			return Integer.parseInt(get(fieldName));
		}
		
		private String bidder() throws MissingValueException {
			return get("Bidder");
		}
		
		private String get(String fieldName) throws MissingValueException {
			String value = fields.get(fieldName);
			if (value == null) {
				throw new MissingValueException(value);
			}
			return value;
		}
		
		private void addField(String field) {
			String[] pair = field.split(":");
			fields.put(pair[0].trim(), pair[1].trim());
		}
		
		static AuctionEvent from(String messageBody) {
			AuctionEvent event = new AuctionEvent();
			for (String field: fieldsIn(messageBody)) {
				event.addField(field);
			}
			return event;
		}
		
		static String[] fieldsIn(String messageBody) {
			return messageBody.split(";");
		}
	}
	
	private static class MissingValueException extends Exception {
		private static final long serialVersionUID = 1L;

		public MissingValueException(String fieldName) {
			super("Missing value for " + fieldName);
		}
	}

}
