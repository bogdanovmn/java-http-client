package com.github.bogdanovmn.httpclient.translate;


import com.github.bogdanovmn.httpclient.core.HttpClient;
import com.github.bogdanovmn.httpclient.core.HttpService;
import com.github.bogdanovmn.httpclient.core.HttpServiceException;
import com.github.bogdanovmn.httpclient.core.ResponseNotFoundException;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

public abstract class HttpTranslateService extends HttpService<Set<String>> implements TranslateService {

	public HttpTranslateService(HttpClient httpClient, String urlPrefix) {
		super(httpClient, urlPrefix);
	}

	@Override
	public final Set<String> translate(String phrase) throws HttpServiceException {
		String htmlText;
		try {
			htmlText = httpClient.get(urlPrefix + phrase);
		}
		catch (IOException e) {
			throw new HttpServiceException(e);
		}

		Set<String> translatedValue = parsedServiceResponse(
			Objects.toString(htmlText, ""),
			phrase
		);

		if (translatedValue == null || translatedValue.isEmpty()) {
			throw new ResponseNotFoundException("Empty result. Something wrong...");
		}
		if (translatedValue.size() == 1 && translatedValue.contains(phrase)) {
			throw new ResponseNotFoundException(
				String.format(
					"No translate for '%s'", phrase
				)
			);
		}

		return translatedValue;
	}
}
