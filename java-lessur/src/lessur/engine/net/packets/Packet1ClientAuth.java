package lessur.engine.net.packets;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.text.Normalizer;
import java.util.logging.Logger;

import lessur.engine.net.GamePacket;
import lessur.engine.net.LessurConnectedNode;
import lessur.util.CryptUtil;
import lessur.util.NetUtil;

/**
 * Received by client ONLY
 * Sent by server ONLY
 */
/*
 * Sends to client the server's public key, and a session key encrypted with the clients public key
 */
public class Packet1ClientAuth extends GamePacket {
	
	public byte[] encryptedSessionKey;
	public byte[] sessionKey;
	public PublicKey clientPublicKey;
	public PublicKey serverPublicKey;
	
	public Packet1ClientAuth(LessurConnectedNode client, PrivateKey clientPrivateKey) {
		super(1, client);
		try {
			DataInputStream i = new DataInputStream(client.getSocket().getInputStream());
			this.encryptedSessionKey = NetUtil.readByteArray(i);
			this.sessionKey = CryptUtil.rsaDecrypt(clientPrivateKey, this.encryptedSessionKey);
			this.serverPublicKey = CryptUtil.readRSAPublicKey(i);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	/**
	 * @param client
	 * @param clientPublicKey For encrypting the session key
	 * @param serverPublicKey For use by the client for further encrypted communications between the client/server
	 * @param sessionKey Randomised key for further encrypted communications between the client/server for use with RC4
	 */
	public Packet1ClientAuth(LessurConnectedNode client, PublicKey clientPublicKey, PublicKey serverPublicKey, byte[] sessionKey){
		super(1, client);
		this.sessionKey = sessionKey;
		this.clientPublicKey = clientPublicKey;
		this.serverPublicKey = serverPublicKey;
		this.encryptedSessionKey = CryptUtil.rsaEncrypt(clientPublicKey, sessionKey);
	}
	
	/**
	 * Writes the session key, encrypted using the client's public key
	 * Writes the server public key
	 */
	@Override
	public byte[] getData() throws IOException {
		NetUtil.writeByteArray(o, encryptedSessionKey);
		CryptUtil.writeRSAPublicKey(this.serverPublicKey, o); // Writes to outputstream
		o.flush();
		return baos.toByteArray();
	}

}
