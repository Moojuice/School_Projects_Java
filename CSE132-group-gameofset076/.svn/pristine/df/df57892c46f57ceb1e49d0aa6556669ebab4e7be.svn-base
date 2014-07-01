package lab5.client;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import lab5.model.Player;

/**
 *  Client
 *   Player program for game of Set.
 *
 *   Author: Jeremy Tang, Edward Xie, Ben Epstein, Kyle Liu
 *   Course: CSE 132
 *   Lab:    5
 */

public class Client {
	private byte playerNumber;			//player number of client
	private byte versionNumber;
	private byte cardsLeft;				//number of cards left in deck
	private byte cardsInField;			//number of cards in field
	private Player[] players;			//players in game
	private String[] cardArray;			//cards on field
	private SetMsgOutputStream smos;
	private SetMsgInputStream smis;
	private boolean connected;			//boolean
	private boolean canSelectCards;		//whether the player can still select cards or not
	private boolean gameStarted;		//whether the game has started or not
	private byte winner;				//the player number of the game winner
	private Socket socket;
	private Timer t;
	private boolean canCallSet;			//whether the player is allowed to call set or not
	private final String ipAddress = "localhost"; // IP address of the server
	private final int setTimeOut = 10000; // penalty time for taking too long or for making bad sets
	private int popUpX = 0;
	private int popUpY = 0;
	private int popUpWidth = 300;
	private int popUpHeight = 100;

	private String name;
	
	/*
	 * Initializes the client, setting most connected instance variables
	 * false
	 */
	public Client(){
		versionNumber = 1;
		gameStarted = false;
		connected = false;
		players = new Player[4];
		cardsInField = 0;
		canCallSet = true;
		t = new Timer();
	}

	/**
	 * Connect to the server and set up SetMsgInputStream and SetMsgOutputStream
	 */
	public boolean connect(){
		if(!connected){
			try {
				System.out.println("About to connect");
				socket = new Socket(ipAddress, 10501);
				System.out.println("Connected");
				DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
				DataInputStream dis = new DataInputStream(socket.getInputStream());
				smis = new SetMsgInputStream(dis);
				smos = new SetMsgOutputStream(dos, true);
				connected = true;
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) { 
				//popup if you cannot connect to server
				connected = false;
				JFrame invalid = new JFrame();
				invalid.setVisible(true);
				invalid.setBounds(popUpX,popUpY, popUpWidth, popUpHeight);
				JTextField explanation = new JTextField("Could not connect to server.");
				explanation.setHorizontalAlignment(SwingConstants.CENTER);
				explanation.setBounds(popUpX,popUpY, popUpWidth, popUpHeight);
				invalid.add(explanation);
			}
		}
		return connected;
	}

	/**
	 * Run the GUI
	 */
	public void run(){
		SetGUI frame = new SetGUI(this);
		frame.setVisible(true);
		frame.run();
	}

