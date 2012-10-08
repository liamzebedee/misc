package lessur.engine.net;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * A holder class for a packet sent by a Lessur server
 * @author liamzebedee
 *
 */
public abstract class GamePacket {
	public short packetID;
	public LessurConnectedNode sender;
	protected ByteArrayOutputStream baos = new ByteArrayOutputStream();
	protected DataOutputStream o = new DataOutputStream(baos);
	
	public GamePacket(int packetID, LessurConnectedNode c){
		this.packetID = (short) packetID;
		this.sender = c;
	}
		
	public abstract byte[] getData() throws IOException;
}
