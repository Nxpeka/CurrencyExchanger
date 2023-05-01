package com.nxpee.currency_exchanger.dto.response;

public class ExceptionMessageDTO {
    private final String message;

    public ExceptionMessageDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
