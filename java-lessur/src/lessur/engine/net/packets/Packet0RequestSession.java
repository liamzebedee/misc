package lessur.engine.net.packets;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import lessur.engine.net.GamePacket;
import lessur.engine.net.LessurConnectedNode;
import lessur.util.CryptUtil;
import lessur.util.Globals;

public class Packet0RequestSession extends GamePacket{
	public String username;
	public String password;
	public PublicKey publicKey;
	
	public Packet0RequestSession(LessurConnectedNode client) {
		super(0, client);
		try{
			DataInputStream i = new DataInputStream(client.getSocket().getInputStream());
			this.username = i.readUTF();
			this.password = i.readUTF(); // Encrypted using servers public key
		    this.publicKey = CryptUtil.readRSAPublicKey(i);
		    client.setPublicKey(publicKey);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Packet0RequestSession(String username, String password, PublicKey publicKey){
		super(0, null);
		this.username = username;
		this.password = password;
		this.publicKey = publicKey;
	}
	
	@Override
	public byte[] getData() throws IOException {
		o.writeUTF(username);
		byte[] encryptedPassword = CryptUtil.rsaEncrypt(Globals.getServerPublicKey(), this.password.getBytes());
		o.writeUTF(new String(encryptedPassword)); // TODO Change to write byte array
		CryptUtil.writeRSAPublicKey(this.publicKey, o);
			
		o.flush();
		return baos.toByteArray();
	}
	
}
