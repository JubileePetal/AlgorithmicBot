package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import models.BotDataHolder;
import models.OpCodes;

public class BotPrompter  {
	
	private BotDataHolder 	dataHolder;
	private JPanel			popUpPanel;
	private JComboBox		optionChoices;
	private JTextField		amountField;
	
	public BotPrompter() {
		
	}

	public void setDataHolder(BotDataHolder dataHolder) {
		this.dataHolder = dataHolder;
	}
	
	public void createPopUpWindow(){
		popUpPanel 		= new JPanel();
		popUpPanel.setPreferredSize(new Dimension(400,75));
		
		optionChoices 	= new JComboBox();
		amountField		= new JTextField(7);
		JLabel amount	= new JLabel("#");
		
		
		popUpPanel.add(optionChoices);
		popUpPanel.add(amount);
		popUpPanel.add(amountField);
		
	
	}

	public void editPortfolio() {
		
		if(optionChoices.getItemCount() == 0){
			getDataForPopUp();
		}
		
		
		int result = JOptionPane.showConfirmDialog(null,  popUpPanel,
				"Add options to portfolio", JOptionPane.OK_CANCEL_OPTION);
		
		int chosenOption 	= optionChoices.getSelectedIndex();
		String amount		= amountField.getText();
		if(amount != null){
			
			dataHolder.addOptionsToPortfolio(chosenOption, Integer.parseInt(amount));
			
		}

	}

	private void getDataForPopUp() {
		Object [] optionsData 	= dataHolder.getOptionStrings();
		
		
		for(Object o : optionsData){
			
			optionChoices.addItem(o);
		}
		
		//optionChoices.repaint();
		
	}


	
	
}
