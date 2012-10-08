package lessur.engine.net;

import java.net.*;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.logging.Logger;

import java.io.*;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;
import org.newdawn.slick.SlickException;

import net.rudp.ReliableServerSocket;
import net.rudp.ReliableSocket;
import net.rudp.ReliableSocketListener;
import net.rudp.ReliableSocketStateListener;

import lessur.engine.GameManager;
import lessur.engine.GameManagerServer;
import lessur.engine.LessurMap;
import lessur.engine.enitity.EntityManager;
import lessur.engine.net.LessurConnectedNode.ConnectionState;
import lessur.engine.physics.WorldManager;
import lessur.util.CryptUtil;
import lessur.util.DStore;
import lessur.util.DStore.DataFile;

/*
 * A server for hosting Lessur games
 * Thread Architecture - 
 * - LessurServer (Main Running Thread)
 * -- ServerReceiverThread
 * --- 
 * -- ServerSenderThread
 * --- PacketSenderThread
 */
public class LessurServer implements Runnable {
	protected ReliableServerSocket socket;
	protected GameManagerServer gameManager;
	protected boolean serverRunning = false;
	protected DStore data;
	protected ArrayList<LessurConnectedClientNode> clientList = new ArrayList<LessurConnectedClientNode>(); 
	protected PublicKey publicKey;
	protected PrivateKey privateKey;
	protected SecureRandom random = new SecureRandom();
	protected CryptUtil cryptUtil = new CryptUtil();
	protected ArrayList<INetServerEventListener> listeners = new ArrayList<INetServerEventListener>();
	public int packetsSent = 0;
	public int packetsReceived = 0;


	public LessurServer(InetAddress address, int port){
		KeyPairGenerator kpg;
		try {
			kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(2048);
			KeyPair kp = kpg.genKeyPair();
			publicKey = kp.getPublic();
			privateKey = kp.getPrivate();
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}
		LessurMap map; 
		World physicsWorld;
		WorldManager physics;
		EntityManager entities;
		BasicServerEventListener basicEventListener = new BasicServerEventListener(this);
		this.registerListener(basicEventListener);
		try {
			socket = new ReliableServerSocket(port, 9001, address);
		} catch (IOException e) {
			e.printStackTrace();
		}

		data = new DStore(System.getProperty("user.dir")+System.getProperty("file.separator")+"res"+System.getProperty("file.separator")+"server"+System.getProperty("file.separator"));
		data.doFile("server.properties", DataFile.property);

		gameManager = new GameManagerServer();
		/*
		try {
			map = new LessurMap("res/"+data.getFile("server.properties").get("map"));
			game.setMap(map);
		} catch (SlickException e) {
			e.printStackTrace();
		}*/

		Vec2 physicsWorldGravity = new Vec2(0,0);
		physicsWorld = new World(physicsWorldGravity, true);
		gameManager.worldManager = new WorldManager(physicsWorld);

		gameManager.entityManager = new EntityManager(gameManager);
		ServerConnectionListener connectionListener = new ServerConnectionListener(this);
		Thread connectionListenerThread = new Thread(connectionListener);
		connectionListenerThread.start();
		serverRunning = true;
		InetSocketAddress serverAddress = (InetSocketAddress) this.socket.getLocalSocketAddress();
		Logger.getLogger("LessurServer").info("Server Started on "+serverAddress.getHostName()+":"+serverAddress.getPort());
	}


	@Override
	public void run() {
		// Game loop
		long lastTime, currentTime, deltaPrecise;
		int delta;
		currentTime = System.nanoTime();
		while (true) {
			lastTime = currentTime;
			currentTime = System.nanoTime();
			deltaPrecise = (currentTime - lastTime);
			delta = (int) (deltaPrecise / 1000 / 1000);
			// delta / 1000 / 1000 - the number of milliseconds since the last update
			if (!serverRunning){
				shutdown();
				break;
			}
			// TODO logic, update
			gameManager.worldManager.update(delta);
			gameManager.entityManager.update(delta);
		}
	}

