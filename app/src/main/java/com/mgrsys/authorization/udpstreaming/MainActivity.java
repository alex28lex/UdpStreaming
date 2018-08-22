package com.mgrsys.authorization.udpstreaming;

import android.media.AudioFormat;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MainActivity extends AppCompatActivity {

    private Button startBtn;
    private Button stopBtn;
    private EditText bufferText;
    private EditText addressText;
    private EditText portText;

    MulticastSocket multicastSocket;
    InetAddress group;
    Handler mainHandler;

    AudioFormat audioFormat;
    int sampleRate = 44100;


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


    void runMulticast() {
/*        new Thread() {
            @Override
            public void run() {*/
        try {
            byte[] buffer = new byte[3584];
            DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
            group = InetAddress.getByName(addressText.getText().toString());
            multicastSocket = new MulticastSocket(Integer.valueOf(portText.getText().toString()));
            multicastSocket.joinGroup(group);
            while (true) {
                multicastSocket.receive(datagramPacket);
                ByteArrayInputStream baiss = new ByteArrayInputStream(
                        datagramPacket.getData());

                String ip = datagramPacket.getAddress().getHostAddress();
                String message = new String(buffer, 0, datagramPacket.getLength());
                Log.d("MyApp", ip + ": " + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //}
        /*   }.start();*/
    }

    void stopMulticast() {
        try {
            multicastSocket.leaveGroup(group);
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
