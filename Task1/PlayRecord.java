/*
CO324-Network and Web Application
Voice communication between two parties
*/

import java.util.*;
import java.net.*;
import java.io.*;
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

public class PlayRecord extends P2P implements Runnable {

    private final int packetsize = 500;
    private final int port = 55001;

    public void run() {

        try {

            // Construct the socket
            DatagramSocket socket = new DatagramSocket(this.port);
            System.out.println("The server is ready...");

            // Create a packet
            DatagramPacket packet = new DatagramPacket(new byte[this.packetsize], (this.packetsize));
            this.playVoice();


            for (;;) {

                try {

                    // Receive a packet (blocking)
                    socket.receive(packet);
                  
                    // Print the packet
                    this.getSourceDataLine().write(packet.getData(), 0, this.packetsize); //playing the audio   

                } catch (Exception e) {

                    e.printStackTrace();

                }

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    
}
