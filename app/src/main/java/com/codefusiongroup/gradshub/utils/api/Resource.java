package com.codefusiongroup.gradshub.utils.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class Resource<T> {

    @NonNull public final Status status;
    @Nullable public final T data;
    @Nullable public final String message;

    public Resource(@NonNull Status status, @Nullable T data, @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> Resource<T> apiDataRequestSuccess(@NonNull T data, @Nullable String message) {
        return new Resource<>(Status.API_DATA_SUCCESS, data, message);
    }

    public static <T> Resource<T> apiNonDataRequestSuccess(String message) {
        return new Resource<>(Status.API_NON_DATA_SUCCESS, null, message);
    }

    public static <T> Resource<T> error(String message) {
        return new Resource<>(Status.ERROR, null, message);
    }

    public enum  Status {
        API_DATA_SUCCESS, // for api requests that return specific data and a message if one exists.
        API_NON_DATA_SUCCESS, // for api requests that don't return specific data
        ERROR, // for network connection failure, server error, etc
    }

}
