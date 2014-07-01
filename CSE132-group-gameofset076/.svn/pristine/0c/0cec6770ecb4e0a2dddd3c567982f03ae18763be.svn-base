package lab5.view;

import lab5.model.Player;
import lab5.model.SetGame;

public class Logger {
	private Observer logger;
	
	/**
	 * Logger watches all activity.
	 * @param model game of set
	 */
	public Logger(SetGame model) {
		logger = new Observer("Logger");
		for (Player p : model.getPlayers()) {
			logger.addObsession(p);
		}
	}

}
