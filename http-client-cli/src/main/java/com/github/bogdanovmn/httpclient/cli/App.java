package com.github.bogdanovmn.httpclient.cli;


import com.github.bogdanovmn.cmdlineapp.CmdLineAppBuilder;
import com.github.bogdanovmn.httpclient.core.HttpClient;
import com.github.bogdanovmn.httpclient.phantomjs.PhantomJsHttpClient;
import com.github.bogdanovmn.httpclient.simple.SimpleHttpClient;

public class App {
	public static void main(String[] args) throws Exception {
		new CmdLineAppBuilder(args)
			.withJarName("http")
			.withDescription("Http client CLI")
			.withArg("url", "Souce URL")
			.withFlag("webdriver", "Use web driver (phantomjs)")
			.withEntryPoint(
				cmdLine -> {
					try (
						HttpClient httpClient = cmdLine.hasOption("w")
							? new PhantomJsHttpClient("")
							: new SimpleHttpClient("")
					) {

						System.out.println(
							httpClient.get(
								cmdLine.getOptionValue("u")
							)
						);
					}
				}
			).build().run();
	}
}
