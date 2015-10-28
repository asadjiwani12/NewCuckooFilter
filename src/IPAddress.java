

import java.util.Random;
import java.util.regex.Pattern;


public class IPAddress {

	int numIPaddress24prefix = 25;
	int numIPaddressOtherprefix = 250;
	String[] IPaddress = new String[64000]; 
	String[] SplitNetworkPrefix = new String[64000];
	String[][] IPaddress_Subnet = new String[64000][2];
	String[][] FormattedIPaddress_Subnet = new String[64000][2];
	String[] NetworkPrefixBinary = new String[64000];
	String[] NetworkPrefixIPformat = new String[64000];
	String[] IPaddress_KnownTestData = new String[64000];
	String[] IPaddress_UnknownTestData = new String[1000000];

	int counterInitAddresses=0;
	int counterKnownTestAddresses=0;
	int counterUnknownTestAddresses=0;



	String splitIPAddress(String ipaddr) {
		if(ipaddr != null) {
			//System.out.println("IP address received : " +ipaddr);
			String[] temp = ipaddr.split(Pattern.quote("."));
			String returnString = temp[0]+temp[1]+temp[2]+temp[3];
			//System.out.println("Return String = " + returnString);
			return returnString;
		} 
		return "0";
	}

	String splitIPAddresstoReturnBinary(String ipaddr) {
		if(ipaddr != null) {
			//System.out.println("IP address received : " +ipaddr);
			String[] temp = ipaddr.split(Pattern.quote("."));
			temp[0] = String.format("%8s", Integer.toBinaryString(Integer.parseInt(temp[0]))).replace(' ', '0');
			temp[1] = String.format("%8s", Integer.toBinaryString(Integer.parseInt(temp[1]))).replace(' ', '0');
			temp[2] = String.format("%8s", Integer.toBinaryString(Integer.parseInt(temp[2]))).replace(' ', '0');
			temp[3] = String.format("%8s", Integer.toBinaryString(Integer.parseInt(temp[3]))).replace(' ', '0');
			String returnString = temp[0]+temp[1]+temp[2]+temp[3];
			//System.out.println("Return String = " + returnString);
			return returnString;
		} 
		return "0";
	}

	void performAndOperation() {
		System.out.println("HELLO");
		for(int i=0; i<counterInitAddresses; i++) {
			NetworkPrefixBinary[i] = String.valueOf((Integer.parseInt(FormattedIPaddress_Subnet[i][0])  & Integer.parseInt(FormattedIPaddress_Subnet[i][1])));
			System.out.println("Network Prefix binary = " + NetworkPrefixBinary[i]);
		}
	}

	void performIntAndOperation() {
		String[] IPadd;
		String[] SubnetMask;
		String[] temp = new String[4] ;
		String ConcatTemp ="";

		for(int i=0; i<counterInitAddresses; i++) {
			//System.out.println(IPaddress_Subnet[i][0]);
			IPadd = IPaddress_Subnet[i][0].split(Pattern.quote("."));
			SubnetMask = IPaddress_Subnet[i][1].split(Pattern.quote("."));
			for(int j=0; j<4; j++) {
				temp[j] = String.valueOf( ( (Integer.parseInt(IPadd[j])) & (Integer.parseInt(SubnetMask[j]))) );
				if(j==3) {
					ConcatTemp += temp[j] ;
				} else {
					ConcatTemp += temp[j] + ".";
				}
			}
			NetworkPrefixIPformat[i] = ConcatTemp; 
			ConcatTemp="";
		}
	}

	// This function takes the string in binary format, splits into 8, converts into IPaddr format
	String processIP(String ipaddr) {
		//System.out.println("IPAddress Recieved = " + ipaddr);
		String[] temp = ipaddr.split("(?<=\\G.{8})");
		temp[0] = String.valueOf(Integer.parseInt(temp[0], 2));
		temp[1] = String.valueOf(Integer.parseInt(temp[1], 2));
		temp[2] = String.valueOf(Integer.parseInt(temp[2], 2));
		temp[3] = String.valueOf(Integer.parseInt(temp[3], 2));
		return (temp[0] + "." + temp[1] + "." + temp[2] + "." + temp[3]);
	}

