package models;

public class MainABot {

	public static void main(String[] args) {
		String host = args[0];
		int port 	= 1337;
		BotInitializer initializer = new BotInitializer(host, port);
		
		initializer.initializeCommunicationObjects();
		initializer.initializeDataHolder();
		initializer.createConsoleAndPrompter();
		initializer.createBot();
		
		initializer.establishDependencies();
		initializer.startListeningToServer();
		initializer.botLogin();
		
		
	}

}
