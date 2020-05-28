/*
co324-Assignmnet
Multicast communication
*/

import java.net.*;

public class PlayRecord extends Multicast {

    private final int packetsize = 500;
    private final int port = 5354;
    private boolean stopPlay = false;
    private MulticastSocket socket = null;
    private InetAddress host = null;

    @Override
    public void run() {

        try {
            // Construct the socket
            this.socket = new MulticastSocket(this.port);
            this.socket.joinGroup(this.host);
            System.out.println("The server is ready...");

            // Create a packet
            DatagramPacket packet = new DatagramPacket(new byte[this.packetsize], (this.packetsize));
            this.playAudio();

            while (true) {
                if (!this.stopPlay) {
                    try {

                        // Receive a packet (blocking)
                        this.socket.receive(packet);

                        // Print the packet
                        this.getSourceDataLine().write(packet.getData(), 0, this.packetsize); //playing the audio  
                        
                        packet.setLength(this.packetsize);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.socket.close();
        }
    }
    
    public PlayRecord(InetAddress host) {
        this.host = host;
    }
    
    public void stopPlay() {
        this.stopPlay = true;
    }
    
    public void startPlay() {
        this.stopPlay = false;
    }
}
