package com.github.bogdanovmn.httpclient.core;

public class HttpServiceException extends Exception {
	public HttpServiceException(String msg) {
		super(msg);
	}

	public HttpServiceException(Exception e) {
		super(e);
	}
}
