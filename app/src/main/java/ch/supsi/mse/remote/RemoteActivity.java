package ch.supsi.mse.remote;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class RemoteActivity extends AppCompatActivity {

    private UUID MY_UUID = UUID.fromString("04c6093b-0000-1000-8000-00805f9b34fb");

    private Button left;
    private Button right;
    private Button close;

    private TextView status;

    private BluetoothSocket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote);

        Intent intent = getIntent();
        BluetoothDevice device = intent.getParcelableExtra("device");

        left = findViewById(R.id.remote_left);
        right = findViewById(R.id.remote_right);
        close = findViewById(R.id.remote_close);
        status = findViewById(R.id.remote_status);

        try {
            socket = device.createRfcommSocketToServiceRecord(MY_UUID);
            status.setText("Connecting...");
            new ConnectTask().execute();

        } catch (IOException e) {
            e.printStackTrace();
        }

        left.setOnClickListener(v -> sendLeft());
        right.setOnClickListener((view) -> sendRight());
        close.setOnClickListener(v -> {
            try {
                socket.close();
                status.setText("Closed");
                close.setEnabled(false);
                left.setEnabled(false);
                right.setEnabled(false);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


    }

    private void onConnected() {
        // enable buttons when connected
        status.setText("Connected");
        left.setEnabled(true);
        right.setEnabled(true);
        close.setEnabled(true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (close.isEnabled()) {

            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
                sendRight();
            else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
                sendLeft();
        }

        return super.onKeyDown(keyCode, event);
    }

    private void sendRight() {
        try {
            sendValue(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendLeft() {
        try {
            sendValue(2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendValue(int value) throws IOException {
        OutputStream outputStream = socket.getOutputStream();

        outputStream.write(value);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ConnectTask extends AsyncTask<Void, Void, Boolean> {


        @Override
        protected Boolean doInBackground(Void... voids) {

            try {
                socket.connect();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (aBoolean) {
                onConnected();
            } else
                status.setText("Error");
        }
    }

}
