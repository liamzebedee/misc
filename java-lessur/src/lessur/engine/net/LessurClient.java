package lessur.engine.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.*;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import lessur.engine.net.LessurConnectedNode.ConnectionState;
import lessur.engine.net.packets.*;

import net.rudp.ReliableSocket;
import net.rudp.ReliableSocketListener;

public class LessurClient {
	public LessurConnectedNode node = null;
	protected ClientSenderThread sender;
	protected ClientReceiverThread receiver;
	public PublicKey publicKey;
	public PrivateKey privateKey;
	public PublicKey serverPublicKey;
	public int packetsSent = 0;
	public int packetsReceived = 0;
	public ConnectionState connectionState;
	protected ArrayList<INetClientEventListener> listeners = new ArrayList<INetClientEventListener>();

	public LessurClient(){
		try {
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(2048);
			KeyPair kp = kpg.genKeyPair();
			this.publicKey = kp.getPublic();
			this.privateKey = kp.getPrivate();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void connect(InetAddress address, int port){
		Logger.getLogger("LessurClient").info("Trying to connect to server "+
				address.toString()+":"+
				port);
		BasicClientEventListener basicEventListener = new BasicClientEventListener(this);
		this.registerListener(basicEventListener);
		try {
			ReliableSocket socket = new ReliableSocket(address, port, InetAddress.getLocalHost(), 1235); // TODO port must be random
			node = new LessurConnectedNode(socket);
			ServerCommunicationListener listener = new ServerCommunicationListener(this);
			this.node.getSocket().addListener(listener);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		Logger.getLogger("LessurClient").info("Connected to server "+address.getHostAddress()+":"+port);


		sender = new ClientSenderThread(this);
		receiver = new ClientReceiverThread(this);
		Thread sendThread = new Thread(sender);
		Thread receiveThread = new Thread(receiver);
		sendThread.start();
		receiveThread.start();

		Packet0RequestSession packet0 = new Packet0RequestSession("liamzebedee", "123", this.publicKey);
		sendPacket(packet0);
		connectionState = ConnectionState.requestingSession;
	}

	public void sendPacket(GamePacket packet){
		sender.sendQueue.add(packet);
		for(INetClientEventListener listener : this.listeners){
			listener.packetSent(packet);
		}
	}

	public void receivePacket(){
		try {
			receiver.receiveQueue.put(0);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		for(INetClientEventListener listener : this.listeners){
			listener.dataReceived(this.node);
		}
	}


	public void registerListener(INetClientEventListener clientEventListener){
		this.listeners.add(clientEventListener);
	}

	// Managed Send Functions
	public void playerMove(){

	}

}

class ClientSenderThread implements Runnable {
	private LessurClient client;
	protected LinkedBlockingQueue<GamePacket> sendQueue = new LinkedBlockingQueue<GamePacket>();

	public ClientSenderThread(LessurClient client){
		this.client = client;
	}

	@Override
	public void run() {
		while(true) {
			try {
				sendPacket(sendQueue.take());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void sendPacket(GamePacket packet){
		try {
			Logger.getLogger("LessurClient").info("Sending packet "+packet.packetID);
			OutputStream o = client.node.getSocket().getOutputStream();
			DataOutputStream d = new DataOutputStream(o);
			d.writeShort(packet.packetID);
			d.write(packet.getData());
			d.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

class ClientReceiverThread implements Runnable {
	private LessurClient client;
	protected LinkedBlockingQueue<Integer> receiveQueue = new LinkedBlockingQueue<Integer>();

	public ClientReceiverThread(LessurClient client){
		this.client = client;
	}

	@Override
	public void run() {
		while(true){
			try {
				receiveQueue.take();
				receivePacket();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void receivePacket(){
		for(INetClientEventListener listener : client.listeners){
			listener.packetReceived();
		}
		short packetID = -1;
		try {
			DataInputStream i = new DataInputStream(client.node.getSocket().getInputStream());
			packetID = i.readShort();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Logger.getLogger("LessurClient").info("Packet "+packetID+" received");
		switch(packetID){				
		case 1:
			Packet1ClientAuth packet1 = new Packet1ClientAuth(client.node, client.privateKey);
			client.serverPublicKey = packet1.serverPublicKey;
			Packet2ClientProveAuth packet2 = new Packet2ClientProveAuth(packet1.sessionKey, client.serverPublicKey);
			client.sendPacket(packet2);
			client.connectionState = ConnectionState.authenticating;
			break;
		case 3:
			Packet3ClientConnected packet3 = new Packet3ClientConnected(client.node);
			client.connectionState = ConnectionState.connected;
			Logger.getLogger("LessurClient").info("Authenticated with server!");
			break;
		}

	}

}

class PacketProcessEventThread implements Runnable {
	public enum PacketProcessEvent { send, receive };
	PacketProcessEvent event;
	LessurServer server;
	LessurClient client;

	public PacketProcessEventThread(LessurServer server, GamePacket packet){

	}

	public PacketProcessEventThread(LessurServer server){

	}

	@Override
	public void run() {
		switch(event){
		case send:

			break;
		case receive:

			break;
		}
	}

}

class BasicClientEventListener implements INetClientEventListener {
	LessurClient client;

	public BasicClientEventListener(LessurClient client){
		this.client = client;
	}
	@Override
	public void dataReceived(LessurConnectedNode node) {
		// TODO Auto-generated method stub

	}
	@Override
	public void packetReceived() {
		client.packetsReceived++;
	}
	@Override
	public void packetSent(GamePacket packet) {
		client.packetsSent++;
	}
	@Override
	public void clientConnect(LessurConnectedNode node) {
		// TODO Auto-generated method stub

	}
	@Override
	public void clientConnected(LessurConnectedNode node) {
		// TODO Auto-generated method stub

	}

}

class ServerCommunicationListener implements ReliableSocketListener {
	/*
	 * For use by the client
	 * Listens for communication from the server
	 */
	protected LessurClient client;

	public ServerCommunicationListener(LessurClient client){
		this.client = client;
	}

	@Override
	public void packetReceivedInOrder() {
		client.receivePacket();
	}

	@Override
	public void packetReceivedOutOfOrder() {
		// client.receivePacket();
	}

	@Override
	public void packetSent() {}

	@Override
	public void packetRetransmitted() {}
}
