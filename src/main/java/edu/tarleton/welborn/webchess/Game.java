package edu.tarleton.welborn.webchess;

import edu.tarleton.welborn.exceptions.PlayerAlreadyExistsInGameException;
import edu.tarleton.welborn.exceptions.PlayerDoesNotExistInGameException;
import edu.tarleton.welborn.exceptions.PlayerExceedsCapacityOfGame;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.ws.rs.core.MultivaluedMap;

public class Game {
    private final BoardGame boardGame;
    private final List<Player> players;
    private String gameID;
    private final long timeOutInMilliseconds;
    
    public Game(BoardGame boardGame, long timeOutInMilliseconds, Map<String,String[]> parameters) {
        this.boardGame = boardGame;
        this.players = new ArrayList<>();
        this.timeOutInMilliseconds = timeOutInMilliseconds;
        for(BoardGameSetting setting : boardGame.getBoardGameSettings()) {
            if(parameters.containsKey(setting.getKey())) {
                if(setting.getType().equals("boolean")) {
                    setting.setValue("true");
                } else {
                    setting.setValue(Arrays.toString(parameters.get(setting.getKey())));
                }
            } else {
                if(setting.getType().equals("boolean")) {
                    setting.setValue("false");
                }
            }
        }
    }

    public BoardGame getBoardGame() {
        return boardGame;
    }

    public void generateID() {
        Random random = new Random();
        gameID = Integer.toHexString(random.nextInt(0x9000)+0x1000);
        System.out.println(gameID);
    }

    public String getGameID() {
        return gameID;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player player) throws PlayerAlreadyExistsInGameException,PlayerExceedsCapacityOfGame {
        synchronized(players) {
            for(Player thatPlayer : players) {
                if(thatPlayer.getSessionToken().equals(player.getSessionToken())) {
                    throw new PlayerAlreadyExistsInGameException("Player of session token "+player.getSessionToken()+" already exists in game!");
                }
            }
            if(players.size() >= boardGame.getMaximumNumberOfPlayers()) {
                System.out.println("We're throwing this out");
                throw new PlayerExceedsCapacityOfGame("Player of session token "+player.getSessionToken()+" would exceed the maximum capacity of the game, which is " + boardGame.getMaximumNumberOfPlayers());
            }
            players.add(player);
        }
        boardGame.onPlayerAdded(player);
    }
    
    public void removePlayer(Player player) {
        synchronized(players) {
            players.remove(player);
        }
    }
    
    public void checkForPlayerTimeOuts() {
        synchronized(players) {
            for(Player player : players) {
                if(player.timedOut(timeOutInMilliseconds)) {
                    synchronized(boardGame) {
                        boardGame.onPlayerRemoving(player, PlayerRemovingReason.PLAYER_LOST_CONNECTION);
                    }
                    removePlayer(player);
                }
            }
        }
    }

    public Player getPlayerFromSessionToken(String sessionToken) throws PlayerDoesNotExistInGameException {
        synchronized(players) {
            for(Player player : players) {
                if(player.getSessionToken().equals(sessionToken)) {
                    return player;
                }
            }
        }
        throw new PlayerDoesNotExistInGameException("No player with a session token "+sessionToken+" exists in game!");
    }
    
    public void receiveGameUpdate(Player player, MultivaluedMap<String, String> gameUpdateData) {
        // Process game updates that aren't necessarily related to the boardgame itself
        if(gameUpdateData.containsKey("screenName")) {
            player.setScreenName(gameUpdateData.getFirst("screenName"));
        }
        
        if(gameUpdateData.containsKey("resign")) {
            synchronized(boardGame) {
                boardGame.onPlayerRemoving(player, PlayerRemovingReason.PLAYER_RESIGNED);
            }
            removePlayer(player);
            return;
        }
        
        if(gameUpdateData.containsKey("passTurn")) {
            synchronized(boardGame) {
                boardGame.onPlayerPassingTurn(player);
            }
        }
        
        // Update the player's last contact
        player.updateLastContact();
        
        synchronized(boardGame) {
            boardGame.receiveGameUpdate(player, gameUpdateData);
        }
        
        // Check for player timeouts, then we can exit.
        checkForPlayerTimeOuts();
    }
    
    public Object sendGameUpdate(String sessionToken) {
        // Check if this is a player. If so, update their last contact.
        try {
            Player player = getPlayerFromSessionToken(sessionToken);
            player.updateLastContact();
            checkForPlayerTimeOuts();
        } catch (PlayerDoesNotExistInGameException ex) {
            // Just a spectator or something. We can silently move on.
        }
        
        return this;
    }
}