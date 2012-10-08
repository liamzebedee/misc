package lessur.engine.net.packets;

import java.io.IOException;

import lessur.engine.net.GamePacket;
import lessur.engine.net.LessurConnectedNode;

public class PacketTemplate extends GamePacket {

	public PacketTemplate(LessurConnectedNode c) {
		super(-1, c);	
	}
	
	public PacketTemplate(){
		super(-1, null);
	}

	@Override
	public byte[] getData() throws IOException {
		return baos.toByteArray();
	}

}
