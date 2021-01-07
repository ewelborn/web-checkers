package edu.tarleton.welborn.webchess.checkersAndVariants;

import edu.tarleton.welborn.webchess.BoardGamePlayerColor;
import java.util.ArrayList;
import java.util.List;

public class EnglishDraughts extends Checkers {
    public EnglishDraughts() {
        // Create an 8x8 board with 3 rows of checkers for each side
        List<CheckerPiece> checkerPieces = new ArrayList<>();
        for(int x=0;x<8;x++) {
            for(int y=0;y<3;y++) {
                if((x % 2 == 0 && y % 2 == 0) || (x % 2 == 1 && y % 2 == 1)) {
                    CheckerPiece checkerPiece = new CheckerPiece();
                    checkerPiece.setOwner(BoardGamePlayerColor.LIGHT);
                    checkerPiece.setX(x);
                    checkerPiece.setY(y);
                    checkerPieces.add(checkerPiece);
                }
            }
            for(int y=5;y<8;y++) {
                if((x % 2 == 0 && y % 2 == 0) || (x % 2 == 1 && y % 2 == 1)) {
                    CheckerPiece checkerPiece = new CheckerPiece();
                    checkerPiece.setOwner(BoardGamePlayerColor.DARK);
                    checkerPiece.setX(x);
                    checkerPiece.setY(y);
                    checkerPieces.add(checkerPiece);
                }
            }
        }
        setCheckerPieces(checkerPieces);
        setBoardSize(8);
    }
}