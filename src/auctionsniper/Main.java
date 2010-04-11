package auctionsniper;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.LocatorEx.Snapshot;

import auctionsniper.ui.MainWindow;
import auctionsniper.ui.SnipersTableModel;


public class Main {

	public static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
	public static final String SNIPER_STATUS_NAME = "sniper status";
	
	private static final int ARG_HOSTNAME = 0;
	private static final int ARG_USERNAME = 1;
	private static final int ARG_PASSWORD = 2;
	private static final int ARG_ITEM_ID = 3;
	
	public static final String AUCTION_RESOURCE = "Auction";
	public static final String ITEM_ID_AS_LOGIN = "auction-%s";
	public static final String AUCTION_ID_FORMAT = ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;
	public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 1.1; Command: JOIN;";
	public static final String BID_COMMAND_FORMAT = "SOLVersion: 1.1; Command: BID; Price: %d;";
	
	private final SnipersTableModel snipers = new SnipersTableModel();
	
	private MainWindow ui;
	
	@SuppressWarnings("unused")
	private List<Chat> notToBeGCd = new ArrayList<Chat>();
	
	public Main() throws Exception {
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				ui = new MainWindow(snipers);
			}
			
		});
	}

	public static void main(String... args) throws Exception {
		Main main = new Main();
		XMPPConnection connection = connectTo(args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]);
		main.disconnectWhenUICloses(connection);
		for (int i = 0; i < args.length; i++) {
			main.joinAuction(connection, args[i]);
		}
	}
	
	


	private void joinAuction(XMPPConnection connection, String itemId) throws Exception {
		safelyAddItemToModel(itemId);
		final Chat chat = connection.getChatManager().createChat(auctionId(itemId, connection), null);
		this.notToBeGCd.add(chat);

		Auction auction = new XMPPAuction(chat);
		SwingThreadSniperListener sniperListener = new SwingThreadSniperListener();
		AuctionSniper listener = new AuctionSniper(itemId, auction, sniperListener);
		chat.addMessageListener(new AuctionMessageTranslator(connection.getUser(), listener));
		sniperListener.sniperStateChanged(SniperSnapshot.joining(itemId));
		auction.join();
		
	}
				

	private void safelyAddItemToModel(final String itemId) throws Exception {
		SwingUtilities.invokeAndWait(new Runnable() {
			public void run() {
				snipers.addSniper(SniperSnapshot.joining(itemId));
			}
		});
	}

	private void disconnectWhenUICloses(final XMPPConnection connection) {
		ui.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosed(WindowEvent e) {
				connection.disconnect();
				
			}

		});
	}

	private static String auctionId(String itemId, XMPPConnection connection) {
		return String.format(AUCTION_ID_FORMAT, itemId, connection.getServiceName());
	}


	private static XMPPConnection connectTo(String hostname, String username,
			String password) throws XMPPException {
		XMPPConnection connection = new XMPPConnection(hostname);
		connection.connect();
		connection.login(username, password, AUCTION_RESOURCE);
		return connection;
	}

	public class XMPPAuction implements Auction {
		private final Chat chat;
		
		public XMPPAuction(Chat chat) {
			this.chat = chat;
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
	
	public class SwingThreadSniperListener implements SniperListener {

		@Override
		public void sniperStateChanged(final SniperSnapshot snapshot) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					ui.sniperStatusChanged(snapshot);
				}
			});
		}
	}



}
