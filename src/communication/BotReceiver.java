	package communication;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import models.BookStatus;
import models.BotDataHolder;
import models.Instrument;
import models.Message;
import models.OpCodes;
import models.Order;
import models.PartialTrade;
import models.Trade;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

//import control.Controller;
//import control.Controller;

public class BotReceiver implements Runnable {

//	public Controller controller; 
	private BufferedReader inFromServer;
	private Gson gson;
	private BotDataHolder dataHolder;
	private int userType;
	
	public BotReceiver() {
		gson = new Gson();
	}
	
	public void addBufferedReader(BufferedReader reader) {
		inFromServer = reader;
	}
	
//	public void addController(Controller controller) {
//		this.controller = controller;
//	}

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
			case OpCodes.LOG_IN_ACCEPTED: logInAccepted(message.getJson());
										   break; 
			case OpCodes.ORDER_ADDED: orderAdded(message.getJson());
										break;
			case OpCodes.PARTIAL_TRADE: tradeMade(message.getJson());
										break;
			case OpCodes.MARKET_DATA: marketDataReceived(message.getJson());
										break;
		}
	}
	
	public void logInAccepted(String json) {
		
		String[] instrumentsAndMD = gson.fromJson(json, String[].class);
		
		Instrument[] instruments = gson.fromJson(instrumentsAndMD[0], Instrument[].class);
		dataHolder.setInstruments(instruments);
		dataHolder.loggedIn();
	

		
		
		BookStatus[] statusOfBooks = gson.fromJson(instrumentsAndMD[1], BookStatus[].class);
		for(BookStatus bookStatus : statusOfBooks) {
			if(bookStatus != null) {
				dataHolder.newMD(bookStatus);
				
			}
		}

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
	
	
}
