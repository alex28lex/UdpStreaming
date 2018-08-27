package com.mgrsys.udpstreaming;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mgrsys.authorization.udpstreaming.R;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity {

    private Button startBtn;
    private Button stopBtn;
    private EditText bufferText;
    private EditText addressText;
    private EditText portText;

    MulticastSocket multicastSocket;
    InetAddress group;

    private AudioTrack track;
    private static final int SAMPLE_RATE = 48000; // Hertz
    private static final int BIT_RATE = 128000; // bps
    private static final int SAMPLE_INTERVAL = 20; // Milliseconds
    private static final int SAMPLE_SIZE = 2; // Bytes
    private static final int BUF_SIZE = SAMPLE_INTERVAL * SAMPLE_INTERVAL * SAMPLE_SIZE * 2; //Bytes

    private String MIME_TYPE = "audio/mp4a-latm";
    private MediaCodec decoder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startBtn = findViewById(R.id.startBtn);
        stopBtn = findViewById(R.id.stopBtn);
        bufferText = findViewById(R.id.buffer);
        addressText = findViewById(R.id.address);
        portText = findViewById(R.id.port);

        initListeners();


    }


    private void setDecoder() {
        MediaFormat mediaFormat = new MediaFormat();
        mediaFormat.setString(MediaFormat.KEY_MIME, MIME_TYPE);
        mediaFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);
        mediaFormat.setInteger(MediaFormat.KEY_SAMPLE_RATE, SAMPLE_RATE);
        mediaFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);

        //set decoder configure
/*
        int profile = MediaCodecInfo.CodecProfileLevel.AACObjectLC;  //AAC LC
        int freqIdx = FREQ_IDX;  //44.1KHz
        int chanCfg = 1;  //CPE
        ByteBuffer csd = ByteBuffer.allocate(2);
        csd.put(0, (byte) (profile << 3 | freqIdx >> 1));
        csd.put(1, (byte)((freqIdx & 0x01) << 7 | chanCfg << 3));
        mediaFormat.setByteBuffer("csd-0", csd);
*/

        try {
            decoder = MediaCodec.createDecoderByType(MIME_TYPE);
            decoder.configure(mediaFormat, null, null, 0);
            decoder.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void runMulticast() {
        new Thread() {
            @Override
            public void run() {
                try {

                    setDecoder();
                    byte[] buffer = new byte[BUF_SIZE];//mb 3584 better?


                    track = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE, AudioFormat.CHANNEL_IN_STEREO,
                            AudioFormat.ENCODING_PCM_16BIT, BUF_SIZE, AudioTrack.MODE_STREAM);
                    track.play();

                    // Define a socket to receive the audio
                    DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                    group = InetAddress.getByName(addressText.getText().toString());
                    multicastSocket = new MulticastSocket(Integer.valueOf(portText.getText().toString()));
                    multicastSocket.joinGroup(group);

                    while (true) {
                        multicastSocket.receive(datagramPacket);
                        track.write(datagramPacket.getData(), 0, BUF_SIZE);
                        String ip = datagramPacket.getAddress().getHostAddress();
                        String message = new String(buffer, 0, datagramPacket.getLength());
                        Log.d("MyApp", ip + ": " + message);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    void stopMulticast() {
        try {
            multicastSocket.leaveGroup(group);
            multicastSocket.disconnect();
            multicastSocket.close();
            track.stop();
            track.flush();
            track.release();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void initListeners() {
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runMulticast();
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopMulticast();
            }
        });
    }
}
