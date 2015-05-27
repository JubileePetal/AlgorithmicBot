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
	private String 							myNickname;
	private Portfolio							portfolio;
	private boolean 							newAnalytics;
	
	
	public BotDataHolder() {
		instrumentStates 	= new HashMap<String, InstrumentState>();
		trueInstruments 	= new HashMap<String, Instrument>();
		analytics			= new HashMap<String, Analytics>();
		portfolio			= new Portfolio();
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
		System.out.println("Hej. Options set in BotDataHolder.");
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
			
			if(options.get(i).getStatus()== OpCodes.SELL_OPTION){
				type = "- "+ type;
				System.out.println("HALOOOOOOOO");
			}	
			
			optionsAsStrings[i] = type + " "+ String.valueOf(strike) +" "
					+ String.valueOf(matTime);	
			
		}
		
		return optionsAsStrings;
	}

	public void addOptionsToPortfolio(int chosenOptionIndex, int amount) {
		
		synchronized(options){
			
			Option opt 			= options.get(chosenOptionIndex);

			/***********************************************************/
			String type;
			if(opt.getType()== OpCodes.CALL_OPTION){
				
				type = "Call";
			}else{
			
				type = "Put";
			}
			double strike 	= opt.getStrikePrice();
			double matTime	= opt.getTimeToMaturity();
			
			System.out.println(type + " "+ String.valueOf(strike) +" "
					+ String.valueOf(matTime));
			
			System.out.println("Amount = " + amount);
			/***********************************************************/
			
			while(amount > 0){
				
				portfolio.addOption(options.get(chosenOptionIndex));
				amount--;
			}
			
			String instrumentName = options.get(chosenOptionIndex).getInstrument();
			InstrumentState instrumentState = instrumentStates.get(instrumentName);
			if(instrumentState != null) {
				instrumentState.setOptions(portfolio.getOptions(instrumentName));
				instrumentState.setObjectChanged(OpCodes.OPTIONS_CHANGED);
				update(instrumentState);
				
			}
			
			
			
			
			
			
		}
		
		
	}


}
