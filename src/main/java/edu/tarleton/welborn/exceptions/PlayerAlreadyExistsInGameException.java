package edu.tarleton.welborn.exceptions;

public class PlayerAlreadyExistsInGameException extends Exception {
    public PlayerAlreadyExistsInGameException(String s) {
        super(s);
    }
}