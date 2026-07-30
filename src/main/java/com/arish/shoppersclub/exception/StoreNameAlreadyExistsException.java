package com.arish.shoppersclub.exception;

public class StoreNameAlreadyExistsException extends RuntimeException {
    public StoreNameAlreadyExistsException(String message){
        super(message);
    }

}