	/**
	 * Listen to messages from the server
	 */
	public void listenToServer(){
		try{
			String input = smis.getInput();
			//Convert input into an array of bytes
			//Byte 37 (aka %) should be transformed into Byte 0 (since byte 0 cannot be directly sent via UTF)
			byte[] bytes = input.getBytes();
			for (int i=0; i<bytes.length; i++) {
				if (bytes[i] == 37) {
					bytes[i] = 0;
				}
			}
			if(bytes[0]==1){	//Server receives hello message, and we receive version and playernumber
				if (versionNumber!= bytes[1]){
					System.out.println("Client is out of date");
					System.exit(-1);
					return;
				}
				playerNumber = bytes[2];
			}
			else if(bytes[0] == 4){ // Wait until the number of players needed to start the game is 0.
				if(bytes[1] == 0){ 
					gameStarted = true;
				}
			}
			else if(bytes[0]==5){	//game field update
				cardsLeft = bytes[1];
				cardsInField = bytes[2];
				//creates an array with what cards are in the field (received from server)
				//updates the card array to reflect the current cards on the field
				String[] cards = new String[cardsInField];
				short index = 3;
				byte position = 0;
				for(int i = 0; i < cardsInField; i++){
					String card = "";
					for(int j =0; j<4; j++){
						card+=(char)bytes[index]; //iterates through each received card's index
						index++;
					}
					cards[position] = card;
					position++;
				}
				cardArray = cards;
			}

			else if(bytes[0] ==6){	// gameplayer update
				ByteBuffer bb = ByteBuffer.allocate(2); //part of protocol for receiving players
				bb.put(bytes[1]);
				bb.put(bytes[2]);
				short numPlayers = bb.getShort(0); //number of players
				int index = 3;
				for(int i=0; i<numPlayers; i++){ //iterates through players
					byte playerNum = bytes[index];
					index++;
					bb.clear();
					bb.put(bytes[index]);
					bb.put(bytes[index+1]);
					short length = bb.getShort(0); //length of current player's name
					index+=2;
					String name = "";
					while(length > 0){ //reads each letter of person's name
						name += (char)bytes[index];
						index++;
						length --;
					}
					//if necessary, create a new player and add them to the array
					if(players[playerNum - 1] == null || players[playerNum - 1].getName()!= name){
						Player person = new Player(name, playerNum);
						players[playerNum-1] = person;
					}
				}
			}
			else if(bytes[0] ==7){	// gamescore update
				ByteBuffer bb = ByteBuffer.allocate(2); //part of protocol
				bb.put(bytes[1]);
				bb.put(bytes[2]);
				short numPlayers = bb.getShort(0); //number of scores equal to number of players
				int index = 3;
				for(int i=0; i<numPlayers; i++){ //iterates through the number of players to update the scores
					byte playerNum = bytes[index];
					index++;
					byte[] scoreBytes = {bytes[index], bytes[index+1]};
					bb.clear();
					bb.put(scoreBytes[0]);
					bb.put(scoreBytes[1]);
					short score = bb.getShort(0);
					index+=2;
					players[playerNum-1].setScore(score); //after receiving the scores of the players, assigns that score 
														  //to the client array of people
				}
			}

			else if (bytes[0] == 14) { // select cards
				byte playerNum = bytes[1];
				if (canSelectCards && playerNum == 0) { // that means you timed out
					JFrame invalid = new JFrame();
					invalid.setVisible(true);
					invalid.setBounds(popUpX,popUpY, popUpWidth, popUpHeight);
					JTextField explanation = new JTextField("You ran out of time. Time penalty!");
					explanation.setHorizontalAlignment(SwingConstants.CENTER);
					explanation.setBounds(popUpX,popUpY, popUpWidth, popUpHeight);
					invalid.add(explanation);
					//temporarily remove selecting capacity
					canSelectCards = false;
					canCallSet = false; 
					//while timer is running, you can select cards, when it runs out, then you time out
					t.schedule(new TimerTask() {
						public void run() {
							canCallSet = true;
						}
					}, setTimeOut);
				}
				else if (this.playerNumber == playerNum) { //allowed to select cards
					canSelectCards = true;
				}
			}

			else if (bytes[0] == 17) { // valid set
				byte playerNum = bytes[1];
				canSelectCards = false;
			}

			else if (bytes[0] == 18) { // invalid set
				byte playerNum = bytes[1];
				if(playerNum!= 0){
					byte reason = bytes[2];
					if (reason == 1) { //invalid because time ran out, makes a pop-up window to notify player of this
						/*
						 * Punishment popUp created for timing out when selecting cards. This is different from the previous popUp in that
						 * this occurs if you submit a set and were out of time
						 */
						JFrame timeOut = new JFrame();
						timeOut.setVisible(true);
						timeOut.setBounds(popUpX, popUpY, popUpWidth, popUpHeight);
						JTextField explanation = new JTextField("You just barely timed out! Too bad!");
						explanation.setHorizontalAlignment(SwingConstants.CENTER);
						explanation.setBounds(popUpX, popUpY, popUpWidth, popUpHeight);
						timeOut.add(explanation);
					} 
					else if (reason == 2) { //invalid because what player chose was not a legitimate set, makes a pop-up window to notify player of this
						/*
						 * Punishment popUp for invalid set
						 */
						JFrame invalid = new JFrame();
						invalid.setVisible(true);
						invalid.setBounds(popUpX,popUpY, popUpWidth, popUpHeight);
						JTextField explanation = new JTextField("That wasn't a set! Time penalty!");
						explanation.setHorizontalAlignment(SwingConstants.CENTER);
						explanation.setBounds(popUpX,popUpY, popUpWidth, popUpHeight);
						invalid.add(explanation);
					}
					canSelectCards = false;
					// player failed; cannot call set for setTimeOut milliseconds.
					canCallSet = false;
					t.schedule(new TimerTask() {
						public void run() {
							canCallSet = true;
						}
					}, setTimeOut);
				}
			}

			else if(bytes[0] ==60){ // game over
				byte playerNum = bytes[1];
				//resets instance variables and streams
				cardsInField = 0;
				players = null;
				setGameStarted(false);
				winner = playerNum;
				smis = null;
				smos = null;
				connected = false;
				socket.close();
			}else{
				//should never happen
				System.out.println("Somehow we got a bad message");
				System.out.println("the message type was"+bytes[0]);
			}
		} catch(SocketException e){
			System.out.println("Disconnected");
			connected = false;
		}catch (EOFException e) {
			connected = false;
		}catch (IOException e) {
			connected = false;
			e.printStackTrace();
		}
	}

