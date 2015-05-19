package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Observable;

import models.BookStatus;

public class BotDataHolder extends Observable {
	
	private HashMap<String, InstrumentState> instrumentStates;
	private HashMap<String, Instrument> trueInstruments;
	private ArrayList<Option> options;
	private String myNickname;
	
	
	public BotDataHolder() {
		instrumentStates 	= new HashMap<String, InstrumentState>();
		trueInstruments 	= new HashMap<String, Instrument>();
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
		System.out.println("Hej.");
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
		
		update(instrumentState);
	}
	
	public String getNickName() {
		return myNickname;
	}
	
	public void setNickName(String nick) {
		this.myNickname = nick;
		
	}

	public void newMD(BookStatus bookStatus) {
		String instrumentName = bookStatus.getInstrumentName();
		InstrumentState instrumentState = instrumentStates.get(instrumentName);
		if(instrumentState != null) {
			instrumentState.newMD(bookStatus);
			update(instrumentState);
			
		}
		
	}


	public ArrayList<Option> getOptions() {
		return options;
	}
	
	


}
