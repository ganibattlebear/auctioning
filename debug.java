package hw6;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import big.data.*;
public class debug {

	public debug() {
		// TODO Auto-generated constructor stub
	}
	public static void main(String[] args) {
		HashMap map = new HashMap(30, 0.75f);
		int increment = 0;
		String s = "sssssssss"; String b = "bbbbbbbbbbbb"; String i = "iiiiiiiiiiiiiiiiiiii";
		while (increment < 15) {
		map.put(increment, new Auction(24, 250, String.valueOf(increment), s, b, i));
		
		increment++;
		}
		System.out.println("Auction ID |      Bid   |        Seller         |          Buyer          |    Time   |  Item Info // truncated to fit on one line\r\n"
				+ "===================================================================================================================================\r\n");
		System.out.println(map.toString());
	}
}
