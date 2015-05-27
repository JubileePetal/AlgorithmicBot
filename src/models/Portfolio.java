package models;

import java.util.ArrayList;
import java.util.HashMap;

public class Portfolio {
	
	private HashMap<String, Integer>  			shares;
	private HashMap<String, ArrayList<Option>> 	options;
	
	public Portfolio() {
		
		shares 		= new HashMap<String,Integer>();
		options		= new HashMap<String,ArrayList<Option>>();
		
	}
	

	
	public void setShares(String instrumentName, int initAmount){
		
		synchronized(shares){
			shares.put(instrumentName, initAmount);
		}
		
	}
	
	public void addOption(Option option){
		
		String instrumentName = option.getInstrument();
		ArrayList<Option> optionsList;
		synchronized(options){
			//options.get(option.getInstrument()).add(option);
			optionsList = options.get(instrumentName);
			
			if(optionsList == null){
				optionsList = new ArrayList<Option>();
				optionsList.add(option);
				options.put(instrumentName, optionsList);
			}else{
				optionsList.add(option);
			}
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
	

	public synchronized ArrayList<Option> getOptions(String instrumentName){
		
		return options.get(instrumentName);
	}

}
