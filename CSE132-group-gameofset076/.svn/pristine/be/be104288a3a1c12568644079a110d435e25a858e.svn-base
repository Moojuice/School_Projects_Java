package lab5.view;

import lab5.model.SetGame;
/**GameQueue
 *  Serves as an intermediary between servantThreads as games, joining the servantThreads 
 *  to games once enough of them have been collected.
 * 
 *   Author: Ben Epstein, Jeremy Tang, Edward Xie, Kyle Liu
 *   Course: CSE 132
 *   Lab:    5
 */
public class GameQueuebe {

	public byte maxPlayers;
	public byte currentPlayers;
	public SetGame model;
	public String[] clients;
	public Object lock;

	/*
	 * Initialize the lock, model, array of clients, and max/current player number
	 */
	public GameQueuebe(int i){
		lock = new Object();
		clients = new String[i];
		for(byte j = 0; j<i; j++){
			clients[j] = "";
		}
		maxPlayers = (byte) i;
		currentPlayers = (byte) 0;
		model = new SetGame();
	}
	/**Adds a new player to the list of clients.
	 *  Returns true if you were the last player and a new
	 *  game has been started. 
	 * @param s
	 * @return Whether or not you were the last client to be added
	 */
	public boolean addClient(String s){
		boolean added = false;
		for(byte i = 0; i < maxPlayers; i++){
			if(!added){//make sure not to add a person multiple times
				if(clients[i] == ""){					
					clients[i] = s;
					added = true; //they've been added.
					currentPlayers++;
				}
			}
		}
		System.out.println("current players waiting:"+currentPlayers); //visibility of what's going on
		System.out.println("maximum players waiting:"+maxPlayers);
		if(currentPlayers == maxPlayers){//The game shall begin
			for(byte i = 0; i < currentPlayers; i++){
				model.addPlayer(clients[i]);
				clients[i] = "";
			}
			currentPlayers = 0;
			model = new SetGame(); //the model now refers to a new game in case others join
			System.out.println(maxPlayers+" player game started");//visibility
			return true; //game has started.
		}
		System.out.println("someone just joined a "+maxPlayers+"game");//visibility
		return false; //not enough players, game hasn't started yet.
	}
	/**
	 * Ask how many players until game starts
	 * @return Number of players until game starts
	 */
	public byte getPlayersLeft(){
		return (byte) (maxPlayers - currentPlayers);
	}
	/**
	 * get the array of client names
	 * @return the array of client names
	 */
	public String[] getClients(){
		return clients;
	}
	/**
	 * get the current SetGame(one that hasn't been filled yet)
	 * @return the current model
	 */
	public SetGame getModel(){
		return model;
	}
	/**
	 * get the lock object.
	 * @return lock
	 */
	public Object getLock(){
		return lock;
	}
}
