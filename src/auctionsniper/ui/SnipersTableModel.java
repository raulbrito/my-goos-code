package auctionsniper.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import auctionsniper.AuctionSniper;
import auctionsniper.SniperListener;
import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;
import auctionsniper.SwingThreadSniperListener;
import auctionsniper.SniperPortfolio.PortfolioListener;
import auctionsniper.util.Defect;

public class SnipersTableModel extends AbstractTableModel implements SniperListener, PortfolioListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<SniperSnapshot> snapshots = new ArrayList<SniperSnapshot>();

	public static String[] STATUS_TEXT = {"Joining", "Bidding", "Winning", "Losing", "Lost", "Won", "Failed" };
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
		return snapshots.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return Column.at(columnIndex).valueIn(snapshots.get(rowIndex));
	}

	public void sniperStateChanged(SniperSnapshot newSniperSnapshot) {
		int row = rowMatching(newSniperSnapshot);
		snapshots.set(row, newSniperSnapshot); 
		fireTableRowsUpdated(row, row);
	}
	
	private int rowMatching(SniperSnapshot snapshot) {
		for (int i = 0; i < snapshots.size(); i++) {
			if (snapshot.isForSameItemAs(snapshots.get(i))) {
				return i;
			}
		}
		
		throw new Defect("Cannot find match for " + snapshot);
	}

	public static String textFor(SniperState sniperState) {
		return STATUS_TEXT[sniperState.ordinal()];
	}

	private void addSniperSnapshot(SniperSnapshot newSniper) {
	    snapshots.add(newSniper);
	    int row = snapshots.size() - 1;
	    fireTableRowsInserted(row, row);
		
	}

	@Override
	public void sniperAdded(AuctionSniper sniper) {
		addSniperSnapshot(sniper.getSnapshot());
		sniper.addSniperListener(new SwingThreadSniperListener(this));
		
	}

}
