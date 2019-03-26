package ch.supsi.mse.remote;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.IOException;
import java.util.UUID;

public class Remote extends AppCompatActivity {

    private UUID MY_UUID = UUID.fromString("04c6093b-0000-1000-8000-00805f9b34fb");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote);

        Intent intent = getIntent();
        BluetoothDevice device = intent.getParcelableExtra("device");

        try {
            BluetoothSocket socket = device.createRfcommSocketToServiceRecord(MY_UUID);



        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
