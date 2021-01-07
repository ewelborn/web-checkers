package edu.tarleton.welborn.webchess;

import edu.tarleton.welborn.exceptions.NoSuchBoardGameException;
import edu.tarleton.welborn.exceptions.NoSuchGameException;
import edu.tarleton.welborn.webchess.checkersAndVariants.EnglishDraughts;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class GameManager {
    private static final Map<String,Game> activeGames = new HashMap<>();
    private static final Map<String,BoardGame> boardGames = new HashMap<>();
    
    public static Game startGame(String boardGameName, Map<String,String[]> parameters) throws NoSuchBoardGameException {
        BoardGame boardGame = boardGames.get(boardGameName);
        if(boardGame == null) {
            throw new NoSuchBoardGameException("The board game, " + boardGameName + ", does not exist!");
        }
        
        Game game;
        try {
            // Create a new game with a copy of the selected board game and a time out of 15 seconds.
            game = new Game((BoardGame) boardGame.getClass().getConstructors()[0].newInstance(),15000,parameters);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new NoSuchBoardGameException("The board game, " + boardGameName + ", could not be instantiated!");
        }
        synchronized(activeGames) {
            // Continue generating IDs for our game until we find one that's unique
            boolean idValid = false;
            while(idValid == false) {
                game.generateID();
                idValid = !activeGames.containsKey(game.getGameID());
            }
            activeGames.put(game.getGameID(), game);
        }
        return game;
    }
    
    public static Game getGameFromID(String gameID) throws NoSuchGameException {
        synchronized(activeGames) {
            if(activeGames.containsKey(gameID)) {
                return activeGames.get(gameID);
            } else {
                throw new NoSuchGameException("No such game of gameID "+gameID+" exists!");
            }
        }
    }
    
    public static synchronized Map<String,BoardGame> getBoardGames() {
        // If this is the first time anyone has asked for the list, then build it!!
        if(boardGames.isEmpty()) {
            boardGames.put("EnglishDraughts", new EnglishDraughts());
        }
        return boardGames;
    }
    
}