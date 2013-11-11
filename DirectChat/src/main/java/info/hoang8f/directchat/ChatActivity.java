package info.hoang8f.directchat;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import info.hoang8f.directchat.utils.WifiDirectUtils;
import jade.android.AgentContainerHandler;
import jade.android.RuntimeCallback;
import jade.android.RuntimeService;
import jade.android.RuntimeServiceBinder;

public class ChatActivity extends Activity implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    public static final String TAG = "ChatActivity";
    private WifiP2pManager mWifiP2pManager;
    private WifiP2pManager.Channel mChannel;
    private RuntimeServiceBinder runtimeServiceBinder;
    private AgentContainerHandler mainContainerHandler;
    private ServiceConnection serviceConnection;
    private final IntentFilter intentFilter = new IntentFilter();
    private DirectBroadcastReceiver receiver;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mNavigationDrawerFragment = (NavigationDrawerFragment)getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        //Start JADE main container
        bindService();

        //  Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mWifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mWifiP2pManager.initialize(this, getMainLooper(), null);

        reloadDevices();
    }

    public void reloadDevices() {
        mWifiP2pManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "###onSuccess");
            }

            @Override
            public void onFailure(int reasonCode) {
                Log.d(TAG, "###onFailure");
            }
        });
    }

    public void onResume() {
        super.onResume();
        receiver = new DirectBroadcastReceiver(mWifiP2pManager, mChannel, this);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    public NavigationDrawerFragment getNavigationFragment() {

        return mNavigationDrawerFragment;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.chat, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((ChatActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }
    /**
     * Create JADE Main Container here
     */
    private void bindService() {
        //Check runtime service
        if (runtimeServiceBinder == null) {
            //Create Runtime Service Binder here
            serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName componentName, IBinder service) {
                    runtimeServiceBinder = (RuntimeServiceBinder) service;
                    Log.i(TAG, "###Gateway successfully bound to RuntimeService");
                    startMainContainer();
                }

                @Override
                public void onServiceDisconnected(ComponentName componentName) {
                    Log.i(TAG, "###Gateway unbound from RuntimeService");
                }
            };
            Log.i(TAG, "###Binding Gateway to RuntimeService...");
            bindService(new Intent(getApplicationContext(), RuntimeService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
            startMainContainer();
        }
    }

    private void startMainContainer() {
        runtimeServiceBinder.createMainAgentContainer(new RuntimeCallback<AgentContainerHandler>() {
            @Override
            public void onSuccess(AgentContainerHandler agentContainerHandler) {
                mainContainerHandler = agentContainerHandler;
                Log.i(TAG, "###Main-Container created...");
                Log.i(TAG, "###Container:" + agentContainerHandler.getAgentContainer().getName());
                Log.i(TAG, "###mainContainerHandler:" + mainContainerHandler);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.i(TAG, "###Failed to create Main Container");
            }
        });
    }

    public WifiP2pManager getWifiP2PManager() {
        return mWifiP2pManager;
    }

    public WifiP2pManager.Channel getWifiP2PChannel() {
        return mChannel;
    }

}
