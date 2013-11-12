package info.hoang8f.directchat.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import info.hoang8f.directchat.activity.ChatActivity;
import info.hoang8f.directchat.R;

public class PlaceholderFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private WifiP2pDevice mDevice;
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlaceholderFragment newInstance(WifiP2pDevice device) {
        PlaceholderFragment fragment = new PlaceholderFragment(device);
        Bundle args = new Bundle();
//        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public PlaceholderFragment(WifiP2pDevice device) {
        mDevice = device;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        TextView textView = (TextView) rootView.findViewById(R.id.section_label);
        textView.setText(mDevice.deviceName);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((ChatActivity) activity).onSectionAttached(mDevice.deviceName);
    }
}