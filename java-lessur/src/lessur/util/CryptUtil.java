package lessur.util;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;

/**
 * A class for accessing crypto functions
 * @author liamzebedee
 *
 */
public class CryptUtil {
	public static byte[] rsaEncrypt(PublicKey key, byte[] data) {
		try {
			 Cipher cipher = Cipher.getInstance("RSA");
			 cipher.init(Cipher.ENCRYPT_MODE, key);
			 byte[] cipherData = new byte[data.length];
			 cipherData = cipher.doFinal(data);
			 return cipherData;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("RSA Encrypt wasn't successful. Returning null");
		}
		return null;
	}
	
	public static byte[] rsaDecrypt(PrivateKey key, byte[] data) {
		try {
			 Cipher cipher = Cipher.getInstance("RSA");
			 cipher.init(Cipher.DECRYPT_MODE, key);
			 byte[] cipherData = new byte[data.length];
			 cipherData = cipher.doFinal(data);
			 return cipherData;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("RSA Decrypt wasn't successful. Returning null");
		}
		return null;
	}
	
	public static byte[] rc4Crypt(String key, byte[] data){
	    RC4 rc4 = new RC4(key);
	    byte[] result = rc4.rc4(data);
	    return result;
	}
	
	public static byte[] getMD5(byte[] data){
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		byte[] digest = md.digest(data);
		return digest;
	}
	
	public static void writeRSAPublicKey(PublicKey key, OutputStream os){
		try {
			KeyFactory fact = KeyFactory.getInstance("RSA");
			RSAPublicKeySpec pub = fact.getKeySpec(key, RSAPublicKeySpec.class);
			ObjectOutputStream o = new ObjectOutputStream(os);
			o.writeObject((BigInteger) pub.getModulus());
			o.writeObject((BigInteger) pub.getPublicExponent());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static PublicKey readRSAPublicKey(InputStream i){
		try {
			ObjectInputStream o;
			o = new ObjectInputStream(i);
			BigInteger m = (BigInteger) o.readObject();
			BigInteger e = (BigInteger) o.readObject();
			RSAPublicKeySpec keySpec = new RSAPublicKeySpec(m, e);
		    KeyFactory fact = KeyFactory.getInstance("RSA");
		    return fact.generatePublic(keySpec);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return null;
	}
}
