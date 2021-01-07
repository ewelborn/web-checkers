package edu.tarleton.welborn.webchess;

public enum BoardGamePlayerColor {
    LIGHT, DARK,;
    // More colors could be added here for variants such as
    // 4 player chess. Code should be written to account for this scenario.
    
    @Override
    public String toString() {
        return this.name();
    }
    
    public BoardGamePlayerColor getInvertedColor() {
        if(this.name().equals("LIGHT")) {
            return BoardGamePlayerColor.DARK;
        } else {
            return BoardGamePlayerColor.LIGHT;
        }
    }
}