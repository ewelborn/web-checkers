package edu.tarleton.welborn.exceptions;

public class PlayerDoesNotExistInGameException extends Exception {
    public PlayerDoesNotExistInGameException(String s) {
        super(s);
    }
}