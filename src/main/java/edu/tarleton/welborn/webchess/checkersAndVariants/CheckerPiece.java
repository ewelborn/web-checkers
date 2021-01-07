package edu.tarleton.welborn.webchess.checkersAndVariants;

import edu.tarleton.welborn.webchess.BoardGamePlayerColor;

public class CheckerPiece {
    private boolean king = false;
    private BoardGamePlayerColor owner;
    private int X; // X coordinate position
    private int Y; // Y coordinate position
    public boolean moved; // Flag if the piece has already moved this turn. Resets at the end of every turn

    public String getUnicodeRepresentation() {
        if(owner == BoardGamePlayerColor.LIGHT) {
            if(king) {
                return "⛁";
            } else {
                return "⛀";
            }
        } else {
            if(king) {
                return "⛃";
            } else {
                return "⛂";
            }
        }
    }
    
    public boolean isKing() {
        return king;
    }

    public void setKing(boolean king) {
        this.king = king;
    }

    public BoardGamePlayerColor getOwner() {
        return owner;
    }

    public void setOwner(BoardGamePlayerColor owner) {
        this.owner = owner;
    }

    public int getX() {
        return X;
    }

    public void setX(int X) {
        this.X = X;
    }

    public int getY() {
        return Y;
    }

    public void setY(int Y) {
        this.Y = Y;
    }

    public boolean hasMoved() {
        return moved;
    }

    public void setMoved(boolean moved) {
        this.moved = moved;
    }
    
    public boolean checkerIsOnPromotionRank(int boardSizeY) {
        if(owner == BoardGamePlayerColor.LIGHT) {
            return Y == boardSizeY - 1;
        } else {
            return Y == 0;
        }
    }
    
    public boolean checkerIsOnPromotionRank(int boardSizeY, int givenY) {
        if(owner == BoardGamePlayerColor.LIGHT) {
            return givenY == boardSizeY - 1;
        } else {
            return givenY == 0;
        }
    }
}