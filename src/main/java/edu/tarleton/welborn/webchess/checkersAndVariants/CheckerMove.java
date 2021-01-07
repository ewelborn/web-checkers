package edu.tarleton.welborn.webchess.checkersAndVariants;

import edu.tarleton.welborn.webchess.BoardGamePlayerColor;
import java.util.List;

public class CheckerMove {
    private int oldX;
    private int oldY;
    private int newX;
    private int newY;
    private CheckerMoveType moveType;

    public int getOldX() {
        return oldX;
    }

    public void setOldX(int oldX) {
        this.oldX = oldX;
    }

    public int getOldY() {
        return oldY;
    }

    public void setOldY(int oldY) {
        this.oldY = oldY;
    }

    public int getNewX() {
        return newX;
    }

    public void setNewX(int newX) {
        this.newX = newX;
    }

    public int getNewY() {
        return newY;
    }

    public void setNewY(int newY) {
        this.newY = newY;
    }

    public CheckerMoveType getMoveType() {
        return moveType;
    }

    public void setMoveType(CheckerMoveType moveType) {
        this.moveType = moveType;
    }
    
    public int getManhattanDistance() {
        int xDistance = Math.abs(oldX - newX);
        int yDistance = Math.abs(oldY - newY);
        return xDistance + yDistance;
    }
    
    public CheckerPiece getCheckerPieceByCoordinates(int x, int y, List<CheckerPiece> checkerPieces) {
        CheckerPiece returnValue = null;
        for(CheckerPiece checkerPiece : checkerPieces) {
            if(checkerPiece.getX() == x && checkerPiece.getY() == y) {
                returnValue = checkerPiece;
                break;
            }
        }
        return returnValue;
    }
    
    public boolean isValid(int boardSizeX, int boardSizeY, List<CheckerPiece> checkerPieces, CheckerGameState gameState) {
        if((newX >= 0 && newX < boardSizeX) && (newY >= 0 && newY < boardSizeY)) {
            CheckerPiece movingPiece = getCheckerPieceByCoordinates(oldX,oldY,checkerPieces);
            if(movingPiece == null) { return false; }
            
            // Now, check if this checker is a pawn (not a king)
            if(movingPiece.isKing() == false) {
                // Pawns are only allowed to move forward (positive Y for light, negative Y for dark)
                if(movingPiece.getOwner() == BoardGamePlayerColor.LIGHT) {
                    if(newY - movingPiece.getY() < 0) {
                        return false;
                    }
                } else {
                    if(newY - movingPiece.getY() > 0) {
                        return false;
                    }
                }
            }
            
            // If we're double jumping, then check and make sure that the piece being moved is the double jumper
            if(gameState == CheckerGameState.PLAYER_TURN_DOUBLE_JUMPING) {
                if(movingPiece.hasMoved() == false) { return false; }
            }
            
            // We can't move to a space if it's already occupied
            CheckerPiece blockingPiece = getCheckerPieceByCoordinates(newX,newY,checkerPieces);
            if(blockingPiece != null) { return false; }
            
            if(moveType.checkerCaptured()) {
                if(getManhattanDistance() != 4) { return false; }
                
                int x2 = oldX + ((newX - oldX)/2);
                int y2 = oldY + ((newY - oldY)/2);
                CheckerPiece capturedPiece = getCheckerPieceByCoordinates(x2,y2,checkerPieces);
                if(capturedPiece == null) { return false; }
                
                if(capturedPiece.getOwner() == movingPiece.getOwner()) { return false; }
            } else {
                if(getManhattanDistance() != 2) { return false; }
            }
        } else {
            return false;
        }
        
        return true;
    }
}