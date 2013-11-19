package info.hoang8f.directchat.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import info.hoang8f.directchat.R;
import info.hoang8f.directchat.activity.ChatActivity;
import info.hoang8f.directchat.agent.ChatInterface;
import jade.android.AgentContainerHandler;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

public class PlaceholderFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "PlaceholderFragment";
    private static final String ARG_SECTION_NUMBER = "section_number";
    private WifiP2pDevice mDevice;
    private TextView mContent;
    private Button mChatButton;
    private EditText mChatBox;
    private ScrollView mScrollView;
    private AgentContainerHandler mainContainerHandler;
    private AgentController mChatAgentController;
    private ChatInterface chatInterface;

    public static PlaceholderFragment newInstance(AgentContainerHandler containerHandler, WifiP2pDevice device) {
        PlaceholderFragment fragment = new PlaceholderFragment(containerHandler, device);
        Bundle args = new Bundle();
//        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public PlaceholderFragment(AgentContainerHandler containerHandler, WifiP2pDevice device) {
        mDevice = device;
        mainContainerHandler = containerHandler;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        mContent = (TextView) rootView.findViewById(R.id.chat_content);
        mChatButton = (Button) rootView.findViewById(R.id.chat_button);
        mChatBox = (EditText) rootView.findViewById(R.id.chat_box);
        mScrollView = (ScrollView) rootView.findViewById(R.id.scroll);
        mChatButton.setOnClickListener(this);
        getActivity().getActionBar().setSubtitle("Connecting...");
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((ChatActivity) activity).onSectionAttached(mDevice.deviceName);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.chat_button:
                String text = mChatBox.getText().toString();
                if (text != null && !"".equals(text)) {
                    showMessage(false, text);
                    mChatBox.setText("");
                    if (chatInterface == null) {
                        try {
                            mChatAgentController = ((ChatActivity)getActivity()).getSenderAgentController();
                            chatInterface = mChatAgentController.getO2AInterface(ChatInterface.class);
                        } catch (StaleProxyException e) {
                            e.printStackTrace();
                        }
                    }
                    if (chatInterface != null) {
                        chatInterface.handleSpoken(text);
                    } else {
                        Toast.makeText(getActivity(), "Agent is not created", Toast.LENGTH_SHORT).show();
                    }
                }
                return;
        }
    }

    public void showMessage(boolean isIncomming, String s) {
        if (isIncomming) {
            mContent.append("\n" + s);
        } else {
            mContent.append("\nMe: " + s);
        }
        mScrollView.fullScroll(View.FOCUS_DOWN);

    }

}