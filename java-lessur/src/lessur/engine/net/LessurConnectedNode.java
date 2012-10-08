package lessur.engine.net;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.security.Key;
import java.security.PublicKey;

import net.rudp.ReliableSocket;

public class LessurConnectedNode {
	protected ReliableSocket socket;
	protected InetAddress address;
	protected int port;
	protected byte[] sessionKey;
	protected PublicKey publicKey;
    public enum ConnectionState { notConnected, requestingSession, sessionDeclined, authenticating, syncing, connected }
	ConnectionState connectionState = ConnectionState.notConnected;
    
	public LessurConnectedNode(ReliableSocket socket){
		this.socket = socket;
		InetSocketAddress sAddress = (InetSocketAddress) socket.getLocalSocketAddress();
		address = sAddress.getAddress();
		port = sAddress.getPort();
	}

	public ReliableSocket getSocket() {
		return socket;
	}
	
	public void setSessionKey(byte[] key){
		this.sessionKey = key;
	}
	
	public byte[] getSessionKey(){
		return this.sessionKey;
	}
	
	public void setPublicKey(PublicKey key){
		this.publicKey = key;
	}
	
	public Key getPublicKey(){
		return this.publicKey;
	}
	
	public String toString(){
		return address.getHostAddress()+":"+port;
	}

}
