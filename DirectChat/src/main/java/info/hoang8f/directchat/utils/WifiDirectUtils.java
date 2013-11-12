package info.hoang8f.directchat.utils;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import info.hoang8f.directchat.R;

public class WifiDirectUtils {

    public static final String TAG = "WifiDirectUtils";

    public static String deviceAddress = "";
    public static String groupOwnerAddress = "";
    public static String otherDeviceAddress = "";

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

    public static String getArpIPAddress(String address) {
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

    private static byte[] getLocalIPAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        if (inetAddress instanceof Inet4Address) { // fix for Galaxy Nexus. IPv4 is easy to use :-)
                            return inetAddress.getAddress();
                        }
                        //return inetAddress.getHostAddress().toString(); // Galaxy Nexus returns IPv6
                    }
                }
            }
        } catch (SocketException ex) {
            //Log.e("AndroidNetworkAddressFactory", "getLocalIPAddress()", ex);
        } catch (NullPointerException ex) {
            //Log.e("AndroidNetworkAddressFactory", "getLocalIPAddress()", ex);
        }
        return null;
    }

    private static String getDottedDecimalIP(byte[] ipAddr) {
        //convert to dotted decimal notation:
        String ipAddrStr = "";
        for (int i = 0; i < ipAddr.length; i++) {
            if (i > 0) {
                ipAddrStr += ".";
            }
            ipAddrStr += ipAddr[i] & 0xFF;
        }
        return ipAddrStr;
    }

    public static String getLocalAddress() {
        return getDottedDecimalIP(getLocalIPAddress());
    }

    private final static String p2pInt = "p2p-p2p0";

    public static String getIPFromMac(String MAC) {
                /*
                 * method modified from:
                 *
                 * http://www.flattermann.net/2011/02/android-howto-find-the-hardware-mac-address-of-a-remote-host/
                 *
                 * */
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = br.readLine()) != null) {

                String[] splitted = line.split(" +");
                if (splitted != null && splitted.length >= 4) {
                    // Basic sanity check
                    String device = splitted[5];
                    if (device.matches(".*" + p2pInt + ".*")) {
                        String mac = splitted[3];
                        if (mac.matches(MAC)) {
                            return splitted[0];
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
