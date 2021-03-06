package lab5.client;

import java.io.DataOutputStream;

import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import lab5.model.Player;
/**SetMsgOutputStream
 * Writes messages for the gameOfSet based on the SetGame Protocol
 * Format: Magic Number (char '!'), Message Type (byte), Length of payload (short), Payload. 
 * 
 *  Author: Jeremy Tang, Edward Xie, Ben Epstein, Kyle Liu
 *  Course: CSE 132
 *  Lab:    4
 */
public class SetMsgOutputStream {
	private DataOutputStream dos;
	private boolean client;
	
	/*
	 * Initializes output stream by retrieving output stream
	 */
	public SetMsgOutputStream(DataOutputStream os, boolean isclient){
		dos = os;
		client = isclient;
	}
	
	/**
	 * Send a hello message from client to server 
	 * @param name player's name
	 * @param players desired number of players
	 * @throws IOException
	 */
	public synchronized void helloMessage(String name, byte players) throws IOException{
		if(client){
			dos.writeByte('!');
			dos.writeByte(2);//since we send different information this is a new type of message
			byte[] letters = name.getBytes();
			dos.writeShort(letters.length+1);//payload; number of bytes to send
			dos.writeByte(players); //desired number of players
			for(byte b = 0; b<letters.length; b++){
				dos.writeByte(letters[b]);//each letter of the name
			}
			System.out.println("written hello successfully");
			dos.flush();
		}
	}
	
	/**
	 * Respond to client's hello message
	 * @param playerNumber the player's number
	 * @param versionNumber game version number
	 * @throws IOException
	 */
	public void helloResponseMessage(byte playerNumber, byte versionNumber) throws IOException{
		if(!client){
			dos.writeByte('!');
			dos.writeByte(1);
			dos.writeShort(2);
			dos.writeByte(versionNumber);
			dos.writeByte(playerNumber); //the client needs to store its player number upon receiving it
			dos.flush();
		}
	}

	/**
	 * Request from client for cards
	 * @throws IOException
	 */
	public synchronized void giveMeCards() throws IOException{
		if(client){
			dos.writeByte('!');
			dos.writeByte(3);
			dos.writeShort(0);
			dos.flush();
		}
	}

	/**
	 * Message from server to client indicating how many players left until game start
	 * @param playersLeft number of players left until game start
	 * @throws IOException
	 */
	public void waitingForPlayers(byte playersLeft) throws IOException{
		if(!client){
			dos.writeByte('!');
			dos.writeByte(4);
			dos.writeShort(1);
			dos.writeByte(playersLeft);
			dos.flush();
			System.out.println("playersLeft: " + playersLeft);
		}
	}

	/**
	 * Message from server to client updating a client's game field
	 * @param cardsLeftInDeck number of cards left in deck
	 * @param currentFieldSize number of cards in field
	 * @param cards array of cards
	 * @throws IOException
	 */
	public void gamefieldUpdateMessage(byte cardsLeftInDeck, byte currentFieldSize, String[] cards) throws IOException{
		if(!client){
			System.out.println("currentFieldSize: " + currentFieldSize);
			System.out.println("cardsLeftInDeck: " + cardsLeftInDeck);
			dos.writeByte('!');
			dos.writeByte(5);
			dos.writeShort(2+(4*currentFieldSize));
			dos.writeByte(cardsLeftInDeck);
			dos.writeByte(currentFieldSize);
			for(int i = 0; i<currentFieldSize; i++){
				String card = cards[i];
				for(int j = 0; j <4; j++) {
					dos.writeByte(card.charAt(j));
				}
			}
			dos.flush();
		}	
	}
	
