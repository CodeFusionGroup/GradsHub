package com.codefusiongroup.gradshub.common.network;

import org.junit.Test;

import retrofit2.Retrofit;

import static org.junit.Assert.*;

public class RetrofitClientTest {

    @Test
    public void getClient() {
        RetrofitClient.getClient();
    }
}