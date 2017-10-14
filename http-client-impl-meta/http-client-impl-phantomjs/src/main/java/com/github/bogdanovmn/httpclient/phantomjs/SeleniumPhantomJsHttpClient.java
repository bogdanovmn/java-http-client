package com.github.bogdanovmn.httpclient.phantomjs;

import com.github.bogdanovmn.httpclient.core.HttpClient;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.io.IOException;

public class SeleniumPhantomJsHttpClient implements HttpClient {
	private final String WEB_DRIVER_PATH_PROPERTY_NAME = "http.webdriver.path";

	private final String urlPrefix;
	private PhantomJSDriver webDriver;
	private final int pageLoadTime;

	public SeleniumPhantomJsHttpClient(String urlPrefix) {
		this.urlPrefix = urlPrefix;
		this.pageLoadTime = 100;
	}
	public SeleniumPhantomJsHttpClient(String urlPrefix, int pageLoadTime) {
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

			String driverFileName = System.getProperty(WEB_DRIVER_PATH_PROPERTY_NAME, "");
			if (driverFileName.isEmpty()) {
				throw new RuntimeException(
					String.format("Expected '%s' property (path to phantomjs webdriver)", WEB_DRIVER_PATH_PROPERTY_NAME)
				);
			}
			DesiredCapabilities caps = new DesiredCapabilities();
			caps.setJavascriptEnabled(true);
			caps.setCapability(
				PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
				driverFileName
			);

//			PhantomJSDriverService service = new PhantomJSDriverService.Builder()
//				.usingPhantomJSExecutable(new File(driverFileName))
//				.usingCommandLineArguments(new String[] {
//					"--webdriver-loglevel=WARN",
//					"--load-images=false"
//				})
//				.withLogFile(null)
//				.build();

//			webDriver = new PhantomJSDriver(service, caps);
			this.webDriver = new PhantomJSDriver(caps);
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