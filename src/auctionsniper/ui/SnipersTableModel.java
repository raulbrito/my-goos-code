package auctionsniper.ui;

import javax.swing.table.AbstractTableModel;

import auctionsniper.SniperListener;
import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;

public class SnipersTableModel extends AbstractTableModel implements SniperListener {
	private final static SniperSnapshot STARTING_UP = new SniperSnapshot(null, 0, 0, SniperState.BIDDING);
	private SniperSnapshot sniperSnapshot = STARTING_UP;
	private String state;
	public static String[] STATUS_TEXT = {"JOINING", "BIDDING", "WINNING", "LOST", "WON" };
	@Override
	public int getColumnCount() {
		return Column.values().length;
	}
	
	@Override
	public String getColumnName(int column) {
		return Column.at(column).name;
	}
	
	@Override
	public int getRowCount() {
		return 1;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return Column.at(columnIndex).valueIn(sniperSnapshot);
	}

	public void sniperStateChanged(SniperSnapshot newSniperSnapshot) {
		this.sniperSnapshot = newSniperSnapshot;
		
		fireTableRowsUpdated(0, 0);
	}

}
