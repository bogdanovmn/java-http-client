package com.github.bogdanovmn.httpclient.simple;

import com.github.bogdanovmn.httpclient.core.HttpClient;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SimpleHttpClient implements HttpClient {
	private final static String DEFAULT_USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36";
	private final static int DEFAULT_TIMEOUT_SECONDS = 10;
	private final CloseableHttpClient httpClient;

	public SimpleHttpClient(CloseableHttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public SimpleHttpClient() {
		this(
			HttpClients.custom()
				.setConnectionTimeToLive(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
				.setUserAgent(DEFAULT_USER_AGENT)
				.setDefaultRequestConfig(
					RequestConfig.custom()
						.setCookieSpec(CookieSpecs.STANDARD)
						.setConnectionRequestTimeout(DEFAULT_TIMEOUT_SECONDS * 1000)
						.setConnectTimeout(DEFAULT_TIMEOUT_SECONDS * 1000)
						.setSocketTimeout(DEFAULT_TIMEOUT_SECONDS * 1000)
						.build()
				)
			.build()
		);
	}

	public static SimpleHttpClient withHttpProxy(String proxyHostPort) {
		String[] components = proxyHostPort.split(":");
		if (components.length != 2) {
			throw new IllegalArgumentException("Wrong proxy format: " + proxyHostPort);
		}

		return SimpleHttpClient.withHttpProxy(
			components[0],
			Integer.valueOf(components[1])
		);
	}

	private static SimpleHttpClient withHttpProxy(String proxyHost, int proxyPort) {
		return new SimpleHttpClient(
			HttpClients.custom()
				.setConnectionTimeToLive(DEFAULT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
				.setUserAgent(DEFAULT_USER_AGENT)
				.setProxy(
					new HttpHost(proxyHost, proxyPort, "http")
				).build()
		);
	}

	@Override
	public String get(String url) throws IOException {
		try (CloseableHttpResponse response = httpClient.execute(new HttpGet(url))) {
			return IOUtils.toString(
				response.getEntity().getContent(),
				response.getEntity().getContentEncoding() == null
					? "UTF-8"
					: response.getEntity().getContentEncoding().getValue()
			);
		}
	}

	public InputStream downloadFile(String url) throws IOException {
		return httpClient.execute(
			new HttpGet(url)
		).getEntity().getContent();
	}

	public Date getLastModified(String url) throws IOException {
		Date result = null;
		HttpHead request = new HttpHead(url);
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
