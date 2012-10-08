package lessur.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class NetUtil {
	public static void writeByteArray(DataOutputStream o, byte[] array){
		try {
			o.writeInt(array.length);
			o.write(array);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static byte[] readByteArray(DataInputStream i){
		try {
			byte[] array;
			int size = i.readInt();
			array = new byte[size];
			i.readFully(array, 0, size);
			return array;
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Reading byte array wasn't successful, returning null!");
		}
		return null;
	}
}
