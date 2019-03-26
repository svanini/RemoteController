package ch.supsi.mse.remote;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int ENABLE_BLUETOOTH_REQ = 1;
    private static final int PRMISSION_LOCATION_REQUEST = 2;
    private BluetoothAdapter mBluetoothAdapter;

    private List<BluetoothDevice> devices;
    BroadcastReceiver bluetoothReceiver;
    DevicesAdapter devicesAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.devices = new ArrayList<>();
        this.devicesAdapter = new DevicesAdapter(this, devices);

        ListView devicesList = findViewById(R.id.list_devices);
        devicesList.setAdapter(devicesAdapter);

        if (mBluetoothAdapter == null) {
            // non supporta bluetooth
            return;
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetoothIntent, ENABLE_BLUETOOTH_REQ);
        } else initBluetoothUI();



        bluetoothReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device.getName() != null && !device.getName().equals("") && !devices.contains(device)) {
                        devices.add(device);
                        devicesAdapter.notifyDataSetChanged();
                    }
                }
            }
        };

        // dichiara intent filter per scoltare quando un nuovo dispositivo è trovato
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(bluetoothReceiver, filter);

        devicesList.setOnItemClickListener((parent, view, position, id) -> {
            BluetoothDevice device = devices.get(position);

            Intent intent = new Intent(getBaseContext(), RemoteActivity.class);
            intent.putExtra("device", device);
            startActivity(intent);
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(bluetoothReceiver);
    }

    private void initBluetoothUI() {
        // dispositivi già accoppiati
        devices.addAll(mBluetoothAdapter.getBondedDevices());
        devicesAdapter.notifyDataSetChanged();


        //prima della discovery mi assicuro di averne i permessi
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // devo chiedere i permessi all'utente

            if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        PRMISSION_LOCATION_REQUEST);
            }
        } else {
            // in questo caso ho i permessi
            if (!mBluetoothAdapter.startDiscovery()) {
                // discovery andata in errore
                Log.e("Discovery", "failed");
            }
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PRMISSION_LOCATION_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!mBluetoothAdapter.startDiscovery()) {
                        Log.e("Discovery", "failed");
                    }
                }
            }

        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ENABLE_BLUETOOTH_REQ) {
            if (resultCode == RESULT_OK) {
                initBluetoothUI();
            }
        }
    }


}
