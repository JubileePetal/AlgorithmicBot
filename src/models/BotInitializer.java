package models;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

import view.BotConsole;
import view.BotPrompter;
//import view.LogInPanel;
import models.BotDataHolder;
import communication.BotReceiver;
import communication.BotSender;

public class BotInitializer {


	private Socket 				socket;
	private DataOutputStream 	outToServer;
	private BufferedReader 		inFromServer;
	private BotSender 			sender;
	private BotReceiver 		receiver;
	private BotDataHolder 		dataHolder;
	private Bot 				bot;
	private BotConsole			console;
	private BotPrompter			prompter;


	public void createBot(){
		
		bot = new Bot();
		
	}
	
	public BotInitializer(String host, int port) {
		
		System.out.println("Attempting to connect to " + host + " on port " + port);
		socket = initializeSocket(host, port);
	

	}

	public void startListeningToServer() {
		Thread t = new Thread(receiver);
		t.start();
	}

	public void establishDependencies() {
		
		/**Order is important here, some things need others to be
		 * initialized in order to start themselves.			*/
		
		sender.addOutputStream(outToServer);
		receiver.addBufferedReader(inFromServer);
	
		dataHolder.addObserver(bot);
		dataHolder.addObserver(console);
		
		prompter.setDataHolder(dataHolder);	
		prompter.createPopUpWindow();
		receiver.addDataHolder(dataHolder);
		console.setPrompter(prompter);
		
		
		bot.setSender(sender);
		receiver.setBot(bot);
	
		console.build();
	
		



	}
	
	public void initializeCommunicationObjects() {
		
		outToServer 	= createOutputStream(socket);
		inFromServer	= createBufferedReader(socket);
		
		sender 			= new BotSender();
		receiver 		= new BotReceiver();

	}
	
	public void initializeDataHolder() {
		
		
		dataHolder = new BotDataHolder();	

	}
	
	private Socket initializeSocket(String host, int port) {

		Socket socket = null;		
		try {
			socket = new Socket(host, port);
		} catch (UnknownHostException e) {
			System.out.println("Unknown host when trying to connect socket, shutting down.");
			System.exit(0);
		} catch (IOException e) {
			System.out.println("IO exception when trying to connect socket, shutting down.");
			System.exit(0);
		}
		return socket;		
	}
	
	private BufferedReader createBufferedReader(Socket socket) {
		
		BufferedReader inFromServer = null;
		try {
			inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			System.out.println("IO exception when trying to create inputstream from server, shutting down.");
			System.exit(0);
		}
		return inFromServer;
		
	}
	
	private DataOutputStream createOutputStream(Socket socket) {
		
		DataOutputStream outToServer = null;
		
		try {
			outToServer = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			System.out.println("IO exception when trying to create outputstream to server, shutting down.");
			System.exit(0);
		}
		return outToServer;	
	}
	

	public void botLogin() {
		
		String username = 		OpNames.ALGORITHM_BOT;
		int usertype	= 		OpCodes.ALGO_BOT;
		receiver.setUserType(usertype);
		if(sender.logIn(username, usertype)){
			
			dataHolder.setNickName(username);
		}
		
	}
	
	

	public void createConsoleAndPrompter() {
			console 	= new BotConsole();
			prompter	= new BotPrompter();
		
	}
	

	
}
