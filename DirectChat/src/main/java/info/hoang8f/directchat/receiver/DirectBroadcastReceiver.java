package info.hoang8f.directchat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import info.hoang8f.directchat.activity.ChatActivity;
import info.hoang8f.directchat.fragment.NavigationDrawerFragment;
import info.hoang8f.directchat.utils.WifiDirectUtils;

public class DirectBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "DirectBroadcastReceiver";

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private ChatActivity mActivity;

    private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {

            mNavigationDrawerFragment.refreshListDevice(peerList.getDeviceList());
            if (peerList.getDeviceList().size() == 0) {
                return;
            }
        }
    };

    public DirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, ChatActivity activity) {
        mChannel = channel;
        mManager = manager;
        mActivity = activity;
        mNavigationDrawerFragment = activity.getNavigationFragment();
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Determine if Wifi P2P mode is enabled or not, alert
            // the Activity.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
//                activity.setIsWifiP2pEnabled(true);
            } else {
//                activity.setIsWifiP2pEnabled(false);
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            if (mManager != null) {
                mManager.requestPeers(mChannel, peerListListener);
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            if (mManager == null) {
                return;
            }
            NetworkInfo networkInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected()) {
                mActivity.getActionBar().setSubtitle("Connected");
                mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
                    @Override
                    public void onConnectionInfoAvailable(WifiP2pInfo wifiP2pInfo) {
                        Log.d(TAG, wifiP2pInfo.groupOwnerAddress.getHostAddress());
                        WifiDirectUtils.groupOwnerAddress = wifiP2pInfo.groupOwnerAddress.getHostAddress();
                    }
                });
                //Create main container if need
                if (mActivity.getRuntimeServiceBinder() == null) {
                    mActivity.bindService();
                }
            } else {
                // It's a disconnect
                mActivity.getActionBar().setSubtitle("Disconnected");
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            WifiP2pDevice device = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
            WifiDirectUtils.deviceAddress = device.deviceAddress;
            mNavigationDrawerFragment.updateThisDevice(device);
        }
    }

}
