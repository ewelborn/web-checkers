package edu.tarleton.welborn.webchess;

import java.util.HashMap;
import java.util.Map;

public class Player {
    private String sessionToken; // Their unique JSESSIONID so that we can identify them
    private String screenName; // The name that other players will see them as ingame
    private long lastContact; // Timestamp of their last contact with the server
    private final Map<String,String> gameData = new HashMap<>(); // A list of key/value settings that the board game can use to store player specific data

    public String getSessionToken() {
        return sessionToken;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public long getLastContact() {
        return lastContact;
    }

    public void setLastContact(long lastContact) {
        this.lastContact = lastContact;
    }
    
    // This setter automatically sets the last contact to the current time, so no other systems have to
    // worry about the implementation, and can move on.
    public void updateLastContact() {
        this.lastContact = System.currentTimeMillis();
    }
    
    // Will return true if the the player's last contact was more than X milliseconds ago, otherwise,
    // it will return false.
    public boolean timedOut(long milliseconds) {
        return this.lastContact <= (System.currentTimeMillis() - milliseconds);
    }

    public Map<String, String> getGameData() {
        return gameData;
    }
    
}