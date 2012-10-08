package lessur.engine.net.packets;

import java.io.DataInputStream;
import java.io.IOException;

import lessur.engine.net.GamePacket;
import lessur.engine.net.LessurConnectedNode;

public class Packet255ConnectionRefused extends GamePacket {
	public String messageTitle = "";
	public String messageBody = "";
	
	
	public Packet255ConnectionRefused(LessurConnectedNode client) {
		super(255, client);
		try {
			DataInputStream i = new DataInputStream(client.getSocket().getInputStream());
			this.messageTitle = i.readUTF();
			this.messageBody = i.readUTF();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Packet255ConnectionRefused(LessurConnectedNode client, String messageTitle, String messageBody){
		super(255, client);
		this.messageTitle = messageTitle;
		this.messageBody = messageBody;
	}

	@Override
	public byte[] getData() throws IOException {
		o.writeUTF(messageTitle);
		o.writeUTF(messageBody);
		o.flush();
		return baos.toByteArray();
	}

}