	/**
	 * Send hello message to server
	 * @param name player's name
	 * @param numPlayers number of players in game
	 */
	public void sayHello(String name, byte numPlayers ){
		if (connected) {
			try {
				smos.helloMessage(name, numPlayers); 
				players = new Player[numPlayers];
			}catch (SocketException e) {
				connected = false;
				e.printStackTrace();
			} 
			catch (IOException e) {
				connected = false;
				e.printStackTrace();
			}
		}
	}

	/**
	 * Sned message to ask for cards from server
	 */
	public void askForCards(){
		if (connected) {
			try {
				smos.giveMeCards();
			}catch (SocketException e) {
				connected = false;
				e.printStackTrace();
			} 
			catch (IOException e) {
				connected = false;
				e.printStackTrace();
			}
		}
	}

	/**
	 * Request to call a set
	 */
	public boolean playerCallSet(){
		if (connected&&canCallSet) {
			try{
				smos.playerCallsSet(playerNumber);
				return true;
			}catch (IOException e){
				connected = false;
				e.printStackTrace();
			}


		}

		return false;
	}
	
	/**
	 * Returns the winner of the game and resets the winner value for the next game
	 * @return winner
	 */
	public byte getWinner(){
		byte thisWinner = winner;
		winner = 0;
		return thisWinner;
	}

	//Auto-generated get/set methods for the variables
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}



	/**
	 * @return the playerNumber
	 */
	public byte getPlayerNumber() {
		return playerNumber;
	}

	/**
	 * @param playerNumber the playerNumber to set
	 */
	public void setPlayerNumber(byte playerNumber) {
		this.playerNumber = playerNumber;
	}

	/**
	 * @return the versionNumber
	 */
	public byte getVersionNumber() {
		return versionNumber;
	}


	/**
	 * @return the cardsLeft
	 */
	public byte getCardsLeft() {
		return cardsLeft;
	}

	/**
	 * @param cardsLeft the cardsLeft to set
	 */
	public void setCardsLeft(byte cardsLeft) {
		this.cardsLeft = cardsLeft;
	}

	/**
	 * @return the cardsInField
	 */
	public byte getCardsInField() {
		return cardsInField;
	}

	/**
	 * @param cardsInField the cardsInField to set
	 */
	public void setCardsInField(byte cardsInField) {
		this.cardsInField = cardsInField;
	}

	/**
	 * @return the players
	 */
	public Player[] getPlayers() {
		return players;
	}

	/**
	 * @param players the players to set
	 */
	public void setPlayers(Player[] players) {
		this.players = players;
	}

	/**
	 * @return the cardArray
	 */
	public String[] getCardArray() {
		return cardArray;
	}

	/**
	 * @param cardArray the cardArray to set
	 */
	public void setCardArray(String[] cardArray) {
		this.cardArray = cardArray;
	}

	/**
	 * @return the smos
	 */
	public SetMsgOutputStream getSmos() {
		return smos;
	}

	/**
	 * @return the connected
	 */
	public boolean isConnected() {
		return connected;
	}


	/**
	 * @return the canSelectCards
	 */
	public boolean isCanSelectCards() {
		return canSelectCards;
	}

	/**
	 * @param canSelectCards the canSelectCards to set
	 */
	public void setCanSelectCards(boolean canSelectCards) {
		this.canSelectCards = canSelectCards;
	}

	/**
	 * @return the gameStarted
	 */
	public boolean isGameStarted() {
		return gameStarted;
	}

	/**
	 * @param gameStarted the gameStarted to set
	 */
	public void setGameStarted(boolean gameStarted) {
		this.gameStarted = gameStarted;
	}


	/**
	 * @return the canCallSet
	 */
	public boolean isCanCallSet() {
		return canCallSet;
	}


	/**
	 * Entry point for lab5 client.
	 * @param args unused
	 */
	public static void main(String[] args) {
		Client c = new Client();
		c.run();
	}
}
