package lessur.engine.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import lessur.engine.net.packets.Packet0RequestSession;
import lessur.engine.net.packets.Packet1ClientAuth;
import lessur.engine.net.packets.Packet255ConnectionRefused;
import lessur.engine.net.packets.Packet2ClientProveAuth;
import lessur.engine.net.packets.Packet3ClientConnected;
import lessur.util.CryptUtil;
import net.rudp.ReliableSocket;

public class LessurConnectedClientNode extends LessurConnectedNode implements Runnable {
	protected LinkedBlockingQueue<Integer> receiveQueue = new LinkedBlockingQueue<Integer>();
	public LessurServer server;

	public LessurConnectedClientNode(ReliableSocket socket, LessurServer server) {
		super(socket);
		this.server = server;
	}

	@Override
	public void run() {
		while(true) {
			try {
				receiveQueue.take();
				receivePacket();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void addSendEvent(GamePacket packet) {
		// Each packet is sent in its own thread, since the ordering of the packets doesn't matter
		PacketSenderThread packetSenderThread = new PacketSenderThread(packet);
		Thread t = new Thread(packetSenderThread);
		t.start();
	}

	public void addReceiveEvent() {
		try {
			this.receiveQueue.put(0);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void receivePacket(){
		short packetID = -1;
		try {
			DataInputStream i = new DataInputStream(this.getSocket().getInputStream());
			packetID = i.readShort();
		} catch (IOException e) {
			Logger.getLogger("LessurServer").log(Level.WARNING, "Cannot read packet ID from client "+this.toString());
			e.printStackTrace();
			return;
		}
		Logger.getLogger("LessurServer").info("Packet "+packetID+" received");
		switch(packetID){
		case 0:
			this.connectionState = ConnectionState.requestingSession;
			Packet0RequestSession packet0 = new Packet0RequestSession(this);
			// Check if the client is premium user
			// TODO
			boolean clientIsPremium = true;
			if(clientIsPremium){
				this.connectionState = ConnectionState.authenticating;
				byte[] sessionKey = new BigInteger(128, server.random).toByteArray();
				this.setSessionKey(sessionKey);
				this.setPublicKey(packet0.publicKey);
				Packet1ClientAuth packet1 = new Packet1ClientAuth(this, packet0.publicKey,
						server.publicKey, sessionKey);
				Logger.getLogger("LessurServer").info("Client is premium");
				server.sendPacket(packet1);
			}
			else {
				Logger.getLogger("LessurServer").info("Refusing client "+this.toString()+" connection, USER NOT PREMIUM");
				Packet255ConnectionRefused packet255 = 
						new Packet255ConnectionRefused(this, "Connection Refused", "User not premium");
				server.sendPacket(packet255);
			}
			break;
		case 2:
			if(this.connectionState != ConnectionState.authenticating){
				Logger.getLogger("LessurServer").warning("Received packet from client "+
						this.toString()+" before authenticated. Ignoring packet!");
				break;
			}
			Packet2ClientProveAuth packet2 = new Packet2ClientProveAuth(this, server.privateKey);
			byte[] hashedSessionKey = CryptUtil.getMD5(packet2.sender.getSessionKey());
			if(Arrays.equals(hashedSessionKey, packet2.hashedSymmetricKey)){
				packet2.sender.connectionState = ConnectionState.connected;
				Packet3ClientConnected packet3 = new Packet3ClientConnected(packet2.sender,null);
				server.sendPacket(packet3);
			}
			else{
				Logger.getLogger("LessurServer").warning("Client "+packet2.sender.toString()+" couldn't decrypt session key!");
			}
			break;
		case 4:
			// Decrypt Packet
			// Check if client is authenticated
			break;
		}
	}

}

/*class ConnectedClientNodeEvent {
	public enum EventType { SEND, RECEIVE }
	public EventType eventType;
	public GamePacket packet;

	public ConnectedClientNodeEvent(EventType type){
		this.eventType = EventType.RECEIVE;
	}

	public ConnectedClientNodeEvent(EventType type, GamePacket packet){
		this.packet	= packet;
		this.eventType = EventType.SEND;
	}
}*/

class PacketSenderThread implements Runnable {
	GamePacket packet;

	public PacketSenderThread(GamePacket packet){
		this.packet = packet;
	}

	@Override
	public void run() {
		try {
			Logger.getLogger("LessurServer").info("Sending packet "+packet.packetID);
			OutputStream o = packet.sender.getSocket().getOutputStream();
			DataOutputStream d = new DataOutputStream(o);
			d.writeShort(packet.packetID);
			d.write(packet.getData());
			d.flush();
		} catch (IOException e) {
			Logger.getLogger("LessurServer").log(Level.INFO, "Cannot send packet to client "+this.toString());
			e.printStackTrace();
		}
	}
}