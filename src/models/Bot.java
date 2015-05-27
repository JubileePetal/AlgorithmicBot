package models;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import view.BotConsole;
import view.BotPrompter;
import communication.BotSender;

public class Bot implements Observer, Runnable{
	

	private BotSender 	  			sender;
	private BotDataHolder			dataHolder;
	private Instrument []			instruments;

	
	
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
	
	
	public void setDataHolder(BotDataHolder dataHolder) {
		this.dataHolder = dataHolder;
	}

	public void setInstruments(Instrument [] instruments) {
		this.instruments = instruments;
	}

	
	public void newOrder(int quantity, int orderType, double price){
		
		Order newOrder = new Order();
		newOrder.setOrderQuantity(quantity);
		newOrder.setTypeOfOrder(OpCodes.LIMIT_ORDER);
		newOrder.setPrice(price);
		newOrder.setOrderOwner(OpNames.ALGORITHM_BOT);
		newOrder.setInstrument(dataHolder.getInstrument(OpNames.INSTRUMENT1));
		
		
		
		
		if(orderType == OpCodes.SELL_ORDER){
		
			newOrder.setToSellOrder();
		}else{
			
			newOrder.setToBuyOrder();
		}
		
		
		
		sender.sendOrder(newOrder);
		
	}
	
	@Override
	public void run() {
		
		
		while(true){
			
			
			
			if(dataHolder.hasNewAnalytics()){
				
				System.out.println("all the analytics!");
				dataHolder.setNewAnalytics(false);
				Analytics currentAnalytics = dataHolder.getAnalytics(OpNames.INSTRUMENT1);
				
				
				newOrder(10, OpCodes.SELL_ORDER, 12);
				// analyze stuff
				
				
				
				
			}
			
			try {
				Thread.sleep(4000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		

		
	}


	
	

}
