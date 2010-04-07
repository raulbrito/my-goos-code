package auctionsniper.ui;

import javax.swing.table.AbstractTableModel;

import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;

public class SnipersTableModel extends AbstractTableModel {
	private final static SniperSnapshot STARTING_UP = new SniperSnapshot(null, 0, 0, SniperState.BIDDING);
	private String statusText = MainWindow.STATUS_JOINING;
	private SniperSnapshot sniperSnapshot = STARTING_UP;
	private String state;
	public static String[] STATUS_TEXT = {MainWindow.STATUS_JOINING, MainWindow.STATUS_BIDDING };
	@Override
	public int getColumnCount() {
		return Column.values().length;
	}

	@Override
	public int getRowCount() {
		return 1;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch(Column.at(columnIndex)) {
		case ITEM_IDENTIFIER:
			return sniperSnapshot.itemId;
		case LAST_PRICE:
			return sniperSnapshot.lastPrice;
		case LAST_BID:
			return sniperSnapshot.lastBid;
		case SNIPER_STATE:
			return sniperSnapshot.sniperState;
		default:
			throw new IllegalArgumentException("No column at " + columnIndex);
		}
	}
	
	public void setStatusText(String newStatusText) {
		this.statusText = newStatusText;
		fireTableRowsUpdated(0, 0);
	}

	public void sniperStateChanged(SniperSnapshot newSniperSnapshot) {
		sniperSnapshot = newSniperSnapshot;
		this.state = STATUS_TEXT[newSniperSnapshot.sniperState.ordinal()];
		
		fireTableRowsUpdated(0, 0);
	}

}
