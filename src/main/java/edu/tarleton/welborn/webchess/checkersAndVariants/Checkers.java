package edu.tarleton.welborn.webchess.checkersAndVariants;

import edu.tarleton.welborn.exceptions.BoardGameSettingDoesNotExistException;
import edu.tarleton.welborn.exceptions.NoSuchCheckerPieceException;
import edu.tarleton.welborn.webchess.BoardGame;
import edu.tarleton.welborn.webchess.BoardGamePlayerColor;
import edu.tarleton.welborn.webchess.BoardGameSetting;
import edu.tarleton.welborn.webchess.Player;
import edu.tarleton.welborn.webchess.PlayerRemovingReason;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.MultivaluedMap;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Checkers extends BoardGame {
    private List<CheckerPiece> checkerPieces;
    private int boardSize; // Size of the square board in tiles
    private List<Player> players;
    private Player currentPlayer; // Who's turn is it?
    private Player winner; // Who won?
    private final List<CheckerMove> moveHistory;
    private CheckerGameState gameState; // Has the game started? Is it over?
    
    public Checkers() {
        setURL("WEB-INF/checkers.jsp");
        setMaximumNumberOfPlayers(2);
        players = new ArrayList<>();
        moveHistory = new ArrayList<>();
        gameState = CheckerGameState.WAITING_FOR_PLAYERS;
        getBoardGameSettings().add(new BoardGameSetting("Forced capture","true","boolean"));
        getBoardGameSettings().add(new BoardGameSetting("Double jump","true","boolean"));
    }

    public List<CheckerPiece> getCheckerPieces() {
        return checkerPieces;
    }

    public void setCheckerPieces(List<CheckerPiece> checkerPieces) {
        this.checkerPieces = checkerPieces;
    }
    
    public CheckerPiece getCheckerPieceByCoordinates(int x, int y) throws NoSuchCheckerPieceException {
        for(CheckerPiece checkerPiece : checkerPieces) {
            if(checkerPiece.getX() == x && checkerPiece.getY() == y) {
                return checkerPiece;
            }
        }
        throw new NoSuchCheckerPieceException("No checker piece exists at coordinates " + x + ", " + y);
    }

    public int getBoardSize() {
        return boardSize;
    }

    public void setBoardSize(int boardSize) {
        this.boardSize = boardSize;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public CheckerGameState getGameState() {
        return gameState;
    }

    public void setGameState(CheckerGameState gameState) {
        this.gameState = gameState;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public Player getWinner() {
        return winner;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }
    
    public Player findNextPlayer(Player currentPlayer) {
        BoardGamePlayerColor nextPlayerColor = BoardGamePlayerColor.valueOf(currentPlayer.getGameData().get("playerColor")).getInvertedColor();
        for(Player player : players) {
            if(BoardGamePlayerColor.valueOf(player.getGameData().get("playerColor")) == nextPlayerColor) {
                return player;
            }
        }
        return null;
    }
    
    public void endCurrentPlayerTurn() {
        checkerPieces.forEach(checkerPiece -> {
            checkerPiece.setMoved(false);
        });
        gameState = CheckerGameState.PLAYER_TURN; 
        currentPlayer = findNextPlayer(currentPlayer);
    }
    
    public boolean canCheckerCapture(CheckerPiece checkerPiece) {
        boolean validMovePossible = false;
        for(int x = -2; x < 3; x = x + 4) {
            for(int y = -2; y < 3; y = y + 4) {
                CheckerMove possibleMove = new CheckerMove();
                possibleMove.setOldX(checkerPiece.getX());
                possibleMove.setOldY(checkerPiece.getY());
                possibleMove.setNewX(checkerPiece.getX() + x);
                possibleMove.setNewY(checkerPiece.getY() + y);
                possibleMove.setMoveType(checkerPiece.checkerIsOnPromotionRank(boardSize,checkerPiece.getY()) ? CheckerMoveType.CAPTURE_AND_PROMOTION : CheckerMoveType.CAPTURE);
                if(possibleMove.isValid(boardSize, boardSize, checkerPieces, gameState)) {
                    validMovePossible = true;
                    break;
                }
            }
            if(validMovePossible) {
                break;
            }
        }
        return validMovePossible;
    }
    
    public void endGame() {
        setGameState(CheckerGameState.GAME_OVER);
    }
    
    @Override
    public void receiveGameUpdate(Player player, MultivaluedMap<String, String> gameUpdateData) {
        if(gameUpdateData.containsKey("move") && currentPlayer.equals(player)) {
            // The player's move comes in the form of X1-Y1-X2-Y2
            String[] submittedMoveComponents = gameUpdateData.getFirst("move").split("-");
            if(submittedMoveComponents.length == 4) {
                try {
                    CheckerMove checkerMove = new CheckerMove();
                    checkerMove.setOldX(Integer.valueOf(submittedMoveComponents[0]));
                    checkerMove.setOldY(Integer.valueOf(submittedMoveComponents[1]));
                    checkerMove.setNewX(Integer.valueOf(submittedMoveComponents[2]));
                    checkerMove.setNewY(Integer.valueOf(submittedMoveComponents[3]));
                    
                    CheckerPiece checkerPiece = getCheckerPieceByCoordinates(checkerMove.getOldX(),checkerMove.getOldY());
                    
                    if(checkerMove.getManhattanDistance() == 2) {
                        // If our checker piece would end up on the promotion rank, then this is a promotion move, otherwise, it's a normal move
                        checkerMove.setMoveType(checkerPiece.checkerIsOnPromotionRank(boardSize,checkerMove.getNewY()) ? CheckerMoveType.PROMOTION : CheckerMoveType.NORMAL);
                    } else {
                        // If our checker piece would end up on the promotion rank, then this is a capture and promotion move, otherwise, it's a capture move
                        checkerMove.setMoveType(checkerPiece.checkerIsOnPromotionRank(boardSize,checkerMove.getNewY()) ? CheckerMoveType.CAPTURE_AND_PROMOTION : CheckerMoveType.CAPTURE);
                    }
                    
                    // Validate the move
                    boolean valid = checkerMove.isValid(boardSize,boardSize,checkerPieces,gameState);
                    
                    // If we're not capturing, and our game is set to forced capture mode, then check if a capture is possible
                    // if a capture is possible, then deem the move invalid, because the player should be making a capture move.
                    if(getBoardGameSetting("Forced capture").equals("true") && checkerMove.getMoveType().checkerCaptured() == false) {
                        for(CheckerPiece otherCheckerPiece : checkerPieces) {
                            if(otherCheckerPiece.getOwner() == BoardGamePlayerColor.valueOf(player.getGameData().get("playerColor"))) {
                                if(canCheckerCapture(otherCheckerPiece)) {
                                    valid = false;
                                }
                            }
                        }
                    }
                    
                    if(valid) {
                        // Resolve movement
                        checkerPiece.setX(checkerMove.getNewX());
                        checkerPiece.setY(checkerMove.getNewY());

                        // Remove any captured pieces
                        if(checkerMove.getMoveType().checkerCaptured()) {
                            int x = checkerMove.getOldX() + ((checkerMove.getNewX() - checkerMove.getOldX())/2);
                            int y = checkerMove.getOldY() + ((checkerMove.getNewY() - checkerMove.getOldY())/2);
                            CheckerPiece capturedPiece = getCheckerPieceByCoordinates(x,y);
                            getCheckerPieces().remove(capturedPiece);
                        }

                        // Promote the piece if applicable
                        if(checkerMove.getMoveType().checkerPromoted()) {
                            checkerPiece.setKing(true);
                        }

                        // If we captured an enemy piece, then check if we won the game. If so, don't end the turn, end the game!
                        // Otherwise, see if our checker can keep capturing (and if it's valid in our ruleset). If so, don't end the turn!
                        if(checkerMove.getMoveType().checkerCaptured()) {
                            boolean enemyStillAlive = false;
                            for(CheckerPiece _checkerPiece : checkerPieces) {
                                if(_checkerPiece.getOwner() != BoardGamePlayerColor.valueOf(player.getGameData().get("playerColor"))) {
                                    enemyStillAlive = true;
                                    break;
                                }
                            }
                            if(enemyStillAlive == false) {
                                winner = player;
                                endGame();
                            } else if(getBoardGameSetting("Double jump").equals("true")) {
                                // Attempt captures in all four diagonal directions: if at least one is valid, then don't end the turn
                                if(canCheckerCapture(checkerPiece)) {
                                    // Flag the checker so that we know this is the one that needs to double jump on the next move
                                    checkerPiece.setMoved(true);
                                    gameState = CheckerGameState.PLAYER_TURN_DOUBLE_JUMPING;
                                } else {
                                    endCurrentPlayerTurn();
                                }
                            } else {
                                endCurrentPlayerTurn();
                            }
                        } else {
                            endCurrentPlayerTurn();
                        }
                    }
                } catch (NoSuchCheckerPieceException ex) {
                    // Do nothing: if we don't process the move, then it's implicitly rejected by the server
                } catch (BoardGameSettingDoesNotExistException ex) {
                    // Okay this is a little worse.
                    Logger.getLogger(Checkers.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    @Override
    public void onPlayerAdded(Player player) {
        if(currentPlayer == null) {
            currentPlayer = player;
            player.getGameData().put("playerColor",BoardGamePlayerColor.LIGHT.toString());
        } else {
            player.getGameData().put("playerColor",BoardGamePlayerColor.DARK.toString());
        }
        players.add(player);
        if(players.size() >= 2) {
            gameState = CheckerGameState.PLAYER_TURN;
            // Set the maximum number of players to 0 so no one else can join, even if a player leaves.
            setMaximumNumberOfPlayers(0);
        }
    }
    
    @Override
    public void onPlayerRemoving(Player player, PlayerRemovingReason playerRemovingReason) {
        if(playerRemovingReason == PlayerRemovingReason.PLAYER_RESIGNED) {
            winner = findNextPlayer(player);
            if(gameState == CheckerGameState.PLAYER_TURN) {
                setGameState(CheckerGameState.GAME_OVER_PLAYER_RESIGNED);
            }
        } else if(playerRemovingReason == PlayerRemovingReason.PLAYER_LOST_CONNECTION) {
            winner = findNextPlayer(player);
            if(gameState == CheckerGameState.PLAYER_TURN) {
                setGameState(CheckerGameState.GAME_OVER_PLAYER_LOST_CONNECTION);
            }
        }
        players.remove(player);
    }
    
    @Override
    public void onPlayerPassingTurn(Player player) {
        try {
            if(player == currentPlayer && gameState == CheckerGameState.PLAYER_TURN_DOUBLE_JUMPING && getBoardGameSetting("Forced capture").equals("false")) {
                endCurrentPlayerTurn();
            }
        } catch (BoardGameSettingDoesNotExistException ex) {
            // Bad news
            Logger.getLogger(Checkers.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}