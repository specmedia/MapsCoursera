package module6;

import java.util.ArrayList;
import java.util.Collections;

/** Author: UC San Diego Intermediate Software Development MOOC team
 * @student Dmitry Sergeev, Minsk, Belarus.
 * Date: March 17, 2016
 * */

public class Airport implements Comparable<Airport> {
					//always gets tacked on iwth the generic as itself, 
					//and will force us to implement a compable method for htis class (see below)
	
	private String city;
	private String country;
	private String code3;
	
	public Airport(String string, String string2, String string3) {
		}

	public String getCity(){
		return this.city;
	}
	
	public String getCountry(){
		return this.country;
	}
	
	public String getCode3(){
		return this.code3;
	}
	
	
	public static void main(String args[]){
		ArrayList<Airport> airports = new ArrayList<>();
		
		Airport a = new Airport("Lagos", "Nigeria", "LAG");
		
		airports.add(a);
		//now if we pass in a list of airport objects and call sort, Java knows what to do since we implemented comparable method
		//and told it how we want to sort airports (see below)
		Collections.sort(airports);
		
	}
	
	//myversion
	public static String findCode(String toFind, Airport [] airports){
		
		//int i=0;
		String codeNotFound = "";
		for(int i=0; i<airports.length; i++ ){
			if(airports[i].getCity()==toFind){
				return airports[i].getCode3();
				//return code; 
			}
		}
		return codeNotFound;
		
	}

	
	//the UCSD version
	public static String findAirportCode(String toFind, Airport [] airports){
	 int i = 0;
	 while(i<airports.length){
		 Airport a = airports[i];
		 if(a.getCity().equals(toFind)){
			 return a.getCode3();
		 }
		 i++; //down't forget to increment a while loop! -- must happen OUTSIDE the while loop if the while loop returns something
		 
	 }
	return null; //null is an okay value to return if we couldn't find something--even a string
	 }
	
	
	//BS my version of selection sort
	public static String findAirportCodeBS(String toFind, Airport [] airports){
		
		//low, mid, high
		int low = 0;
		int high = airports.length-1; //UCSD used aiports.size() -1
		int mid; //mid gets declared, but continually calculated within the loop = (low+high)/2;
		
		while(high>=low){ //they did low<=high
			mid = (low+high)/2; //mid = low +((high-low)/2)
			//compare our search string to the mid string
			int compare = toFind.compareTo(airports[mid].getCity()); //with BS are always comparing to the midpoint
				//not an "i" value, because comparing the mid will allow you to split the list appropriately
			if(compare<0){ //we need to get the -1, 0, or 1 from the compareTo function to determine how to shift the values
					//I scrambled these ("the thing we're trying to find something taht's less than the mid, so we need to adjust our high value"
				high = mid-1;
			}
			else if(compare >0){
				low = mid+1; //the thing we're tring to find is greater than then mid so we need to adjust our low value up.
				
			}
						else return airports[mid].getCode3();
			
		}
		
		return null;
	}
	
	public static void selectionSort(int [] arr){
		//i is our invariant property, allows us to keep track of what we've checked
		for(int i=0; i<arr.length-2; i++){//outer loop -- covers finding and putting a value into each slot, 0-end
			
			int idxOfMin = i; //keeps track of smallest thing, at the beginning our guest is that it's our current position
							//but then we step through unsorted part of array to look and see how the rest of vals compare
			
					//j covers unsorted part of the array
			for(int j=i+1; j<arr.length; j++){ //don't need the minus one, because we want to check even the last value
					
				if(arr[j]<arr[idxOfMin]){
						idxOfMin = j; //means our location for the smallest number needs to be updated, and so we reassign
									//this inner loop will look at each possible  value
					}
				}
					
			// place the smallest number where it belongs--in our current i (helper method swap)
			swap(arr, idxOfMin, i);
			
			}
		//return arr;
	}
	private static void swap(int [] arr, int idxOfMin, int currI){
		
		int swapHolder = arr[currI];
		arr[currI] = arr[idxOfMin];
		arr[idxOfMin] = swapHolder;
		
	}
	
	@Override //needs to be called by a calling object of this class, and how we want to compare it to another object of the class
	public int compareTo(Airport o) {
		
		//need access to instance variable in the object for comparison
			//in this case we can just compare strings
		return (this.getCity().compareTo(o.getCity()));
		
		}			
}