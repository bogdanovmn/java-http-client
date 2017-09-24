package com.github.bogdanovmn.httpclient.simple;

import com.github.bogdanovmn.httpclient.core.HttpClient;
import org.apache.http.client.fluent.Request;

import java.io.IOException;

public class SimpleHttpClient implements HttpClient {
	private final String urlPrefix;

	public SimpleHttpClient(String urlPrefix) {
		this.urlPrefix = urlPrefix;
	}

	@Override
	public String get(String url) throws IOException {
		return Request.Get(urlPrefix + url)
			.connectTimeout(10000)
			.execute()
				.returnContent().asString();
	}

	@Override
	public void close() throws IOException {
	}
}
