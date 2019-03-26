package ch.supsi.mse.remote;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.drawable.AnimatedStateListDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Set;

public class DevicesAdapter extends ArrayAdapter<BluetoothDevice> {


    public DevicesAdapter(Context context, List<BluetoothDevice> devices) {
        super(context, 0, devices);


    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(getContext()).inflate(R.layout.element_device, parent, false);

        TextView txtName = listItem.findViewById(R.id.device_name);
        TextView txtUUID = listItem.findViewById(R.id.device_uuid);

        BluetoothDevice device = getItem(position);

        if (device != null) {
            txtName.setText(device.getName());
//            txtUUID.setText(device.getUuids()[0].toString());
        }

        return listItem;

    }
}
