package com.hong.springapi.response;

import lombok.Getter;

@Getter
public class Response {
    private final String state;
    private final String detail;

    public Response(String state, String detail) {
        this.state = state;
        this.detail = detail;
    }
}
