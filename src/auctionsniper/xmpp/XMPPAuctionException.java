package auctionsniper.xmpp;

public class XMPPAuctionException extends Exception {

	private String exceptionString;
	private Exception exception;

	public XMPPAuctionException(String exceptionString, Exception exception) {
		this.exceptionString = exceptionString;
		this.exception = exception;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String toString() {
		return "exception:" + exceptionString + " : " + exception;
	}

}
