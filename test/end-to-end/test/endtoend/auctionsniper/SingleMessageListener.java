package test.endtoend.auctionsniper;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;


import org.hamcrest.Matcher;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

public class SingleMessageListener implements MessageListener {
	
	private final ArrayBlockingQueue<Message> messages = new ArrayBlockingQueue<Message>(1);

	@Override
	public void processMessage(Chat chat, Message message) {
		messages.add(message);
	}
	
	public void receivesAMessage(Matcher<? super String> matcher) throws InterruptedException {
		Message message = messages.poll(5, TimeUnit.SECONDS);
		assertThat("Message", message, is(notNullValue()));
		assertThat(message.getBody(), matcher);
	}

}
