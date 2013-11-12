package info.hoang8f.directchat.utils;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import info.hoang8f.directchat.R;

public class WifiDirectUtils {

    public static final String TAG = "WifiDirectUtils";

    public static void renameDevice(final Context context, WifiP2pManager mWifiP2pManager, WifiP2pManager.Channel mChannel, String name) {

        if (mWifiP2pManager != null) {
            mChannel = mWifiP2pManager.initialize(context, context.getMainLooper(), null);
            if (mChannel == null) {
                //Failure to set up connection
                Log.e(TAG, "Failed to set up connection with wifi p2p service");
                mWifiP2pManager = null;
            }
        } else {
            Log.e(TAG, "mWifiP2pManager is null !");
        }
        if (mWifiP2pManager != null) {
            mWifiP2pManager.setDeviceName(mChannel,
                    name,
                    new WifiP2pManager.ActionListener() {
                        public void onSuccess() {
                            Log.d(TAG, " device rename success");
                        }

                        public void onFailure(int reason) {
                            Toast.makeText(context, context.getResources().getString(R.string.wifi_p2p_failed_rename_message), Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    public String get_arp_IP(String address) {
        String IPaddress = "";
        String[] s = address.split(":");
        int mask = Integer.parseInt(s[4], 16) - 128;
        String p = Integer.toHexString(mask);
        String arp_MAC = s[0] + ":" + s[1] + ":" + s[2] + ":" + s[3] + ":" + p
                + ":" + s[5];
        File root = new File("/proc/net", "arp");
        try {
            BufferedReader br = new BufferedReader(new FileReader(root));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains(arp_MAC)) {
                    String[] a = line.split(" ");
                    IPaddress = a[0];
                }
            }
        } catch (IOException e) {
        }
        return IPaddress;
    }
}
