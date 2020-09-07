package com.codefusiongroup.gradshub.common.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


// class creates a singleton of Retrofit and returns it to the caller
public class RetrofitClient {

    private static final String BASE_URL = "https://gradshub.herokuapp.com/api/";
    private static Retrofit retrofit = null;

    public static synchronized Retrofit getClient() {

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }

}



