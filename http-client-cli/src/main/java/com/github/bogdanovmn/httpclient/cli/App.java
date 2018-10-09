package com.github.bogdanovmn.httpclient.cli;


import com.github.bogdanovmn.cmdlineapp.CmdLineAppBuilder;
import com.github.bogdanovmn.httpclient.core.HttpClient;
import com.github.bogdanovmn.httpclient.phantomjs.SeleniumPhantomJsHttpClient;
import com.github.bogdanovmn.httpclient.simple.SimpleHttpClient;

public class App {
	public static void main(String[] args) throws Exception {
		new CmdLineAppBuilder(args)
			.withJarName("http")
			.withDescription("Http client CLI")
			.withArg("url", "Souce URL")
			.withFlag("last-modified", "Get last modified date")
			.withFlag("webdriver", "Use web driver (phantomjs)")
			.withEntryPoint(
				cmdLine -> {
					try (
						HttpClient httpClient = cmdLine.hasOption("w")
							? new SeleniumPhantomJsHttpClient("https://translate.google.ru/?ie=UTF-8#en/ru/", 200)
							: new SimpleHttpClient()
					) {

						if (cmdLine.hasOption("l")) {
							System.out.println(
								new SimpleHttpClient().getLastModified(
									cmdLine.getOptionValue("u")
								)
							);
						}
						else {
							System.out.println(
								httpClient.get(
									cmdLine.getOptionValue("u")
								)
							);
						}
					}
				}
			).build().run();
	}
}
