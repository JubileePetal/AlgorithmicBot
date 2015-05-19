package models;

import java.util.Observable;
import java.util.Observer;

import communication.BotSender;

public class Bot implements Observer, Runnable{
	
	private BotDataHolder dataHolder;
	private BotSender 	  sender;
	
	public Bot() {
		// TODO Auto-generated constructor stub
	}

	public void setSender(BotSender sender) {
		this.sender = sender;
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		
		//Get instrument state from instrument names.
		System.out.println("Got an update!");
		
	}

	@Override
	public void run() {
		
//		while(true){
//			
//		}
		
	}
	
	

}
