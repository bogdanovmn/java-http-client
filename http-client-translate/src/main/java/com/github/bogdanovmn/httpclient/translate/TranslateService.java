package com.github.bogdanovmn.httpclient.translate;


import com.github.bogdanovmn.httpclient.core.HttpServiceException;

import java.util.Set;

public interface TranslateService {
	Set<String> translate(String phrase) throws HttpServiceException;
}
