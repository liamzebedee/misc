package lessur.engine;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import lessur.engine.net.LessurClient;
import lessur.engine.net.LessurServer;

public class ServerTestState {
	public static void main(String [] args)	{
		LessurServer server = null;
		try {
			server = new LessurServer(java.net.InetAddress.getLocalHost(), 1234);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
        Thread t = new Thread(server);
        t.start();
        
        LessurClient client = new LessurClient();
    	InetSocketAddress a = (InetSocketAddress) server.getSocket().getLocalSocketAddress();
        client.connect(a.getAddress(), a.getPort());
        try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        System.out.println("Client: PR-"+client.packetsReceived+" PS-"+client.packetsSent);
        System.out.println("Server: PR-"+server.packetsReceived+" PS-"+server.packetsSent);
        
	}
}
