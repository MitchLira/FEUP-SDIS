package metadata;


import java.io.Serializable;
import java.util.HashSet;

public class ChunkMetadata implements Serializable {
    private int chunkNo;
    private int repDegree;
    private HashSet<String> storeds;
    boolean onTransaction;

    public ChunkMetadata(int chunkNo, int repDegree) {
        this.chunkNo = chunkNo;
        this.repDegree = repDegree;
        this.storeds = new HashSet<>();
        onTransaction = false;
    }

    public synchronized void addReplication(String serverID) {
        if (!storeds.contains(serverID))
            storeds.add(serverID);
    }

    public boolean isOnTransaction() {
        return onTransaction;
    }

    public void startTransaction() {
        onTransaction = true;
    }

    public void stopTransaction() {
        onTransaction = false;
    }

    public int getChunkNo() {
        return chunkNo;
    }

    public int getRepDegree() {
        return repDegree;
    }

    public synchronized int getActualRepDegree() {
        return storeds.size();
    }

    @Override
    public String toString() {
        return "Chunk: nº"+chunkNo+", perceivedRepDegree: "+storeds.size();
    }
}
