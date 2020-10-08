package com.codefusiongroup.gradshub.common.network;

import org.junit.Test;

import static org.junit.Assert.*;

public class ApiBaseResponseTest {
    String statusCode = "success";
    String message = "Hello";
    ApiBaseResponse apiBaseResponse = new ApiBaseResponse(statusCode, message);
    @Test
    public void getStatusCode() {
        assertEquals(statusCode, apiBaseResponse.getStatusCode());
    }

    @Test
    public void getMessage() {
        assertEquals(message, apiBaseResponse.getMessage());
    }
}