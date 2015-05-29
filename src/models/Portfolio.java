package models;

import java.util.ArrayList;
import java.util.HashMap;

public class Portfolio {
	
	private HashMap<String, Integer>  			shares;
	private HashMap<Integer,Option> 			options;
	 private HashMap<Integer, Integer> 			longOptions;
	 private HashMap<Integer, Integer>	 		shortOptions;
	
	
	public Portfolio() {
		
		shares 				= new HashMap<String,Integer>();
		options				= new HashMap<Integer,Option>();
		longOptions			= new HashMap<Integer, Integer>();	
		shortOptions		= new HashMap<Integer, Integer>();	
	
		
	}
	

	
	public void setShares(String instrumentName, int initAmount){
		
		synchronized(shares){
			shares.put(instrumentName, initAmount);
		}
		
	}
	
	public void addBoughtOptions(Option option, int amount){
		
		int shortAmount = getShortValue(option.getId());
		
		int newAmount = amount - shortAmount;
		
		if(newAmount < 0){
			newShortValue(option.getId(), Math.abs(newAmount));
			newLongValue(option.getId(), 0);
		}else{
			
			newLongValue(option.getId(), newAmount);
			newShortValue(option.getId(), 0);
		}

		
	}
	
	public void addSoldOptions(Option option, int amount){
		
		int longAmount = getLongValue(option.getId());
		
		int newAmount = amount - longAmount;
		
		if(newAmount < 0){
			
			newLongValue(option.getId(), Math.abs(newAmount));
			newShortValue(option.getId(), 0);
			
		}else{
			
			newShortValue(option.getId(), newAmount);
			newLongValue(option.getId(), 0);
		}
		
		
	}
	
	
	public int editShares(int quantity){
		
		int oldQuantity = 0;
		int newQuantity = 0;
		
		if(shares.get(OpNames.INSTRUMENT1)== null){
				
			shares.put(OpNames.INSTRUMENT1, quantity);
			newQuantity = quantity;
		}else{
			
			
			oldQuantity 	= shares.get(OpNames.INSTRUMENT1);
			newQuantity		= oldQuantity + quantity;
			shares.put(OpNames.INSTRUMENT1, newQuantity);
			
		}

		
		return newQuantity;
		
		
		
	}


	
	
	private void newShortValue(int ID,int newAmount){
		
		shortOptions.put(ID,newAmount);
	}
	
	
	private void newLongValue(int ID,int newAmount){
		
		longOptions.put(ID,newAmount);
	}

	public int getShortValue(int ID){
		
		if(shortOptions.get(ID) == null){
			return 0;
		}else{
			return shortOptions.get(ID);
		}
	}
	
	
	public int getLongValue(int ID){
		
		if(longOptions.get(ID) == null){
			return 0;
		}else{
			return longOptions.get(ID);
		}
	}
	
	
	public Option getOption(int optionID){
		
		
		return options.get(optionID);
	}
	
	public void setOptions(ArrayList<Option> options){
		
		for(Option o: options){
			
			this.options.put(o.getId(), o);
		}
		
		
	}
	
	
	public synchronized int getInstrumentShares(String instrumentName){
		
		if(shares.get(instrumentName) == null){
			return 0;
		}else{
			return shares.get(instrumentName);
		}	
	}
	
	
	
	public HashMap<Integer, Integer> getLongOptions() {
		return longOptions;
	}



	public HashMap<Integer, Integer> getShortOptions() {
		return shortOptions;
	}



	public HashMap<Integer,Option> getOptions(){
		return options;
	}
	
}
