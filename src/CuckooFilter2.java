
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashSet;

import javax.swing.text.html.HTMLDocument.HTMLReader.SpecialAction;

public class CuckooFilter2 {

	final int max_attempts = 500;
	double elements_count ;
	double row_size = 16000;
	double col_size = 4;
	int speciallengthElements = 0;
	int occupancy = 0;
	int regularlengthElements = 0;
	int totalElements = 0;
	int totalInsertions =0;
	int containsCount=0;
	double falsePositiveRate = 0.0;

	HashSet<String> mySet ;

	CuckooFilter2() {
		elements_count = 0;
	}

	private Object hash_table[][] = new Object[(int) row_size][(int)col_size];

	// This function converts hex to String
	String hexToString(byte[] output) {
		char hexDigit[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F' };

		StringBuffer buf = new StringBuffer();

		for (int j = 0; j < output.length; j++) {
			buf.append(hexDigit[(output[j] >> 4) & 0x0f]);
			buf.append(hexDigit[output[j] & 0x0f]);
		}
		return buf.toString();
	}

	//use the hash function passed as input value to produce an integer
	//output. The output is then mapped onto one of the servers for
	//file replication.
	int useHash(String input, String functionName) {

		try {
			MessageDigest digest = MessageDigest.getInstance(functionName);
			digest.update(input.getBytes());
			byte[] out = digest.digest() ;
			String hex = hexToString(out);
			BigInteger val = new BigInteger(hex,16);
			//System.out.println("Value" + val);
			BigDecimal k = BigDecimal.valueOf(row_size);
			BigInteger b = k.toBigInteger();
			int mappedValue = val.mod(b).intValue();
			return mappedValue;
		}catch(Exception e) {
			e.printStackTrace();
		}

		return 0;
	}



	// Returns the hashvalue which is fingerPrint
	int get_hashValue(Object o) {
		int gen_code =  o.hashCode();
		if(gen_code < 0) {
			gen_code = (Math.abs(gen_code) % 255);
		}	
		return gen_code;
	}

	// Returns 2nd position in the table
	int get_hashValue2(int pos1, int hashvalue) {

		int position2 = this.useHash(String.valueOf(hashvalue),"SHA1");
		position2 = (position2 ^ pos1) % (int)this.row_size;
		//System.out.println("Hashcode2 generated : " + position2 );
		return position2;
	}


	/*
	 * This method clears the HashTable.
	 */
	void clear() {
		for(int row_cnt = 0 ; row_cnt < row_size ; row_cnt++ ) {
			for(int col_cnt=0; col_cnt < col_size; col_cnt++) {
				this.hash_table[row_cnt][col_cnt] = null ;
			}
		}
		elements_count = 0;
		hash_table = new Object[(int) row_size][(int)col_size];
	}

	/*
	 * This method gives a shallow copy of the object 
	 * @param: A 1-dimensional object array
	 */
	void copy(Object a[][]) {
		for(int row_no = 0 ; row_no < row_size; row_no++) {
			for(int col_no=0; col_no < col_size; col_no++) {
				if(hash_table[row_no] != null ) {
					a[row_no][col_no] = hash_table[row_no][col_no];	
				}
			}
		}
	}	

	/*
	 * This method checks if the parameter is already there in the hashtable.
	 *@param : Object to be checked if present
	 */
	boolean contains(Object o) {
		int flag = 0;

		int row_pos1;
		int row_pos2;
		int valuetobestored;

		// Checks the size of the hash_table
		//check(e);
		valuetobestored = get_hashValue((String) o);
		row_pos1 = this.useHash((String) o,"SHA1");
		row_pos2 = get_hashValue2(valuetobestored, row_pos1);
		//System.out.println("Row Position1 : " + row_pos1);
		//System.out.println("Row Position2 : " + row_pos2);

		for(int col_cnt=0; col_cnt < col_size; col_cnt++) {
			if(hash_table[row_pos1][col_cnt] != null || hash_table[row_pos2][col_cnt] !=null) {
				if(hash_table[row_pos1][col_cnt] != null) {
					if((int)hash_table[row_pos1][col_cnt] == valuetobestored) { 
						flag = 1;
					} 
					if(hash_table[row_pos2][col_cnt] != null) {
						if((int)hash_table[row_pos2][col_cnt] == valuetobestored) {
							flag = 1;
						}
					}	
				}	
			}
		}
		if(flag == 1) {
			return true;
		} else{
			return false;
		}
	}

	/*
	 * This method checks if the size is sufficient for an object to be added
	 * @param : The object to be added
	 */
	void check(Object o) {
		if(elements_count > (0.5 * row_size)) {
			Object new_hashtable[][]  = new Object[(int)row_size*2][(int)col_size];
			copy(new_hashtable); 	
			this.hash_table = new_hashtable;
			row_size *= 2;
		}
	}

	// This method allows to delete the object in the hashtable
	boolean delete(Object o) {
		if(contains(o)) {		
			int row_pos1;
			int row_pos2;
			int valuetobestored;

			// Checks the size of the hash_table
			//check(e);
			valuetobestored = get_hashValue((String) o);
			row_pos1 = this.useHash((String) o,"SHA1");
			row_pos2 = get_hashValue2(valuetobestored, row_pos1);

			for(int col_cnt = 0 ;col_cnt<col_size; col_cnt++) {
				if( ((int)hash_table[row_pos1][col_cnt] == valuetobestored ) || ( ((int)hash_table[row_pos2][col_cnt] == valuetobestored))) {
					if(((int)hash_table[row_pos1][col_cnt] == valuetobestored )) {
						hash_table[row_pos1][col_cnt] = null;
					} else if((int)hash_table[row_pos2][col_cnt] == valuetobestored ) {
						hash_table[row_pos2][col_cnt] = null;		
					}
					this.elements_count--;
					return true;
				}	
			}
		} 

		return false;

	}

	// This function allows the insertion of the element into the hashtable
	boolean insert(Object e) {
		int row_pos1;
		int row_pos2;

		int valuetobestored;

		// Checks the size of the hash_table
		//check(e);
		valuetobestored = get_hashValue((String) e);
		row_pos1 = this.useHash((String) e,"SHA1");
		row_pos2 = get_hashValue2(valuetobestored, row_pos1);

		//System.out.println("Row Position 1 : " + row_pos1);
		//System.out.println("Row Position 2 : " + row_pos2);
		//System.out.println("Value to be stored : " + valuetobestored);

		for(int col_no = 0; col_no < col_size; col_no++) {
			if(this.hash_table[row_pos1][col_no] == null) {
				this.hash_table[row_pos1][col_no] = valuetobestored;
				this.elements_count++;
				return true;
			}	
			else if(this.hash_table[row_pos2][col_no] == null) {
				// if storing at position 2 then too store hashcode1
				this.hash_table[row_pos2][col_no] = valuetobestored;
				this.elements_count++;
				return true ;	 
			} 
		}


		// Finding alternate position
		// i1 and i2  both are not null...
		// We will replace [i1][0] and find alternate place for the i1.
		// Find an empty spot for the previous one in the slot and reloacte that to the new spot
		for(int i = 0; i < max_attempts; i++) {
			// prev_element_value
			int prev_stored_value = (int)this.hash_table[row_pos1][0];
			// insert new element
			this.hash_table[row_pos1][0] = valuetobestored;
			// find new place for the previous
			int alt_row_pos = get_hashValue2(prev_stored_value, row_pos1);
			for(int col_no =0; col_no < col_size; col_no++) {
				if(this.hash_table[alt_row_pos][col_no] == null ) {
					this.hash_table[alt_row_pos][col_no] = prev_stored_value;
					this.elements_count++;
					return true;	
				} else {
					row_pos1 = alt_row_pos; 
					valuetobestored = (int)this.hash_table[alt_row_pos][0];
				}
			}
		}
		return false;
	}

	//This function is for lookup of the element
	// Generates all possible prefixes and then checks for the presence.
	boolean Lookup(String ipaddr, IPAddress obj1, CuckooFilter2 obj, boolean unknownData) {
		String lookupip;
		Integer arr[] = new Integer[17];
		String prev_lookupip="";

		//System.out.println("SR.NO \t\t\t Look up IP \t\t\t Prefixlength");
		for(int i=8;i<=24;i++) {
			lookupip = obj1.getVariableIpforprefix(ipaddr, i );
			lookupip = obj1.splitIPAddress(lookupip);
			if(prev_lookupip.equals(lookupip)) {
				prev_lookupip = lookupip;
				continue;
			} else {
				if(obj.contains(lookupip)) {
					if(unknownData) {
						if(mySet.contains(lookupip)) {
							//System.out.println("Found to be in the hashset");
							return false;
						}
					}
					arr[i-8] = 1;
				} else {
					arr[i-8] = 0;
				}
				prev_lookupip = lookupip;
			}
			//System.out.println((i-7) + "." + lookupip + "\t\t" + i  );
		}

		int max_pos = -1;
		int flag = 0;
		for(int i=0; i<arr.length; i++) {
			if(arr[i]!=null) {
				if(arr[i] == 1) {
					if(max_pos < i) {
						max_pos = i;
						flag = 1;
					}
				}
			}
		}

		//System.out.println("\n\n **************Lookup Result************ \n");
		if(flag == 1) {
			return true;
			//System.out.println("Network Prefix length hit = " + (max_pos + 8 ));
			//System.out.println("It's a member !");
		} else {
			return false;
			//System.out.println(ipaddr +" : Is Not a Member !");
		}

	}


	// This function prints the hash table
	void printTable(String[] ipaddress) {
		/*int row_pos1;
		int row_pos2;
		int valuetobestored;*/

		/*		for(int i=0; i<this.elements_count; i++) {
			valuetobestored = get_hashValue((String) ipaddress[i]);
			row_pos1 = this.useHash((String) ipaddress[i],"SHA1");
			row_pos2 = get_hashValue2(valuetobestored, row_pos1);
			System.out.println(ipaddress[i] + "\t" + row_pos1 + "\t" + row_pos2);
		}
		 */
		for(int i=0; i<this.row_size; i++) {
			System.out.println(i + "\t");
			for(int j=0; j<this.col_size;j++) {
				System.out.print("\t" + this.hash_table[i][j]);
			}
			System.out.println();
		}
	}

	void generate_occupancy(int totalElementsPercentage) {

		totalElements = (int) ((row_size * col_size) * 0.01 * totalElementsPercentage); 
		speciallengthElements = (int) (0.1 * totalElements);
		regularlengthElements = totalElements - speciallengthElements ;

		System.out.println("Total Elements : " + totalElements) ;
		System.out.println("Special Length Elements : " + speciallengthElements) ;
		System.out.println("Regular Length Elements : " + regularlengthElements) ;		
	}


	// false positives / false positives  + number of test elements
	double falsePositiveRate(double yesbutnot, double totaltried) {

		System.out.println("Denominator :  " + (yesbutnot + totaltried));
		System.out.println("Numerator :  " + yesbutnot);
		return ((yesbutnot / (yesbutnot + totaltried))) ;
	}


	public static void main(String args[]) {

		IPAddress obj1 = new IPAddress();
		CuckooFilter2 obj = new CuckooFilter2();

		obj.occupancy = 10;

		obj.generate_occupancy(obj.occupancy);
		int totalUnknownTestElements = 1000000;

		obj1.generateIPaddressesSubnet24bit(obj.speciallengthElements);
		obj1.generateIPaddressesSubnet(obj.regularlengthElements);

		obj1.getSubsetKnownTestData(obj.speciallengthElements, obj.regularlengthElements);

		obj1.getSubsetUnknownTestData(1000000);

		//System.out.println("****************IP-SUBNET TABLE********************* \n\n");



		//obj1.printIpSubnetTable();

		// Get the ip address and subnet in the binary format
		obj1.getFormattedIPSubnet();

		//System.out.println("\n\n\n****************BINARY IP-SUBNET TABLE********************* \n\n");

		//obj1.printBinaryIpSubnetTable();

		// Direct "And of integers from IP and subnet"
		obj1.performIntAndOperation();

		//System.out.println("\n\n\n****************NETWORK PREFIX TABLE********************* \n\n");

		//obj1.printNetworkPrefixTable();

		// Insertion
		for(int i=0; i<obj1.counterInitAddresses;i++) {
			//System.out.println(obj1.IPaddress[i]);
			obj1.SplitNetworkPrefix[i] = obj1.splitIPAddress(obj1.NetworkPrefixIPformat[i]);
			//System.out.println(obj1.SplitNetworkPrefix[i]);
		}

		for(int i=0; i<obj1.counterInitAddresses; i++) {
			if(obj.insert(obj1.SplitNetworkPrefix[i])) {
				obj.totalInsertions +=1;
			}
			//System.out.println("IP address : " + obj1.NetworkPrefixIPformat[i] + "  : " + obj.insert(obj1.SplitNetworkPrefix[i]) + "\n\n");
		}

		System.out.println("Total elements inserted is " + obj.totalInsertions);

		obj.mySet = new HashSet<String>(Arrays.asList(obj1.SplitNetworkPrefix));


		// Check for false positives
		for(int i=0; i<obj1.IPaddress_KnownTestData.length;i++) {
			if(obj1.IPaddress_KnownTestData[i] != null) {
				if(obj.Lookup(obj1.IPaddress_KnownTestData[i],obj1,obj,false)) {
					obj.containsCount += 1;
				}
			}
		}

		int unknownTestdata = 0;
		// Check for unknown test data

		for(int i=0; i<obj1.IPaddress_UnknownTestData.length;i++) {
			if(obj1.IPaddress_UnknownTestData[i] != null) {
				if(obj.Lookup(obj1.IPaddress_UnknownTestData[i],obj1,obj,true)) {
					unknownTestdata += 1;
					//obj.containsCount += 1;
				}
			}
		}

		System.out.println("Total Unknown Elements is " + unknownTestdata);
		System.out.println("Total elements contains is " + obj.containsCount);
		obj.falsePositiveRate = obj.falsePositiveRate(unknownTestdata, totalUnknownTestElements);
		System.out.println("False Positive Rate " + obj.falsePositiveRate);


		try {
			PrintWriter pw = new PrintWriter(new FileOutputStream(new File("C:\\Users\\Admin\\SEM4RIT\\Project\\FPR.txt"), true)); 
			pw.append("\n");
			pw.append(obj.occupancy + "," + obj.falsePositiveRate);
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
