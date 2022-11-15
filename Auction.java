package hw6;

import java.io.Serializable;
import java.util.Formatter;

/**
 * http://tinyurl.com/nbf5g2h - Ebay Auction Data http://tinyurl.com/p7vub89 -
 * http://tinyurl.com/p7vub89 - Yahoo Auction Data
 * Class represents an active ongoing auction which is put in a HashMap table of class AuctionTable and kept in a file.
 * @author gania
 */
public class Auction implements Serializable {
	/**
		 *
		 */
	private static final long serialVersionUID = 1L;
	int timeRemaining;
	double currentBid;
	String auctionID;
	String sellerName;
	String buyerName;
	String itemInfo;

	/**
	 * Default constructor for creating an auction -- not useful.
	 */
	public Auction() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * Constructor with auctionID
	 * @param auctionID
	 */
	public Auction(String auctionID) {
		this.auctionID = auctionID;
	}
	/**
	 * Constructor used when adding a new auction to the table
	 * @param auctionID
	 * @param timeRemaining
	 * @param info
	 */
	public Auction(String auctionID, int timeRemaining, String info, String sellername) {
		this.auctionID = auctionID;
		this.timeRemaining = timeRemaining;
		this.itemInfo = info;
		this.sellerName = sellername;
		this.currentBid = 0;
		this.buyerName = "";
	}
	/**
	 * Creates Auction with all specified member variables for use in AuctionTable generation.
	 * @param TimeRemaining
	 * @param currentBid
	 * @param auctionID
	 * @param sellerName
	 * @param buyerName
	 * @param itemInfo
	 */
	public Auction(int TimeRemaining, double currentBid, String auctionID, String sellerName, String buyerName, String itemInfo) {
		this.timeRemaining = TimeRemaining; this.currentBid = currentBid; this.auctionID = auctionID; this.sellerName = sellerName;
		this.buyerName = buyerName; this.itemInfo = itemInfo;
	}
	/**
	 * @return time left in the auction
	 */
	public int getTimeRemaining() {
		return timeRemaining;
	}

	/**
	 * @return highest bidder amount 
	 */
	public double getCurrentBid() {
		return currentBid;
	}

	/**
	 * @return unique identifier number for the auction
	 */
	public String getAuctionID() {
		return auctionID;
	}

	/**
	 * @return name of seller of item
	 */
	public String getSellerName() {
		return sellerName;
	}

	/**
	 * @return name of highest bidder
	 */
	public String getBuyerName() {
		return buyerName;
	}

	/**
	 * @return the items memory, cpu and hard-drive.
	 */
	public String getItemInfo() {
		return itemInfo;
	}

	/**
	 * Decreases the auctions time by a specified amount
	 * @param time
	 */
	public void decrementTimeRemaining(int time) {
		if (this.timeRemaining >= time) {
			this.timeRemaining -= time;
		} else {
			this.timeRemaining = 0;
		}
	}

	/**
	 * Allows one to bid on the auction. Bid is only accepted when you are the highest bidder, otherwise nothing happens.
	 * @param biddername
	 * @param bidAmt
	 * @throws ClosedAuctionException
	 */
	public void newBid(String biddername, double bidAmt) throws ClosedAuctionException {
		if (getTimeRemaining() == 0) {
			throw new ClosedAuctionException("Auction is closed.");
		}
		if (bidAmt > getCurrentBid()) {
			this.buyerName = biddername;
			this.currentBid = bidAmt;
			System.out.println("New bid placed.");
		} else
			System.out.println("Transaction failed; Bid not accepted because it is too low.");
	}

	/**
	 *Converts auction to string. Commented section should be printed outside of method before use.
	 *@return String representaiton of the auction.
	 */
	@Override
	public String toString() {
		Formatter fmt = new Formatter();
		//fmt.format("%12s %15s %21s %16s %8s\n", "Auction ID", "Bid", "Seller", "Buyer", "Time", "Item Info"); 
		String info = "";
		if (getItemInfo().length() < 42) {
			info = getItemInfo();
		}
		else {
			info += getItemInfo().substring(0, 41);
		}
		//"Auction ID |      Bid   |        Seller         |          Buyer          |    Time   |  Item Info // truncated to fit on one line\r\n"
		//+ "===================================================================================================================================\r\n"
		fmt.format("%12s %13s %20s %21s %6s %19s\n", getAuctionID(), "$"+getCurrentBid(), getSellerName(), getBuyerName(), getTimeRemaining() + "hrs", info);
		String s = fmt.toString();
		fmt.close();
		return s;
	}

}
