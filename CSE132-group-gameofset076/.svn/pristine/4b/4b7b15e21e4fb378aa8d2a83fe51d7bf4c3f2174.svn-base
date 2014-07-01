package lab5.view;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import lab5.model.SetGame;

public class NetworkView implements Runnable {
	SetGame model;
	ServerSocket ss;

	public NetworkView(SetGame model) throws IOException {
		this.model = model;
		ss = new ServerSocket(10501);
	}

	public void run() {
		Socket s = null;
		try {
			s = ss.accept();
			DataInputStream dis = new DataInputStream(s.getInputStream());
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			model.addPlayer("Fred");
			while (true) {
				// read commands and return random response
				char c = (char)dis.readByte();
				while (c != '!') {
					c = (char)dis.readByte();
				}
				int type = dis.readByte();
				int length = dis.readShort();
				char[] payload = new char[length];
				String payloadString = "";
				for (int i=0; i<length; i++) {
					c = (char)dis.readByte();
					payload[i] = c;
					payloadString+= c;
				}
				if (type == 0) {			// hello
					model.addPlayer(payloadString);
					startMsg(dos, 1, 2);			//send type 1 msg
					dos.writeByte(0);				//game version
					dos.writeByte((byte)model.getPlayers().size()); 				//player ID
				} else if (type == 13) {	// player calls set
					startMsg(dos, 14, 1);			//send type 14 msg
					dos.writeByte(1);				//player ID
				} else if (type == 15) {	// set call
					startMsg(dos, 17, 1);			//send type 17 msg
					dos.writeByte(1);				//player ID
				} else {					// anything else
					startMsg(dos, 18, 2);			//send type 18 msg
					dos.writeByte(1);				//player ID
					dos.writeByte(2);				//reason == NOTASET
				}
			}
		} catch (EOFException e) {
			// graceful termination on EOF
		} catch (IOException e) {
			System.out.println("Remote connection reset");
		}
	}

	private void startMsg(DataOutputStream s, int type, int length) throws IOException {
		s.writeByte((int)'!');	//msg delimiter
		s.writeByte(type);		//msg type
		s.writeShort(length);	//payload length (in bytes)
	}
}
