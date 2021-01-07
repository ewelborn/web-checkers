package edu.tarleton.welborn.exceptions;

public class NoSuchBoardGameException extends Exception {
    public NoSuchBoardGameException(String s) {
        super(s);
    }
}