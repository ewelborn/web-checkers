package edu.tarleton.welborn.exceptions;

public class BoardGameSettingDoesNotExistException extends Exception {
    public BoardGameSettingDoesNotExistException(String s) {
        super(s);
    }
}