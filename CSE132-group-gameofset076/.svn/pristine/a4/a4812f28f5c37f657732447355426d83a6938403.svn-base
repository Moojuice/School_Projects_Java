package lab5.view;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import lab5.client.SetMsgInputStream;
import lab5.client.SetMsgOutputStream;
import lab5.model.SetGame;
/**ServantThread:
 * 
 * This thread listens to the client for what type of game to join, joins an
 * appropriate game, and then listens to requests from the client and pcs from the game
 * in order to facilitate interaction between them to interact with the game.
 * 
 *   Author: Ben Epstein, Jeremy Tang, Edward Xie, Kyle Liu
 *   Course: CSE 132
 *   Lab:    5
 */
public class ServantThread extends Thread {
	private SetGame model;
	private byte yourNumber;
	private byte versionNumber = 1; //arbitrary.
	private Socket mySocket;
	boolean started;
	private String name;
	private GameQueuebe master;
	private SetMsgOutputStream smos;
	private SetMsgInputStream smis;
	private boolean connected; 
	
	/*
	 *Initialize input and output streams, listen to client for the requested game type, and join the
	 *corresponding GameQueuebe. Joins the game, and adds PCS listeners for when things in the game change.
	 */
	public ServantThread(GameQueuebe[] masters, Socket s){
		try {
			mySocket = s;
			connected = true;
			smis = new SetMsgInputStream(new DataInputStream(s.getInputStream()));
			smos = new SetMsgOutputStream(new DataOutputStream(s.getOutputStream()),false);
			byte[] hello = smis.getInput().getBytes();
			System.out.println("player successfully received");
			byte players;
			name = "";
			if(hello[0]==2){//Was this the right message type?
				players = hello[1];
				for(short position = 2; position<hello.length; position++){
					name+= (char) hello[position];

				}
				System.out.println("We have a name: "+name);//visibility
			}else{
				s.close(); //disconnect if you didn't receive a hello message.
				connected = false;
				model = null;
				System.out.println("no hello message :("); //visibility
				return;
			}
			yourNumber = 0;
			master = masters[players-1];
			System.out.println("We have a master: " + master);//visibility
			Object lock = master.getLock();

			synchronized(lock){//assure that nothing changes while you work
				lock.notifyAll();
				model = master.getModel();
				
				if(master.addClient(name)){//if you were the last player to be added in this game
					yourNumber = players;
				}else{//if there are still more players to be added
					for (int i=players-1; i>=0; i--) {
						if (name.equals(master.getClients()[i])) {
							yourNumber = (byte)(i+1);
							started = false;
							System.out.println("we have not yet started");
							i = -1;
						}
					}
				}
				

				
				System.out.println(yourNumber+" is "+name+"'s number!");//visibility
				lock.notifyAll();
				smos.helloResponseMessage(yourNumber, versionNumber); //tell the client their number
				//set up the pcs for all cases
				model.getPCS().addPropertyChangeListener("score", //response for when score changes
						new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						if(connected){//don't do anything if you have no connection(clearly)
							if(model.isGameRunning()){// don't do anything unless game is ongoing
								try {
									smos.gamescoreUpdateMessage((byte)model.getPlayers().size(), model.getScores());
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}else{
							return;
						}
					}
				});
				model.getPCS().addPropertyChangeListener("removed", //response for when the players are removed
						new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						if(connected){//don't do anything if you have no connection(clearly)
							if(model.isGameRunning()){// don't do anything unless game is ongoing
								System.out.println("PCS removed a player");
								try {
									smos.gameplayerUpdateMessage((byte)model.getPlayers().size(), model.getPlayers());
									smos.gamescoreUpdateMessage((byte)model.getPlayers().size(), model.getScores());
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}else{
							return;
						}
					}
				});
				model.getPCS().addPropertyChangeListener("playerCallingSet", //response for when someone calls set
						new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						if(connected){//don't do anything if you have no connection(clearly)
							if(model.isGameRunning()){// don't do anything unless game is ongoing
								try {
									smos.selectCards(model.getPlayerCallingSet());
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}else{
							return;
						}
					}
				});
				model.getPCS().addPropertyChangeListener("cards", //response for when the cards change
						new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						if(connected){//don't do anything if you have no connection
							if(model.isGameRunning()){// don't do anything unless game is ongoing
								try {
									smos.gamefieldUpdateMessage((byte)model.numCardsLeft(), (byte)model.getNumCardsOnField(), model.getCardsInField());

								} catch (IOException e) {
									e.printStackTrace();
								}
							}
						}else{
							return;
						}
					}
				});
				model.getPCS().addPropertyChangeListener("gameover", //response for when the game ends
						new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						if(connected){//don't do anything if you have no connection
							try {
								smos.gameOver(model.getHighestScorer());
							} catch (IOException e) {
								e.printStackTrace();
							}
						}else{
							return;
						}
					}
				});
			}
		}
		catch (IOException e) {
			if(master!=null){
				if(name!= null){
					synchronized(master.getLock()){
						try {
							mySocket.close();
							connected = false;
						} catch (IOException e1) {

							e1.printStackTrace();
						}
					}
				}
			}
		}
	}

	/**
	 * Sends the client information about the game.
	 * Listens to the client for requests, and responds accordingly.
	 */
	public void run(){
		System.out.println("I'm running, just like I always dreamed I one day would");
		if(model!= null){
			Object lock = master.getLock(); 
			while(!started){//wait until the game starts.
				synchronized(lock){ //functionality for before the game begins. 
					try {
						if(model==master.getModel()){//check if the master started a new game or not
							if(connected){
								System.out.println("waiting for players"+master.getPlayersLeft());//visibility
								smos.waitingForPlayers(master.getPlayersLeft());
								lock.wait();
							}else{
								lock.notifyAll();
							}
						}else{
							started = true;
							smos.waitingForPlayers((byte) 0);
						}
					}catch(InterruptedException e) {
						e.printStackTrace();
					}catch (IOException e) {
						try {
							mySocket.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						lock.notifyAll();
					}
				}
			}

			//Send out the Initial data
			try {
				smos.gameplayerUpdateMessage((byte) model.getPlayers().size(),model.getPlayers());
				smos.gamescoreUpdateMessage((byte) model.getPlayers().size(), model.getScores());	
				smos.gamefieldUpdateMessage((byte)model.numCardsLeft(), (byte)model.getNumCardsOnField(), model.getCardsInField());
			}catch(SocketException e){//graceful termination 
				connected = false;
				model.removePlayer(name,yourNumber);
				return;
			}catch(EOFException e){//graceful termination 
				connected = false;
				model.removePlayer(name,yourNumber);
				return;
			}catch (IOException e1) {
				model.removePlayer(name, yourNumber);
				System.out.println("We had a problem with an IO exception");
				e1.printStackTrace();
				return;
			}

			while(model.isGameRunning()){//while the game is running.
				try {
					byte[] message = smis.getInput().getBytes();
					for (int i=0; i<message.length; i++) {
						if (message[i] == 37) {//decrypt so 0s can be sent properly. The client will NEVER 
											   //send a 37 that isn't representing a 0
							message[i] = 0; 
						}
					}
					byte type = message[0];

					if(type == 3){//request for more cards
						System.out.println("we got the request for more cards");//visibility
						if(model.isGameRunning()){
							System.out.println("player " + yourNumber+" has requested more cards");//visibility
							model.drawCards(yourNumber);
						}else{
							System.out.println("Playtime's over");//visibility
							smos.gameOver(model.getHighestScorer());
						}
					}else if(type == 13){//player called set
						System.out.println("player " + yourNumber + " called set ;)"); //visibility
						model.callSet((int) message[1]);
					}else if(type == 15){//player called isSet
						System.out.println("calling isSet"); //visibility
						int result = model.isSet(message[2], message[3], message[4], message[1]);
						if (result == 0) {//valid set
							smos.validSet(yourNumber);
						}
						else {
							smos.invalidSet(yourNumber, (byte)result);//result would be the reason for invalid
						}
					}else{//so far, this has never been reached
						System.out.println("The message was bad: CRAP MESSAGE MADE IT THROUGH SOMEHOW"); 
					}
				}catch(SocketException e){//graceful termination 
					try {
						mySocket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					connected = false;
					model.removePlayer(name,yourNumber);
					return;
				}catch(EOFException e){//graceful termination 
					try {
						mySocket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					connected = false;
					model.removePlayer(name,yourNumber);
					return;
				}
				catch (IOException e) { //should never reach this point
					e.printStackTrace();
				}
			}
		}else{
			return; //the socket never connected, no need to do anything.
		}
	}
}
