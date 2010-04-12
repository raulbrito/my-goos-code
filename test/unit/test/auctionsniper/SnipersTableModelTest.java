package test.auctionsniper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.Assert.assertEquals;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;
import auctionsniper.ui.Column;
import auctionsniper.ui.SnipersTableModel;
import auctionsniper.util.Defect;

@RunWith(JMock.class)
public class SnipersTableModelTest {
	private final Mockery context = new Mockery();
	private TableModelListener listener = context.mock(TableModelListener.class);
	private final SnipersTableModel model = new SnipersTableModel();

	@Before
	public void attachModelListener() {
		model.addTableModelListener(listener);
	}
	
	@Test
	public void hasEnoughColumns() {
		assertThat(model.getColumnCount(), equalTo(Column.values().length));
	}
	
	@Test
	public void setSniperValuesInColumns() {
		SniperSnapshot joining = SniperSnapshot.joining("item id");
		SniperSnapshot bidding = joining.bidding(555, 666);
		context.checking(new Expectations() {{
			allowing(listener).tableChanged(with(anyInsertionEvent()));
			
			one(listener).tableChanged(with(aChangeInRow(0)));
		}});
		
		model.addSniper(joining);
		model.sniperStateChanged(bidding);
		
		assertRowMatchesSnapshot(0, bidding);
	
	}
	
	@Test
	public void setsUpColumnHeadings() {
		for(Column column: Column.values()) {
			Assert.assertEquals(column.name, model.getColumnName(column.ordinal()));
		}
	}
	
	Matcher<TableModelEvent> anyInsertionEvent() {
		return hasProperty("type", equalTo(TableModelEvent.INSERT));
	}
	
	private Matcher<TableModelEvent> aChangeInRow(int row) { 
	    return samePropertyValuesAs(new TableModelEvent(model, row)); 
	} 


	private void assertColumnEquals(Column column, Object expected) {
		final int rowIndex = 0;
		final int columnIndex = column.ordinal();
		
		Assert.assertEquals(expected, model.getValueAt(rowIndex, columnIndex));
		
	}

	private Matcher<TableModelEvent> aRowChangedEvent() {
		return samePropertyValuesAs(new TableModelEvent(model, 0));
	}
	
	@Test
	public void notifiesListenersWhenAddingASniper() {
		SniperSnapshot joining = SniperSnapshot.joining("item123");
		context.checking(new Expectations() {{
			one(listener).tableChanged(with(anInsertionAtRow(0)));
		}});

		
		Assert.assertEquals(0, model.getRowCount());
		
		model.addSniper(joining);
		
		Assert.assertEquals(1, model.getRowCount());
		assertRowMatchesSnapshot(0, joining);
	}
	
	Matcher<TableModelEvent> anInsertionAtRow(final int row) {
		return samePropertyValuesAs(new TableModelEvent(model, row, row, TableModelEvent.ALL_COLUMNS, TableModelEvent.INSERT));
	}

	  
	private void assertRowMatchesSnapshot(int row, SniperSnapshot snapshot) {
		assertEquals(snapshot.itemId, cellValue(row, Column.ITEM_IDENTIFIER));
		assertEquals(snapshot.lastPrice, cellValue(row, Column.LAST_PRICE));
		assertEquals(snapshot.lastBid, cellValue(row, Column.LAST_BID));
		assertEquals(SnipersTableModel.textFor(snapshot.sniperState), cellValue(row, Column.SNIPER_STATE));
	}


	private Object cellValue(int rowIndex, Column column) {
		return model.getValueAt(rowIndex, column.ordinal());
	}
	
	@Test
	public void holdsSnipersInAdditionOrder() {
		context.checking(new Expectations() {{
			ignoring(listener);
		}});
		
		model.addSniper(SniperSnapshot.joining("item 0"));
		model.addSniper(SniperSnapshot.joining("item 1"));
		
		Assert.assertEquals("item 0", cellValue(0, Column.ITEM_IDENTIFIER));
		Assert.assertEquals("item 1", cellValue(1, Column.ITEM_IDENTIFIER));
	}
	
	@Test
	public void updatesCorrectRowForSniper() {
		context.checking(new Expectations() {{
			ignoring(listener);
		}});
		
		SniperSnapshot snapshot = SniperSnapshot.joining("item 0");
		SniperSnapshot snapshot2 = SniperSnapshot.joining("item 1");
		model.addSniper(snapshot);
		model.addSniper(snapshot2);
		
		SniperSnapshot winning = snapshot.winning(0);
		model.sniperStateChanged(winning);
		SniperSnapshot bidding = snapshot2.bidding(0, 0);
		model.sniperStateChanged(bidding);
		
		assertRowMatchesSnapshot(0, winning);
		assertRowMatchesSnapshot(1, bidding);
	}
	
	@Test(expected=Defect.class)
	public void throwsDefectIfNoExistingSniperForUpdate() {
	    model.sniperStateChanged(new SniperSnapshot("item 1", 123, 234, SniperState.WINNING));
	}
}