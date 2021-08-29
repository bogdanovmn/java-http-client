package com.github.bogdanovmn.httpclient.core;

import java.io.Closeable;
import java.io.IOException;

public abstract class ExternalHttpService<ParsedAnswer> implements Closeable {
	protected final HttpClient httpClient;
	protected final String urlPrefix;

	public ExternalHttpService(HttpClient httpClient, String urlPrefix) {
		this.httpClient = httpClient;
		this.urlPrefix = urlPrefix;
	}

	protected abstract ParsedAnswer parsedServiceResponse(String responseBody) throws ResponseException;

	@Override
	public void close() throws IOException {
		this.httpClient.close();
	}
}
