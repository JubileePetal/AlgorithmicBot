package communication;

import java.io.DataOutputStream;
import java.io.IOException;

import com.google.gson.Gson;

import models.Message;
import models.OpCodes;
import models.Order;
import models.User;

public class BotSender {
	
	DataOutputStream outToServer;
	Gson gson;
	
	public BotSender() {
		gson = new Gson();
	}
	
	public void addOutputStream(DataOutputStream outputStream) {
		outToServer = outputStream;
	}
	
	public void sendOrder(Order order) {
		
		String json 	= gson.toJson(order);
		Message message = new Message();
		
		message.setType(OpCodes.ORDER);
		message.setJson(json);

		String jsonMsg = gson.toJson(message);
		send(jsonMsg);

	}
	
	public boolean logIn(String nickname, int userType) {
		
		User user 		= new User();
		
		user.setUsername(nickname);
		user.setUserType(userType);
		
		String gsonUser = gson.toJson(user);
		
		Message message = new Message();
		message.setType(OpCodes.LOG_IN);
		message.setJson(gsonUser);
		
		String gsonMessage = gson.toJson(message);
		
		return send(gsonMessage);
	}
	
	private boolean send(String message) {
		
		boolean success = true;
		
		try {
			outToServer.writeBytes(message + '\n');
		} catch (IOException e) {
			System.out.println("Couldn't send!");
			success = false;
		}
		return success;
	}
}