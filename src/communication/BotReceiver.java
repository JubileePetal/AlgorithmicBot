	package communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import models.Analytics;
import models.BookStatus;
import models.Bot;
import models.BotDataHolder;
import models.Instrument;
import models.Message;
import models.OpCodes;
import models.Option;
import models.Order;
import models.PartialTrade;
import models.Trade;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class BotReceiver implements Runnable {

	private static final int INSTRUMENTS = 0;
	private static final int MARKET_DATA = 1;
	private static final int OPTIONS = 2;
	
	
	
	private BufferedReader 	inFromServer;
	private Gson 			gson;
	private BotDataHolder 	dataHolder;
	private int 			userType;
	private Bot				bot;
	
	public BotReceiver() {
		gson = new Gson();
	}
	
	public void addBufferedReader(BufferedReader reader) {
		inFromServer = reader;
	}



	@Override
	public void run() {
		
		while(true) {
			String received = readFromServer();
			Message message = unpackMessage(received);
			routeToDestination(message);
		}
		
	}
	
	public void routeToDestination(Message message) {
		
		int messageType = message.getType();
		
		switch (messageType) {
			case OpCodes.LOG_IN_ACCEPTED: 	logInAccepted(message.getJson());
										   	break; 
			case OpCodes.ORDER_ADDED: 		orderAdded(message.getJson());
											break;
			case OpCodes.PARTIAL_TRADE: 	tradeMade(message.getJson());
											break;
			case OpCodes.MARKET_DATA: 		marketDataReceived(message.getJson());
											break;					
			case OpCodes.ANALYTICS:			analyticsReceived(message.getJson());
											break;
		}
	}
	
	public void logInAccepted(String json) {
		
		String[] initData = gson.fromJson(json, String[].class);
		
		Instrument[] instruments = gson.fromJson(initData[INSTRUMENTS], Instrument[].class);
		dataHolder.setInstruments(instruments);
		
		Option [] options  = gson.fromJson(initData[OPTIONS], Option[].class);
		dataHolder.setOptions(options);

		BookStatus[] statusOfBooks = gson.fromJson(initData[MARKET_DATA], BookStatus[].class);
		for(BookStatus bookStatus : statusOfBooks) {
			if(bookStatus != null) {
				dataHolder.newMD(bookStatus);
				
			}
		}
		
		
		
		
		//dataHolder.loggedIn();
		
		startBot(instruments, options);
	}
	
	public void analyticsReceived(String json){
		
		Analytics analytics = gson.fromJson(json, Analytics.class);
		if(analytics == null){
			
		}
		dataHolder.addAnalytics(analytics);
		dataHolder.setNewAnalytics(true);
	
		
	}
	
	
	public void marketDataReceived(String json) {
		BookStatus bookStatus = gson.fromJson(json, BookStatus.class);
		dataHolder.newMD(bookStatus);
	}
	
	public void tradeMade(String json) {
		
		PartialTrade partialTrade = gson.fromJson(json, PartialTrade.class);
		dataHolder.addTrade(partialTrade);
	}
	
	public void orderAdded(String json) {
		Order order = gson.fromJson(json, Order.class);
		
		dataHolder.addOrder(order);
	}
	
	public Message unpackMessage(String received) {
		return gson.fromJson(received, Message.class);
	}
	
	public String readFromServer() {
		
		String received = null;
		
		try {
			received = inFromServer.readLine();
		} catch (IOException e) {
			System.out.println("Could not receive message from server, Receiver");
			e.printStackTrace();
		}
		
		return received;
	}

	public void addDataHolder(BotDataHolder dataHolder) {
		this.dataHolder = dataHolder;
		
	}

	public void setUserType(int userType) {
		this.userType = userType;
	}

	public void setBot(Bot bot) {
		this.bot = bot;
	}
	
	public void startBot(Instrument [] instruments, Option[] options){
		
		bot.setInstruments(instruments);
		bot.setDataHolder(dataHolder);
		
		(new Thread(bot)).start();
	}
	
	
	
}
