/**
 * 
 */
package auctionsniper;

import javax.swing.SwingUtilities;

public class SwingThreadSniperListener implements SniperListener {

	private SniperListener sniper;

	public SwingThreadSniperListener(SniperListener sniper) {
		this.sniper = sniper;
	}

	@Override
	public void sniperStateChanged(final SniperSnapshot snapshot) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				sniper.sniperStateChanged(snapshot);
			}
		});
	}
}