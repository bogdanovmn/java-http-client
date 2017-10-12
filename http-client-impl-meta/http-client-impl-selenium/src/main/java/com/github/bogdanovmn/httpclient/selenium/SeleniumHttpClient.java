package com.github.bogdanovmn.httpclient.selenium;

import com.github.bogdanovmn.httpclient.core.HttpClient;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.io.IOException;

public class SeleniumHttpClient implements HttpClient {
	private final String urlPrefix;
	private WebDriver browser = new HtmlUnitDriver();
	private final int pageLoadTime;

	public SeleniumHttpClient(String urlPrefix) {
		this.urlPrefix = urlPrefix;
		this.pageLoadTime = 100;
	}
	public SeleniumHttpClient(String urlPrefix, int pageLoadTime) {
		this.urlPrefix = urlPrefix;
		this.pageLoadTime = pageLoadTime;
	}

	@Override
	public String get(String url) throws IOException {
		browser.get(this.urlPrefix + url);
		try {
			Thread.sleep(this.pageLoadTime);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}

		String result = browser.getPageSource();
		browser.get("about:blank");

		return result;
	}

	@Override
	public void close() throws IOException {
		if (this.browser != null) {
			browser.quit();
		}
	}
}
