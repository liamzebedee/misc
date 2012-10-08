package lessur.engine.net;

public interface INetClientEventListener {
	public abstract void dataReceived(LessurConnectedNode node); 
	public abstract void packetReceived();
	public abstract void packetSent(GamePacket packet);
	public abstract void clientConnect(LessurConnectedNode node);
	public abstract void clientConnected(LessurConnectedNode node);
}
