package lessur.engine.net.packets;

import java.io.DataInputStream;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;

import lessur.engine.net.GamePacket;
import lessur.engine.net.LessurConnectedNode;
import lessur.util.CryptUtil;
import lessur.util.NetUtil;

public class Packet2ClientProveAuth extends GamePacket {
	/**
	 * Received by server ONLY
	 * Sent by client ONLY
	 */
	/* Client decrypts session key using private key, and using the session key and RC4 cipher, it encrypts the hash of the
	session key and sends it to the server. This is done as to protect against a MITM attack, by proving the 
	clients authenticity, the client would be able to decrypt the message, because only they have their private key
	*/
	
	public byte[] symmetricKey;
	public byte[] encryptedSymmetricKey;
	public byte[] hashedSymmetricKey;
	
	/**
	 * @param c
	 * @param serverPrivateKey For use with decrypting the hashed session key
	 */
	public Packet2ClientProveAuth(LessurConnectedNode c, PrivateKey serverPrivateKey) {
		super(2, c);
		try {
			DataInputStream	i = new DataInputStream(c.getSocket().getInputStream());
			this.encryptedSymmetricKey = NetUtil.readByteArray(i);
			this.hashedSymmetricKey = CryptUtil.rsaDecrypt(serverPrivateKey, this.encryptedSymmetricKey);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param symmetricKey The session key
	 * @param serverPublicKey For use with encrypting the hashed session key
	 */
	public Packet2ClientProveAuth(byte[] symmetricKey, PublicKey serverPublicKey){
		super(2, null);
		this.symmetricKey = symmetricKey;
		this.hashedSymmetricKey = CryptUtil.getMD5(symmetricKey);
		this.encryptedSymmetricKey = CryptUtil.rsaEncrypt(serverPublicKey, this.hashedSymmetricKey);
	}

	@Override
	public byte[] getData() throws IOException {
		NetUtil.writeByteArray(o, encryptedSymmetricKey);
		o.flush();
		return baos.toByteArray();
	}

}
