package models;

import java.util.ArrayList;
import java.util.HashMap;
import models.BookStatus;

public class InstrumentState {

	BookStatus marketData;
	HashMap<Long, Order> orders;
	ArrayList<PartialTrade> trades;
	
	private String myInstrument;
	
	public InstrumentState(String instrumentName) {
		
		myInstrument = instrumentName;
		orders = new HashMap<Long,Order>();
		trades = new ArrayList<PartialTrade>();

	}

	public void addOrder(Order order) {
		long id = order.getId();
		synchronized(orders) {
			orders.put(id, order);
		}
	}

	public void addTrade(PartialTrade partialTrade) {
		int tradedQuantity = partialTrade.getOrder().getQuantity();
		long orderID = partialTrade.getOrder().getId();
		updateOrder(orderID, tradedQuantity);
		
		synchronized(trades) {
			trades.add(partialTrade);
		}
	}
	
	private void updateOrder(long id, int tradedQuantity) {

		Order order = orders.get(id);
		
		if(order != null) {
			
			int oldQuantity = order.getQuantity();
			
			if(oldQuantity == tradedQuantity) {
				orders.remove(id);
			} else {
				order.setOrderQuantity(oldQuantity-tradedQuantity); 
			}
			
		}
	}
	
	public ArrayList<Object[]> getOrders() {
		
		ArrayList<Object[]> orderInfoCollection = new ArrayList<Object[]>();
		
		for(Order order : orders.values()) {
			Object[] orderInfo = new Object[4];
			orderInfo[0] = order.getId();
			orderInfo[1] = (order.isBuyOrSell() == OpCodes.BUY_ORDER) ? "Buy" : "Sell";
			orderInfo[2] = order.getPrice();
			orderInfo[3] = order.getQuantity();
			orderInfoCollection.add(orderInfo);
		}
		
		return orderInfoCollection;
	}
	
	public ArrayList<Object[]> getTrades() {
		
		ArrayList<Object[]> tradeInfoCollection = new ArrayList<Object[]>();
		
		synchronized(trades) {
			for(PartialTrade trade : trades) {
				
				String buyOrSell;
				
				if(trade.getOrder().isBuyOrSell() == OpCodes.BUY_ORDER) {
					buyOrSell = "Buy";
				} else {
					buyOrSell = "Sell";
				}
				
				Object[] tradeInfo = new Object[4];
				tradeInfo[0] = trade.getTradeID();
				tradeInfo[1] = buyOrSell;
				tradeInfo[2] = trade.getOrder().getPrice();
				tradeInfo[3] = trade.getOrder().getQuantity();
				tradeInfoCollection.add(tradeInfo);
			}
		}

		
		return tradeInfoCollection;
	}
	
	public String getInstrumentName() {
		return myInstrument;
	}

	public void newMD(BookStatus bookStatus) {
		marketData = bookStatus;		
	}

	public ArrayList<Object[]> getMarketData() {
		
		ArrayList<Object[]> MD = new ArrayList<Object[]>();
		
		if(marketData != null) {
			for(Level level : marketData.getBuyLevels()) {
				Object[] levelInfo = new Object[4];
				levelInfo[0] = level.getQuantity();
				levelInfo[1] = level.getPrice();
				levelInfo[2] = "";
				levelInfo[3] = "";
				MD.add(levelInfo);
			}
			
			ArrayList<Level> sellLevels = marketData.getSellLevels();
			
			for(int i = 0; i < marketData.getSellLevels().size(); i++) {
				if(MD.size() < i+1) {
					Object[] levelInfo = new Object[4];
					levelInfo[0] = "";
					levelInfo[1] = "";
					levelInfo[2] = sellLevels.get(i).getPrice();
					levelInfo[3] = sellLevels.get(i).getQuantity();
					MD.add(levelInfo);
				} else {
					MD.get(i)[2] = sellLevels.get(i).getPrice();
					MD.get(i)[3] = sellLevels.get(i).getQuantity();
				}
			}
		}
		
		return MD;
	}
	
	public BookStatus getTrueMarketData() {
		return marketData;
	}
	
	public HashMap<Long, Order> getTrueOrders() {
		return orders;
	}
	
	public ArrayList<PartialTrade> getTrueTrades() {
		return trades;
	}
	
	public double getSellTopLevel(){
		
		double sellTop;
		
		if(!marketData.sellLevels.isEmpty()){
			sellTop = marketData.sellLevels.get(0).getPrice();
		
		}else{
			
			sellTop = 10.0;
		}
		
		return sellTop;
	}
	
	public double getBuyTopLevel(){
		
		double buyTop;
		
		if(!marketData.getBuyLevels().isEmpty()){
			buyTop = marketData.getBuyLevels().get(0).getPrice();
			
		
		}else{
			
			buyTop = 12;
		}
		
		return buyTop;
	}
	
	
}
