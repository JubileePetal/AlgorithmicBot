package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import models.InstrumentState;
import models.OpCodes;
import models.Option;

public class BotConsole extends JFrame implements KeyListener, Observer {

	  private static JTextArea 		displayArea;
	  private JTextField 			typingArea;
	  private static BotPrompter 	myPrompter;
	  
	  static final int 		P_KEY = 80;

	
	public BotConsole(String name) {
		super(name);
	}
	
	public BotConsole() {

	}
	
	
	public void build(){
		
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
		
	}
	
    private static void createAndShowGUI() {
        //Create and set up the window.
        BotConsole listenerFrame = new BotConsole("Portfolio");
        listenerFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	listenerFrame.setLocationRelativeTo(null);
         
        //Set up the content pane.
        listenerFrame.addComponentsToPane();
         
         
        //Display the window.
        listenerFrame.pack();
        listenerFrame.setVisible(true);
    }
    
    private void addComponentsToPane() {
    	
 
         
        typingArea = new JTextField(20);
        typingArea.addKeyListener(this);

         
        displayArea = new JTextArea("");
        displayArea.setEditable(false);
       // displayArea.setBackground(Color.BLACK);
        Font font = new Font("Jokerman", Font.PLAIN, 12);
        
       // displayArea.setFont(font);
        //displayArea.setForeground(Color.white);
        JScrollPane scrollPane = new JScrollPane(displayArea);
        scrollPane.setPreferredSize(new Dimension(375, 225));
         
    
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(typingArea, BorderLayout.PAGE_END);
    }

    public void keyTyped(KeyEvent e) {
        //displayInfo(e, "KEY TYPED: ");

    }
     

    public void keyPressed(KeyEvent e) {

    	if(e.getKeyCode()==P_KEY){
    		
    			triggerPrompter();
    	}
    		
    		
    	
    }
     

    public void keyReleased(KeyEvent e) {
      
    	typingArea.setText("");
    	
    }
	
	
	public void setPrompter(BotPrompter prompter) {
		
		myPrompter = prompter;
		if(myPrompter != null){
			System.out.println("YEEES");
		}
		
	}
	
	
	public void triggerPrompter(){
		
		myPrompter.editPortfolio();
		if(myPrompter != null){
			System.out.println("WFT.....wees");
		}
		
	}	

	
	private void setCosoleText(ArrayList<Option> options){
		
		this.displayArea.setText("");
		

		
		
		for(int i = 0; i < options.size(); i++){
			
			String type;
			if(options.get(i).getType()== OpCodes.CALL_OPTION){
				
				type = "Call";
			}else{
			
				type = "Put";
			}
		
			double strike 	= options.get(i).getStrikePrice();
			double matTime	= options.get(i).getTimeToMaturity();
			
			if(options.get(i).getStatus() == OpCodes.SELL_OPTION){
				type = " - "+ type;
			
			}	
				this.displayArea.append(type + " "+ String.valueOf(strike) +" "
					+ String.valueOf(matTime)+"\n");
			
		}
		
		
		
	}
	
	
	@Override
	public void update(Observable observed, Object objectChanged) {

		
		final InstrumentState is = (InstrumentState)objectChanged;
		System.out.println("Nr of shares: " +(is.getNrOfShares()));
		
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		    	
		    	setCosoleText(is.getOptions());
		    }
		});
		

		
	}
	
	
	

}
