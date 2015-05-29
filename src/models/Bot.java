package models;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
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
				
				dataHolder.setNewAnalytics(false);
				Analytics currentAnalytics = dataHolder.getAnalytics(OpNames.INSTRUMENT1);
				
				HashMap<Integer,Option> options = dataHolder.getPortfolio().getOptions();
				
				HashMap<Integer,Integer> optionsInPortfolio = new HashMap<Integer,Integer>();
				
				for(Integer i : options.keySet()){
					
					int longValue =  dataHolder.getPortfolio().getLongValue(i);
					int shortValue = dataHolder.getPortfolio().getShortValue(i);
					
					//if we are long in this particular option
					if(longValue > shortValue){
						
						optionsInPortfolio.put(i, longValue);
						
					}else{
						
						optionsInPortfolio.put(i, -shortValue);
					}
				}
				
				
				double portfolioDelta = hedgeAssistant.portFolioDelta(optionsInPortfolio, currentAnalytics);
				int  shares = dataHolder.getPortfolio().getInstrumentShares(OpNames.INSTRUMENT1);
				
				int sharesToGet = hedgeAssistant.obtainOfUnderlying(portfolioDelta, shares);
				
				if(sharesToGet != 0){
					if(sharesToGet > 0){
						
						newOrder(sharesToGet,OpCodes.BUY_ORDER,100000);
						
					}else{
						
						newOrder(Math.abs(sharesToGet),OpCodes.SELL_ORDER,1);
					}
					
				}//if

				
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		

		
	}


	
	

}
