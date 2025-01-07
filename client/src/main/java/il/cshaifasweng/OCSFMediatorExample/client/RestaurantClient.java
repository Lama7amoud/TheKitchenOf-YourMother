package il.cshaifasweng.OCSFMediatorExample.client;

import il.cshaifasweng.OCSFMediatorExample.entities.Warning;
import org.greenrobot.eventbus.EventBus;
import il.cshaifasweng.OCSFMediatorExample.client.ocsf.AbstractClient;

public class RestaurantClient extends AbstractClient {

	private static RestaurantClient client = null;
	private String host ;
	private int port ;
	private String symbol ;

	public RestaurantClient(String host, int port) {
		super(host, port);
		this.host = host;
		this.port = port;
	}

	public String getSymbol() {
		return this.symbol;
	}

	public static RestaurantClient getClient(String host, int port) {
		if (client == null) {
			client = new RestaurantClient(host, port);
		}
		return client;
	}

	@Override
	protected void handleMessageFromServer(Object msg) {
		if (msg instanceof Warning) {
			// Handle warning message
			EventBus.getDefault().post(new WarningEvent((Warning) msg));
			return;
		}
		if(msg.toString().equals("competitorwin"))
		{
			EventBus.getDefault().post("competitorwin");
			return;
		}
		if(msg.toString().equals("playAgain"))
		{
			EventBus.getDefault().post("playAgain");
			return;
		}
		if(msg.toString().equals("TEEKO"))
		{
			EventBus.getDefault().post("TEEKO");
			return;
		}
		if(msg.toString().equals("playerTurn"))
		{
			EventBus.getDefault().post("playerTurn");
			return;
		}
		if(msg.toString().equals("competitorTurn"))
		{
			EventBus.getDefault().post("competitorTurn");
			return;
		}
		if(msg.toString().equals("competitorLeft"))
		{
			EventBus.getDefault().post("competitorLeft");
			return;
		}

		if (msg instanceof TurnInfo) {
			TurnInfo data = (TurnInfo) msg;
			EventBus.getDefault().post(data);
		}else if(msg instanceof String) {

			if(msg.toString().startsWith("Yess , you are player X"))
					symbol = "X";
				else{
					symbol = "O";
				}
		}
	}





}
