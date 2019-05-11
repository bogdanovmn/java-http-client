package com.github.bogdanovmn.httpclient.core;

public class ResponseParseException extends ResponseException {
	public ResponseParseException(String msg) {
		super(msg);
	}

	public ResponseParseException(Exception e) {
		super(e);
	}
}
