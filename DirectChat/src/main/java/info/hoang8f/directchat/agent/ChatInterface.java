package info.hoang8f.directchat.agent;

/**
 * This interface implements the logic of the chat client running on the user
 * terminal.
 * 
 * @author Michele Izzo - Telecomitalia
 */

public interface ChatInterface {
	public void handleSpoken(String s);
	public String[] getParticipantNames();
    public void onHostChanged(String host);
    public void onAgentNameChanged(String name);
}