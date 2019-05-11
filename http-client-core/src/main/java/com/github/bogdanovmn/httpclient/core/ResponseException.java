package com.github.bogdanovmn.httpclient.core;

public class ResponseException extends HttpServiceException {
	public ResponseException(String msg) {
		super(msg);
	}

	public ResponseException(Exception e) {
		super(e);
	}
}
