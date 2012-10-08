package lessur.engine.net;

public interface INetServerEventListener {
    public abstract void dataReceived(LessurConnectedNode node); 
	public abstract void packetReceived();
	public abstract void packetSent(GamePacket packet);
	public abstract void clientConnect(LessurConnectedNode node);
	public abstract void clientRequestSession(LessurConnectedNode node);
	public abstract void clientTryAuth(LessurConnectedNode node);
	public abstract void playerConnected();
	public abstract void playerDownloadData();
}
