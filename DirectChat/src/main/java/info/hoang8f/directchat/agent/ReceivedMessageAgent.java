package info.hoang8f.directchat.agent;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import info.hoang8f.directchat.utils.Constants;
import jade.content.lang.Codec;
import jade.content.lang.sl.SLCodec;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import jade.util.leap.Set;
import jade.util.leap.SortedSetImpl;

public class ReceivedMessageAgent extends Agent implements SimpleAgentInterface {

    private static final String TAG = "ReceivedMessage";
    private static final long serialVersionUID = 1594371294421614291L;
    private Set participants = new SortedSetImpl();
    private Codec codec = new SLCodec();
    private Context context;
    private String ipAddress = "133.19.63.184";
    private String agentName = "android-agent";

    protected void setup() {
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            if (args[0] instanceof Context) {
                context = (Context) args[0];
            }
        }

        // Add initial behaviours
        addBehaviour(new ParticipantsManager(this));

        // Activate the GUI
        registerO2AInterface(SimpleAgentInterface.class, this);

        Intent broadcast = new Intent();
        broadcast.setAction("jade.demo.agent.SEND_MESSAGE");
        Log.i(TAG, "###Sending broadcast " + broadcast.getAction());
        context.sendBroadcast(broadcast);

        //Get ipAddress and agentName
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constants.PREFS_FILE_NAME, Context.MODE_PRIVATE);
        ipAddress = sharedPreferences.getString(Constants.PREFS_HOST_ADDRESS, ipAddress);
        agentName = sharedPreferences.getString(Constants.PREFS_AGENT_NAME, agentName);
    }

    protected void takeDown() {
    }

    class ParticipantsManager extends CyclicBehaviour {
        private static final long serialVersionUID = -4845730529175649756L;

        ParticipantsManager(Agent a) {
            super(a);
        }

        public void onStart() {
            //Start cyclic
        }

        public void action() {
            // Listening for incomming
            ACLMessage msg = myAgent.receive();
            if (msg != null) {
                try {
                    //Get message
                    String message = msg.getContent();
                    Log.i(TAG, "###Incomming message:" + message);
                    exportLog("Incomming message:" + message);

                } catch (Exception e) {
                    Logger.println(e.toString());
                    e.printStackTrace();
                }
            } else {
                block();
            }
        }
    } // END of inner class ParticipantsManager

    public void handleSpoken(String s) {

    }

    public String[] getParticipantNames() {
        String[] pp = new String[participants.size()];
        return pp;
    }

    public void onHostChanged(String host) {
        ipAddress = host;
    }

    public void onAgentNameChanged(String name) {
        agentName = name;
    }

    private void exportLog(String log) {
//        MainActivity mainActivity = (MainActivity)context;
//        mainActivity.exportLogConsole(log);
    }
}