	/**
	 * Message from server to client with information about other players in the game
	 * @param numPlayers number of players
	 * @param players Set of players
	 * @throws IOException
	 */
	public void gameplayerUpdateMessage(byte numPlayers, Set<Player> players) throws IOException{
		if(!client){
			dos.writeByte('!');
			dos.writeByte(6);
			short namesLength = 0;
			String[] names = new String[numPlayers];
			Iterator<Player> it = players.iterator();
			while(it.hasNext()){
				Player p = it.next();
				names[(p.getID()-1)] = p.getName();
				namesLength+= p.getName().length();
			}
			dos.writeShort(namesLength+(3*numPlayers)+2); //one byte per player number and 2 bytes per length of name.
			dos.writeShort(names.length); //number of players
			for(byte i = 0; i< numPlayers; i++){
				dos.writeByte(i+1); //player number
				dos.writeUTF(names[i]);
			}
			dos.flush();
		}
	}
	
	/**
	 * Message from server to client updating the scores of each player
	 * @param numplayers
	 * @param scores
	 * @throws IOException
	 */
	public void gamescoreUpdateMessage(byte numplayers, short[] scores) throws IOException{
		if(!client){
			dos.writeByte('!');
			dos.writeByte(7);
			dos.writeShort(numplayers*3+2);
			dos.writeShort(numplayers);
			for(int i = 0; i< numplayers; i++){
				dos.writeByte(i+1); //player number
				dos.writeShort(scores[i]);//that player's score
				System.out.println("score of player " + i + ": " + scores[i]);
			}
			dos.flush();
		}

	}

	/**
	 * Message from client to server asking to call a set
	 * @param playerNumber the player's number
	 * @throws IOException
	 */
	public synchronized void playerCallsSet(byte playerNumber) throws IOException{
		if(client){
			dos.writeByte('!');
			dos.writeByte(13);
			dos.writeShort(1);
			dos.writeByte(playerNumber);
			dos.flush();
		}
	}

	/**
	 * Message from server to client authorizing the client to select cards
	 * @param playerNumber the player number of the player authorized to select cards
	 * @throws IOException
	 */
	public void selectCards(byte playerNumber) throws IOException{
		if(!client){
			dos.writeByte('!');
			dos.writeByte(14);
			dos.writeShort(1);
			dos.writeByte(playerNumber);//when clients read this, the one with this 
			//number will then choose the cards
			dos.flush();
		}
	}

	/**
	 * Message from client to server with the cards the player claims is a set
	 * @param playerNumber the player's number
	 * @param indexes the indices of the cards on the field that the player thinks is a set
	 * @throws IOException
	 */
	public synchronized void setCall(byte playerNumber, byte[] indexes) throws IOException{
		if(client){
			dos.writeByte('!');
			dos.writeByte(15);
			dos.writeShort(4);
			dos.writeByte(playerNumber);
			dos.writeByte(indexes[0]);
			dos.writeByte(indexes[1]);
			dos.writeByte(indexes[2]);
			dos.flush();
		}
	}

	/**
	 * Message from server to client saying the player called a valid set
	 * @param winningPlayerNumber number of the player 
	 * @throws IOException
	 */
	public synchronized void validSet(byte winningPlayerNumber)throws IOException{
		if(!client){
			System.out.println("Valid Set!");
			dos.writeByte('!');
			dos.writeByte(17);
			dos.writeShort(1);
			dos.writeByte(winningPlayerNumber);
			dos.flush();
		}
	}
	
	/**
	 * Message from server to client saying the player failed to call a valid set
	 * @param playerNumber number of the player
	 * @param reason error code indicating why the player failed to call a valid set
	 * @throws IOException
	 */
	public synchronized void invalidSet(byte playerNumber, byte reason) throws IOException{
		if(!client){
			System.out.println("Invalid set");
			dos.writeByte('!');
			dos.writeByte(18);
			dos.writeShort(2);
			dos.writeByte(playerNumber);
			dos.writeByte(reason);
			dos.flush();
		}
	}
	
	/**
	 * Message from server to client indicating that someone won the game
	 * @param winningNumber player who won the game
	 * @throws IOException
	 */
	public synchronized void gameOver(byte winningNumber) throws IOException{
		if(!client){
			dos.writeByte('!');
			dos.writeByte(60);
			dos.writeShort(1);
			dos.writeByte(winningNumber);
			dos.flush();
		}
	}
}