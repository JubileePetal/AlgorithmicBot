package models;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import communication.BotSender;

public class Bot implements Observer, Runnable{
	
	//private BotDataHolder dataHolder;
	private BotSender 	  sender;

	
	public Bot() {

	}

	public void setSender(BotSender sender) {
		this.sender = sender;
	}

	@Override
	public void update(Observable observed, Object objectChanged) {
		
		BotDataHolder dh = (BotDataHolder)observed;
		dh.getInstrumentNames();
		System.out.println("Bot got update");
		//InstrumentState is = (InstrumentState)objectChanged;
		
	}	

	@Override
	public void run() {
		
//		while(true){
//			
//		}
		
	}
	
	

}
