package info.hoang8f.directchat;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class DevicesAdapter extends ArrayAdapter<WifiP2pDevice> {

    private ArrayList<WifiP2pDevice> mDeviceList;
    private Context mContext;


    public DevicesAdapter(Context context, ArrayList<WifiP2pDevice> deviceList) {
        super(context, R.layout.row_device, deviceList);
        mContext = context;
        mDeviceList = deviceList;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = layoutInflater.inflate(R.layout.row_device, null);
       TextView deviceName = (TextView) rowView.findViewById(R.id.row_device_name);
        deviceName.setText(mDeviceList.get(i).deviceName);
        return rowView;
    }
}
