/*
CO324-Network and Web Application
Voice communication between two parties
*/

import java.net.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

//to capture sound and send it to the other devices connected
public class CaptureSound extends P2P implements Runnable {

    private final int packetsize = 500;//packet size
    private final int port = 55000;//port number of sender 
    private InetAddress host = null;
    private DatagramSocket socket = null;//initiate datagram soket as null 
    private ByteArrayOutputStream byteArrayOutputStream = null;//write data into byte array
    private byte tempBuffer[] = new byte[this.packetsize];//create temporary buffer with the size of paketsize
    private boolean stopCapture = false;

public static void main(String[] args) {

        // Check the whether the arguments are given
        if (args.length != 1) {
            System.out.println("Usage: java <filename> <receiver's IP> ");
            return;
        }

/*used two threads because we want to different kind of works should be done parallel */
        try {

            Thread capture;
            Thread play;

            capture = new Thread(new  CaptureSound(InetAddress.getByName(args[0])));//capture sounds and transmit it to other device
            play = new Thread(new PlayRecord());//play record message

            capture.start();//start capturing
            play.start();//start play

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//After record voice and transmit it using a socket.
    private void Transfer() {
        this.byteArrayOutputStream = new ByteArrayOutputStream();//to write data into byte array
        this.stopCapture = false;
        try {
               int count;
            while (!this.stopCapture) {
                count = getTargetDataLine().read(this.tempBuffer, 0, this.tempBuffer.length);  //read incoming data pakkets and capture sound into tempBuffer

                if (count > 0) {
                    this.byteArrayOutputStream.write(this.tempBuffer, 0, count);//in this condition write data into a byte array

                    // Construct the datagram packet with the length of trmparory buffer length to specified port no. on the specified host 
                    DatagramPacket packet = new DatagramPacket(this.tempBuffer, this.tempBuffer.length, this.host,55001);
                    
                    // Send the packet to other device
                    this.socket.send(packet);
                }
            }
            this.byteArrayOutputStream.close();//close the stream(in this method we can close without getting IO exceptions)
        } catch (IOException e) {
            
            e.printStackTrace();
            
        }
    }

    public void run() {
        try {
            this.socket = new DatagramSocket(this.port);
            this.captureAudio();//starts to capture audio recordings using the default audio recording application in the device.It allows the device user to capture multiple recordings in a single session.
            this.Transfer();//capture sound and transmit 

        } catch (Exception e) {

            e.printStackTrace();

        } finally {
            this.socket.close();
        }
    }

    public CaptureSound(InetAddress host) {//pkts send to the particular host addr and it is delivered to the interface identified by that adrress
        this.host = host;
    }

    public  CaptureSound() {
        super();
    }
 
}
