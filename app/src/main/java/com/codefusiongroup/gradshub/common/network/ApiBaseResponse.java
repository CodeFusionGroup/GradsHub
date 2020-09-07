package com.codefusiongroup.gradshub.common.network;

import com.google.gson.annotations.SerializedName;

public class ApiBaseResponse {

	@SerializedName("success")
	String statusCode;

	@SerializedName("message")
	String message;


	public ApiBaseResponse(String statusCode, String message) {
		this.statusCode = statusCode;
		this.message = message;
	}


	public String getStatusCode() { return statusCode; }

	public String getMessage() {
		return message;
	}

}
