package com.github.bogdanovmn.httpclient.phantomjs;

import com.github.bogdanovmn.httpclient.core.HttpClient;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.IOException;

public class SeleniumPhantomJsHttpClient implements HttpClient {
	private final String WEB_DRIVER_PATH_PROPERTY_NAME = "http.webdriver.path";

	private PhantomJSDriver webDriver;
	private final int pageLoadTimeInMills;

	public SeleniumPhantomJsHttpClient(int pageLoadTimeInMills) {
		this.pageLoadTimeInMills = pageLoadTimeInMills;
	}

	public SeleniumPhantomJsHttpClient() {
		this(100);
	}

	@Override
	public String get(String url) throws IOException {
		PhantomJSDriver browser = this.getWebDriver();
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

	private PhantomJSDriver getWebDriver() throws IOException {
		if (this.webDriver == null) {

			String driverFileName = System.getProperty(WEB_DRIVER_PATH_PROPERTY_NAME, "");
			if (driverFileName.isEmpty()) {
				throw new RuntimeException(
					String.format("Expected '%s' property (path to phantomjs webdriver)", WEB_DRIVER_PATH_PROPERTY_NAME)
				);
			}

			DesiredCapabilities caps = new DesiredCapabilities();

			PhantomJSDriverService service = new PhantomJSDriverService.Builder()
				.usingPhantomJSExecutable(new File(driverFileName))
				.usingCommandLineArguments(new String[] {
					"--webdriver-loglevel=NONE",
					"--load-images=false"
				})
				.withLogFile(null)
				.build();

			this.webDriver = new PhantomJSDriver(service, caps);
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
