package com.nxpee.currency_exchanger.exception;

public class AlreadyExistException extends Exception{
    public AlreadyExistException(String errorMessage){
        super(errorMessage);
    }
}
