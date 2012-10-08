package lessur.engine.net.packets;

import java.io.DataInputStream;
import java.io.IOException;

import lessur.engine.net.GamePacket;
import lessur.engine.net.LessurConnectedNode;

public class Packet3ClientConnected extends GamePacket {
	/*
	 * Alerts the client that they are authenticated with the server. 
	 */

	public Packet3ClientConnected(LessurConnectedNode c) {
		super(3, c);	
		try {
			DataInputStream i = new DataInputStream(c.getSocket().getInputStream());
			System.out.println(i.readUTF());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Packet3ClientConnected(LessurConnectedNode c, Object o){
		super(3, c);
	}

	@Override
	public byte[] getData() throws IOException {
		o.writeUTF("O HAI THAR!");
		o.flush();
		return baos.toByteArray();
	}

}
