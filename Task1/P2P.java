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

public class P2P {

    private AudioFormat audioFormat;
    private TargetDataLine targetDataLine;
    private AudioInputStream audioInputStream;
    private SourceDataLine sourceDataLine;

    public AudioFormat getAudioFormat() {//to specifies the data that can be read from the line(every data line has audio data format with associate with its data format)
        float sampleRate = 16000.0F;
        int sampleSizeInBits = 16;
        int channels = 2;
        boolean signed = true;
        boolean bigEndian = true;
        return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);//Constructs an AudioFormat with a linear PCM encoding and the given parameters
    }

    public synchronized SourceDataLine getSourceDataLine() {//allow program to write data
        return this.sourceDataLine;
    }

    public synchronized TargetDataLine getTargetDataLine() {
        return this.targetDataLine;
    }

    public void playVoice() {
        try {
            audioFormat = getAudioFormat();     //get the audio format

            DataLine.Info dataLineInfo1 = new DataLine.Info(SourceDataLine.class, audioFormat);
            sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo1);
            sourceDataLine.open(audioFormat);
            sourceDataLine.start();

//            //Setting the maximum volume
            FloatControl floatControl = (FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
            floatControl.setValue(floatControl.getMaximum()/2);

        } catch (LineUnavailableException e) {
            e.printStackTrace();
            System.exit(0);
        }

    }
    
    public synchronized void  captureAudio() {
        try {
           Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();    //get available mixers
            Mixer mixer = null;
            for (int cnt = 0; cnt < mixerInfo.length; cnt++) {
                mixer = AudioSystem.getMixer(mixerInfo[cnt]);
                
                Line.Info[] lineInfos = mixer.getTargetLineInfo(); 
                if (lineInfos.length >= 1 && lineInfos[0].getLineClass().equals(TargetDataLine.class)) {
                    System.out.println("Welcome!");
                    System.out.println("Start Speaking...");
                    break;
                }
            }

            audioFormat = getAudioFormat();     //get the audio format
            DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);

            targetDataLine = (TargetDataLine) mixer.getLine(dataLineInfo);
            targetDataLine.open(audioFormat);
            targetDataLine.start();

        } catch (LineUnavailableException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
