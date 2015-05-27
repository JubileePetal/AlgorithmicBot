package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;

import models.Analytics;
import models.Option;
import models.hedgeAssistant;

import org.junit.Before;
import org.junit.Test;

import tests.OptionsCollections;

public class hedgeAssistantTest {

	Analytics analytics;
	HashMap<Integer, Integer> optionsInPortfolio;
	
	@Before
	public void setUp() {
		
		analytics = new Analytics();
		ArrayList<Option> options = OptionsCollections.tinyOptionsSet();
		analytics.setOptions(options);
		
		optionsInPortfolio = new HashMap<Integer, Integer>();
		optionsInPortfolio.put(12, 100000);
		optionsInPortfolio.put(13, -200000);
		optionsInPortfolio.put(22, -50000);
	}
	
	@Test
	public void testPortfolioDelta() {
		assertEquals(-14900.0,hedgeAssistant.portFolioDelta(optionsInPortfolio, analytics), 1e-11);
	}
	
	@Test
	public void testObtainOfUnderlyingPosNeg() {
		int amountIHave = 500;
		assertEquals(14400, hedgeAssistant.obtainOfUnderlying(-14900d, amountIHave));
	}

	@Test
	public void testObtainOfUnderlyingPosPos() {
		int amountIHave = 500;
		assertEquals(-15400, hedgeAssistant.obtainOfUnderlying(14900d, amountIHave));
	}
	
	@Test
	public void testObtainOfUnderlyingNegNeg() {
		int amountIHave = -500;
		assertEquals(15400, hedgeAssistant.obtainOfUnderlying(-14900d, amountIHave));
	}

	@Test
	public void testObtainOfUnderlyingNegPos() {
		int amountIHave = -500;
		assertEquals(-14400, hedgeAssistant.obtainOfUnderlying(14900d, amountIHave));
	}


}
