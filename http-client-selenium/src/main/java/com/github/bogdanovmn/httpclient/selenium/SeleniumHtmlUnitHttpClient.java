package com.github.bogdanovmn.httpclient.selenium;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.github.bogdanovmn.httpclient.core.HttpClient;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.io.IOException;

public class SeleniumHtmlUnitHttpClient implements HttpClient {
	private final HtmlUnitDriver browser = new HtmlUnitDriver(BrowserVersion.CHROME) {
		@Override
		protected WebClient newWebClient(BrowserVersion version) {
			WebClient webClient = super.newWebClient(version);
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			webClient.getOptions().setJavaScriptEnabled(true);
			webClient.getOptions().setPrintContentOnFailingStatusCode(true);
			webClient.getOptions().setUseInsecureSSL(true);
			return webClient;
		}
	};
	private final int pageLoadTimeInMills;

	public SeleniumHtmlUnitHttpClient() {
		this(100);
	}
	public SeleniumHtmlUnitHttpClient(int pageLoadTimeInMills) {
		this.pageLoadTimeInMills = pageLoadTimeInMills;
	}

	@Override
	public String get(String url) throws IOException {
		browser.get(url);
		try {
			Thread.sleep(this.pageLoadTimeInMills);
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
		browser.quit();
	}
}
