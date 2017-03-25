package channels;

import filesystem.Chunk;
import filesystem.FileChunk;
import filesystem.FileManager;
import protocols.Protocol;
import server.Server;
import utils.Message;
import utils.Message.FieldIndex;

import java.io.IOException;
import java.net.DatagramPacket;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;

public class DataChannel extends Channel {

    public DataChannel(Server server, String addressStr, String portVar){
        super(server, addressStr,portVar);
    }

    @Override
    void handler() {

        DatagramPacket packet = new DatagramPacket(new byte[256], 256);

        try {
            socket.receive(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        while (!shutdown) {
            DatagramPacket packet = new DatagramPacket(new byte[Message.MAX_CHUNK_SIZE], Message.MAX_CHUNK_SIZE);
            try {
                this.socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Message message = new Message(packet);
            String[] headerFields = message.getHeaderFields();
            String body = message.getBody();

            System.out.println(message.getHeader());

            switch (headerFields[FieldIndex.MessageType]) {
                case Protocol.MessageType.Putchunk:
                    putChunk(headerFields, body);
                    break;
            }
        }
    }


    private void putChunk(String[] headerFields, String body) {
        String senderID = headerFields[FieldIndex.SenderId];
        String fileID = headerFields[FieldIndex.FileId];
        int chunkNo = Integer.parseInt(headerFields[FieldIndex.ChunkNo]);
        int replicationDegree = Integer.parseInt(headerFields[FieldIndex.ReplicationDeg]);

        //if (senderID.equals(server.getServerID()))
           // return;

        FileChunk file;
        if (FileManager.instance().hasFile(fileID))
            file = FileManager.instance().getFile(fileID);
        else
            file = new FileChunk(fileID);


        if (file.hasChunk(chunkNo)) {
            server.sendStored(fileID, chunkNo);
            return;
        }
        else {
            Chunk chunk = new Chunk(chunkNo, replicationDegree, body.getBytes(StandardCharsets.US_ASCII));
            file.addChunk(chunk);
            try {
                chunk.storeContent(fileID);
                server.sendStored(fileID,chunkNo);
            } catch (IOException e) { /* Do nothing */ }
        }
    }


    @Override
    public void send(Message msg) throws IOException {
        super.send(msg);

        new Timer().schedule(new TimerTask() {
            int tries = 1;
            String[] headerFields = msg.getHeaderFields();
            String fileId = headerFields[FieldIndex.FileId];
            String chunkNumber = headerFields[FieldIndex.ChunkNo];

            @Override
            public void run() {
                if (tries == 5 || FileManager.instance().chunkDegreeSatisfied(fileId, chunkNumber)) {
                    this.cancel();
                    return;
                }

                try {
                    DataChannel.super.send(msg);
                } catch (IOException e) {
                    tries--;        // TALVEZ? (exceção pode significar buffer cheio)
                }

                tries++;
            }
        }, 1000, 1000);
    }
}
