package auctionsniper.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.LineBorder;

import auctionsniper.SniperState;

public class MainWindow extends JFrame {
	private static final String APPLICATION_TITLE = "Auction Sniper";
	private static final String MAIN_WINDOW_NAME = "Auction Sniper Main";
	private static final String SNIPER_STATUS_NAME = "sniper status";
	public static final String STATUS_JOINING = "Joining";
	public static final String STATUS_LOST = "Lost";
	public static final String STATUS_BIDDING = "Bidding";
	public static final String STATUS_WINNING = "Winning";
	public static final String STATUS_WON = "Won";
	private static final String SNIPERS_TABLE_NAME = "Snipers Table";

	private final SnipersTableModel snipers = new SnipersTableModel();
	
	public MainWindow() {
		super(APPLICATION_TITLE);
		setName(MAIN_WINDOW_NAME);
		fillContentPane(makeSnipersTable());
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	private void fillContentPane(JTable snipersTable) {
		final Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		
		contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
	}

	private JTable makeSnipersTable() {
		final JTable snipersTable = new JTable(snipers);
		snipersTable.setName(SNIPERS_TABLE_NAME);
		return snipersTable;
	}

	public void showStatus(String status) {
		snipers.setStatusText(status);
		
	}

	public void sniperStatusChanged(SniperState state, String statusText) {
		snipers.sniperStatusChanged(state, statusText);
	}


}
