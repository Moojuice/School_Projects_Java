package lab5;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import lab5.view.GameQueuebe;
import lab5.view.ServantThread;

/*
 * Grade: 205/200
 * TA: RM
 *
 * Client: 100/100
 *    Cover Page: 15/15
 *    Sending and Receiving Messages: 40/40
 *    GUI: 35/35
 *    Erroneous message handling: 10/10
 *
 * Server: 105/100
 *   Multi-user: 40/40
 *      Fair for all users: 10/10
 *   Model: 30/30
 *   Win condition/game consistency: 15/20 Didn't seem to indicate a game end when no possible sets were left.
 *   Erroneous message handling: 10/10
 *   Extra features (extra credit): 5/5
 *
 * Deductions: 0/20
 *    Missing Javadoc: 0/-10
 *    Commented out code: 0/-5
 *    FIXME comments: 0/-2
 */

/**NewServerMain
 *  Listens for clients, and assigns them a servantThread.
 *  Use this class.
 * 
 *  Author: Jeremy Tang, Edward Xie, Ben Epstein, Kyle Liu
 *  Course: CSE 132
 *  Lab:    5
 */
public class NewServerMain implements Runnable {
	ServerSocket ss;
	GameQueuebe[] gameMasters;
	
	/*
	 * Initialize the queues and server socket
	 */
	public NewServerMain() throws IOException{
		ss = new ServerSocket(10501);
		gameMasters = new GameQueuebe[4];
		gameMasters[0] = new GameQueuebe(1);
		gameMasters[1] = new GameQueuebe(2);
		gameMasters[2] = new GameQueuebe(3);
		gameMasters[3] = new GameQueuebe(4);
		System.out.println("Queues are done");//lets you know server is ready
	}

	/**
	 * Run the server and makes ServantThreads for each client it receives
	 */
	@Override
	public void run() {
		while (true){//Assuming proper hardware, this can run as many games and talk with as many clients as you want.
			try{
				Socket s = ss.accept();
				System.out.println("got a new client!"); //visibility
				ServantThread servant = new ServantThread(gameMasters, s);
				servant.start(); //and now the servantThread handles the client
			}catch(IOException e){
				e.printStackTrace();
			}
		}

	}
	public static void main(String[] args) throws IOException{
		NewServerMain m = new NewServerMain();
		m.run();
	}

}
