package com.codefusiongroup.gradshub.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;


/*
This is a singleton class that provides a RequestQueue for the Application context.
https://developer.android.com/training/volley/requestqueue
*/

public class NetworkRequestQueue {
    private static NetworkRequestQueue instance;
    private RequestQueue requestQueue;
    private static Context ctx;


    private NetworkRequestQueue(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized NetworkRequestQueue getInstance(Context context) {
        if (instance == null) {
            instance = new NetworkRequestQueue(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }


}
