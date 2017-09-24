package com.github.bogdanovmn.httpclient.phantomjs;

import ch.racic.selenium.drivers.PhantomJSDriverHelper;
import ch.racic.selenium.drivers.exceptions.ExecutableNotFoundException;
import com.github.bogdanovmn.httpclient.core.HttpClient;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.IOException;

public class PhantomJsHttpClient implements HttpClient {
	private final String urlPrefix;
	private PhantomJSDriver webDriver;
	private final int pageLoadTime;

	public PhantomJsHttpClient(String urlPrefix) {
		this.urlPrefix = urlPrefix;
		this.pageLoadTime = 100;
	}
	public PhantomJsHttpClient(String urlPrefix, int pageLoadTime) {
		this.urlPrefix = urlPrefix;
		this.pageLoadTime = pageLoadTime;
	}

	@Override
	public String get(String url) throws IOException {
		PhantomJSDriver browser = this.getWebDriver();
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

	private PhantomJSDriver getWebDriver() throws IOException {
		if (this.webDriver == null) {
			DesiredCapabilities caps = new DesiredCapabilities();

			try {
				PhantomJSDriverService service = new PhantomJSDriverService.Builder()
					.usingPhantomJSExecutable(PhantomJSDriverHelper.executable())
					.usingCommandLineArguments(new String[] {"--webdriver-loglevel=NONE"})
					.withLogFile(null)
					.build();
				webDriver = new PhantomJSDriver(service, caps);
			}
			catch (ExecutableNotFoundException e) {
				throw new IOException(e);
			}
		}
		return this.webDriver;
	}

	@Override
	public void close() throws IOException {
		PhantomJSDriver browser = this.getWebDriver();
		if (browser != null) {
			browser.quit();
		}
	}
}
