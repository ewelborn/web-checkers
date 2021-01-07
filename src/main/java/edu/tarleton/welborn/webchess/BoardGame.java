package edu.tarleton.welborn.webchess;

import edu.tarleton.welborn.exceptions.BoardGameSettingDoesNotExistException;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.core.MultivaluedMap;

public abstract class BoardGame {
    private String URL;
    private int maximumNumberOfPlayers;
    private int minimumNumberOfPlayers;
    private List<BoardGameSetting> boardGameSettings = new ArrayList<BoardGameSetting>();

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public int getMaximumNumberOfPlayers() {
        return maximumNumberOfPlayers;
    }

    public void setMaximumNumberOfPlayers(int maximumNumberOfPlayers) {
        this.maximumNumberOfPlayers = maximumNumberOfPlayers;
    }

    public int getMinimumNumberOfPlayers() {
        return minimumNumberOfPlayers;
    }

    public void setMinimumNumberOfPlayers(int minimumNumberOfPlayers) {
        this.minimumNumberOfPlayers = minimumNumberOfPlayers;
    }

    public List<BoardGameSetting> getBoardGameSettings() {
        return boardGameSettings;
    }

    public void setBoardGameSettings(List<BoardGameSetting> boardGameSettings) {
        this.boardGameSettings = boardGameSettings;
    }
    
    public String getBoardGameSetting(String key) throws BoardGameSettingDoesNotExistException {
        for(BoardGameSetting setting : boardGameSettings) {
            if(setting.getKey().equals(key)) {
                return setting.getValue();
            }
        }
        throw new BoardGameSettingDoesNotExistException("Board game setting, " + key + ", does not exist!");
    }
    
    public abstract void receiveGameUpdate(Player player, MultivaluedMap<String, String> gameUpdateData);
    
    public abstract void onPlayerAdded(Player player);
    
    public abstract void onPlayerRemoving(Player player, PlayerRemovingReason playerRemovingReason);
    
    public abstract void onPlayerPassingTurn(Player player);
}