	// Gets the IP format(decimal) of network prefix(binary form)
	void generateIPfromNetworkPrefix() {
		for(int i=0; i<counterInitAddresses; i++) {
			NetworkPrefixIPformat[i] = processIP(NetworkPrefixBinary[i]); 
		}
	}

	
	// Generate Random IP addresses of test data
	void generateIPaddresses(int count) {
		Random r = new Random();
		int Low1 = 192;
		int LowMod1 = 10;
		int High1 = 223;
		int Low2 = 0;
		int High2 = 255;
		int Low3 = 0;
		int High3 = 255;
		int Low4 = 0;
		int High4 = 255;
		int R=0;
		String ipaddress;
		int j=0;
		
		for(int i=0; i<count; i++) {
			if(j<(int)count/2) {
				R = r.nextInt(High1-Low1) + Low1;
				j++;
			} else {
				R = r.nextInt(High1-LowMod1) + LowMod1;
				j++;
			}
			ipaddress = String.valueOf(R);
			ipaddress = ipaddress +".";
			R = r.nextInt(High2-Low2) + Low2;
			ipaddress = ipaddress + String.valueOf(R);
			ipaddress = ipaddress +".";
			R = r.nextInt(High3-Low3) + Low3;
			ipaddress = ipaddress + String.valueOf(R);
			ipaddress = ipaddress +".";
			R = r.nextInt(High4-Low4) + Low3;
			ipaddress = ipaddress + String.valueOf(R);
			IPaddress_UnknownTestData[i] = ipaddress;
			//System.out.println("IPaddress " + (i+1) + ": " +IPaddress_UnknownTestData[i]);
		}
	}

	void generateIPaddressesSubnet24bit(int count) {
		Random r = new Random();
		int Low1 = 192;
		int High1 = 223;
		int Low2 = 0;
		int High2 = 255;
		int Low3 = 0;
		int High3 = 255;
		int Low4 = 0;
		int High4 = 255;
		int R=0;
		String ipaddress;
		this.counterInitAddresses += count; 

		for(int i=0; i<count; i++) {
			R = r.nextInt(High1-Low1) + Low1;
			ipaddress = String.valueOf(R);
			ipaddress = ipaddress +".";
			R = r.nextInt(High2-Low2) + Low2;
			ipaddress = ipaddress + String.valueOf(R);
			ipaddress = ipaddress +".";
			R = r.nextInt(High3-Low3) + Low3;
			ipaddress = ipaddress + String.valueOf(R);
			ipaddress = ipaddress +".";
			R = r.nextInt(High4-Low4) + Low3;
			ipaddress = ipaddress + String.valueOf(R);
			IPaddress_Subnet[i][0] = ipaddress;
			IPaddress_Subnet[i][1] = "255.255.255.0";

			//System.out.println("IPaddress " + (i+1) + ": " +IPaddress[i]);
		}
	}

	void getSubsetKnownTestData(int special, int regular) {
		counterKnownTestAddresses = (int)(0.5 * (special+regular));
		int counterSpecialTestAddresses = (int)(0.5 * counterKnownTestAddresses);

		// Fill half test array with 24bit prefixes
		for (int i=0; i < counterSpecialTestAddresses; i++) {
				IPaddress_KnownTestData[i]= IPaddress_Subnet[i][0];
			}
		

		// Fill half test array with regular addresses
		for (int i=counterSpecialTestAddresses; i < counterKnownTestAddresses; i++) {
			IPaddress_KnownTestData[i] = IPaddress_Subnet[special+i][0];
		}

		System.out.println("Total Number of known Test Addresses being checked if Present: " + counterKnownTestAddresses);
		/*System.out.println("************KNOWN TEST DATA***********************");
		for(int i=0; i < IPaddress_KnownTestData.length; i++) {
			if(IPaddress_KnownTestData[i] != null) {
				System.out.println(IPaddress_KnownTestData[i]);
			}
		}*/
		
	}

	void getSubsetUnknownTestData(int totalCount) {
		//counterUnknownTestAddresses = (int)(0.5 * counterInitAddresses);
		generateIPaddresses(totalCount);
		System.out.println("Total Number of Unknown Test Addresses being checked if Present: " + counterUnknownTestAddresses);

		
		/*System.out.println("************UNKNOWN TEST DATA***********************");
		for(int i=0; i < IPaddress_UnknownTestData.length; i++) {
			if(IPaddress_UnknownTestData[i] != null) {
				System.out.println(IPaddress_UnknownTestData[i]);
			}
		}*/
		
		// Generate24bit
	}

