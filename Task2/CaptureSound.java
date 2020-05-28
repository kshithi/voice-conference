/*
co324-Assignmnet
Multicast communication
*/

import java.net.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Scanner;


public class CaptureSound extends Multicast {

    private final int packetsize = 500;//packet size
    private final int port = 5353;//port number of sender 
    private MulticastSocket socket = null;//initiate datagram soket as null 
    private byte tempBuffer[] = new byte[this.packetsize];//create temporary buffer with the size of paketsize
    private boolean stopCapture = true;
    private InetAddress host = null;
 
    public CaptureSound(InetAddress host) {
        this.host = host;
    }

public static void main(String[] args) {

        // Check the whether the arguments are given
        if (args.length != 1) {
            System.out.println("Usage: java <filename> <receiver's IP> ");
            return;
        }
        
        CaptureSound capture = null;
        PlayRecord play = null;

        try {

            capture = new CaptureSound(InetAddress.getByName(args[0]));
            play = new PlayRecord(InetAddress.getByName(args[0]));

            capture.start();
            play.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
        
        Scanner in = new Scanner(System.in);
        boolean state = true; // playing
        
        while(true && (capture != null) && (play != null)){
            in.nextLine();
            if(state) {
                capture.stopCapture();
                play.startPlay();
                System.out.println("Start Playing");
                state = false;
            } else {
                play.stopPlay();
                capture.startCapture();
                System.out.println("Start Capturing");
                state = true;
            }
        } 
    }

    private void Transfer() {
        this.stopCapture = true;
        try {
            int count;
            while (true) {
                if (!this.stopCapture) {
                    count = getTargetDataLine().read(this.tempBuffer, 0, this.tempBuffer.length);  //capture sound into tempBuffer

                    if (count > 0) {

                        // Construct the datagram packet
                        DatagramPacket packet = new DatagramPacket(this.tempBuffer, this.tempBuffer.length, this.host, 55001);

                        // Send the packet
                        this.socket.send(packet);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void stopCapture(){
        this.stopCapture = true;
    }
    
    public void startCapture(){
        this.stopCapture = false;
    }

  

    public CaptureSound() {
        super();
    }

    public void run() {
        try {
            this.socket = new MulticastSocket();
            this.socket.joinGroup(this.host);
            this.captureAudio();
            this.Transfer();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.socket.close();
        }
    }

   
    
}
