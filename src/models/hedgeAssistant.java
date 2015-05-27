package models;

import java.util.ArrayList;
import java.util.HashMap;

public final class hedgeAssistant {

	static final public Double portFolioDelta(HashMap<Integer, Integer> optionsInPortfolio, Analytics analytics) {
		
		Double portfolioDelta = 0d;
		ArrayList<Option> availableOptions = analytics.getOptions();
				
		Option matchingOption = null;
		
		for(Integer id : optionsInPortfolio.keySet()) {
			
			for(Option option : availableOptions) {
				if(id == option.getId()) {
					matchingOption = option;
					break;
				}
			}
			
			Double quantity = (double) optionsInPortfolio.get(id).intValue();
			Double delta = matchingOption.getDelta();
			
			portfolioDelta += quantity*delta;
		}		
		return portfolioDelta;
	}
	
	static final public int obtainOfUnderlying(Double portfolioDelta, int amountIHave) {
		
		return (int) ((-1*portfolioDelta) - amountIHave);
	}

}
