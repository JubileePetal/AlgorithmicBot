package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Observable;

import models.BookStatus;

public class BotDataHolder extends Observable {
	
	private HashMap<String, InstrumentState> 	instrumentStates;
	private HashMap<String, Instrument> 		trueInstruments;
	private HashMap<String,Analytics>			analytics;
	private ArrayList<Option> 					options;
	private String 								myNickname;
	private Portfolio							portfolio;
	private boolean 							newAnalytics;
	
	
	public BotDataHolder() {
		instrumentStates 	= new HashMap<String, InstrumentState>();
		trueInstruments 	= new HashMap<String, Instrument>();
		analytics			= new HashMap<String, Analytics>();
		portfolio			= new Portfolio();
	}	
	
	
	
	
	public Portfolio getPortfolio() {
		return portfolio;
	}




	private void update(InstrumentState instrumentState) {
		setChanged();
		notifyObservers(instrumentState);
	}
	
	public void loggedIn() {
		update(null);
	}

	public void setInstruments(Instrument[] instruments) {
		
		System.out.println("Instruments set!");
		
		for(Instrument instrument : instruments) {
			String name = instrument.getName();
			instrumentStates.put(name, new InstrumentState(name));
			trueInstruments.put(name, instrument);
		}
		
	}
	
	public void setOptions(Option[] options) {
	
		this.options = new ArrayList<Option>(Arrays.asList(options));
		portfolio.setOptions(this.options);
	}
	
	public Instrument getInstrument(String name) {
		return trueInstruments.get(name);
	}
	
	public ArrayList<String> getInstrumentNames() {

		ArrayList<String> names = new ArrayList<String>();
		
		for(String name : instrumentStates.keySet()) {
			names.add(name);
		}

		return names;
	}

	public void addOrder(Order order) {
		
		String instrumentName = order.getInstrument().getName();
		InstrumentState instrumentState = 
				instrumentStates.get(instrumentName);
		instrumentState.addOrder(order);
		instrumentState.setObjectChanged(OpCodes.ORDERS_CHANGED);
		update(instrumentState);
		
	}
	
	public InstrumentState getInstrumentState(String instrumentName) {
		return instrumentStates.get(instrumentName);
	}

	public void addTrade(PartialTrade partialTrade) {
		String instrumentName = partialTrade.getOrder().getInstrument().getName();
		InstrumentState instrumentState = instrumentStates.get(instrumentName);
		
		try {
			instrumentState.addTrade(partialTrade);
		} catch(NullPointerException e) {
			return;
		}
		instrumentState.setObjectChanged(OpCodes.TRADES_CHANGED);
		
		int quantity = partialTrade.getOrder().getQuantity();
		
		System.out.println("Initial quantity: " + quantity);
		
		if(partialTrade.getOrder().isBuyOrSell() == OpCodes.SELL_ORDER){
			System.out.println("IS SELL ORDER INDEED!");
			quantity = quantity * -1;
			System.out.println("Quantity should be negative: "+ quantity);
		}
		
		
		int currentNrOfShares = portfolio.editShares(quantity);
		instrumentState.setNumberOfShares(currentNrOfShares);

		
		update(instrumentState);
	}
	
	public String getNickName() {
		return myNickname;
	}
	
	public void setNickName(String nick) {
		this.myNickname = nick;
		
	}

	public void addAnalytics(Analytics newAnalytics){
		
		//get Instrument name and add to hashMap
		
		analytics.put(newAnalytics.getInstrumentName(), newAnalytics);
		
	}
	
	
	public Analytics getAnalytics(String instrumentName){
			
		return analytics.get(instrumentName);
	}
	
	
	public void newMD(BookStatus bookStatus) {
		String instrumentName = bookStatus.getInstrumentName();
		InstrumentState instrumentState = instrumentStates.get(instrumentName);
		if(instrumentState != null) {
			instrumentState.newMD(bookStatus);
			instrumentState.setObjectChanged(OpCodes.MD_CHANGED);
			update(instrumentState);
			
		}
		
	}

	public ArrayList<Option> getOptions() {
		return options;
	}
	
	
	
	public synchronized boolean hasNewAnalytics() {
		return newAnalytics;
	}

	public synchronized void setNewAnalytics(boolean newAnalytics) {
		this.newAnalytics = newAnalytics;
	}

	public synchronized String [] getOptionStrings(){
		
	
		String optionsAsStrings [] = new String [options.size()];
		String type;
		for(int i = 0; i < options.size(); i++){
			
			double strike 	= options.get(i).getStrikePrice();
			double matTime	= options.get(i).getTimeToMaturity();
			
			if(options.get(i).getType()== OpCodes.CALL_OPTION){
				type = "Call";
			}else{
			
				type = "Put";
			}
			

//			if(options.get(i).getStatus()== OpCodes.SELL_OPTION){
//				type = "- "+ type;
//				System.out.println("HALOOOOOOOO");
//			}	

			
			optionsAsStrings[i] = type + " "+ String.valueOf(strike) +" "
					+ String.valueOf(matTime);	
			
		}
		
		return optionsAsStrings;
	}

	public void addOptionsToPortfolio(int chosenOptionIndex, int amount) {
		
		synchronized(options){
			
			Option opt 			= options.get(chosenOptionIndex);

			//if sell option
			if(amount < 0){
				portfolio.addSoldOptions(opt, Math.abs(amount));
				
			}else{
				portfolio.addBoughtOptions(opt, Math.abs(amount));
				
			}
			
			//Use the absolute value for the amount, but 
			//if the amount is negative, add it to shortOptions,
			//else add it to long options
			
			
			
//			int counter = Math.abs(amount);
//			while(counter > 0){
//				
//				
//				if(amount < 0){
//					
//					portfolio.addSoldOption(options.get(chosenOptionIndex));
//				}else{
//					portfolio.addBoughtOption(options.get(chosenOptionIndex));
//				}
//				
//				
//				counter--;
//			}
			
			String instrumentName = options.get(chosenOptionIndex).getInstrument();
			InstrumentState instrumentState = instrumentStates.get(instrumentName);
			if(instrumentState != null) {
//				instrumentState.setOptions(portfolio.getOptions(instrumentName));
				//instrumentState.setLongOptions(portfolio.getLongOptions(instrumentName));
				//instrumentState.setShortOptions(portfolio.getShortOptions(instrumentName));
				
				instrumentState.setLongOptions(portfolio.getLongOptions());
				instrumentState.setShortOptions(portfolio.getShortOptions());
				instrumentState.setOptions(portfolio.getOptions());
				
				
				/*******************************************************************************/
				instrumentState.setObjectChanged(OpCodes.OPTIONS_CHANGED);
				update(instrumentState);
				
			}
			
			
			
			
			
			
		}
		
		
	}


}
