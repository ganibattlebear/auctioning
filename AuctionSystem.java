package hw6;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;
/**
 * The greater system which functions for I/O execution purposes and for recalling hash storage.
 * @author gania
 */
public class AuctionSystem {
AuctionTable auctionTable; String username;
	public AuctionSystem() {
		// TODO Auto-generated constructor stub
	}
	/**
	 * 
	 * @param args
	 * @throws Exception 
	 */
	@SuppressWarnings("boxing")
	public static void main(String[] args) throws Exception {
		Scanner input = new Scanner(System.in);
		input.useDelimiter(Pattern.compile("[\\r\\n;]+"));
		AuctionSystem sys = new AuctionSystem();
		System.out.println("DISCLAIMER: DO NOT MOVE THE CURSOR. I Warned You!");
		Thread.sleep(1500);
		// menu options
		System.out.println("Starting...");
		Thread.sleep(1500);
		try {
			FileInputStream file = new FileInputStream("auction.obj");
			ObjectInputStream inStream = new ObjectInputStream(file);
			System.out.println("Loading previous Auction Table...");
			sys.auctionTable = (AuctionTable) inStream.readObject();
			
		}
		catch (Exception e) {
			System.out.print("No previous auction table detected."
					+ "\r\nCreating new auction table...\r\n");
		}
		Thread.sleep(500);
		System.out.print("\r\nEnter a username: ");
		sys.username = input.next(); 
		while (true) {
			System.out.println("\r\n"
					+ "Menu:\r\n"
					+ "    (D) - Import Data from URL\r\n"
					+ "    (A) - Create a New Auction\r\n"
					+ "    (B) - Bid on an Item\r\n"
					+ "    (I) - Get Info on Auction\r\n"
					+ "    (P) - Print All Auctions\r\n"
					+ "    (R) - Remove Expired Auctions\r\n"
					+ "    (T) - Let Time Pass\r\n"
					+ "    (Q) - Quit");
			System.out.println("\r\nPlease select an option: ");
			String line = input.next();
			if (line.toLowerCase().equals("d")) {
				if (sys.auctionTable != null) {
					System.out.println("Would you like to add another set of data? y/n");
					if (input.next().toLowerCase().contains("y")){
						System.out.print("\r\nPlease enter url: ");
						String s = input.next();
						System.out.println("Loading...");
						AuctionTable table = AuctionTable.buildFromURL(s);
						for(HashMap.Entry<Object, Object> entry : table.map.entrySet()) {
							String key = (String) entry.getKey();
						    Auction value = (Auction) entry.getValue();
						    sys.auctionTable.putAuction(key, value);
						}
						System.out.println("Auction data loaded successfully!");
					}
					if (input.next().toLowerCase().contains("n")) {
						System.out.println("Ok...");
					}
				}
				else {
					System.out.print("\r\nPlease enter url: ");
					String s = input.next();
					System.out.println("Loading...");
					sys.auctionTable = AuctionTable.buildFromURL(s);
					System.out.println("Auction data loaded successfully!");
				}
			}
			else if (line.toLowerCase().equals("a")) {
				if (sys.auctionTable == null) {
					sys.auctionTable = new AuctionTable();
				}
				System.out.println("Creating new auction under seller name "+sys.username);
				String[] is = new String[3];
				System.out.println("Please enter an Auction ID:");
				is[0] = input.next();
				System.out.println("Enter an auction time (hrs): ");
				is[1] = input.next();
				System.out.println("Enter some info about item");
				is[2] = input.next();
				try {
				sys.auctionTable.putAuction(is[0], new Auction(is[0], Integer.valueOf(is[1]), is[2], sys.username));
				System.out.println("Auction " + is[0] + " added to table.");
				}
				catch(Exception e) {
					System.out.println("Problem with input...");
				}
			}
			else if (line.toLowerCase().equals("b")) {
				System.out.println("Please enter an Auction ID on which you wish to bid: ");
				String newid = input.next();
				Auction a = sys.auctionTable.getAuction(newid);
				if (a == null) {
					System.out.println("Auction does not exist.");
				}
				else if (a.getTimeRemaining() == 0) {
					System.out.println("Auction " + a.getAuctionID()+ "is CLOSED");
				}
				else {
					System.out.println("Auction " + a.getAuctionID()+ " is OPEN");
					System.out.println("Current Bid: $"+ a.getCurrentBid());
					System.out.println("What would you like to bid?: ");
					try {
					double newbid = input.nextDouble();
					a.newBid(sys.username, newbid);
					}
					catch (Exception e) {
						System.out.println("Wrong type of input.");
					}
				}
			}
			else if (line.toLowerCase().equals("i")) {
				System.out.println("Please enter an Auction ID: ");
				String theid = input.next();
				Auction inauc = sys.auctionTable.getAuction(theid);
				if (inauc == null) {
					System.out.println("No auction found.");
				}
				else {
				System.out.println("Auction "+theid+":\r\n"
						+ "    Seller: "+inauc.getSellerName()+"\r\n"
						+ "    Buyer: "+inauc.getBuyerName()+"\r\n"
						+ "    Time: "+inauc.getTimeRemaining()+"\r\n"
						+ "    Info: "+inauc.getItemInfo()+"");
				}
			}
			else if (line.toLowerCase().equals("p")) {
				sys.auctionTable.printTable();
			}
			else if (line.toLowerCase().equals("r")) {
				System.out.println("Removing expired auctions...\r\n");
				try {
					sys.auctionTable.removeExpiredAuctions();
				}
				catch (Exception e) {
					System.out.println("No auctions found.");
				}
			}
			else if (line.toLowerCase().equals("t")) {
				System.out.println("How many hours should pass: ");
				try {
				int time = input.nextInt();
				Thread.sleep(1000);
				System.out.println("Time passing...");
				Thread.sleep(4000);
				sys.auctionTable.letTimePass(time);
				System.out.println("Auction times updated. Check to see if some timers have hit zero.");
				}
				catch (Exception e) {
					System.out.println("Wrong input type");
				}
			}
			else if (line.toLowerCase().equals("q")) {
				try {
				System.out.println("Writing auction table to file auction.obj...");
				FileOutputStream file = new FileOutputStream("auction.obj");
				ObjectOutputStream outStream = new ObjectOutputStream(file);
				outStream.writeObject(sys.auctionTable);
				outStream.close(); input.close();
				System.out.println("Done!");
				break;
				}
				catch (Exception e) {
					System.out.print("Something went wrong with saving your data.");
				}
			}
			else {
				System.out.println("You entered the wrong input.");
				Thread.sleep(1000);
			}
			Thread.sleep(3000);
		}
	}
}
