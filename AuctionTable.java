package hw6;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Formatter;
import big.data.*;

/**
 * AuctionTable is a hashtable of auctions. The object itself will be printed as a table, and contains an auctionID as a key and Auction
 * as a value.
 * @author gania
 *
 */
public class AuctionTable implements Serializable {
	/**
		 * 
		 */
	private static final long serialVersionUID = 3028444359824629884L;
	HashMap<Object, Object> map;
	/**
	 * Traces auction data from given URL (assumed to be valid else throws IAE). Builds AuctionTable hashmap with variables in place.
	 * @param URL
	 * @return AuctionTable
	 * @throws IllegalArgumentException, Exception
	 */
	public static AuctionTable buildFromURL(String URL) throws IllegalArgumentException, Exception {
		try {
			DataSource ds = DataSource.connect(URL).load();
			AuctionTable table = new AuctionTable(30, 0.75f);
			String[] sellerName = ds.fetchStringArray("listing/seller_info/seller_name");
			String[] tsellerName = new String[sellerName.length];
			for (int i = 0; i < sellerName.length; i++) {
			    tsellerName[i] = sellerName[i].trim().replaceAll(" ", "_");
			}
			String[] currentBid = ds.fetchStringArray("listing/auction_info/current_bid");
			//Parse each string so it can be stored as double properly
			double[] topbid = new double[currentBid.length]; int incrementer = 0;
			for (String c : currentBid) {
				if (c.contains("$") || c.contains(",")) {
					String g = c.replaceAll("\\$", "");
					String h = g.replaceAll(",", "");
					String p = h.trim();
					double l = Double.valueOf(p);
					topbid[incrementer] = l;
				}
				else {
					topbid[incrementer] = Double.valueOf(c);
				}
				incrementer++;
			}
			String[] timeRemaining = ds.fetchStringArray("listing/auction_info/time_left");
			// Get total hours by sorting through each entry and then splitting and parsing to convert
			int nmhrs = 0; int[] time = new int[timeRemaining.length]; int count = 0;
			for (String s : timeRemaining) {
				String[] f = s.split(" ");
				for(int i = 0; i < f.length; i++) {
					if(f[i].equals("days") || f[i].equals("days,") || f[i].equals("day") || f[i].equals("day,")) {
						nmhrs += (Integer.valueOf(f[i-1]) * 24);
					}
					if(f[i].equals("hours") || f[i].equals("hrs") || f[i].equals("hr") || f[i].equals("hour")) {
						nmhrs += Integer.valueOf(f[i-1]);
					}
				}
				time[count] = nmhrs; nmhrs = 0; count++;
				
			}
			String[] auctionID = ds.fetchStringArray("listing/auction_info/id_num");
			String[] tauctionID = new String[auctionID.length];
			for (int i = 0; i < auctionID.length; i++)
			    tauctionID[i] = auctionID[i].trim();

			String[] bidderName = ds.fetchStringArray("listing/auction_info/high_bidder/bidder_name");
			String[] tbidderName = new String[bidderName.length];
			for (int i = 0; i < bidderName.length; i++)
				if(bidderName[i].trim().length() == 0) {
					tbidderName[i] = " ";
				}
				else {
			    tbidderName[i] = bidderName[i].trim().replaceAll(" ", "_");
				}

			String[] memory = ds.fetchStringArray("listing/item_info/memory");
			for (int i = 0; i < memory.length; i++) {
				if(memory[i].trim().length() == 0) {
					memory[i] = " ";
				}
			}
			String[] hard_drive = ds.fetchStringArray("listing/item_info/hard_drive");
			String[] cpu = ds.fetchStringArray("listing/item_info/cpu");
			if (auctionID.length <= 0) {
				System.out.println("There are no auctions in this file.");
			}
			else {
				int numAuctions = auctionID.length;
				int increment = 0;
				while (numAuctions > 0) {
					table.map.put(tauctionID[increment], (new Auction(time[increment], topbid[increment], tauctionID[increment], tsellerName[increment],
							tbidderName[increment], cpu[increment] + " - " + hard_drive[increment] + " - " + memory[increment])));
					numAuctions--; increment++;
				}
			}
			//outStream.writeObject(table);
			return table;
		} 
		catch (IllegalArgumentException r) {
			r.printStackTrace();
			throw new IllegalArgumentException("Try entering a correct URL...");
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Data in file contains incorrect syntax.");
			}
		}
	
	/**
	 * Manually input an auction into the table.
	 * @param auctionID
	 * @param auction
	 * @throws IllegalArgumentException
	 */
	public void putAuction(String auctionID, Auction auction) throws IllegalArgumentException {
		auction.auctionID = auctionID;
		if (this.map.containsKey(auctionID)) {
			throw new IllegalArgumentException("Auction table already contains auction ID " + auctionID);
		}
		else {
			map.put(auctionID, auction);
		}
	}
	/**
	 * Get the info of an auction with its key, and if key DNE returns null
	 * @param auctionID
	 * @return information of auction with auctionID
	 */
	public Auction getAuction(String auctionID) {
		return (Auction) this.map.get(auctionID);
	}
	/**
	 * Decrements all auction times by numHours. Cannot go below zero, but will hit zero if numHours is too large.
	 * @param numHours
	 */
	public void letTimePass(int numHours) throws IllegalArgumentException {
		if (numHours < 0) {
			throw new IllegalArgumentException("Negative input not allowed! Time is linear");
		}
		// For-each loop using Entry class and EntrySet to simplify the process because for loop doesn't work well.
		for(HashMap.Entry<Object, Object> entry : this.map.entrySet()) {
		    Auction value = (Auction) entry.getValue();
		    value.decrementTimeRemaining(numHours);
		}
	}
	/**
	 * Removes all auctions with timeRemaining = 0 from the AuctionTable HashMap.
	 */
	public void removeExpiredAuctions() {
		
		//"You can’t remove a key when you are iterating through a hash map, 
		//so you have to figure out a way to remove it after you iterate through the whole hash map."
		String[] keys = new String[this.map.size()]; int nume = 0;
		for(HashMap.Entry<Object, Object> entry : this.map.entrySet()) {
			String key = (String) entry.getKey();
		    Auction value = (Auction) entry.getValue();
		    if (value.getTimeRemaining() == 0) {
		    	keys[nume] = key;
		    	nume++;
		    }
		}
		for (String st : keys) {
			this.map.remove(st);
		}
	}
	/**
	 * Prints the whole AuctionTable with all Auctions.
	 */
	public void printTable() {
//		System.out.println("Auction ID |      Bid   |        Seller         |          Buyer          |    Time   |  Item Info // truncated to fit on one line\r\n"
//				+ "===================================================================================================================================\r\n");
		Formatter fmt = new Formatter();
		fmt.format("%12s %11s %19s %18s %11s %19s\n", "Auction ID", "Bid", "Seller", "Buyer", "Time", "Item Info");  
		System.out.println(fmt);
		for(HashMap.Entry<Object, Object> ent : this.map.entrySet()) {
		    Auction val = (Auction) ent.getValue();
		    System.out.print(val.toString());
		}
		fmt.close();
	}
		
		

	/**
	 * Construct AuctionTable based off of hashmap.
	 * @param initialCapacity
	 * @param loadFactor
	 */
	public AuctionTable(int initialCapacity, float loadFactor) {
		this.map = new HashMap<>(initialCapacity, loadFactor);
	}
	/**
	 * Default constructs AuctionTable with default HashMap settings.
	 */
	public AuctionTable() {
		this.map = new HashMap<>(16, 0.75f);
	}
}
