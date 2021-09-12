package com.github.bogdanovmn.httpclient.diskcache;

import com.github.bogdanovmn.httpclient.core.HttpClient;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class UrlContentDiskCache {
	private static final String PROPERTY_BASE_DIR = "urlContentDiskCache.baseDir";

	private final Path baseDir;
	private final HttpClient httpClient;


	public UrlContentDiskCache(HttpClient httpClient, String tag) {
		this.httpClient = httpClient;

		log.info("Initializing cache");

		String baseDirProperty = System.getProperty(PROPERTY_BASE_DIR, "");
		if (baseDirProperty.isEmpty()) {
			throw new IllegalArgumentException(
				String.format("Cache base dir specification expected (use %s property)", PROPERTY_BASE_DIR)
			);
		}
		this.baseDir = Paths.get(baseDirProperty, tag);

		if (!this.baseDir.toFile().exists()) {
			log.info("Created base dir: {}", this.baseDir);
			try {
				Files.createDirectories(this.baseDir);
			} catch (IOException e) {
				throw new RuntimeException("Can't create base dir: " + this.baseDir, e);
			}
		}
	}

	public UrlContentDiskCache(HttpClient httpClient, Class<?> clazz) throws IOException {
		this(httpClient, clazz.getSimpleName());
	}

	public String get(URL url) throws IOException {
		String result;

		File file = urlToFileName(url).toFile();
		if (!file.exists()) {
			log.info("Cache has not been found: {}", url);
			result = this.putAndReturnValue(url);
		} else {
			result = new String(
				Files.readAllBytes(file.toPath()),
				StandardCharsets.UTF_8
			);
		}

		return result;
	}

	private String putAndReturnValue(URL url) throws IOException {
		Path newFilePath = urlToFileName(url);
		String responseBody;
		if (newFilePath.getParent().toFile().exists() || newFilePath.getParent().toFile().mkdirs()) {
			File outputFile = Files.createFile(newFilePath).toFile();

			try (FileOutputStream output = new FileOutputStream(outputFile)) {
				responseBody = httpClient.get(url.toString());
				output.write(responseBody.getBytes());
				log.info("Download to {}", newFilePath);

			}
		}
		else {
			throw new IOException("Can't create cache dir: " + newFilePath.getParent().toString());
		}

		return responseBody;
	}

	private String urlToKey(URL url) {
		try {
			return String.format(
				"%032x",
				new BigInteger(
					1,
					MessageDigest.getInstance("MD5")
						.digest(url.toString().getBytes())
				)
			);
		}
		catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	private Path urlToFileName(URL url) {
		String key = urlToKey(url);
		String keyFirstChars = key.substring(0, 2);

		return Paths.get(
			baseDir.toString(),
			url.getHost().replaceAll("\\W", "_"),
			keyFirstChars,
			String.format(
				"%s.%s",
				key,
				String.format("%s__%s", url.getPath(), url.getQuery())
					.replaceAll("\\W", "_")
					.replaceFirst("__null", "")
			)
		);
	}
}