	public void receivePacket(LessurConnectedClientNode client){
		client.addReceiveEvent();
		for(INetServerEventListener listener : this.listeners){
			listener.dataReceived(client);
		}
	}

	public void sendPacket(GamePacket packet) {
		((LessurConnectedClientNode) packet.sender).addSendEvent(packet);
		for(INetServerEventListener listener : this.listeners){
			listener.packetSent(packet);
		}
	}

	public void shutdown(){
		// TODO
	}

	public ReliableServerSocket getSocket(){
		return this.socket;
	}

	public void registerListener(INetServerEventListener serverEventListener){
		this.listeners.add(serverEventListener);
	}

	public LessurConnectedClientNode matchClient(LessurConnectedNode node){
		for(LessurConnectedClientNode client : this.clientList){
			if(node.socket.equals(client.socket)){
				return client;
			}
		}
		return null;
	}
	
	public ArrayList<LessurConnectedClientNode> getAllClients(){
		return this.clientList;
	}

}



class BasicServerEventListener implements INetServerEventListener {
	LessurServer server;

	public BasicServerEventListener(LessurServer server){
		this.server = server;
	}

	@Override
	public void packetReceived() {
		server.packetsReceived++;
	}

	@Override
	public void packetSent(GamePacket packet) {
		server.packetsSent++;
	}

	@Override
	public void clientConnect(LessurConnectedNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clientRequestSession(LessurConnectedNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clientTryAuth(LessurConnectedNode node) {
		// TODO Auto-generated method stub

	}

	@Override
	public void playerConnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void playerDownloadData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dataReceived(LessurConnectedNode node) {
		// TODO Auto-generated method stub

	}

}

class ServerConnectionListener implements Runnable {

	/*
	 * For use by the server
	 * Listens for connections from clients
	 */

	protected LessurServer server;

	public ServerConnectionListener(LessurServer server){
		this.server = server;
	}

	@Override
	public void run() {
		while(true){
			try {
				ReliableSocket clientSocket = server.socket.accept();
				InetSocketAddress clientAddress = (InetSocketAddress) clientSocket.getRemoteSocketAddress();
				Logger.getLogger("ServerConnectionListener").info("Processing new connection from "+
						clientAddress.getHostName()+":"+clientAddress.getPort()+" ...");
				LessurConnectedClientNode client = new LessurConnectedClientNode(clientSocket, server);
				Thread clientThread = new Thread(client);
				clientThread.start();
				ClientCommunicationSocketListener listener = new ClientCommunicationSocketListener(server, client);
				clientSocket.addListener(listener);
				client.connectionState = ConnectionState.notConnected;
				server.clientList.add(client);
				Logger.getLogger("ServerConnectionListener").info(
						"Connection from "+client.toString()+" has been processed successfully!");
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0); // TODO
			}
		}
	}

}

class ClientCommunicationSocketListener implements ReliableSocketListener, ReliableSocketStateListener {
	LessurConnectedClientNode client;
	LessurServer server;

	public ClientCommunicationSocketListener(LessurServer server, LessurConnectedClientNode client){
		this.server = server;
		this.client = client;
	}

	@Override
	public void packetReceivedInOrder() {
		server.receivePacket(client);
	}

	@Override
	public void packetReceivedOutOfOrder() {
		// server.receivePacket(client);
	}

	@Override
	public void packetSent() { }

	@Override
	public void packetRetransmitted() {}

	@Override
	public void connectionOpened(ReliableSocket sock) {

	}

	@Override
	public void connectionRefused(ReliableSocket sock) {

	}

	@Override
	public void connectionClosed(ReliableSocket sock) {

	}

	@Override
	public void connectionFailure(ReliableSocket sock) {

	}

	@Override
	public void connectionReset(ReliableSocket sock) {

	}
}
