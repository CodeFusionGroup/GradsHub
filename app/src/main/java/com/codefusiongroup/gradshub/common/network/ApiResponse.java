package com.codefusiongroup.gradshub.common.network;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

// wrapper model for network responses
public class ApiResponse<T> {

    //private String message;
    //private List<T> list;


    private T data;


    private Throwable error;

    public ApiResponse( T data) {
        this.data = data;
        //this.list = null;
        //this.message = null;
        this.error = null;
    }

//    public ApiResponse(List<T> list) {
//        //this.list = list;
//        this.data = null;
//        //this.message = null;
//        this.error = null;
//    }

    public ApiResponse(Throwable error) {
        this.error = error;
        this.data = null;
        //this.message = null;
        //this.list = null;
    }

//    public ApiResponse(String message) {
//        //this.message = message;
//        //this.list = null;
//        this.error = null;
//    }


    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    //    public String getMessage() {
//        return message;
//    }
//
//    public void setMessage(String message) {
//        this.message = message;
//    }

//    public List<T> getList() {
//        return list;
//    }
//
//    public void setList(List<T> list) {
//        this.list = list;
//    }


    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
    }

}