	String generate_subnetmask(int prefixlength) {

		int numof255 = prefixlength / 8;
		int numbertobeconverted = prefixlength % 8;
		String subnetmask = "";
		int remaindertoInt = 0;

		for(int i=0; i<numof255;i++) {
			subnetmask += "255.";
		}


		for(int i=0; i<numbertobeconverted; i++) {
			remaindertoInt += Math.pow(2, 7-i);
		}

		if(numbertobeconverted == 0) {
			if(numof255 == 2) {
				subnetmask = subnetmask + "0" + ".0";
			} else {	
				subnetmask = subnetmask + "0" + ".0" + ".0" ;
			}	
		} else {
			if(prefixlength>16 && prefixlength <24 ) {
				subnetmask = subnetmask + String.valueOf(remaindertoInt) + ".0";
			} else if(prefixlength>8 && prefixlength <16 ) {
				subnetmask = subnetmask + String.valueOf(remaindertoInt) + ".0" + ".0" ;
			}
		} 
		return subnetmask;
	}

	void generateIPaddressesSubnet(int count) {
		Random r = new Random();
		int Low1 = 10;
		int High1 = 192;
		int Low2 = 0;
		int High2 = 255;
		int Low3 = 0;
		int High3 = 255;
		int Low4 = 0;
		int High4 = 255;
		int iplength_low = 8;
		int iplength_high = 23;
		int R=0;
		String ipaddress;

		//System.out.println("Counter Value " + this.counter);

		for(int i= this.counterInitAddresses; i<this.counterInitAddresses+count; i++) {
			R = r.nextInt(High1-Low1) + Low1;
			ipaddress = String.valueOf(R);
			ipaddress = ipaddress +".";
			R = r.nextInt(High2-Low2) + Low2;
			ipaddress = ipaddress + String.valueOf(R);
			ipaddress = ipaddress +".";
			R = r.nextInt(High3-Low3) + Low3;
			ipaddress = ipaddress + String.valueOf(R);
			ipaddress = ipaddress +".";
			R = r.nextInt(High4-Low4) + Low3;
			ipaddress = ipaddress + String.valueOf(R);
			IPaddress_Subnet[i][0] = ipaddress;
			R= r.nextInt(iplength_high-iplength_low) + iplength_low;
			IPaddress_Subnet[i][1] = generate_subnetmask(R);
			//System.out.println("IPaddress " + (i+1) + ": " +IPaddress[i]);
		}

		this.counterInitAddresses += count;
	}

	// Prints IP address and Subnet in original format
	void printIpSubnetTable() {
		System.out.println("SR.NO \t IPADDRESS \t SUBNET");
		for(int i=0; i<counterInitAddresses;i++) {
			System.out.println(i+1 + ". \t" + IPaddress_Subnet[i][0] + "\t" + IPaddress_Subnet[i][1]);
		}
	}

	void printNetworkPrefixTable() {
		System.out.println("SR.NO \t Prefix");
		for(int i=0; i<counterInitAddresses;i++) {
			System.out.println(i+1 + ". \t" + NetworkPrefixIPformat[i] );
		}
	}

	void printBinaryIpSubnetTable() {
		System.out.println("SR.NO \t\t IPADDRESS \t\t\t\t SUBNET");
		for(int i=0; i<counterInitAddresses;i++) {
			System.out.println(i+1 + ". \t" + FormattedIPaddress_Subnet[i][0] + "\t" + FormattedIPaddress_Subnet[i][1]);
		}
	}

	// Gets the IP address and subnet in the binary format
	void getFormattedIPSubnet() {
		//System.out.println(" Counter value : "+ counter);
		for(int i=0; i<counterInitAddresses;i++) {
			for(int j=0;j<2;j++) {
				FormattedIPaddress_Subnet[i][j] = splitIPAddresstoReturnBinary(IPaddress_Subnet[i][j]);
			}
		}
	}

	String getVariableIpforprefix(String ipaddr, int i ) {

		String BinaryofIP = splitIPAddresstoReturnBinary(ipaddr);
		String BinaryofIPFirstHalf = BinaryofIP.substring(0, i);
		String BinaryofIPNewSecondHalf = BinaryofIP.substring(i);
		String ReplacedSecondhalf = BinaryofIPNewSecondHalf.replace("1", "0");

		String NewIP = BinaryofIPFirstHalf + ReplacedSecondhalf;
		return processIP(NewIP);

	}


}
