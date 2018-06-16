package com.github.bogdanovmn.httpclient.simple;

import com.github.bogdanovmn.httpclient.core.HttpClient;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SimpleHttpClient implements HttpClient {
	private final String urlPrefix;
	private final CloseableHttpClient httpClient = HttpClients.custom()
		.setConnectionTimeToLive(10, TimeUnit.SECONDS)
		.setUserAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36")
		.build();

	public SimpleHttpClient(String urlPrefix) {
		this.urlPrefix = urlPrefix;
	}

	@Override
	public String get(String url) throws IOException {
		CloseableHttpResponse response = httpClient.execute(
			new HttpGet(urlPrefix + url)
		);
		return IOUtils.toString(
			response.getEntity().getContent(),
			response.getEntity().getContentEncoding() == null
				? "UTF-8"
				: response.getEntity().getContentEncoding().getValue()
		);
	}

	public Date getLastModified(String url) throws IOException {
		Date result = null;
		HttpHead request = new HttpHead(urlPrefix + url);
		CloseableHttpResponse httpResponse = httpClient.execute(request);
		if (httpResponse.getStatusLine().getStatusCode() == 200) {
			Header header = httpResponse.getLastHeader("Last-Modified");
			if (header != null) {
				try {
					result = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US).parse(header.getValue());
				}
				catch (ParseException e) {
					e.printStackTrace();
					return null;
				}
			}
		}
		return result;
	}

	@Override
	public void close() throws IOException {
		httpClient.close();
	}
}
