package edu.tarleton.welborn.webchess.checkersAndVariants;

public enum CheckerMoveType {
    NORMAL,CAPTURE,PROMOTION,CAPTURE_AND_PROMOTION;
    
    public boolean checkerCaptured() {
        return (this.equals(CAPTURE) || this.equals(CAPTURE_AND_PROMOTION));
    }
    
    public boolean checkerPromoted() {
        return (this.equals(PROMOTION) || this.equals(CAPTURE_AND_PROMOTION));
    }
    
}